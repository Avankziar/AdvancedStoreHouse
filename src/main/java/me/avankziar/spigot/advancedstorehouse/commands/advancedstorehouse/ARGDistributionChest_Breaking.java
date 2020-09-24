package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;

public class ARGDistributionChest_Breaking extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Breaking(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest breaking")));
			return;
		}
		if(user.canDistributionChestBreak())
		{
			user.setCanDistributionChestBreak(false);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.CanBreakDChest.Deactive")));
		} else
		{
			user.setCanDistributionChestBreak(true);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.CanBreakDChest.Active")));
		}
		PluginUserHandler.addUser(user);
		return;
	}
}