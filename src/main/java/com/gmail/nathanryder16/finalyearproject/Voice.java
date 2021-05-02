package com.gmail.nathanryder16.finalyearproject;

import com.gmail.nathanryder16.finalyearproject.model.Device;
import com.gmail.nathanryder16.finalyearproject.model.Script;
import com.gmail.nathanryder16.finalyearproject.mqtt.MqttClientPublish;
import com.gmail.nathanryder16.finalyearproject.repository.ScriptRepository;
import com.gmail.nathanryder16.finalyearproject.service.DeviceService;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class Voice {

    @Autowired
    private DeviceService deviceRepo;

    @Autowired
    private ScriptRepository scriptRepo;

    @Qualifier("mqttClientPublish")
    @Autowired
    private MqttClientPublish mqttClient;

    @Autowired
    private FileStorage storage;

    private static StreamController controller;
    private static int endTime = 0;
    private static int tempFinalEndTime = 0;
    private static int finalEndTime = 0;
    private static TargetDataLine dataLine = null;

    public void handleCommand(String cmd) {
        cmd = cmd.toLowerCase();

        for (Device device : deviceRepo.getRepo().findAll()) {
            String topic = device.getUpdateTopic() == null ? device.getStatusTopic() : device.getUpdateTopic();
            String payload = null;

            if (cmd.contains("turn on") && cmd.contains(device.getDisplayName().toLowerCase())) {
                payload = "ON";
            } else if (cmd.contains("turn off") && cmd.contains(device.getDisplayName().toLowerCase())) {
                payload = "OFF";
            }

            if (payload != null) {
                if (device.getUpdatePattern() != null) {
                    payload = device.getUpdatePattern().replace("%s", "ON");
                }
                if (device.getActivePayload() != null) {
                    payload = payload.replace("ON", device.getActivePayload());
                }
                if (device.getInactivePayload() != null) {
                    payload = payload.replace("OFF", device.getInactivePayload());
                }

                mqttClient.publish(topic, payload);
            }
        }

        //Script
        for (Script script : scriptRepo.findAll()) {
            if (script.getTriggerType() != ScriptTrigger.VOICE)
                continue;

            if (cmd.contains(script.getTriggerValue().toLowerCase())) {
                script.run(cmd.replace(script.getTriggerValue(), ""));
            }
        }

    }

    @Bean
    public void process() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Resource res = storage.loadAsResource();
                if (res == null) {
                    return;
                }

                BlockingQueue<byte[]> queue = new LinkedBlockingQueue();

                Thread voice = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dataLine.start();
                        byte[] data = new byte[6400];

                        while (dataLine.isOpen()) {

                            int read = dataLine.read(data, 0, data.length);
                            if (dataLine.isOpen() && read <= 0)
                                continue;

                            try {
                                queue.put(data.clone());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                SpeechSettings speechSettings;
                try {
                    ServiceAccountCredentials file = ServiceAccountCredentials.fromStream(res.getInputStream());
                    CredentialsProvider creds = FixedCredentialsProvider.create(file);
                    speechSettings = SpeechSettings.newBuilder().setCredentialsProvider(creds).build();
                } catch (IOException e) {
                    System.out.println("[WARN] Failed to find voice credentials file. Voice commands will not work.");
                    return;
                }

                ResponseObserver<StreamingRecognizeResponse> response = null;
                try (SpeechClient client = SpeechClient.create(speechSettings)) {
                    ClientStream<StreamingRecognizeRequest> stream;

                    response = new ResponseObserver<>() {
                        List<StreamingRecognizeResponse> responses = new ArrayList<>();

                        @Override
                        public void onStart(StreamController streamController) {
                            controller = streamController;
                        }

                        @Override
                        public void onResponse(StreamingRecognizeResponse streamingRecognizeResponse) {
                            responses.add(streamingRecognizeResponse);

                            StreamingRecognitionResult res = streamingRecognizeResponse.getResults(0);
                            Duration smallEndTime = res.getResultEndTime();
                            endTime = (int) ((smallEndTime.getSeconds()*1000) + (smallEndTime.getNanos()/1000000));

                            SpeechRecognitionAlternative alt = res.getAlternatives(0);
                            if (res.getIsFinal()) {
                                handleCommand(alt.getTranscript());
                                tempFinalEndTime = endTime;
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    };

                    stream = client.streamingRecognizeCallable().splitCall(response);

                    RecognitionConfig recogConfig = RecognitionConfig.newBuilder().setSampleRateHertz(16000).setLanguageCode("en-IE").setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).build();
                    StreamingRecognitionConfig streamConfig = StreamingRecognitionConfig.newBuilder().setConfig(recogConfig).setInterimResults(true).build();
                    StreamingRecognizeRequest req = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamConfig).build();
                    stream.send(req);

                    try {
                        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                        if (!AudioSystem.isLineSupported(info)) {
                            System.out.println("[WARN] Failed to find a supported microphone! Voice commands will not work.");
                            return;
                        }

                        dataLine = (TargetDataLine) AudioSystem.getLine(info);
                        dataLine.open(format);
                        voice.start();

                        long start = System.currentTimeMillis();

                        List<ByteString> lastInput = new ArrayList<>();
                        List<ByteString> currInput = new ArrayList<>();

                        boolean createNewStream = true;
                        double offset = 0;
                        while (true) {
                            long end = System.currentTimeMillis() - start;

                            if (end >= 290000) {
                                stream.closeSend();
                                controller.cancel();

                                if (endTime > 0)
                                    finalEndTime = tempFinalEndTime;

                                lastInput = currInput;
                                currInput = new ArrayList<>();
                                createNewStream = true;

                                stream = client.streamingRecognizeCallable().splitCall(response);
                                req = StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamConfig).build();

                                start = System.currentTimeMillis();
                            } else {
                                if (lastInput.size() > 0 && createNewStream) {
                                    double tempLastTime = 290000 / (double) lastInput.size();

                                    if (tempLastTime != 0) {
                                        offset = offset < 0 ? 0 : offset;

                                        if (offset > finalEndTime) {
                                            offset = finalEndTime;
                                        }

                                        int lastTime = (int) Math.floor((finalEndTime - offset) / tempLastTime);
                                        offset = (int) Math.floor((lastInput.size() - lastTime) * tempLastTime);

                                        for (int i = lastTime; i < lastInput.size(); i++) {
                                            req = StreamingRecognizeRequest.newBuilder().setAudioContent(lastInput.get(i)).build();

                                            stream.send(req);
                                        }
                                    }

                                    createNewStream = false;
                                }

                                ByteString temp = ByteString.copyFrom(queue.take());
                                req = StreamingRecognizeRequest.newBuilder().setAudioContent(temp).build();

                                lastInput.add(temp);
                            }

                            stream.send(req);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

}
