package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficentFundsException;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.*;


@Component
public class JdbcAccountDao implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override //returns full list of accounts
    public List<Account> list() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            accounts.add(mapRowToAccount(results));
        }
        return accounts;
    }

    @Override //returns specific account based on userId of receiver that sender must enter
    public Account find(long userId) throws UsernameNotFoundException {
        String sql = "SELECT * " +
                "FROM account " +
                "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            return mapRowToAccount(results);
        } else {
            throw new UsernameNotFoundException("Could not find account");
        }
    }

    @Override //retrieves current balance from account based on userId
    public BigDecimal getBalance(long accountId) throws UsernameNotFoundException {
        BigDecimal balance = null;
        String sql = "SELECT balance FROM account " +
                "WHERE account_id = ?;";
        balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, accountId);
        if (balance == null) {
            throw new UsernameNotFoundException("Could not find account balance");
        }
        return balance;
    }

    @Override //adds to balance in account, returns updated balance
    public BigDecimal deposit(long accountId, BigDecimal amount) throws UsernameNotFoundException  {
        BigDecimal newBal = getBalance(accountId).add(amount);
        String sql = "UPDATE account SET balance = ? " +
                "WHERE account_id = ?;";
        int lines = jdbcTemplate.update(sql, newBal, accountId);

        if (lines == 0) {
            throw new UsernameNotFoundException("Could not update balance");
        }
        return newBal;

    }


    @Override //subtracts from balance in account, returns updated balance
    public BigDecimal withdraw(long accountId, BigDecimal amount) throws InsufficentFundsException, UsernameNotFoundException {
        BigDecimal newBal = getBalance(accountId).subtract(amount);
        if(newBal.compareTo(ZERO) >= 0 && amount.compareTo(ZERO) > 0) {
            String sql = "UPDATE account SET balance = ? " +
                    "WHERE account_id = ?;";
            int lines = jdbcTemplate.update(sql, newBal, accountId);
            if (lines == 0) {
                throw new UsernameNotFoundException("Could not update balance");
        }
        } else if(amount.compareTo(ZERO) <= 0) {
            throw new InsufficentFundsException("Transaction amount cannot be for 0 or less.");
        } else if(newBal.compareTo(ZERO) < 0) {
            throw new InsufficentFundsException("Transaction amount cannot exceed available funds.");
        }
        return newBal;
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }


}
