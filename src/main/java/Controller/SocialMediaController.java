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
        app.get("/messages/{message_id}", this::getMessageById);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUser);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessageById);

        return app;
    }

    // register new user
    private void registerUser(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
                context.status(400);
                return;
            }
            Account existingAccount = accountService.getAccountByUsername(account.getUsername());
            if (existingAccount != null) {
                context.status(400);
                return;
            }
            Account createdAccount = accountService.registerUser(account);
            context.status(200).json(createdAccount);
        } catch (SQLException e) {
            context.status(400);
        }
    }

    // user login
    private void loginUser(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
                context.status(400);
                return;
            }
            Account loggedInAccount = accountService.loginUser(account);
            if (loggedInAccount != null) {
                context.status(200).json(loggedInAccount);
            } else {
                context.status(401).json("");
            }
        } catch (SQLException e) {
            context.status(400);
        }
    }

    // create new message
    private void createMessage(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
                context.status(400);
                return;
            }
            if (accountService.getAccountById(message.getPosted_by()) == null) {
                context.status(400);
                return;
            }
            Message createdMessage = messageService.createMessage(message);
            context.status(200).json(createdMessage);
        } catch (SQLException e) {
            context.status(400);
        }
    }

    // retrieve all messages
    private void getAllMessages(Context context) {
        try {
            List<Message> messages = messageService.getAllMessages();
            context.status(200).json(messages);
        } catch (SQLException e) {
            context.status(400);
        }
    }

    // retrieve a message by ID
    private void getMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message message = messageService.getMessageById(messageId);
            if (message != null) {
                context.status(200).json(message);
            } else {
                context.status(200); // Response status should always be 200, which is the default
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400);
        }
    }

    // dlete a message by ID
    private void deleteMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message deletedMessage = messageService.deleteMessageById(messageId);
            if (deletedMessage != null) {
                context.status(200).json(deletedMessage);
            } else {
                context.status(200); 
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400);
        }
    }

    // update message by ID
    private void updateMessageById(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            String newMessageText = context.bodyAsClass(String.class);
            if (newMessageText.isBlank() || newMessageText.length() > 255) {
                context.status(400);
                return;
            }
            Message updatedMessage = messageService.updateMessage(messageId, newMessageText);
            if (updatedMessage != null) {
                context.status(200).json(updatedMessage);
            } else {
                context.status(400);
            }
        } catch (SQLException | NumberFormatException e) {
            context.status(400);
        }
    }

    // get messages by user
    private void getMessagesByUser(Context context) {
        try {
            int accountId = Integer.parseInt(context.pathParam("account_id"));
            List<Message> messages = messageService.getMessagesByUserId(accountId);
            context.status(200).json(messages);
        } catch (SQLException | NumberFormatException e) {
            context.status(400);
        }
    }

}