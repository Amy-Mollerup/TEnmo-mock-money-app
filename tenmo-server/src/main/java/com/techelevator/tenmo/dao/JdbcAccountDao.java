package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.BalanceNotZeroException;
import com.techelevator.tenmo.exceptions.UserNotAuthorizedException;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcAccountDao implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    //returns full list of accounts
    public List<Account> list() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            accounts.add(mapRowToAccount(results));
        }
        return accounts;
    }


    //updated: per trainer notes, do not need to account for more than one account (accounts should be 1-1)
    @Override
    //returns specific account based on userId of receiver that sender must enter
    public Account getAccount(long userId) throws UsernameNotFoundException {
        Account account1 = null;
        String sql = "SELECT account_id, user_id" + //not displaying balances for security purposes, will update balance in another method
                "FROM account " +
                "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            account1 = mapRowToAccount(results);
        }
        return account1;
    }

    //added getBalance method so we can specifically pull just the balance from the account id, need to authenticate the
    //id belongs to the user and the user is authorized
    //isFullyAuthenticated() && hasRole('user')
    @Override
    public BigDecimal getBalance(long userId) {
        BigDecimal balance = null;
        String sql = "SELECT balance FROM account " +
                "WHERE  user_id = ?;";
        try {
            balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        } catch (DataAccessException e) {
            System.out.println("Error accessing balance");
        }
        return balance;
    }

    @Override
    //make update balance method - return bigdecimal
    public BigDecimal updateAddBalance(long userId, BigDecimal amount) { // passing userId so method can be called in transfers, amount to update bal by
    Account account = getAccount(userId);
    BigDecimal newBal = account.getBalance().add(amount);
    String sql = "UPDATE account SET balance = ? " +
            "WHERE user_id = ?;";
    try {
        jdbcTemplate.update(sql, newBal, userId);
    } catch (UserNotAuthorizedException e) {
        System.out.println(e.getMessage());
    }
    try {
        jdbcTemplate.update(sql, newBal, userId);
    } catch (DataAccessException e) {
        System.out.println("Error accessing data");
    }
        System.out.println("New Balance: ");
        return account.getBalance();

    }

    @Override
    public BigDecimal updateSubtractBalance(long userId, BigDecimal amount) throws UserNotAuthorizedException {
        Account account = getAccount(userId);
        BigDecimal newBal = account.getBalance().subtract(amount);
        String sql = "UPDATE account SET balance = ? " +
                "WHERE user_id = ?;";
        try {
            jdbcTemplate.update(sql, newBal, userId);
        } catch (UserNotAuthorizedException e) {
            System.out.println(e.getMessage());
        }
        try {
            jdbcTemplate.update(sql, newBal, userId);
        } catch (BalanceNotZeroException e ) {
            System.out.println(e.getMessage());
        }
        try {
            jdbcTemplate.update(sql, newBal, userId);
        } catch (DataAccessException e) {
            System.out.println("Error accessing data");
        }
        System.out.println("New Balance: ");
        return account.getBalance();
    }

    @Override
    public void delete(long accountId, long userId) {
        String delete = "DELETE FROM account WHERE " +
                "account_id = ? AND user_id = ?";
            try {
            jdbcTemplate.update(delete, accountId, userId);
        } catch (UserNotAuthorizedException e) { //throw if user is not auth (user needs to own account)
                System.out.println(e.getMessage());
            } try { jdbcTemplate.update(delete, accountId, userId);
             } catch (BalanceNotZeroException e) { //throw if money is still in account
                System.out.println(e.getMessage());
            }
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
