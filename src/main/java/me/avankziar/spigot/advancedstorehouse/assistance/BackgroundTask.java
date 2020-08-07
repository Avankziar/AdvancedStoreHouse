package main.java.me.avankziar.spigot.advancedstorehouse.assistance;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.ConvertHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class BackgroundTask 
{
	private AdvancedStoreHouse plugin;
	
	public BackgroundTask(AdvancedStoreHouse plugin) 
	{
		this.plugin = plugin;
		runTask();
	}
	
	private void runTask()
	{
		int schedular = plugin.getYamlHandler().get().getInt("TransferSchedularTimer", 15);
		String server = plugin.getYamlHandler().get().getString("Servername", "");
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				ArrayList<DistributionChest> dcList = new ArrayList<>();
				try
				{
					dcList = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`id`", false, "`automaticdistribution` = ?", true));
				} catch (IOException e) {}
				for(DistributionChest dc : dcList)
				{
					if(Bukkit.getWorld(dc.getWorld()) == null)
					{
						continue;
					}
					Location dcloc = new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
					Block dcblock = dcloc.getBlock();
					if(dcblock == null)
					{
						continue;
					}
					if(dcblock.getState() == null)
					{
						continue;
					}
					if(!(dcblock.getState() instanceof Chest)
							&& !(dcblock.getState() instanceof ShulkerBox)
							&& !(dcblock.getState() instanceof Barrel)
							&& !(dcblock.getState() instanceof Furnace)
							&& !(dcblock.getState() instanceof BlastFurnace)
							&& !(dcblock.getState() instanceof Smoker)
							&& !(dcblock.getState() instanceof Hopper)
							&& !(dcblock.getState() instanceof Dropper)
							&& !(dcblock.getState() instanceof Dispenser)
							&& !(dcblock.getState() instanceof BrewingStand)
							)
					{
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dc.getId());
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`distributionchestid` = ?", dc.getId());
						continue;
					}
					int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
					ArrayList<StorageChest> prioList = new ArrayList<>();
					ArrayList<StorageChest> endList = new ArrayList<>();
					try
					{
						prioList = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(
										MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
						endList = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(
										MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
					} catch (IOException e) {}
					
					//Verteilung Beginn
					if(!(dcblock.getState() instanceof Container))
					{
						continue;
					}
					Container dccontainer = (Container) dcblock.getState();
					Inventory inventory = dccontainer.getInventory();
					ItemStack[] cloneInv = inventory.getContents();
					//Normal Lager
					for(StorageChest sc : prioList)
					{
						if(ChestHandler.isContentEmpty(cloneInv))
						{
							break;
						}
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							continue;
						}
						if(block.getState() == null)
						{
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							continue;
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							continue;
						}
						cloneInv = ChestHandler.distribute(cinv, sc.getContents(), cloneInv, inventory, false);
					}
					//Endlager
					if(!ChestHandler.isContentEmpty(cloneInv))
					{
						for(StorageChest sc : endList)
						{
							if(ChestHandler.isContentEmpty(cloneInv))
							{
								break;
							}
							Block block = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(block == null)
							{
								continue;
							}
							if(block.getState() == null)
							{
								continue;
							}
							if(!(block.getState() instanceof Container))
							{
								continue;
							}
							Container container = (Container) block.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								continue;
							}
							cloneInv = ChestHandler.distribute(cinv, sc.getContents(), cloneInv, inventory, true);
						}
					}
					inventory.setContents(cloneInv);
				}
			}
		}.runTaskTimer(plugin, 20L*60, 20L*60*schedular);
	}
}
