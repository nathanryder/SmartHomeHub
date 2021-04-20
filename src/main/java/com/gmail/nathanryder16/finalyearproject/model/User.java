package com.gmail.nathanryder16.finalyearproject.model;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
    private @Getter String password;
    private @Getter @Setter int validated;
    private @Getter @Setter int superuser;

    public User(String email, String password, int validated) {
        this.email = email;
        this.validated = validated;
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.superuser = 0;
        setPassword(password);
    }

    public User() {

    }

    public void setPassword(String password) {
        this.password = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
