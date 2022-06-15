package com.techelevator.tenmo.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) { this.baseUrl = url; }
//
//    public BigDecimal getBalanceById(Long id) {
//        try {
//            BigDecimal balance =
//                    restTemplate.exchange(baseUrl + "")
//        }
//    }
}
