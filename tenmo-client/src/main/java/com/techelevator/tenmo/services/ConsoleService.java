package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final Scanner scanner = new Scanner(System.in);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final String dashes = String.format("%043d", 0).replace("0", "-");
    private final String columnFormat = "%-12s%-23s\n";

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printCurrentBalance(AuthenticatedUser user) {
        BigDecimal balance = accountService.getBalance(user);
        if (balance != null) {
            System.out.println("Your current account balance is: " + balance);
        } else {
            printErrorMessage();
        }
    }

    public void printUsers(AuthenticatedUser authenticatedUser) {
        printHeaders("Users", "Name", false);
        User[] allUsers = userService.getAllUsers(authenticatedUser);
        for(User user : allUsers) {
            System.out.printf(columnFormat, user.getId(), user.getUsername());
        }
        System.out.println("---------");
    }

    public void printHeaders(String menuTitle, String field2, boolean amount) {
        System.out.println(dashes);
        System.out.println(menuTitle);
        if(amount) {
            String newFormat = columnFormat + "%s";
            System.out.printf(newFormat, "ID", field2, "amount");
        } else {
            System.out.printf(columnFormat, "ID", field2);
        }
        System.out.println(dashes);
    }

    public void printPendingRequests(AuthenticatedUser authenticatedUser) {
        //just need to clean up aesthetic formatting
        printHeaders("Pending Transfers", "To", true );
        Account accountByUserId = accountService.getAccountByUserId(authenticatedUser, authenticatedUser.getUser().getId());
        long accountId = accountByUserId.getAccountId();
        Transfer[] transfers = transferService.getAllTransfersByAccountId(authenticatedUser, accountId);
        for(Transfer t : transfers) {
            if(t.getTransferStatusId() != 1) {
                System.out.println("No pending transfers.");
                break;
            } else {
                System.out.println("Current pending transfers: " + t.getTransferStatusId() + t.getAccountFrom() + t.getAccountTo() + t.getAmount());
            }
        }
    }



}
