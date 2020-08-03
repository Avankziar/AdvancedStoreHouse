package main.java.me.avankziar.spigot.advancedstorehouse.listener;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class JoinQuitListener implements Listener
{
	private AdvancedStoreHouse plugin;
	
	public JoinQuitListener(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) throws IOException
	{
		Player player = event.getPlayer();
		PluginUser user = null;
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString()))
		{
			user = new PluginUser(player.getUniqueId().toString(), player.getName(), PluginUser.SearchType.COMPASS);
			plugin.getMysqlHandler().create(MysqlHandler.Type.PLUGINUSER, user);
		}
		if(user == null)
		{
			user = (PluginUser) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		}
		if(!player.getName().equals(user.getName()))
		{
			user.setName(player.getName());
			plugin.getMysqlHandler().updateData(
					MysqlHandler.Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		}
		PluginUserHandler.addUser(user);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		UUID uuid = event.getPlayer().getUniqueId();
		PluginUser user = PluginUserHandler.getUser(uuid);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		PluginUserHandler.removeUser(PluginUserHandler.getUser(uuid));
	}
}
