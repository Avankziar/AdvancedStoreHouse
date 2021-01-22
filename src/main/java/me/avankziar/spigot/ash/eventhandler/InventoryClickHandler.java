package main.java.me.avankziar.spigot.ash.eventhandler;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class InventoryClickHandler implements Listener
{
	private AdvancedStoreHouse plugin;
	
	public InventoryClickHandler(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player))
		{
			return;
		}
		if(event.getClickedInventory() == null)
		{
			return;
		}
		if(event.getCurrentItem() == null)
		{
			return;
		}
		/*if(event.getAction() != InventoryAction.PICKUP_ALL)
		{
			return;
		}*/ //das führt zum Item Duplizieren
		Player player = (Player) event.getWhoClicked();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			return;
		}
		boolean isTopInventory = false;
		if(event.getClickedInventory().getType() == InventoryType.CHEST)
		{
			isTopInventory = true;
		}
		switch(user.getMode())
		{
		case CONSTRUCT:
			//Hier passiert nix
			return;
		case NONE:
			//Hier passiert nix
			return;
		case BLOCKINFO:
			//Hier passiert nix
			return;
		case CREATEDISTRIBUTIONCHEST:
			//Hier passiert nix
			return;
		case CREATESTORAGE:
			updateGui(event, player, user, isTopInventory);
			return;
		case UPDATESTORAGE:
			//Hier passiert nix
			return;
		case UPDATESTORAGEITEMFILTERSET:
			updateGui(event, player, user, isTopInventory);
			return;
		case CREATEITEMFILTERSET:
			updateGui(event, player, user, isTopInventory);
			return;
		case CHANGEITEMFILTERSET:
			updateGui(event, player, user, isTopInventory);
			return;
		case POSITIONUPDATEDISTRIBUTION:
			//Hier passiert nix
			return;
		case POSITIONUPDATESTORAGE:
			//Hier passiert nix
			return;
		}
	}
	
	private void updateGui(InventoryClickEvent event, Player player, PluginUser user, boolean isTopInventory)
	{
		if(event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
				|| event.getAction() == InventoryAction.HOTBAR_SWAP)
		{
			event.setCancelled(true);
			event.setResult(Result.DENY);
			return;
		}
		ItemStack clicked = event.getCurrentItem().clone();
		clicked.setAmount(1);
		if(clicked.getItemMeta() instanceof Damageable)
		{
			Damageable di = (Damageable) clicked.getItemMeta();
			di.setDamage(0);
			clicked.setItemMeta((ItemMeta) di);
		}
		final int slot = event.getSlot();
		event.setCancelled(true);
		event.setResult(Result.DENY);
		Inventory inv = event.getView().getTopInventory();
		if(inv == null)
		{
			return;
		}
		if(isTopInventory)
		{
			inv.setItem(slot, null);
		} else
		{
			if(ChestHandler.isSimilar(clicked, inv.getContents()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.InventoyClick.ItemExist")));
				return;
			}
			if(ChestHandler.isFull(inv))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.InventoyClick.InventoryFull")));
				return;
			}
			inv.addItem(clicked);
		}
	}
}