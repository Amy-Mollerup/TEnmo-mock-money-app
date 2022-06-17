package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

    public class TransferService {

        private RestTemplate restTemplate = new RestTemplate();
        private final String baseUrl;

        public TransferService(String url) {
            this.baseUrl = url + "transfer/";

        }

        public void createSendTransfer(AuthenticatedUser authenticatedUser, Long toUserId, BigDecimal amount) {
            //need to work more on this - not sure if it is set up correctly
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authenticatedUser.getToken());
            // TODO fix withdraw and deposit methods in AccountDao so they use the correct ID
            String string = "2001,2004,50";
            HttpEntity<String> entity = new HttpEntity(string,headers);
            try {
                restTemplate.exchange(baseUrl + "send", HttpMethod.POST, entity, Transfer.class);
            } catch (RestClientResponseException e) {
                System.out.println("Unable to create transfer. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch(ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
        }

        public void updateTransferStatus(AuthenticatedUser authenticatedUser, Long transferId) {
            HttpEntity entity = makeEntity(authenticatedUser);
            try {
                restTemplate.exchange(baseUrl + transferId, HttpMethod.POST, entity, Transfer.class);
            } catch (RestClientResponseException e) {
                System.out.println("Unable to update transfer status. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch(ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
        }

        public void requestSendTransfer(AuthenticatedUser authenticatedUser) {
            HttpEntity entity = makeEntity(authenticatedUser);
            try {
                restTemplate.exchange(baseUrl + "request", HttpMethod.POST, entity, Transfer.class);
            } catch (RestClientResponseException e) {
                System.out.println("Unable to complete request. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch(ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
        }

        public Transfer[] getAllTransfersByAccountId(AuthenticatedUser authenticatedUser, Long accountId) {
            HttpEntity entity = makeEntity(authenticatedUser);
            Transfer[] allTransfers = null;
            try {
                allTransfers = restTemplate.exchange(baseUrl + accountId, HttpMethod.GET, entity, Transfer[].class).getBody();
            } catch (RestClientResponseException e) {
                System.out.println("Unable to display transfers. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch(ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
            return allTransfers;
        }

        public Transfer getTransferByTransferId(AuthenticatedUser authenticatedUser, Long transferId) {
            HttpEntity entity = makeEntity(authenticatedUser);
            Transfer transfer = null;
            try {
                transfer = restTemplate.exchange(baseUrl + transferId, HttpMethod.GET, entity, Transfer.class).getBody();
            } catch (RestClientResponseException e) {
                System.out.println("Unable to retrieve transfer. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch (ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
            return transfer;
        }


        public Transfer[] getTransfersByStatus(AuthenticatedUser authenticatedUser, Long transferStatusId) {
            HttpEntity entity = makeEntity(authenticatedUser); //can this be set up to just return literally the transfer status text?
            Transfer[] transfersByStatus = null;
            try {
                transfersByStatus = restTemplate.exchange(baseUrl + transferStatusId, HttpMethod.GET, entity, Transfer[].class).getBody();
            } catch (RestClientResponseException e) {
                System.out.println("Unable to display transfers. Error code: " + e.getRawStatusCode());
                BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
            } catch(ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
                System.out.println(e.getMessage());
            }
            return transfersByStatus;
        }


        public Transfer getTransferType(AuthenticatedUser authenticatedUser, String transferType) { //look at getting transfer status desc in controller method?
            HttpEntity entity = makeEntity(authenticatedUser);
            Transfer transfer = null;
            try {
                transfer = restTemplate.exchange(baseUrl + transferType, HttpMethod.GET, entity, Transfer.class).getBody();
            } catch (RestClientResponseException e) {
            System.out.println("Unable to display transfers. Error code: " + e.getRawStatusCode());
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch(ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println(e.getMessage());
        }
            return transfer;
    }

    private HttpEntity makeEntity(AuthenticatedUser authenticatedUser){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}

