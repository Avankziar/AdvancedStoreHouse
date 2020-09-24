package main.java.me.avankziar.spigot.advancedstorehouse.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;

public class PlayerCommandPreprocessListener implements Listener
{
	
	@EventHandler
	public void onPrepareCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			return;
		}
		if(user.getMode() == PluginUser.Mode.NONE
				|| user.getMode() == PluginUser.Mode.CREATEITEMFILTERSET
				|| user.getMode() == PluginUser.Mode.CHANGEITEMFILTERSET
				|| user.getMode() == PluginUser.Mode.CHANGEITEMFILTERSET)
		{
			user.setMode(PluginUser.Mode.CONSTRUCT);
			PluginUserHandler.addUser(user);
		}
		return;
	}
}
