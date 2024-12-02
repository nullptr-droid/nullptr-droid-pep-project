package DAO;

import Model.Account;
import java.sql.*;

import Util.ConnectionUtil;

public class AccountDAO {

    // Create a new user account
    public Account createAccount(Account account) throws SQLException {
        String query = "INSERT INTO Account (username, password) VALUES (?, ?)";

        try (Connection conn = ConnectionUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setAccount_id(generatedKeys.getInt(1));
                }
            }
        }
        
        return account;
    }

    // Identify an account by username
    public Account findAccountByUsername(String username) throws SQLException {
        String query = "SELECT * FROM Account WHERE username = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        }

        return null;
    }

    // Return account by account_id
    public Account findAccountById(int accountId) throws SQLException {
        String query = "SELECT * FROM Account WHERE account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        }

        return null;
    }
}
