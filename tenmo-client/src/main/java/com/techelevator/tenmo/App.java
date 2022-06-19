package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
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
    private long userAccountId;

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
        userAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
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
        BigDecimal balance = accountService.getBalance(currentUser);
        if(balance != null) {
            consoleService.printCurrentBalance(balance);
        } else {
            consoleService.printErrorMessage();
        }
	}

	private void viewTransferHistory() {
        Transfer[] transfers = transferService.getAllTransfersByAccountId(currentUser, userAccountId);
		consoleService.printTransferHistory(transfers);
        long transferId = consoleService.promptForInt("Please enter Transfer ID to view details (0 to cancel): ");
        if (transferId != 0) {
            Transfer transfer = transferService.getTransferByTransferId(currentUser, transferId);
            System.out.println(transfer);
        }
	}

	private void viewPendingRequests() {
        Transfer[] transfers = transferService.getAllTransfersByAccountId(currentUser, userAccountId);
        consoleService.printPendingRequests(transfers, currentUser.getUser().getUsername());
        long transferID = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (transferID != 0) {
            consoleService.printTransferUpdateChoiceMenu();
            int choice = consoleService.promptForInt("Please choose an option: ");
            if(choice != 0) {
                transferService.updateTransferStatus(currentUser, transferID, choice);
            }
        }
		
	}

	private void sendBucks() {
        consoleService.printUsers(userService.getAllUsers(currentUser));
        long toUserId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if (toUserId != 0) {
            handleCreateTransfer("Send", toUserId);
        } else {
            consoleService.printErrorMessage();
        }
	}

	private void requestBucks() {
		consoleService.printUsers(userService.getAllUsers(currentUser));
        long fromUserId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if (fromUserId != 0) {
            handleCreateTransfer("Request", fromUserId);
        } else {
            consoleService.printErrorMessage();
        }
	}

    // Created to make code more DRY
    private void handleCreateTransfer(String transferType, long secondUserId) {
        boolean success = false;
        long secondPartyAccountId = accountService.getAccountByUserId(currentUser, secondUserId).getAccountId();
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if(transferType.equals("Send")) {
            success = transferService.createTransfer(currentUser, 2, userAccountId, secondPartyAccountId, amount);
        } else if(transferType.equals("Request")) {
           success = transferService.createTransfer(currentUser, 1, secondPartyAccountId, userAccountId, amount);
        }
        if(success) {
            System.out.println("Transfer successfully created.");
        } else {
            consoleService.printErrorMessage();
        }
    }

}
