package main.java.me.avankziar.spigot.ash.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.tables.TableI;
import main.java.me.avankziar.spigot.ash.database.tables.TableII;
import main.java.me.avankziar.spigot.ash.database.tables.TableIII;
import main.java.me.avankziar.spigot.ash.database.tables.TableIV;
import main.java.me.avankziar.spigot.ash.database.tables.TableV;

public class MysqlHandler implements TableI, TableII, TableIII, TableIV, TableV//, TableVI
{
	public enum Type
	{
		PLUGINUSER, DISTRIBUTIONCHEST, STORAGECHEST, ITEMFILTERSET, TRANSFERLOG, //CROSSSERVERTRANSFER
		;
	}
	
	private AdvancedStoreHouse plugin;
	public String tableNameI; //PluginUser
	public String tableNameII; //Verteilerkiste
	public String tableNameIII; //Lagerkiste
	public String tableNameIV; //ItemFilterSet
	public String tableNameV; //TransferLog
	public String tableNameVI; //CrossServerTransfer
	
	public MysqlHandler(AdvancedStoreHouse plugin) 
	{
		this.plugin = plugin;
		loadMysqlHandler();
	}
	
	public boolean loadMysqlHandler()
	{
		tableNameI = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameI");
		if(tableNameI == null)
		{
			return false;
		}
		tableNameII = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameII");
		if(tableNameII == null)
		{
			return false;
		}
		tableNameIII = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameIII");
		if(tableNameIII == null)
		{
			return false;
		}
		tableNameIV = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameIV");
		if(tableNameIV == null)
		{
			return false;
		}
		tableNameV = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameV");
		if(tableNameV == null)
		{
			return false;
		}
		tableNameVI = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameVI");
		if(tableNameVI == null)
		{
			return false;
		}
		return true;
	}
	
