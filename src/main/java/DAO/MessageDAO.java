package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Util.ConnectionUtil;

public class MessageDAO {

    // create a new message
    public Message createMessage(Message message) throws SQLException {

        String query = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    message.setMessage_id(generatedKeys.getInt(1));
                }
            }
        }

        return message;
    }

    // retrieve all messages
    public List<Message> getAllMessages() throws SQLException {

        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Message";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                ));
            }
        }

        return messages;
    }

    // retrieve a message by ID
    public Message getMessageById(int messageId) throws SQLException {

        String query = "SELECT * FROM Message WHERE message_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
            }
        }

        return null;
    }

    // delete a message by ID
    public Message deleteMessageById(int messageId) throws SQLException {

        String query = "DELETE FROM Message WHERE message_id = ?";
        Message deletedMessage = getMessageById(messageId); 

        if (deletedMessage != null) {
            try (Connection conn = ConnectionUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, messageId);
                stmt.executeUpdate();
            }
        }

        return deletedMessage;
    }

    // update a message by ID
    public Message updateMessage(int messageId, String newMessageText) throws SQLException {
        
        String query = "UPDATE Message SET message_text = ? WHERE message_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newMessageText);
            stmt.setInt(2, messageId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return getMessageById(messageId); 
            }
        }

        return null;
    }

    // retrieve all messages posted by a particular user
    public List<Message> getMessagesByUserId(int userId) throws SQLException {

        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Message WHERE posted_by = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                ));
            }
        }

        return messages;
    }
}
