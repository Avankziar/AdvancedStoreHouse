package main.java.me.avankziar.general.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PluginUser
{
	public enum Mode
	{
		CONSTRUCT, //Wenn man nur bauen will.
		NONE, //Zwischen ding
		BLOCKINFO, //Durch shift+Rechtsklick werden alle Infos zu dem Block angezeigt.
		CREATEDISTRIBUTIONCHEST, //Man hat den Cmd /ash create genutzt
		CREATESTORAGE, //Man hat eine Verteilerkiste erstellt und will nun Lagerkisten erstellen
		UPDATESTORAGE, //Ein Mode durch ein befehl getriggert, um existierende Lagerkisten durch den Zwischenspeicher zu verändern.
		UPDATESTORAGEITEMFILTERSET, //Das eigentliche Update der Gui nach NONE
		CREATEITEMFILTERSET, //Erstellen von individuelle ItemFilterSet
		CHANGEITEMFILTERSET, //Ändern von individuellen ItemFilterSet
		POSITIONUPDATEDISTRIBUTION, //Ändern von verteilerkisten positionen
		POSITIONUPDATESTORAGE, //Ändern von lagerkisten positionen
		
		DISTRIBUTIONCHESTGUI, //Das main Gui für Verteilerkisten
		
		OPTIONGUI, //Das Gui für Sc und Dc, per PDC wird darin unterschieden.
		
		;
	}
	
	public enum SearchType
	{
		COMPASS, TELEPORT, EFFECT, SOUND, GLOWINGENTITY;
	}
	
	private String uuid;
	private String name;
	private Mode mode;
	private SearchType searchType;
	private int distributionChestID;
	private String distributionChestName;
	private int storageChestID;
	private int priority;
	private boolean endStorage;
	private boolean override;
	private ItemFilterSet itemFilterSet;
	private Location compassLocation;
	private Boolean canDistributionChestBreak;
	
	//The number (in a List), from where it call Particels, to show the storagechest
	private int numberScForParticel;
	
	public PluginUser(String uuid, String name, SearchType searchType)
	{
		setUUID(uuid);
		setName(name);
		setMode(Mode.CONSTRUCT);
		setSearchType(SearchType.COMPASS);
		setDistributionChestID(0);
		setDistributionChestName("0");
		setStorageChestID(0);
		setPriority(0);
		setEndStorage(false);
		setOverride(false);
		setItemFilterSet(new ItemFilterSet(0, "0", uuid, Bukkit.createInventory(null, 9*6).getContents()));
		setCompassLocation(null);
		setCanDistributionChestBreak(false);
		setNumberScForParticel(0);
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

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
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

	public boolean isEndStorage()
	{
		return endStorage;
	}

	public void setEndStorage(boolean endStorage)
	{
		this.endStorage = endStorage;
	}

	public boolean isOverride()
	{
		return override;
	}

	public void setOverride(boolean override)
	{
		this.override = override;
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

	public int getNumberScForParticel()
	{
		return numberScForParticel;
	}

	public void setNumberScForParticel(int numberScForParticel)
	{
		this.numberScForParticel = numberScForParticel;
	}

}
