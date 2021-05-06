package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.interfacehub.spigot.interfaces.StorageSystem;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

public class StorageSystemAPI implements StorageSystem
{
	private AdvancedStoreHouse plugin;
	
	public StorageSystemAPI(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	private DistributionChest getDc(int id)
	{
		try
		{
			return (DistributionChest) plugin.getMysqlHandler().getData(Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		} catch (IOException e)
		{
			return null;
		}	
	}
	
	private StorageChest getSc(int id)
	{
		try
		{
			return (StorageChest) plugin.getMysqlHandler().getData(Type.STORAGECHEST, "`id` = ?", id);
		} catch (IOException e)
		{
			return null;
		}	
	}

	@Override
	public boolean deleteDistributionChest(int id, boolean deleteBoundedChest)
	{
		if(deleteBoundedChest)
		{
			plugin.getMysqlHandler().deleteData(Type.STORAGECHEST, "`distributionchestid` = ?", id);
		}
		return plugin.getMysqlHandler().deleteData(Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		
	}

	@Override
	public boolean deleteStorageChest(int id)
	{
		return plugin.getMysqlHandler().deleteData(Type.STORAGECHEST, "`id` = ?", id);
	}

	@Override
	public boolean existDistributionChest(String server, Location point1)
	{
		return plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, point1.getWorld().getName(), point1.getBlockX(), point1.getBlockY(), point1.getBlockZ());
	}

	@Override
	public boolean existStorageChest(String server, Location point1)
	{
		return plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, point1.getWorld().getName(), point1.getBlockX(), point1.getBlockY(), point1.getBlockZ());
	}

	@Override
	public Location getDistribtionChestLocation(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return new Location(Bukkit.getWorld(dc.getWorld()), (double)dc.getBlockX(), (double)dc.getBlockY(), (double)dc.getBlockZ());
	}

	@Override
	public Boolean getDistributionChestAutomaticDistribution(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.isAutomaticDistribution();
	}

	@Override
	public String getDistributionChestChestName(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getChestName();
	}

	@Override
	public Long getDistributionChestCreationDate(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getCreationDate();
	}

	@Override
	public Boolean getDistributionChestDistributeRandom(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.isDistributeRandom();
	}

	@Override
	public Integer[] getDistributionChestIDs(String server, Location point1)
	{
		ArrayList<DistributionChest> dclist = new ArrayList<>();
		try
		{
			dclist = ConvertHandler.convertListII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, "`server` = ? AND `world` = ?"
							+ " AND `blockx` = ?"
							+ " AND `blocky` = ?"
							+ " AND `blockz` = ?",
							server, point1.getWorld().getName(),
							point1.getBlockX(), point1.getBlockY(), point1.getBlockZ()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(DistributionChest dc : dclist)
		{
			list.add(dc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public Integer[] getDistributionChestIDs(String server, Location point1, Location arg2)
	{
		ArrayList<DistributionChest> dclist = new ArrayList<>();
		try
		{
			dclist = ConvertHandler.convertListII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, "`server` = ? AND `world` = ?"
							+ " AND `blockx` <= ? AND `blockx` >= ?"
							+ " AND `blocky` <= ? AND `blocky` >= ?"
							+ " AND `blockz` <= ? AND `blockz` >= ?",
							server, point1.getWorld().getName(),
							Math.max(point1.getBlockX(), point1.getBlockX()), Math.min(point1.getBlockX(), point1.getBlockX()),
							Math.max(point1.getBlockY(), point1.getBlockY()), Math.min(point1.getBlockY(), point1.getBlockY()),
							Math.max(point1.getBlockZ(), point1.getBlockY()), Math.min(point1.getBlockZ(), point1.getBlockZ())));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(DistributionChest dc : dclist)
		{
			list.add(dc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public List<String> getDistributionChestMembers(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getMemberList();
	}

	@Override
	public Boolean getDistributionChestNormalPriority(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.isNormalPriority();
	}

	@Override
	public String getDistributionChestOwnerUUID(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getOwneruuid();
	}

	@Override
	public Integer getDistributionChestPriorityNumber(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getPriorityNumber();
	}

	@Override
	public String getDistributionChestPriorityType(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getPriorityType().toString();
	}

	@Override
	public String getDistributionChestServer(int id)
	{
		DistributionChest dc = getDc(id);
		if(dc == null)
		{
			return null;
		}
		return dc.getServer();
	}

	@Override
	public String getStorageChestChestName(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getChestName();
	}

	@Override
	public Long getStorageChestCreationDate(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getCreationDate();
	}

	@Override
	public Integer getStorageChestDistributionChestID(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getDistributionChestID();
	}

	@Override
	public Integer getStorageChestDurabilityPercent(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getDurability();
	}

	@Override
	public String getStorageChestDurabilityType(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getDurabilityType().toString();
	}

	@Override
	public Boolean getStorageChestEndStorage(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isEndstorage();
	}

	@Override
	public ItemStack[] getStorageChestFilter(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getContents();
	}

	@Override
	public Integer[] getStorageChestIDs(String server, Location point1)
	{
		ArrayList<StorageChest> sclist = new ArrayList<>();
		try
		{
			sclist = ConvertHandler.convertListIII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`server` = ? AND `world` = ?"
							+ " AND `blockx` = ?"
							+ " AND `blocky` = ?"
							+ " AND `blockz` = ?",
							server, point1.getWorld().getName(),
							point1.getBlockX(), point1.getBlockY(), point1.getBlockZ()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(StorageChest sc : sclist)
		{
			list.add(sc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public Integer[] getStorageChestIDs(String server, Location point1, Location point2)
	{
		ArrayList<StorageChest> sclist = new ArrayList<>();
		try
		{
			sclist = ConvertHandler.convertListIII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`server` = ? AND `world` = ?"
							+ " AND `blockx` <= ? AND `blockx` >= ?"
							+ " AND `blocky` <= ? AND `blocky` >= ?"
							+ " AND `blockz` <= ? AND `blockz` >= ?",
							server, point1.getWorld().getName(),
							Math.max(point1.getBlockX(), point1.getBlockX()), Math.min(point1.getBlockX(), point1.getBlockX()),
							Math.max(point1.getBlockY(), point1.getBlockY()), Math.min(point1.getBlockY(), point1.getBlockY()),
							Math.max(point1.getBlockZ(), point1.getBlockY()), Math.min(point1.getBlockZ(), point1.getBlockZ())));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(StorageChest sc : sclist)
		{
			list.add(sc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public Location getStorageChestLocation(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return new Location(Bukkit.getWorld(sc.getWorld()), (double)sc.getBlockX(), (double)sc.getBlockY(), (double)sc.getBlockZ());
	}

	@Override
	public Boolean getStorageChestOptionDurability(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isOptionDurability();
	}

	@Override
	public Boolean getStorageChestOptionEnchantment(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isOptionEnchantment();
	}

	@Override
	public Boolean getStorageChestOptionMaterial(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isOptionMaterial();
	}

	@Override
	public Boolean getStorageChestOptionRepair(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isOptionRepair();
	}

	@Override
	public Boolean getStorageChestOptionVoid(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.isOptionVoid();
	}

	@Override
	public String getStorageChestOwnerUUID(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getOwneruuid();
	}

	@Override
	public Integer getStorageChestPriorityNumber(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getPriorityNumber();
	}

	@Override
	public Integer getStorageChestRepairLevelCost(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getRepairCost();
	}

	@Override
	public String getStorageChestRepairType(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getRepairType().toString();
	}

	@Override
	public String[] getStorageChestSearchContent(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getSearchContents();
	}

	@Override
	public String getStorageChestServer(int id)
	{
		StorageChest sc = getSc(id);
		if(sc == null)
		{
			return null;
		}
		return sc.getServer();
	}

	@Override
	public Integer[] getStorageChestsWithSearchableItem(ItemStack searchItem)
	{
		final String data = ChestHandler.getGroundSpecs(searchItem);
		ArrayList<StorageChest> sclist = new ArrayList<>();
		try
		{
			sclist = ConvertHandler.convertListIII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`searchcontent` LIKE ?",
							"%"+data+"%"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(StorageChest sc : sclist)
		{
			list.add(sc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public Integer[] getStorageChestsWithSearchableItem(int distributionchestid, ItemStack searchItem)
	{
		final String data = ChestHandler.getGroundSpecs(searchItem);
		ArrayList<StorageChest> sclist = new ArrayList<>();
		try
		{
			sclist = ConvertHandler.convertListIII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`distributionchestid` = ? AND `searchcontent` LIKE ?",
							distributionchestid, "%"+data+"%"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(StorageChest sc : sclist)
		{
			list.add(sc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public Integer[] getStorageChestsWithSearchableItem(Player player, ItemStack searchItem)
	{
		final String data = ChestHandler.getGroundSpecs(searchItem);
		ArrayList<StorageChest> sclist = new ArrayList<>();
		try
		{
			sclist = ConvertHandler.convertListIII(
					plugin.getMysqlHandler().getAllListAt(
							MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`owner_uuid` = ? AND `searchcontent` LIKE ?",
							player.getUniqueId().toString(), "%"+data+"%"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<Integer> list = new ArrayList<>();
		for(StorageChest sc : sclist)
		{
			list.add(sc.getId());
		}
		Integer[] ar = new Integer[list.size()];
		list.toArray(ar);
		return ar;
	}

	@Override
	public boolean hasDistributionChests(Player player)
	{
		return plugin.getMysqlHandler().exist(Type.DISTRIBUTIONCHEST, "`owner_uuid` = ?", player.getUniqueId().toString());
	}

}
