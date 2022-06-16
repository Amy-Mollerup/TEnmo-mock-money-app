package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping()
    public List<Account> getAll() {
        return accountDao.list();
    }

    @GetMapping("/{userId}")
    public Account getAccountById(@PathVariable long userId) {
        return accountDao.getAccountById(userId);
    }

    @GetMapping("/{userId}/balance")
    public BigDecimal getBalance(@PathVariable long userId) {
        return accountDao.getBalance(userId);
    }

    @PutMapping("/{userId}/deposit")
    public BigDecimal deposit(@PathVariable long userId, @RequestBody String amount) {
        //Ran into error using @RequestBody, could not parse BigDecimal, could try to fix later
        return accountDao.deposit(userId, BigDecimal.valueOf(Double.parseDouble(amount)));
    }

    @PutMapping("/{userId}/withdraw")
    public BigDecimal withdraw(@PathVariable long userId, @RequestBody String amount) {
        return accountDao.withdraw(userId, BigDecimal.valueOf(Double.parseDouble(amount)));
    }

}
