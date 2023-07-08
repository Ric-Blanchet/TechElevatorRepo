package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private final JdbcUserDao userDao;

    public UserController(JdbcUserDao userDao) {
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "/users/{id}")
    public Map<User, BigDecimal> getUserById(@PathVariable int id, Principal principal) {
        User user = userDao.getUserById(id);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Map<User,BigDecimal> userAccount = new HashMap<>();
        if (principal.getName().equals(user.getUsername())) {
            userAccount.put(user,user.getBalance());
        } else
            userAccount.put(user,null);
        return userAccount;
    }

    /** getUserByName() has a hidden ability to call getUserById() when the path contains '?id=' instead */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "/users")
    public Map<User,BigDecimal> getUserByName(@RequestParam(name = "username", required = false) String username,
                                              @RequestParam(name = "id", required = false) Integer id, Principal principal) {
        if (username != null && id == null) {
            User user = userDao.findByUsername(username);
            if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            Map<User,BigDecimal> userAccount = new HashMap<>();
            if (principal.getName().equals(user.getUsername())) {
                userAccount.put(user,user.getBalance());
            } else
                userAccount.put(user,null);
            return userAccount;
        } else if (id != null) {
            return getUserById(id,principal);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "users/all")
    public List<User> getListOfUsers() {
        return userDao.findAll();
    }

}
