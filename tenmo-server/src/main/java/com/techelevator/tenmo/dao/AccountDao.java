package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface AccountDao {
    // TODO - REVIEW EXCEPTIONS

    List<Account> list();

    //may need to change exception
    Account get(long id) throws UsernameNotFoundException;

    Account create(Account account);

    Account update(Account account, long id);
    //throw exception -- may need to make one that is for user not authorized to update or access

    void delete(long id);
    //throw exception -- may need to make one that is for user not authorized to update or access

}
