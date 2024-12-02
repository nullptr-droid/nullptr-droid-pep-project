package Service;

import DAO.MessageDAO;
import DAO.AccountDAO;
import Model.Message;
import java.sql.SQLException;
import java.util.List;

public class MessageService {
    
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    // create a new message
    public Message createMessage(Message message) throws SQLException {
        // check if message is valid
        if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
            throw new IllegalArgumentException("Invalid message text.");
        }

        // check if user is valid
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            throw new IllegalArgumentException("Invalid user.");
        }

        return messageDAO.createMessage(message);
    }

    // retrieve all messages
    public List<Message> getAllMessages() throws SQLException {
        return messageDAO.getAllMessages();
    }

    // get message by ID
    public Message getMessageById(int messageId) throws SQLException {
        return messageDAO.getMessageById(messageId);
    }

    // delete message by ID
    public Message deleteMessageById(int messageId) throws SQLException {
        return messageDAO.deleteMessageById(messageId);
    }

    // update message text
    public Message updateMessage(int messageId, String newMessageText) throws SQLException {
        // check if message text is valid
        if (newMessageText.isBlank() || newMessageText.length() > 255) {
            throw new IllegalArgumentException("Invalid message text.");
        }

        return messageDAO.updateMessage(messageId, newMessageText);
    }

    // retrieve all messages posted by a particular user
    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        return messageDAO.getMessagesByUserId(userId);
    }
}
