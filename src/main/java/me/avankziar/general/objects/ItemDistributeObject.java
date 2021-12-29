package main.java.me.avankziar.general.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.DistributionHandlerII;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ItemDistributeObject
{
	private ItemStack[] itemsRight;
	private ItemStack[] itemsLeft;
	private boolean finish = false;
	
	public ItemDistributeObject(ItemStack[] itemsRight, ItemStack[] itemsLeft)
	{
		setItemsRight(itemsRight);
		setItemsLeft(itemsLeft);
	}

	public ItemStack[] getItemsLeft()
	{
		return itemsLeft;
	}

	public void setItemsLeft(ItemStack[] itemsLeft)
	{
		this.itemsLeft = itemsLeft;
	}

	public ItemStack[] getItemsRight()
	{
		return itemsRight;
	}

	public void setItemsRight(ItemStack[] itemsRight)
	{
		this.itemsRight = itemsRight;
	}
	
	public boolean isFinish()
	{
		return finish;
	}

	public void setFinish(boolean finish)
	{
		this.finish = finish;
	}
	
	public static void debug(String s)
	{
		boolean bo = false;
		if(bo)
		{
			AdvancedStoreHouse.log.info(s);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}			
		}
	}
	
	public static void debug(int level, String s)
	{
		boolean bo = false;
		int l = -1;
		if(bo && level >= l
				)
		{
			AdvancedStoreHouse.log.info(s);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}
		}
	}
	
	/*@Deprecated
	public void chestDistribute(AdvancedStoreHouse plugin, Inventory inventory, 
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList,
			ItemStack[] cloneInvyL, ItemStack[] cloneInvyR, String server, boolean isRandom, String debug)
	{
		new BukkitRunnable()
		{
			ItemStack[] cloneInvL = cloneInvyL;
			ItemStack[] cloneInvR = cloneInvyR;
			int i = 0;
			int j = 0;
			int loop = PluginSettings.settings.getChestsPerTick()*PluginSettings.settings.getDelayedTicks();
			int loopi = loop-1;
			int loopj = loop-1;
			@Override
			public void run()
			{
				boolean li = false;
				boolean lj = false;
				while(i <= loopi && j <= loopj)
				{
					if(i < prioList.size())
					{
						li = true;
						StorageChest sc = prioList.get(i);
						//debug(true, debug+"distribution i: "+i+" | j: "+j);
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							debug(1, debug+"distribution Content is Empty");
							i = prioList.size();
							continue;
						}
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug(1, debug+"distribution block == null");
							i++;
							continue;
						}
						if(block.getState() == null)
						{
							debug(1, debug+"distribution block.State == null");
							i++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug(1, debug+"distribution not Container");
							i++;
							continue;
						}
						//ChainDc, is this already in Use, no distribution to this chest
						DistributionChest chaindc = null;
						try
						{
							chaindc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
						} catch (IOException e) {}
						if(chaindc != null)
						{
							if(ChestHandler.isDistributionChestOnCooldown(plugin, chaindc))
							{
								debug(1, debug+"StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(1, debug+"distribution cinv == null");
							i++;
							continue;
						}
						debug(debug+"distribution Normal Storage start i = "+i);
						cloneInvL = _DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvL, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						cloneInvR = _DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvR, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						i++;
					} else if(i >= prioList.size() && j < endList.size())
					{
						lj = true;
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							debug(debug+"distribution Content is Empty");
							setItemsLeft(cloneInvyL);
							setItemsRight(cloneInvyR);
							setFinish(true);
							j++;
							continue;
						}
						StorageChest sc = endList.get(j);
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug(debug+"distribution block == null");
							j++;
							continue;
						}
						if(block.getState() == null)
						{
							debug(debug+"distribution block.State == null");
							j++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug(debug+"distribution not Container");
							j++;
							continue;
						}
						//ChainDc, is this already in Use, no distribution to this chest
						DistributionChest chaindc = null;
						try
						{
							chaindc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
						} catch (IOException e) {}
						if(chaindc != null)
						{
							if(ChestHandler.isDistributionChestOnCooldown(plugin, chaindc))
							{
								debug(debug+"StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(debug+"distribution cinv == null");
							j++;
							continue;
						}
						debug(debug+"distribution EndStorage start j = "+j);
						cloneInvL = _DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvL, true, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						cloneInvR = _DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvR, true, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						j++;
					} else
					{
						debug(2, debug+"distribution not Removed Items set back");
						if(inventory instanceof DoubleChestInventory)
						{
							inventory.clear();
							for(ItemStack is : cloneInvL)
							{
								if(is != null)
								{
									inventory.addItem(is);
								}
							}
							for(ItemStack is : cloneInvR)
							{
								if(is != null)
								{
									inventory.addItem(is);
								}
							}
						} else
						{
							inventory.clear();
							for(ItemStack is : cloneInvL)
							{
								if(is != null)
								{
									inventory.addItem(is);
								}
							}
						}
						cancel();
						break;
					}
				}
				if(li)
				{
					loopi += loop;
				}
				if(lj)
				{
					loopj += loop;
				}
			}
		}.runTaskTimer(plugin, 1L, 1L*PluginSettings.settings.getDelayedTicks());
	}*/
	
	public void itemDistribute(AdvancedStoreHouse plugin, Inventory inventory,
			LinkedHashMap<Integer, ItemStack> map,
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList,
			String server, boolean isRandom, String debug)
	{
		debug(0, "priolist: "+prioList.size()+" | endlist: "+endList.size());
		new BukkitRunnable()
		{
			int i = 0;
			int j = 0;
			int loop = PluginSettings.settings.getChestsPerTick()*PluginSettings.settings.getDelayedTicks();
			int loopi = loop-1;
			int loopj = loop-1;
			boolean allDistributed = false;
			@Override
			public void run()
			{
				boolean li = false;
				boolean lj = false;
				while(i <= loopi && j <= loopj)
				{
					if(i < prioList.size())
					{
						li = true;
						StorageChest sc = prioList.get(i);
						debug(1, debug+"distribution normalchest "+i+" "+sc.getChestName());
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug(1, debug+"distribution block == null");
							i++;
							continue;
						}
						if(block.getState() == null)
						{
							debug(1, debug+"distribution block.State == null");
							i++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug(1, debug+"distribution not Container");
							i++;
							continue;
						}
						//ChainDc, is this already in Use, no distribution to this chest
						DistributionChest chaindc = null;
						try
						{
							chaindc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
						} catch (IOException e) {}
						if(chaindc != null)
						{
							if(ChestHandler.isDistributionChestOnCooldown(plugin, chaindc))
							{
								debug(1, debug+"StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(1, debug+"distribution cinv == null");
							i++;
							continue;
						}
						debug(1, debug+"distribution Normal Storage start i = "+i);
						allDistributed = DistributionHandlerII.distribute(inventory, cinv, sc.getContents(), map,
								sc.isEndstorage(), isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						if(allDistributed)
						{
							debug(1, debug+"distribution normal chest, all is distributed");
							cancel();
							return;
						}
						i++;
					} else if(i >= prioList.size() && j < endList.size())
					{
						lj = true;
						StorageChest sc = endList.get(j);
						debug(1, debug+"distribution endchest "+j+" "+sc.getChestName());
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug(1, debug+"distribution endchest block == null");
							j++;
							continue;
						}
						if(block.getState() == null)
						{
							debug(1, debug+"distribution endchest block.State == null");
							j++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug(1, debug+"distribution endchest not Container");
							j++;
							continue;
						}
						//ChainDc, is this already in Use, no distribution to this chest
						DistributionChest chaindc = null;
						try
						{
							chaindc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
						} catch (IOException e) {}
						if(chaindc != null)
						{
							if(ChestHandler.isDistributionChestOnCooldown(plugin, chaindc))
							{
								debug(1, debug+"endchest StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(1, debug+"distribution endchest cinv == null");
							j++;
							continue;
						}
						debug(1, debug+"distribution EndStorage start j = "+j);
						allDistributed = DistributionHandlerII.distribute(inventory, cinv, sc.getContents(), map,
								sc.isEndstorage(), isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						if(allDistributed)
						{
							debug(1, debug+"distribution endchest, all is distributed");
							cancel();
							return;
						}
						j++;
					} else
					{
						debug(2, debug + "distribution has through all StorageChest. Distribution finished!");
						cancel();
						break;
					}
				}
				if(li)
				{
					loopi += loop;
				}
				if(lj)
				{
					loopj += loop;
				}
			}
		}.runTaskTimer(plugin, 1L, 1L*PluginSettings.settings.getDelayedTicks());
	}
}
