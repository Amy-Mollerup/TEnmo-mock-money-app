package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface AccountDao {
    // TODO - REVIEW EXCEPTIONS

    List<Account> list();
    //returns list of accounts, no balances, just ID - no usernames or names for security purposes


    //users don't specifically have more than one account id, may look into further
    //updated: per trainer notes, do not need to account for more than one account (accounts should be 1-1)
    //may need to change exception
    Account getAccount(long userId) throws UsernameNotFoundException;

    Account getBalance(long userId);

    Account create(Account account);

    Account update(Account account, long accountId);
    //throw exception -- may need to make one that is for user not authorized to update or access

    void delete(long accountId, long userId);
    //throw exception -- may need to make one that is for user not authorized to update or access
    //add exception for if balance is not zero, let user know to withdraw remaining funds before proceeding


}
