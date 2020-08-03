package main.java.me.avankziar.general.objects;

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
	
	public StorageChest(int id, int distributionChestID, String owneruuid, int priority, long creationDate,
			ItemStack[] contents, boolean endstorage, String server, String world, int blockX, int blockY, int blockZ)
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

}
