package main.java.me.avankziar.spigot.ash.database.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public interface TableII
{
	default boolean existII(AdvancedStoreHouse plugin, String whereColumn, Object... object) 
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameII
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
	
	default boolean createII(AdvancedStoreHouse plugin, Object object) 
	{
		if(!(object instanceof DistributionChest))
		{
			return false;
		}
		DistributionChest cu = (DistributionChest) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) {
			try 
			{
				String sql = "INSERT INTO `" + plugin.getMysqlHandler().tableNameII 
						+ "`(`owner_uuid`, `memberlist`, `creationdate`, `chestname`, `normalpriority`,"
						+ " `prioritytype`, `prioritynumber`, `automaticdistribution`, `random`, `server`, `world`, `blockx`, `blocky`, `blockz`) " 
						+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				preparedStatement = conn.prepareStatement(sql);
		        preparedStatement.setString(1, cu.getOwneruuid());
		        preparedStatement.setString(2, String.join(";", cu.getMemberList()));
		        preparedStatement.setLong(3, cu.getCreationDate());
		        preparedStatement.setString(4, cu.getChestName());
		        preparedStatement.setBoolean(5, cu.isNormalPriority());
		        preparedStatement.setString(6, cu.getPriorityType().toString());
		        preparedStatement.setInt(7, cu.getPriorityNumber());
		        preparedStatement.setBoolean(8, cu.isAutomaticDistribution());
		        preparedStatement.setBoolean(9, cu.isDistributeRandom());
		        preparedStatement.setString(10, cu.getServer());
		        preparedStatement.setString(11, cu.getWorld());
		        preparedStatement.setInt(12, cu.getBlockX());
		        preparedStatement.setInt(13, cu.getBlockY());
		        preparedStatement.setInt(14, cu.getBlockZ());
		        
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
	
	default boolean updateDataII(AdvancedStoreHouse plugin, Object object, String whereColumn, Object... whereObject) 
	{
		if(!(object instanceof DistributionChest))
		{
			return false;
		}
		if(whereObject == null)
		{
			return false;
		}
		DistributionChest cu = (DistributionChest) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + plugin.getMysqlHandler().tableNameII
						+ "` SET `owner_uuid` = ?, `memberlist` = ?, `creationdate` = ?, `chestname` = ?, `normalpriority` = ?," 
						+ " `prioritytype` = ?, `prioritynumber` = ?, `automaticdistribution` = ?, `random` = ?,"
						+ " `server` = ?, `world` = ?, `blockx` = ?, `blocky` = ?, `blockz` = ?" 
						+ " WHERE "+whereColumn;
				preparedStatement = conn.prepareStatement(data);
				preparedStatement.setString(1, cu.getOwneruuid());
		        preparedStatement.setString(2, String.join(";", cu.getMemberList()));
		        preparedStatement.setLong(3, cu.getCreationDate());
		        preparedStatement.setString(4, cu.getChestName());
		        preparedStatement.setBoolean(5, cu.isNormalPriority());
		        preparedStatement.setString(6, cu.getPriorityType().toString());
		        preparedStatement.setInt(7, cu.getPriorityNumber());
		        preparedStatement.setBoolean(8, cu.isAutomaticDistribution());
		        preparedStatement.setBoolean(9, cu.isDistributeRandom());
		        preparedStatement.setString(10, cu.getServer());
		        preparedStatement.setString(11, cu.getWorld());
		        preparedStatement.setInt(12, cu.getBlockX());
		        preparedStatement.setInt(13, cu.getBlockY());
		        preparedStatement.setInt(14, cu.getBlockZ());
		        
		        int i = 15;
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
	
	default Object getDataII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII 
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
		        	return new DistributionChest(
		        			result.getInt("id"),
		        			result.getString("owner_uuid"),
		        			Arrays.asList(result.getString("memberlist").split(";")),
		        			result.getLong("creationdate"),
		        			result.getString("chestname"),
		        			result.getBoolean("normalpriority"),
		        			PriorityType.valueOf(result.getString("prioritytype")),
		        			result.getInt("prioritynumber"),
		        			result.getBoolean("automaticdistribution"),
		        			result.getBoolean("random"),
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
	
	default boolean deleteDataII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		try 
		{
			String sql = "DELETE FROM `" + plugin.getMysqlHandler().tableNameII + "` WHERE "+whereColumn;
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
	
	default int lastIDII(AdvancedStoreHouse plugin)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameII + "` ORDER BY `id` DESC LIMIT 1";
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
	
	default int countWhereIDII(AdvancedStoreHouse plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameII
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
	
	default ArrayList<DistributionChest> getListII(AdvancedStoreHouse plugin, String orderByColumn,
			boolean desc, int start, int end, String whereColumn, Object...whereObject)
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
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII
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
		        ArrayList<DistributionChest> list = new ArrayList<DistributionChest>();
		        while (result.next()) 
		        {
		        	DistributionChest ep = new DistributionChest(
		        			result.getInt("id"),
		        			result.getString("owner_uuid"),
		        			Arrays.asList(result.getString("memberlist").split(";")),
		        			result.getLong("creationdate"),
		        			result.getString("chestname"),
		        			result.getBoolean("normalpriority"),
		        			PriorityType.valueOf(result.getString("prioritytype")),
		        			result.getInt("prioritynumber"),
		        			result.getBoolean("automaticdistribution"),
		        			result.getBoolean("random"),
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
	
	default ArrayList<DistributionChest> getTopII(AdvancedStoreHouse plugin, String orderByColumn, int start, int end)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII 
						+ "` ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
		        preparedStatement = conn.prepareStatement(sql);
		        
		        result = preparedStatement.executeQuery();
		        ArrayList<DistributionChest> list = new ArrayList<DistributionChest>();
		        while (result.next()) 
		        {
		        	DistributionChest ep = new DistributionChest(
		        			result.getInt("id"),
		        			result.getString("owner_uuid"),
		        			Arrays.asList(result.getString("memberlist").split(";")),
		        			result.getLong("creationdate"),
		        			result.getString("chestname"),
		        			result.getBoolean("normalpriority"),
		        			PriorityType.valueOf(result.getString("prioritytype")),
		        			result.getInt("prioritynumber"),
		        			result.getBoolean("automaticdistribution"),
		        			result.getBoolean("random"),
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
	
	default ArrayList<DistributionChest> getAllListAtII(AdvancedStoreHouse plugin, String orderByColumn,
			boolean desc, String whereColumn, Object...whereObject)
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
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC";
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameII
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
		        ArrayList<DistributionChest> list = new ArrayList<DistributionChest>();
		        while (result.next()) 
		        {
		        	DistributionChest ep = new DistributionChest(
		        			result.getInt("id"),
		        			result.getString("owner_uuid"),
		        			Arrays.asList(result.getString("memberlist").split(";")),
		        			result.getLong("creationdate"),
		        			result.getString("chestname"),
		        			result.getBoolean("normalpriority"),
		        			PriorityType.valueOf(result.getString("prioritytype")),
		        			result.getInt("prioritynumber"),
		        			result.getBoolean("automaticdistribution"),
		        			result.getBoolean("random"),
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