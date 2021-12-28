package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractSubHandler
{	
	public static LinkedHashMap<Integer, Long> chestAnimationCooldown = new LinkedHashMap<>();
	
	public void simplifiedHandling(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		if(event.getClickedBlock() != null 
				&& AdvancedStoreHouse.getPlugin().getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
		{
			return;
		}
		ItemStack isInHand = player.getInventory().getItemInMainHand();
		Material inHand = (isInHand == null) ? Material.AIR : isInHand.getType();
		if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Simple.CopyAndPaste")))
		{
			//Mit einer Enderkiste (Ohne Shift) sollen Verteilerkisten kopiert werden können. 
			//Dabei wählt man vorher eine Verteilerkiste aus und mit dem klick kopiert man diese. (Nur für System mit weniger als x Kisten (LIMIT))
			enderchestHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Simple.Select")))
		{
			//Mit einem Fass (MIt shift) wird die Verteilerkiste, ohne Shift eine Lagerkiste ausgewählt.
			barrelHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Simple.Visuals")))
		{
			//Mit dem Schmelzofen (Ohne shift) auf eine bestehende Lagerkiste, wird das Itemfilterset aufgerufen. 
			//Mit Shift werden alle Zugehörigen Lagerkisten einer Verteilerkiste angezeigt. (Visuelle darstellung)
			blastfurnaceHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Simple.OpenOptionGUI")))
		{
			//Mit einem Ofen (ohne shift) wird eine GUI zu den Optioneinstellungen einer lagerkiste, 
			//(mit shift) das der Verteilerkisten aufgerufen.
			furnaceHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else if(inHand == Material.valueOf(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig().getString("Simple.Reposition")))
		{
			//Mit einem Räucherofen (ohne Shift) kann eine Lagerkiste versetzt werden. Mit Shift kann eine Verteilerkiste versetzt werden.
			smokerHandling(AdvancedStoreHouse.getPlugin(), event, player, user);
		} else
		{
			PluginUserHandler.cancelAction(player, user, user.getMode(), 
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("CancelAction"));
			checkIfDistributionChest(event, player, user);
			return;
		}
	}
	
	private void enderchestHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(plugin.getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		if(event.getClickedBlock().getState() instanceof Chest)
		{
			Chest chest = (Chest) event.getClickedBlock().getState();
			if(chest.getInventory() instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcinv = (DoubleChestInventory) chest.getInventory();
				Location left = dcinv.getLeftSide().getLocation();
				Location right = dcinv.getRightSide().getLocation();
				if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						server, left.getWorld().getName(), left.getBlockX(), left.getBlockY(), left.getBlockZ()))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DoubleChestAlreadyAsDistributionChestExist")));
					return;
				} else
				{
					if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, right.getWorld().getName(), right.getBlockX(), right.getBlockY(), right.getBlockZ()))
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DoubleChestAlreadyAsDistributionChestExist")));
						return;
					}
				}
			} else if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("ChestAlreadyAsDistributionChestExist")));
				return;
			}
		} else
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("ChestAlreadyAsDistributionChestExist")));
				return;
			}
		}
		int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST, 
				"`owner_uuid` = ?", user.getUUID());
		if(!PermissionHandler.canCreate(player, Utility.PERMCOUNTDISTRIBUTIONCHEST+"*", Utility.PERMCOUNTDISTRIBUTIONCHEST,
				amount , plugin.getYamlHandler().getConfig().getInt("MaximumDistributionChest"), false))
		{
			debug(event.getPlayer(), "TooMany DistributionChest");
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.TooManyDC")));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
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
		int maxCopy = plugin.getYamlHandler().getConfig().getInt("CopyPasteMaxStorageChest");
		if(countsc > maxCopy)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.TooManyToCopy")
					.replace("%mysccount%", String.valueOf(countsc))
					.replace("%copylimit%", String.valueOf(maxCopy))));
			return;
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.CopyAndPasteTaskRun")));
		final String newowner = player.getUniqueId().toString();
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
									"`id` ASC", "`distributionchestid` = ?", dc.getId()));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if(sclist == null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.CopyPaste.CopyAndPasteTaskRunFailed")));
					return;
				}
				final long createdate = System.currentTimeMillis();
				DistributionChest newOne = new DistributionChest(0, newowner, dc.getMemberList(),
						createdate, dc.getChestName()+"_Copy", dc.isNormalPriority(),
						dc.getPriorityType(), dc.getPriorityNumber(), dc.isAutomaticDistribution(), dc.isDistributeRandom(),
						dc.getServer(), loc.getWorld().getName(),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				plugin.getMysqlHandler().create(Type.DISTRIBUTIONCHEST, newOne);
				try
				{
					newOne = (DistributionChest) plugin.getMysqlHandler().getData(Type.DISTRIBUTIONCHEST, "`creationdate` = ?", createdate);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				for(StorageChest sc : sclist)
				{
					sc.setDistributionChestID(newOne.getId());
					sc.setOwneruuid(newowner);
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
			ArrayList<DistributionChest> dclist = new ArrayList<>();
			try
			{
				dclist = ConvertHandler.convertListII(
						plugin.getMysqlHandler().getAllListAt(Type.DISTRIBUTIONCHEST, "`id` ASC",
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if(dclist.isEmpty())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(dclist.size() == 1)
			{
				DistributionChest dc = dclist.get(0);
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
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectDC")
						.replace("%iddc%", String.valueOf(dc.getId()))
						.replace("%name%", dc.getChestName())));
				PluginUserHandler.addUser(user);
			} else
			{
				List<BaseComponent> dcbc = new ArrayList<>();
				for(DistributionChest dc : dclist)
				{
					TextComponent t1 = ChatApi.apiChat("&f"+dc.getId()+"-&e"+dc.getChestName(),
							ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)+dc.getChestName(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
					TextComponent t2 = ChatApi.apiChat("&aⒾ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.DC_INFO)+dc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
					TextComponent t3 = ChatApi.apiChat("&bⓄ ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.DC_OPENOPTION)+dc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
					TextComponent t4 = ChatApi.tctl(" &1| ");
					dcbc.add(t1);
					dcbc.add(t2);
					dcbc.add(t3);
					dcbc.add(t4);
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
				if(!dcbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.DistributionChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(dcbc);
					player.spigot().sendMessage(tc);
				}
			}
		} else
		{
			ArrayList<StorageChest> sclist = new ArrayList<>();
			try
			{
				sclist = ConvertHandler.convertListIII(
						plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST, "`id` ASC",
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if(sclist.isEmpty())
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
				user.addSelectedStorageChest(sclist.get(0).getId());
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectSC")
						.replace("%idsc%", String.valueOf(sclist.get(0).getId()))
						.replace("%namesc%", sclist.get(0).getChestName())
						.replace("%iddc%", String.valueOf(dc.getId()))
						.replace("%namedc%", dc.getChestName())));
				PluginUserHandler.addUser(user);
			} else
			{
				List<BaseComponent> scbc = new ArrayList<>();
				for(StorageChest sc : sclist)
				{
					TextComponent t1 = ChatApi.apiChat("&f"+sc.getId()+"-&e"+sc.getChestName(),
							ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
					TextComponent t2 = ChatApi.apiChat("&aⒾ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
					TextComponent t3 = ChatApi.apiChat("&bⓄ ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
					TextComponent t4 = ChatApi.tctl(" &1| ");
					scbc.add(t1);
					scbc.add(t2);
					scbc.add(t3);
					scbc.add(t4);
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
				if(!scbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.StorageChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(scbc);
					player.spigot().sendMessage(tc);
				}
			}
		}
	}
	
	private void blastfurnaceHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		DistributionChest dc = ChestHandler.getDistributionChest(plugin, loc);
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
			return;
		}
		animation(plugin, player, dc);
	}
	
	public void animation(AdvancedStoreHouse plugin, Player player, DistributionChest dc) throws IOException
	{
		if(chestAnimationCooldown.containsKey(dc.getId()))
		{
			if(chestAnimationCooldown.get(dc.getId()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.AnimationCooldown"))
						.replace("%dcid%", String.valueOf(dc.getId()))
						.replace("%dcname%", dc.getChestName()));
				return;
			}
		}
		if(!PermissionHandler.canViewIFSOrVisual(player, dc))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
			return;
		}
		ArrayList<StorageChest> sclist = ConvertHandler.convertListIII(plugin.getMysqlHandler()
				.getAllListAt(Type.STORAGECHEST, "`id` ASC", "`distributionchestid` = ?", dc.getId()));
		long cooldown = System.currentTimeMillis()
				+ plugin.getYamlHandler().getConfig().getLong("Animation.AdditionalCooldown", 10000)
				+ (sclist.size()/(plugin.getYamlHandler().getConfig().getInt("Animation.PerTick", 25)*20)*1000)
				+ plugin.getYamlHandler().getConfig().getLong("Animation.Lenght", 10000);
		if(chestAnimationCooldown.containsKey(dc.getId()))
		{
			chestAnimationCooldown.replace(dc.getId(), cooldown);
		} else
		{
			chestAnimationCooldown.put(dc.getId(), cooldown);
		}
		final int wait = 5;
		new BukkitRunnable()
		{
			int count = 0;
			int countGlobal = 0;
			int animationPerTick = plugin.getYamlHandler().getConfig().getInt("Animation.PerTick", 25);
			int animationLenght = plugin.getYamlHandler().getConfig().getInt("Animation.Lenght", 10000);
			Particle particledc = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.DistributionChest", "WATER_DROP"));
			Particle particledcrandom = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.RandomDistributionChest", "END_ROD"));
			Particle particlesc = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.StorageChest", "COMPOSTER"));
			Particle particlescvoid = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.VoidStorageChest", "FLAME"));
			Particle particlescend = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.EndStorageChest", "SMOKE_LARGE"));
			
			@Override
			public void run()
			{
				if(countGlobal == 0)
				{
					//Only DistributionChest
					if(dc.isDistributeRandom())
					{
						new ChestAnimation(ChestHandler.getLocationDistributionChest(dc))
						.startSingleChestAnimation(animationLenght+(1+sclist.size()/(wait*animationPerTick)), particledcrandom);
					} else
					{
						new ChestAnimation(ChestHandler.getLocationDistributionChest(dc))
						.startSingleChestAnimation(animationLenght+(1+sclist.size()/(wait*animationPerTick)), particledc);
					}
				}
				if(sclist.size() == 0)
				{
					cancel();
					return;
				}
				while(countGlobal < sclist.size())
				{
					int rest = (count == 0 ? 1 : count) % (animationPerTick*wait);
					if(rest == 0)
					{
						count = 0;
						break;
					}
					StorageChest sc = sclist.get(countGlobal);
					debug(player, "countGlobal: "+countGlobal+" | count: "+count+" | sc.list.sizee: "+sclist.size());
					if(sc.isOptionVoid())
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlescvoid);
					} else if(sc.isEndstorage())
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlescend);
					} else
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlesc);
					}
					count++;
					countGlobal++;
				}
				count = 0;
				if(countGlobal >= (sclist.size()-1))
				{
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 1L*wait);
	}
	
	public void animation(AdvancedStoreHouse plugin, Player player, DistributionChest dc, ArrayList<StorageChest> sclist) throws IOException
	{
		if(chestAnimationCooldown.containsKey(dc.getId()))
		{
			if(chestAnimationCooldown.get(dc.getId()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.AnimationCooldown"))
						.replace("%dcid%", String.valueOf(dc.getId()))
						.replace("%dcname%", dc.getChestName()));
				return;
			}
		}
		if(!PermissionHandler.canViewIFSOrVisual(player, dc))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.NoPermission")));
			return;
		}
		long cooldown = System.currentTimeMillis()
				+ plugin.getYamlHandler().getConfig().getLong("Animation.AdditionalCooldown", 10000)
				+ (sclist.size()/(plugin.getYamlHandler().getConfig().getInt("Animation.PerTick", 25)*20)*1000)
				+ plugin.getYamlHandler().getConfig().getLong("Animation.Lenght", 10000);
		if(chestAnimationCooldown.containsKey(dc.getId()))
		{
			chestAnimationCooldown.replace(dc.getId(), cooldown);
		} else
		{
			chestAnimationCooldown.put(dc.getId(), cooldown);
		}
		final int wait = 5;
		new BukkitRunnable()
		{
			int count = 0;
			int countGlobal = 0;
			int animationPerTick = plugin.getYamlHandler().getConfig().getInt("Animation.PerTick", 25);
			int animationLenght = plugin.getYamlHandler().getConfig().getInt("Animation.Lenght", 10000);
			Particle particledc = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.DistributionChest", "WATER_DROP"));
			Particle particledcrandom = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.RandomDistributionChest", "END_ROD"));
			Particle particlesc = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.StorageChest", "COMPOSTER"));
			Particle particlescvoid = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.VoidStorageChest", "FLAME"));
			Particle particlescend = 
					Particle.valueOf(plugin.getYamlHandler().getConfig().getString("Animation.Particle.EndStorageChest", "SMOKE_LARGE"));
			
			@Override
			public void run()
			{
				if(countGlobal == 0)
				{
					//Only DistributionChest
					if(dc.isDistributeRandom())
					{
						new ChestAnimation(ChestHandler.getLocationDistributionChest(dc))
						.startSingleChestAnimation(animationLenght+(1+sclist.size()/(wait*animationPerTick)), particledcrandom);
					} else
					{
						new ChestAnimation(ChestHandler.getLocationDistributionChest(dc))
						.startSingleChestAnimation(animationLenght+(1+sclist.size()/(wait*animationPerTick)), particledc);
					}
				}
				if(sclist.size() == 0)
				{
					cancel();
					return;
				}
				while(countGlobal < sclist.size())
				{
					int rest = (count == 0 ? 1 : count) % (animationPerTick*wait);
					if(rest == 0)
					{
						count = 0;
						break;
					}
					StorageChest sc = sclist.get(countGlobal);
					debug(player, "countGlobal: "+countGlobal+" | count: "+count+" | sc.list.sizee: "+sclist.size());
					if(sc.isOptionVoid())
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlescvoid);
					} else if(sc.isEndstorage())
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlescend);
					} else
					{
						new ChestAnimation(ChestHandler.getLocationStorageChest(sc))
						.startSingleChestAnimation(animationLenght, particlesc);
					}
					count++;
					countGlobal++;
				}
				count = 0;
				if(countGlobal >= (sclist.size()-1))
				{
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 1L*wait);
	}
	
	private void furnaceHandling(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		final Location loc = event.getClickedBlock().getLocation();
		event.setCancelled(true);
		if(player.isSneaking())
		{
			ArrayList<DistributionChest> dclist = new ArrayList<>();
			try
			{
				dclist = ConvertHandler.convertListII(
						plugin.getMysqlHandler().getAllListAt(Type.DISTRIBUTIONCHEST, "`id` ASC",
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if(dclist.isEmpty())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.DcDontExist")));
				return;
			}
			if(dclist.size() == 1)
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
				user.setDistributionChestID(dc.getId());
				PluginUserHandler.addUser(user);
				new OptionGuiHandler().openDcGuiMain(player, user, dc, null);
			} else
			{
				List<BaseComponent> dcbc = new ArrayList<>();
				for(DistributionChest dc : dclist)
				{
					TextComponent t1 = ChatApi.apiChat("&f"+dc.getId()+"-&e"+dc.getChestName(),
							ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)+dc.getChestName(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
					TextComponent t2 = ChatApi.apiChat("&aⒾ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.DC_INFO)+dc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
					TextComponent t3 = ChatApi.apiChat("&bⓄ ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.DC_OPENOPTION)+dc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
					TextComponent t4 = ChatApi.tctl(" &1| ");
					dcbc.add(t1);
					dcbc.add(t2);
					dcbc.add(t3);
					dcbc.add(t4);
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
				if(!dcbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.DistributionChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(dcbc);
					player.spigot().sendMessage(tc);
				}
			}
		} else
		{
			ArrayList<StorageChest> sclist = new ArrayList<>();
			try
			{
				sclist = ConvertHandler.convertListIII(
						plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST, "`id` ASC",
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if(sclist.isEmpty())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScsDontExist")));
				return;
			}
			if(sclist.size() == 1)
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
				user.setStorageChestID(sc.getId());
				PluginUserHandler.addUser(user);
				new OptionGuiHandler().openScGuiMain(player, user, sc, null);
			} else
			{
				List<BaseComponent> scbc = new ArrayList<>();
				for(StorageChest sc : sclist)
				{
					TextComponent t1 = ChatApi.apiChat("&f"+sc.getId()+"-&e"+sc.getChestName(),
							ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
					TextComponent t2 = ChatApi.apiChat("&aⒾ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
					TextComponent t3 = ChatApi.apiChat("&bⓄ ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION)+sc.getId(),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
					TextComponent t4 = ChatApi.tctl(" &1| ");
					scbc.add(t1);
					scbc.add(t2);
					scbc.add(t3);
					scbc.add(t4);
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
				if(!scbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.StorageChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(scbc);
					player.spigot().sendMessage(tc);
				}
			}
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
	
	/* REMOVEME Veraltet und nicht zielführend
	 * private void updateStorageChestItemFilterSet(AdvancedStoreHouse plugin, PlayerInteractEvent event, Player player, PluginUser user)
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.DistributionChestDontExistsNone")
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
				ChatApi.tl(plugin.getYamlHandler().getLang().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriorityNumber()))
				.replace("%e%", ItemGenerator.getColor(sc.isEndstorage()))
				.replace("%dcname%", name)
				.replace("%dcid%", id)
				.replace("%scname%", sc.getChestName())
				.replace("%scid%", String.valueOf(sc.getId()))));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}*/
	
	/*
	 * Methode is for check, if the block is a dc.
	 * If it a dc, and the cooldown is on, it denying the access.
	 */
	public void checkIfDistributionChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		Block block = event.getClickedBlock();
		if(block == null)
		{
			return;
		}
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
			if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
			{
				long dcc = InteractHandler.distributionCooldown.get(dc.getId());
				long start = InteractHandler.distributionCooldownStartTime.get(dc.getId());
				debug(player, "Cooldown: "+dcc+" | Milli: "+System.currentTimeMillis());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("DistributionIsRunning")
						.replace("%start%", TimeHandler.getTime(start))
						.replace("%time%", TimeHandler.getTime(dcc))));
			}
		}
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			if(player != null)
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}
			System.out.println(s);
		}
	}
}
