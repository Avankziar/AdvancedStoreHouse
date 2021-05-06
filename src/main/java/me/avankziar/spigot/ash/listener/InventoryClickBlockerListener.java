package main.java.me.avankziar.spigot.ash.listener;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Lockable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class InventoryClickBlockerListener implements Listener
{
	public static LinkedHashMap<String, Long> distributionChestForHopperOnCooldown = new LinkedHashMap<>();
	
	public static void debug(int level, String s)
	{
		boolean bo = false;
		int l = 0;
		if(bo)
		{
			if(level >= l)
			{
				AdvancedStoreHouse.log.info(s);
				for(Player player : Bukkit.getOnlinePlayers())
				{
					player.spigot().sendMessage(ChatApi.tctl(s));
				}	
			}	
		}
	}
	
	@EventHandler
	public void hopperBlock(InventoryClickEvent event) throws IOException
	{
		if(event.getInventory().getHolder() instanceof Chest)
		{
			if(isDcOnCooldown(event.getInventory(), "clickevent"))
			{
				event.setCancelled(true);
				event.setResult(Result.DENY);
				return;
			}
		}
	}
	
	private boolean isDcOnCooldown(final Inventory inv, String target) throws IOException
	{
		if(inv == null || inv.getLocation() == null)
		{
			debug(5, "inv == null || inv.Location == null");
			return true;
		}
		BlockState bs = inv.getLocation().getBlock().getState(); //Funktioniert
		if(bs instanceof Lockable)
		{
			debug(5, "instanceof lockable | locked == "+((Lockable) bs).isLocked());
			if(((Lockable) bs).isLocked())
			{
				debug(5, "is locked");
				return true;
			}
		}	
		return false;
	}
	
	public static String getLocText(Location loc)
	{
		return PluginSettings.settings.getServer()
				+";"+loc.getWorld().getName()
				+";"+loc.getBlockX()
				+";"+loc.getBlockY()
				+";"+loc.getBlockZ();
	}
	
	public static void setLocationCooldown(Location loc, long time)
	{
		String s = getLocText(loc);
		if(distributionChestForHopperOnCooldown.containsKey(s))
		{
			distributionChestForHopperOnCooldown.replace(s, time);
		} else
		{
			distributionChestForHopperOnCooldown.put(s, time);
		}
	}
}