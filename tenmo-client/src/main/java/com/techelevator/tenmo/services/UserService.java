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

public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;


    public UserService(String url) {
        this.baseUrl = url + "/user";
    }

    public User[] getAllUsers(AuthenticatedUser authenticatedUser) {
        HttpEntity entity = makeEntity(authenticatedUser);
        User[] allUsers = null;
        try {
            allUsers = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, User[].class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Unable to display users. Error code: " + e.getRawStatusCode());
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }
        return allUsers;
    }

    public User getUserByUserId(AuthenticatedUser authenticatedUser, Long userId) {
        HttpEntity entity = makeEntity(authenticatedUser);
        User user = null;
        try {
            user = restTemplate.exchange(baseUrl + "/user/" + userId, HttpMethod.GET,
                    entity, User.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Unable to locate user. Error code: " + e.getRawStatusCode());
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
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
