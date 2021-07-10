
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.sql.rowset.CachedRowSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MSİ
 */

@SessionScoped

@ManagedBean(name="Customer")
public class Customer {
    
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    List<List<Object>> shoppingCart = new ArrayList<List<Object>>();
    Date someDate;
    Date currentDate;

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getSomeDate() {
        return someDate;
    }

    public void setSomeDate(Date someDate) {
        this.someDate = someDate;
    }
    
    public Customer(){
        try{
        currentDate = new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<List<Object>> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(List<List<Object>> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
   
    Connection connection;
    CachedRowSet rowSet=null;
    
    public ResultSet ListProducts() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM PRODUCTS WHERE IS_SALEABLE = TRUE");

            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public String AddToCart(String productID, String productName, int amount) throws SQLException
    {
        for (int i=0; i < shoppingCart.size(); i++)
        {
            
            for (int j=0; j < shoppingCart.get(i).size(); j++)
            {
                if(shoppingCart.get(i).get(0).equals(productID))
                {
                    int oldAmount = (int)shoppingCart.get(i).get(2);
                    shoppingCart.get(i).set(2,oldAmount + amount);
                    return "customer_cart.xhtml";
                }
                        
            }
        }
        
        ArrayList<Object> product = new ArrayList<>(Arrays.asList(productID, productName, amount));
        shoppingCart.add(product);
        
        return "customer_cart.xhtml";
    }
    
    public String ConfirmOrder(List<List<Object>> shoppingCart) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try{
            //customer id'yi bul
            PreparedStatement sql = connection.prepareStatement("SELECT CUSTOMER_ID FROM CUSTOMERS WHERE NAME=? AND PASSWORD=?");
            sql.setString(1,name);
            sql.setString(2,password);
            ResultSet customerIDResult = sql.executeQuery();
            customerIDResult.next();
            
            //mevcut tarihi ve temin tarihini sql.Date tipine çevir
            java.sql.Date deadline = new java.sql.Date(someDate.getTime());
            long millis=System.currentTimeMillis();  
            java.sql.Date currentDate=new java.sql.Date(millis);  
            
            //orders tablosuna ekle
            PreparedStatement sql2 = connection.prepareStatement("INSERT INTO ORDERS" 
                    + " (CUSTOMER_ID,ORDER_DATE,DEADLINE,STATUS)values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            sql2.setInt(1, customerIDResult.getInt("CUSTOMER_ID"));
            sql2.setDate(2, currentDate);
            sql2.setDate(3, deadline);
            sql2.setString(4, "Waiting");
            sql2.execute();
            
            //order items tablosuna ekle
            
            ResultSet rs = sql2.getGeneratedKeys();
            if(rs.next());
            int lastOrderId = rs.getInt(1); //en son eklenen orderin id'sini al
            
            PreparedStatement sql3 = null;
            for (int i=0; i < shoppingCart.size(); i++)
            {

                    sql3 = connection.prepareStatement("INSERT INTO ORDERITEMS" 
                    + " (ORDER_ID,PRODUCT_ID,AMOUNT)values(?, ?, ?)");
                    sql3.setInt(1, lastOrderId);
                    sql3.setString(2, shoppingCart.get(i).get(0).toString());
                    sql3.setInt(3, (int)shoppingCart.get(i).get(2));
                    sql3.execute();
                
            }
            
            shoppingCart.clear();
            return "customer_orders.xhtml";
        }
        finally
        {
            connection.close();
        }
        
    }
    
    public ResultSet ListOrders() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        PreparedStatement sql2 = connection.prepareStatement("SELECT CUSTOMER_ID FROM CUSTOMERS WHERE NAME=? AND PASSWORD=?");
            sql2.setString(1,name);
            sql2.setString(2,password);
            ResultSet customerIDResult = sql2.executeQuery();
            customerIDResult.next();
            
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM ORDERS WHERE CUSTOMER_ID = ?");
            sql.setString(1, customerIDResult.getString("CUSTOMER_ID"));
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public ResultSet ListOrderItems(int orderID) throws SQLException{
        
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT PRODUCTS.PRODUCT_NAME, ORDERITEMS.AMOUNT "
                    + "FROM ORDERITEMS, PRODUCTS "
                    + "WHERE ORDERITEMS.ORDER_ID = ? AND ORDERITEMS.PRODUCT_ID = PRODUCTS.PRODUCT_ID ");
            sql.setInt(1, orderID);
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public String Login() throws SQLException{
        
        String name = getName();
        String password = getPassword();
        
        Connection connection = Factory.ConnectToDatabase();
        try{
            PreparedStatement sql = connection.prepareStatement("SELECT PASSWORD FROM CUSTOMERS WHERE NAME=?");
            sql.setString(1,name);
            ResultSet result = sql.executeQuery();

            while(result.next()) 
            {
                if(password.equals(result.getString("PASSWORD"))) 
                {
                    return "customer_products.xhtml";
                }
            }
                return "customer_login.xhtml";
        }
        finally
        {
            connection.close();
        }
        
    }
}
