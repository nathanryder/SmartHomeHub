package com.gmail.nathanryder16.finalyearproject.repository;

import com.gmail.nathanryder16.finalyearproject.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

    User findByEmail(String email);

}
