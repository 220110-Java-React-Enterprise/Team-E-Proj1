package Repository;

public class GetType {

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
