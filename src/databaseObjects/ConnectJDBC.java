package databaseObjects;

import java.util.ArrayList;

public class ConnectJDBC {
    public static Users validateLogin(String userName, String password) {

        return new Users();
    }

    public static boolean register(String user, String password) {
        return false;
    }

    public static ArrayList<Transactions> getPastTransaction(Users user) {
        return new ArrayList<>();
    }

    public static boolean addTransactionToDatabase(Transactions transaction) {
        return true;
    }

    public static boolean updateCurrentBalance(Users user) {
        return true;
    }

    public static boolean transfer(Users user, String transferredUser, float amount) {
        return true;
    }

    public static ArrayList<Transactions> getPastTransactions(Users user) {
    }
}
