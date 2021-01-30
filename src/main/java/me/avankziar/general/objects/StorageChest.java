package main.java.me.avankziar.general.objects;

import org.bukkit.inventory.ItemStack;

public class StorageChest
{
	public enum Type
	{
		LESSTHAN, LARGERTHAN
	}
	
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
	private Type durabilityType;
	private int durability;
	private boolean optionRepair;
	private Type repairType;
	private int repairCost;
	private boolean optionEnchantment;
	private boolean optionMaterial;
	
	public StorageChest(int id, int distributionChestID, String owneruuid, int priority, long creationDate,
			ItemStack[] contents, boolean endstorage, String server, String world, int blockX, int blockY, int blockZ,
			String chestname, boolean optionVoid,
			boolean optionDurability, Type durabilityType, int durability, 
			boolean optionRepair, Type repairType, int repairCost,
			boolean optionEnchantment,
			boolean optionMaterial)
	{
		setId(id);
		setOwneruuid(owneruuid);
		setDistributionChestID(distributionChestID);
		setCreationDate(creationDate);
		setPriorityNumber(priority);
		setContents(contents);
		setEndstorage(endstorage);
		setServer(server);
		setWorld(world);
		setBlockX(blockX);
		setBlockY(blockY);
		setBlockZ(blockZ);
		setChestName(chestname);
		setOptionVoid(optionVoid);
		setOptionDurability(optionDurability);
		setDurability(durability);
		setDurabilityType(durabilityType);
		setOptionRepair(optionRepair);
		setRepairType(repairType);
		setRepairCost(repairCost);
		setOptionEnchantment(optionEnchantment);
		setOptionMaterial(optionMaterial);
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

	public int getPriorityNumber()
	{
		return priority;
	}

	public void setPriorityNumber(int priority)
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

	public String getChestName()
	{
		return chestname;
	}

	public void setChestName(String chestname)
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

	public Type getDurabilityType()
	{
		return durabilityType;
	}

	public void setDurabilityType(Type durabilityType)
	{
		this.durabilityType = durabilityType;
	}

	public Type getRepairType()
	{
		return repairType;
	}

	public void setRepairType(Type repairType)
	{
		this.repairType = repairType;
	}

	public boolean isOptionMaterial()
	{
		return optionMaterial;
	}

	public void setOptionMaterial(boolean optionMaterial)
	{
		this.optionMaterial = optionMaterial;
	}
}
