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
}
