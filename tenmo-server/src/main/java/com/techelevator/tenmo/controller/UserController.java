package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserDao dao;

    public UserController(UserDao dao) { this.dao = dao; }

    @GetMapping
    public List<User> list() {
        return dao.findAll();
    }

    @PostMapping
    public void createUser(@RequestBody User user) {
        dao.create(user.getUsername(), user.getPassword());
    }

}
