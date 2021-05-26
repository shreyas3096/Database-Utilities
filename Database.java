import java.sql.*;
import java.io.*;
import java.util.*;


public class Database {

    private String protocol;
    private String driver;
    private String db;
    private String host;
    private String user;
    private String password;
    private Connection con;
    private int transactionCount = 0;

    public Database() throws DLException {
        try {
            String file = "dbconfig.properties";

            InputStream read = this.getClass().getResourceAsStream(file);
            Properties prop = new Properties();
            prop.load(read);

            this.protocol = prop.getProperty("protocol");
            this.host = prop.getProperty("host");
            this.db = prop.getProperty("db");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.driver = prop.getProperty("driver");

            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.con = DriverManager.getConnection(uri, this.user, this.password);
        }
        catch (Exception a) {
            throw new DLException(a," ");
        }
    }

    public Database(String fileName) throws DLException {
        try {
            InputStream read = this.getClass().getResourceAsStream(fileName);
            Properties prop = new Properties();
            prop.load(read);

            this.protocol = prop.getProperty("protocol");
            this.host = prop.getProperty("host");
            this.db = prop.getProperty("db");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.driver = prop.getProperty("driver");

            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.con = DriverManager.getConnection(uri, this.user, this.password);
        }
        catch (Exception a) {
            throw new DLException(a," ");
        }
    }

    public Database(String protocol, String driver, String db, String host, String user, String password) throws DLException{
        try {
            this.protocol = protocol;
            this.driver = driver;
            this.db = db;
            this.host = host;
            this.user = user;
            this.password = password;

            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.con = DriverManager.getConnection(uri, this.user, this.password);
        }
        catch (Exception a) {
            throw new DLException(a," ");
        }
    }

    public Connection getConnection(){
        return this.con;
    }

    public boolean close() throws DLException {
        boolean rc = false;
        try {
            this.con.close();
            rc = true;
            return rc;
        }
        catch(SQLException a){
            throw new DLException(a," ");
        }
    }

    public int execute(String sql, List<String> parameter) throws DLException{
        int rc = 0;
        try {
            PreparedStatement prepstat = prepareAndBind(sql,parameter);
            rc = prepstat.executeUpdate();
            //Statement stat = this.con.createStatement();
            //rc = stat.executeUpdate(sql);
            return rc;
        }
        catch(SQLException a){
            throw new DLException(a," ");
        }
    }

    public List<List<String>> getTable(String sql, List<String> parameter) throws DLException{
        List<List<String>> table = new ArrayList<>();
        try {
            table = getTable(sql, false, parameter);
            return table;
        }
        catch (Exception a){
            throw new DLException(a," ");
        }
    }

    public List<List<String>> getTable(String sql, boolean includeHeaders, List<String> parameter) throws DLException{
        List<List<String>> table = new ArrayList<List<String>>();
        try {
            //Statement stat = this.con.createStatement();
            PreparedStatement prepstat = prepareAndBind(sql,parameter);
            ResultSet rset = prepstat.executeQuery();

            ResultSetMetaData rsetmdata = rset.getMetaData();
            int numCols = rsetmdata.getColumnCount();

            if (includeHeaders) {
                List<String> row = new ArrayList<String>();

                for (int i = 1; i <= numCols; i++) {
                    row.add(rsetmdata.getColumnName(i));
                }
                table.add(row);
            }

            while (rset.next()) {
                List<String> row1 = new ArrayList<String>();

                for (int i = 1; i <= numCols; i++) {
                    row1.add(rset.getString(i));
                }
                table.add(row1);
            }
            return table;
        }
        catch (SQLException a){
            throw new DLException(a," ");
        }
    }

    public List<String> getRow(String sql, List<String> parameter) throws DLException{
        List<String> row = new ArrayList<String>();
        try {
            //Statement stat = this.con.createStatement();
            PreparedStatement prepstat = prepareAndBind(sql,parameter);
            ResultSet rset = prepstat.executeQuery();
            ResultSetMetaData rsmdata = rset.getMetaData();
            int numCols = rsmdata.getColumnCount();

            while (rset.next()) {
                for (int i = 1; i <= numCols; i++) {
                    row.add(rset.getString(i));
                }
            }
            return row;
        }
        catch(SQLException a) {
            throw new DLException(a," ");
        }
    }

    public String getValue(String sql, List<String> parameter) throws DLException{
        String value = null;
        try {
            //Statement stat = this.con.createStatement();
            PreparedStatement prepstat = prepareAndBind(sql,parameter);
            ResultSet rset = prepstat.executeQuery();

            if (rset.next()) {
                value = rset.getString(1);
            }

            return value;
        }
        catch (SQLException a){
            throw new DLException(a," ");
        }
    }

    private PreparedStatement prepareAndBind(String sql, List<String> params) throws DLException{
        try{
            PreparedStatement prepstat = this.con.prepareStatement(sql);

            if(params != null){
                for(int i = 0 ; i < params.size() ; i++){
                    prepstat.setString(i+1,params.get(i));
                }
            }
            return prepstat;
        }
        catch(SQLException a){
            throw new DLException(a," ");
        }
    }

