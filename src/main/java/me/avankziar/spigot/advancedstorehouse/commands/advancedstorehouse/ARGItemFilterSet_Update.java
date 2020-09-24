package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.handler.PermissionHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGItemFilterSet_Update extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGItemFilterSet_Update(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash itemfilterset update")));
			return;
		}
		if(user.getItemFilterSet() == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSet.NotExist")));
			return;
		}
		ItemFilterSet ifs = user.getItemFilterSet();
		ItemFilterSet newifs = null;
		if(!ifs.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSITEMFILTERSETUPDATE))
		{
			int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.ITEMFILTERSET, "`owner_uuid` = ?", user.getUUID());
			if(!PermissionHandler.canCreate(player, Utility.PERMBYPASSITEMFILTERSET, Utility.PERMCOUNTITEMFILTERSET,
					amount, plugin.getYamlHandler().get().getInt("maximumItemFilterSet", 500), false))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetCreate.TooMany")));
				return;
			}
			newifs = new ItemFilterSet(0, ifs.getName()+"_Copy", user.getUUID(), ifs.getContents());
			plugin.getMysqlHandler().create(MysqlHandler.Type.ITEMFILTERSET, newifs);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetUpdate.NewOne")));
			newifs.setID(((ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
					"`itemfiltersetname` = ? AND `owner_uuid` = ?", newifs.getName(), user.getUUID())).getID());
		}
		user.setMode(PluginUser.Mode.CHANGEITEMFILTERSET);
		user.setItemFilterSet(newifs);
		PluginUserHandler.addUser(user);
		Inventory inv = Bukkit.createInventory(null, 6*9, "ItemFilter: "+ifs.getName()+"_Copy");
		inv.setContents(newifs.getContents());
		player.openInventory(inv);
		return;
	}
}