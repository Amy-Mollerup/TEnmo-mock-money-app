package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        consoleService.printCurrentBalance(currentUser);
	}

	private void viewTransferHistory() {
		consoleService.printTransferHistory(currentUser);
        Long transferId = (long) consoleService.promptForInt("Please enter Transfer ID to view details (0 to cancel): ");
        Transfer transfer = transferService.getTransferByTransferId(currentUser, transferId);
        System.out.println(consoleService.transferDetails(currentUser, transfer));

	}

	private void viewPendingRequests() {
        consoleService.printPendingRequests(currentUser);
        Long transferID = (long) consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        System.out.println(consoleService.transferUpdateChoiceMenu());
        int choice = consoleService.promptForInt("Please choose an option: ");
        transferService.updateTransferStatus(currentUser, transferID, choice);

		
	}

	private void sendBucks() {
        consoleService.printUsers(currentUser);

        Long toUserId = (long) consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        Long fromAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        Long toAccountId = accountService.getAccountByUserId(currentUser, toUserId).getAccountId();
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");

        transferService.createSendTransfer(currentUser, fromAccountId, toAccountId, amount);
	}

	private void requestBucks() {
		consoleService.printUsers(currentUser);

        Long fromUserId = (long) consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        Long fromAccountId = accountService.getAccountByUserId(currentUser, fromUserId).getAccountId();
        Long toAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");

        transferService.createRequestTransfer(currentUser, fromAccountId, toAccountId, amount);
	}

}
