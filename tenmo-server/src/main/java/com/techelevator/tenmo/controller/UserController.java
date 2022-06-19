package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserDao dao;

    public UserController(UserDao dao) { this.dao = dao; }

    @GetMapping("/id/{username}")
    public int findIdByUsername(@PathVariable String username) {
        return dao.findIdByUsername(username);
    }

    @GetMapping()
    public List<User> list() {
        return dao.findAll();
    }

    @GetMapping("/find/{accountId}")
    public User findUserByAccountId(@PathVariable Long accountId) {
        return dao.findUserByAccountId(accountId);
    }

    @GetMapping("/{username}")
    public User findUserByUsername(@PathVariable String username) {
        return dao.findByUsername(username);
    }

    @PostMapping("/create")
    public void createUser(@Valid @RequestBody User user) {
        dao.create(user.getUsername(), user.getPassword());
    }

}
