package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGDistributionChest_Search extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Search(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash search")));
			return;
		}
		if(user.getDistributionChestID() == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectDc")));
			return;
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectDcDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", user.getDistributionChestID());
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectDcDontExist")));
			return;
		}
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSSEARCH))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
			return;
		}
		switch(user.getSearchType())
		{
		case COMPASS:
			compass(player, user, dc);
			return;
		case EFFECT:
			return;
		case GLOWINGENTITY:
			return;
		case SOUND:
			return;
		case TELEPORT:
			return;
		}
		return;
	}
	
	private void compass(Player player, PluginUser user, DistributionChest dc)
	{
		String server = plugin.getYamlHandler().get().getString("Servername");
		if(!dc.getServer().equals(server))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotSameServer")
					.replace("%yourserver%", server)
					.replace("%server%", dc.getServer())));
			return;
		}
		user.setCompassLocation(player.getCompassTarget());
		PluginUserHandler.addUser(user);
		player.setCompassTarget(ChestHandler.getLocationDistributionChest(dc));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.Compass")
				.replace("%world%", dc.getWorld())
				.replace("%x%", String.valueOf(dc.getBlockX()))
				.replace("%y%", String.valueOf(dc.getBlockY()))
				.replace("%z%", String.valueOf(dc.getBlockZ()))));
		return;
	}
}