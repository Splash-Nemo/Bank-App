package databaseObjects;

import java.util.ArrayList;
import java.sql.*;
import java.math.BigDecimal;
import java.sql.*;

public class ConnectJDBC {
    private static final String dburl = "jdbc:mysql://localhost:3306/bank-server?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String dbusername= "root";
    private static final String dbpassword= "aditya@1234";

    public static Users validateLogin(String userName, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection= DriverManager.getConnection(dburl, dbusername, dbpassword);

        PreparedStatement preparedStatement= connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME= ? AND PASSWORD= ?");

        preparedStatement.setString(1,userName);
        preparedStatement.setString(2, password);

        ResultSet resultSet= preparedStatement.executeQuery();

        if(resultSet.next()){
            int userID= resultSet.getInt("ID");
            BigDecimal currentBalance= resultSet.getBigDecimal("CURRENT_BALANCE");

            return new Users(userID, userName, password, currentBalance);
        }

        return null;
    }

    public static boolean register(String userName, String password) throws SQLException, ClassNotFoundException {
        if(!checkUser(userName)){
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection= DriverManager.getConnection(dburl, dbusername, dbpassword);
            
            PreparedStatement preparedStatement= connection.prepareStatement("INSERT INTO USERS(USERNAME, PASSWORD, CURRENT_BALANCE) VALUES(?,?,?)");
            
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.setBigDecimal(3, new BigDecimal(0));
            
            preparedStatement.executeUpdate();
            return true;
        }
        return false;
    }

    private static boolean checkUser(String userName) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection= DriverManager.getConnection(dburl, dbusername, dbpassword);

        PreparedStatement preparedStatement= connection.prepareStatement("SELECT * FROM USERS WHERE USERNAME= ?");
        preparedStatement.setString(1,userName);

        ResultSet resultSet= preparedStatement.executeQuery();

        return resultSet.next();
    }

    public static ArrayList<Transactions> getPastTransaction(Users user) {
        return new ArrayList<>();
    }

    public static boolean addTransactionToDatabase(Transactions transaction) throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection= DriverManager.getConnection(dburl, dbusername, dbpassword);

            PreparedStatement insertTransaction = connection.prepareStatement("INSERT INTO TRANSACTIONS (USER_ID, TRANSACTION_TYPE, TRANSACTION_AMOUNT, TRANSACTION_DATE) VALUES (?, ?, ?, NOW())");

            insertTransaction.setInt(1, transaction.getUserID());
            insertTransaction.setString(2, transaction.getTransactionType());
            insertTransaction.setBigDecimal(3, transaction.getTransactionAmount());

            insertTransaction.executeUpdate();

            return true;
        } catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static boolean updateCurrentBalance(Users user) throws ClassNotFoundException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword);

            PreparedStatement updateBalance = connection.prepareStatement(
                    "UPDATE USERS SET CURRENT_BALANCE = ? WHERE ID = ?"
            );

            updateBalance.setBigDecimal(1, user.getCurrentBalance());
            updateBalance.setInt(2, user.getId());

            updateBalance.executeUpdate();
            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean transfer(Users user, String transferredUsername, float transferAmount) throws ClassNotFoundException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword);

            PreparedStatement queryUser = connection.prepareStatement(
                    "SELECT * FROM USERS WHERE USERNAME = ?"
            );

            queryUser.setString(1, transferredUsername);
            ResultSet resultSet = queryUser.executeQuery();

            while (resultSet.next()) {
                Users transferredUser = new Users(
                        resultSet.getInt("id"),
                        transferredUsername,
                        resultSet.getString("password"),
                        resultSet.getBigDecimal("current_balance")
                );

                Transactions transferTransaction = new Transactions(
                        user.getId(),
                        "Transfer",
                        new BigDecimal(-transferAmount),
                        null
                );

                Transactions receivedTransaction = new Transactions(
                        transferredUser.getId(),
                        "Transfer",
                        new BigDecimal(transferAmount),
                        null
                );

                transferredUser.setCurrentBalance(transferredUser.getCurrentBalance().add(BigDecimal.valueOf(transferAmount)));
                updateCurrentBalance(transferredUser);

                user.setCurrentBalance(user.getCurrentBalance().subtract(BigDecimal.valueOf(transferAmount)));
                updateCurrentBalance(user);

                addTransactionToDatabase(transferTransaction);
                addTransactionToDatabase(receivedTransaction);

                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Transactions> getPastTransactions(Users user) throws ClassNotFoundException {
        ArrayList<Transactions> pastTransactions = new ArrayList<>();
        Class.forName("com.mysql.cj.jdbc.Driver");
        try{
            Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword);

            PreparedStatement selectAllTransaction = connection.prepareStatement(
                    "SELECT * FROM TRANSACTIONS WHERE USER_ID = ?"
            );
            selectAllTransaction.setInt(1, user.getId());

            ResultSet resultSet = selectAllTransaction.executeQuery();

            while(resultSet.next()){
                // create transaction obj
                Transactions transaction = new Transactions(
                        user.getId(),
                        resultSet.getString("transaction_type"),
                        resultSet.getBigDecimal("transaction_amount"),
                        resultSet.getDate("transaction_date")
                );

                // store into array list
                pastTransactions.add(transaction);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return pastTransactions;
    }
}
