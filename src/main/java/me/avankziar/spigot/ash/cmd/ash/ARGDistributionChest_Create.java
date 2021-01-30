package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.PermissionHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDistributionChest_Create extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Create(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash create")));
			return;
		}
		String name = args[2];
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, 
				"`owner_uuid` = ? AND `chestname` = ?", user.getUUID(), name))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.NameExist")));
			return;
		}
		int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST, "`owner_uuid` = ?", user.getUUID());
		if(!PermissionHandler.canCreate(player, Utility.PERMCOUNTDISTRIBUTIONCHEST+"*", Utility.PERMCOUNTDISTRIBUTIONCHEST,
				amount , plugin.getYamlHandler().getConfig().getInt("maximumDistributionChest"), false))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.TooMany")));
			return;
		}
		user.setMode(PluginUser.Mode.CREATEDISTRIBUTIONCHEST);
		user.setDistributionChestName(name);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.Init")));
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Cancel.SetCancel")));
		return;
	}
}