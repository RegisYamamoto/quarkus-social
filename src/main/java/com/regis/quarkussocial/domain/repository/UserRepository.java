package com.regis.quarkussocial.domain.repository;

import com.regis.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    

}
