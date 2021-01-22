package main.java.me.avankziar.spigot.ash.database.tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public interface TableIII
{
	default boolean existIII(AdvancedStoreHouse plugin, String whereColumn, Object... object) 
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameIII
						+ "` WHERE "+whereColumn+" LIMIT 1";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : object)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return true;
		        }
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	default boolean createIII(AdvancedStoreHouse plugin, Object object) 
	{
		if(!(object instanceof StorageChest))
		{
			return false;
		}
		StorageChest cu = (StorageChest) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) {
			try 
			{
				String sql = "INSERT INTO `" + plugin.getMysqlHandler().tableNameIII 
						+ "`(`distributionchestid`, `owner_uuid`, `creationdate`, `priority`, `content`,"
						+ " `endstorage`, `server`, `world`, `blockx`, `blocky`, `blockz`) " 
						+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, cu.getDistributionChestID());
		        preparedStatement.setString(2, cu.getOwneruuid());
		        preparedStatement.setLong(3, cu.getCreationDate());
		        preparedStatement.setInt(4, cu.getPriority());
		        preparedStatement.setString(5, ConvertHandler.ToBase64itemStackArray(cu.getContents()));
		        preparedStatement.setBoolean(6, cu.isEndstorage());
		        preparedStatement.setString(7, cu.getServer());
		        preparedStatement.setString(8, cu.getWorld());
		        preparedStatement.setInt(9, cu.getBlockX());
		        preparedStatement.setInt(10, cu.getBlockY());
		        preparedStatement.setInt(11, cu.getBlockZ());
		        
		        preparedStatement.executeUpdate();
		        return true;
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	default boolean updateDataIII(AdvancedStoreHouse plugin, Object object, String whereColumn, Object... whereObject) 
	{
		if(!(object instanceof StorageChest))
		{
			return false;
		}
		if(whereObject == null)
		{
			return false;
		}
		StorageChest cu = (StorageChest) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + plugin.getMysqlHandler().tableNameIII
						+ "` SET `distributionchestid` = ?, `owner_uuid` = ?, `creationdate` = ?, `priority` = ?, `content` = ?," 
						+ " `endstorage` = ?, `server` = ?, `world` = ?, `blockx` = ?, `blocky` = ?, `blockz` = ?" 
						+ " WHERE "+whereColumn;
				preparedStatement = conn.prepareStatement(data);
				preparedStatement.setInt(1, cu.getDistributionChestID());
		        preparedStatement.setString(2, cu.getOwneruuid());
		        preparedStatement.setLong(3, cu.getCreationDate());
		        preparedStatement.setInt(4, cu.getPriority());
		        preparedStatement.setString(5, ConvertHandler.ToBase64itemStackArray(cu.getContents()));
		        preparedStatement.setBoolean(6, cu.isEndstorage());
		        preparedStatement.setString(7, cu.getServer());
		        preparedStatement.setString(8, cu.getWorld());
		        preparedStatement.setInt(9, cu.getBlockX());
		        preparedStatement.setInt(10, cu.getBlockY());
		        preparedStatement.setInt(11, cu.getBlockZ());
		        
		        int i = 12;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
				
				preparedStatement.executeUpdate();
				return true;
			} catch (SQLException e) {
				AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (preparedStatement != null) 
					{
						preparedStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        return false;
	}
	
	default Object getDataIII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject) throws IOException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII 
						+ "` WHERE "+whereColumn+" LIMIT 1";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return new StorageChest(result.getInt("id"),
		        			result.getInt("distributionchestid"),
		        			result.getString("owner_uuid"),
		        			result.getInt("priority"),
		        			result.getLong("creationdate"),
		        			ConvertHandler.FromBase64itemStackArray(result.getString("content")),
		        			result.getBoolean("endstorage"),
		        			result.getString("server"),
		        			result.getString("world"),
		        			result.getInt("blockx"),
		        			result.getInt("blocky"),
		        			result.getInt("blockz"));
		        }
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return null;
	}
	
	default boolean deleteDataIII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		try 
		{
			String sql = "DELETE FROM `" + plugin.getMysqlHandler().tableNameIII + "` WHERE "+whereColumn;
			preparedStatement = conn.prepareStatement(sql);
			int i = 1;
	        for(Object o : whereObject)
	        {
	        	preparedStatement.setObject(i, o);
	        	i++;
	        }
			preparedStatement.execute();
			return true;
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally 
		{
			try {
				if (preparedStatement != null) 
				{
					preparedStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	default int lastIDIII(AdvancedStoreHouse plugin)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameIII + "` ORDER BY `id` DESC LIMIT 1";
		        preparedStatement = conn.prepareStatement(sql);
		        
		        result = preparedStatement.executeQuery();
		        while(result.next())
		        {
		        	return result.getInt("id");
		        }
		    } catch (SQLException e) 
			{
		    	e.printStackTrace();
		    	return 0;
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	default int countWhereIDIII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameIII
						+ "` WHERE "+whereColumn
						+ " ORDER BY `id` DESC";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        result = preparedStatement.executeQuery();
		        int count = 0;
		        while(result.next())
		        {
		        	count++;
		        }
		        return count;
		    } catch (SQLException e) 
			{
		    	e.printStackTrace();
		    	return 0;
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	default ArrayList<StorageChest> getListIII(AdvancedStoreHouse plugin, String orderByColumn, boolean desc,
			int start, int end, String whereColumn, Object...whereObject) throws IOException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "";
				if(desc)
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" ASC LIMIT "+start+", "+end;
				}
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        result = preparedStatement.executeQuery();
		        ArrayList<StorageChest> list = new ArrayList<StorageChest>();
		        while (result.next()) 
		        {
		        	StorageChest ep = new StorageChest(result.getInt("id"),
		        			result.getInt("distributionchestid"),
		        			result.getString("owner_uuid"),
		        			result.getInt("priority"),
		        			result.getLong("creationdate"),
		        			ConvertHandler.FromBase64itemStackArray(result.getString("content")),
		        			result.getBoolean("endstorage"),
		        			result.getString("server"),
		        			result.getString("world"),
		        			result.getInt("blockx"),
		        			result.getInt("blocky"),
		        			result.getInt("blockz"));
		        	list.add(ep);
		        }
		        return list;
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return null;
	}
	
	default ArrayList<StorageChest> getTopIII(AdvancedStoreHouse plugin, String orderByColumn, int start, int end) throws IOException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII 
						+ "` ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
		        preparedStatement = conn.prepareStatement(sql);
		        
		        result = preparedStatement.executeQuery();
		        ArrayList<StorageChest> list = new ArrayList<StorageChest>();
		        while (result.next()) 
		        {
		        	StorageChest ep = new StorageChest(result.getInt("id"),
		        			result.getInt("distributionchestid"),
		        			result.getString("owner_uuid"),
		        			result.getInt("priority"),
		        			result.getLong("creationdate"),
		        			ConvertHandler.FromBase64itemStackArray(result.getString("content")),
		        			result.getBoolean("endstorage"),
		        			result.getString("server"),
		        			result.getString("world"),
		        			result.getInt("blockx"),
		        			result.getInt("blocky"),
		        			result.getInt("blockz"));
		        	list.add(ep);
		        }
		        return list;
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return null;
	}
	
	default ArrayList<StorageChest> getAllListAtIII(AdvancedStoreHouse plugin, String orderByColumn, boolean desc,
			String whereColumn, Object...whereObject) throws IOException
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "";
				if(desc)
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC";
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameIII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" ASC";
				}
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        result = preparedStatement.executeQuery();
		        ArrayList<StorageChest> list = new ArrayList<StorageChest>();
		        while (result.next()) 
		        {
		        	StorageChest ep = new StorageChest(result.getInt("id"),
		        			result.getInt("distributionchestid"),
		        			result.getString("owner_uuid"),
		        			result.getInt("priority"),
		        			result.getLong("creationdate"),
		        			ConvertHandler.FromBase64itemStackArray(result.getString("content")),
		        			result.getBoolean("endstorage"),
		        			result.getString("server"),
		        			result.getString("world"),
		        			result.getInt("blockx"),
		        			result.getInt("blocky"),
		        			result.getInt("blockz"));
		        	list.add(ep);
		        }
		        return list;
		    } catch (SQLException e) 
			{
				  AdvancedStoreHouse.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return null;
	}
}