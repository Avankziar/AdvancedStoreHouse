package main.java.me.avankziar.spigot.advancedstorehouse.handler;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.ConvertHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

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
		case CONSTRUCT:
			distributionStart(event, player, user);
			return;
		case NONE:
			distributionStart(event, player, user);
			return;
		case BLOCKINFO:
			//Hier passiert nix
			return;
		case CREATEDISTRIBUTIONCHEST:
			//Hier passiert nix
			return;
		case CREATESTORAGE:
			createStorageChest(event, player, user); //Update des ItemStack[]
			return;
		case UPDATESTORAGE:
			createStorageChest(event, player, user); //Update des ItemStack[], update anderer Werte im InteractHandler
			return;
		case UPDATESTORAGEITEMFILTERSET:
			createStorageChest(event, player, user); //Update des ItemStack[]
			return;
		case CREATEITEMFILTERSET:
			createItemFilterSet(event, player, user); //Erstellen spezieller ItemStack[]
			return;
		case CHANGEITEMFILTERSET:
			updateItemFilterSet(event, player, user); //Update von spezieller ItemStack[]
			return;
		case POSITIONUPDATEDISTRIBUTION:
			//Hier passiert nix
			return;
		case POSITIONUPDATESTORAGE:
			//Hier passiert nix
			return;
		}
	}
	
	//Wenn nur rechtsklickt auf kisten gemacht wurden. Verteilung start
	private void distributionStart(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
	{
		debug(player, "=> Begin Methode distributionStart");
		String server = plugin.getYamlHandler().get().getString("Servername");
		Location loc = event.getInventory().getLocation();
		if(loc == null | server == null)
		{
			return;
		}
		boolean dci = false;
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			debug(player, "distribution not found");
			if(event.getInventory() instanceof DoubleChestInventory)
			{
				debug(player, "distribution == DoubleChestInv");
				dci = true;
				loc = isDoubleChest(player, server, loc);
				if(loc == null)
				{
					debug(player, "Distributionchest dont exist: ");
					return;
				}
			} else
			{
				debug(player, "Distributionchest dont exist: "
						+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
				return;
			}
		} else
		{
			debug(player, "distribution found!");
			if(event.getInventory() instanceof DoubleChestInventory)
			{
				debug(player, "distribution == DoubleChestInv II");
				dci = true;
			}
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
		ArrayList<StorageChest> prioList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
		ArrayList<StorageChest> endList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
		Inventory inventory = event.getView().getTopInventory();
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
			Block block = new Location(Bukkit.getWorld(sc.getWorld()),
					sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
			if(block == null)
			{
				debug(player, "distribution block == null");
				continue;
			}
			if(block.getState() == null)
			{
				debug(player, "distribution block.State == null");
				continue;
			}
			if(!(block.getState() instanceof Container))
			{
				debug(player, "distribution not Container");
				continue;
			}
			Container container = (Container) block.getState();
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
		debug(player, "distribution EndStorage start");
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
				Block block = new Location(Bukkit.getWorld(sc.getWorld()),
						sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
				if(block == null)
				{
					debug(player, "distribution block == null");
					continue;
				}
				if(block.getState() == null)
				{
					debug(player, "distribution block.State == null");
					continue;
				}
				if(!(block.getState() instanceof Container))
				{
					debug(player, "distribution not Container");
					continue;
				}
				Container container = (Container) block.getState();
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
				debug(player, "Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug(player, "Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug(player, "Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(player, "Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					lo = isDoubleChest(player, server, lo);
					if(lo == null)
					{
						debug(player, "Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug(player, "Chain - II Adding: "+allAt.size());
				} else
				{
					debug(player, "ChainDc dont exist here");
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
				debug(player, "Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug(player, "Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug(player, "Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(player, "Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					lo = isDoubleChest(player, server, lo);
					if(lo == null)
					{
						debug(player, "Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug(player, "Chain - II Adding: "+allAt.size());
				} else
				{
					debug(player, "ChainDC dont exist here");
					continue;
				}
			}
		}
		//Verteilung der kette
		debug(player, "ChainDc distribution starts");
		for(DistributionChest dcc : chain)
		{
			debug(player, "ChainDc: "+dcc.getChestName());
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
				debug(player, "ChainDc world == null");
				continue;
			}
			Block dcblock = new Location(world, dcc.getBlockX(), dcc.getBlockY(), dcc.getBlockZ()).getBlock();
			if(dcblock == null)
			{
				debug(player, "ChainDc block == null");
				continue;
			}
			if(dcblock.getState() == null)
			{
				debug(player, "ChainDc block.getstate == null");
				continue;
			}
			if(!(dcblock.getState() instanceof Container))
			{
				debug(player, "ChainDc block.getstate not a container");
				continue;
			}
			Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
			if(inventoryc == null)
			{
				debug(player, "ChainDc container inv == null");
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
				Block block = new Location(Bukkit.getWorld(sc.getWorld()),
						sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
				if(block == null)
				{
					debug(player, "distribution block == null");
					continue;
				}
				if(block.getState() == null)
				{
					debug(player, "distribution block.State == null");
					continue;
				}
				if(!(block.getState() instanceof Container))
				{
					debug(player, "distribution not Container");
					continue;
				}
				Container container = (Container) block.getState();
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
					Block block = new Location(Bukkit.getWorld(sc.getWorld()),
							sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
					if(block == null)
					{
						debug(player, "distribution block == null");
						continue;
					}
					if(block.getState() == null)
					{
						debug(player, "distribution block.State == null");
						continue;
					}
					if(!(block.getState() instanceof Container))
					{
						debug(player, "distribution not Container");
						continue;
					}
					Container container = (Container) block.getState();
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
	
	//Nur eine Update für den ItemFilterSet der Lagerkiste
	private void createStorageChest(InventoryCloseEvent event, Player player, PluginUser user) throws IOException
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
			user.setMode(Mode.NONE);
			PluginUserHandler.addUser(user);
		}
		player.spigot().sendMessage(
				ChatApi.generateTextComponent(plugin.getYamlHandler().getL().getString("CmdAsh.Create.UpdateStorageChest")));
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
		user.setMode(Mode.NONE);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSet.Create")
				.replace("%name%", ifs.getName())));
		return;
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSet.NotExist")));
			return;
		}
		ItemFilterSet ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
				"`id` = ?", user.getItemFilterSet().getID());
		ifs.setContents(content);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.ITEMFILTERSET, ifs, "`id` = ?", user.getItemFilterSet().getID());
		user.setItemFilterSet(ifs);
		user.setMode(Mode.NONE);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSet.Update")
				.replace("%name%", ifs.getName())));
		return;
	}
}
