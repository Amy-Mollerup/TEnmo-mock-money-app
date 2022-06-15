package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/account")
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

}
