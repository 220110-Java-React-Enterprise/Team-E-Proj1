package Repository;

public class GetType {

    //overloaded functions that decide what type of data will be insterted
    //into the SQL statement
    public String decideType(String a){
        return "Varchar(200)";
    }
    public String decideType(char a){
        return "Varchar(200)";
    }
    public String decideType(int a){
        return "INT";
    }
    public String decideType(float a){
        return "Float";
    }
    public String decideType(boolean a){
        return "Boolean";
    }

    // overloaded function that determines what kind of data
    // will be inserted into SQL statements
    public String decideJDBCType(String a){
        return "setString";
    }
    public String decideJDBCType(char a){
        return "setString";
    }
    public String decideJDBCType(int a){
        return "setInt";
    }
    public String decideJDBCType(float a){
        return "setBigDecimal";
    }
    public String decideJDBCType(boolean a){
        return "setBoolean";
    }


}
