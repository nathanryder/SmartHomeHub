package com.gmail.nathanryder16.finalyearproject.model;

import com.gmail.nathanryder16.finalyearproject.ScriptTrigger;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.aspectj.util.FileUtil;

import javax.persistence.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "scripts")
public class Script {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter @Setter int id;
    private @Getter @Setter int enabled;
    private @Getter @Setter String name;
    private @Getter @Setter String description;
    @Enumerated(EnumType.STRING)
    private @Getter @Setter ScriptTrigger triggerType;
    private @Getter @Setter String triggerValue;
    private @Getter @Setter String path = null;

    public Script(String name, String description, ScriptTrigger triggerType, String rawScript) {
        this.enabled = 1;
        this.name = name;
        this.description = description;
        this.triggerType = triggerType;
        this.path = setScript(rawScript);
    }

    public Script() {
    }

    public String setScript(String content) {
        String scriptPath = path;
        if (scriptPath == null) {
            //Create new file
            File parent = new File("scripts");
            if (!parent.exists()) {
                parent.mkdir();
            }

            scriptPath = "test.py";
        }

        File f = new File("scripts" + File.separator + scriptPath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileUtil.writeAsString(f, content);

        return scriptPath;
    }

    public String getScript() {
        try {
            return FileUtil.readAsString(new File("scripts" + File.separator + path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void run(String message) {

        String header = "import os,sys,inspect\n" +
                        "currentdir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))\n" +
                        "parentdir = os.path.dirname(currentdir) + '/pylibs'\n" +
                        "sys.path.insert(0,parentdir)\n\n";
        File libs = new File("pylibs");
        for (File file : libs.listFiles()) {
            String[] fileData = file.getName().split("\\.");
            if (fileData[0].startsWith("__")) {
                continue;
            }
            if (fileData[1].equals("pyc")) {
                continue;
            }

            header += "from " + fileData[0] + " import *\n\n";
        }

        header += "message = ' '.join(sys.argv[1:])";

        //Take arguments

        String original = "scripts" + File.separator + path;
        String file = "scripts" + File.separator + "." + path;
        File tmp = new File(file);
        try {
            tmp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contents = null;
        try {
            contents = IOUtils.toString(new File(original).toURL().openStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        contents = header + "\n" + contents;

        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(tmp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            IOUtils.write(contents.getBytes(), os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(os);

        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("python " + file + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        try {
            String line;
            while ((line = out.readLine()) != null) {
                System.out.println("[Script INFO][" + path + "] " + line);
            }
            while ((line = err.readLine()) != null) {
                System.out.println("[Script ERROR][" + path + "] " + line);
            }

            tmp.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
