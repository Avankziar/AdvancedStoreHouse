package main.java.me.avankziar.spigot.advancedstorehouse.eventhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.ItemDistributeObject;
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
				DoubleChestInventory dcInv = (DoubleChestInventory) event.getInventory();
				debug(player, "distribution == DoubleChestInv");
				dci = true;
				loc = ChestHandler.isDoubleChest(plugin, player, server, loc, dcInv);
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
		if(ChestHandler.isDistributionChestOnCooldown(plugin, dc))
		{
			debug(player, "Dc is already in distribution");
			return;
		}
		ArrayList<StorageChest> prioList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(),
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
		ArrayList<StorageChest> endList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(),
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
		int storagechestamount = prioList.size()+endList.size();
		Inventory inventory = event.getView().getTopInventory();
		ItemStack[] cloneInvL = null;
		ItemStack[] cloneInvR = null;
		
		if(ChestHandler.isContentEmpty(inventory.getContents()))
		{
			return;
		}
		
		ChestHandler.setDistributionChestOnCooldown(plugin, dc, storagechestamount, true);
		
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
		//PrioList und EndList
		ItemDistributeObject ido = new ItemDistributeObject(null, null);
		if(dc.isDistributeRandom())
		{
			int[] excludes = new int[0];
			ArrayList<StorageChest> clonePrioList = new ArrayList<>();
			for(int i = 0; i < prioList.size(); i++)
			{
				int n = ChestHandler.getRandomWithExclusion(new Random(), 0, prioList.size()-1, excludes);
				clonePrioList.add(prioList.get(n));
			}
			prioList = clonePrioList;
			int[] excludesEnd = new int[0];
			ArrayList<StorageChest> cloneEndList = new ArrayList<>();
			for(int i = 0; i < endList.size(); i++)
			{
				int n = ChestHandler.getRandomWithExclusion(new Random(), 0, endList.size()-1, excludesEnd);
				cloneEndList.add(endList.get(n));
			}
			endList = cloneEndList;
		}
		ido.chestDistribute(plugin, player, inventory, prioList, endList, cloneInvL, cloneInvR, dc.isDistributeRandom());
		long supposeCooldown = storagechestamount*plugin.getYamlHandler().get().getInt("DelayedTicks", 1)*20+10;
		
		//Kettenverteilung
		debug(player, "ChainDc distribution starts");
		//Kettekisten bestimmung
		final ArrayList<DistributionChest> chain = ChestHandler.getChainChest(plugin, player, prioList, endList, server);
		new BukkitRunnable()
		{
			
			int i = 0;
			@Override
			public void run()
			{
				if(i >= chain.size())
				{
					cancel();
					return;
				}
				DistributionChest dcc = chain.get(i);
				debug(player, "ChainDc: "+dcc.getChestName());
				if(ChestHandler.isDistributionChestOnCooldown(plugin, dcc))
				{
					debug(player, "Dcc is already in distribution");
					i++;
					return;
				}
				ArrayList<StorageChest> prioListc = new ArrayList<>();
				try
				{
					prioListc = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), false, server));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				ArrayList<StorageChest> endListc = new ArrayList<>();
				try
				{
					endListc = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), true, server));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				World world = Bukkit.getWorld(dcc.getWorld());
				if(world == null)
				{
					debug(player, "ChainDc world == null");
					i++;
					return;
				}
				Block dcblock = new Location(world, dcc.getBlockX(), dcc.getBlockY(), dcc.getBlockZ()).getBlock();
				if(dcblock == null)
				{
					debug(player, "ChainDc block == null");
					i++;
					return;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "ChainDc block.getstate == null");
					i++;
					return;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "ChainDc block.getstate not a container");
					i++;
					return;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(player, "ChainDc container inv == null");
					i++;
					return;
				}
				int storagechestamountc = prioListc.size()+endListc.size();
				ChestHandler.setDistributionChestOnCooldown(plugin, dcc, storagechestamountc, true);
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
					cloneInvRc = cloneInvLc;
					int j = 0;
					for(int i = 0; i < cloneInvLc.length; i++)
					{
						cloneInvRc[i] = null;
						j = i;
					}
					debug(player, "distribution Right side set all null | i = "+j);
				}
				
				ItemDistributeObject idoc = new ItemDistributeObject(null, null);
				if(dcc.isDistributeRandom())
				{
					int[] excludes = new int[0];
					ArrayList<StorageChest> clonePrioListc = new ArrayList<>();
					for(int i = 0; i < prioListc.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, prioListc.size()-1, excludes);
						clonePrioListc.add(prioListc.get(n));
					}
					prioListc = clonePrioListc;
					int[] excludesEnd = new int[0];
					ArrayList<StorageChest> cloneEndListc = new ArrayList<>();
					for(int i = 0; i < endListc.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, endListc.size()-1, excludesEnd);
						cloneEndListc.add(endListc.get(n));
					}
					endListc = cloneEndListc;
				}
				idoc.chestDistribute(plugin, player, inventoryc, prioListc, endListc, cloneInvLc, cloneInvRc, dcc.isDistributeRandom());
				i++;
			}
		}.runTaskTimer(plugin, supposeCooldown,
				1L*plugin.getYamlHandler().get().getInt("DelayedChainTicks", 10));
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
