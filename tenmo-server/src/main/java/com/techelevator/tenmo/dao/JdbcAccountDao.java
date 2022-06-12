package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Account> list() {
        return null;
    }

    @Override
    public Account get(long id) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public Account create(Account account) {
        return null;
    }

    @Override
    public Account update(Account account, long id) {
        return null;
    }

    @Override
    public void delete(long id) {

    }
}
