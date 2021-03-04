package com.gmail.nathanryder16.finalyearproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private @Getter @Setter String id;
    private @Getter @Setter String email;
    private @Getter @Setter String password;
    private @Getter @Setter int validated;

    public User(String email, String password, int validated) {
        this.email = email;
        this.password = password;
        this.validated = validated;
        this.id = UUID.randomUUID().toString().replace("-", "");
    }

    public User() {

    }
}
