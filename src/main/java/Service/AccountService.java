package Service;

import DAO.AccountDAO;
import Model.Account;
import java.sql.SQLException;

public class AccountService {
    
    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    // register new user's account
    public Account registerUser(Account account) throws SQLException {
        // ensure user credentials are valid
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // ensure username does not already exist
        Account existingAccount = accountDAO.findAccountByUsername(account.getUsername());
        if (existingAccount != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        return accountDAO.createAccount(account);
    }

     // for user login
     public Account loginUser(Account account) throws SQLException {
        Account yourAccount = accountDAO.findAccountByUsername(account.getUsername());
        
        if (yourAccount == null || !yourAccount.getPassword().equals(account.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return yourAccount;
    }
}
