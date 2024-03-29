package main.java.me.avankziar.spigot.ash.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.general.objects.SettingLevel;
import main.java.me.avankziar.spigot.ash.database.Language.ISO639_2B;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guiKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> limitsKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initLanguage();
		initGuis();
		initLimits();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigKey()
	{
		return configKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getGuiKeys(String key)
	{
		return guiKeys.get(key);
	}
	
	public LinkedHashMap<String, Language> getLimitsKey()
	{
		return limitsKeys;
	}

	public static void setGuiKeys(LinkedHashMap<String, LinkedHashMap<String, Language>> guiKeys)
	{
		YamlManager.guiKeys = guiKeys;
	}

	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{
		configKeys.put("useIFHAdministration"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("IFHAdministrationPath"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash"}));
		configKeys.put("Language"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ENG"}));
		configKeys.put("Prefix"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"&7[&2ASH&7] &r"}));
		configKeys.put("Servername"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"hub"}));
		configKeys.put("Mysql.Status"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.Host"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"127.0.0.1"}));
		configKeys.put("Mysql.Port"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3306}));
		configKeys.put("Mysql.DatabaseName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mydatabase"}));
		configKeys.put("Mysql.SSLEnabled"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.AutoReconnect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("Mysql.VerifyServerCertificate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.User"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"admin"}));
		configKeys.put("Mysql.Password"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"not_0123456789"}));
		//Scheduler to automatic distribute. If false nothing running.
		configKeys.put("IsAutomaticDistribution"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//When your Server estimated to Restart. In order to be able to estimate when the last cycle must end in the Dynamic Format.
		configKeys.put("ServerRestartTimes"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"~06:00",
				"~12:00"}));
		/*
		 * Use the Dynamic Format. This means, per storagechest on the server the task take 1 Tick
		 * And with or without the pause the task calculate the period to run the cyclus of the task.
		 * Also is the dynamic format performace friendlyish. Also the Pause is only important in the dynamic format.
		 */
		//The DirectPausevalue is a timevalue in ticks. A tick is 50 ms. Default is 10 min. In this Pause nothing will be distributed
		configKeys.put("DirectPauseValue"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				12000}));
		//The IndirectPausevalue is a timevalue in ticks. Default is 10 min. This value will be added on the TotalTicks from the amount on storagechest.
		configKeys.put("IndirectPauseValue"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				12000}));
		//The min value for a distributionchest. Aka a basevalue added to the dynamic value.
		configKeys.put("MinimumTickPerDistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		/*
		 * If UseDelayedDistribution is false can lags occur!
		 * If true, per Tick it works off a storagechest. And in the next tick, the next storagechest is processed etc.
		 */
		//For example, if you have 1500 Storagechest, so you must 1500/100 = 15 Ticks wait, before the distribution starts.
		configKeys.put("WaitBeforeStartFactor"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				100}));
		//A Tick is 50 ms. Between 1 and 2 Ticks may should be enough for ~50 - 100 StorageChests per DistributionChest.
		configKeys.put("DelayedTicks"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3}));
		//Seconds, when the chain will be activated
		configKeys.put("DelayChainChests"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		//Ticks, how fast per Distributionchest looping.
		configKeys.put("DelayedChainTicks"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		//The value set, how many storagechest per tick in a loop is processed
		configKeys.put("ChestsPerTick"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				5}));
		
		//The value set, how many seconds counted before, the a new runnable check how many void chest exists
		configKeys.put("VoidChestRun"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				300}));
		//the value set, how many voidchest per tick in a loop is processed
		configKeys.put("VoidChestsPerTick"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		configKeys.put("BlockBreakDistributionChestDeleteLinkedStorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//The sphere radius to interact and trigger Distributionchests. Attention! To high number cann occur lags.
		configKeys.put("ButtonPlateInteractRadius"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3}));
		/*
		 * Set the diplayed Amount Chest in the "list" command (/ash distributionchest|storagechest list)
		 * This is maded, because it happend by some server, that the player, which perform the command was kicked without errors
		 */
		configKeys.put("AmountToDisplayDistributuionChestInListCommand"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				25}));
		configKeys.put("AmountToDisplayStorageChestInListCommand"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				25}));
		//Maximum Copy Paste
		configKeys.put("CopyPasteMaxStorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				20}));
		configKeys.put("StorageChest.DefaultName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"unnamed"}));
		//Here is all Simple Methode Materials
		configKeys.put("Simple.Creating"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"AIR"}));
		configKeys.put("Simple.CreateDirectWithIFS"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"TRAPPED_CHEST"}));
		configKeys.put("Simple.CreateDirectWithChestContents"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"CHEST"}));
		configKeys.put("Simple.CopyAndPaste"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ENDER_CHEST"}));
		configKeys.put("Simple.Select"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"BARREL"}));
		configKeys.put("Simple.Visuals"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"BLAST_FURNACE"}));
		configKeys.put("Simple.OpenOptionGUI"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"FURNACE"}));
		configKeys.put("Simple.Reposition"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"SMOKER"}));
		configKeys.put("Animation.PerTick"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				25}));
		configKeys.put("Animation.Lenght"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10000}));
		configKeys.put("Animation.AdditionalCooldown"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10000}));
		configKeys.put("Animation.Particle.DistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"WATER_DROP"}));
		configKeys.put("Animation.Particle.RandomDistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"END_ROD"}));
		configKeys.put("Animation.Particle.StorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"COMPOSTER"}));
		configKeys.put("Animation.Particle.VoidStorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"FLAME"}));
		configKeys.put("Animation.Particle.EndStorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"SMOKE_LARGE"}));
		configKeys.put("GUISound"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"BLOCK_ANCIENT_DEBRIS_HIT"}));
		configKeys.put("SIGN.Identifier"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"[ASH]"}));
		configKeys.put("SIGN.SWITCH"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"SWITCH"}));
		configKeys.put("SIGN.DISTRIBUTE"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"DISTRIBUTE"}));
		configKeys.put("GuiList"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"DC_MAIN", "DC_NUMPAD",
				"SC_MAIN", "SC_PRIORITY_NUMPAD", "SC_DURABILITY_NUMPAD", "SC_REPAIR_NUMPAD"}));
		/*configKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				""}));*/
	}
	
	@SuppressWarnings("unused") //INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "";
		commandsInput("ash", "ash", "ash.cmd.ash", 
				"/ash [pagenumber]", "/ash ",
				"&c/ash &f| Infoseite für alle Befehle.",
				"&c/ash &f| Info page for all commands.");
		argumentInput("ash_automaticdistributioninfo", "automaticdistributioninfo", "ash.cmd.automaticdistributioninfo",
				"/ash automaticdistributioninfo", "/ash automaticdistributioninfo ", 
				"&c/ash automaticdistributioninfo &f| Zeigt alle Infos zum Automatischen Verteilen an.",
				"&c/ash automaticdistributioninfo &f| Displays all info about the automatic distribution.");
		argumentInput("ash_blockinfo", "blockinfo", "ash.cmd.blockinfo",
				"/ash blockinfo", "/ash blockinfo ", 
				"&c/ash blockinfo &f| Zeigt mit shift+Rechtsklick alle Blockinfos vom Lagersystem.",
				"&c/ash blockinfo &f| Show all block info from the storage system with shift+right click");
		argumentInput("ash_cancel", "cancel", "ash.cmd.cancel",
				"/ash cancel", "/ash cancel ", 
				"&c/ash cancel &f| Bricht alle Aktionen ab.",
				"&c/ash cancel &f| Cancels all actions");
		argumentInput("ash_convert", "convert", "ash.cmd.convert",
				"/ash convert", "/afkr convert ",
				"&c/ash convert &f| Konvertiert alte Datenbankeinträge auf Build 4. Pro Server laufend.",
				"&c/ash convert &f| Converts old database entries to Build 4. Per Server running.");
		argumentInput("ash_checkunboundchest", "checkunboundchest", "ash.cmd.checkunboundchest",
				"/ash checkunboundchest", "/afkr checkunboundchest ",
				"&c/ash checkunboundchest &f| Checkt ob ungenutzte Verteilerkisten oder Lagerkisten ohne Verteilerkiste existieren und löscht diese.",
				"&c/ash checkunboundchest &f| Checks if there are unused distribution chests or storage chests without distribution chest and deletes them.");
		argumentInput("ash_cutandpaste", "cutandpaste", "ash.cmd.cutandpaste",
				"/ash cutandpaste ", "/ash cutandpaste ", 
				"&c/ash cutandpaste &f| Schneidet vorher ausgewählte Lagerkisten aus und fügt sie zu einer bestehenden Verteilerkiste hinzu.",
				"&c/ash cutandpaste &f| Cuts previously selected storage boxes and adds them to an existing distribution chest.");
		argumentInput("ash_debug", "debug", "ash.cmd.debug",
				"/ash debug", "/ash debug ", 
				"&c/ash debug &f| Zwischenbefehl",
				"&c/ash debug &f| Intermediate command.");
		argumentInput("ash_debug_itemmeta", "itemmeta", "ash.cmd.debug.itemmeta",
				"/ash debug itemmeta", "/ash debug itemmeta ", 
				"&c/ash debug itemmeta &f| Zeigt einen String der Itemmeta des Items in der Haupthand an.",
				"&c/ash debug itemmeta &f| Displays a string of the item meta of the item in the main hand.");
		argumentInput("ash_delete", "delete", "ash.cmd.delete",
				"/ash delete <server> <world>", "/ash delete ", 
				"&c/ash delete <server> <welt> &f| Löscht alle Verteiler- und Lagerkisten die sich auf diesem Server und in dieser Welt befinden.",
				"&c/ash delete <server> <welt> &f| Deletes all distribution and storage chests that are on this server and in this world.");
		argumentInput("ash_dc", "distributionchest", "ash.cmd.distributionchest",
				"/ash distributionchest", "/ash distributionchest ", 
				"&c/ash distributionchest &f| Zwischenbefehl.",
				"&c/ash distributionchest &f| Intermediate command.");
		argumentInput("ash_dc_breaking", "breaking", "ash.cmd.distributionchest.breaking",
				"/ash distributionchest breaking", "/ash distributionchest breaking ", 
				"&c/ash distributionchest breaking &f| Toggelt, ob du Verteilerkisten abbauen möchtest.",
				"&c/ash distributionchest breaking &f| Toggles, whether you may break a block which is a distributionchest.");
		argumentInput("ash_dc_chestname", "chestname", "ash.cmd.distributionchest.chestname",
				"/ash distributionchest chestname", "/ash distributionchest chestname ", 
				"&c/ash distributionchest chestname <Name> &f| Setzt einen neuen Namen für die Verteilerkiste.",
				"&c/ash distributionchest chestname <Name> &f| Sets a new name for the distribution box");
		argumentInput("ash_dc_create", "create", "ash.cmd.distributionchest.create",
				"/ash distribution create", "/ash distribution create ", 
				"&c/ash distributionchest create <Name> &f| Beginnt die Erstellung einer Verteilerkiste.",
				"&c/ash distributionchest create <Name> &f| Starts the creation of a distribution box");
		argumentInput("ash_dc_delete", "delete", "ash.cmd.distributionchest.delete",
				"/ash distributionchest delete ", "/ash distributionchest delete ", 
				"&c/ash distributionchest delete &f| Löscht die vorher ausgewählte Verteilerkiste.", 
				"&c/ash distributionchest delete &f| Deletes the previously selected distribution box");
		argumentInput("ash_dc_info", "info", "ash.cmd.distributionchest.info",
				"/ash distributionchest info", "/ash distributionchest info ", 
				"&c/ash distributionchest info [ID/Name] &f| Listet alle Infos der Verteilerkiste auf.",
				"&c/ash distributionchest info [ID/Name] &f| Lists all the information in the distribution box");
		argumentInput("ash_dc_list", "list", "ash.cmd.distributionchest.list",
				"/ash distributionchest list [pagenumber] ", "/ash distributionchest list ", 
				"&c/ash distributionchest list [Seitenzahl] &f| Listet alle IDs und Namen deiner Verteilerkistena auf.", 
				"&c/ash distributionchest list [pagenumber] &f| Lists all IDs and names of your distribution boxes.");
		argumentInput("ash_dc_member", "member", "ash.cmd.distributionchest.member",
				"/ash distributionchest member <playername>", "/ash distributionchest member ", 
				"&c/ash distributionchest member <Spielername> &f| Fügt einen Spieler der Mitgliedsliste hinzu oder entfernt ihn.",
				"&c/ash distributionchest member <playername> &f| Add or remove a player from the member list.");
		argumentInput("ash_dc_openoption", "openoption", "ash.cmd.distributionchest.openoption",
				"/ash distributionchest openoption [ID/Chestname]", "/ash distributionchest openoption ", 
				"&c/ash distributionchest openoption [ID/Kistenname] &f| Öffnet das Options Menu der Verteilerkiste.",
				"&c/ash distributionchest openoption [ID/Chestname] &f| Opens the Options menu of the distribution chest.");
		argumentInput("ash_dc_remotetriggeranimation", "remotetriggeranimation", "ash.cmd.distributionchest.remotetriggeranimation",
				"/ash distributionchest remotetriggeranimation [id/Chestname]", "/ash distributionchest remotetriggeranimation ", 
				"&c/ash distributionchest remotetriggeranimation [id/Kistenname] &f| Löst die Kistenanimation fern aus.",
				"&c/ash distributionchest remotetriggeranimation [id/Chestname] &f| Triggered remote the chestanimation.");
		argumentInput("ash_dc_select", "select", "ash.cmd.distributionchest.select",
				"/ash distributionchest select <ID/Name> [PlayerName]", "/ash distributionchest select ", 
				"&c/ash distributionchest select <ID/Name> [Spielername] &f| Wählt eine Verteilerkiste aus.", 
				"&c/ash distributionchest select <ID/Name> [PlayerName] &f| Selects a distribution box.");
		argumentInput("ash_dc_search", "search", "ash.cmd.distributionchest.search",
				"/ash distributionchest search [ID/Name] ", "/ash distributionchest search ", 
				"&c/ash distributionchest search [ID/Name] &f| Sucht die ausgewählte Verteilerkiste per Partikel.", 
				"&c/ash distributionchest search [ID/Name] &f| Searches the selected distribution box per particel.");
		argumentInput("ash_dc_switch", "switch", "ash.cmd.distributionchest.switch",
				"/ash distributionchest switch", "/ash distributionchest switch ", 
				"&c/ash distributionchest switch &f| Toggelt ob die Prioriäten aufsteigend oder absteigend berücksichtig werden sollen.", 
				"&c/ash distributionchest switch &f| Toggles whether the priorities should be considered in ascending or descending order.");
		argumentInput("ash_dc_transfer", "transfer", "ash.cmd.distributionchest.transfer",
				"/ash distributionchest transfer <playername>", "/ash distributionchest transfer ", 
				"&c/ash distributionchest transfer <Spielername> &f| Überträgt den Eigentümerstatus auf den angegebenen Spieler.",
				"&c/ash distributionchest transfer <playername> &f| Transfers the owner status to the specified player.");
		argumentInput("ash_itemfilterset", "itemfilterset", "ash.cmd.itemfilterset",
				"/ash itemfilterset", "/ash itemfilterset ", 
				"&c/ash itemfilterset &f| ZwischenBefehl.", 
				"&c/ash itemfilterset &f| Intermediate Command.");
		argumentInput("ash_itemfilterset_create", "create", "ash.cmd.itemfilterset.create",
				"/ash itemfilterset create <Name>", "/ash itemfilterset create ", 
				"&c/ash itemfilterset create <Name> &f| Erstellt ein ItemFilterSet.", 
				"&c/ash itemfilterset create <Name> &f| Creates an ItemFilterSet.");
		argumentInput("ash_itemfilterset_delete", "delete", "ash.cmd.itemfilterset.delete",
				"/ash itemfilterset delete", "/ash itemfilterset delete ", 
				"&c/ash itemfilterset delete &f| Löscht ein ItemFilterSet.",
				"&c/ash itemfilterset delete &f| Deletes an ItemFilterSet.");
		argumentInput("ash_itemfilterset_list", "list", "ash.cmd.itemfilterset.list",
				"/ash itemfilterset list [Page] [Player]", "/ash itemfilterset list ", 
				"&c/ash itemfilterset list [Seitezahl] [Spielername] &f| Listet ItemFilterSets auf.",
				"&c/ash itemfilterset list [page number] [player name] &f| Lists ItemFilterSets.");
		argumentInput("ash_itemfilterset_name", "name", "ash.cmd.itemfilterset.name",
				"/ash itemfilterset name <Name>", "/ash itemfilterset name ", 
				"&c/ash itemfilterset name <Name> &f| Setzt einen neuen Namen für den ItemFilterSet.", 
				"&c/ash itemfilterset name <Name> &f| Sets a new name for the ItemFilterSet.");
		argumentInput("ash_itemfilterset_select", "select", "ash.cmd.itemfilterset.select",
				"/ash itemfilterset select <Name/ID> [Player]", "/ash itemfilterset select ", 
				"&c/ash itemfilterset select <Name/ID> [Spielername] &f| Wählt einen ItemFilterSet für den Zwischenspeicher aus.", 
				"&c/ash itemfilterset select <Name/ID> [Player] &f| Selects an ItemFilterSet for the cache.");
		argumentInput("ash_itemfilterset_update", "update", "ash.cmd.itemfilterset.update",
				"/ash itemfilterset update ", "/ash itemfilterset update ", 
				"&c/ash itemfilterset update &f| Öffnet einen ItemFilterSet zum Verändern.", 
				"&c/ash itemfilterset update &f| Opens an ItemFilterSet for modification.");
		argumentInput("ash_mode", "mode", "ash.cmd.mode",
				"/ash mode <Modus>", "/ash mode ", 
				"&c/ash mode <Modus> &f| Versetzt dich in einen spezifischen Modus.", 
				"&c/ash mode <Mode> &f| Puts you in a specific mode.");
		argumentInput("ash_playerinfo", "playerinfo", "ash.cmd.playerinfo",
				"/ash playerinfo [Player]", "/ash playerinfo ", 
				"&c/ash playerinfo [Spielername] &f| Zeigt alle Infos von dir oder dem angegebenen Spieler an.", 
				"&c/ash playerinfo [player name] &f| Displays all info about you or the specified player.");
		argumentInput("ash_sc", "storagechest", "ash.cmd.storagechest",
				"/ash storagechest", "/ash storagechest ", 
				"&c/ash storagechest &f| Zwischenbefehl", 
				"&c/ash storagechest &f| Cache command");
		argumentInput("ash_sc_create", "create", "ash.cmd.storagechest.create",
				"/ash storagechest create", "/ash storagechest create ", 
				"&c/ash storagechest create &f| Setzt den Spieler in den Erstellungsmodus für Lagerkisten.", 
				"&c/ash storagechest create &f| Put the player in storagechest create mode.");
		argumentInput("ash_sc_chestname", "chestname", "ash.cmd.storagechest.chestname",
				"/ash storagechest chestname <name> ", "/ash storagechest chestname ", 
				"&c/ash storagechest chestname <Name> &f| Setzt einen neuen Namen für die Lagerkiste.",
				"&c/ash storagechest chestname <Name> &f| Sets a new name for the storagechest");
		argumentInput("ash_sc_delete", "delete", "ash.cmd.storagechest.delete",
				"/ash storagechest delete", "/ash storagechest delete ", 
				"&c/ash storagechest delete &f| Löscht Lagerkiste.", 
				"&c/ash storagechest delete &f| Delete storage crate.");
		argumentInput("ash_sc_info", "info", "ash.cmd.storagechest.info",
				"/ash storagechest info", "/ash storagechest info ", 
				"&c/ash storagechest info &f| Zeigt alle Informationen zu der ausgewählten Lagerkiste an.",
				"&c/ash storagechest info &f| Shows all information about the selected storage crate.");
		argumentInput("ash_sc_list", "list", "ash.cmd.storagechest.list",
				"/ash storagechest list [page] [player] ", "/ash storagechest list ", 
				"&c/ash storagechest list [Seitenzahl] [Spielername] &f| Listet alle LagerkistenIDs des Spielers auf.", 
				"&c/ash storagechest list [page] [player] &f| Lists all the players storagechest IDs.");
		argumentInput("ash_sc_openoption", "openoption", "ash.cmd.storagechest.openoption",
				"/ash storagechest openoption [ID/Chestname]", "/ash storagechest openoption ", 
				"&c/ash storagechest openoption [ID/Kistenname] &f| Öffnet das Options Menu der Lagerkiste.",
				"&c/ash storagechest openoption [ID/Chestname] &f| Opens the Options menu of the storage chest.");
		argumentInput("ash_sc_select", "select", "ash.cmd.storagechest.select",
				"/ash storagechest select <id>", "/ash storagechest select ", 
				"&c/ash storagechest select <ID> &f| Wählt eine Lagerkiste aus.", 
				"&c/ash storagechest select <ID> &f| Selects a storage crate.");
		argumentInput("ash_sc_search", "search", "ash.cmd.storagechest.search",
				"/ash storagechest search [ID/Name] ", "/ash storagechest search ", 
				"&c/ash storagechest search [ID/Name] &f| Sucht Lagerkisten per Partikel. Sollte ein Item in der Hand sein, so werden alle Lagerkisten gesucht, welches dieses Item sortieren.", 
				"&c/ash storagechest search [ID/Name] &f| Searches storage boxes by particle. If an item is in the hand, all storage boxes are searched, which sort this item.");
		/*argumentInput("", "", "",
				"", "", 
				"", "");*/
	}
	
	private void comBypass() //INFO:ComBypass
	{
		String path = "Bypass.";
		commandsKeys.put(path+"Delete"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.delete"}));
		commandsKeys.put(path+"ItemFilterSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.itemfilterset"}));
		commandsKeys.put(path+"ItemFilterSetSelect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.itemfiltersetselect"}));
		commandsKeys.put(path+"ItemFilterSetList"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.itemfiltersetlist"}));
		commandsKeys.put(path+"ItemFilterSetUpdate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.itemfiltersetupdate"}));
		commandsKeys.put(path+"Info"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.info"}));
		commandsKeys.put(path+"Select"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.select"}));
		commandsKeys.put(path+"PlayerInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.playerinfo"}));
		commandsKeys.put(path+"List"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.list"}));
		commandsKeys.put(path+"Search"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.search"}));
		commandsKeys.put(path+"Transfer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.transfer"}));
		commandsKeys.put(path+"Random"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.random"}));
		commandsKeys.put(path+"Reposition"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.reposition"}));
		commandsKeys.put(path+"OpenOption"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.openoption"}));
		commandsKeys.put(path+"Visual"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.visual"}));
		commandsKeys.put(path+"CopyAndPaste"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.copyandpaste"}));
		commandsKeys.put(path+"Sign"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.sign"}));
		commandsKeys.put(path+"ExpertModus"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.expertmodus"}));
		commandsKeys.put(path+"CreateDropper"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.bypass.createdropper"}));
		/*path = "Custom.";
		commandsKeys.put(path+"DistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.distributionchest."}));
		commandsKeys.put(path+"ItemFilterSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.itemfilterset."}));
		commandsKeys.put(path+"StorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.storagechest."}));*/
		/*commandsKeys.put(path+""
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				""}));*/
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("NoPermission", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast dafür keine Rechte!",
						"&cYou dont have the rights!"}));
		languageKeys.put("PlayerNotExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler existiert nicht!",
						"&cThe player does not exist!"}));
		languageKeys.put("PlayerNotOnline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler ist nicht online!",
						"&cThe player is not online!"}));
		languageKeys.put("IllegalArgument", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas angegebene Argument ist keine Zahl!",
						"&cThe given argument is not a number!"}));
		languageKeys.put("InputIsWrong", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDeine Eingabe ist fehlerhaft, klick hier auf den Text um weitere Infos zu bekommen!",
						"&cYour input is incorrect, click here to get more information!"}));
		languageKeys.put("DatabaseError", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cEs gibt einen Datenbank Fehler im Befehl |&f%cmd%&c|! Bitte wende dich an einen Admin!",
						"&cThere is a database error in the command |&f%cmd%&c|! Please contact an admin!"}));
		languageKeys.put("NotOwner", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist nicht der Eigentümer! Zugriff verweigert!",
						"&cYou are not the owner! Access denied!"}));
		languageKeys.put("NotMember", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist kein Mitglied! Zugriff verweigert!",
						"&cYou are not a member! Access denied!"}));
		languageKeys.put("NotOwnerOrMember",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist nicht der Eigentümer oder ein Mitglied! Zugriff verweigert!",
						"&cYou are not the owner or a member! Access denied!"}));
		languageKeys.put("NotSameServer",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist nicht auf dem gleichen Server. Angegebener Server: &f%server%&c. Dein Server &f%yourserver%",
						"&cYou are not on the same server. Specified server: &f%server%&c. Your server &f%yourserver%"}));
		languageKeys.put("BeforeSelect", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung!~!~&eBevor du den Befehl ausführen kannst, klick zuerst auf den davor stehenden Namen oder die ID.",
						"&cAttention!~!~&eBefore you can execute this command, first click on the name or ID in front of it"}));
		languageKeys.put("CancelAction", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAktion Abgebrochen! &eFalls dies unerwünscht ist, die vermeintliche Aktion erneut per Befehl setzen oder auslösen.",
						"&cAction Cancelled! &eIf this is undesired, set or trigger the supposed action again by command."}));
		languageKeys.put("Cooldown", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu klickst zu schnell.",
						"&eYou click too fast."}));
		languageKeys.put("DoubleChestAlreadyAsDistributionChestExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDieser Kistenteil kann nicht registriert werden, da der andere Kistenteil schon registriert ist!",
						"&cThis part of the box cannot be registered, because the other part of the box is already registered!"}));
		languageKeys.put("ChestAlreadyAsDistributionChestExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDieser Block ist schon als Verteilerkiste registriert!",
						"&cThis block is already registered as distribution box!"}));
		languageKeys.put("DistributionIsRunning", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &7Der Verteilungsprozess ist schon aktiv für diesen Block, deshalb kann er nicht geöffnet werden. &eStartzeit: &f%start% &7| &eZu Ende: &f%time%",
						"&cAttention! &7The distribution process is already active for this block, so it cannot be opened. &eStart time: &f%start% &7| &eTo end: &f%time%"}));
		languageKeys.put("SafetyBreak",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! Du bist dabei eine Verteilerkiste abzubauen! Falls das gewollt ist, stelle dieses per Befehl zuerst ein!",
						"&cAttention! You are about to dismantle a distributionchest! If that is wanted, set by command first this!"}));
		languageKeys.put("NoNumber", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%args% &cist keine Zahl!",
						"&cThe argument &f%args% &cis no number!"}));
		languageKeys.put("NumberIsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Zahl &f%args% &cist Negativ! Benutze nur positive Zahlen!",
						"&cThe number &f%args% &cis negative! Use only positive numbers!"}));
		languageKeys.put("NoPlayerAccount", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cSpielerkonten sind nicht aktiv!",
						"&cPlayer accounts are not active!"}));
		languageKeys.put("GeneralHover", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick mich!",
						"&eClick me!"}));
		languageKeys.put("GeneralError",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cEs passiert gerade ein genereller Fehler beim Befehl: &f%cmd% | %message%",
						"&cThere is a general error in the command right now: &f%cmd% %message%"}));
		languageKeys.put("GUI", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"LK: &c%scid% &f%scname% &7| &b%dcid% &e%dcname% &7(&fP:&d%p%&7||&fEnd:&r%e%&7)",
						"SC: &c%scid% &f%scname% &7| &b%dcid% &e%dcname% &7(&fP:&d%p%&7)"}));
		languageKeys.put("PleaseNoItemInHand",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte habe kein Item in der Hand, wenn du den Befehl ausführen willst.",
						"&cPlease do not have an item in your hand when you want to execute the command."}));
		languageKeys.put("CmdAsh.BaseInfo.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&7[&2Lagersystem &bInfo&7]&e===",
						"&e===&7[&2Storage system &bInfo&7]&e==="}));
		languageKeys.put("CmdAsh.BaseInfo.Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e&nnächste Seite &e==>",
						"&e&next page &e==>"}));
		languageKeys.put("CmdAsh.BaseInfo.Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e<== &nvorherige Seite",
						"&e<== &previous page"}));
		languageKeys.put("CmdAsh.AutoDistribution.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&7[&6Infos zur Automatischen Verteilung&7]&e===",
						"&e===&7[&6Infos zur Automatischen Verteilung&7]&e==="}));
		languageKeys.put("CmdAsh.AutoDistribution.TotalTick", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVerteilungszyklus: &f%time%",
						"&cVerteilungszyklus: &f%time%"}));
		languageKeys.put("CmdAsh.AutoDistribution.TicksPerDc",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cZeit pro Verteilerkiste: &f%time%",
						"&cZeit pro Verteilerkiste: &f%time%"}));
		languageKeys.put("CmdAsh.AutoDistribution.NextDc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNächste VerteilerkistenID: &f%id%",
						"&cNächste VerteilerkistenID: &f%id%"}));
		languageKeys.put("CmdAsh.BlockBreak.DeleteDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVerteilerkiste &f%id% &c| &f%name% &cgelöscht!",
						"&cDistribution box &f%id% &c| &f%name% &cdeleted!"}));
		languageKeys.put("CmdAsh.BlockBreak.DeleteSC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cLagerkiste &f%id% &cgelöscht!",
						"&cDistribution box &f%id% &deleted!"}));
		
		languageKeys.put("CmdAsh.BlockInfo.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&7[&6Gefundene Lagersystem&7]&e===",
						"&e==&7[&6Founded storagesystem&7]&e==="}));
		languageKeys.put("CmdAsh.BlockInfo.BlockHasNoStoragesystem",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Block beinhaltet keine Verteiler- oder Lagerkiste!",
						"&cThe block dont contains a distribution- or storagechest!"}));
		languageKeys.put("CmdAsh.BlockInfo.DistributionChestsIntro",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6Gefundene Verteilerkisten:",
						"&6Founded distributionchest:"}));
		languageKeys.put("CmdAsh.BlockInfo.StorageChestsIntro",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6Gefundene Lagerkisten:",
						"&6Founded storagechest:"}));
		languageKeys.put("CmdAsh.BlockInfo.ChestHoverSelect",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier um die Kiste auszuwählen.",
						"&eClick here to select the box."}));
		languageKeys.put("CmdAsh.BlockInfo.ChestHoverInfo",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier alle Informationen zu kiste einzusehen.",
						"&eClick here to view all information about kiste."}));
		languageKeys.put("CmdAsh.BlockInfo.ChestHoverOpenGui",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier um das Menü der Kiste zu öffnen.",
						"&eClick here to open the menu of the box."}));
		
		languageKeys.put("CmdAsh.BlockInfo.Dc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVorhandene Verteilerkisten: ",
						"&cAvailing distribution boxes: "}));
		languageKeys.put("CmdAsh.BlockInfo.Sc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVorhandene Lagerkisten: ",
						"&cAvailable storage boxes: "}));
		languageKeys.put("CmdAsh.Cancel.SetCancel", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eZum+Abbrechen+des+Vorgangs+klick+hier+oder+gib+&f%cmd%+&eein!~click@RUN_COMMAND@%cmd%",
						"&eTo+Cancel+click+here+or+give+&f%cmd%+&on!~click@RUN_COMMAND@%cmd%"}));
		languageKeys.put("CmdAsh.Cancel.IsCancel",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVorgang abgebrochen!",
						"&cProcess aborted!"}));
		languageKeys.put("CmdAsh.CanBreakDC.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu kannst nun Verteilerkisten abbauen. &cSei also vorsichtig!",
						"&eYou can now dismantle distribution chests. &cSo be careful!"}));
		languageKeys.put("CmdAsh.CanBreakDC.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu kannst nun keine Verteilerkisten mehr abbauen!",
						"&eYou can now no longer dismantle distribution boxes!"}));
		
		languageKeys.put("CmdAsh.Convert.PleaseConfirm",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &eDer Konvertierungsprozess wird bei &f%count% &eDatensätzen etwa &f%time% &e(+- ein paar Minuten) lang dauern. Pro Sekunde werden etwa 60 Datensätze bearbeitet. Bitte bestätige den start mit einem &fbestätigen &eam ende des Befehls!",
						"&cAttention! &eTThe conversion process will take about &f%time% &e(+- a few minutes) long for &f%count% &edatasets. About 60 data records are processed per second. Please confirm the start with a &fconfirm &eam end of the command!"}));
		languageKeys.put("CmdAsh.Convert.Start",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cKonvertierungsprozess startet. Ende des Prozess etwa: %time%",
						"Conversion process starts. End of the process about: %time%"}));
		
		languageKeys.put("CmdAsh.CheckUnboundChest.PleaseConfirm",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &eDer prozess wird bei &f%count% &eDatensätzen etwa &f%time% &elang dauern. Pro Sekunde werden etwa 60 Datensätze bearbeitet. Bitte bestätige den start mit einem &fbestätigen &eam ende des Befehls!",
						"&cAttention! &eThe process will take about &f%time% &elong for &f%count% &edatasets. About 60 data records are processed per second. Please confirm the start with a &fconfirm &eam end of the command!"}));
		languageKeys.put("CmdAsh.CheckUnboundChest.Start",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cProzess startet. Ende des Prozess etwa: %time%",
						"&cProcess starts. End of the process about: %time%"}));
		languageKeys.put("CmdAsh.CheckUnboundChest.InProgress",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBefehl ist schon in Benutztung.",
						"&cCommand is already in use."}));
		
		languageKeys.put("CmdAsh.Create.Init", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu bist dabei eine Verteilerkiste zu erstellen! Bitte klick mit &cShift + Rechts &enun die neue Verteilerkiste an!",
						"&eYou are creating a distribution box! Please click with &cShift+Right &nun on the new distribution box!"}));
		languageKeys.put("CmdAsh.Create.NameExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Name existiert schon!",
						"&cThe name already exists!"}));
		languageKeys.put("CmdAsh.Create.TooManyDC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast schon zu viele Verteilerkisten erstellt! Lösche vorher eine, um eine weitere zu erstellen!",
						"&cYou have already created too many distribution boxes! Delete one before to create another one!"}));
		languageKeys.put("CmdAsh.Create.TooManySC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast schon zu viele Lagerkisten erstellt! Lösche vorher eine, um eine weitere zu erstellen!",
						"&cYou have already created too many storage boxes! Delete one first to create another one!"}));
		languageKeys.put("CmdAsh.Create.SetupDChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aVerteilerkiste+&f%dc%+&aerstellt!+&eFür+weitere+Informationen+klick+hier~click@RUN_COMMAND@%cmd%+%dc%",
						"&aDistributionbox+&f%dc%+&created!+&eFor+more+information+click+here~click@RUN_COMMAND@%cmd%+%dc%"}));
		languageKeys.put("CmdAsh.Create.FurtherInstruction", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eSofern keine speziellen Einstellungen vorgenommen werden sollen, so ist das Erstellen von Lagerkisten für die Verteilerkiste &f%dc% &emöglich.",
						"&eAs long as no special settings are to be made, the creation of storage boxes for the distribution box &f%dc% &eis possible"}));
		languageKeys.put("CmdAsh.Create.StorageChestExists",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDiese Kiste wurde von dir schon als Lagerkiste der Verteilerkiste &f%dc% &cregistriert! &eFalls diese Kiste trotzdem als Lagerkiste registriert werden soll, so wähle eine andere Verteilerkiste aus!",
						"&cThis box has already been &cregistered by you as storage box of the distribution box &f%dc%! &eIf you still want to register this crate as storage crate, choose another distribution crate!"}));
		languageKeys.put("CmdAsh.Create.DistributionChestDontExists",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste, welche du ausgewählt oder erstellt hast, existiert nicht mehr!",
						"&cThe distribution box you selected or created no longer exists!"}));
		languageKeys.put("CmdAsh.Create.DistributionChestDontExistsNone",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste, zu dem diese Lagerkiste gehört, existiert nicht mehr!",
						"&cThe distribution box this storage box belongs to no longer exists!"}));
		languageKeys.put("CmdAsh.Create.SetupSC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aLagerkiste+&f%sc%+&aerstellt!+&eFür+weitere+Informationen+klick+hier~click@RUN_COMMAND@%cmd%",
						"&aStoragebox+&f%sc%+&created!+&eFor+more+information+click+here~click@RUN_COMMAND@%cmd%"}));
		languageKeys.put("CmdAsh.Create.SetupSCIFS",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aLagerkiste+&f%sc%+mit+ItemFilterSet+&aerstellt!+&eFür+weitere+Informationen+klick+hier~click@RUN_COMMAND@%cmd%",
						"&aStoragebox+&f%sc%+with+ItemFilterSet+&acreated!+&eFor+more+information+click+here~click@RUN_COMMAND@%cmd%"}));
		languageKeys.put("CmdAsh.Create.SetupSCInventory",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aLagerkiste+&f%sc%+mit+Kisteninventar+&aerstellt!+&eFür+weitere+Informationen+klick+hier~click@RUN_COMMAND@%cmd%",
						"&aStoragebox+&f%sc%+with+chestinventory+&acreated!+&eFor+more+information+click+here~click@RUN_COMMAND@%cmd%"}));
		languageKeys.put("CmdAsh.Create.UpdateStorageChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste+Update!+klick+hier+für+weitere+Informationen~click@RUN_COMMAND@%cmd%",
						"&eStorageBox+Update!+click+here+for+more+information~click@RUN_COMMAND@%cmd%"}));
		languageKeys.put("CmdAsh.Create.SCDontBeSameAsDC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste kann nicht gleichzeitig die Verteilerkiste, des gleichen Lagersystems sein!",
						"&cThe storage box cannot be the distribution box of the same storage system at the same time!"}));
		languageKeys.put("CmdAsh.Create.OtherStorageChestExistAndNoAccess",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDiese Kiste beinhaltet schon eine Lagerkiste eines anderen Lagersystems, wo du keinen Zugriff hast. Somit kannst du keine Lagerkiste eines anderen Lagersystems dort einrichten!",
						"&eThis box already contains a storage box of another storage system, where you have no access. So you can't set up a storage box of another storage system there!"}));
		languageKeys.put("CmdAsh.Create.IsNotChestOrBarrel",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cEs können nur Kisten und Fäßer als Lagerkisten registriert werden!",
						"&cOnly chests and barrels can be registered as storage crates!"}));
		languageKeys.put("CmdAsh.CutAndPaste.NoDcSelected",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Verteilerkiste ausgewählt!",
						"&cYou have not selected a distributionchest!"}));
		languageKeys.put("CmdAsh.CutAndPaste.NoScSelected",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Lagerkisten ausgewählt!",
						"&cYou have not selected a storagechests!"}));
		languageKeys.put("CmdAsh.CutAndPaste.Finished",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEs wurden &f%amount% &eLagerkisten von den alten Lagersystem ausgeschnitten und bei der Verteilerkiste &f%dc% &eeingefügt.",
						"&eThere were &f%amount% &estoragechest cut out from the old storagesystem and inserted at the distributionchest &f%dc%&e."}));
		languageKeys.put("CmdAsh.Delete.IsDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteiler- und Lagerkisten welche sich auf dem Server &f%server% &eund in der Welt &f%world% &ebefanden, wurden gelöscht!",
						"&eDistribution and storagechest which were on the server &f%server% &eand in the world &f%world% &edeleted!"}));
		languageKeys.put("CmdAsh.Delete.DistributionChestDontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste &f%id% &cexistiert nicht!",
						"&cThe distribution box &f%id% &does not exist!"}));
		languageKeys.put("CmdAsh.Delete.DCDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%id% &e| &f%name% &eist &cgelöscht!",
						"&eDistributionChest &f%id% &e| &f%name% &most &cdeleted!"}));
		languageKeys.put("CmdAsh.Delete.LinkedSCDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkisten der Verteilerkiste &f%id% &e| &f%name% &ewurden &cgelöscht&e! Anzahl: &f%count%",
						"&eDistribution box &f%id% &e| &f%name% &have been &deleted&e! quantity: &f%count%"}));
		languageKeys.put("CmdAsh.Delete.StorageChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste &f%id% &cexistiert nicht!",
						"&cThe StorageChest &f%id% &cannot be used!"}));
		languageKeys.put("CmdAsh.Delete.SCDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%id% &eist &cgelöscht!",
						"&eStorage crate &f%id% &is &clear!"}));
		languageKeys.put("CmdAsh.DistributionChest.OtherCmd",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("CmdAsh.DistributionChestList.Empty", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Verteilerkisten!",
						"&cDont have any distribution boxes!"}));
		languageKeys.put("CmdAsh.DistributionChestList.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Verteilerkiste Liste von &f%player%&7]&e=====",
						"&e=====&7[&6DistributionChestList of &f%player%&7]&e======"}));
		languageKeys.put("CmdAsh.DistributionChestName.SetName",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%oldname% &ewurde in &f%newname% &eumbenannt.",
						"&eDistributionChest% &f%oldname% &has been &renamed to &f%newname% &e."}));
		languageKeys.put("CmdAsh.DistributionChestMember.Remove",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eSpieler &f%player% &ewurde von der Verteilerkiste &f%dc% &eals Mitglied entfernt!",
						"&eSpieler &f%player% &has been removed from the distribution box &f%dc% &eas a member!"}));
		languageKeys.put("CmdAsh.DistributionChestMember.Add", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eSpieler &f%player% &ewurde zu der Verteilerkiste &f%dc% &eals Mitglied hinzugefügt!",
						"&eSpieler &f%player% &has been added too the distribution box &f%dc% &eals member!"}));
		languageKeys.put("CmdAsh.DistributionChestSwitch.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBei Verteilerkiste &f%name% &ewurden die Prioritäten nun zu &aaufsteigend &e(Kleinster Wert zuerst) gewechselt.",
						"&eFor distributionchest &f%name% &the priorities have been switched to &aascending &e(lowest value first)"}));
		languageKeys.put("CmdAsh.DistributionChestSwitch.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBei Verteilerkiste &f%name% &ewurden die Prioritäten nun zu &babsteigend &e(Größter Wert zuerst) gewechselt.",
						"&eFor distribution box &f%name% &the priorities have now been changed to &descending &e(Highest value first)"}));

		//REMOVEME (wird die noch irgendwo benutzt?
		languageKeys.put("CmdAsh.Gui.Active", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBaumodus ist aktiviert. Menüs werden nun nicht mehr angezeigt.",
						"&eConstruction mode is activated. GUIs are now no longer displayed."}));
		
		languageKeys.put("CmdAsh.Info.HeadlineDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Verteilerkiste &f%id% &6| &f%name%&7]&e=====",
						"&e====&7[&6Distribution box &f%id% &6| &f%name%&7]&e====="}));
		languageKeys.put("CmdAsh.Info.Owner", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cEigentümer: &f%owner%",
						"&cOwner: &f%owner%"}));
		languageKeys.put("CmdAsh.Info.Member",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cMitglieder: &f%member%",
						"&cMembers: &f%member%"}));
		languageKeys.put("CmdAsh.Info.Location", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPosition: &f%pos%",
						"&cPosition: &f%pos%"}));
		languageKeys.put("CmdAsh.Info.SCAmount", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAnzahl verlinkter Lagerkisten: &f",
						"&cNumber of linked storage boxes: &f"}));
		languageKeys.put("CmdAsh.Info.SCAmountEnd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAnzahl verlinkter Endlagerkisten: &f",
						"&cNumber of linked repository boxes: &f"}));
		languageKeys.put("CmdAsh.Info.HeadlineSC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Lagerkiste &f%id% | %name%&7]&e=====",
						"&e=====&7[&6Storage box &f%id% | %name%&7]&e====="}));
		languageKeys.put("CmdAsh.Info.DCName", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVerteilerkiste: &f%id% &e| &f%name%",
						"&cDistributionchest: &f%id% &e| &f%name%"}));
		languageKeys.put("CmdAsh.Info.AutoDistribution", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAutomatische Verteilung: &f%auto%",
						"&cAutomatic distribution: &f%auto%"}));
		languageKeys.put("CmdAsh.Info.NormalPriority", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt absteigende Priorität: &f%nprio%",
						"&cUses descending priority: &f%nprio%"}));
		languageKeys.put("CmdAsh.Info.PriorityType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPrioritätstyp: &f%priotype%",
						"&cPrioritytype: &f%priotype%"}));
		languageKeys.put("CmdAsh.Info.PriorityNumber", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPrioritätsnummer: &f%prion%",
						"&cPrioritynumber: &f%prion%"}));
		languageKeys.put("CmdAsh.Info.Random", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt zufällige Verteilung: &f%random%",
						"&cUses random distribution: &f%random%"}));
		languageKeys.put("CmdAsh.Info.Priority", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPriorität: &f%prio%",
						"&cPriority: &f%prio%"}));
		languageKeys.put("CmdAsh.Info.Endstorage",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cist Endlagerkiste: &f%end%",
						"&cist final storage box: &f%end%"}));
		languageKeys.put("CmdAsh.Info.OptionDurability",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt Option Haltbarkeit: &f%opdur%",
						"&cUses option durability: &f%opdur%"}));
		languageKeys.put("CmdAsh.Info.DurabilityType",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cHaltbarkeitstyp: &f%durtype%",
						"&cDurabilitytype: &f%durtype%"}));
		languageKeys.put("CmdAsh.Info.DurabilityValue",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cHaltbarkeitswert: &f%dur% &b%",
						"&cDurabilityvalue: &f%dur% &b%"}));
		languageKeys.put("CmdAsh.Info.OptionRepair",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt Option Reparatur: &f%oprepair%",
						"&cUses option repair: &f%oprepair%"}));
		languageKeys.put("CmdAsh.Info.RepairType",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cReparaturtyp: &f%repairtype%",
						"&cRepairtype: &f%repairtype%"}));
		languageKeys.put("CmdAsh.Info.RepairCost",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cReparaturkosten: &f%repaircost% &blvl",
						"&cRepaircost: &f%repaircost% &blvl"}));
		languageKeys.put("CmdAsh.Info.OptionEnchantment",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt Option Verzauberung: &f%opench%",
						"&cUses option enchantment: &f%opench%"}));
		languageKeys.put("CmdAsh.Info.OptionMaterial",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt Option Material: &f%opmat%",
						"&cUses option material: &f%opmat%"}));
		languageKeys.put("CmdAsh.Info.OptionVoid",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt Option Void: &f%opvoid%",
						"&cUses option void: &f%opvoid%"}));
		languageKeys.put("CmdAsh.ItemFilterSet.InventoryName",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilter: &r%name%",
						"&eItemFilter: &r%name%"}));
		languageKeys.put("CmdAsh.InventoryClick.ItemExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cItem existiert schon als ItemFilter!",
						"&cItem already exists as ItemFilter!"}));
		languageKeys.put("CmdAsh.InventoryClick.InventoryFull", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cItemFilter ist bereits voll!",
						"&cItemFilter is already full!"}));
		languageKeys.put("CmdAsh.ItemFilterSet.OtherCmd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("CmdAsh.ItemFilterSet.Create",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%name% &eerstellt.",
						"&eItemFilterSet &f%name% &created."}));
		languageKeys.put("CmdAsh.ItemFilterSet.NotExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas ausgewählte ItemFilterSet existiert nicht!",
						"&cThe selected ItemFilterSet doesnt exist!"}));
		languageKeys.put("CmdAsh.ItemFilterSet.Update", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%name% &eaktualisiert.",
						"&eItemFilterSet &f%name% &updated."}));
		languageKeys.put("CmdAsh.ItemFilterSet.NewName", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%oldname% &ewurde in &f%newname% &eumbenannt!",
						"&eItemFilterSet &f%oldname% &has been &renamed to &f%newname% &elect!"}));
		languageKeys.put("CmdAsh.ItemFilterSetCreate.TooMany", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast schon zu viele ItemFilterSets erstellt. Bitte lösche vorher erst welche um neue zu erstellen.",
						"&cYou have already created too many ItemFilterSets. Please delete some first to create new ones."}));
		languageKeys.put("CmdAsh.ItemFilterSetCreate.AlreadyExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas ItemFilterSet &f%name% &cexistiert schon. Zum Verändern nutzte den Update Befehl!",
						"&cThe ItemFilterSet &f%name% &cexists already. To change it use the Update command!"}));
		languageKeys.put("CmdAsh.ItemFilterSetDelete.NotExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cItemFilterSet &f%name% &cexistiert nicht!",
						"&cItemFilterSet &f%name% &does not &cexist!"}));
		languageKeys.put("CmdAsh.ItemFilterSetDelete.Deleted",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%id% &e| &f%name% &egelöscht!",
						"&eItemFilterSet &f%id% &e| &f%name% &deleted!"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.Empty", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine ItemFilterSets!",
						"&cDont have any ItemFilterSets!"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6ItemFilterSet Liste von &f%player%&7]&e=====",
						"&e=====&7[&6ItemFilterSet list from &f%player%&7]&e====="}));
		languageKeys.put("CmdAsh.ItemFilterSetList.LineOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eAnzahl an Items im Filter: &f%amount%",
						"&eNumber of items in filter: &f%amount%"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.OpenHover", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eÖffnet das ItemFilterSet.~!~",
						"&eopens the ItemFilterSet.~!~"}));
		languageKeys.put("CmdAsh.ItemFilterSetSelect.Selected",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%name% &e| &f%id% &eausgewählt!",
						"&eItemFilterSet &f%name% &e| &f%id% &eselected!"}));
		languageKeys.put("CmdAsh.ItemFilterSetUpdate.NewOne",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &eDir gehört das Original ItemFilterSet nicht! Es wurde daher eine Kopie auf deinen Namen erstellt!",
						"&cAttention! &eYou do not own the original ItemFilterSet! Therefore a copy has been made in your name!"}));
		languageKeys.put("CmdAsh.Limit.StorageChestItemLimit", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDieses Item darf nur %limit% mal in den Lagerkisten des gleichen Lagersystem vorhanden sein!",
						"&cThis item may only be present %limit% times in the storage boxes of the same storage system!"}));
		languageKeys.put("CmdAsh.Limit.AutomaticLimit", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu darfst nur %limit%, Anzahl momentan %amount%, von deinen Verteilerkisten auf Automatische Verteilung stellen!",
						"&cYou may only set %limit% of your distribution chests to Automatic distribution!"}));
		languageKeys.put("CmdAsh.Mode.SetMode", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eModus &f%mode% &egesetzt!",
						"&eMode &f%mode% &eset!"}));
		languageKeys.put("CmdAsh.OpenItemFilter.DontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cLagerkiste mit der ID &f%id% &cexistiert nicht!",
						"&cStorage box with the ID &f%id% &cdoes not &cexist!"}));
		languageKeys.put("CmdAsh.PlayerInfo.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Zwischenspeicher von &f%name%&7]&e=====",
						"&e=====&7[&6Cache of &f%name%&7]&e======"}));
		languageKeys.put("CmdAsh.PlayerInfo.Mode", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cModus: &f%mode%",
						"&cMode: &f%mode%"}));
		languageKeys.put("CmdAsh.PlayerInfo.DC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAusgewählte Verteilerkiste: &f",
						"&cSelected distribution box: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.SC",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAusgewählte Lagerkiste: &f",
						"&cSelected storage box: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.ItemFilterID",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAusgewählte ItemFilterSetID: &f",
						"&cSelected ItemFilterSetID: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.SearchType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cSuchtyp: &f",
						"&cSearch type: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.SelectedStorageChests", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4Ausgewählte Lagerkisten: &f",
						"&4Selected storagechests: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.CHANGEITEMFILTERSET",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSet Änderungsmodus (Das ItemFilterSet wird gerade bearbeitet)",
						"ItemFilterSet change mode (The ItemFilterSet is currently being processed)"}));
		languageKeys.put("CmdAsh.PlayerInfo.CONSTRUCT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Normaler Modus (Du kannst frei bauen und bekommst keine ItemFilterSet bei Shift + Rechtsklick angezeigt)",
						"Normal mode (you can build freely and do not get an ItemFilterSet displayed when shift + right click)"}));
		languageKeys.put("CmdAsh.PlayerInfo.CREATEDISTRIBUTIONCHEST",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Verteilerkisteerstellungsmodus (Du bist dabei Verteilerkisten zu erstellen)",
						"Distribution box creation mode (you are creating distribution boxes)"}));
		languageKeys.put("CmdAsh.PlayerInfo.CREATEITEMFILTERSET",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSeterstellungsmodus (Du erstellst gerade ein ItemFilterSet)",
						"ItemFilterSet creation mode (you are creating an ItemFilterSet)"}));
		languageKeys.put("CmdAsh.PlayerInfo.CREATESTORAGE",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Lagerkistenerstellungsmodus (Du bist gerade dabei Lagerkisten für eine Verteilerkiste zu erstellen)",
						"Storage box creation mode (you are creating storage boxes for a distribution box)"}));
		languageKeys.put("CmdAsh.PlayerInfo.OPTIONGUI", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"OptionsMenü (Du schaust gerade in die Optionen einer Kiste)",
						"OptionGui (You are currently looking at the options of a box part)"}));
		languageKeys.put("CmdAsh.PlayerInfo.UPDATESTORAGEITEMFILTERSET",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSet Aktualisierungsmodus (Du bist dabei, ItemFilterSet von Lagerkisten zu aktualisieren)",
						"ItemFilterSet update mode (you are about to update ItemFilterSet from storage boxes)"}));
		languageKeys.put("CmdAsh.Random.Deactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Randommodus ist nun deaktiviert.",
						"&eThe random mode is nun deactive."}));
		languageKeys.put("CmdAsh.Random.Active", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Randommodus ist nun aktiv!",
						"&eThe random mode is nun active!"}));
		languageKeys.put("CmdAsh.Search.SelectDc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte wähle zuvor erstmal eine Verteilerkiste aus!",
						"&cPlease select a distribution box first!"}));
		languageKeys.put("CmdAsh.Search.SelectSc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte wähle zuvor erstmal eine Lagerkiste aus!",
						"&cPlease select a storage box first!"}));
		languageKeys.put("CmdAsh.Search.SelectScsHeadline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&aAuswählbare Lagerkisten&e===",
						"&e===&aSelectable storage chests&e==="}));
		languageKeys.put("CmdAsh.Search.SelectDcDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Verteilerkiste existiert nicht!",
						"&cThe selected distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Search.SelectScDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Lagerkiste existiert nicht!",
						"&cThe selected stock box does not exist!"}));
		languageKeys.put("CmdAsh.Search.SearchDc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eUm die Verteilerkiste &f%dcid% | %dcname% &ewerden nun Partikel generiert.",
						"&eParticles are now generated around the distributionchest &f%dcid% | %dcname%&e."}));
		languageKeys.put("CmdAsh.Search.SearchSc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eUm die Lagerkiste &f%scid% | %scname% &eder Verteilerkiste &f%dcid% | %dcname% &ewerden nun Partikel generiert.",
						"&eParticles are now generated around the storagechest &f%scid% | %scname% &eof the distributionchest &f%dcid% | %dcname%&e."}));
		languageKeys.put("CmdAsh.Search.SearchItem", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eUm die Lagerkisten der Verteilerkiste &f%dcid% | %dcname%&e, welche das &r%item% &r&esortieren, werden nun Partikel generiert. Anzahl: &f%count%",
						"&eParticles are now generated around the storagechests of the distributionchest &f%dcid% | %dcname% &ethat &sort% the &r%item%&r&e. Quantity: &f%count%"}));
		languageKeys.put("CmdAsh.StorageChest.OtherCmd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("CmdAsh.StorageChestName.SetName",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%oldname% &ewurde in &f%newname% &eumbenannt.",
						"&eStorageChest% &f%oldname% &has been &renamed to &f%newname% &e."}));
		languageKeys.put("CmdAsh.StorageChest.CreateActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eErstellungsmodus für Lagerkisten aktiviert! Verlinkte VerteilerkistenID: &f%id%",
						"&eCreation mode for storage boxes enabled! Linked distribution boxesID: &f%id%"}));
		languageKeys.put("CmdAsh.StorageChestList.Empty", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Lagerkisten!",
						"&cYou dont have any storagechests!"}));
		languageKeys.put("CmdAsh.StorageChestList.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Lagerkiste Liste von &f%player%&7]&e=====",
						"&e=====&7[&6StorageChestList from &f%player%&7]&e====="}));
		languageKeys.put("CmdAsh.StorageChestList.LostChests", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNicht verlinke Lagerkisten&f:",
						"&cNot linked storage boxes&f: "}));
		languageKeys.put("CmdAsh.StorageChestList.InfoHover", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eZeigt alle Infos zu der Lagerkiste an.~!~",
						"&eShows all information about the storage crate.~!~"}));
		languageKeys.put("CmdAsh.StorageChestList.OpenHover", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eÖffnet das Optionsmenu der Lagerkiste.~!~",
						"&eOpens the optionmenu of the storagechest.~!~"}));
		languageKeys.put("CmdAsh.Select.DChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste existiert nicht!",
						"&cThe distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Select.SChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste existiert nicht!",
						"&cThe storage box doesnt exist!"}));
		languageKeys.put("CmdAsh.Select.SelectDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%iddc% &e| &f%name% &eausgewählt!",
						"&eDistribution box &f%iddc% &e| &f%name% &elect!"}));
		languageKeys.put("CmdAsh.Select.SelectSC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%idsc% &e| &f%namesc% &eder Verteilerkiste &f%iddc% &e| &f%namedc% &eausgewählt!",
						"&eStorage crate &f%idsc% &e| &f%name% &ethe distribution crate &f%iddc% &e| &f%name% &elect!"}));
		languageKeys.put("CmdAsh.Select.SelectSCWithOutDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%id% &eausgewählt! Beachte, dass diese Kiste mit keiner Verteilerkiste verlinkt ist!",
						"&eStorage crate &f%id% &elect! Note that this box is not linked to any distribution box!"}));
		languageKeys.put("CmdAsh.Transfer.TransferDc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast die Verteilerkiste &f%id% | %name% &ean &c%player% &eübertragen!",
						"&eYou have transferred the distributionchest &f%id% | %name% &eto &c%player%&e!"}));
		languageKeys.put("CmdAsh.Transfer.TransferDcToYou", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &c%player% &ehat dir den Eigentümerstatus der Verteilerkiste &f%id% | %name% &eübertragen!",
						"&eThe player &c%player% &ehas given you the owner status of the distribution box &f%id% | %name%&e!"}));
		languageKeys.put("CmdAsh.Transfer.TransferScStart", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Transfer der &f%amount% &eLagerkisten beginnt. Der Vorgang kann etwas dauern.",
						"&eThe transfer of the &f%amount% &estorage boxes begins. The process may take some time."}));
		languageKeys.put("CmdAsh.Transfer.TransferScEnd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Transfer der Lagerkisten ist abgeschlossen.",
						"&eThe transfer of the storagechest is finished."}));
		languageKeys.put("CmdAsh.Update.StorageChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste mit der gewählten Verteilerkiste existiert nicht!",
						"&cThe storage box with the selected distribution box does not exist!"}));
		
		languageKeys.put("Interact.Base.DcNotSelected"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Verteilerkiste ausgewählt.",
						"&cYou have not selected a distribution box."}));
		languageKeys.put("Interact.Base.ScNotSelected"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Lagerkiste ausgewählt!",
						"&cYou have not selected a storage box!"}));
		languageKeys.put("Interact.Base.DcDontExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Verteilerkiste existiert nicht!",
						"&cThe selected distribution box does not exist!"}));
		languageKeys.put("Interact.Base.ScDontExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Lagerkiste existiert nicht!",
						"&cThe selected storagechest does not exist!"}));
		languageKeys.put("Interact.Base.ScsDontExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählten Lagerkisten existierten nicht!",
						"&eThe selected storage boxes did not exist!"}));
		languageKeys.put("Interact.Base.ScDcDontExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste der ausgewählten Lagerkiste existiert nicht!",
						"&cThe distribution box of the selected storage box does not exist!"}));
		languageKeys.put("Interact.CopyPaste.TooManyToCopy"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist dabei ein Lagersystem mit zu %mysccount% Lagerkisten zu kopieren. Das Limit beträgt %copylimit%, daher wähle bitte ein kleineres Lagersystem dafür aus.",
						"&cYou are copying a storage system with %mysccount% storage boxes. The limit is %copylimit%, please choose a smaller storage system for it."}));
		languageKeys.put("Interact.CopyPaste.CopyAndPasteTaskRun"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Kopiervorgang start nun. Je nach Größe des Lagersystems wird dies einige Momente dauern.",
						"&eThe copy process will now start. Depending on the size of the storage system, this will take a few moments."}));
		languageKeys.put("Interact.CopyPaste.CopyAndPasteTaskRunFailed"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Kopiervorgang ist fehlgeschlagen! Siehe Konsole!",
						"&cThe copy operation has failed! See console!"}));
		languageKeys.put("Interact.CopyPaste.CopyAndPasteTaskRunSuccess"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Kopiervorgang ist erfolgreich beendet! Kopiert wurde ein Lagersystem von &f%count% &eLagerkisten!",
						"&eThe copying process has been completed successfully! Copied was a storage system from &f%count% &estoragechests!"}));
		languageKeys.put("Interact.Reposition.NoPermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast nicht das Recht um diese Kiste zu versetzen!",
						"&cYou do not have the right to move this box!"}));
		languageKeys.put("Interact.Reposition.DcIsReposition"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%dcid% - %dcname% &ewurde auf ihre neue Position versetzt!",
						"&eThe distributionchest &f%dcid% - %dcname% &has been moved to its new position!"}));
		languageKeys.put("Interact.Reposition.ScIsReposition"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Lagerkiste &f%scid% - %scname% &ewurde auf ihre neue Position versetzt!",
						"&eThe storagechest &f%scid% - %scname% &has been moved to its new position!"}));
		languageKeys.put("Interact.Option.NoPermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast nicht das Recht die Einstellungen aufzurufen!",
						"&cYou do not have the right to access the settings!"}));
		languageKeys.put("Interact.Option.AnimationCooldown"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Partikelanimation für die Verteilerkiste &f%dcid%-%dcname% &cist noch im Cooldown!",
						"&cThe particle animation for the distributionchest &f%dcid%-%dcname% &cis still in cooldown!"}));
		languageKeys.put("Interact.Select.NoPermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast nicht das Recht diese Kiste auszuwählen!",
						"&cYou do not have the right to choose this box!"}));
		languageKeys.put("Gui.Base.GuiNotExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Gui der Yaml-Datei &f%file%.yml &cexistiert nicht!",
						"&cThe gui of the yaml file &f%file%.yml &cexists not!"}));
		languageKeys.put("Gui.Base.DcTitle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVK &f%id%&e-&f%name%",
						"&eDC &f%id%&e-&f%name%"}));
		languageKeys.put("Gui.Base.ScTitle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLK &f%id%&e-&f%name%",
						"&eSC &f%id%&e-&f%name%"}));
		languageKeys.put("Gui.Dc.Chestname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier um den Befehlsvorschlag zum Ändern des Kistennamens zu bekommen.",
						"&eClick here to get the command suggestion to change the box name."}));
		languageKeys.put("Gui.Dc.Member"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier um den Befehlsvorschlag zum Hinzufügen oder Entfernen eines Mitglieds zu bekommen.",
						"&eClick here to get the command suggestion to add or remove a member."}));
		languageKeys.put("Gui.Sc.Chestname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick hier um den Befehlsvorschlag zum Ändern des Kistennamens zu bekommen.",
						"&eClick here to get the command suggestion to change the box name."}));
		languageKeys.put("Gui.Sc.IFSIsNull"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDein ItemFilterSet is nicht vorhanden! Bitte wähle zuerst eins aus!",
						"&cYour ItemFilterSet is not available! Please choose one first!"}));
		languageKeys.put("Gui.Sc.IFSIsOverriden"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast das ItemFilterSet der Lagerkiste mit deinem überschrieben!",
						"&eYou have overwritten the ItemFilterSet of the storagechest with yours!"}));
		languageKeys.put("Sign.SWITCH.SetSWITCH"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%id% - %name% &eist nun nicht mehr auf eine Priorität eingestellt.",
						"&eThe distributionchest &f%id% - %name% &eis now no longer set to a priority."}));
		languageKeys.put("Sign.SWITCH.SetASC"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%id% - %name% &efolgt der Priorität nun aufsteigend.",
						"eThe distributionchest &f%id% - %name% &enow follows the priority in ascending order."}));
		languageKeys.put("Sign.SWITCH.SetDESC"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%id% - %name% &efolgt der Priorität nun absteigend.",
						"eThe distributionchest &f%id% - %name% &enow follows the priority in descending order."}));
		languageKeys.put("Sign.DISTRIBUTE.Start"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%id% - %name% &estartet nun die Verteilung, sofern sie nicht schon vorher gestartet wurde.",
						"eThe distributionchest &f%id% - %name% &enow starts the distribution, if it has not been started before."}));
		languageKeys.put("Sign.PLACE.SetPLACE"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Verteilerkiste &f%id% - %name% &eist nun auf die Priorität &f%prio% &eeingestellt.",
						"&eThe distributionchest &f%id% - %name% &eis now set to the priority &f%prio%&e."}));
		
		languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						""}));
		/*languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"",
				""}))*/
	}
	
	public enum GuiType
	{
		 DC_MAIN, DC_NUMPAD,
		 SC_MAIN, SC_PRIORITY_NUMPAD, SC_DURABILITY_NUMPAD, SC_REPAIR_NUMPAD
	}
	
	private void setSlot(GuiType type, int slot, SettingLevel settingLevel, Material material, String urlTexture,
			String displaynameGER, String displaynameENG,
			String[] itemflag, String[] enchantments, String[] lore)
	{
		if(guiKeys.containsKey(type.toString()))
		{
			LinkedHashMap<String, Language> gui = guiKeys.get(type.toString());
			gui.put(slot+"."+settingLevel.getName()+".Name"
					, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
					displaynameGER,
					displaynameENG}));
			gui.put(slot+"."+settingLevel.getName()+".Material"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					material.toString()}));
			if(urlTexture != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".PlayerHeadTexture"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					urlTexture}));
			}
			if(itemflag != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Itemflag"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						itemflag}));
			}
			if(enchantments != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Enchantments"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						enchantments}));
			}
			if(lore != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Lore"
						, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, lore));
			}
			guiKeys.replace(type.toString(), gui);
		} else
		{
			LinkedHashMap<String, Language> gui = new LinkedHashMap<>();
			gui.put(slot+"."+settingLevel.getName()+".Name"
					, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
					displaynameGER,
					displaynameENG}));
			gui.put(slot+"."+settingLevel.getName()+".Material"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					material.toString()}));
			if(urlTexture != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".PlayerHeadTexture"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					urlTexture}));
			}
			if(itemflag != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Itemflag"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						itemflag}));
			}
			if(enchantments != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Enchantments"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						enchantments}));
			}
			if(lore != null)
			{
				gui.put(slot+"."+settingLevel.getName()+".Lore"
						, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, lore));
			}
			guiKeys.put(type.toString(), gui);
		}
	}
	
	private void setSlot(GuiType type, int slot, Material material, String urlTexture,
			String displaynameGER, String displaynameENG,
			String[] itemflag, String[] enchantments, String[] lore)
	{
		setSlot(type, slot, SettingLevel.BASE, material, urlTexture, displaynameGER, displaynameENG, itemflag, enchantments, lore);
		setSlot(type, slot, SettingLevel.EXPERT, material, urlTexture, displaynameGER, displaynameENG, itemflag, enchantments, lore);
	}
	
	public void initGuis() //INFO:Guis
	{
		setSlot(GuiType.DC_MAIN, 0, SettingLevel.BASE, Material.CHARCOAL,
				null,
				"&eZur &6Experten&e-Einstellung wechseln.",
				"&eSwitch to the &6Expert&e-setting.",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eWechselt zur Ansicht der &6Experten&e-Einstellung,",
				"&ewelche mehr Einstellungsmöglichkeiten sichtbar macht.",
				
				"&eSwitches to the &6Expert&e setting view,",
				"&ewhich makes more setting options visible."
				});
		setSlot(GuiType.DC_MAIN, 0, SettingLevel.EXPERT, Material.NETHERITE_INGOT,
				null,
				"&eZur &fGrund&e-Einstellung wechseln.",
				"&eSwitch to the &fBase&e-setting.",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eWechselt zur Ansicht der &fGrund&e-Einstellung,",
				"&ewelche nur die Basis Einstellungsmöglichkeiten sichtbar macht.",
				
				"&eSwitches to the &fBase&e setting view,",
				"&ewhich makes only the basic setting options visible."
				});
		setSlot(GuiType.DC_MAIN, 4, SettingLevel.BASE, Material.BOOKSHELF,
				null,
				"&eVerteilerkiste &f%id% &e- &f%name%",
				"&eDistributionchest &f%id% &e- &f%name%",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eErstellungsdatum: &f%creationdate%",
				"&eVerteilt automatisch: &f%automaticdistribution%",
				"&eMitglieder: &f%member%",
				"&eOrt: &f%locationone%",
				"&eKoordinaten: &f%locationtwo%",
				"&eAnzahl Lagerkisten: &f%storagechestamount%",
				"&eAnzahl Endlagerkisten: &f%storagechestendamount%",
				
				"&eCreationdate: &f%creationdate%",
				"&eDistribute automatic: &f%automaticdistribution%",
				"&eMember: &f%member%",
				"&eLocation: &f%locationone%",
				"&eCoordinates: &f%locationtwo%",
				"&eAmount Storagechest: &f%storagechestamount%",
				"&eAmount Endstoragechest: &f%storagechestendamount%"
				});
		setSlot(GuiType.DC_MAIN, 4, SettingLevel.EXPERT, Material.BOOKSHELF,
				null,
				"&eVerteilerkiste &f%id% &e- &f%name%",
				"&eDistributionchest &f%id% &e- &f%name%",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eErstellungsdatum: &f%creationdate%",
				"&ePrioritätstyp: &f%type%",
				"&ePrioritätssortierung: &f%status%",
				"&ePrioritätszahl: &f%number%",
				"&eVerteilt automatisch: &f%automaticdistribution%",
				"&eVerteilt zufallig: &f%random%",
				"&eMitglieder: &f%member%",
				"&eOrt: &f%locationone%",
				"&eKoordinaten: &f%locationtwo%",
				"&eAnzahl Lagerkisten: &f%storagechestamount%",
				"&eAnzahl Endlagerkisten: &f%storagechestendamount%",
				
				"&eCreationdate: &f%creationdate%",
				"&ePrioritytype: &f%type%",
				"&ePrioritystate: &f%state%",
				"&ePrioritynumber: &f%number%",
				"&eIs in the Automatic distribution: &f%automaticdistribution%",
				"&eDistrubute random: &f%random%",
				"&eMember: &f%member%",
				"&eLocation: &f%locationone%",
				"&eCoordinates: &f%locationtwo%",
				"&eAmount Storagechest: &f%storagechestamount%",
				"&eAmount Endstoragechest: &f%storagechestendamount%"
				});
		setSlot(GuiType.DC_MAIN, 10, Material.BOOK,
				null,
				"&eKistennamen ändern",
				"&eChestname change", 
				null,
				null,
				null);
		setSlot(GuiType.DC_MAIN, 16, SettingLevel.EXPERT, Material.CRIMSON_SIGN,
				null,
				"&ePrioritätssortierung switchen",
				"&ePrioritysorting switch", 
				null,
				null,
				new String[] {
				"&eKlick hier um die Prioritätssortierung",
				"&ezu switchen.",
				"&ePer Klick wechselt es zu &aauf&e- oder",
				"&cabsteigend &e(false &c↘ &e/true &a↗&e)",
				
				"&eClick here to switch the",
				"&eprioritysorting.",
				"&eBy click it changes to &aascending",
				"&eor &cdescending &e(false &c↘ &e/true &a↗&e)"
				});
		setSlot(GuiType.DC_MAIN, 25, SettingLevel.EXPERT, Material.OAK_SIGN,
				null,
				"&ePrioritätstyp wechseln",
				"&ePrioritytype change", 
				null,
				null,
				new String[] {
				"&eKlick hier um zwischen dem Typ SWITCH &7(&b↔&7)",
				"&eund PLACE &7(&d»۝«&7) &ezu wechseln.",
				"&eSWITCH &7(&b↔&7) &ebedeutet, das es nach der Kistenprioritätssortierung geht.",
				"&ePLACE &7(&d»۝«&7) &ebedeutet, das nur Kisten MIT dieser exakten",
				"&ePriorität angesteuert werden.",
				
				"&eClick here to switch between SWITCH&7(&b↔&7)",
				"&eand PLACE &7(&d»۝«&7) &etype.",
				"&eSWITCH &7(&b↔&7) &emeans that goes after the chest-priority-sorting.",
				"&ePLACE &7(&d»۝«&7) &emeans that only boxes WITH this",
				"&eexact priority are controlled."
				});
		setSlot(GuiType.DC_MAIN, 34, SettingLevel.EXPERT, Material.JUNGLE_SAPLING,
				null,
				"&ePrioritätszahl setzten",
				"&ePrioritynumber set", 
				null,
				null,
				new String[] {
				"&eLeitet dich zu einem weiteren Menü,",
				"&ewo du die exakte Zahl eingeben kannst.",
				"&eDirects you to another gui,",
				"&ewhere you can enter the exact number."
				});
		setSlot(GuiType.DC_MAIN, 28, Material.CHEST_MINECART,
				null,
				"&eAutomatische Verteilung",
				"&eAutomatic distribution", 
				null,
				null,
				new String[] {
				"&eWenn du hier klickst, schaltest du",
				"&edie automatische Verteilung ein oder aus.",
				"&eIf you click here, you will activate",
				"&eor deactivate the Automatic distribution."
				});
		setSlot(GuiType.DC_MAIN, 46, SettingLevel.EXPERT, Material.SUSPICIOUS_STEW,
				null,
				"&eZufällige Verteilung",
				"&eRandom distribution", 
				null,
				null,
				new String[] {
				"&eWenn du hier klickst, schaltest du",
				"&edie zufällige Verteilung ein oder aus.",
				"&eIf you click here, you will activate",
				"&eor deactivate the random distribution."
				});
		setSlot(GuiType.DC_MAIN, 52, Material.FILLED_MAP,
				null,
				"&eMitglieder hinzufügen oder entfernen.",
				"&eAdd or remove members.", 
				null,
				null,
				null);
		//INFO DC_NUMPAD
		setSlot(GuiType.DC_NUMPAD, 4, Material.BOOKSHELF,
				null,
				"&eVerteilerkiste &f%id% &e- &f%name%",
				"&eDistributionchest &f%id% &e- &f%name%",  
				null,
				null,
				new String[] {
				"&eKlick auf das Numpad zum Eingeben der exakten Zahl.",
				"&eKlick auf >&fC&e< um die Zahl auf 0 zu resetten.",
				"&eAktuelle Prioritätszahl: &b%prioritynumber%",
				"&eClick on the numpad to enter the exact number.",
				"&eClick >&fC&e< to reset the number to 0.",
				"&eCurrent priority number: &b%prioritynumber%"
				});
		setSlot(GuiType.DC_NUMPAD, 13, Material.PLAYER_HEAD, //C
				"http://textures.minecraft.net/texture/abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f",
				"&eC",
				"&eC", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 14, Material.PLAYER_HEAD, //N
				"http://textures.minecraft.net/texture/bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193",
				"&eVorzeichen ändern",
				"&eChange sign", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 23, Material.PLAYER_HEAD, //9
				"http://textures.minecraft.net/texture/e67caf7591b38e125a8017d58cfc6433bfaf84cd499d794f41d10bff2e5b840",
				"&e9",
				"&e9", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 22, Material.PLAYER_HEAD, //8
				"http://textures.minecraft.net/texture/59194973a3f17bda9978ed6273383997222774b454386c8319c04f1f4f74c2b5",
				"&e8",
				"&e8", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 21, Material.PLAYER_HEAD, //7
				"http://textures.minecraft.net/texture/6db6eb25d1faabe30cf444dc633b5832475e38096b7e2402a3ec476dd7b9",
				"&e7",
				"&e7", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 32, Material.PLAYER_HEAD, //6
				"http://textures.minecraft.net/texture/334b36de7d679b8bbc725499adaef24dc518f5ae23e716981e1dcc6b2720ab",
				"&e6",
				"&e6", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 31, Material.PLAYER_HEAD, //5
				"http://textures.minecraft.net/texture/6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2",
				"&e5",
				"&e5", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 30, Material.PLAYER_HEAD, //4
				"http://textures.minecraft.net/texture/d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5",
				"&e4",
				"&e4", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 41, Material.PLAYER_HEAD, //3
				"http://textures.minecraft.net/texture/1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5",
				"&e3",
				"&e3", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 40, Material.PLAYER_HEAD, //2
				"http://textures.minecraft.net/texture/4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847",
				"&e2",
				"&e2", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 39, Material.PLAYER_HEAD, //1
				"http://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530",
				"&e1",
				"&e1", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 49, Material.PLAYER_HEAD, //0
				"http://textures.minecraft.net/texture/0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27",
				"&e0",
				"&e0", 
				null,
				null,
				null);
		setSlot(GuiType.DC_NUMPAD, 53, Material.ARROW, //Zurück
				null,
				"&eZurück",
				"&eBack", 
				null,
				null,
				null);
		//INFO SC_MAIN
		setSlot(GuiType.SC_MAIN, 0, SettingLevel.BASE, Material.CHARCOAL,
				null,
				"&eZur &6Experten&e-Einstellung wechseln.",
				"&eSwitch to the &6Expert&e-setting.",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eWechselt zur Ansicht der &6Experten&e-Einstellung,",
				"&ewelche mehr Einstellungsmöglichkeiten sichtbar macht.",
				
				"&eSwitches to the &6Expert&e setting view,",
				"&ewhich makes more setting options visible."
				});
		setSlot(GuiType.SC_MAIN, 0, SettingLevel.EXPERT, Material.NETHERITE_INGOT,
				null,
				"&eZur &fGrund&e-Einstellung wechseln.",
				"&eSwitch to the &fBase&e-setting.",
				null,//Itemflag
				null,//Ench
				new String[] {
				"&eWechselt zur Ansicht der &fGrund&e-Einstellung,",
				"&ewelche nur die Basis Einstellungsmöglichkeiten sichtbar macht.",
				
				"&eSwitches to the &fBase&e setting view,",
				"&ewhich makes only the basic setting options visible."
				});
		setSlot(GuiType.SC_MAIN, 4, SettingLevel.BASE, Material.BOOKSHELF,
				null,
				"&eLagerkiste &f%id% &e- &f%name%",
				"&eStoragechest &f%id% &e- &f%name%",
				null,
				null,
				new String[] {
				"&eEigentümer: &f%owner%",
				"&eErstellungsdatum: &f%creationdate%",
				"&eVerteilerkisten ID: &f%distributionchestid%",
				"&eIst Endlagerkiste: &f%isendstorage%",
				"&eOrt: &f%locationone%",
				"&eKoordinaten: &f%locationtwo%",
				
				"&eOwner: &f%owner%",
				"&eCreationdate: &f%creationdate%",
				"&eDistributionchest ID: &f%distributionchestid%",
				"&eIs endstoragechest: &f%isendstorage%",
				"&eLocation: &f%locationone%",
				"&eCoordinates: &f%locationtwo%"
				});
		setSlot(GuiType.SC_MAIN, 4, SettingLevel.EXPERT, Material.BOOKSHELF,
				null,
				"&eLagerkiste &f%id% &e- &f%name%",
				"&eStoragechest &f%id% &e- &f%name%",
				null,
				null,
				new String[] {
				"&eEigentümer: &f%owner%",
				"&eErstellungsdatum: &f%creationdate%",
				"&eVerteilerkisten ID: &f%distributionchestid%",
				"&ePrioritätszahl: &f%priority%",
				"&eIst Endlagerkiste: &f%isendstorage%",
				"&eOption Void: &f%isvoid%",
				"&eOption Material: &f%material%",
				"&eOption Haltbarkeit: &f%isdurability%",
				"&eHaltbarkeitstyp: &f%durabilitytype%",
				"&eHaltbarkeit: &f%durability% &b%",
				"&eOption Reparaturkosten: &f%isrepair%",
				"&eReparaturtyp: &f%repairtype%",
				"&eReparaturkosten: &f%repaircost% &blvl",
				"&eOption Verzauberung: &f%isenchantment%",
				"&eOrt: &f%locationone%",
				"&eKoordinaten: &f%locationtwo%",
				
				"&eOwner: &f%owner%",
				"&eCreationdate: &f%creationdate%",
				"&eDistributionchest ID: &f%distributionchestid%",
				"&ePrioritynumber: &f%priority%",
				"&eIs endstoragechest: &f%isendstorage%",
				"&eOption Void: &f%isvoid%",
				"&eOption material: &f%material%",
				"&eOption durability: &f%isdurability%",
				"&eDurabilitytype: &f%durabilitytype%",
				"&eDurability: &f%durability% &b%",
				"&eOption repaircost: &f%isrepair%",
				"&eRepairtype: &f%repairtype%",
				"&eRepaircost: &f%repaircost% &blvl",
				"&eOption enchantments: &f%isenchantment%",
				"&eLocation: &f%locationone%",
				"&eCoordinates: &f%locationtwo%"
				});
		setSlot(GuiType.SC_MAIN, 11, Material.BOOK,
				null,
				"&eKistennamen ändern",
				"&eChestname change", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 12, Material.BARREL,
				null,
				"&eEndlagerkisten &a✔ &eoder &c✖&e.",
				"&eEndstoragechest &a✔ &eoder &c✖&e.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 14, SettingLevel.EXPERT, Material.PLAYER_HEAD,
				"http://textures.minecraft.net/texture/9ae85f74f8e2c054b781a29fa9b25934ba63bb79f1de8a95b436d9bfdcaf4cd",
				"&ePrioritätszahl setzten",
				"&ePrioritynumber set",
				null,
				null,
				new String[] {
				"&eLeitet dich zu einem weiteren Menü,",
				"&ewo du die exakte Zahl eingeben kannst.",
				"&eDirects you to another gui,",
				"&ewhere you can enter the exact number."
				});
		setSlot(GuiType.SC_MAIN, 15, SettingLevel.EXPERT, Material.BUCKET,
				null,
				"&eVoid-Option &a✔ &eoder &c✖&e.",
				"&eVoid option &a✔ &eor &c✖&e.", 
				null,
				null,
				new String[] {
				"&eWenn du hier klickst, schaltest du",
				"&edie automatische Löschung ein oder aus.",
				"&eIf you click here, you will activate",
				"&eor deactivate the automatic delete."
				});
		setSlot(GuiType.SC_MAIN, 19, SettingLevel.EXPERT, Material.SMITHING_TABLE,
				null,
				"&eHaltbarkeit-Option &a✔ &eoder &c✖&e.",
				"&eDurability option &a✔ &eor &c✖&e.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 28, SettingLevel.EXPERT, Material.WOODEN_SWORD,
				null,
				"&eHaltbarkeittyp wechseln",
				"&eDurabilitytype switch.", 
				null,
				null,
				new String[] {
				"&eZwischen &fkleiner als &7(&a<&7)",
				"&eund &fgrößer als &7(&c>&7) &ewechseln.",
				"&eBetween &fless than &7(&a<&7)",
				"&eand &flarger than &7(&c<&7) &eswitching."});
		setSlot(GuiType.SC_MAIN, 37, SettingLevel.EXPERT, Material.WOODEN_SHOVEL,
				null,
				"&eHaltbarkeitswert setzen.",
				"&eDurabilityvalue set.", 
				null,
				null,
				new String[] {
				"&eLeitet dich zu einem weiteren Menü,",
				"&ewo du die exakte Zahl eingeben kannst.",
				"&eDirects you to another gui,",
				"&ewhere you can enter the exact number."
				});
		setSlot(GuiType.SC_MAIN, 25, SettingLevel.EXPERT, Material.ANVIL,
				null,
				"&eReparatur-Option &a✔ &eoder &c✖&e.",
				"&eReparation option &a✔ &eor &c✖&e.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 34, SettingLevel.EXPERT, Material.CHIPPED_ANVIL,
				null,
				"&eReparaturtyp wechseln.",
				"&eReparationtype switch.", 
				null,
				null,
				new String[] {
				"&eZwischen &fkleiner als &7(&a<&7)",
				"&eund &fgrößer als &7(&c>&7) &ewechseln.",
				"&eBetween &fless than &7(&a<&7)",
				"&eand &flarger than &7(&c<&7) &eswitching."});
		setSlot(GuiType.SC_MAIN, 43, SettingLevel.EXPERT, Material.DAMAGED_ANVIL,
				null,
				"&eReparaturwert setzen.",
				"&eReparationvalue set.", 
				null,
				null,
				new String[] {
				"&eLeitet dich zu einem weiteren Menü,",
				"&ewo du die exakte Zahl eingeben kannst.",
				"&eDirects you to another gui,",
				"&ewhere you can enter the exact number."
				});
		setSlot(GuiType.SC_MAIN, 39, SettingLevel.EXPERT, Material.ENCHANTING_TABLE,
				null,
				"&eVerzauberungsoption &a✔ &eoder &c✖&e.",
				"&eEnchantment option &a✔ &eor &c✖&e.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 48, SettingLevel.EXPERT, Material.CRAFTING_TABLE,
				null,
				"&eMaterial-Option &a✔ &eoder &c✖&e.",
				"&eMaterial option &a✔ &eor &c✖&e.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 41, Material.FILLED_MAP,
				null,
				"&eÖffne den ItemFilterSet der Kiste.",
				"&eOpen the ItemFilterSet of the chest.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 45, SettingLevel.EXPERT, Material.BARRIER,
				null,
				"&cLöscht diese Lagerkiste.",
				"&cDelete this storagechest.", 
				null,
				null,
				null);
		setSlot(GuiType.SC_MAIN, 53, SettingLevel.EXPERT, Material.MAP,
				null,
				"&eFilter mit deinem ItemFilterSet überschreiben.",
				"&eOverwrite filter with your ItemFilterSet.", 
				null,
				null,
				null);
		//INFO SC_PRIORITY_NUMPAD
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 4, Material.BOOKSHELF,
				null,
				"&eLagerkiste &f%id% &e- &f%name%",
				"&eStoragechest &f%id% &e- &f%name%",  
				null,
				null,
				new String[] {
				"&eKlick auf das Numpad zum Eingeben der exakten Zahl.",
				"&eKlick auf >&fC&e< um die Zahl auf 0 zu resetten.",
				"&eAktuelle Prioritätszahl: &b%priority%",
				"&eClick on the numpad to enter the exact number.",
				"&eClick >&fC&e< to reset the number to 0.",
				"&eCurrent priority number: &b%priority%"
				});
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 13, Material.PLAYER_HEAD, //C
				"http://textures.minecraft.net/texture/abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f",
				"&eC",
				"&eC", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 14, Material.PLAYER_HEAD, //N
				"http://textures.minecraft.net/texture/bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193",
				"&eVorzeichen ändern",
				"&eChange sign", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 23, Material.PLAYER_HEAD, //9
				"http://textures.minecraft.net/texture/e67caf7591b38e125a8017d58cfc6433bfaf84cd499d794f41d10bff2e5b840",
				"&e9",
				"&e9", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 22, Material.PLAYER_HEAD, //8
				"http://textures.minecraft.net/texture/59194973a3f17bda9978ed6273383997222774b454386c8319c04f1f4f74c2b5",
				"&e8",
				"&e8", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 21, Material.PLAYER_HEAD, //7
				"http://textures.minecraft.net/texture/6db6eb25d1faabe30cf444dc633b5832475e38096b7e2402a3ec476dd7b9",
				"&e7",
				"&e7", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 32, Material.PLAYER_HEAD, //6
				"http://textures.minecraft.net/texture/334b36de7d679b8bbc725499adaef24dc518f5ae23e716981e1dcc6b2720ab",
				"&e6",
				"&e6", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 31, Material.PLAYER_HEAD, //5
				"http://textures.minecraft.net/texture/6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2",
				"&e5",
				"&e5", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 30, Material.PLAYER_HEAD, //4
				"http://textures.minecraft.net/texture/d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5",
				"&e4",
				"&e4", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 41, Material.PLAYER_HEAD, //3
				"http://textures.minecraft.net/texture/1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5",
				"&e3",
				"&e3", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 40, Material.PLAYER_HEAD, //2
				"http://textures.minecraft.net/texture/4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847",
				"&e2",
				"&e2", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 39, Material.PLAYER_HEAD, //1
				"http://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530",
				"&e1",
				"&e1", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 49, Material.PLAYER_HEAD, //0
				"http://textures.minecraft.net/texture/0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27",
				"&e0",
				"&e0", 
				null,
				null,
				null);
		setSlot(GuiType.SC_PRIORITY_NUMPAD, 53, Material.ARROW, //Zurück
				null,
				"&eZurück",
				"&eBack", 
				null,
				null,
				null);
		//INFO SC_DURABILITY_NUMPAD
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 4, Material.BOOKSHELF,
				null,
				"&eLagerkiste &f%id% &e- &f%name%",
				"&eStoragechest &f%id% &e- &f%name%",  
				null,
				null,
				new String[] {
				"&eKlick auf das Numpad zum Eingeben der exakten Zahl.",
				"&eKlick auf >&fC&e< um die Zahl auf 0 zu resetten.",
				"&eAktuelle Haltbarkeit in Prozent: &b%durability% &e%",
				"&eClick on the numpad to enter the exact number.",
				"&eClick >&fC&e< to reset the number to 0.",
				"&eCurrent durabiliypercent: &v%durability%"
				});
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 13, Material.PLAYER_HEAD, //C
				"http://textures.minecraft.net/texture/abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f",
				"&eC",
				"&eC", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 23, Material.PLAYER_HEAD, //9
				"http://textures.minecraft.net/texture/e67caf7591b38e125a8017d58cfc6433bfaf84cd499d794f41d10bff2e5b840",
				"&e9",
				"&e9", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 22, Material.PLAYER_HEAD, //8
				"http://textures.minecraft.net/texture/59194973a3f17bda9978ed6273383997222774b454386c8319c04f1f4f74c2b5",
				"&e8",
				"&e8", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 21, Material.PLAYER_HEAD, //7
				"http://textures.minecraft.net/texture/6db6eb25d1faabe30cf444dc633b5832475e38096b7e2402a3ec476dd7b9",
				"&e7",
				"&e7", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 32, Material.PLAYER_HEAD, //6
				"http://textures.minecraft.net/texture/334b36de7d679b8bbc725499adaef24dc518f5ae23e716981e1dcc6b2720ab",
				"&e6",
				"&e6", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 31, Material.PLAYER_HEAD, //5
				"http://textures.minecraft.net/texture/6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2",
				"&e5",
				"&e5", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 30, Material.PLAYER_HEAD, //4
				"http://textures.minecraft.net/texture/d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5",
				"&e4",
				"&e4", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 41, Material.PLAYER_HEAD, //3
				"http://textures.minecraft.net/texture/1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5",
				"&e3",
				"&e3", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 40, Material.PLAYER_HEAD, //2
				"http://textures.minecraft.net/texture/4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847",
				"&e2",
				"&e2", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 39, Material.PLAYER_HEAD, //1
				"http://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530",
				"&e1",
				"&e1", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 49, Material.PLAYER_HEAD, //0
				"http://textures.minecraft.net/texture/0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27",
				"&e0",
				"&e0", 
				null,
				null,
				null);
		setSlot(GuiType.SC_DURABILITY_NUMPAD, 53, Material.ARROW, //Zurück
				null,
				"&eZurück",
				"&eBack", 
				null,
				null,
				null);
		//INFO SC_REPAIR_NUMPAD
		setSlot(GuiType.SC_REPAIR_NUMPAD, 4, Material.BOOKSHELF,
				null,
				"&eLagerkiste &f%id% &e- &f%name%",
				"&eStoragechest &f%id% &e- &f%name%",  
				null,
				null,
				new String[] {
				"&eKlick auf das Numpad zum Eingeben der exakten Zahl.",
				"&eKlick auf >&fC&e< um die Zahl auf 0 zu resetten.",
				"&eAktuelle Reparaturkosten: &b%repaircost% &elvl",
				"&eClick on the numpad to enter the exact number.",
				"&eClick >&fC&e< to reset the number to 0.",
				"&eCurrent repaircosts: &b%repaircost% &elvl"
				});
		setSlot(GuiType.SC_REPAIR_NUMPAD, 13, Material.PLAYER_HEAD, //C
				"http://textures.minecraft.net/texture/abe983ec478024ec6fd046fcdfa4842676939551b47350447c77c13af18e6f",
				"&eC",
				"&eC", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 23, Material.PLAYER_HEAD, //9
				"http://textures.minecraft.net/texture/e67caf7591b38e125a8017d58cfc6433bfaf84cd499d794f41d10bff2e5b840",
				"&e9",
				"&e9", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 22, Material.PLAYER_HEAD, //8
				"http://textures.minecraft.net/texture/59194973a3f17bda9978ed6273383997222774b454386c8319c04f1f4f74c2b5",
				"&e8",
				"&e8", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 21, Material.PLAYER_HEAD, //7
				"http://textures.minecraft.net/texture/6db6eb25d1faabe30cf444dc633b5832475e38096b7e2402a3ec476dd7b9",
				"&e7",
				"&e7", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 32, Material.PLAYER_HEAD, //6
				"http://textures.minecraft.net/texture/334b36de7d679b8bbc725499adaef24dc518f5ae23e716981e1dcc6b2720ab",
				"&e6",
				"&e6", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 31, Material.PLAYER_HEAD, //5
				"http://textures.minecraft.net/texture/6d57e3bc88a65730e31a14e3f41e038a5ecf0891a6c243643b8e5476ae2",
				"&e5",
				"&e5", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 30, Material.PLAYER_HEAD, //4
				"http://textures.minecraft.net/texture/d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5",
				"&e4",
				"&e4", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 41, Material.PLAYER_HEAD, //3
				"http://textures.minecraft.net/texture/1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5",
				"&e3",
				"&e3", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 40, Material.PLAYER_HEAD, //2
				"http://textures.minecraft.net/texture/4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847",
				"&e2",
				"&e2", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 39, Material.PLAYER_HEAD, //1
				"http://textures.minecraft.net/texture/71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530",
				"&e1",
				"&e1", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 49, Material.PLAYER_HEAD, //0
				"http://textures.minecraft.net/texture/0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27",
				"&e0",
				"&e0", 
				null,
				null,
				null);
		setSlot(GuiType.SC_REPAIR_NUMPAD, 53, Material.ARROW, //Zurück
				null,
				"&eZurück",
				"&eBack", 
				null,
				null,
				null);
	}
	
	public void initLimits() //INFO:Limits
	{
		limitsKeys.put("MaximumItemFilterSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				500}));
		limitsKeys.put("AutomaticDistributionChestLimitPermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"ash.automatic.limit."}));
		limitsKeys.put("StorageChestLimitPerItemType"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						25}));
		limitsKeys.put("StorageChestLimitPerItemTypeException"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						50}));
		limitsKeys.put("StorageChestLimitPerItemTypeExceptionList"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						"STONE",
						"SAND",
						"DIRT"}));
	}
}
