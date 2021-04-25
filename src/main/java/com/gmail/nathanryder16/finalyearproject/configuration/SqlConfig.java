package com.gmail.nathanryder16.finalyearproject.configuration;

import com.gmail.nathanryder16.finalyearproject.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SqlConfig {

    @Autowired
    private Config config;

    @Bean
    public DataSource getSql() {
        DataSourceBuilder builder = DataSourceBuilder.create();

        String host = "jdbc:mysql://" + config.getSqlHost() + ":" + config.getSqlPort() + "/" + config.getSqlDatabase();
        builder.url(host);
        builder.username(config.getSqlUser());
        builder.password(config.getSqlPassword());

        return builder.build();
    }

}
