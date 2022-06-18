package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferAmountZeroOrLessException;
import com.techelevator.tenmo.exceptions.TransferAttemptExceedsAccountBalException;
import com.techelevator.tenmo.exceptions.UserNotAuthorizedException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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


    @Override //returns full list of accounts - should we update this to be by userId?
    //possibly remove when we do front end
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
    //works in postman
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


    //added getBalance method so we can specifically pull just the balance from the account id, need to authenticate the
    //id belongs to the user and the user is authorized
    //isFullyAuthenticated() && hasRole('user')
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
        // passing userId so method can be called in transfers, amount to update bal by
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
    //BIG DECIMAL SUCKS - will throw numberformatexception if less than 0
    // TODO check exception handling on this method
    public BigDecimal withdraw(long accountId, BigDecimal amount) throws TransferAmountZeroOrLessException,
            TransferAttemptExceedsAccountBalException, UsernameNotFoundException {

        BigDecimal newBal = getBalance(accountId).subtract(amount);
        if(newBal.compareTo(ZERO) >= 0 && amount.compareTo(ZERO) > 0) {
            String sql = "UPDATE account SET balance = ? " +
                    "WHERE account_id = ?;";
            int lines = jdbcTemplate.update(sql, newBal, accountId);
            if (lines == 0) {
                throw new UsernameNotFoundException("Could not update balance");
        }
        } else if(amount.compareTo(ZERO) <= 0) {
            throw new TransferAmountZeroOrLessException();
        } else if(newBal.compareTo(ZERO) < 0) {
            throw new TransferAttemptExceedsAccountBalException();
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

//    @Override
//    public Account findById(long accountId) {
//
//        Account account = null;
//        String sql = "SELECT * " +
//                "FROM account " +
//                "WHERE account_id = ?;";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
//        if (results.next()) {
//            account = mapRowToAccount(results);
//        }
//        return account;
//    }

//    @Override
//    public Long findUserIdByAccountId(Long accountId) {
//        String sql = "SELECT user_id FROM account WHERE account_id = ?";
//        return jdbcTemplate.queryForObject(sql, Long.class, accountId);
//    }

//    @Override //will delete account as long as balance is at zero - do we need to include delete user too since create user includes delete?
//    public void delete(long accountId, long userId) throws BalanceNotZeroException {
//        String delete = "DELETE FROM account WHERE " +
//                "account_id = ? AND user_id = ?";
//            try {
//            jdbcTemplate.update(delete, accountId, userId);
//        } catch (UserNotAuthorizedException e) { //throw if user is not auth (user needs to own account)
//                System.out.println(e.getMessage());
//            }
//    }

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

}
