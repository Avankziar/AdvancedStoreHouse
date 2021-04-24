package main.java.me.avankziar.spigot.ash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import net.md_5.bungee.api.ChatColor;

public class SignChangeListener implements Listener
{	
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		String line2 = event.getLine(1);
		if(!line2.contains("[Lager]")
				&& !line2.contains("[ASH]"))
		{
			return;
		}
		if(!event.getPlayer().hasPermission(Utility.PERMBYPASSSIGN))
		{
			event.getPlayer().sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("NoPermission")));
			return;
		}
		String line3 = event.getLine(2);
		String line4 = event.getLine(3);
		if(MatchApi.isInteger(ChatColor.stripColor(line3)) == false)
		{
			event.setLine(0, "Error in ID");
			event.setLine(1, "");
			return;
		}
		if(!line4.contains("SWITCH")
				&& !line4.contains("DISTRIBUTE")
				&& !line4.contains("VERTEILEN")
				&& !MatchApi.isInteger(ChatColor.stripColor(line4)))
		{
			event.setLine(0, "Error in Line 4");
			event.setLine(1, "");
			return;
		}
	}
}
