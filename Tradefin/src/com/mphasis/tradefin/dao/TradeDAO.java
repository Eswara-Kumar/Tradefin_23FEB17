package com.mphasis.tradefin.dao;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class TradeDAO {
	private static Connection con = null;

	public static String ENV_VARIABLE_NAME = "CATALINA_HOME";
	public static String PROP_FILE_NAME = "db.chain.properties";
	
	public static Connection getConnection() {

		/*String dburl = "jdbc:mysql://172.21.80.234:3306/trade1";
		String dbUserName = "user1";
		String dbPassword = "User1234!";*/

		try {
			//--------------
            Properties prop = new Properties();        
    		//InputStream in = getServletContext().getResourceAsStream("WEB-INF/mysql.properties");
    		
    		String cat = System.getenv(ENV_VARIABLE_NAME);
    		String catalinaConf = cat+"/conf/";
    		System.out.println("##### CATALINA_HOME/conf/<prop_file> : " + catalinaConf + PROP_FILE_NAME);
    		
    		InputStream in = new FileInputStream(catalinaConf + PROP_FILE_NAME);
    		
            if (in == null){
            	System.out.println("##### Sorry, unable to find properties file");
            }
            
            prop.load(in);
            
            String drivers = prop.getProperty("db.drivers");
            String connectionURL = prop.getProperty("db.url");
            String databaseName = prop.getProperty("db.database");
            String username = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");
            String connectionURLWithDatabase = connectionURL + "/" + databaseName;
            System.out.println("##### connectionURLWithDatabase : " + connectionURLWithDatabase);
            
            /*String strChainIP = prop.getProperty("chain.ip");
            String strChainPort = prop.getProperty("chain.port");
            String strChainUsername = prop.getProperty("chain.userName");
            String strChainPassword = prop.getProperty("chain.password");
            //chain.url=http://multichainrpc:A9eETUUgh6b3JbVmX5pFkAgcVYZZkH7GpzA5ucjkFaN3@172.21.80.232:9568
            int iChainPort = Integer.parseInt(strChainPort);
            String ChainURL = "http://"+strChainUsername+":"+strChainPassword+"@"+strChainIP+":"+strChainPort;*/
            //--------------
            
			Class.forName(drivers);
			con = DriverManager.getConnection(connectionURLWithDatabase, username, password);
			System.out.println("***Database Connection Established***");
			in.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return con;
	}
}
