package dao;

//import com.sun.deploy.util.SessionState;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client");
        ResultSet result = stmt.getResultSet();
        List<BankClient> list = new ArrayList<>();
        while (result.next()) {
            list.add(new BankClient(result.getLong(1), result.getString(2), result.getString(3), result.getLong(4)));
            Long id = result.getLong(1);
        }
        result.close();
        stmt.close();
        return list;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        try (PreparedStatement stmt
                     = connection.prepareStatement("select * from bank_client where name=? AND password=?")) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.executeQuery();
            ResultSet result = stmt.getResultSet();
            if (result.next()) {
                return true;
            }
        }
        return false;
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        try (PreparedStatement stmt
                     = connection.prepareStatement("update bank_client set money=money+'" + transactValue + "' where name = '" + name + "'")) {
            stmt.executeUpdate();
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where id='" + id + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        BankClient bankclient = new BankClient(result.getLong(1), result.getString(2), result.getString(3), result.getLong(4));
        result.close();
        stmt.close();
        return bankclient;
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long money = result.getLong(4);
        result.close();
        stmt.close();
        if (money >= expectedSum) {
            return true;
        }
        return false;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        if (result.next()) {
            BankClient bankclient = new BankClient(result.getLong(1), result.getString(2), result.getString(3), result.getLong(4));
            result.close();
            stmt.close();
            return bankclient;
        }
        return null;
    }

    public void addClient(BankClient client) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + client.getName() + "'");
        ResultSet result = stmt.getResultSet();
        if (!result.next()) {
            stmt.execute("insert into bank_client (name,password,money) values ('" + client.getName() + "','" + client.getPassword() + "', " + client.getMoney() + ")");
        } else {
            client.setId(result.getLong(1));
        }
        stmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
