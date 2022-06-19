package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TransferService {

        private final RestTemplate restTemplate = new RestTemplate();
        private final String baseUrl;

        public TransferService(String url) {
            this.baseUrl = url + "transfer/";
        }

        public boolean createTransfer(AuthenticatedUser authenticatedUser, int transferTypeID, Long fromAccountId, Long toAccountId, BigDecimal amount) {
            boolean success = false;
            TransferDto transferDto = new TransferDto();
            transferDto.setTransferTypeId(transferTypeID);
            transferDto.setTransferStatusId(transferTypeID);
            transferDto.setAccountFrom(fromAccountId);
            transferDto.setAccountTo(toAccountId);
            transferDto.setAmount(amount);

            HttpEntity<TransferDto> entity = makeTransferEntity(authenticatedUser, transferDto);
            try {
                restTemplate.exchange(baseUrl, HttpMethod.POST, entity, Boolean.class);
                success = true;
            } catch (RestClientResponseException | ResourceAccessException e) {;
                BasicLogger.log(e.getMessage());
            } return success;
        }

        public boolean updateTransferStatus(AuthenticatedUser authenticatedUser, Long transferId, int choice) {
            boolean success = false;
            choice++;
            TransferDto transferDto = getTransferDtoByTransferId(authenticatedUser, transferId);
            transferDto.setTransferStatusId(choice);
            HttpEntity<TransferDto> entity = makeTransferEntity(authenticatedUser, transferDto);
            try {
                restTemplate.exchange(baseUrl + "/update", HttpMethod.PUT, entity, Boolean.class);
                success = true;
            } catch (RestClientResponseException | ResourceAccessException e) {;
                BasicLogger.log(e.getMessage());
            }
            return success;
        }

        public Map<Long, Transfer> getAllTransfersByAccountId(AuthenticatedUser authenticatedUser, Long accountId) {
            Map<Long, Transfer> transferMap = new HashMap<>();
            HttpEntity<Void> entity = makeAuthEntity(authenticatedUser);
            Transfer[] allTransfers = null;
            try {
                allTransfers = restTemplate.exchange(baseUrl + "/user/" + accountId, HttpMethod.GET, entity, Transfer[].class).getBody();
                for(Transfer t : allTransfers) {
                    transferMap.put(t.getTransferId(), t);

                }
            } catch (RestClientResponseException | ResourceAccessException e) {;
                BasicLogger.log(e.getMessage());
            }
            return transferMap;
        }

        public Transfer getTransferByTransferId(AuthenticatedUser authenticatedUser, Long transferId) {
            HttpEntity<Void> entity = makeAuthEntity(authenticatedUser);
            Transfer transfer = null;
            try {
                transfer = restTemplate.exchange(baseUrl + "/getinfo/" + transferId, HttpMethod.GET, entity, Transfer.class).getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {;
                BasicLogger.log(e.getMessage());
            }
            return transfer;
        }

        public TransferDto getTransferDtoByTransferId(AuthenticatedUser authenticatedUser, Long transferId) {
            HttpEntity<Void> entity = makeAuthEntity(authenticatedUser);
            TransferDto transferDto = null;
            try {
                transferDto = restTemplate.exchange(baseUrl + transferId, HttpMethod.GET, entity, TransferDto.class).getBody();
            } catch (RestClientResponseException | ResourceAccessException e) {;
                BasicLogger.log(e.getMessage());
            }
            return transferDto;
        }

        private HttpEntity<Void> makeAuthEntity(AuthenticatedUser authenticatedUser){
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authenticatedUser.getToken());
            return new HttpEntity<>(headers);
        }

        private HttpEntity<TransferDto> makeTransferEntity(AuthenticatedUser user, TransferDto transferDto) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(user.getToken());
            return new HttpEntity<>(transferDto, headers);
        }

}

