package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.DistributionHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PermissionHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.general.objects.StorageChest.Type;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InteractHandler implements Listener
{
	private AdvancedStoreHouse plugin;
	private static HashMap<String, Long> cooldown;
	public static HashMap<Integer, Long> distributionCooldown;
	public static HashMap<Integer, Long> distributionCooldownStartTime;
	
	public InteractHandler(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
		cooldown = new HashMap<>();
		distributionCooldown = new HashMap<>();
		distributionCooldownStartTime = new HashMap<>();
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
		//TODO bis das per Spieler einstellbar ist
		if(event.getClickedBlock() != null)
		{
			if(event.getClickedBlock().getType() == Material.LEVER)
			{
				return;
			}
		}
		debug(event.getPlayer(), "=> Interact Beginn <= Action: "+event.getAction().toString());
		if(cooldown.containsKey(event.getPlayer().getName()))
		{
			if(cooldown.get(event.getPlayer().getName()) > System.currentTimeMillis())
			{
				debug(event.getPlayer(), "Cooldown");
				
				/*Player player = event.getPlayer();
				PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
				if(user == null)
				{
					debug(event.getPlayer(), "User == null");
					return;
				}
				if(user.getMode() != Mode.CONSTRUCT)
				{
					event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("Cooldown")));
				}*/
				return;
			} else
			{
				cooldown.replace(event.getPlayer().getName(), System.currentTimeMillis()+1000*1);
			}
		} else
		{
			cooldown.put(event.getPlayer().getName(), System.currentTimeMillis()+1000*1);
		}
		Player player = event.getPlayer();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			debug(event.getPlayer(), "User == null");
			return;
		}
		if(event.getClickedBlock() == null)
		{
			debug(event.getPlayer(), "ClickedBlock == null");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		if(event.isCancelled())
		{
			debug(event.getPlayer(), "Event isCancelled");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		if(event.getAction() == Action.PHYSICAL)
		{	
			debug(event.getPlayer(), "Action == Physical || ClickedBlock isNull == "+event.getClickedBlock());
			if(event.getClickedBlock() != null)
			{
				debug(event.getPlayer(), "Plate Start");
				buttonAndPlate(event, player, user);
			}
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		if(isSign(event.getClickedBlock().getType()))
		{
			sign(event, player, user);
			return;
		}
		if(plugin.getUtility().isButtonOrPlate(event.getClickedBlock().getType()))
		{
			buttonAndPlate(event, player, user);
			return;
		}
		InteractSubHandler irch = new InteractSubHandler();
		if(!player.isSneaking())
		{
			debug(event.getPlayer(), "!player.isSneacking");
			//Simple Access
			PluginUserHandler.cancelAction(player, user, user.getMode(), 
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("CancelAction"));
			irch.checkIfDistributionChest(event, player, user);
			return;
		}
		if(event.isCancelled())
		{
			return;
		}
		switch(user.getMode())
		{
		default:
			return;
		case CONSTRUCT:
			irch.simplifiedHandling(event, player, user);
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
		case UPDATESTORAGEITEMFILTERSET:
			irch.checkIfDistributionChest(event, player, user);
			return;
		case CREATEITEMFILTERSET:
			irch.checkIfDistributionChest(event, player, user);
			return;
		case CHANGEITEMFILTERSET:
			irch.checkIfDistributionChest(event, player, user);
			return;
		}
	}
	
	private void createDistributionChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode createDistributionChest");
		if(plugin.getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		event.setCancelled(true);
		Location loc = event.getClickedBlock().getLocation();
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
				amount , plugin.getYamlHandler().getConfig().getInt("maximumDistributionChest"), false))
		{
			debug(event.getPlayer(), "TooMany DistributionChest");
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.TooMany")));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.DISTRIBUTIONCHEST)+1;
		String name = String.valueOf(last);
		if(user.getDistributionChestName() != null)
		{
			name = user.getDistributionChestName();
		}
		DistributionChest dc = new DistributionChest(
				last, user.getUUID(), new ArrayList<String>(), System.currentTimeMillis(),
				name, true, PriorityType.SWITCH, 0,
				plugin.getYamlHandler().getConfig().getBoolean("IsAutomaticDistribution", false), false, 
				server,
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		plugin.getMysqlHandler().create(MysqlHandler.Type.DISTRIBUTIONCHEST, dc);
		last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.DISTRIBUTIONCHEST);
		dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		player.spigot().sendMessage(ChatApi.generateTextComponent(
				plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SetupDChest")
				.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.DC_INFO).replace(" ", "+"))
				.replace("%dc%", dc.getChestName())));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.FutherInstruction")
				.replace("%dc%", dc.getChestName())));
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Cancel.SetCancel")
						.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.CANCEL).replace(" ", "+"))));
		user.setDistributionChestID(dc.getId());
		user.setMode(PluginUser.Mode.CREATESTORAGE);
		PluginUserHandler.addUser(user);
		return;
	}
	
	//Muss nicht nach Owner oder Member nachgefragt werden, da hier erstellt wird.
	private void createStorageChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Methode createStorageChest");
		if(plugin.getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
		{
			debug(event.getPlayer(), "!(ClickedBlock.State instanceof Chest && Barrel)");
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, 
				"`distributionchestid` = ? AND `owner_uuid` = ?", user.getDistributionChestID(), user.getUUID());
		if(!PermissionHandler.canCreate(player, Utility.PERMCOUNTSTORAGECHEST+"*", Utility.PERMCOUNTSTORAGECHEST,
				amount , plugin.getYamlHandler().getConfig().getInt("maximumStorageChestPerDistributionChest"), false))
		{
			debug(event.getPlayer(), "TooMany StorageChest");
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.TooManyS")));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `owner_uuid` = ? AND"
				+ " `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(), user.getUUID(), 
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.StorageChestExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` <> ? AND `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				user.getDistributionChestID(),
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			StorageChest othersc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
					"`distributionchestid` <> ? AND `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							user.getDistributionChestID(), server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			DistributionChest otherdc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", othersc.getDistributionChestID());
			if(otherdc != null)
			{
				if(!otherdc.getOwneruuid().equals(player.getUniqueId().toString())
						&& !otherdc.getMemberList().contains(player.getUniqueId().toString()))
				{
					player.sendMessage(ChatApi.tl(
							plugin.getYamlHandler().getLang().getString("CmdAsh.Create.OtherStorageChestExistAndNoAccess")));
					return;
				}
			}
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.DistributionChestDontExist")
					.replace("%dc%", String.valueOf(user.getDistributionChestID()))));
			PluginUserHandler.cancelAction(player, user, user.getMode(), plugin.getYamlHandler().getLang().getString("CancelAction"));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", user.getDistributionChestID());
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
			return;
		}
		event.setCancelled(true);
		StorageChest sc = new StorageChest(0, user.getDistributionChestID(), user.getUUID(),
				user.getPriority(),
				System.currentTimeMillis(), Bukkit.createInventory(null, 6*9).getContents(), user.isEndStorage(),
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
				"unnamed", false, false, Type.LESSTHAN, 0, false, Type.LESSTHAN, 0, false, false);
		
		Location dcloc = new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
		Block block = event.getClickedBlock();
		Location sclocr = null;
		Location sclocl = null;
		ItemStack[] dinvContent = null;
		if(block.getState() != null)
		{
			if(block.getState() instanceof Container)
			{
				Container c = (Container) block.getState();
				Inventory inv = c.getInventory();
				dinvContent = inv.getContents();
				if(inv instanceof DoubleChestInventory)
				{
					DoubleChestInventory dcinv = (DoubleChestInventory) inv;
					sclocr = dcinv.getRightSide().getLocation();
					sclocl = dcinv.getLeftSide().getLocation();
					dinvContent = dcinv.getContents();
				}
			}
		}
		if(sclocr != null)
		{
			if(ChestHandler.isLocationsEquals(dcloc, sclocr))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SChestDontBeSameAsDChest")));
				return;
			}
		}
		if(sclocl != null)
		{
			if(ChestHandler.isLocationsEquals(dcloc, sclocl))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SChestDontBeSameAsDChest")));
				return;
			}
		}
		
		String name = "/";
		String id = "/";
		if(dc != null)
		{
			name = dc.getChestName();
			id = ""+dc.getId();
		}
		ItemStack isInHand = player.getInventory().getItemInMainHand();
		Material inHand = (isInHand == null) ? Material.AIR : isInHand.getType();
		if(inHand == Material.valueOf(
				AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.Creating")))
		{
			plugin.getMysqlHandler().create(MysqlHandler.Type.STORAGECHEST, sc);
			int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
			user.setStorageChestID(last);
			PluginUserHandler.addUser(user);
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SetupSChest")
							.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO).replace(" ", "+"))
							.replace("%sc%", String.valueOf(last))));
			Inventory gui = Bukkit.createInventory(null, 6*9, 
					plugin.getYamlHandler().getLang().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
					.replace("%p%", String.valueOf(sc.getPriorityNumber()))
					.replace("%name%", name)
					.replace("%dcid%", id)
					.replace("%id%", String.valueOf(sc.getId())));
			gui.setContents(sc.getContents());
			player.openInventory(gui);
		} else if(inHand == Material.valueOf
				(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.CreateDirectWithIFS")))
		{
			sc.setContents(user.getItemFilterSet().getContents());
			plugin.getMysqlHandler().create(MysqlHandler.Type.STORAGECHEST, sc);
			int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
			user.setStorageChestID(last);
			PluginUserHandler.addUser(user);
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SetupSChestIFS")
							.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO).replace(" ", "+"))
							.replace("%sc%", String.valueOf(last))));
		} else if(inHand == Material.valueOf
				(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Simple.CreateDirectWithChestContents")))
		{
			dinvContent = getIFSFromInventory(dinvContent);
			sc.setContents(dinvContent);
			plugin.getMysqlHandler().create(MysqlHandler.Type.STORAGECHEST, sc);
			int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
			user.setStorageChestID(last);
			PluginUserHandler.addUser(user);
			player.spigot().sendMessage(
					ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("CmdAsh.Create.SetupSChestInventory")
							.replace("%cmd%", PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO).replace(" ", "+"))
							.replace("%sc%", String.valueOf(last))));
		}
		return;
	}
	
	private ItemStack[] getIFSFromInventory(ItemStack[] content)
	{
		ItemStack[] ia = content.clone();
		for(int i = 0; i < content.length; i++)
		{
			ItemStack is = content[i];
			is.setAmount(1);
			ia[i] = is;
		}
		return ia;
	}
	
	@SuppressWarnings("deprecation")
	private void buttonAndPlate(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin ButtonAndPlate");
		Block blocks = event.getClickedBlock();
		if(blocks == null)
		{
			debug(event.getPlayer(), "Block == null");
			return;
		}
		if(plugin.getUtility().isNOTButtonOrPlate(blocks.getType()))
		{
			debug(event.getPlayer(), "Not a Button or Plate");
			return;
		}
		if(event.isCancelled())
		{
			return;
		}
		Location loc = blocks.getLocation();
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		
		final int radius = plugin.getYamlHandler().getConfig().getInt("ButtonPlateInteractRadius", 3);
		final int xmax = loc.getBlockX()+radius;
		final int xmin = loc.getBlockX()-radius;
		final int ymax = loc.getBlockY()+radius;
		final int ymin = loc.getBlockY()-radius;
		final int zmax = loc.getBlockZ()+radius;
		final int zmin = loc.getBlockZ()-radius;
		
		new BukkitRunnable()
		{
			int i = 0;
			ArrayList<DistributionChest> dclist = ConvertHandler.convertListII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, "`server` = ? AND `world` = ?"
							+ " AND `blockx` <= ? AND `blockx` >= ?"
							+ " AND `blocky` <= ? AND `blocky` >= ?"
							+ " AND `blockz` <= ? AND `blockz` >= ?",
							server, loc.getWorld().getName(),
							xmax, xmin,
							ymax, ymin,
							zmax, zmin));
			@Override
			public void run()
			{
				if(i >= dclist.size())
				{
					cancel();
					return;
				}
				DistributionChest dc = dclist.get(i);
				debug(player, "ButtonDc: "+dc.getChestName());
				if(ChestHandler.isDistributionChestOnCooldown(plugin, dc))
				{
					i++;
					debug(player, "ButtonDc: "+dc.getChestName()+" is on cooldown!");
					return;
				}
				World world = Bukkit.getWorld(dc.getWorld());
				if(world == null)
				{
					debug(player, "ButtonDc world == null");
					i++;
					return;
				}
				Block dcblock = new Location(world, dc.getBlockX(), dc.getBlockY(), dc.getBlockZ()).getBlock();
				if(dcblock == null)
				{
					debug(player, "ButtonDc block == null");
					i++;
					return;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "ButtonDc block.getstate == null");
					i++;
					return;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "ButtonDc block.getstate not a container");
					i++;
					return;
				}
				Inventory inventory = ((Container)dcblock.getState()).getInventory();
				if(inventory == null)
				{
					debug(player, "ButtonDc container inv == null");
					i++;
					return;
				}
				try
				{
					DistributionHandler.distributeStartVersionButton(server, loc, inventory);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}.runTaskTimer(plugin, 0L,
				1L*plugin.getYamlHandler().getConfig().getInt("DelayedChainTicks", 10));
	}
	
	private boolean isSign(Material mat)
	{
		if(mat == Material.ACACIA_SIGN || mat == Material.ACACIA_WALL_SIGN
				|| mat == Material.BIRCH_SIGN || mat == Material.BIRCH_WALL_SIGN
				|| mat == Material.OAK_SIGN || mat == Material.OAK_WALL_SIGN
				|| mat == Material.SPRUCE_SIGN || mat == Material.SPRUCE_WALL_SIGN
				|| mat == Material.DARK_OAK_SIGN || mat == Material.DARK_OAK_WALL_SIGN
				|| mat == Material.JUNGLE_SIGN || mat == Material.JUNGLE_WALL_SIGN
				|| mat == Material.CRIMSON_SIGN || mat == Material.CRIMSON_WALL_SIGN
				|| mat == Material.WARPED_SIGN || mat == Material.WARPED_WALL_SIGN)
		{
			return true;
		}
		return false;
	}
	
	private void sign(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		debug(event.getPlayer(), "=> Begin Lever");
		Block block = event.getClickedBlock();
		if(block == null)
		{
			debug(event.getPlayer(), "Block == null");
			return;
		}
		Sign sign = (Sign) block.getState();

		if(!sign.getLine(1).contains("[Lager]") && !sign.getLine(1).contains("[ASH]"))
		{
			return;
		}
		if(MatchApi.isInteger(ChatColor.stripColor(sign.getLine(2))) == false)
		{
			return;
		}
		int id = Integer.parseInt(ChatColor.stripColor(sign.getLine(2)));
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			return;
		}
		if(!sign.getLine(3).contains("SWITCH")
				&& !sign.getLine(3).contains("DISTRIBUTE")
				&& !sign.getLine(3).contains("VERTEILEN")
				&& !MatchApi.isInteger(ChatColor.stripColor(sign.getLine(3))))
		{
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", Integer.parseInt(sign.getLine(2)));
		if(dc == null)
		{
			return;
		}
		if(!dc.getServer().equals(PluginSettings.settings.getServer()))
		{
			return;
		}
		if(PluginSettings.settings.isLwc() && com.griefcraft.lwc.LWC.getInstance() != null)
		{
			com.griefcraft.lwc.LWC lwc = com.griefcraft.lwc.LWC.getInstance();
			if(lwc.findProtection(sign.getBlock()) != null)
			{
				if(!lwc.canAccessProtection(player, sign.getBlock()))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.LWC.NotAccess")));
					return;
				}
			}
		}
		if(sign.getLine(3).contains("SWITCH"))
		{
			if(dc.getPriorityType() != PriorityType.SWITCH)
			{
				dc.setPriorityType(PriorityType.SWITCH);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.SWITCH.SetSWITCH")
						.replace("%id%", String.valueOf(dc.getId()))
						.replace("%name%", dc.getChestName())));
			} else
			{
				if(dc.isNormalPriority())
				{
					dc.setNormalPriority(false);
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.SWITCH.SetDESC")
							.replace("%id%", String.valueOf(dc.getId()))
							.replace("%name%", dc.getChestName())));
				} else
				{
					dc.setNormalPriority(true);
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.SWITCH.SetAESC")
							.replace("%id%", String.valueOf(dc.getId()))
							.replace("%name%", dc.getChestName())));
				}
			}
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			return;
		} else if(sign.getLine(3).contains("DISTRIBUTE") || sign.getLine(3).contains("VERTEILEN"))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.DISTRIBUTE.Start")
					.replace("%id%", String.valueOf(dc.getId()))
					.replace("%name%", dc.getChestName())));
			DistributionHandler.distributeStartVersionRemoteTriggering(dc);
			return;
		} else if(MatchApi.isInteger(ChatColor.stripColor(sign.getLine(3))))
		{
			if(dc.getPriorityType() == PriorityType.SWITCH)
			{
				dc.setPriorityType(PriorityType.PLACE);
			}
			dc.setPriorityNumber(Integer.parseInt(ChatColor.stripColor(sign.getLine(3))));
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Sign.PLACE.SetPLACE")
					.replace("%prio%", ChatColor.stripColor(sign.getLine(3)))
					.replace("%id%", String.valueOf(dc.getId()))
					.replace("%name%", dc.getChestName())));
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			return;
		}
	}	
	
	private void blockInfo(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		final String server = plugin.getYamlHandler().getConfig().getString("Servername");
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
		if(plugin.getUtility().isNOTStoragechest(event.getClickedBlock().getState()))
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
		event.setCancelled(true);
		ArrayList<BaseComponent> bcI = new ArrayList<>();
		bcI.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Dc")));
		for(DistributionChest dci : dc)
		{
			bcI.add(ChatApi.clickEvent("&6"+dci.getId()+"&f,&e"+dci.getChestName()+" ",
					ClickEvent.Action.RUN_COMMAND, 
					plugin.getYamlHandler().getLang().getString("CmdAsh.DistributionChestList.CommandRun")
					.replace("%id%", String.valueOf(dci.getId()))));
		}
		ArrayList<BaseComponent> bcII = new ArrayList<>();
		bcII.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Sc")));
		for(StorageChest sci : sc)
		{
			String color = "&a";
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sci.getDistributionChestID()))
			{
				color = "&4";
			}
			bcII.add(ChatApi.clickEvent("&6"+sci.getId()+"&f("+color+sci.getDistributionChestID()+"&f) ",
					ClickEvent.Action.RUN_COMMAND, 
					plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.CommandRun")
					.replace("%id%", String.valueOf(sci.getId()))));
		}
		TextComponent tcI = ChatApi.tc("");
		tcI.setExtra(bcI);
		TextComponent tcII = ChatApi.tc("");
		tcII.setExtra(bcII);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
		player.spigot().sendMessage(tcI);
		player.spigot().sendMessage(tcII);
		return;
	}
}
