package main.java.me.avankziar.general.objects;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class StorageChest
{
	private int id;
	private String owneruuid;
	private int distributionChestID;
	private long creationDate;
	private int priority;
	private ItemStack[] contents;
	private boolean endstorage;
	private String server;
	private String world;
	private int blockX;
	private int blockY;
	private int blockZ;
	private String chestname;
	private boolean optionVoid;
	private boolean optionDurability;
	private int durability;
	private boolean optionRepair;
	private int repairCost;
	private boolean optionEnchantment;
	private LinkedHashMap<Enchantment, Integer> enchantments;
	
	public StorageChest(int id, int distributionChestID, String owneruuid, int priority, long creationDate,
			ItemStack[] contents, boolean endstorage, String server, String world, int blockX, int blockY, int blockZ,
			String chestname, boolean optionVoid,
			boolean optionDurability,
			int durability,
			boolean optionRepair,
			int repairCost,
			boolean optionEnchantment,
			LinkedHashMap<Enchantment, Integer> enchantments)
	{
		setId(id);
		setOwneruuid(owneruuid);
		setDistributionChestID(distributionChestID);
		setCreationDate(creationDate);
		setPriority(priority);
		setContents(contents);
		setEndstorage(endstorage);
		setServer(server);
		setWorld(world);
		setBlockX(blockX);
		setBlockY(blockY);
		setBlockZ(blockZ);
		setChestname(chestname);
		setOptionVoid(optionVoid);
		setOptionDurability(optionDurability);
		setDurability(durability);
		setOptionRepair(optionRepair);
		setRepairCost(repairCost);
		setOptionEnchantment(optionEnchantment);
		setEnchantments(enchantments);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getOwneruuid()
	{
		return owneruuid;
	}

	public void setOwneruuid(String owneruuid)
	{
		this.owneruuid = owneruuid;
	}

	public int getDistributionChestID()
	{
		return distributionChestID;
	}

	public void setDistributionChestID(int distributionChestID)
	{
		this.distributionChestID = distributionChestID;
	}

	public long getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(long creationDate)
	{
		this.creationDate = creationDate;
	}

	public ItemStack[] getContents()
	{
		return contents;
	}

	public void setContents(ItemStack[] contents)
	{
		this.contents = contents;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public int getBlockX()
	{
		return blockX;
	}

	public void setBlockX(int blockX)
	{
		this.blockX = blockX;
	}

	public int getBlockY()
	{
		return blockY;
	}

	public void setBlockY(int blockY)
	{
		this.blockY = blockY;
	}

	public int getBlockZ()
	{
		return blockZ;
	}

	public void setBlockZ(int blockZ)
	{
		this.blockZ = blockZ;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public boolean isEndstorage()
	{
		return endstorage;
	}

	public void setEndstorage(boolean endstorage)
	{
		this.endstorage = endstorage;
	}

	public String getChestname()
	{
		return chestname;
	}

	public void setChestname(String chestname)
	{
		this.chestname = chestname;
	}

	public boolean isOptionVoid()
	{
		return optionVoid;
	}

	public void setOptionVoid(boolean optionVoid)
	{
		this.optionVoid = optionVoid;
	}

	public boolean isOptionDurability()
	{
		return optionDurability;
	}

	public void setOptionDurability(boolean optionDurability)
	{
		this.optionDurability = optionDurability;
	}

	public int getDurability()
	{
		return durability;
	}

	public void setDurability(int durability)
	{
		this.durability = durability;
	}

	public boolean isOptionRepair()
	{
		return optionRepair;
	}

	public void setOptionRepair(boolean optionRepair)
	{
		this.optionRepair = optionRepair;
	}

	public int getRepairCost()
	{
		return repairCost;
	}

	public void setRepairCost(int repairCost)
	{
		this.repairCost = repairCost;
	}

	public boolean isOptionEnchantment()
	{
		return optionEnchantment;
	}

	public void setOptionEnchantment(boolean optionEnchantment)
	{
		this.optionEnchantment = optionEnchantment;
	}

	public LinkedHashMap<Enchantment, Integer> getEnchantments()
	{
		return enchantments;
	}

	public void setEnchantments(LinkedHashMap<Enchantment, Integer> enchantments)
	{
		this.enchantments = enchantments;
	}
	
	public String encryptEnchantments()
	{
		String s = "";
		for(Entry<Enchantment, Integer> entry : enchantments.entrySet())
		{
			s += entry.getKey().getKey().getKey()+";"+entry.getValue()+"@";
		}
		s.substring(0, s.length()-1);
		return s;
	}
	
	@SuppressWarnings("deprecation")
	public static LinkedHashMap<Enchantment, Integer> decryptEnchantments(String s)
	{
		LinkedHashMap<Enchantment, Integer> map = new LinkedHashMap<>();
		String[] split = s.split("@");
		for(String ench : split)
		{
			String[] e = ench.split(";");
			if(e.length == 2)
			{
				Enchantment ec = Enchantment.getByName(e[0]);
				int level = Integer.parseInt(e[1]);
				map.put(ec, level);
			}
		}
		return map;
	}
}
