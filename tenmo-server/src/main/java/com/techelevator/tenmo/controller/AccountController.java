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

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable long id) {
        return accountDao.getAccountById(id);
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable long id) {
        return accountDao.getBalance(id);
    }

    @PutMapping("/{id}/deposit")
    public BigDecimal deposit(@PathVariable long id, @RequestParam BigDecimal amount) {
        //Ran into error using @RequestBody, could not parse BigDecimal, could try to fix later
        return accountDao.deposit(id, amount);
    }

    @PutMapping("/{id}/withdraw")
    public BigDecimal withdraw(@PathVariable long id, @RequestParam BigDecimal amount) {
        return accountDao.withdraw(id, amount);
    }

}
