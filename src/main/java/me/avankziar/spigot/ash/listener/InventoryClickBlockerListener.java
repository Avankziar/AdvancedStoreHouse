package main.java.me.avankziar.spigot.ash.listener;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.handler.ChestHandler;
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
	@EventHandler
	 public void onMoveItem(InventoryMoveItemEvent event) throws IOException 
	 {
		if(isDcOnCooldown(event.getSource(), "Source"))
		{
			event.setCancelled(true);
			return;
		}
		if(isDcOnCooldown(event.getDestination(), "Destination"))
		{
			event.setCancelled(true);
			return;
		}
	 }
	
	private boolean isDcOnCooldown(final Inventory inv, String target) throws IOException
	{
		String server = PluginSettings.settings.getServer();
		if(inv == null || inv.getLocation() == null)
		{
			if(inv != null && inv.getType() == InventoryType.CHEST)
			{
				debug(1, target +" Chest == null");
			}
			return true;
		}
		if(inv.getType() != InventoryType.CHEST)
		{
			return false;
		}
		final Location loc = inv.getLocation();
		String s = getLocText(loc);
		if(isCooldown(s))
		{
			if(inv != null && inv.getType() == InventoryType.CHEST)
			{
				debug(1, target+" Chest is Cooldown");
			}
			return true;
		}
		if(inv instanceof DoubleChestInventory)
		{
			DoubleChestInventory dcInv = (DoubleChestInventory) inv;
			final Location loc2 = ChestHandler.isDoubleChest(AdvancedStoreHouse.getPlugin(), server, loc, dcInv);
			if(loc2 == null)
			{
				return false;
			}
			s = getLocText(loc2);
			if(isCooldown(s))
			{
				if(inv != null && inv.getType() == InventoryType.CHEST)
				{
					debug(1, target+" Chest is Cooldown");
				}
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
	
	private boolean isCooldown(String s)
	{
		Long l = distributionChestForHopperOnCooldown.get(s);
		if(l != null && l >= System.currentTimeMillis())
		{
			return true;
		}
		return false;
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