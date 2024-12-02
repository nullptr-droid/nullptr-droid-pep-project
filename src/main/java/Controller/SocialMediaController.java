package Controller;

import Model.Account;
import Model.Message;
import DAO.AccountDAO;
import DAO.MessageDAO;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.SQLException;
import java.util.List;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController() {
        AccountDAO accountDAO = new AccountDAO();
        MessageDAO messageDAO = new MessageDAO();

        this.accountService = new AccountService(accountDAO);
        this.messageService = new MessageService(messageDAO, accountDAO);
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        // endpoints for Account
        app.post("/register", this::registerUser);
        app.post("/login", this::loginUser);

        // endpoints for message
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{id}", this::getMessageById);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUser);
        app.delete("/messages/{id}", this::deleteMessageById);
        app.patch("/messages/{id}", this::updateMessageById);

        return app;
    }

    // register new user
    private void registerUser(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
                context.status(400).json("Error: Username cannot be blank and password must be at least 4 characters long.");
                return;
            }
            Account existingAccount = accountService.getAccountByUsername(account.getUsername());
            if (existingAccount != null) {
                context.status(400).json("Error: Account with that username already exists.");
                return;
            }
            Account createdAccount = accountService.registerUser(account);
            context.status(200).json(createdAccount);
        } catch (SQLException e) {
            context.status(500).json("Database Error: " + e.getMessage());
        }
    }

    // user login
    private void loginUser(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
                context.status(400).json("Error: Username and password cannot be blank.");
                return;
            }
            Account loggedInAccount = accountService.loginUser(account);
            if (loggedInAccount != null) {
                context.status(200).json(loggedInAccount);
            } else {
                context.status(401).json("Error: Invalid username or password.");
            }
        } catch (SQLException e) {
            context.status(500).json("Database Error: " + e.getMessage());
        }
    }

    // create new message
    private void createMessage(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
                context.status(400).json("Error: Message text cannot be blank and must be under 255 characters.");
                return;
            }
            if (accountService.getAccountById(message.getPosted_by()) == null) {
                context.status(400).json("Error: Invalid user.");
                return;
            }
            Message createdMessage = messageService.createMessage(message);
            context.status(200).json(createdMessage);
        } catch (SQLException e) {
            context.status(500).json("Database Error: " + e.getMessage());
        }
    }

    // retrieve all messages
    private void getAllMessages(Context context) {
        try {
            List<Message> messages = messageService.getAllMessages();
            context.status(200).json(messages);
        } catch (SQLException e) {
            context.status(500).json("Database Error: " + e.getMessage());
        }
    }

    // retrieve a message by ID
    private void getMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("id"));
            Message message = messageService.getMessageById(messageId);
            if (message != null) {
                context.status(200).json(message);
            } else {
                context.status(404).json("Error: Message not found.");
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400).json("Error: Invalid request.");
        }
    }

    // dlete a message by ID
    private void deleteMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("id"));
            Message deletedMessage = messageService.deleteMessageById(messageId);
            if (deletedMessage != null) {
                context.status(200).json(deletedMessage);
            } else {
                context.status(200).json("{}"); // Idempotent, return empty response if not found.
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400).json("Error: Invalid request.");
        }
    }

    // update message by ID
    private void updateMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("id"));
            String newMessageText = context.bodyAsClass(String.class);
            if (newMessageText.isBlank() || newMessageText.length() > 255) {
                context.status(400).json("Error: Message text cannot be blank and must be under 255 characters.");
                return;
            }
            Message updatedMessage = messageService.updateMessage(messageId, newMessageText);
            if (updatedMessage != null) {
                context.status(200).json(updatedMessage);
            } else {
                context.status(400).json("Error: Message not found.");
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400).json("Error: Invalid request.");
        }
    }

    // get messages by user
    private void getMessagesByUser(Context context) {
        try {
            int accountId = Integer.parseInt(context.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByUserId(accountId);
            context.status(200).json(messages);
        } catch (SQLException | NumberFormatException e) {
            context.status(400).json("Error: Invalid request.");
        }
    }

}