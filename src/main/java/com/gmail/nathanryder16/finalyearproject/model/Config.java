package com.gmail.nathanryder16.finalyearproject.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class Config {

    private @Getter @Setter String sqlHost;
    private @Getter @Setter int sqlPort;
    private @Getter @Setter String sqlUser;
    private @Getter @Setter String sqlPassword;
    private @Getter @Setter String sqlDatabase;

    private @Getter @Setter String mqttHost;
    private @Getter @Setter int mqttPort;
    private @Getter @Setter String mqttUser;
    private @Getter @Setter String mqttPassword;

    private @Getter @Setter int webPort;

    public Config() {
        try {
            load();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Config(boolean startup) {
    }

    public void load() throws FileNotFoundException {
        if (!isSetup()) {
            return;
        }

        Yaml yaml = new Yaml();

        File config = new File("config.yml");
        Map<String, Object> root = yaml.load(new FileInputStream(config));

        sqlHost = (String) root.get("sqlHost");
        sqlPort = (int) root.get("sqlPort");
        sqlUser = (String) root.get("sqlUser");
        sqlPassword = (String) root.get("sqlPassword");
        sqlDatabase = (String) root.get("sqlDatabase");
        mqttHost = (String) root.get("mqttHost");
        mqttPort = (int) root.get("mqttPort");
        mqttUser = (String) root.get("mqttUser");
        mqttPassword = (String) root.get("mqttPassword");
        webPort = (int) (root.get("webPort") == null ? 8080 : root.get("webPort"));
    }

    public Object getSetting(Object setting, Object defaultValue) {
        return setting == null ? defaultValue : setting;
    }

    public void save() throws IOException {
        Yaml yaml = new Yaml();

        File config = new File("config.yml");
        if (!config.exists()) {
            config.createNewFile();
        }
        Map<String, Object> root = yaml.load(new FileInputStream(config));
        if (root == null) {
            root = new HashMap<>();
        }

        root.put("sqlHost", getSetting(sqlHost, "127.0.0.1"));
        root.put("sqlPort", getSetting(sqlPort, "3306"));
        root.put("sqlUser", getSetting(sqlUser, "user"));
        root.put("sqlPassword", getSetting(sqlPassword, "password"));
        root.put("sqlDatabase", getSetting(sqlDatabase, "fyp"));
        root.put("mqttHost", getSetting(mqttHost, "127.0.0.1"));
        root.put("mqttPort", getSetting(mqttPort, "1883"));
        root.put("mqttUser", getSetting(mqttUser, "user"));
        root.put("mqttPassword", getSetting(mqttPassword, "password"));
        root.put("webPort", getSetting(webPort, "8080"));

        yaml.dump(root, new PrintWriter(config));
    }

    public boolean isSetup() {
        File config = new File("config.yml");
        return config.exists();
    }

}
