package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.handler.PermissionHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash itemfilterset update")));
			return;
		}
		if(user.getItemFilterSet() == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSet.NotExist")));
			return;
		}
		ItemFilterSet ifs = user.getItemFilterSet();
		ItemFilterSet newifs = null;
		if(!ifs.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSITEMFILTERSETUPDATE)
				)
		{
			int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.ITEMFILTERSET, "`owner_uuid` = ?", user.getUUID());
			if(!PermissionHandler.canCreate(player, Utility.PERMBYPASSITEMFILTERSET, Utility.PERMCOUNTITEMFILTERSET,
					amount, plugin.getYamlHandler().getLimits().getInt("MaximumItemFilterSet", 500), false))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetCreate.TooMany")));
				return;
			}
			newifs = new ItemFilterSet(0, ifs.getName()+"_Copy", user.getUUID(), ifs.getContents());
			plugin.getMysqlHandler().create(MysqlHandler.Type.ITEMFILTERSET, newifs);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetUpdate.NewOne")));
			newifs.setID(((ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
					"`itemfiltersetname` = ? AND `owner_uuid` = ?", newifs.getName(), user.getUUID())).getID());
			user.setItemFilterSet(newifs);
		} else
		{
			user.setMode(PluginUser.Mode.CHANGEITEMFILTERSET);
		}
		PluginUserHandler.addUser(user);
		Inventory inv = Bukkit.createInventory(null, 6*9, 
				ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSet.InventoryName")
						.replace("%name%", user.getItemFilterSet().getName())));
		inv.setContents(user.getItemFilterSet().getContents());
		player.openInventory(inv);
		return;
	}
}