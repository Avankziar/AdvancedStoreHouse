package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PermissionHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestAnimation;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractSubHandler
{	
	public void simplifiedHandling(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		ItemStack isInHand = player.getInventory().getItemInMainHand();
		Material inHand = (isInHand == null) ? Material.AIR : isInHand.getType();
		if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.CopyAndPaste")))
		{
			//Mit einer Enderkiste (Ohne Shift) sollen Verteilerkisten kopiert werden können. 
			//Dabei wählt man vorher eine Verteilerkiste aus und mit dem klick kopiert man diese. (Nur für System mit weniger als x Kisten (LIMIT))
			enderchestHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.Select")))
		{
			//Mit einem Fass (MIt shift) wird die Verteilerkiste, ohne Shift eine Lagerkiste ausgewählt.
			barrelHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.OpenIFSAndVisuals")))
		{
			//Mit dem Schmelzofen (Ohne shift) auf eine bestehende Lagerkiste, wird das Itemfilterset aufgerufen. 
			//Mit Shift werden alle Zugehörigen Lagerkisten einer Verteilerkiste angezeigt. (Visuelle darstellung)
			blastfurnaceHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.OpenOptionGUI")))
		{
			//Mit einem Ofen (ohne shift) wird eine GUI zu den Optioneinstellungen einer lagerkiste, 
			//(mit shift) das der Verteilerkisten aufgerufen.
			furnaceHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.Reposition")))
		{
			//Mit einem Räucherofen (ohne Shift) kann eine Lagerkiste versetzt werden. Mit Shift kann eine Verteilerkiste versetzt werden.
			smokerHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else
		{
			//Egal welches Item
			return;
		}
	}
	
	private void enderchestHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(user.getDistributionChestID() == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcNotSelected")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
			return;
		}
		if(!PermissionHandler.canCopyAndPaste(player, dc))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
			return;
		}
		int countsc = plugin.getMysqlHandler().countWhereID(Type.STORAGECHEST, "`distributionchestid` = ?", dc.getId());
		if(countsc > plugin.getYamlHandler().getConfig().getInt("CopyPasteMaxStorageChest"))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.TooManyToCopy")));
			return;
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.CopyAndPasteTaskRun")));
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				ArrayList<StorageChest> sclist = null;
				try
				{
					sclist = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST,
									"`id`", false, "`distributionchestid` = ?", dc.getId()));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if(sclist == null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.CopyAndPasteTaskRunFailed")));
					return;
				}
				DistributionChest newOne = new DistributionChest(0, dc.getOwneruuid(), dc.getMemberList(),
						System.currentTimeMillis(), dc.getChestName()+"_Copy", dc.isNormalPriority(),
						dc.getPriorityType(), dc.getPriorityNumber(), dc.isAutomaticDistribution(), dc.isDistributeRandom(),
						dc.getServer(), loc.getWorld().getName(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				plugin.getMysqlHandler().create(Type.DISTRIBUTIONCHEST, newOne);
				for(StorageChest sc : sclist)
				{
					plugin.getMysqlHandler().create(Type.STORAGECHEST, sc);
				}
				if(player != null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.CopyAndPasteTaskRunSuccess")
							.replace("%count%", String.valueOf(sclist.size()))));
				}
				return;
			}
		}.runTaskAsynchronously(plugin);
	}
	
	private void barrelHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(player.isSneaking())
		{
			DistributionChest dc = ChestHandler.getDistributionChest(plugin, loc);
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(!PermissionHandler.canSelect(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
				return;
			}
			user.setDistributionChestID(dc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectDChest")
					.replace("%iddc%", String.valueOf(dc.getId()))
					.replace("%name%", dc.getChestName())));
			PluginUserHandler.addUser(user);
		} else
		{
			ArrayList<StorageChest> sclist = ConvertHandler.convertListIII(plugin.getMysqlHandler().getAllListAt(
					MysqlHandler.Type.STORAGECHEST, "`id`", false,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			if(sclist == null || sclist.isEmpty())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScsDontExist")));
				return;
			}
			if(sclist.size() == 1)
			{
				DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
						Type.DISTRIBUTIONCHEST, "`id` = ?", sclist.get(0).getDistributionChestID());
				if(dc == null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDcDontExist")));
					return;
				}
				if(!PermissionHandler.canSelect(player, dc))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Select.NoPermission")));
					return;
				}
				user.setStorageChestID(sclist.get(0).getId());
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectSChest")
						.replace("%idsc%", String.valueOf(sclist.get(0).getId()))
						.replace("%iddc%", String.valueOf(dc.getId()))
						.replace("%name%", dc.getChestName())));
				PluginUserHandler.addUser(user);
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Search.SelectScsHeadline")));
				ArrayList<BaseComponent> s = new ArrayList<>();
				for(StorageChest sc : sclist)
				{
					TextComponent tc = ChatApi.clickEvent("&f"+sc.getId()+"&e"+sc.getChestName(),
							Action.SUGGEST_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getId());
					s.add(tc);
					s.add(ChatApi.tctl("&1 | "));
				}
				TextComponent all = ChatApi.tc("");
				all.setExtra(s);
				player.spigot().sendMessage(all);
			}
		}
	}
	
	private void blastfurnaceHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(player.isSneaking())
		{
			DistributionChest dc = ChestHandler.getDistributionChest(plugin, loc);
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(!PermissionHandler.canViewIFSOrVisual(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
				return;
			}
			ArrayList<StorageChest> sclist = ConvertHandler.convertListIII(plugin.getMysqlHandler().getList(Type.STORAGECHEST,
					"`id`", false, user.getNumberScForParticel(), PluginSettings.settings.getStorageChestAmountWhereShowParticels(),
					"`distributionchestid` = ?", dc.getId()));
			for(StorageChest sc : sclist)
			{
				new ChestAnimation(ChestHandler.getLocationStorageChest(sc)).startSingleChestAnimation();
			}
		} else
		{
			updateStorageChestItemFilterSet(plugin, event, player, user);
		}
	}
	
	private void furnaceHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(player.isSneaking())
		{
			DistributionChest dc = ChestHandler.getDistributionChest(plugin, loc);
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(!PermissionHandler.canOpenOption(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
				return;
			}
			new OptionGuiHandler().openDcGuiMain(player, user, dc, null);
		} else
		{
			StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if(sc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDontExist")));
				return;
			}
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
					Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID());
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDcDontExist")));
				return;
			}
			if(!PermissionHandler.canOpenOption(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
				return;
			}
			new OptionGuiHandler().openScGuiMain(player, user, sc, null);
		}
	}
	
	private void smokerHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final Location newloc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(player.isSneaking())
		{
			if(user.getDistributionChestID() == 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcNotSelected")));
				return;
			}
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
					Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(!PermissionHandler.canReposition(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Reposition.NoPermission")));
				return;
			}
			dc.setWorld(newloc.getWorld().getName());
			dc.setBlockX(newloc.getBlockX());
			dc.setBlockY(newloc.getBlockY());
			dc.setBlockZ(newloc.getBlockZ());
			plugin.getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Reposition.DcIsReposition")
					.replace("%dcname%", dc.getChestName())
					.replace("%dcid%", String.valueOf(dc.getId()))));
			return;
		} else
		{
			if(user.getStorageChestID() == 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScNotSelected")));
				return;
			}
			StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
			if(sc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDontExist")));
				return;
			}
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
					Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID());
			if(dc == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDcDontExist")));
				return;
			}
			if(!PermissionHandler.canReposition(player, dc))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Reposition.NoPermission")));
				return;
			}
			sc.setWorld(newloc.getWorld().getName());
			sc.setBlockX(newloc.getBlockX());
			sc.setBlockY(newloc.getBlockY());
			sc.setBlockZ(newloc.getBlockZ());
			plugin.getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Reposition.ScIsReposition")
					.replace("%scname%", sc.getChestName())
					.replace("%scid%", String.valueOf(sc.getId()))));
			return;
		}
	}
	
	private void updateStorageChestItemFilterSet(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user)
			throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode updateStorageChestItemFilterSet");
		if(plugin.getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		Location loc = event.getClickedBlock().getLocation();
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
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
					DoubleChestInventory dcInv = (DoubleChestInventory) inv;
					l = ChestHandler.isDoubleChest(plugin, server, l, dcInv);
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.DistributionChestDontExistNone")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Update.MayDeleteNone")));
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
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
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
				plugin.getYamlHandler().getLang().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriorityNumber()))
				.replace("%name%", name)
				.replace("%dcid%", id)
				.replace("%id%", String.valueOf(sc.getId())));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}
	
	/*
	 * Methode is for check, if the block is a dc.
	 * If it a dc, and the cooldown is on, it denying the access.
	 */
	public void checkIfDistributionChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		Block block = event.getClickedBlock();
		if(block.getState() == null)
		{
			debug(event.getPlayer(), "Block.State == null");
			return;
		}
		if(!(block.getState() instanceof Container))
		{
			debug(event.getPlayer(), "Block != Container | Type: "+block.getType().toString());
			return;
		}
		String server = AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		if(loc == null)
		{
			debug(event.getPlayer(), "Location == null");
			return;
		}
		DistributionChest dc = ChestHandler.getDistributionChest(AdvancedStoreHouse.getPlugin(), loc);
		if(dc == null)
		{
			debug(event.getPlayer(), "Block == Container | Type: "+block.getType().toString());
			debug(player, "Distributionchest dont find, search: "
					+server+" "+block.getLocation().getWorld().getName()+" "+block.getLocation().getBlockX()+" "+
					block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ());
			Container c = (Container) block.getState();
			Inventory inv = c.getInventory();
			if(inv instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcInv = (DoubleChestInventory) inv;
				debug(player, "distribution == DoubleChestInv");
				loc = ChestHandler.isDoubleChest(AdvancedStoreHouse.getPlugin(), server, loc, dcInv);
				if(loc == null)
				{
					debug(player, "Distributionchest dont exist: ");
					return;
				}
				dc = (DistributionChest) ChestHandler.getDistributionChest(AdvancedStoreHouse.getPlugin(), loc);
			} else
			{
				debug(player, "Distributionchest dont exist: "
						+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
				return;
			}
		}
		if(dc != null)
		{
			if(InteractHandler.distributionCooldown.containsKey(dc.getId()))
			{
				long dcc = InteractHandler.distributionCooldown.get(dc.getId());
				long start = InteractHandler.distributionCooldownStartTime.get(dc.getId());
				debug(player, "Cooldown: "+dcc+" | Milli: "+System.currentTimeMillis());
				if(dcc > System.currentTimeMillis())
				{
					event.setCancelled(true);
					player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("DistributionIsRunning")
							.replace("%start%", TimeHandler.getTime(start))
							.replace("%time%", TimeHandler.getTime(dcc))));
				}
			}
		}
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
}