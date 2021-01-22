package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGDistributionChest_Position extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Position(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash distributionchest position")));
			return;
		}
		if(user.getMode() == Mode.POSITIONUPDATEDISTRIBUTION)
		{
			user.setMode(Mode.NONE);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.Deactive")));
		} else
		{
			user.setMode(Mode.POSITIONUPDATEDISTRIBUTION);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.DChest")));
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.PLUGINUSER, user, "`player_uuid` = ?", user.getUUID());
		PluginUserHandler.addUser(user);
		return;
	}
}