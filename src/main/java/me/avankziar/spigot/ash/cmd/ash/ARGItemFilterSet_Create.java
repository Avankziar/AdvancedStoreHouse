package main.java.me.avankziar.spigot.ash.cmd.ash;

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

public class ARGItemFilterSet_Create extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGItemFilterSet_Create(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String name = args[2];
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash itemfiltersetcreate")));
			return;
		}
		int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.ITEMFILTERSET, "`owner_uuid` = ?", user.getUUID());
		if(!PermissionHandler.canCreate(player, Utility.PERMBYPASSITEMFILTERSET, Utility.PERMCOUNTITEMFILTERSET,
				amount, plugin.getYamlHandler().get().getInt("maximumItemFilterSet", 500), false))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetCreate.TooMany")));
			return;
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.ITEMFILTERSET, 
				"`itemfiltersetname` = ? AND `owner_uuid` = ?", name, user.getUUID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetCreate.AlreadyExist")
					.replace("%name%", name)));
			return;
		}
		user.setMode(PluginUser.Mode.CREATEITEMFILTERSET);
		Inventory empty = Bukkit.createInventory(null, 6*9, "ItemFilter: "+name);
		user.setItemFilterSet(new ItemFilterSet(0, name, user.getUUID(), empty.getContents()));
		PluginUserHandler.addUser(user);
		player.openInventory(empty);
		return;
	}
}
