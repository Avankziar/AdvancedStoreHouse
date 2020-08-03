package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGStorageChest_Search extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_Search(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
		if(user.getStorageChestID() == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectSc")));
			return;
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", user.getStorageChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectScDontExist")));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
				"`id` = ?", user.getStorageChestID());
		if(sc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.SelectScDontExist")));
			return;
		}
		switch(user.getSearchType())
		{
		case COMPASS:
			compass(player, user, sc);
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
	
	private void compass(Player player, PluginUser user, StorageChest dc)
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
		player.setCompassTarget(ChestHandler.getLocationStorageChest(dc));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Search.Compass")
				.replace("%world%", dc.getWorld())
				.replace("%x%", String.valueOf(dc.getBlockX()))
				.replace("%y%", String.valueOf(dc.getBlockY()))
				.replace("%z%", String.valueOf(dc.getBlockZ()))));
		return;
	}
}