package main.java.me.avankziar.spigot.advancedstorehouse.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.ConvertHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PermissionHandler;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractHandler implements Listener
{
	private AdvancedStoreHouse plugin;
	private static HashMap<String, Long> cooldown;
	
	public InteractHandler(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
		cooldown = new HashMap<>();
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) throws IOException
	{
		debug(event.getPlayer(), "=> Interact Beginn <= Action: "+event.getAction().toString());
		if(cooldown.containsKey(event.getPlayer().getName()))
		{
			if(cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis())
			{
				debug(event.getPlayer(), "Cooldown");
				if(event.getClickedBlock() != null)
				{
					if(event.getClickedBlock().getType() == Material.LEVER)
					{
						event.setCancelled(true);
					}
				}
				Player player = event.getPlayer();
				PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
				if(user == null)
				{
					debug(event.getPlayer(), "User == null");
					return;
				}
				if(user.getMode() != Mode.CONSTRUCT)
				{
					event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("Cooldown")));
				}
				return;
			} else
			{
				cooldown.replace(event.getPlayer().getName(), System.currentTimeMillis()+1000*1);
			}
		} else
		{
			cooldown.put(event.getPlayer().getName(), System.currentTimeMillis()+1000*1);
		}
		if(event.getClickedBlock() == null)
		{
			debug(event.getPlayer(), "ClickedBlock == null");
			return;
		}
		Player player = event.getPlayer();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			debug(event.getPlayer(), "User == null");
			return;
		}
		if(event.isCancelled())
		{
			debug(event.getPlayer(), "Event isCancelled");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		if(event.getAction() == Action.PHYSICAL)
		{	
			debug(event.getPlayer(), "Action == Physical");
			if(event.getClickedBlock() != null)
			{
				buttonAndPlate(event, player, user);
			}
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			debug(event.getPlayer(), "Action != Right_Click_Block");
			//PluginUserHandler.cancelAction(user, user.getMode());
			return;
		}
		if(event.getClickedBlock().getType() == Material.LEVER)
		{
			lever(event, player, user);
		}
		if(event.getClickedBlock().getType() == Material.STONE_BUTTON
				|| event.getClickedBlock().getType() == Material.POLISHED_BLACKSTONE_BUTTON
				|| event.getClickedBlock().getType() == Material.ACACIA_BUTTON
				|| event.getClickedBlock().getType() == Material.BIRCH_BUTTON
				|| event.getClickedBlock().getType() == Material.CRIMSON_BUTTON
				|| event.getClickedBlock().getType() == Material.DARK_OAK_BUTTON
				|| event.getClickedBlock().getType() == Material.JUNGLE_BUTTON
				|| event.getClickedBlock().getType() == Material.OAK_BUTTON
				|| event.getClickedBlock().getType() == Material.SPRUCE_BUTTON
				|| event.getClickedBlock().getType() == Material.WARPED_BUTTON)
		{
			buttonAndPlate(event, player, user);
			return;
		}
		if(!player.isSneaking())
		{
			debug(event.getPlayer(), "!player.isSneacking");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		switch(user.getMode())
		{
		case CONSTRUCT:
			return;
		case NONE:
			updateStorageChestItemFilterSet(event, player, user);
			return;
		case BLOCKINFO:
			blockInfo(event, player, user);
			return;
		case CREATEDISTRIBUTIONCHEST:
			createDistributionChest(event, player, user);
			return;
		case CREATESTORAGE:
			createStorageChest(event, player, user);
			return;
		case UPDATESTORAGE:
			updateStorageChest(event, player, user);
			return;
		case UPDATESTORAGEITEMFILTERSET:
			//Hier kommt nix
			return;
		case CREATEITEMFILTERSET:
			//Hier kommt nix
			return;
		case CHANGEITEMFILTERSET:
			//Hier kommt nix
			return;
		case POSITIONUPDATEDISTRIBUTION:
			updatePosition(event, player, user, true);
			return;
		case POSITIONUPDATESTORAGE:
			updatePosition(event, player, user, false);
			return;
		}
	}
	
	private void updateStorageChestItemFilterSet(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode updateStorageChestItemFilterSet");
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		Location loc = event.getClickedBlock().getLocation();
		String server = plugin.getYamlHandler().get().getString("Servername");
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND"
				+ " `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(), 
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
				&& !plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
						" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			Block block = event.getClickedBlock();
			if(block == null)
			{
				return;
			}
			Location l = block.getLocation();
			if(block.getState() == null)
			{
				return;
			}
			if(block.getState() instanceof Container)
			{
				Container c = (Container) block.getState();
				Inventory inv = c.getInventory();
				if(inv instanceof DoubleChestInventory)
				{
					l = isDoubleChestII(player, server, l);
					if(l == null)
					{
						debug(event.getPlayer(), "Loop DoubleChest Loc == null");
						return;
					}
					loc = l;
				} else
				{
					debug(event.getPlayer(), "StorageChest dont exist here");
					return;
				}
			}
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, 
				"`distributionchestid` = ? AND"
				+ " `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(), 
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(sc == null)
		{
			sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, 
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?", 
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.DistributionChestDontExistNone")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Update.MayDeleteNone")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", sc.getDistributionChestID());
		String name = "/";
		String id = "/";
		if(dc != null)
		{
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSSELECT))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
				return;
			}
			name = dc.getChestName();
			id = ""+dc.getId();
		}
		event.setCancelled(true);
		user.setMode(PluginUser.Mode.UPDATESTORAGEITEMFILTERSET);
		user.setStorageChestID(sc.getId());
		PluginUserHandler.addUser(user);
		Inventory inv = Bukkit.createInventory(null, 6*9, 
				plugin.getYamlHandler().getL().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriority()))
				.replace("%name%", name)
				.replace("%dcid%", id)
				.replace("%id%", String.valueOf(sc.getId())));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}
	
	private void createDistributionChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode createDistributionChest");
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		event.setCancelled(true);
		Location loc = event.getClickedBlock().getLocation();
		int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.DISTRIBUTIONCHEST)+1;
		String name = String.valueOf(last);
		if(user.getDistributionChestName() != null)
		{
			name = user.getDistributionChestName();
		}
		DistributionChest dc = new DistributionChest(
				last, user.getUUID(), new ArrayList<String>(), System.currentTimeMillis(),
				name, true, plugin.getYamlHandler().get().getBoolean("IsAutomaticDistribution", false),
				plugin.getYamlHandler().get().getString("Servername"),
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		plugin.getMysqlHandler().create(MysqlHandler.Type.DISTRIBUTIONCHEST, dc);
		last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.DISTRIBUTIONCHEST);
		dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		player.spigot().sendMessage(ChatApi.generateTextComponent(
				plugin.getYamlHandler().getL().getString("CmdAsh.Create.SetupDChest")
				.replace("%dc%", dc.getChestName())));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.FutherInstruction")
				.replace("%dc%", dc.getChestName())));
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Cancel.SetCancel")));
		user.setDistributionChestID(dc.getId());
		user.setMode(PluginUser.Mode.CREATESTORAGE);
		PluginUserHandler.addUser(user);
		return;
	}
	
	//Muss nicht nach Owner oder Member nachgefragt werden, da hier erstellt wird.
	private void createStorageChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode createStorageChest");
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, 
				"`distributionchestid` = ? AND `owner_uuid` = ?", user.getDistributionChestID(), user.getUUID());
		if(!PermissionHandler.canCreate(player, Utility.PERMCOUNTSTORAGECHEST+"*", Utility.PERMCOUNTSTORAGECHEST,
				amount , plugin.getYamlHandler().get().getInt("maximumStorageChest"), false))
		{
			debug(event.getPlayer(), "TooMany StorageChest");
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.TooManyS")));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		String server = plugin.getYamlHandler().get().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `owner_uuid` = ? AND"
				+ " `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(), user.getUUID(), 
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.StorageChestExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.DistributionChestDontExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		event.setCancelled(true);
		StorageChest sc = new StorageChest(0, user.getDistributionChestID(), user.getUUID(),
				user.getPriority(),
				System.currentTimeMillis(), user.getItemFilterSet().getContents(), user.isEndStorage(),
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", user.getDistributionChestID());
		String name = "/";
		String id = "/";
		if(dc != null)
		{
			name = dc.getChestName();
			id = ""+dc.getId();
		}
		plugin.getMysqlHandler().create(MysqlHandler.Type.STORAGECHEST, sc);
		int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
		user.setStorageChestID(last);
		PluginUserHandler.addUser(user);
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Create.SetupSChest")
						.replace("%sc%", String.valueOf(last))));
		Inventory gui = Bukkit.createInventory(null, 6*9, 
				plugin.getYamlHandler().getL().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriority()))
				.replace("%name%", name)
				.replace("%dcid%", id)
				.replace("%id%", String.valueOf(sc.getId())));
		gui.setContents(sc.getContents());
		player.openInventory(gui);
		return;
	}
	
	private void updateStorageChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode updateStorageChest");
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		String server = plugin.getYamlHandler().get().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND"
				+ " `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(),
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Update.StorageChestDontExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			return;
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.DistributionChestDontExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Update.MayDelete")));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
				" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID());
		if(dc != null)
		{
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSSELECT))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
				return;
			}
		}
		
		event.setCancelled(true);
		int i = 0;
		if(user.isOverride())
		{
			if(user.getDistributionChestID() != 0)
			{
				if(user.getDistributionChestID() == dc.getId())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Update.SameSelectedDC")));
				} else
				{
					DistributionChest newdc = (DistributionChest) plugin.getMysqlHandler().getData(
							MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
					if(!ChestHandler.isMember(player, newdc) && !newdc.getOwneruuid().equals(player.getUniqueId().toString())
							&& !player.hasPermission(Utility.PERMBYPASSSELECT))
					{
						player.sendMessage(ChatApi.tl(
								plugin.getYamlHandler().getL().getString("CmdAsh.Update.NotOwnerOrMemberSelectedDC")));
						return;
					}
					i++;
					sc.setDistributionChestID(user.getDistributionChestID());
					debug(event.getPlayer(), "sc override");
				}
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Update.NoSelectedDC")));
			}
		}
		if(user.getItemFilterSet() != null)
		{
			if(ChestHandler.isContentEmpty(user.getItemFilterSet().getContents()))
			{
				i++;
				sc.setContents(user.getItemFilterSet().getContents());
				debug(event.getPlayer(), "sc setContent");
			}
		}
		if(sc.isEndstorage() != user.isEndStorage())
		{
			i++;
			sc.setEndstorage(user.isEndStorage());
			debug(event.getPlayer(), "sc set as endstorage");
		}
		if(sc.getPriority() != user.getPriority())
		{
			i++;
			sc.setPriority(user.getPriority());
			debug(event.getPlayer(), "sc set prio "+user.getPriority());
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
		player.spigot().sendMessage(ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Update.IsUpdated")
				.replace("%sc%", String.valueOf(sc.getId()))
				.replace("%i%", String.valueOf(i))));
		return;
	}
	
	private void updatePosition(PlayerInteractEvent event, Player player, PluginUser user, boolean isDistributionChest) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode updatePosition");
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getL().getString("CancelAction"));
			return;
		}
		if(user.getDistributionChestID() == 0 && user.getStorageChestID() == 0)
		{
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Position.NoSelectedChest")));
			return;
		}
		String server = plugin.getYamlHandler().get().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		
		if(isDistributionChest)
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
					&& plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id` = ?", user.getDistributionChestID()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.SameDistributionChest")));
				return;
			}
			//DcId wird /ash select !=0 gecheckt
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.DistributionChestNotExist")));
				return;
			}
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`id` = ?", user.getDistributionChestID());
			dc.setServer(server);
			dc.setWorld(loc.getWorld().getName());
			dc.setBlockX(loc.getBlockX());
			dc.setBlockY(loc.getBlockY());
			dc.setBlockZ(loc.getBlockZ());
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.IsUpdated")));
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.MayChange")));
		} else
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
					&& plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
							"`id` = ?", user.getStorageChestID()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.SameStorageChest")));
				return;
			}
			StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
					"`id` = ?", user.getStorageChestID());
			
			int dcID = 0;
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID()))
			{
				if(user.getDistributionChestID() == 0
						|| !plugin.getMysqlHandler().exist(
								MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Create.DistributionChestDontExistNone")
							.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
					player.spigot().sendMessage(
							ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Update.MayDeleteNone")));
					return;
				}
				dcID = user.getDistributionChestID();
			} else
			{
				dcID = sc.getDistributionChestID();
			}
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`id` = ?", dcID);
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Update.NotOwnerOrMember")));
				return;
			}
			sc.setServer(server);
			sc.setWorld(loc.getWorld().getName());
			sc.setBlockX(loc.getBlockX());
			sc.setBlockY(loc.getBlockY());
			sc.setBlockZ(loc.getBlockZ());
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.IsUpdated")));
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Position.MayChange")));
		}
		return;
	}
	
	private void buttonAndPlate(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin ButtonAndPlate");
		Block blocks = event.getClickedBlock();
		if(blocks == null)
		{
			debug(event.getPlayer(), "Block == null");
			return;
		}
		if(blocks.getType() != Material.STONE_BUTTON
				&& blocks.getType() != Material.POLISHED_BLACKSTONE_BUTTON
				&& blocks.getType() != Material.ACACIA_BUTTON
				&& blocks.getType() != Material.BIRCH_BUTTON
				&& blocks.getType() != Material.CRIMSON_BUTTON
				&& blocks.getType() != Material.DARK_OAK_BUTTON
				&& blocks.getType() != Material.JUNGLE_BUTTON
				&& blocks.getType() != Material.OAK_BUTTON
				&& blocks.getType() != Material.SPRUCE_BUTTON
				&& blocks.getType() != Material.WARPED_BUTTON
				&& blocks.getType() != Material.ACACIA_PRESSURE_PLATE
				&& blocks.getType() != Material.BIRCH_PRESSURE_PLATE
				&& blocks.getType() != Material.CRIMSON_PRESSURE_PLATE
				&& blocks.getType() != Material.DARK_OAK_PRESSURE_PLATE
				&& blocks.getType() != Material.JUNGLE_PRESSURE_PLATE
				&& blocks.getType() != Material.OAK_PRESSURE_PLATE
				&& blocks.getType() != Material.POLISHED_BLACKSTONE_PRESSURE_PLATE
				&& blocks.getType() != Material.SPRUCE_PRESSURE_PLATE
				&& blocks.getType() != Material.STONE_PRESSURE_PLATE
				&& blocks.getType() != Material.WARPED_PRESSURE_PLATE)
		{
			debug(event.getPlayer(), "Not a Button or Plate");
			return;
		}
		Location loc = blocks.getLocation().add(-1, -1, -1);
		String server = plugin.getYamlHandler().get().getString("Servername");
		
		int reup = 0;
		int reside = 0;
		
		for(int deep = 0; deep <= 2; deep++)
		{
			for(int up = 0; up <= 2; up++)
			{
				for(int side = 0; side <= 2; side++)
				{
					final Block block = loc.getBlock();
					loc.add(0, 0, 1);
					reside--;
					if(block == null)
					{
						debug(event.getPlayer(), "Loop Block == null");
						continue;
					}
					Location l = block.getLocation();
					if(block.getState() == null)
					{
						debug(event.getPlayer(), "Loop Block.State == null");
						continue;
					}
					if(!(block.getState() instanceof Container))
					{
						debug(event.getPlayer(), "Loop Block != Container | Type: "+block.getType().toString());
						continue;
					}
					debug(event.getPlayer(), "Loop Block == Container | Type: "+block.getType().toString());
					debug(player, "Distributionchest dont find, search: "
							+server+" "+block.getLocation().getWorld().getName()+" "+block.getLocation().getBlockX()+" "+
							block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ());
					Container c = (Container) block.getState();
					Inventory inv = c.getInventory();
					boolean dci = false;
					if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
							" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))
					{
						if(inv instanceof DoubleChestInventory)
						{
							dci = true;
							l = isDoubleChest(player, server, l);
							if(l == null)
							{
								debug(event.getPlayer(), "Loop DoubleChest Loc == null");
								continue;
							}
						} else
						{
							debug(event.getPlayer(), "Loop Block dont Exist And isnt DoubleChest");
							debug(player, "Distributionchest dont find, search: "
									+server+" "+l.getWorld().getName()+" "+l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
							continue;
						}
					} else
					{
						if(inv instanceof DoubleChestInventory)
						{
							debug(player, "distribution == DoubleChestInv II");
							dci = true;
						}
					}
					DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
					int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
					ArrayList<StorageChest> prioList = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`",
									dc.isNormalPriority(), 0, last,
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
					ArrayList<StorageChest> endList = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`",
									dc.isNormalPriority(), 0, last,
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
					Inventory inventory = inv;
					ItemStack[] cloneInvL = null;
					ItemStack[] cloneInvR = null;
					if(dci)
					{
						debug(player, "distribution dci == true");
						if(inventory instanceof DoubleChestInventory)
						{
							debug(player, "distribution is DoubleChestInv");
							DoubleChestInventory dcinv = (DoubleChestInventory) inventory;
							cloneInvL = dcinv.getLeftSide().getContents();
							cloneInvR = dcinv.getRightSide().getContents();
						}
					} else
					{
						debug(player, "distribution dci == false");
						cloneInvL = inventory.getContents();
						cloneInvR = inventory.getContents();
						int j = 0;
						for(int i = 0; i < cloneInvR.length; i++)
						{
							cloneInvR[i] = null;
							j = i;
						}
						debug(player, "distribution Right side set all null | i = "+j);
					}
					//Normal Lager
					for(StorageChest sc : prioList)
					{
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							debug(player, "distribution Content is Empty");
							break;
						}
						Block scblock = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(scblock == null)
						{
							debug(player, "distribution block == null");
							continue;
						}
						if(scblock.getState() == null)
						{
							debug(player, "distribution block.State == null");
							continue;
						}
						if(!(scblock.getState() instanceof Container))
						{
							debug(player, "distribution not Container");
							continue;
						}
						Container container = (Container) scblock.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(player, "distribution cinv == null");
							continue;
						}
						debug(player, "distribution Normal Storage start");
						cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, false);
						cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, false);
					}
					//Endlager
					if(!ChestHandler.isContentEmpty(cloneInvL) || !ChestHandler.isContentEmpty(cloneInvR))
					{
						debug(player, "distribution EndStorage Content isnt Empty");
						for(StorageChest sc : endList)
						{
							if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
							{
								debug(player, "distribution Content is Empty");
								break;
							}
							Block scblock = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(scblock == null)
							{
								debug(player, "distribution block == null");
								continue;
							}
							if(scblock.getState() == null)
							{
								debug(player, "distribution block.State == null");
								continue;
							}
							if(!(scblock.getState() instanceof Container))
							{
								debug(player, "distribution not Container");
								continue;
							}
							Container container = (Container) scblock.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								debug(player, "distribution cinv == null");
								continue;
							}
							cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, true);
							cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, true);
						}
					}
					debug(player, "distribution not Removed Items set back");
					if(inventory instanceof DoubleChestInventory)
					{
						debug(player, "distribution Removed in DCI");
						DoubleChestInventory dcinv = (DoubleChestInventory) inventory;
						dcinv.getLeftSide().setContents(cloneInvL);
						dcinv.getRightSide().setContents(cloneInvR);
					} else
					{
						debug(player, "distribution Removed in else");
						inventory.setContents(cloneInvL);
					}
					
					//Ketten Check
					ArrayList<DistributionChest> chain = new ArrayList<>();
					for(StorageChest sc : prioList)
					{
						if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()))
						{
							ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
									plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`id`", false, 
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
							chain.addAll(allAt);
						} else
						{
							World world = Bukkit.getWorld(sc.getWorld());
							if(world == null)
							{
								continue;
							}
							Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
							Block dcblock = lo.getBlock();
							if(dcblock == null)
							{
								continue;
							}
							if(dcblock.getState() == null)
							{
								continue;
							}
							if(!(dcblock.getState() instanceof Container))
							{
								continue;
							}
							Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
							if(inventoryc == null)
							{
								continue;
							}
							if(inventoryc instanceof DoubleChestInventory)
							{
								lo = isDoubleChest(player, server, lo);
								if(lo == null)
								{
									debug(event.getPlayer(), "Loop DoubleChest Loc == null");
									continue;
								}
								ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
										plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
										"`id`", false, 
										"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
										sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
								chain.addAll(allAt);
							} else
							{
								debug(event.getPlayer(), "StorageChest dont exist here");
								continue;
							}
						}
					}
					for(StorageChest sc : endList)
					{
						if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()))
						{
							ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
									plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`id`", false, 
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
							chain.addAll(allAt);
						} else
						{
							World world = Bukkit.getWorld(sc.getWorld());
							if(world == null)
							{
								continue;
							}
							Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
							Block dcblock = lo.getBlock();
							if(dcblock == null)
							{
								continue;
							}
							if(dcblock.getState() == null)
							{
								continue;
							}
							if(!(dcblock.getState() instanceof Container))
							{
								continue;
							}
							Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
							if(inventoryc == null)
							{
								continue;
							}
							if(inventoryc instanceof DoubleChestInventory)
							{
								lo = isDoubleChest(player, server, lo);
								if(lo == null)
								{
									debug(event.getPlayer(), "Loop DoubleChest Loc == null");
									continue;
								}
								ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
										plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
										"`id`", false, 
										"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
										sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
								chain.addAll(allAt);
							} else
							{
								debug(event.getPlayer(), "StorageChest dont exist here");
								continue;
							}
						}
					}
					//Verteilung der kette
					for(DistributionChest dcc : chain)
					{
						int lastc = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
						ArrayList<StorageChest> prioListc = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(), 0, lastc,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), false, server));
						ArrayList<StorageChest> endListc = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(), 0, lastc,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), true, server));
						World world = Bukkit.getWorld(dcc.getWorld());
						if(world == null)
						{
							continue;
						}
						Block dcblock = new Location(world, dcc.getBlockX(), dcc.getBlockY(), dcc.getBlockZ()).getBlock();
						if(dcblock == null)
						{
							continue;
						}
						if(dcblock.getState() == null)
						{
							continue;
						}
						if(!(dcblock.getState() instanceof Container))
						{
							continue;
						}
						Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
						if(inventoryc == null)
						{
							continue;
						}
						ItemStack[] cloneInvLc = null;
						ItemStack[] cloneInvRc = null;
						if(inventoryc instanceof DoubleChestInventory)
						{
							debug(player, "distribution dci == true");
							DoubleChestInventory dcinv = (DoubleChestInventory) inventoryc;
							cloneInvLc = dcinv.getLeftSide().getContents();
							cloneInvRc = dcinv.getRightSide().getContents();
						} else
						{
							debug(player, "distribution dci == false");
							cloneInvLc = inventoryc.getContents();
							cloneInvRc = cloneInvL;
							int j = 0;
							for(int i = 0; i < cloneInvL.length; i++)
							{
								cloneInvR[i] = null;
								j = i;
							}
							debug(player, "distribution Right side set all null | i = "+j);
						}
						
						for(StorageChest sc : prioListc)
						{
							if(ChestHandler.isContentEmpty(cloneInvLc) && ChestHandler.isContentEmpty(cloneInvRc))
							{
								debug(player, "distribution Content is Empty");
								break;
							}
							Block scblock = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(scblock == null)
							{
								debug(player, "distribution block == null");
								continue;
							}
							if(scblock.getState() == null)
							{
								debug(player, "distribution block.State == null");
								continue;
							}
							if(!(scblock.getState() instanceof Container))
							{
								debug(player, "distribution not Container");
								continue;
							}
							Container container = (Container) scblock.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								debug(player, "distribution cinv == null");
								continue;
							}
							cloneInvLc = ChestHandler.distribute(cinv, sc.getContents(), cloneInvLc, inventoryc, false);
							cloneInvRc = ChestHandler.distribute(cinv, sc.getContents(), cloneInvRc, inventoryc, false);
						}
						if(!ChestHandler.isContentEmpty(cloneInvLc) || !ChestHandler.isContentEmpty(cloneInvRc))
						{
							debug(player, "distribution EndStorage Content isnt Empty");
							for(StorageChest sc : endListc)
							{
								if(ChestHandler.isContentEmpty(cloneInvLc) && ChestHandler.isContentEmpty(cloneInvRc))
								{
									debug(player, "distribution Content is Empty");
									break;
								}
								Block scblock = new Location(Bukkit.getWorld(sc.getWorld()),
										sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
								if(scblock == null)
								{
									debug(player, "distribution block == null");
									continue;
								}
								if(scblock.getState() == null)
								{
									debug(player, "distribution block.State == null");
									continue;
								}
								if(!(scblock.getState() instanceof Container))
								{
									debug(player, "distribution not Container");
									continue;
								}
								Container container = (Container) scblock.getState();
								Inventory cinv = container.getInventory();
								if(cinv == null)
								{
									debug(player, "distribution cinv == null");
									continue;
								}
								cloneInvLc = ChestHandler.distribute(cinv, sc.getContents(), cloneInvLc, inventoryc, true);
								cloneInvRc = ChestHandler.distribute(cinv, sc.getContents(), cloneInvRc, inventoryc, true);
							}
						}
						debug(player, "distribution not Removed Items set back Chain");
						if(inventoryc instanceof DoubleChestInventory)
						{
							debug(player, "distribution Removed in DCI Chain");
							DoubleChestInventory dcinv = (DoubleChestInventory) inventoryc;
							dcinv.getLeftSide().setContents(cloneInvLc);
							dcinv.getRightSide().setContents(cloneInvRc);
						} else
						{
							debug(player, "distribution Removed in else Chain");
							inventoryc.setContents(cloneInvLc);
						}
					}
				}
				reup--;
				loc.add(1, 0, reside);
				reside = 0;
			}
			loc.add(reup, 1, 0);
			reup = 0;
		}
	}
	
	private void lever(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Lever");
		Block blocks = event.getClickedBlock();
		if(blocks == null)
		{
			debug(event.getPlayer(), "Block == null");
			return;
		}
		if(blocks.getType() != Material.LEVER)
		{
			debug(event.getPlayer(), "Not a Lever");
			return;
		}
		Location loc = blocks.getLocation().add(-1, -1, -1);
		String server = plugin.getYamlHandler().get().getString("Servername");
		
		int reup = 0;
		int reside = 0;
		
		for(int deep = 0; deep <= 2; deep++)
		{
			for(int up = 0; up <= 2; up++)
			{
				for(int side = 0; side <= 2; side++)
				{
					final Block block = loc.getBlock();
					loc.add(0, 0, 1);
					reside--;
					if(block == null)
					{
						debug(event.getPlayer(), "Loop Block == null");
						continue;
					}
					Location l = block.getLocation();
					if(block.getState() == null)
					{
						debug(event.getPlayer(), "Loop Block.State == null");
						continue;
					}
					if(!(block.getState() instanceof Container))
					{
						debug(event.getPlayer(), "Loop Block != Container | Type: "+block.getType().toString());
						continue;
					}
					debug(event.getPlayer(), "Loop Block == Container | Type: "+block.getType().toString());
					debug(player, "Distributionchest dont find, search: "
							+server+" "+block.getLocation().getWorld().getName()+" "+block.getLocation().getBlockX()+" "+
							block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ());
					Container c = (Container) block.getState();
					Inventory inv = c.getInventory();
					if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
							" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))
					{
						if(inv instanceof DoubleChestInventory)
						{
							l = isDoubleChest(player, server, l);
							if(l == null)
							{
								debug(event.getPlayer(), "Loop DoubleChest Loc == null");
								continue;
							}
						} else
						{
							debug(event.getPlayer(), "Loop Block dont Exist And isnt DoubleChest");
							debug(player, "Distributionchest dont find, search: "
									+server+" "+l.getWorld().getName()+" "+l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
							continue;
						}
					}
					DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
					if(dc.isNormalPriority() == true)
					{
						dc.setNormalPriority(false);
						player.sendMessage(ChatApi.tl(
								plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestSwitch.Deactive")
								.replace("%name%", dc.getChestName())));
					} else
					{
						dc.setNormalPriority(true);
						player.sendMessage(ChatApi.tl(
								plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestSwitch.Active")
								.replace("%name%", dc.getChestName())));
					}
					plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
				}
				reup--;
				loc.add(1, 0, reside);
				reside = 0;
			}
			loc.add(reup, 1, 0);
			reup = 0;
		}
	}
	
	private Location isDoubleChest(Player player, String server, final Location loc)
	{
		Location l1 = loc;
		l1.add(1, 0, 0);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, 1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		return null;
	}
	
	private Location isDoubleChestII(Player player, String server, final Location loc)
	{
		debug(player, "Storagechest dont find, search: "
				+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
		Location l1 = loc;
		l1.add(1, 0, 0);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, 1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		return null;
	}
	
	private void blockInfo(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final String server = plugin.getYamlHandler().get().getString("Servername");
		final Location loc = event.getClickedBlock().getLocation();
		if(loc == null)
		{
			return;
		}
		if(event.getClickedBlock() == null)
		{
			debug(event.getPlayer(), "ClickedBlock == null");
			return;
		}
		if(!(event.getClickedBlock().getState() instanceof Chest)
				&& !(event.getClickedBlock().getState() instanceof ShulkerBox)
				&& !(event.getClickedBlock().getState() instanceof Barrel)
				&& !(event.getClickedBlock().getState() instanceof Furnace)
				&& !(event.getClickedBlock().getState() instanceof BlastFurnace)
				&& !(event.getClickedBlock().getState() instanceof Smoker)
				&& !(event.getClickedBlock().getState() instanceof Hopper)
				&& !(event.getClickedBlock().getState() instanceof Dropper)
				&& !(event.getClickedBlock().getState() instanceof Dispenser)
				&& !(event.getClickedBlock().getState() instanceof BrewingStand)
				)
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			return;
		}
		ArrayList<DistributionChest> dc = new ArrayList<DistributionChest>();
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			dc = ConvertHandler.convertListII(plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`id`" ,true,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		}
		
		ArrayList<StorageChest> sc = new ArrayList<StorageChest>();
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			sc = ConvertHandler.convertListIII(plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST,
					"`id`", true,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		}
		if(dc.isEmpty() && sc.isEmpty())
		{
			return;
		}
		event.setCancelled(true);
		ArrayList<BaseComponent> bcI = new ArrayList<>();
		bcI.add(ChatApi.tctl(plugin.getYamlHandler().getL().getString("CmdAsh.BlockInfo.Dc")));
		for(DistributionChest dci : dc)
		{
			bcI.add(ChatApi.clickEvent("&6"+dci.getId()+"&f,&e"+dci.getChestName()+" ",
					ClickEvent.Action.RUN_COMMAND, 
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRun")
					.replace("%id%", String.valueOf(dci.getId()))));
		}
		ArrayList<BaseComponent> bcII = new ArrayList<>();
		bcII.add(ChatApi.tctl(plugin.getYamlHandler().getL().getString("CmdAsh.BlockInfo.Sc")));
		for(StorageChest sci : sc)
		{
			String color = "&a";
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sci.getDistributionChestID()))
			{
				color = "&4";
			}
			bcII.add(ChatApi.clickEvent("&6"+sci.getId()+"&f("+color+sci.getDistributionChestID()+"&f) ",
					ClickEvent.Action.RUN_COMMAND, 
					plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRun")
					.replace("%id%", String.valueOf(sci.getId()))));
		}
		TextComponent tcI = ChatApi.tc("");
		tcI.setExtra(bcI);
		TextComponent tcII = ChatApi.tc("");
		tcII.setExtra(bcII);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.BlockInfo.Headline")));
		player.spigot().sendMessage(tcI);
		player.spigot().sendMessage(tcII);
		return;
	}
}