	public boolean exist(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.existI(plugin, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.existII(plugin, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.existIII(plugin, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.existIV(plugin, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.existV(plugin, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.existVI(plugin, whereColumn, whereObject);
		}
		return false;
	}
	
	public boolean create(Type type, Object object)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.createI(plugin, object);
		case DISTRIBUTIONCHEST:
			return TableII.super.createII(plugin, object);
		case STORAGECHEST:
			return TableIII.super.createIII(plugin, object);
		case ITEMFILTERSET:
			return TableIV.super.createIV(plugin, object);
		case TRANSFERLOG:
			return TableV.super.createV(plugin, object);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.createVI(plugin, object);
		}
		return false;
	}
	
	public boolean updateData(Type type, Object object, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.updateDataI(plugin, object, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.updateDataII(plugin, object, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.updateDataIII(plugin, object, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.updateDataIV(plugin, object, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.updateDataV(plugin, object, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.updateDataVI(plugin, object, whereColumn, whereObject);
		}
		return false;
	}
	
	public Object getData(Type type, String whereColumn, Object... whereObject) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getDataI(plugin, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.getDataII(plugin, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.getDataIII(plugin, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.getDataIV(plugin, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.getDataV(plugin, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.getDataVI(plugin, whereColumn, whereObject);
		}
		return null;
	}
	
	public boolean deleteData(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.deleteDataI(plugin, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.deleteDataII(plugin, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.deleteDataIII(plugin, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.deleteDataIV(plugin, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.deleteDataV(plugin, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.deleteDataVI(plugin, whereColumn, whereObject);
		}
		return false;
	}
	
	public int lastID(Type type)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.lastIDI(plugin);
		case DISTRIBUTIONCHEST:
			return TableII.super.lastIDII(plugin);
		case STORAGECHEST:
			return TableIII.super.lastIDIII(plugin);
		case ITEMFILTERSET:
			return TableIV.super.lastIDIV(plugin);
		case TRANSFERLOG:
			return TableV.super.lastIDV(plugin);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.lastIDVI(plugin);
		}
		return 0;
	}
	
	public int countWhereID(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.countWhereIDI(plugin, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.countWhereIDII(plugin, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.countWhereIDIII(plugin, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.countWhereIDIV(plugin, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.countWhereIDV(plugin, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.countWhereIDVI(plugin, whereColumn, whereObject);
		}
		return 0;
	}
	
	public int getCount(Type type, String orderByColumn, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getCountI(plugin, orderByColumn, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.getCountII(plugin, orderByColumn, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.getCountIII(plugin, orderByColumn, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.getCountIV(plugin, orderByColumn, whereColumn, whereObject);
		case TRANSFERLOG:
			return 0;
		}
		return 0;
	}
	
	public ArrayList<?> getList(Type type, String orderByColumn,
			boolean desc, int start, int quantity, String whereColumn, Object...whereObject) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getListI(plugin, orderByColumn, start, quantity, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.getListII(plugin, orderByColumn, desc, start, quantity, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.getListIII(plugin, orderByColumn, desc, start, quantity, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.getListIV(plugin, orderByColumn, start, quantity, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.getListV(plugin, orderByColumn, start, quantity, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.getListVI(plugin, orderByColumn, start, quantity, whereColumn, whereObject);
		}
		return null;
	}
	
	public ArrayList<?> getTop(Type type, String orderByColumn, boolean desc, int start, int end) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getTopI(plugin, orderByColumn, start, end);
		case DISTRIBUTIONCHEST:
			return TableII.super.getTopII(plugin, orderByColumn, start, end);
		case STORAGECHEST:
			return TableIII.super.getTopIII(plugin, orderByColumn, start, end);
		case ITEMFILTERSET:
			return TableIV.super.getTopIV(plugin, orderByColumn, start, end);
		case TRANSFERLOG:
			return TableV.super.getTopV(plugin, orderByColumn, start, end);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.getTopVI(plugin, orderByColumn, start, end);
		}
		return null;
	}
	
	public ArrayList<?> getAllListAt(Type type, String orderByColumn,
			boolean desc, String whereColumn, Object...whereObject) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getAllListAtI(plugin, orderByColumn, whereColumn, whereObject);
		case DISTRIBUTIONCHEST:
			return TableII.super.getAllListAtII(plugin, orderByColumn, desc, whereColumn, whereObject);
		case STORAGECHEST:
			return TableIII.super.getAllListAtIII(plugin, orderByColumn, desc, whereColumn, whereObject);
		case ITEMFILTERSET:
			return TableIV.super.getAllListAtIV(plugin, orderByColumn, whereColumn, whereObject);
		case TRANSFERLOG:
			return TableV.super.getAllListAtV(plugin, orderByColumn, whereColumn, whereObject);
		//case CROSSSERVERTRANSFER:
			//return TableVI.super.getAllListAtVI(plugin, orderByColumn, whereColumn, whereObject);
		}
		return null;
	}
	
	public void startConvert(String server, final Player player, int lastid)
	{
		new BukkitRunnable()
		{
			int start = 0;
			final int amount = 15;
			@Override
			public void run()
			{
				if(start >= lastid)
				{
					cancel();
					int lastidII = countWhereID(Type.STORAGECHEST, "`server` = ?", server);
					startConvertPartII(server, player, lastidII);
					return;
				}
				convertII(server, 0, amount);
				start += amount;
			}
		}.runTaskTimer(plugin, 0L, 5L);
	}
	
	private void convertII(String server, int start, int amount)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id`,`world`,`blockx`,`blocky`,`blockz` FROM `" + tableNameII + "` WHERE `server` = ? ORDER BY `id` ASC LIMIT "+start+", "+amount;
		        
				preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, server);
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	int id = result.getInt("id");
		        	String world = result.getString("world");
		        	int x = result.getInt("blockx");
		        	int y = result.getInt("blocky");
		        	int z = result.getInt("blockz");
		        	Block b = new Location(Bukkit.getWorld(world), x, y, z).getBlock();
		        	if(plugin.getUtility().isNOTStoragechest(b.getState()))
		        	{
		        		deleteData(Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		        		deleteData(Type.STORAGECHEST, "`distributionchestid` = ?", id);
		        	}
		        }
		    } catch (SQLException e) 
			{
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return;
	}
	
	private void startConvertPartII(String server, final Player player, int lastid)
	{
		new BukkitRunnable()
		{
			int start = 0;
			final int amount = 15;
			@Override
			public void run()
			{
				if(start >= lastid)
				{
					cancel();
					if(player != null)
					{
						player.sendMessage(ChatApi.tl("&6Convert finish!"));
					}
					return;
				}
				convertIIPartII(server, 0, amount);
				start += amount;
			}
		}.runTaskTimer(plugin, 0L, 5L);
	}
	
	private void convertIIPartII(String server, int start, int amount)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id`,`world`,`blockx`,`blocky`,`blockz` FROM `" 
						+ tableNameIII + "` WHERE `server` = ? ORDER BY `id` ASC LIMIT "+start+", "+amount;
		        
				preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, server);
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	int id = result.getInt("id");
		        	String world = result.getString("world");
		        	int x = result.getInt("blockx");
		        	int y = result.getInt("blocky");
		        	int z = result.getInt("blockz");
		        	Block b = new Location(Bukkit.getWorld(world), x, y, z).getBlock();
		        	if(plugin.getUtility().isNOTStoragechest(b.getState()))
		        	{
		        		deleteData(Type.STORAGECHEST, "`id` = ?", id);
		        	}
		        }
		    } catch (SQLException e) 
			{
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return;
	}
	
	public void checkUnboundChest(int start, int amount)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" 
						+ tableNameII + "` ORDER BY `id` ASC LIMIT "+start+", "+amount;
		        
				preparedUpdateStatement = conn.prepareStatement(sql);
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	int id = result.getInt("id");
		        	if(!exist(Type.STORAGECHEST, "`distributionchestid` = ?", id))
		        	{
		        		deleteData(Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		        	}
		        }
		    } catch (SQLException e) 
			{
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return;
	}
	public void checkUnboundChestII(int start, int amount)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id`,`distributionchestid` FROM `" 
						+ tableNameIII + "` ORDER BY `id` ASC LIMIT "+start+", "+amount;
		        
				preparedUpdateStatement = conn.prepareStatement(sql);
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	int dcid = result.getInt("distributionchestid");
		        	if(!exist(Type.DISTRIBUTIONCHEST, "`id` = ?", dcid))
		        	{
		        		deleteData(Type.DISTRIBUTIONCHEST, "`distributionchestid` = ?", dcid);
		        	}
		        }
		    } catch (SQLException e) 
			{
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return;
	}
}
