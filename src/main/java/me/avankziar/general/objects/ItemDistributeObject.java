package main.java.me.avankziar.general.objects;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;

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
	
	private void debug(Player player, String s)
	{
		boolean bo = true;
		if(bo)
		{
			if(player != null)
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}
		}
	}
	
	public void chestDistribute(AdvancedStoreHouse plugin, Player player, Inventory inventory, 
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList,
			ItemStack[] cloneInvyL, ItemStack[] cloneInvyR)
	{
		if(plugin.getYamlHandler().get().getBoolean("UseDelayedDistribution", true))
		{
			if(plugin.getYamlHandler().get().getBoolean("UseFastDelayedDistribution", true))
			{
				new BukkitRunnable()
				{
					ItemStack[] cloneInvL = cloneInvyL;
					ItemStack[] cloneInvR = cloneInvyR;
					int i = 0;
					int j = 0;
					int loop = plugin.getYamlHandler().get().getInt("ChestsPerTick", 10);
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
									debug(player, "distribution Content is Empty");
									i = prioList.size();
									continue;
								}
								Block block = new Location(Bukkit.getWorld(sc.getWorld()),
										sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
								if(block == null)
								{
									debug(player, "distribution block == null");
									i++;
									continue;
								}
								if(block.getState() == null)
								{
									debug(player, "distribution block.State == null");
									i++;
									continue;
								}
								if(!(block.getState() instanceof Container))
								{
									debug(player, "distribution not Container");
									i++;
									continue;
								}
								Container container = (Container) block.getState();
								Inventory cinv = container.getInventory();
								if(cinv == null)
								{
									debug(player, "distribution cinv == null");
									i++;
									continue;
								}
								debug(player, "distribution Normal Storage start i = "+i); //0
								cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, false);
								cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, false);
								i++;
							} else if(i >= prioList.size() && j < endList.size())
							{
								lj = true;
								if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
								{
									debug(player, "distribution Content is Empty");
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
									debug(player, "distribution block == null");
									j++;
									continue;
								}
								if(block.getState() == null)
								{
									debug(player, "distribution block.State == null");
									j++;
									continue;
								}
								if(!(block.getState() instanceof Container))
								{
									debug(player, "distribution not Container");
									j++;
									continue;
								}
								Container container = (Container) block.getState();
								Inventory cinv = container.getInventory();
								if(cinv == null)
								{
									debug(player, "distribution cinv == null");
									j++;
									continue;
								}
								debug(player, "distribution EndStorage start j = "+j);
								cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, true);
								cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, true);
								j++;
							} else
							{
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
								cancel();
								break;
							}
						}
						if(li)
						{
							loopi = loopi + loop;
						}
						if(lj)
						{
							loopj = loopj + loop;
						}
					}
				}.runTaskTimer(plugin, 0L, 1L*plugin.getYamlHandler().get().getInt("DelayedTicks", 1));
			} else
			{
				new BukkitRunnable()
				{
					ItemStack[] cloneInvL = cloneInvyL;
					ItemStack[] cloneInvR = cloneInvyR;
					int i = 0;
					int j = 0;
					@Override
					public void run()
					{
						if(i < prioList.size())
						{
							StorageChest sc = prioList.get(i);
							if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
							{
								debug(player, "distribution Content is Empty");
								i = prioList.size();
								return;
							}
							Block block = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(block == null)
							{
								debug(player, "distribution block == null");
								i++;
								return;
							}
							if(block.getState() == null)
							{
								debug(player, "distribution block.State == null");
								i++;
								return;
							}
							if(!(block.getState() instanceof Container))
							{
								debug(player, "distribution not Container");
								i++;
								return;
							}
							Container container = (Container) block.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								debug(player, "distribution cinv == null");
								i++;
								return;
							}
							debug(player, "distribution Normal Storage start i = "+i); //0
							cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, false);
							cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, false);
							i++;
						} else if(i >= prioList.size() && j < endList.size())
						{
							if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
							{
								debug(player, "distribution Content is Empty");
								setItemsLeft(cloneInvyL);
								setItemsRight(cloneInvyR);
								setFinish(true);
								j++;
								return;
							}
							StorageChest sc = endList.get(j);
							Block block = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(block == null)
							{
								debug(player, "distribution block == null");
								j++;
								return;
							}
							if(block.getState() == null)
							{
								debug(player, "distribution block.State == null");
								j++;
								return;
							}
							if(!(block.getState() instanceof Container))
							{
								debug(player, "distribution not Container");
								j++;
								return;
							}
							Container container = (Container) block.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								debug(player, "distribution cinv == null");
								j++;
								return;
							}
							debug(player, "distribution EndStorage start j = "+j);
							cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, true);
							cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, true);
							j++;
						} else
						{
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
							cancel();
							return;
						}
					}
				}.runTaskTimer(plugin, 0L, 1L*plugin.getYamlHandler().get().getInt("DelayedTicks", 1));
			}
		} else
		{
			ItemStack[] cloneInvL = cloneInvyL;
			ItemStack[] cloneInvR = cloneInvyR;
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
		}
	}
}
