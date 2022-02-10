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

    //   public static ConnectionManager getConnectionManager(){
    //       if(connectionManager == null) {
    //           connectionManager = new ConnectionManager();
    //       }
    //      return connectionManager;
    //   }

    public static Connection getConnection(String connectionString){
        if(connection == null){
            connection = connect(connectionString);
        }
        return connection;
    }

    private static Connection connect(String connectionString){
        try{
//            Properties props = new Properties();
//            FileReader fr = new FileReader("src/main/resources/jdbc.properties");
//            props.load(fr);
//
//            String connectionString = "jdbc:mariadb://" +
//                    props.getProperty("hostname") + ":" +
//                    props.getProperty("port") + "/" +
//                    props.getProperty("dbname") + "?user=" +
//                    props.getProperty("username") + "&password=" +
//                    props.getProperty("password");
//
//            //Class.forName("org.mariadb.jdbc.Driver");

            connection = DriverManager.getConnection(connectionString);

        } catch(SQLException e){
            FileLogger.getFileLogger().log("Couldn't connect to database");
        }
        return connection;
    }
}