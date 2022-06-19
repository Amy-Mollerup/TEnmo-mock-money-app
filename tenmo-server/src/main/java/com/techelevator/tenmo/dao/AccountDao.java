package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    List<Account> list();
    //returns list of accounts, no balances, just ID - no usernames or names for security purposes

    Account find(long userId) throws UsernameNotFoundException;
    // Finds full account information by Account ID

    BigDecimal getBalance(long accountId);
    //user needs to be authorized to view balance

    BigDecimal deposit(long accountId, BigDecimal amount);


    BigDecimal withdraw(long accountId, BigDecimal amount);



}
