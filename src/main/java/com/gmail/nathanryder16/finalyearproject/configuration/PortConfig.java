package com.gmail.nathanryder16.finalyearproject.configuration;

import com.gmail.nathanryder16.finalyearproject.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class PortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Autowired
    private Config config;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(config.getWebPort());
    }

}
