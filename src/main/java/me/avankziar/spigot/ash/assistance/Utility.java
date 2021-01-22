package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utility 
{
	private static AdvancedStoreHouse plugin;
	public static LinkedHashMap<String, String> item = new LinkedHashMap<>(); //Playeruuid, ItemJason
	public static LinkedHashMap<String, String> itemname = new LinkedHashMap<>(); //Playeruuid, Itemname
	
	private String prefix;
	
	public static String 
	PERMCOUNTDISTRIBUTIONCHEST = "",
	PERMCOUNTSTORAGECHEST = "",
	PERMBYPASSDELETE = "",
	PERMBYPASSITEMFILTERSET = "",
	PERMCOUNTITEMFILTERSET = "",
	PERMBYPASSITEMFILTERSETSELECT = "",
	PERMBYPASSITEMFILTERSETUPDATE = "",
	PERMBYPASSITEMFILTERSETLIST = "",
	PERMBYPASSINFO = "",
	PERMBYPASSSELECT = "",
	PERMBYPASSPLAYERINFO = "",
	PERMBYPASSLIST = "",
	PERMBYPASSSEARCH = "",
	PERMBYPASSTRANSFER = "",
	PERMBYPASSRANDOM = "";
	
	public Utility(AdvancedStoreHouse plugin)
	{
		Utility.plugin = plugin;
		loadUtility();
		setPermissions();
	}
	
	public boolean loadUtility()
	{
		setPrefix(plugin.getYamlHandler().get().getString("Prefix", "&7[&eASH&7] &r"));
		return true;
	}
	
	public void setPermissions()
	{
		PERMCOUNTDISTRIBUTIONCHEST = plugin.getYamlHandler().getCom().getString("Custom.DistributionChest", "ash.count.distributionchest.");
		PERMCOUNTSTORAGECHEST = plugin.getYamlHandler().getCom().getString("Custom.StorageChest", "ash.count.storagechest.");
		PERMBYPASSDELETE = plugin.getYamlHandler().getCom().getString("Bypass.Delete", "ash.bypass.delete");
		PERMBYPASSITEMFILTERSET = plugin.getYamlHandler().getCom().getString("Bypass.ItemFilterSet", "ash.bypass.itemfilterset");
		PERMCOUNTITEMFILTERSET = plugin.getYamlHandler().getCom().getString("Custom.ItemFilterSet", "ash.count.itemfilterset");
		PERMBYPASSITEMFILTERSETLIST = plugin.getYamlHandler().getCom().getString("Bypass.ItemFilterSetList", "ash.bypass.itemfiltersetlist");
		PERMBYPASSITEMFILTERSETSELECT = plugin.getYamlHandler().getCom().getString("Bypass.ItemFilterSetSelect",
				"ash.bypass.itemfiltersetselect");
		PERMBYPASSITEMFILTERSETUPDATE = plugin.getYamlHandler().getCom().getString("Bypass.ItemFilterSetUpdate",
				"ash.bypass.itemfiltersetupdate");
		PERMBYPASSINFO = plugin.getYamlHandler().getCom().getString("Bypass.Info", "ash.bypass.info");
		PERMBYPASSSELECT = plugin.getYamlHandler().getCom().getString("Bypass.Select", "ash.bypass.select");
		PERMBYPASSPLAYERINFO = plugin.getYamlHandler().getCom().getString("Bypass.PlayerInfo", "ash.bypass.playerinfo");
		PERMBYPASSLIST = plugin.getYamlHandler().getCom().getString("Bypass.List", "ash.bypass.list");
		PERMBYPASSSEARCH = plugin.getYamlHandler().getCom().getString("Bypass.Search", "ash.bypass.search");
		PERMBYPASSTRANSFER = plugin.getYamlHandler().getCom().getString("Bypass.Transfer", "ash.bypass.transfer");
		PERMBYPASSRANDOM = plugin.getYamlHandler().getCom().getString("Bypass.Random", "ash.bypass.random");
	}
	
	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
	
	/*
	 * itemStringFromReflection see {@link RefectionUtil}
	 */
	@SuppressWarnings("deprecation")
	public TextComponent apiChatItem(@Nonnull String text, @Nullable ClickEvent.Action caction, @Nullable String cmd,
			@Nonnull String itemStringFromReflection)
	{
		TextComponent msg = ChatApi.tctl(text);
		if(caction != null && cmd != null)
		{
			msg.setClickEvent( new ClickEvent(caction, cmd));
		}
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, 
				new BaseComponent[]{new TextComponent(itemStringFromReflection)}));
		return msg;
	}
	
	public  String getDate(String format)
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dt = sdf.format(now);
		return dt;
	}
	
	public boolean existMethod(Class<?> externclass, String method)
	{
	    try 
	    {
	    	Method[] mtds = externclass.getMethods();
	    	for(Method methods : mtds)
	    	{
	    		if(methods.getName().equalsIgnoreCase(method))
	    		{
	    	    	//AdvancedStoreHouse.log.info("Method "+method+" in Class "+externclass.getName()+" loaded!");
	    	    	return true;
	    		}
	    	}
	    	return false;
	    } catch (Exception e) 
	    {
	    	return false;
	    }
	}
	
	/*public void isAfk(Player player, Player targed)
	{
		if(plugin.getAfkRecord() != null)
		{
			if(existMethod(plugin.getAfkRecord().getClass(), "isAfk"))
			{
				if(plugin.getAfkRecord().isAfk(targed))
				{
					///Der Spieler ist afk!
					player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getL().getString("AfkRecord.IsAfk")));	
				}
			}
		}
	}*/
	
	public static boolean containsIgnoreCase(String message, String searchStr)     
	{
	    if(message == null || searchStr == null) return false;

	    final int length = searchStr.length();
	    if (length == 0)
	        return true;

	    for (int i = message.length() - length; i >= 0; i--) 
	    {
	        if (message.regionMatches(true, i, searchStr, 0, length))
	        {
	        	return true;
	        }
	    }
	    return false;
	}
	
	public String convertItemStackToJson(ItemStack itemStack) //FIN
	{
		/*
		 * so baut man das manuell
		 * ItemStack is = createHoverItem(p, bookpath+".hover.item."+ar[2], bok);
		 * emptyword.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,new BaseComponent[]{new TextComponent(convertItemStackToJson((is)))}));
		 */
	    // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
	    Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
	    Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

	    // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
	    Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
	    Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
	    Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

	    Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
	    Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
	    Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

	    try {
	        nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
	        nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
	        itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
	    } catch (Throwable t) {
	        t.printStackTrace();
	        return null;
	    }

	    // Return a string representation of the serialized object
	    return itemAsJsonObject.toString();
	}
	
	public static String convertUUIDToName(String uuid) throws IOException
	{
		String name = null;
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLUGINUSER, "player_uuid = ?", uuid))
		{
			name = ((PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
					"player_uuid = ?", uuid)).getName();
			return name;
		}
		return null;
	}
	
	public static UUID convertNameToUUID(String playername) throws IOException
	{
		UUID uuid = null;
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLUGINUSER, "player_name = ?", playername))
		{
			uuid = UUID.fromString(((PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
					"player_name = ?", playername)).getUUID());
			return uuid;
		}
		return null;
	}
}