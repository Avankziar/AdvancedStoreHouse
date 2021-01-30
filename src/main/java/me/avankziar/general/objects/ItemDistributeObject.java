package main.java.me.avankziar.general.objects;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.DistributionHandler;
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
	
	public void chestDistribute(AdvancedStoreHouse plugin, Inventory inventory, 
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList,
			ItemStack[] cloneInvyL, ItemStack[] cloneInvyR, String server, boolean isRandom)
	{
		new BukkitRunnable()
		{
			ItemStack[] cloneInvL = cloneInvyL;
			ItemStack[] cloneInvR = cloneInvyR;
			int i = 0;
			int j = 0;
			int loop = PluginSettings.settings.getChestsPerTick();
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
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							debug("distribution Content is Empty");
							i = prioList.size();
							continue;
						}
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug("distribution block == null");
							i++;
							continue;
						}
						if(block.getState() == null)
						{
							debug("distribution block.State == null");
							i++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug("distribution not Container");
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
								debug("StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug("distribution cinv == null");
							i++;
							continue;
						}
						debug("distribution Normal Storage start i = "+i); //0
						cloneInvL = DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvL, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						cloneInvR = DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvR, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						i++;
					} else if(i >= prioList.size() && j < endList.size())
					{
						lj = true;
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							debug("distribution Content is Empty");
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
							debug("distribution block == null");
							j++;
							continue;
						}
						if(block.getState() == null)
						{
							debug("distribution block.State == null");
							j++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug("distribution not Container");
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
								debug("StorageChest is Distributionchest and on cooldown!");
								i++;
								continue;
							}
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug("distribution cinv == null");
							j++;
							continue;
						}
						debug("distribution EndStorage start j = "+j);
						cloneInvL = DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvL, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						cloneInvR = DistributionHandler.distribute(inventory, cinv, sc.getContents(), cloneInvR, false, isRandom,
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						j++;
					} else
					{
						debug("distribution not Removed Items set back");
						/*//FIXME Junk
						 * Must not be used! Because is be distributed from a event
						if(inventory instanceof DoubleChestInventory)
						{
							debug("distribution Removed in DCI");
							DoubleChestInventory dcinv = (DoubleChestInventory) inventory;
							dcinv.getLeftSide().setContents(cloneInvL);
							dcinv.getRightSide().setContents(cloneInvR);
						} else
						{
							debug("distribution Removed in else");
							inventory.setContents(cloneInvL);
						}*/
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
		}.runTaskTimer(plugin, 0L, 1L*PluginSettings.settings.getDelayedTicks());
	}
}
