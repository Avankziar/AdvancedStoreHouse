package main.java.me.avankziar.general.handler;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class CollectionHandler
{
	public static void debug(int lvl, String s)
	{
		int level = 0;
		boolean bo = false;
		if(bo && lvl <= level
				)
		{
			AdvancedStoreHouse.log.info(s);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}			
		}
	}
	
	public static void collectStartVersionShop(int shopID, String server, DistributionChest dc, ItemStack itemStack, long itemStackAmount) throws IOException
	{
		debug(3, "Collect VersionShop start");		
		String data = itemStack.getType().toString();
		debug(0, "Data: "+data);
		ArrayList<StorageChest> prioListPre;
		String orderII = !dc.isNormalPriority() == true ? "`priority` DESC, `id` ASC" : "`priority` ASC, `id` ASC";
		debug(0, "orderII : "+orderII);
		if(dc.getPriorityType() == PriorityType.SWITCH)
		{
			prioListPre = ConvertHandler.convertListIII(
					AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, orderII,
							"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ? AND `searchcontent` LIKE ?",
							dc.getId(), false, server, "%"+data+"%"));
		} else
		{
			prioListPre = ConvertHandler.convertListIII(
					AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, orderII,
							"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ? AND `priority` = ? AND `searchcontent` LIKE ?",
							dc.getId(), false, server, dc.getPriorityNumber(), "%"+data+"%"));
		}
		debug(0, "Amount PrioList: "+prioListPre.size());
		//---
		ArrayList<StorageChest> prioList = new ArrayList<>();
		ArrayList<StorageChest> prList = prioList;
		new BukkitRunnable()
		{
			int i = 0;
			int loop = PluginSettings.settings.getChestsPerTick()*PluginSettings.settings.getDelayedTicks();
			int loopi = loop-1;
			String debug = "Shop ";
			long isAmount = itemStackAmount;
			@Override
			public void run()
			{
				while(i <= loopi)
				{
					if(i < prList.size())
					{
						StorageChest sc = prList.get(i);
						debug(1, debug+"collection normalchest "+i+" "+sc.getChestName());
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							debug(1, debug+"collection block == null");
							i++;
							continue;
						}
						if(block.getState() == null)
						{
							debug(1, debug+"collection block.State == null");
							i++;
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							debug(1, debug+"distribution not Container");
							i++;
							continue;
						}
						//NO ChainDc
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							debug(1, debug+"distribution cinv == null");
							i++;
							continue;
						}
						debug(1, debug+"distribution Normal Storage start i = "+i);
						isAmount = collectForShop(cinv, itemStack, itemStackAmount,
								sc.isEndstorage(), dc.isDistributeRandom(),
								sc.isOptionDurability(), sc.getDurabilityType(), sc.getDurability(),
								sc.isOptionRepair(), sc.getRepairType(), sc.getRepairCost(),
								sc.isOptionEnchantment(), sc.isOptionMaterial());
						if(isAmount == 0)
						{
							debug(1, debug+"distribution from shop, all is distributed");
							cancel();
							return;
						}
						i++;
					} else
					{
						debug(2, debug + "distribution has through all StorageChest. Distribution finished!");
						AdvancedStoreHouse.getPlugin().getShop().putIntoStorage(shopID, itemStack, isAmount);
						cancel();
						break;
					}
				}
				loopi += loop;
			}
		}.runTaskTimer(AdvancedStoreHouse.getPlugin(), 1L, 1L*PluginSettings.settings.getDelayedTicks());
	}
	
	public static long collectForShop(Inventory sc,
			ItemStack is, long itemStackAmount,
			boolean endstorage, boolean isRandom,
			boolean optionDurability, StorageChest.Type durabilityType, int durability, 
			boolean optionRepair, StorageChest.Type repairType, int repaircost,
			boolean optionEnchantments,
			boolean optionMaterial
			)
	{
		long returnItemStackAmount = 0;
		ItemStack cc = is;
		if(returnItemStackAmount > is.getMaxStackSize())
		{
			cc.setAmount(is.getMaxStackSize());
		} else
		{
			cc.setAmount((int) returnItemStackAmount);
		}
		
		for(int slot = 0; slot < sc.getStorageContents().length; slot++)
		{
			ItemStack ccsc = sc.getItem(slot);
			if(ccsc == null || ccsc.getType() == Material.AIR)
			{
				continue;
			}
			ccsc.setAmount(1);
			if(!ccsc.toString().equals(is.toString()))
			{
				continue;
			}
			int amount = sc.getItem(slot).getAmount();
			if(returnItemStackAmount + amount > itemStackAmount)
			{
				long dif = itemStackAmount - returnItemStackAmount;
				returnItemStackAmount += dif;
				sc.getItem(slot).setAmount((int) dif);
				break;
			} else
			{
				returnItemStackAmount += amount;
				sc.getItem(slot).setAmount(0);
			}
		}
		return returnItemStackAmount;
	}
}