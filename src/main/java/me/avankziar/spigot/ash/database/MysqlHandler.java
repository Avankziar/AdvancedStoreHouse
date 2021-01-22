package main.java.me.avankziar.spigot.ash.database;

import java.io.IOException;
import java.util.ArrayList;

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
		tableNameI = plugin.getYamlHandler().get().getString("Mysql.TableNameI");
		if(tableNameI == null)
		{
			return false;
		}
		tableNameII = plugin.getYamlHandler().get().getString("Mysql.TableNameII");
		if(tableNameII == null)
		{
			return false;
		}
		tableNameIII = plugin.getYamlHandler().get().getString("Mysql.TableNameIII");
		if(tableNameIII == null)
		{
			return false;
		}
		tableNameIV = plugin.getYamlHandler().get().getString("Mysql.TableNameIV");
		if(tableNameIV == null)
		{
			return false;
		}
		tableNameV = plugin.getYamlHandler().get().getString("Mysql.TableNameV");
		if(tableNameV == null)
		{
			return false;
		}
		tableNameVI = plugin.getYamlHandler().get().getString("Mysql.TableNameVI");
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
}
