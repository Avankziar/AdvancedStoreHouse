package main.java.me.avankziar.spigot.ash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.cmd.tree.CommandConstructor;

public class PlayerCommandPreprocessListener implements Listener
{
	private CommandConstructor cc;
	
	public PlayerCommandPreprocessListener(CommandConstructor cc)
	{
		this.cc = cc;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPrepareCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			return;
		}
		if(user.getMode() == PluginUser.Mode.NONE
				|| user.getMode() == PluginUser.Mode.CREATEDISTRIBUTIONCHEST
				|| user.getMode() == PluginUser.Mode.CREATESTORAGE
				|| user.getMode() == PluginUser.Mode.CREATEITEMFILTERSET
				|| user.getMode() == PluginUser.Mode.CHANGEITEMFILTERSET
				|| user.getMode() == PluginUser.Mode.CHANGEITEMFILTERSET)
		{
			if(!event.getMessage().startsWith(cc.getSuggestion()))
			{
				user.setMode(PluginUser.Mode.CONSTRUCT);
				PluginUserHandler.addUser(user);
			}
		}
		return;
	}
}
