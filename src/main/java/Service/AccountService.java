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
        return accountDAO.createAccount(account);
    }

     // for user login
     public Account loginUser(Account account) throws SQLException {
        Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());

        if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
            return existingAccount;
        }

        return null; 
    }

    // fetch an account by username
    public Account getAccountByUsername(String username) throws SQLException {
        return accountDAO.getAccountByUsername(username);
    }

    // fetch an account by ID
    public Account getAccountById(int accountId) throws SQLException {
        return accountDAO.getAccountById(accountId);
    }

}
