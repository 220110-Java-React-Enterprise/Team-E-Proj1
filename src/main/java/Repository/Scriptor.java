package Repository;

import Repository.ConnectionManager;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Scriptor {
    private static Connection connection;
    private static String connectionString;

    protected Scriptor(String connectionString) {
        //this.connectionString = connectionString;
        connection = ConnectionManager.getConnection(connectionString);
    }

    //reads all the rows of a specific table
    public static ArrayList<ArrayList<Object>> readTable(Object test) {
        ArrayList<ArrayList<Object>> allData = new ArrayList<>();
        ArrayList<Object> rowAllData = new ArrayList<Object>();

        String tName = ""; // holds the table name
        try {
            //gets the table name
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod().toString().contains("TableName")) {
                    tName = (propertyDescriptor.getReadMethod().invoke(test).toString());
                }
            }

            //gets all the information from the table
            String selectStatement = "SELECT * FROM " + tName;
            PreparedStatement pstmt = connection.prepareStatement(selectStatement);
            ResultSet rs = pstmt.executeQuery();
            Integer numColumns = test.getClass().getDeclaredFields().length - 1;

            //places all the rows into an arraylist
            while (rs.next()) {
                rowAllData = new ArrayList<>();
                for (int i = 0; i < numColumns; i++) {

                    rowAllData.add(rs.getObject(i + 1));
                }
                allData.add(rowAllData);
            }
        } catch(IntrospectionException | IllegalAccessException | InvocationTargetException | SQLException e){
            FileLogger.getFileLogger().log(e);
        }
        for(ArrayList<Object> o : allData){
            for(int i = 0; i < o.size(); i++){
            }
        }
        return allData;
    }

    //overloaded function that prints table where a certain column equals something(Both are passed in)
    public static ArrayList<ArrayList<Object>> readTable(Object test, String columnAndID) {
        ArrayList<ArrayList<Object>> allData = new ArrayList<>();
        ArrayList<Object> rowAllData = new ArrayList<Object>();
        String tName = ""; // holds table name
        String[] options = columnAndID.split("=");
        String columnName = options[options.length-2]; //gets the column name
        String IDName = options[options.length - 1]; //gets the ID
        try {

            //gets the table name
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod().toString().contains("TableName")) {
                    tName = (propertyDescriptor.getReadMethod().invoke(test).toString());
                }
            }

            //gets rows from table where the column equals what was inserted
            String selectStatement = "SELECT * FROM " + tName + " WHERE " + columnName + " = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectStatement);
            pstmt.setObject(1, IDName);
            ResultSet rs = pstmt.executeQuery();
            Integer numColumns = test.getClass().getDeclaredFields().length - 1;
            while (rs.next()) {
                rowAllData = new ArrayList<>();
                for (int i = 0; i < numColumns; i++) {
                    rowAllData.add(rs.getObject(i + 1));
                }
                allData.add(rowAllData);
            }
        } catch(IntrospectionException | IllegalAccessException | InvocationTargetException | SQLException e){
            FileLogger.getFileLogger().log(e);
        }
        for(ArrayList<Object> o : allData){
            for(int i = 0; i < o.size(); i++){
            }
        }
        return allData;
    }

    //either updates a row or inserts a new one based on the id
    public static void updateTable(Object test) {
        String tName = "";
        int tPrimaryKey = -1;

        //gets table name and the id of the object inserted
        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod().toString().contains("TableName")) {
                    tName = (propertyDescriptor.getReadMethod().invoke(test).toString());
                }
                if (propertyDescriptor.getReadMethod().toString().contains("_id")) {
                    tPrimaryKey = (int) propertyDescriptor.getReadMethod().invoke(test);
                } else {

                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            FileLogger.getFileLogger().log(e);
        }
        int testID = -1;
        GetType dataType = new GetType();

        //creates array of all the fields in the object
        Field[] fields = test.getClass().getDeclaredFields();
        String readStatement = "Select * FROM " + tName;
        String fromWhere = " Where " + tName + "_id = ?";
        String updateStatement = "UPDATE " + tName + " SET ";
        String insertStatement = "INSERT INTO " + tName + " (";
        String questionString = "";
        String updateWhere;
        String insertWhere;
        String columnNames = "";
        String[] typeArr = new String[200]; //gets the sql type of the variable
        Class[] javaTypeArr = new Class[200];//gets the java type of the variable

        for (int i = 2; i < fields.length; i++) {
            String temp = fields[i].getName();
            javaTypeArr[i - 2] = fields[i].getType();
            typeArr[i - 2] = dataType.decideJDBCType(fields[i].getType().toString());

            //adds this to string unless it is at the end
            if (i != fields.length - 1) {
                updateStatement += temp + "= ?, ";
                insertStatement += temp + ", ";
                questionString += "?,";
                columnNames += " " + temp;

                //if at end adds these without commas and with closed bracket
            } else {
                updateStatement += temp + "= ?";
                insertStatement += temp + ") ";
                questionString += "?)";
            }
        }
        updateWhere = updateStatement + fromWhere; // creates update where string
        insertWhere = insertStatement + "VALUES (" + questionString; // creates insert where string
        PreparedStatement pstmt = null;
        String checkIfExists = readStatement + fromWhere;
        try {
            pstmt = connection.prepareStatement(checkIfExists);
            pstmt.setInt(1, tPrimaryKey);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                testID = rs.getInt(1);
            }

            //checks if the id entered already exists, if it doesn't then creates new row
            if (testID == -1) {
                pstmt = connection.prepareStatement(insertWhere);
                for (int i = 2; i < fields.length; i++) {
                    for (PropertyDescriptor propertyDescriptor :
                            Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                        if (propertyDescriptor.getReadMethod().toString().contains(fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1))) {
                            pstmt.setObject(i - 1, propertyDescriptor.getReadMethod().invoke(test));
                        }
                    }
                }
                pstmt.executeUpdate();

                //if the id did exist then it updates that row
            } else {
                pstmt = connection.prepareStatement(updateWhere);
                for (int i = 1; i < fields.length; i++) {
                    for (PropertyDescriptor propertyDescriptor :
                            Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                        if (propertyDescriptor.getReadMethod().toString().contains(fields[i].getName().substring(0, 1).toUpperCase() + fields[i].getName().substring(1))) {
                            if (fields[i].getName().substring(1).contains("_id")) {
                                pstmt.setObject(fields.length - 1, propertyDescriptor.getReadMethod().invoke(test));
                            } else {
                                pstmt.setObject(i - 1, propertyDescriptor.getReadMethod().invoke(test));
                            }
                        }
                    }
                }
                pstmt.executeUpdate();
            }
        } catch (SQLException | IntrospectionException | InvocationTargetException | IllegalAccessException ex) {
            FileLogger.getFileLogger().log(ex);
        }
    }

    //deletes a row given an id
    public static void DeleteStatement(Object test) {
        PreparedStatement pstmt = null;
        String tName = "";
        Field[] fields = test.getClass().getDeclaredFields();

        try {

            //gets the table name
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod().toString().contains("TableName")) {
                    tName = (propertyDescriptor.getReadMethod().invoke(test).toString());
                }
            }
            String deleteStatement = "Delete FROM " + tName + " Where " + tName + "_id = ?";
            pstmt = connection.prepareStatement(deleteStatement);

            //gets the primary key of the object then deletes that row
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getName().contains("_id")) {
                    pstmt.setInt(1, (Integer) propertyDescriptor.getReadMethod().invoke(test));
                }
            }
            pstmt.executeUpdate();

        } catch (IntrospectionException | SQLException | IllegalAccessException | InvocationTargetException e) {
            FileLogger.getFileLogger().log(e);
        }
    }

    //builds a new table based on object
    public static String BuildStatement(Object test) {
        String tName = "";
        try {

            //gets table name
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(test.getClass(), Object.class).getPropertyDescriptors()) {
                if (propertyDescriptor.getReadMethod().toString().contains("TableName")){
                    tName = (propertyDescriptor.getReadMethod().invoke(test).toString());
                }
            }
        } catch (IntrospectionException e) {
            FileLogger.getFileLogger().log("could not show getters");
        } catch (InvocationTargetException | IllegalAccessException e) {
            FileLogger.getFileLogger().log(e);
        }
        String finalCreateString = "CREATE TABLE IF NOT EXISTS " + tName + " ( ";
        Field[] fields = test.getClass().getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            String temp = fields[i].getName();
            String type = (fields[i].getType().toString());

            //sets the primary key
            if(i == 1) {
                finalCreateString += tName + "_id " + type + " auto_increment not null, ";
            }

            //checks to see the java type and changes it to SQL type
            else if(type.contains("String")){
                finalCreateString += temp + " Varchar(200), ";
            }
            else if(type.contains("int")){
                finalCreateString += temp + " int, ";
            }
            else if(type.contains("bool")){
                finalCreateString += temp + " boolean, ";
            }
            else if(type.contains("char")){
                finalCreateString += temp + " VARCHAR(200), ";
            }
            else if(type.contains("float")){
                finalCreateString += temp + " float, ";
            }

        }
        //adds the primary key constraint
        finalCreateString += "Constraint " + tName + "_id_pk PRIMARY KEY (" + tName + "_id));" ;
        try {
            PreparedStatement pstmt = connection.prepareStatement(finalCreateString);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            FileLogger.getFileLogger().log(e);
        }
        return finalCreateString;
    }
}