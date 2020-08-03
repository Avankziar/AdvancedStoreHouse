package main.java.me.avankziar.general.objects;

import org.bukkit.inventory.ItemStack;

public class ItemFilterSet
{
	private int iD;
	private String name;
	private String owneruuid;
	private ItemStack[] contents;
	
	public ItemFilterSet(int id, String name, String owneruuid, ItemStack[] contents)
	{
		setID(id);
		setName(name);
		setOwneruuid(owneruuid);
		setContents(contents);
	}

	public String getOwneruuid()
	{
		return owneruuid;
	}

	public void setOwneruuid(String owneruuid)
	{
		this.owneruuid = owneruuid;
	}

	public int getID()
	{
		return iD;
	}

	public void setID(int ID)
	{
		this.iD = ID;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ItemStack[] getContents()
	{
		return contents;
	}

	public void setContents(ItemStack[] contents)
	{
		this.contents = contents;
	}

}
