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
    public Account getAccountByUserId(@PathVariable long userId) {
        return accountDao.find(userId);
    }

    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable long accountId) {
        return accountDao.getBalance(accountId);
    }

    @PutMapping("/{accountId}/deposit")
    public BigDecimal deposit(@PathVariable long accountId, @RequestBody String amount) {
        //Ran into error using @RequestBody, could not parse BigDecimal, could try to fix later
        return accountDao.deposit(accountId, BigDecimal.valueOf(Double.parseDouble(amount)));
    }

    @PutMapping("/{accountId}/withdraw")
    public BigDecimal withdraw(@PathVariable long accountId, @RequestBody String amount) {
        return accountDao.withdraw(accountId, BigDecimal.valueOf(Double.parseDouble(amount)));
    }



}
