package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.handler.DistributionHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class InventoryCloseHandler implements Listener
{
	private AdvancedStoreHouse plugin;
	
	public InventoryCloseHandler(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) throws IOException
	{
		if(!(event.getPlayer() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getPlayer();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			//debug(player, "User == null");
			return;
		}
		switch(user.getMode())
		{
		default:
			return;
		case CONSTRUCT:
			distributionStart(event, player, user);
			return;
		case CREATESTORAGE:
			updateStorageChest(event, player, user); //Update des ItemStack[]
			return;
		case UPDATESTORAGEITEMFILTERSET:
			updateStorageChest(event, player, user); //Update des ItemStack[]
			return;
		case CREATEITEMFILTERSET:
			createItemFilterSet(event, player, user); //Erstellen spezieller ItemStack[]
			return;
		case CHANGEITEMFILTERSET:
			updateItemFilterSet(event, player, user); //Update von spezieller ItemStack[]
			return;
		}
	}
	
	//Wenn nur rechtsklickt auf kisten gemacht wurden. Verteilung start
	private void distributionStart(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
	{
		debug(player, "=> Begin Methode distributionStart");
		Location loc = event.getInventory().getLocation();
		if(loc == null)
		{
			return;
		}
		DistributionHandler.distributeStartVersionPhysical(PluginSettings.settings.getServer(), loc, event.getInventory());
	}
	
	//Nur eine Update für den ItemFilterSet der Lagerkiste
	private void updateStorageChest(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
	{
		debug(player, "=> Begin Methode createStorageChest (Inventory)");
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", user.getStorageChestID()))
		{
			debug(player, "StorageChest dont exist");
			return;
		}
		final ItemStack[] contents = event.getView().getTopInventory().getContents();
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		sc.setContents(contents);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
		if(user.getMode() == Mode.UPDATESTORAGEITEMFILTERSET)
		{
			user.setMode(Mode.CONSTRUCT);
			PluginUserHandler.addUser(user);
		}
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.UpdateStorageChest")
						.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO).replace(" ", "+"))));
		return;
	}
	
	private void createItemFilterSet(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
	{
		debug(player, "=> Begin Methode createItemFilterSet (Inventory)");
		if(user.getItemFilterSet() == null)
		{
			debug(player, "ItemFilterSet == null");
			return;
		}
		if(user.getItemFilterSet().getName() == null)
		{
			debug(player, "ItemFilterSet.Name == null");
			return;
		}
		final ItemStack[] content = event.getView().getTopInventory().getContents();
		user.getItemFilterSet().setContents(content);
		user.getItemFilterSet().setOwneruuid(user.getUUID());
		plugin.getMysqlHandler().create(MysqlHandler.Type.ITEMFILTERSET, user.getItemFilterSet());
		ItemFilterSet ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
				"`owner_uuid` = ? AND `itemfiltersetname` = ?", user.getUUID(), user.getItemFilterSet().getName());
		user.setItemFilterSet(ifs);
		user.setMode(Mode.CONSTRUCT);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSet.Create")
				.replace("%name%", ifs.getName())));
		return;
	}
	
	private void updateItemFilterSet(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
	{
		debug(player, "=> Begin Methode updateItemFilterSet (Inventory)");
		if(user.getItemFilterSet() == null)
		{
			debug(player, "ItemFilterSet == null");
			return;
		}
		if(user.getItemFilterSet().getID() == 0)
		{
			debug(player, "ItemFilterSet.ID == 0");
			return;
		}
		if(user.getItemFilterSet().getName() == null)
		{
			debug(player, "ItemFilterSet.Name == null");
			return;
		}
		final ItemStack[] content = event.getView().getTopInventory().getContents();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.ITEMFILTERSET, "`id` = ?", user.getItemFilterSet().getID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSet.NotExist")));
			return;
		}
		ItemFilterSet ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
				"`id` = ?", user.getItemFilterSet().getID());
		ifs.setContents(content);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.ITEMFILTERSET, ifs, "`id` = ?", user.getItemFilterSet().getID());
		user.setItemFilterSet(ifs);
		user.setMode(Mode.CONSTRUCT);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSet.Update")
				.replace("%name%", ifs.getName())));
		return;
	}
}
