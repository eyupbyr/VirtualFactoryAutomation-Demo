
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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

@ManagedBean(name="Personnel")
public class Personnel {
    
    public Personnel(){

    }
    
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
    
    @ManagedProperty(value="#{Product}")
    private Product productBean;

    public Product getProductBean() {
        return productBean;
    }

    public void setProductBean(Product productBean) {
        this.productBean = productBean;
    }
    
    Connection connection;
    CachedRowSet rowSet=null;
    
    public ResultSet ListProducts() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM PRODUCTS");

            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );

        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public ResultSet ListSubProducts(String product_ID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM SUBPRODUCTTREE WHERE PRODUCT_ID = ?");
            sql.setString(1, product_ID);
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public void AddProduct() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "INSERT INTO PRODUCTS"
                    + "(PRODUCT_ID,PRODUCT_NAME,PRODUCT_TYPE,IS_SALEABLE)values(?, ?, ?, ?)");
            sql.setString(1, productBean.getProductId());
            sql.setString(2, productBean.getProductName());
            sql.setString(3, productBean.getProductType());
            sql.setBoolean(4, productBean.isSaleable());
            sql.execute();
        }
        finally
        {
            connection.close();
        }
    }
    
    public void AddSubProduct() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "INSERT INTO SUBPRODUCTTREE"
                    + "(SUB_PRODUCT_ID,PRODUCT_ID,AMOUNT)values(?, ?, ?)");
            sql.setString(1, productBean.getSubProductId());
            sql.setString(2, productBean.getProductId());
            sql.setInt(3, productBean.getAmount());
            sql.execute();
        }
        finally
        {
            connection.close();
        }
    }
    
    public void DeleteProduct(String product_ID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT IS_SALEABLE FROM "
                    + "PRODUCTS WHERE PRODUCT_ID = ?");
            sql.setString(1, product_ID);
            sql.execute();            
            ResultSet result = sql.executeQuery();
            result.next();
            if(result.getBoolean("IS_SALEABLE") == true)
            {
                PreparedStatement sql2 = connection.prepareStatement( "DELETE FROM SUBPRODUCTTREE "
                    + "WHERE PRODUCT_ID = ?");
                sql2.setString(1, product_ID);
                sql2.execute();    
            }
            PreparedStatement sql3 = connection.prepareStatement( "DELETE FROM PRODUCTS "
                    + "WHERE PRODUCT_ID = ?");
            sql3.setString(1, product_ID);
            sql3.execute();                   
        }
        finally
        {
            connection.close();
        }
    }
    
    public ResultSet ListOrders() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM ORDERS");
            
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
            PreparedStatement sql = connection.prepareStatement( "SELECT PRODUCTS.PRODUCT_NAME, ORDERITEMS.AMOUNT, OPERATIONS.OPERATION_NAME, OPERATIONS.OPERATION_ID " +
                    "FROM ORDERITEMS, OPERATIONS, PRODUCTS " +
                    "WHERE ORDERITEMS.PRODUCT_ID = PRODUCTS.PRODUCT_ID AND OPERATIONS.PRODUCT_TYPE = PRODUCTS.PRODUCT_TYPE ");
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public ResultSet ListOperations() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM OPERATIONS");
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public void AddOperation(int operationID, String operationName, String productType) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "INSERT INTO OPERATIONS"
                    + "(OPERATION_ID,OPERATION_NAME,PRODUCT_TYPE)values(?, ?, ?)");
            sql.setInt(1, operationID);
            sql.setString(2, operationName);
            sql.setString(3, productType);
            sql.execute();
        }
        finally
        {
            connection.close();
        }
    }
    
    public void DeleteOperation(int operationID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "DELETE FROM OPERATIONS "
                + "WHERE OPERATION_ID = ?");
            sql.setInt(1, operationID);
            sql.execute();                       
        }
        finally
        {
            connection.close();
        }
    }
    
    public ResultSet ListWorkCenters() throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT * FROM WORKCENTERS");
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public void AddWorkCenter(int workCenterID, String workCenterName, boolean active) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "INSERT INTO WORKCENTERS"
                    + "(WORK_CENTER_ID,WORK_CENTER_NAME,ACTIVE)values(?, ?, ?)");
            sql.setInt(1, workCenterID);
            sql.setString(2, workCenterName);
            sql.setBoolean(3, active);
            sql.execute();
        }
        finally
        {
            connection.close();
        }
    }    
    
    public void DeleteWorkCenter(int workCenterID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "DELETE FROM WORKCENTEROPERATION "
                + "WHERE WORK_CENTER_ID = ?");
            sql.setInt(1, workCenterID);
            sql.execute();
            
            PreparedStatement sql2 = connection.prepareStatement( "DELETE FROM WORKCENTERS "
                + "WHERE WORK_CENTER_ID = ?");
            sql2.setInt(1, workCenterID);
            sql2.execute();
            
        }
        finally
        {
            connection.close();
        }
    }
    
    public void ActivateWorkCenter(int workCenterID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "UPDATE WORKCENTERS SET ACTIVE = TRUE"
                + " WHERE WORK_CENTER_ID = ?");
            sql.setInt(1, workCenterID);
            sql.execute();                       
        }
        finally
        {
            connection.close();
        }
    }
    
    public void DeactivateWorkCenter(int workCenterID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "UPDATE WORKCENTERS SET ACTIVE = FALSE"
                + " WHERE WORK_CENTER_ID = ?");
            sql.setInt(1, workCenterID);
            sql.execute();                       
        }
        finally
        {
            connection.close();
        }
    }
    
    public ResultSet ListWorkCenterOperations(int workCenterID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "SELECT OPERATIONS.OPERATION_ID, OPERATIONS.OPERATION_NAME, WORKCENTEROPERATION.SPEED "
                    + "FROM WORKCENTERS, WORKCENTEROPERATION, OPERATIONS "
                    + "WHERE WORKCENTERS.WORK_CENTER_ID = WORKCENTEROPERATION.WORK_CENTER_ID AND OPERATIONS.OPERATION_ID = WORKCENTEROPERATION.OPERATION_ID "
                    + "AND WORKCENTERS.WORK_CENTER_ID = ?");
            
            sql.setInt(1, workCenterID);
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( sql.executeQuery() );
        }
        finally
        {
            connection.close();
        }
        return rowSet;
    }
    
    public void DefineOperationtoWorkCenter(int workCenterID, int operationID, int speed) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "INSERT INTO WORKCENTEROPERATION"
                    + "(WORK_CENTER_ID,OPERATION_ID,SPEED)values(?, ?, ?)");
            sql.setInt(1, workCenterID);
            sql.setInt(2, operationID);
            sql.setInt(3, speed);
            sql.execute();
        }
        finally
        {
            connection.close();
        }
    }
    
    public void StartProduction(int orderID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "UPDATE ORDERS SET STATUS = 'In Production'"
                + " WHERE ORDER_ID = ?");
            sql.setInt(1, orderID);
            sql.execute();                       
        }
        finally
        {
            connection.close();
        }
    }
    
    public void StopProduction(int orderID) throws SQLException
    {
        connection = Factory.ConnectToDatabase();
        try
        {
            PreparedStatement sql = connection.prepareStatement( "UPDATE ORDERS SET STATUS = 'Waiting'"
                + " WHERE ORDER_ID = ?");
            sql.setInt(1, orderID);
            sql.execute();                       
        }
        finally
        {
            connection.close();
        }
    }
    
    public String Login() throws SQLException{
        
        String name = getName();
        String password = getPassword();
        
        Connection connection = Factory.ConnectToDatabase();
        try{
            PreparedStatement sql = connection.prepareStatement("SELECT PASSWORD FROM USERS WHERE NAME=?");
            sql.setString(1,name);
            ResultSet result = sql.executeQuery();

            while(result.next()) 
            {
                if(password.equals(result.getString("PASSWORD"))) 
                {
                    return "personnel_dashboard.xhtml";
                }
            }
                return "personnel_login.xhtml";
        }
        finally
        {
            connection.close();
        }
    }
}
