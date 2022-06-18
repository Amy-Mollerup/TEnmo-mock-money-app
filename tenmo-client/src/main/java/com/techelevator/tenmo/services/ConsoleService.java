package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final Scanner scanner = new Scanner(System.in);
    private final String dashes = String.format("%043d", 0).replace("0", "-");
    // No amount column
    private final String smallColumnFormat = "%-12s%-23s\n";
    // Yes amount column
    private final String largeColumnFormat = "%-12s%-23s%s\n";

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

    public void printTransferUpdateChoiceMenu() {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
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

    public void printCurrentBalance(BigDecimal balance) {
            System.out.println("Your current account balance is: " + balance);
    }

    public void printUsers(User[] allUsers) {
        printHeaders("Users", "Name", false);
        for(User user : allUsers) {
            System.out.printf(smallColumnFormat, user.getId(), user.getUsername());
        }
        System.out.println("---------");
    }

    public void printHeaders(String menuTitle, String field2, boolean amount) {
        System.out.println(dashes);
        System.out.println(menuTitle);
        if(amount) {
            System.out.printf(largeColumnFormat, "ID", field2, "Amount");
        } else {
            System.out.printf(smallColumnFormat, "ID", field2);
        }
        System.out.println(dashes);
    }

    public void printPendingRequests(Transfer[] transfers) {
        // TODO works, need to get username instead of accountID for TO column
        // This method runs into a hiccup with the refactoring of Services out of ConsoleService, I'm not sure how to get the usernames
        // I can't figure out how to do this without using another service class so this might be a goner
        printHeaders("Pending Transfers", "To", true);
        for (Transfer t : transfers) {
            if (t.getTransferStatusId() == 1) {
                System.out.printf(largeColumnFormat, t.getTransferId(), t.getAccountTo(), t.getAmount().toString());
            }
        }
    }


    public void printTransferHistory(Transfer[] transfers) {
        //  TODO amount needs to be corrected - pulling wrong data - check if this is still the case
        printHeaders("Transfer History", "From/To", true);
        for (Transfer t : transfers) {
            if (t.getTransferTypeId() == 1) {
                System.out.printf(largeColumnFormat, t.getTransferId(), "From: " + t.getAccountFrom(), t.getAmount().toString());
            } else {
                System.out.printf(largeColumnFormat, t.getTransferId(), "To:   " + t.getAccountTo(), t.getAmount().toString());
            }
        }


    }


//  This big mess has been simplified and added to the Transfer model for now. There may be a better solution for the accountFrom and accountTo
//
//    public void transferDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {
//        String transferId = transfer.getTransferId().toString();
//        String accountFrom = userService.getUserByAccountId(authenticatedUser, transfer.getAccountFrom()).getUsername();
//        String accountTo = userService.getUserByAccountId(authenticatedUser, transfer.getAccountTo()).getUsername();
//        String transferTypeDescription = transfer.getTransferTypeDescription();
//        String transferStatusDescription = transfer.getTransferStatusDescription();
//        String amount = transfer.getAmount().toString();
//
//        System.out.println("\nID: " + transferId +
//                    "\nFrom: " + accountFrom +
//                    "\nTo: " + accountTo +
//                    "\nType: " + transferTypeDescription +
//                    "\nStatus: " + transferStatusDescription +
//                    "\nAmount: $" + amount);
//    }
}

