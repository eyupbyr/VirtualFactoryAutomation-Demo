
import java.sql.Connection;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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

@ManagedBean(name="Factory")
public class Factory {
    
    public Factory(){
        
    }
    
    private static DataSource dataSource;
    private static Connection connection;
    
    public static Connection ConnectToDatabase() throws SQLException{
        
        try{
           Context ctx = new InitialContext();
           dataSource = (DataSource) ctx.lookup("jdbc/factory");
        }catch(Exception e) {
           e.printStackTrace();
        }
        
        if ( dataSource == null )
        throw new SQLException( "Unable to obtain DataSource" );
       
        connection = dataSource.getConnection();
       
        if ( connection == null )
        throw new SQLException( "Unable to connect to DataSource" );
        
        return connection;
    }
}
