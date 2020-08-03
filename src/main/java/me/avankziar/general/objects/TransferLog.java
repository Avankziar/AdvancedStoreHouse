package main.java.me.avankziar.general.objects;

import org.bukkit.inventory.ItemStack;

public class TransferLog
{
	private long date;
	private int distributionChestID;
	private int storageChestID;
	private ItemStack[] distributedContent;
	
	public TransferLog(long date, int distributionChestID, int storageChestID, ItemStack[] distributedContent)
	{
		setDate(date);
		setDistributionChestID(distributionChestID);
		setStorageChestID(storageChestID);
		setDistributedContent(distributedContent);
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public int getDistributionChestID()
	{
		return distributionChestID;
	}

	public void setDistributionChestID(int distributionChestID)
	{
		this.distributionChestID = distributionChestID;
	}

	public int getStorageChestID()
	{
		return storageChestID;
	}

	public void setStorageChestID(int storageChestID)
	{
		this.storageChestID = storageChestID;
	}

	public ItemStack[] getDistributedContent()
	{
		return distributedContent;
	}

	public void setDistributedContent(ItemStack[] distributedContent)
	{
		this.distributedContent = distributedContent;
	}

}