    public void startTransaction() throws DLException {
        this.transactionCount =this.transactionCount+1;
        if (this.transactionCount == 1) {
            try {
                //System.out.println("inside starttransaction");
                this.con.setAutoCommit(false);
            }
            catch (SQLException a) {
                throw new DLException(a," ");
            }
        }
    }

    public void commitTransaction() throws DLException {
        this.transactionCount = this.transactionCount-1;
        if (this.transactionCount == 0) {
            try {
                //System.out.println("inside commitTransaction");
                this.con.commit();
                this.con.setAutoCommit(true);
            }
            catch (SQLException a) {
                throw new DLException(a," ");
            }
        }
    }

    public void rollbackTransaction() throws DLException {
        this.transactionCount = this.transactionCount-1;
        if (this.transactionCount == 0) {
            try {
                //System.out.println("inside rollbackTransaction");
                this.con.rollback();
                this.con.setAutoCommit(true);
            }
            catch (SQLException a) {
                throw new DLException(a," ");
            }
        }
    }

    public int getNewId(String pkName,String tblName) throws DLException{
        int Id;
        try {
            this.startTransaction();
            String sql = null;
            sql = "SELECT MAX( " + pkName + " ) FROM " + tblName;
            Id = Integer.parseInt(getValue(sql,null));

            Id = Id + 1;
            String insertsql = "INSERT INTO " + tblName + " ( " + pkName + " ) VALUES ( " + Id + " )";
            //System.out.println(insertsql);

            Statement stat = this.con.createStatement();
            int rc = stat.executeUpdate(insertsql);

            this.commitTransaction();
            return Id;
        }
        catch (SQLException a){
            throw new DLException(a," ");
        }
    }

    public int getNewId(String pkName,String tblName, List<List<String>> fKey) throws DLException{
        int Id;
        String insertsql1;
        String insertsql2;
        try {
            this.startTransaction();
            String sql = null;
            sql = "SELECT MAX(" + pkName + ") FROM " + tblName;
            Id = Integer.parseInt(getValue(sql,null));

            Id = Id + 1;

            insertsql1 = "INSERT INTO " + tblName + " ( " + pkName;
            insertsql2 = " ) VALUES ( " + Id;

            for(int i = 0 ; i < fKey.size() ; i++) {
                insertsql1 += " , " + fKey.get(i).get(0);
                insertsql2 += " , '" + fKey.get(i).get(1) + "' ";
            }
            insertsql2 += ")";

            System.out.println(insertsql1+insertsql2);
            Statement stat = this.con.createStatement();
            int rc = stat.executeUpdate(insertsql1+insertsql2);

            this.commitTransaction();
            return Id;
        }
        catch (SQLException a){
            throw new DLException(a," ");
        }
    }


    public static void main(String[] args) {
        try{
            Database db = new Database();
            System.out.println(db.getNewId("EquipID","equipment"));
            List<String> list = new ArrayList<String>();
            List<List<String>> listoflist = new ArrayList<>();
            list.add("Zip");
            list.add("14623");
            listoflist.add(list);
            System.out.println(db.getNewId("PassengerID","Passenger",listoflist));
            //System.out.println(db.getConnection().getCatalog());
            //System.out.print("****************************************************\n");
            //int test1 = db.execute("DELETE FROM phones WHERE PassengerID=?",Arrays.asList(new String[] {"11"}));
            //System.out.println(test1);
            //System.out.print("****************************************************\n");
            //List<List<String>> test2 = db.getTable("SELECT PassengerID,FName,LName,Street,Zip FROM PASSENGER WHERE Zip = ?",Arrays.asList(new String[] {"14623"}));
            //test2.forEach(
            //        c->{
            //            System.out.println(c);
            //        });
            //System.out.print("****************************************************\n");
            //List<List<String>> test3 = db.getTable("SELECT PassengerID,FName,LName,Street,Zip FROM PASSENGER WHERE Zip = ?",true,Arrays.asList(new String[] {"14623"}));
            //test3.forEach(
            //        e->{
            //            System.out.println(e);
            //        });
            //System.out.print("****************************************************\n");
            //List<String> test4 = db.getRow("SELECT PassengerID,FName,LName,Street,Zip FROM PASSENGER WHERE PassengerID=?",Arrays.asList(new String[] {"1"}));
            //test4.forEach(
            //        g->{
            //            System.out.println(g);
            //        });
            //System.out.print("****************************************************\n");
            //String test5 = db.getValue("SELECT FName FROM PASSENGER WHERE PassengerID=?",Arrays.asList(new String[] {"1"}));
            //System.out.println(test5);
            //System.out.println("0 - "+db.transactionCount);
            //db.startTransaction();
            //System.out.println("1 - "+db.transactionCount);
            //db.startTransaction();
            //System.out.println("2 - "+db.transactionCount);
            //db.rollbackTransaction();
            //System.out.println("1 - "+db.transactionCount);
            //db.commitTransaction();
            //System.out.println("0 - "+db.transactionCount);
        }
        catch(Exception a)
        {
            System.out.println("Oops");
        }
    }
}