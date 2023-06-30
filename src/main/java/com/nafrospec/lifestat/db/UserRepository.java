package com.nafrospec.lifestat.db;

import com.nafrospec.lifestat.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> getByName(String name);

    Optional<User> findByName(String name);

    Boolean existsByName(String name);
}
