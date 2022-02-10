package Repository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static Connection connection;

    private ConnectionManager(){

    }

    //takes in a string and creates a connection with it
    public static Connection getConnection(String connectionString){
        if(connection == null){
            connection = connect(connectionString);
        }
        return connection;
    }

    //also returns the connection
    private static Connection connect(String connectionString){
        try{

            connection = DriverManager.getConnection(connectionString);

        } catch(SQLException e){
            FileLogger.getFileLogger().log("Couldn't connect to database");
        }
        return connection;
    }
}