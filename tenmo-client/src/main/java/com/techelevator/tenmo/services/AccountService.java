package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import io.cucumber.core.gherkin.ScenarioOutline;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) { this.baseUrl = url + "account/"; }

    public BigDecimal getBalance(AuthenticatedUser authenticatedUser) { //Throw user not auth exception?
        HttpEntity entity = makeEntity(authenticatedUser);
        Long userId = authenticatedUser.getUser().getId();
        BigDecimal balance = null;
        try {
            balance = restTemplate.exchange(baseUrl + userId + "/balance",
                    HttpMethod.GET, entity,
                    BigDecimal.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }
    public Account getAccountByUserId(AuthenticatedUser authenticatedUser, Long userId) {
        HttpEntity entity = makeEntity(authenticatedUser);
        Account account = null;
        try {
            account = restTemplate.exchange(baseUrl + userId, HttpMethod.GET, entity,
                    Account.class).getBody();
        }  catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;

    }


    private HttpEntity makeEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }


}
