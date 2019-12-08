package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {
    private static BankClientService bankClientService;

    public static BankClientService getInstance() {
        if (bankClientService == null) {
            bankClientService = new BankClientService();
        }
        return bankClientService;
    }

    private BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<BankClient> getAllClient() throws DBException {
        try {
            return getBankClientDAO().getAllBankClient();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean addClient(BankClient client) throws DBException {
        try {
            getBankClientDAO().addClient(client);
            if(client.getId()!=0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws DBException {
        BankClientDAO dao = getBankClientDAO();
        BankClient bankClient;
        try {
            bankClient = dao.getClientByName(name);
            if ((!dao.validateClient(sender.getName(), sender.getPassword()))
                    || bankClient == null
                    ||!(dao.isClientHasSum(sender.getName(),value))) {
                return false;
            }
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), value * (-1));
            dao.updateClientsMoney(bankClient.getName(), bankClient.getPassword(), value);
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return true;

    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance());
//com.mysql.jdbc.Driver
            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=123&serverTimezone=UTC");       //password

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
