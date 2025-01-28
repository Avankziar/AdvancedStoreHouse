package main.java.me.avankziar.general.handler;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class CallInventoryPostUpdateEvent
{
	public static void callEvent(boolean b, Inventory i)
	{
		Bukkit.getPluginManager().callEvent(new me.avankziar.ifh.spigot.event.inventory.InventoryPostUpdateEvent(false, i));
	}
}
