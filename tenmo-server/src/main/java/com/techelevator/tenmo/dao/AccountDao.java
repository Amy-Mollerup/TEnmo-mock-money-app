package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.TransferAttemptExceedsAccountBalException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.exceptions.UserNotAuthorizedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    // TODO - REVIEW EXCEPTIONS

    List<Account> list();
    //returns list of accounts, no balances, just ID - no usernames or names for security purposes

    //updated: per trainer notes, do not need to account for more than one account (accounts should be 1-1)
    //may need to change exception
    Account find(long userId) throws UsernameNotFoundException;

    //Long findUserIdByAccountId(Long accountId);

    BigDecimal getBalance(long accountId) throws UserNotAuthorizedException;
    //user needs to be authorized to view balance

    BigDecimal deposit(long accountId, BigDecimal amount) throws UserNotAuthorizedException;

//    Account updateBalance(int balance, long id);

    BigDecimal withdraw(long accountId, BigDecimal amount) throws UserNotAuthorizedException, TransferAttemptExceedsAccountBalException;
    //can also have this throw balance cannot be 0 exception

 //   void delete(long accountId, long userId) throws UserNotAuthorizedException;
    //throw exception if not authorized
    //add exception for if balance is not zero, let user know to withdraw remaining funds before proceeding


}
