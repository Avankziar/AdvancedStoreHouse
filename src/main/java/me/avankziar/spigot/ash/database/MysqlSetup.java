package main.java.me.avankziar.spigot.ash.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class MysqlSetup 
{
	private AdvancedStoreHouse plugin;
	private Connection conn = null;
	
	public MysqlSetup(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
		loadMysqlSetup();
	}
	
	public boolean loadMysqlSetup()
	{
		if(!connectToDatabase())
		{
			return false;
		}
		if(!setupDatabaseI())
		{
			return false;
		}
		if(!setupDatabaseII())
		{
			return false;
		}
		if(!setupDatabaseIII())
		{
			return false;
		}
		if(!setupDatabaseIV())
		{
			return false;
		}
		if(!setupDatabaseV())
		{
			return false;
		}
		/*if(!setupDatabaseVI())
		{
			return false;
		}*/
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		AdvancedStoreHouse.log.info("Connecting to the database...");
		try 
		{
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getYamlHandler().getConfig().getString("Mysql.User"));
            properties.setProperty("password", plugin.getYamlHandler().getConfig().getString("Mysql.Password"));
            properties.setProperty("autoReconnect", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true) + "");
            properties.setProperty("verifyServerCertificate", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false) + "");
            properties.setProperty("useSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            properties.setProperty("requireSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getYamlHandler().getConfig().getString("Mysql.Host") 
            		+ ":" + plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306) + "/" 
            		+ plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName"), properties);
           
          } catch (ClassNotFoundException e) 
		{
        	  AdvancedStoreHouse.log.severe("Could not locate drivers for mysql! Error: " + e.getMessage());
            return false;
          } catch (SQLException e) 
		{
        	  AdvancedStoreHouse.log.severe("Could not connect to mysql database! Error: " + e.getMessage());
            return false;
          }
		AdvancedStoreHouse.log.info("Database connection successful!");
		return true;
	}
	
	public boolean setupDatabaseI() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameI
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " player_uuid char(36) NOT NULL UNIQUE,"
		        		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
		        		+ " searchtype text);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public boolean setupDatabaseII() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameII
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " owner_uuid char(36) NOT NULL,"
		        		+ " memberlist mediumtext,"
		        		+ " creationdate bigint,"
		        		+ " chestname text,"
		        		+ " normalpriority boolean,"
		        		+ " prioritytype text,"
		        		+ " prioritynumber int,"
		        		+ " automaticdistribution boolean,"
		        		+ " random boolean,"
		        		+ " server text,"
		        		+ " world text,"
		        		+ " blockx int,"
		        		+ " blocky int,"
		        		+ " blockz int);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public boolean setupDatabaseIII() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameIII
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " distributionchestid int,"
		        		+ " owner_uuid text,"
		        		+ " creationdate bigint,"
		        		+ " priority int,"
		        		+ " content mediumtext,"
		        		+ " searchcontent mediumtext,"
		        		+ " endstorage boolean,"
		        		+ " server text,"
		        		+ " world text,"
		        		+ " blockx int,"
		        		+ " blocky int,"
		        		+ " blockz int,"
		        		+ " chestname text,"
		        		+ " optionvoid boolean,"
		        		+ " optiondurability boolean,"
		        		+ " durabilitytype text,"
		        		+ " durability int,"
		        		+ " optionrepair boolean,"
		        		+ " repairtype text,"
		        		+ " repaircost int,"
		        		+ " optionenchantments boolean,"
		        		+ " optionmaterial boolean);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	
	
	public boolean setupDatabaseIV() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameIV
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " itemfiltersetname text,"
		        		+ " owner_uuid text,"
		        		+ " content mediumtext);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public boolean setupDatabaseV() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameV
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " datum bigint,"
		        		+ " distributionchestid int,"
		        		+ " storagechestid int,"
		        		+ " distributedcontent mediumtext);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public boolean setupDatabaseVI() //TODO
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		        String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameVI
		        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		        		+ " channel_name TEXT NOT NULL,"
		        		+ " creator TEXT NOT NULL,"
		        		+ " vice MEDIUMTEXT,"
		        		+ " members MEDIUMTEXT,"
		        		+ " password TEXT,"
		        		+ " banned MEDIUMTEXT,"
		        		+ " symbolextra TEXT,"
		        		+ " namecolor TEXT,"
		        		+ " chatcolor TEXT);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) 
		      {
		        e.printStackTrace();
		        AdvancedStoreHouse.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally 
		      {
		    	  try 
		    	  {
		    		  if (query != null) 
		    		  {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public Connection getConnection() 
	{
		checkConnection();
		return conn;
	}
	
	public void checkConnection() 
	{
		try {
			if (conn == null) 
			{
				AdvancedStoreHouse.log.warning("Connection failed. Reconnecting...");
				reConnect();
			}
			if (!conn.isValid(3)) 
			{
				AdvancedStoreHouse.log.warning("Connection is idle or terminated. Reconnecting...");
				reConnect();
			}
			if (conn.isClosed() == true) 
			{
				AdvancedStoreHouse.log.warning("Connection is closed. Reconnecting...");
				reConnect();
			}
		} catch (Exception e) 
		{
			AdvancedStoreHouse.log.severe("Could not reconnect to Database! Error: " + e.getMessage());
		}
	}
	
	public boolean reConnect() 
	{
		try 
		{            
            long start = 0;
			long end = 0;
			
		    start = System.currentTimeMillis();
		    AdvancedStoreHouse.log.info("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getYamlHandler().getConfig().getString("Mysql.User"));
            properties.setProperty("password", plugin.getYamlHandler().getConfig().getString("Mysql.Password"));
            properties.setProperty("autoReconnect", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true) + "");
            properties.setProperty("verifyServerCertificate", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false) + "");
            properties.setProperty("useSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            properties.setProperty("requireSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getYamlHandler().getConfig().getString("Mysql.Host") 
            		+ ":" + plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306) + "/" 
            		+ plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName"), properties);
		    end = System.currentTimeMillis();
		    AdvancedStoreHouse.log.info("Connection to MySQL server established!");
		    AdvancedStoreHouse.log.info("Connection took " + ((end - start)) + "ms!");
            return true;
		} catch (Exception e) 
		{
			AdvancedStoreHouse.log.severe("Error re-connecting to the database! Error: " + e.getMessage());
			return false;
		}
	}
	
	public void closeConnection() 
	{
		try
		{
			AdvancedStoreHouse.log.info("Closing database connection...");
			conn.close();
			conn = null;
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
