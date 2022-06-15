package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class JdbcAccountDao implements AccountDao {

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

    //users don't specifically have more than one account id, may look into further
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
    //should this maybe be in transfer so we can create a balance object?
    @Override
    public Account getBalance(long userId) {
        Account account = null;
        String sql = "SELECT balance FROM account " +
                "WHERE  user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }


    /*@Override
    //need update balance method -- initial account creation does not require balance, need to add at least 0 to account
    //account_id serial?
    //user id comes from when user is created, does there need to be a join statement here?
    public Account create(Account newAccount) {
        String create = "INSERT INTO account (user_id, balance) " +
                "VALUES (?, ?) " +
                "RETURNING account_id;";
        long accountId = (jdbcTemplate.queryForObject(create, Long.class, newAccount.getUserId(), newAccount.getBalance()));
        newAccount.setAccountId(accountId);
        return newAccount;

        }*/


    @Override
    //we need update balance methods but do we need update accounts for anything?
    public Account update(Account account, long id) {
        return null;

    }

    @Override
    //need to make sure we're only deleting the account of the user logged in
    //balance should be 0 to delete otherwise return money (exception message)
    public void delete(long accountId, long userId) {
        String delete = "DELETE FROM account WHERE " +
                "account_id = ? AND user_id = ?";
        jdbcTemplate.update(delete, accountId, userId);

    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getLong("account_id"));
        account.setUserId(rowSet.getLong("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
