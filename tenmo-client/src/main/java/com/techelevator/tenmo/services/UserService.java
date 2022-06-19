package com.techelevator.tenmo.services;



import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;


    public UserService(String url) {
        this.baseUrl = url + "/user";
    }

    public Map<Long, User> getAllUsers(AuthenticatedUser authenticatedUser) {
        Map<Long, User> userMap = new HashMap<>();
        HttpEntity<Void> entity = makeEntity(authenticatedUser);
        User[] allUsers = null;
        try {
            allUsers = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, User[].class).getBody();
            if (allUsers != null) {
                for (User user : allUsers) {
                    if (!user.getId().equals(authenticatedUser.getUser().getId())) {
                        userMap.put(user.getId(), user);
                    }
                }
            }
        } catch (RestClientResponseException | ResourceAccessException e) {;
            BasicLogger.log(e.getMessage());
        }
        return userMap;
    }

    public User getUserByUserId(AuthenticatedUser authenticatedUser, Long userId) {
        HttpEntity entity = makeEntity(authenticatedUser);
        User user = null;
        try {
            user = restTemplate.exchange(baseUrl + "/" + userId, HttpMethod.GET,
                    entity, User.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {;
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public User getUserByAccountId(AuthenticatedUser authenticatedUser, Long accountId) {
        HttpEntity<User> entity = makeEntity(authenticatedUser);
        User user = new User();
        try {
            user = restTemplate.exchange(baseUrl + "/find/" + accountId, HttpMethod.GET, entity, User.class).getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {;
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    private HttpEntity makeEntity (AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }


}
