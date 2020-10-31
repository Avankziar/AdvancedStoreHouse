package main.java.me.avankziar.general.objects;

import java.util.List;

public class DistributionChest
{
	private int id;
	private String owneruuid;
	private List<String> memberList; //uuid
	private long creationDate;
	private String chestName;
	private boolean normalPriority;
	private boolean automaticDistribution;
	private boolean distributeRandom;
	private String server;
	private String world;
	private int blockX;
	private int blockY;
	private int blockZ;
	
	public DistributionChest(int id, String owneruuid, List<String> memberlist, long creationDate,
			String chestName, boolean normalPriority, boolean automaticDistribution, boolean distributeRandom,
			String server, String world, int blockX, int blockY, int blockZ)
	{
		setId(id);
		setOwneruuid(owneruuid);
		setMemberList(memberlist);
		setCreationDate(creationDate);
		setChestName(chestName);
		setNormalPriority(normalPriority);
		setAutomaticDistribution(automaticDistribution);
		setDistributeRandom(distributeRandom);
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

	public List<String> getMemberList()
	{
		return memberList;
	}

	public void setMemberList(List<String> memberList)
	{
		this.memberList = memberList;
	}

	public long getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(long creationDate)
	{
		this.creationDate = creationDate;
	}

	public String getChestName()
	{
		return chestName;
	}

	public void setChestName(String chestName)
	{
		this.chestName = chestName;
	}

	public boolean isNormalPriority()
	{
		return normalPriority;
	}

	public void setNormalPriority(boolean normalPriority)
	{
		this.normalPriority = normalPriority;
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

	public boolean isAutomaticDistribution()
	{
		return automaticDistribution;
	}

	public void setAutomaticDistribution(boolean automaticDistribution)
	{
		this.automaticDistribution = automaticDistribution;
	}

	public boolean isDistributeRandom()
	{
		return distributeRandom;
	}

	public void setDistributeRandom(boolean distributeRandom)
	{
		this.distributeRandom = distributeRandom;
	}
}
