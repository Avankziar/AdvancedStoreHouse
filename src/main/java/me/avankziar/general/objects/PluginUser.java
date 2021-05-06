package main.java.me.avankziar.general.objects;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PluginUser
{
	public enum Mode
	{
		CONSTRUCT, //Wenn man nur bauen will.
		CREATEDISTRIBUTIONCHEST, //Man hat den Cmd /ash create genutzt
		CREATESTORAGE, //Man hat eine Verteilerkiste erstellt und will nun Lagerkisten erstellen
		UPDATESTORAGEITEMFILTERSET, //Das eigentliche Update der Gui nach NONE
		CREATEITEMFILTERSET, //Erstellen von individuelle ItemFilterSet
		CHANGEITEMFILTERSET, //Ändern von individuellen ItemFilterSet		
		OPTIONGUI, //Das Gui für Sc und Dc, per PDC wird darin unterschieden.
		
		;
	}
	
	public enum SearchType
	{
		COMPASS, EFFECT
	}
	
	private String uuid;
	private String name;
	private Mode mode;
	private SearchType searchType;
	private int distributionChestID;
	private String distributionChestName;
	private int storageChestID;
	private ItemFilterSet itemFilterSet;
	private Location compassLocation;
	private Boolean canDistributionChestBreak;
	private SettingLevel settingLevel;
	private ArrayList<Integer> selectedStorageChest = new ArrayList<>();
	
	public PluginUser(String uuid, String name, SearchType searchType)
	{
		setUUID(uuid);
		setName(name);
		setMode(Mode.CONSTRUCT);
		setSearchType(searchType);
		setDistributionChestID(0);
		setDistributionChestName("unnamed");
		setStorageChestID(0);
		setItemFilterSet(new ItemFilterSet(0, "0", uuid, Bukkit.createInventory(null, 9*6).getContents()));
		setCompassLocation(null);
		setCanDistributionChestBreak(false);
		setSettingLevel(SettingLevel.BASE);
	}

	public String getUUID()
	{
		return uuid;
	}

	public void setUUID(String uuid)
	{
		this.uuid = uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Mode getMode()
	{
		return mode;
	}

	public void setMode(Mode mode)
	{
		this.mode = mode;
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

	public ItemFilterSet getItemFilterSet()
	{
		return itemFilterSet;
	}

	public void setItemFilterSet(ItemFilterSet itemFilterSet)
	{
		this.itemFilterSet = itemFilterSet;
	}

	public SearchType getSearchType()
	{
		return searchType;
	}

	public void setSearchType(SearchType searchType)
	{
		this.searchType = searchType;
	}

	public String getDistributionChestName()
	{
		return distributionChestName;
	}

	public void setDistributionChestName(String distributionChestName)
	{
		this.distributionChestName = distributionChestName;
	}

	public Location getCompassLocation()
	{
		return compassLocation;
	}

	public void setCompassLocation(Location compassLocation)
	{
		this.compassLocation = compassLocation;
	}

	public Boolean canDistributionChestBreak()
	{
		return canDistributionChestBreak;
	}

	public void setCanDistributionChestBreak(Boolean canDistributionChestBreak)
	{
		this.canDistributionChestBreak = canDistributionChestBreak;
	}

	public SettingLevel getSettingLevel()
	{
		return settingLevel;
	}

	public void setSettingLevel(SettingLevel settingLevel)
	{
		this.settingLevel = settingLevel;
	}

	public ArrayList<Integer> getSelectedStorageChest()
	{
		return selectedStorageChest;
	}
	
	public void addSelectedStorageChest(int id)
	{
		if(!selectedStorageChest.contains(id))
		{
			selectedStorageChest.add(id);
		}
	}

	public void setSelectedStorageChest(ArrayList<Integer> selectedStorageChest)
	{
		this.selectedStorageChest = selectedStorageChest;
	}
}
