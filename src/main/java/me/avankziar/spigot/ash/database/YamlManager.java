package main.java.me.avankziar.spigot.ash.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.spigot.ash.database.Language.ISO639_2B;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initLanguage();
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
		configKeys.put("Mysql.TableNameI"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashPluginUser"}));
		configKeys.put("Mysql.TableNameII"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashDistributionChest"}));
		configKeys.put("Mysql.TableNameIII"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashStorageChest"}));
		configKeys.put("Mysql.TableNameIV"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashItemFilterSet"}));
		configKeys.put("Mysql.TableNameV"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashTransferLog"}));
		configKeys.put("Mysql.TableNameVI"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ashCrossServerTransfer"}));
		//Scheduler to automatic distribute. If false nothing running.
		configKeys.put("RunTransferSchedularTimer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//In Minuten when the Schedular run and distributed the items to the storagechests.
		configKeys.put("TransferSchedularTimer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				15}));
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
		configKeys.put("UseDynamicFormat"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
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
		//FIXME Werden die 3 nachfolgenden Werte korrekt verarbeitet?
		configKeys.put("maximumDistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				500}));
		configKeys.put("maximumStorageChestPerDistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				500}));
		configKeys.put("maximumItemFilterSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				500}));
		configKeys.put("IsAutomaticDistribution"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("BlockBreakDistributionChestDeleteLinkedStorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//The sphere radius to interact and trigger Distributionchests. Attention! To high number cann occur lags.
		configKeys.put("ButtonPlateInteractRadius"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3}));
		/*
		 * If UseDelayedDistribution is false can lags occur!
		 * If true, per Tick it works off a storagechest. And in the next tick, the next storagechest is processed etc.
		 */
		configKeys.put("UseDelayedDistribution"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//A Tick is 50 ms. Between 1 and 2 Ticks may should be enough for ~50 - 100 StorageChests per DistributionChest.
		configKeys.put("DelayedTicks"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				1}));
		//Seconds, when the chain will be activated
		configKeys.put("DelayChainChests"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		//Ticks, how fast per Distributionchest looping.
		configKeys.put("DelayedChainTicks"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
		//Use this in the dynamic Format to upspeed the distribution with the next value
		configKeys.put("UseFastDelayedDistribution"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		//The value set, how many storagechest per tick in a loop is processed
		configKeys.put("ChestsPerTick"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				10}));
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
				"&c/ash automaticdistributioninfo &f| Zeigt alle Info zum Automatischen Verteilen an.",
				""); //FIXME Alle fehlende Einträge müssen übersetzt werden. Aber erst nach dem die GUIS eingebaut wurden.
		argumentInput("ash_blockinfo", "blockinfo", "ash.cmd.blockinfo",
				"/ash blockinfo", "/ash blockinfo ", 
				"&c/ash blockinfo &f| Zeigt mit shift+Rechtsklick alle Blocksinfo vom Lagersystem.",
				"&c/ash blockinfo &f| Show all block info from the storage system with shift+right click");
		argumentInput("ash_cancel", "cancel", "ash.cmd.cancel",
				"/ash cancel", "/ash cancel ", 
				"&c/ash cancel &f| Bricht alle Aktionen ab.",
				"&c/ash cancel &f| Cancels all actions");
		argumentInput("ash_debug", "debug", "ash.cmd.debug",
				"/ash debug", "/ash debug ", 
				"&c/ash debug &f| Zwischenbefehl",
				"&c/ash debug &f| Intermediate command.");
		argumentInput("ash_debug_itemmeta", "itemmeta", "ash.cmd.debug.itemmeta",
				"/ash debug itemmeta", "/ash debug itemmeta ", 
				"&c/ash debug itemmeta &f| Zeigt einen String der Itemmeta des Items in der Haupthand an.",
				"");
		argumentInput("ash_delete", "delete", "ash.cmd.delete",
				"/ash delete <server> <world>", "/ash delete ", 
				"&c/ash delete <server> <welt> &f| Löscht alle Verteiler- und Lagerkisten die sich auf diesem Server und in dieser Welt befinden.",
				"");
		argumentInput("ash_dc", "distributionchest", "ash.cmd.distributionchest",
				"/ash distributionchest", "/ash distributionchest ", 
				"&c/ash distributionchest &f| Zwischenbefehl.",
				"&c/ash distributionchest &f| Intermediate command.");
		argumentInput("ash_dc_autodistr", "automaticdistribution", "ash.cmd.distributionchest.automaticdistribution",
				"/ash distributionchest automaticdistribution", "/ash distributionchest automaticdistribution ", 
				"&c/ash distributionchest automaticdistribution &f| Toggelt, ob der Schedular die Kiste absaugen und die Items verteilen kann.",
				"&c/ash distributionchest automaticdistribution &f| Toggles whether the scheduler can suck the crate and distribute the items");
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
				"/ash distributionchest delete [true/false(DeleteAllLinkedStorageChest)]", " /ash distributionchest delete ", 
				"&c/ash distributionchest delete &f| Löscht die vorher ausgewählte Verteilerkiste.", 
				"&c/ash distributionchest delete &f| Deletes the previously selected distribution box");
		argumentInput("ash_dc_info", "info", "ash.cmd.distributionchest.info",
				"/ash distributionchest info", "/ash distributionchest info ", 
				"&c/ash distributionchest info <ID> &f| Listet alle Infos der Verteilerkiste auf.",
				"&c/ash distributionchest info <ID> &f| Lists all the information in the distribution box");
		argumentInput("ash_dc_list", "list", "ash.cmd.distributionchest.list",
				"/ash distributionchest list", "/ash distributionchest list ", 
				"&c/ash distributionchest list &f| Listet alle IDs und Namen deiner Verteilerkistena auf.", 
				"&c/ash distributionchest list &f| Lists all IDs and names of your distribution boxes.");
		argumentInput("ash_dc_member", "member", "ash.cmd.distributionchest.member",
				"/ash distributionchest member <playername>", "/ash distributionchest member ", 
				"&c/ash distributionchest member <Spielername> &f| Fügt einen Spieler der Mitgliedsliste hinzu oder entfernt ihn.",
				"&c/ash distributionchest member <playername> &f| Add or remove a player from the member list.");
		argumentInput("ash_dc_position", "position", "ash.cmd.distributionchest.position",
				"/ash distributionchest position", "/ash distributionchest position ", 
				"&c/ash distributionchest position &f| Setzt den Spieler in den Positionswechselmodus für Verteilerkisten.", 
				"&c/ash distributionchest position &f| Put the player in distributionchest position change mode");
		argumentInput("ash_dc_random", "random", "ash.cmd.distributionchest.random",
				"/ash distributionchest random", "/ash distributionchest random ", 
				"&c/ash distributionchest random &f| Aktiviert oder deaktiviert den Randommodus.", 
				"&c/ash distributionchest random &f| Activated or deactivated the random mode.");
		argumentInput("ash_dc_select", "select", "ash.cmd.distributionchest.select",
				"/ash distributionchest select <Name> [PlayerName]", "/ash distributionchest select ", 
				"&c/ash distributionchest select <ID> &f| Wählt eine Verteilerkiste aus.", 
				"&c/ash distributionchest select <ID> &f| Selects a distribution box.");
		argumentInput("ash_dc_search", "search", "ash.cmd.distributionchest.search",
				"/ash distributionchest search", "/ash distributionchest search ", 
				"&c/ash distributionchest search &f| Sucht die ausgewählte Verteilerkiste.", 
				"&c/ash distributionchest search &f| Searches the selected distribution box.");
		argumentInput("ash_dc_switch", "switch", "ash.cmd.distributionchest.switch",
				"/ash distributionchest switch", "/ash distributionchest switch ", 
				"&c/ash distributionchest switch &f| Toggelt ob die Prioriäten aufsteigend oder absteigend berücksichtig werden sollen.", 
				"&c/ash distributionchest switch &f| Toggles whether the priorities should be considered in ascending or descending order.");
		argumentInput("ash_dc_transfer", "transfer", "ash.cmd.distributionchest.transfer",
				"/ash distributionchest transfer <playername>", "/ash distributionchest transfer ", 
				"&c/ash distributionchest transfer <Spielername> &f| Überträgt den Eigentümerstatus auf den angegebenen Spieler.",
				"");
		argumentInput("ash_endstorage", "endstorage", "ash.cmd.endstorage",
				"/ash endstorage", "/ash endstorage ", 
				"&c/ash endstorage &f| Toggelt die Lagerkiste zu einer Endlagerkiste.", 
				"&c/ash endstorage &f| Toggles the storage crate to a final storage crate");
		argumentInput("ash_gui", "gui", "ash.cmd.gui",
				"/ash gui", "/ash gui ", 
				"&c/ash gui &f| Toggelt zwischen den Baumodus und dem Gui-Öffnungsmodus.", 
				"&c/ash gui &f| Toggles between build mode and gui open mode.");
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
				"&c/ash itemfilterset select <Name/ID> &f| Wählt einen ItemFilterSet für den Zwischenspeicher aus.", 
				"&c/ash itemfilterset select <Name/ID> &f| Selects an ItemFilterSet for the cache.");
		argumentInput("ash_itemfilterset_update", "update", "ash.cmd.itemfilterset.update",
				"/ash itemfilterset update [Player]", "/ash itemfilterset update ", 
				"&c/ash itemfilterset update &f| Öffnet einen ItemFilterSet zum Verändern.", 
				"&c/ash itemfilterset update &f| Opens an ItemFilterSet for modification.");
		argumentInput("ash_mode", "mode", "ash.cmd.mode",
				"/ash mode <Modus>", "/ash mode ", 
				"&c/ash mode <Modus> &f| Versetzt dich in einen spezifischen Modus.", 
				"&c/ash mode <Mode> &f| Puts you in a specific mode.");
		argumentInput("ash_override", "override", "ash.cmd.override",
				"/ash override", "/ash override ", 
				"&c/ash override &f| Toggelt den Änderungsmodus.", 
				"&c/ash override &f| Toggles the change mode.");
		argumentInput("ash_playerinfo", "playerinfo", "ash.cmd.playerinfo",
				"/ash playerinfo [Player]", "/ash playerinfo ", 
				"&c/ash playerinfo [Spielername] &f| Zeigt alle Infos von dir oder dem angegebenen Spieler an.", 
				"&c/ash playerinfo [player name] &f| Displays all info about you or the specified player.");
		argumentInput("ash_priority", "priority", "ash.cmd.priority",
				"/ash priority <Number>", "/ash priority ", 
				"&c/ash priority <Zahl> &f| Setzt für den Zwischenspeicher eine Priorität.",
				"&c/ash priority <number> &f| Sets a priority for the cache.");
		argumentInput("ash_sc", "storagechest", "ash.cmd.storagechest",
				"/ash storagechest", "/ash storagechest ", 
				"&c/ash storagechest &f| Zwischenbefehl", 
				"&c/ash storagechest &f| Cache command");
		argumentInput("ash_sc_create", "create", "ash.cmd.storagechest.create",
				"/ash storagechest create", "/ash storagechest create ", 
				"&c/ash storagechest create &f| Setzt den Spieler in den Erstellungsmodus für Lagerkisten.", 
				"&c/ash storagechest create &f| Put the player in storagechest create mode.");
		argumentInput("ash_sc_delete", "delete", "ash.cmd.storagechest.delete",
				"/ash storagechest delete", "/ash storagechest delete ", 
				"&c/ash storagechest delete &f| Löscht Lagerkiste.", 
				"&c/ash storagechest delete &f| Delete storage crate.");
		argumentInput("ash_sc_info", "info", "ash.cmd.storagechest.info",
				"/ash storagechest info", "/ash storagechest info ", 
				"&c/ash storagechest info &f| Zeigt alle Informationen zu der ausgewählten Lagerkiste an.",
				"&c/ash storagechest info &f| Shows all information about the selected storage crate.");
		argumentInput("ash_sc_list", "list", "ash.cmd.storagechest.list",
				"/ash storagechest list", "/ash storagechest list ", 
				"&c/ash storagechest list &f| Listet alle LagerkistenIDs des Spielers auf.", 
				"&c/ash storagechest list &f| Lists all the players storagechest IDs.");
		argumentInput("ash_sc_openitemfilter", "openitemfilter", "ash.cmd.storagechest.openitemfilter",
				"/ash storagechest openitemfilter", "/ash storagechest openitemfilter ", 
				"&c/ash storagechest openitemfilter <ID> &f| Öffnet das ItemfilterSet der Lagerkiste.", 
				"&c/ash storagechest openitemfilter <ID> &f| Opens the item filter set of the storage crate.");
		argumentInput("ash_sc_position", "position", "ash.cmd.storagechest.position",
				"/ash storagechest position", "/ash storagechest position ", 
				"&c/ash storagechest position &f| Setzt den Spieler in den Positionswechselmodus für Lagerkisten.", 
				"&c/ash storagechest position &f| Puts the player in storagechest position change mode.");
		argumentInput("ash_sc_select", "select", "ash.cmd.storagechest.select",
				"/ash storagechest select <id>", "/ash storagechest select ", 
				"&c/ash storagechest select <ID> &f| Wählt eine Lagerkiste aus.", 
				"&c/ash storagechest select <ID> &f| Selects a storage crate.");
		argumentInput("ash_sc_update", "update", "ash.cmd.storagechest.update",
				"/ash storagechest update", "/ash storagechest update ", 
				"&c/ash storagechest search &f| Sucht die ausgewählte Lagerkiste.", 
				"&c/ash storagechest search &f| Searches for the selected storage crate.");
		argumentInput("ash_sc_search", "search", "ash.cmd.storagechest.search",
				"/ash storagechest search", "/ash storagechest search ", 
				"&c/ash storagechest update &f| Toggelt den Updatemodus für die Lagerkisten.", 
				"&c/ash storagechest update &f| Toggles the update mode for the storage crates");
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
		path = "Custom.";
		commandsKeys.put(path+"DistributionChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.distributionchest."}));
		commandsKeys.put(path+"ItemFilterSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.itemfilterset."}));
		commandsKeys.put(path+"StorageChest"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ash.count.storagechest."}));
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
						"&cDeine Eingabe ist fehlerhaft, klicke hier auf den Text um weitere Infos zu bekommen!",
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
						"&cAchtung!~!~&eBevor du den diesen Befehl ausführen kannst, klicke zuerst auf den davor stehenden Namen oder die ID.",
						"&cAttention!~!~&eBefore you can execute this command, first click on the name or ID in front of it"}));
		languageKeys.put("CancelAction", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAktion Abgebrochen! &eFalls dies unerwünscht ist, die vermeintliche Aktion erneut per Befehl setzten oder auslösen.",
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
						"&cDieser Block ist schon als Verteilerkiste registeriert!",
						"&cThis block is already registered as distribution box!"}));
		languageKeys.put("DistributionIsRunning", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &7Der Verteilungsprozess ist schon aktiv für diesen Block, deshalb kann er nicht geöffnet werden. &eStartzeit: &f%start% &7| &eZu Ende: &f%time%",
						""}));
		languageKeys.put("SafetyBreak",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! Du bist dabei eine Verteilerkiste abzubauen! Falls das gewollt ist, stelle per Befehl zuerst dies ein!",
						""}));
		languageKeys.put("NoNumber", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%args% &cist keine Nummer!",
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
						"StorageChest GUI ID: &c%id% | %dcid% %name%",
						"StorageChest GUI ID: &c%id% | %dcid% %name%"}));
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
		languageKeys.put("CmdAsh.BlockInfo.Deactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBlockinformation werden nun nicht mehr angezeigt.",
						"&eBlock information is no longer displayed."}));
		languageKeys.put("CmdAsh.BlockInfo.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBlockinformationen werden nun angezeigt.",
						"&eBlock information is now displayed."}));
		languageKeys.put("CmdAsh.BlockInfo.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&7[&6Lagersystem &aBlockInfo&7]&e===",
						"&e==&7[&6Storage system &aBlockInfo&7]&e==="}));
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
						"&eZum+Abbrechen+des+Vorgangs+klicke+hier+oder+gib+&f/ash+cancel+&eein!~click@RUN_COMMAND@/ash+cancel",
						"&eTo+Cancel+click+here+or+give+&f/ash+cancel+&on!~click@RUN_COMMAND@/ash+cancel"}));
		languageKeys.put("CmdAsh.Cancel.IsCancel",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVorgang abgebrochen!",
						"&cProcess aborted!"}));
		languageKeys.put("CmdAsh.CanBreakDChest.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu kannst nun Verteilerkisten abbauen. &cSei also vorsichtig!",
						"&eDu kannst nun Verteilerkisten abbauen. &cSei also vorsichtig!"}));
		languageKeys.put("CmdAsh.CanBreakDChest.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu kannst nun keine Verteilerkisten mehr abbauen!",
						""}));
		languageKeys.put("CmdAsh.Create.Init", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu bist dabei eine Verteilerkiste zu erstellen! Bitte klicke mit &cShift + Rechts &enun die neue Verteilerkiste an!",
						"&eYou are creating a distribution box! Please click with &cShift+Right &nun on the new distribution box!"}));
		languageKeys.put("CmdAsh.Create.NameExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Name existiert schon!",
						"&cThe name already exists!"}));
		languageKeys.put("CmdAsh.Create.TooMany",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast schon zu viele Verteilerkisten erstellt! Lösche vorher eine, um eine weitere zu erstellen!",
						"&cYou have already created too many distribution boxes! Delete one before to create another one!"}));
		languageKeys.put("CmdAsh.Create.TooManyS",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast schon zu viele Lagerkisten erstellt! Lösche vorher eine, um eine weitere zu erstellen!",
						"&cYou have already created too many storage boxes! Delete one first to create another one!"}));
		languageKeys.put("CmdAsh.Create.SetupDChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aVerteilerkiste+&f%dc%+&aerstellt!+&eFür+weitere+Informationen+klicke+hier~click@RUN_COMMAND@/ash+distributionchest+info+%dc%",
						"&aDistributionbox+&f%dc%+&created!+&eFor+more+information+click+here~click@RUN_COMMAND@/ash+distributionchest+info+%dc%"}));
		languageKeys.put("CmdAsh.Create.FutherInstruction", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eSofern keine speziellen Einstellungen vorgenommen werden sollen, so ist das Erstellen von Lagerkisten für die Verteilerkiste &f%dc% &emöglich.",
						"&eAs long as no special settings are to be made, the creation of storage boxes for the distribution box &f%dc% &eis possible"}));
		languageKeys.put("CmdAsh.Create.StorageChestExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDiese Kiste wurde von dir schon als Lagerkiste der Verteilerkiste &f%dc% &cregisteriert! &eFalls diese Kiste trotzdem als Lagerkiste registriert werden soll, so wähle eine andere Verteilerkiste aus!",
						"&cThis box has already been &cregistered by you as storage box of the distribution box &f%dc%! &eIf you still want to register this crate as storage crate, choose another distribution crate!"}));
		languageKeys.put("CmdAsh.Create.DistributionChestDontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste, welche du ausgewählt oder erstellt hast, existiert nicht mehr!",
						"&cThe distribution box you selected or created no longer exists!"}));
		languageKeys.put("CmdAsh.Create.DistributionChestDontExistNone",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste, zu dem diese Lagerkiste gehört, existiert nicht mehr!",
						"&cThe distribution box this storage box belongs to no longer exists!"}));
		languageKeys.put("CmdAsh.Create.SetupSChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aLagerkiste+&f%sc%+&aerstellt!+&eFür+weitere+Informationen+klicke+hier~click@RUN_COMMAND@/ash+storagechest+info",
						"&aStoragebox+&f%sc%+&created!+&eFor+more+information+click+here~click@RUN_COMMAND@/ash+storagechest+info"}));
		languageKeys.put("CmdAsh.Create.UpdateStorageChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste+Update!+Klicke+hier+für+weitere+Informationen~click@RUN_COMMAND@/ash+storagechest+info",
						"&eStorageBox+Update!+click+here+for+more+information~click@RUN_COMMAND@/ash+storagechest+info"}));
		languageKeys.put("CmdAsh.Create.SChestDontBeSameAsDChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste kann nicht gleichzeitig die Verteilerkiste, des gleichen Lagersystems sein!",
						""}));
		languageKeys.put("CmdAsh.Delete.IsDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteiler- und Lagerkisten welche sich auf dem Server &f%server% &eund in der Welt &f%world% &esich befanden, wurden gelöscht!",
						""}));
		languageKeys.put("CmdAsh.Delete.DistributionChestDontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste &f%id% &cexistiert nicht!",
						"&cThe distribution box &f%id% &does not exist!"}));
		languageKeys.put("CmdAsh.Delete.DChestDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%id% &e| &f%name% &eist &cgelöscht!",
						"&eDistributionChest &f%id% &e| &f%name% &most &cdeleted!"}));
		languageKeys.put("CmdAsh.Delete.LinkedSChestDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkisten der Verteilerkiste &f%id% &e| &f%name% &ewurden &cgelöscht&e! Anzahl: &f%count%",
						"&eDistribution box &f%id% &e| &f%name% &have been &deleted&e! quantity: &f%count%"}));
		languageKeys.put("CmdAsh.Delete.StorageChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste &f%id% &cexistiert nicht!",
						"&cThe StorageChest &f%id% &cannot be used!"}));
		languageKeys.put("CmdAsh.Delete.SChestDeleted", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%id% &eist &cgelöscht!",
						"&eStorage crate &f%id% &is &clear!"}));
		languageKeys.put("CmdAsh.DistributionChest.OtherCmd",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("CmdAsh.DistributionChestAutomaticDistribution.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%name% &ewird nun nicht mehr automatisch geleert!",
						"&eDistributionChest &f%name% &is now no longer automatically emptied!"}));
		languageKeys.put("CmdAsh.DistributionChestAutomaticDistribution.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%name% &ewird nun automatisch geleert!",
						"&eDistribution tray &f%name% &is now emptied automatically!"}));
		languageKeys.put("CmdAsh.DistributionChestList.Empty", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Verteilerkisten!",
						"&cDont have any distribution boxes!"}));
		languageKeys.put("CmdAsh.DistributionChestList.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Verteilerkiste Liste von &f%player%&7]&e=====",
						"&e=====&7[&6DistributionChestList of &f%player%&7]&e======"}));
		languageKeys.put("CmdAsh.DistributionChestList.CommandRun", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash distributionchest select %name%",
						"/ash distributionchest select %name%"}));
		languageKeys.put("CmdAsh.DistributionChestList.CommandRunInfo",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash distributionchest info",
						"/ash distributionchest info"}));
		languageKeys.put("CmdAsh.DistributionChestList.CommandRunDelete", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash distributionchest delete",
						"/ash distributionchest delete"}));
		languageKeys.put("CmdAsh.DistributionChestList.CommandString", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash distributionchest list",
						"/ash distributionchest list"}));
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
						"&eSpieler &f%player% &ewurde von der Verteilerkiste &f%dc% &eals Mitglied hinzugefügt!",
						"&eSpieler &f%player% &has been added from the distribution box &f%dc% &eals member!"}));
		languageKeys.put("CmdAsh.DistributionChestSwitch.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBei Verteilerkiste &f%name% &ewurden die Prioritäten nun zu &aaufsteigend &e(Kleineste Wert zuerst) gewechselt.",
						"&eFor distributionchest &f%name% &the priorities have been switched to &aascending &e(lowest value first)"}));
		languageKeys.put("CmdAsh.DistributionChestSwitch.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBei Verteilerkiste &f%name% &ewurden die Prioritäten nun zu &babsteigend &e(Größter Wert zuerst) gewechselt.",
						"&eFor distribution box &f%name% &the priorities have now been changed to &descending &e(Highest value first)"}));
		languageKeys.put("CmdAsh.EndStorage.Deactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEndlager ist nun im Zwischenspeicher nicht mehr aktiv!",
						"&eEndStorage is now inactive in the cache!"}));
		languageKeys.put("CmdAsh.EndStorage.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEndlager ist nun im Zwischenspeicher aktiv!",
						"&eEndstorage is now active in the cache!"}));
		languageKeys.put("CmdAsh.Gui.Deactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBaumodus ist deaktiviert. GUIs werden nun bei registierten Lagerkisten mit Shift + Rechtsklick angezeigt.",
						"&eConstruction mode is deactivated. GUIs are now displayed for registered storage boxes with Shift + right click"}));
		languageKeys.put("CmdAsh.Gui.Active", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBaumodus ist aktiviert. GUIs werden nun nicht mehr angezeigt.",
						"&eConstruction mode is activated. GUIs are now no longer displayed."}));
		languageKeys.put("CmdAsh.Info.HeadlineD", 
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
		languageKeys.put("CmdAsh.Info.SChestAmount", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAnzahl verlinkter Lagerkisten: &f",
						"&cNumber of linked storage boxes: &f"}));
		languageKeys.put("CmdAsh.Info.SChestAmountEnd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAnzahl verlinkter Endlagerkisten: &f",
						"&cNumber of linked repository boxes: &f"}));
		languageKeys.put("CmdAsh.Info.HeadlineS", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Lagerkiste &f%id%&7]&e=====",
						"&e=====&7[&6Storage box &f%id%&7]&e====="}));
		languageKeys.put("CmdAsh.Info.DChestName", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVerteilerkiste: &f%id% &e| &f%name%",
						"&cJunction box: &f%id% &e| &f%name%"}));
		languageKeys.put("CmdAsh.Info.AutoDistribution", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAutomatische Verteilung: &f%auto%",
						"&cAutomatic distribution: &f%auto%"}));
		languageKeys.put("CmdAsh.Info.NormalPriority", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cNutzt absteigende Priorität: &f%nprio%",
						"&cUses descending priority: &f%nprio%"}));
		languageKeys.put("CmdAsh.Info.Priority", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPriorität: &f%prio%",
						"&cPriority: &f%prio%"}));
		languageKeys.put("CmdAsh.Info.Endstorage",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cist Endlagerkiste: &f%end%",
						"&cist final storage box: &f%end%"}));
		languageKeys.put("CmdAsh.InventoyClick.ItemExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cItem existiert schon als ItemFilter!",
						"&cItem already exists as ItemFilter!"}));
		languageKeys.put("CmdAsh.InventoyClick.InventoryFull", 
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
						"&cDer ausgewählte ItemFilterSet existiert nicht!",
						"&cThe selected ItemFilterSet doesnt exist!"}));
		languageKeys.put("CmdAsh.ItemFilterSet.Update", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilterSet &f%name% &eaktualisiert.",
						"&eItemFilterSet &f%name% &updated."}));
		languageKeys.put("CmdAsh.ItemFilterSet.NewName", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eItemFilter &f%oldname% &ewurde in &f%newname% &eumbenannt!",
						"&eItemFilter &f%oldname% &has been &renamed to &f%newname% &elect!"}));
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
		languageKeys.put("CmdAsh.ItemFilterSetList.CommandRun",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash itemfilterset select %id% %uuid%",
						"/ash itemfilterset select %id% %uuid%"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.LineOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eAnzahl an Items im Filter: &f%amount%",
						"&eNumber of items in filter: &f%amount%"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.CommandString",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash itemfilterset list ",
						"/ash itemfilterset list "}));
		languageKeys.put("CmdAsh.ItemFilterSetList.CommandRunOpen",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash itemfilterset update",
						"/ash itemfilterset update"}));
		languageKeys.put("CmdAsh.ItemFilterSetList.CommandRunDelete",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash itemfilterset delete",
						"/ash itemfilterset delete"}));
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
						"&cAchtung! &eDir gehört das orginal ItemFilterSet nicht! Es wurde daher eine Kopie auf deinen Namen erstellt!",
						"&cAttention! &eYou do not own the original ItemFilterSet! Therefore a copy has been made in your name!"}));
		languageKeys.put("CmdAsh.Mode.SetMode", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eModus &f%mode% &egesetzt!",
						"&eMode &f%mode% &eset!"}));
		languageKeys.put("CmdAsh.OpenItemFilter.DontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cLagerkiste mit der ID &f%id% &cexistiert nicht!",
						"&cStorage box with the ID &f%id% &cdoes not &cexist!"}));
		languageKeys.put("CmdAsh.Override.Deactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eÄnderung ist nun im Zwischenspeicher nicht mehr aktiv!",
						"&eChangeMode is now no longer active in the cache!"}));
		languageKeys.put("CmdAsh.Override.Active",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eÄnderung ist nun im Zwischenspeicher aktiv!",
						"&eChangeMode is now active in the cache!"}));
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
		languageKeys.put("CmdAsh.PlayerInfo.Priority",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPriorität: &f",
						"&cPriority: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.ItemFilterID",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAusgewählte ItemFilterSetID: &f",
						"&cSelected ItemFilterSetID: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.SearchType", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cSuchtyp: &f",
						"&cSearch type: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.Override", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cÄnderungsmodus aktiv: &f",
						"&cChange mode active: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.EndStorage", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cEndlagerkistenmodus aktiv: &f",
						"&cEnd storage crate mode active: &f"}));
		languageKeys.put("CmdAsh.PlayerInfo.BLOCKINFO",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"BlockinfoModus (Zeigt alle Info zu einem geklicken Block[Kiste ect.] an)",
						"BlockinfoMode (Shows all info about a clicked block [box etc.])"}));
		languageKeys.put("CmdAsh.PlayerInfo.CHANGEITEMFILTERSET",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSet Änderungsmodus (Das ItemFilterSet wird gerade bearbeitet)",
						"ItemFilterSet change mode (The ItemFilterSet is currently being processed)"}));
		languageKeys.put("CmdAsh.PlayerInfo.CONSTRUCT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Normaler Modus (Du kannst frei bauen und bekommst keine ItemFilterSet bei shift + Rechtsklick angezeigt)",
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
		languageKeys.put("CmdAsh.PlayerInfo.NONE",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSet von Lagerkisten Aufrufenmodus (Du kannst per Shift + Rechtsklick ItemFilterSets von Lagerkisten öffnen)",
						"ItemFilterSet from stockboxes Call up mode (you can open ItemFilterSets from stockboxes by Shift + right click)"}));
		languageKeys.put("CmdAsh.PlayerInfo.POSITIONUPDATEDISTRIBUTION",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Positionswechselmodus (Du bist dabei eine Verteilerkiste umzupositionieren)",
						"Position change mode (you are repositioning a distribution box)"}));
		languageKeys.put("CmdAsh.PlayerInfo.POSITIONUPDATESTORAGE", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Positionswechselmodus (Du bist dabei eine Lagerkiste umzupositionieren)",
						"Position change mode (you are repositioning a storage crate)"}));
		languageKeys.put("CmdAsh.PlayerInfo.UPDATESTORAGE", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Lagerkistenaktualisierungsmodus (Du bist dabei eine Lagerkiste nach neuen Werten [Priorität etc.] zu aktualisieren)",
						"Storage box update mode (you are about to update a storage box according to new values [priority etc.])"}));
		languageKeys.put("CmdAsh.PlayerInfo.UPDATESTORAGEITEMFILTERSET",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"ItemFilterSet Aktualisierungsmodus (Du bist dabei, ItemFilterSet von Lagerkisten zu aktualisieren)",
						"ItemFilterSet update mode (you are about to update ItemFilterSet from storage boxes)"}));
		languageKeys.put("CmdAsh.Position.Deactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ePositionswechselmodus ist deaktiviert!",
						"&ePosition change mode is deactivated!"}));
		languageKeys.put("CmdAsh.Position.DChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ePositionswechselmodus für Verteilerkisten ist aktiviert!",
						"&ePosition change mode for distribution boxes is activated!"}));
		languageKeys.put("CmdAsh.Position.SChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ePositionswechselmodus für Lagerkisten ist aktiviert!",
						"&ePosition change mode for storage crates is activated!"}));
		languageKeys.put("CmdAsh.Position.NoSelectedChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast weder eine Verteilerkiste, noch eine Lagerkiste ausgewählt! &eKlicke+hier+um+eine+Kiste+auszuwählen~click@SUGGEST_COMMAND@/ash+distributionchest+select+<ID>",
						"&cYou have neither selected a distribution box nor a storage box! &eClick+here+to+select+a+box~click@SUGGEST_COMMAND@/ash+distributionchest+select+<ID>"}));
		languageKeys.put("CmdAsh.Position.DistributionChestNotExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie gewählte Verteilerkiste existiert nicht!",
						"&cThe selected distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Position.SameStorageChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte LagerkistenID stimmt mit der ausgewählt Lagerkisten-Position überein! Bitte wechsel den Ort oder die ausgewählte LagerkistenID!",
						"&cThe selected storage box ID matches the selected storage box position! Please change the location or the selected storage crate ID!"}));
		languageKeys.put("CmdAsh.Position.SameDistributionChest",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte VerteilerkistenID stimmt mit der ausgewählt Verteilerkisten-Position überein! Bitte wechsel den Ort oder die ausgewählte VerteilerkistenID!",
						"&cThe selected distribution box ID matches the selected distribution box position! Please change the location or the selected distribution crate ID!"}));
		languageKeys.put("CmdAsh.Position.IsUpdated", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cPosition der Kiste aktualisiert!",
						"&cPosition of the crate updated!"}));
		languageKeys.put("CmdAsh.Position.MayChange", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eBitte denkt daran die Aktion nun abzubrechen oder eine andere Verteiler-/Lagerkiste auszuwählen.",
						"&ePlease remember to cancel the action now or select another distribution/storage box"}));
		languageKeys.put("CmdAsh.Priority.Set",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ePriorität auf &f%priority% &egesetzt!",
						"&ePriority set to &f%priority% &set!"}));
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
		languageKeys.put("CmdAsh.Search.SelectDcDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Verteilerkiste existiert nicht!",
						"&cThe selected distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Search.SelectScDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie ausgewählte Lagerkiste existiert nicht!",
						"&cThe selected stock box does not exist!"}));
		languageKeys.put("CmdAsh.Search.Compass", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDie Kiste wurde als Ziel für deine Kompasse gesetzt! Position ist: &f%world% %x% %y% %z%",
						"&eThe box has been set as the target for your compass! Position is: &f%world% %x% %y% %z%"}));
		languageKeys.put("CmdAsh.StorageChest.OtherCmd", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("CmdAsh.StorageChest.CreateDeactive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eErstellungsmodus für Lagerkisten deaktiviert!",
						"&eCreation mode for storage boxes disabled!"}));
		languageKeys.put("CmdAsh.StorageChest.CreateActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eErstellungsmodus für Lagerkisten aktiviert! Verlinkte VerteilerkistenID: &f%id%",
						"&eCreation mode for storage boxes enabled! Linked distribution boxesID: &f%id%"}));
		languageKeys.put("CmdAsh.StorageChest.UpdateDeactive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eUpdatemodus für Lagerkisten deaktiviert!",
						"&eUpdate mode for storage boxes disabled!"}));
		languageKeys.put("CmdAsh.StorageChest.UpdateActive", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eUpdatemodus für Lagerkisten aktiviert! Beachte, dass eventuelle Zusatzteinstellungen wie &fÄnderungmodus, ItemFilterSet, Priorität und Endlager &eauch eingestellt werden sollten.",
						"&eUpdate mode for storage crates enabled! Please note that possible additional settings like &change mode, ItemFilterSet, priority and final storage should &also be set."}));
		languageKeys.put("CmdAsh.StorageChestList.Empty", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast keine Lagerkisten!",
						"&cYou dont have any storage boxes!"}));
		languageKeys.put("CmdAsh.StorageChestList.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6Lagerkiste Liste von &f%player%&7]&e=====",
						"&e=====&7[&6StorageChestList from &f%player%&7]&e====="}));
		languageKeys.put("CmdAsh.StorageChestList.CommandRun", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash storagechest select %id%",
						"/ash storagechest select %id%"}));
		languageKeys.put("CmdAsh.StorageChestList.CommandString",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash storagechest list",
						"/ash storagechest list"}));
		languageKeys.put("CmdAsh.StorageChestList.CommandRunInfo",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash storagechest info",
						"/ash storagechest info"}));
		languageKeys.put("CmdAsh.StorageChestList.CommandRunOpen", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash storagechest openitemfilter",
						"/ash storagechest openitemfilter"}));
		languageKeys.put("CmdAsh.StorageChestList.CommandRunDelete", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"/ash storagechest delete",
						"/ash storagechest delete"}));
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
						"&eÖffnet das ItemFilterSet der Lagerkiste.~!~",
						"&eOpens the ItemFilterSet of the storage crate.~!~"}));
		languageKeys.put("CmdAsh.Select.DChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Verteilerkiste existiert nicht!",
						"&cThe distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Select.SChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste existiert nicht!",
						"&cThe storage box doesnt exist!"}));
		languageKeys.put("CmdAsh.Select.SelectDChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eVerteilerkiste &f%iddc% &e| &f%name% &eausgewählt!",
						"&eDistribution box &f%iddc% &e| &f%name% &elect!"}));
		languageKeys.put("CmdAsh.Select.SelectSChest", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%idsc% &eder Verteilerkiste &f%iddc% &e| &f%name% &eausgewählt!",
						"&eStorage crate &f%idsc% &the distribution crate &f%iddc% &e| &f%name% &e| &f%name% &elect!"}));
		languageKeys.put("CmdAsh.Select.SelectSChestWithOutD", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eLagerkiste &f%id% &eausgewählt! Beachte, dass diese Kiste mit keiner Verteilerkiste verlinkt ist!",
						"&eStorage crate &f%id% &elect! Note that this box is not linked to any distribution box!"}));
		languageKeys.put("CmdAsh.Transfer.TransferDc", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu hast die Verteilerkiste &f%id% | %name% &ean &c%player% &eübertragen!",
						""}));
		languageKeys.put("CmdAsh.Transfer.TransferDcToYou", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &c%player% &ehat dir den Eigentümerstatus der Verteilerkiste &f%id% | %name% &eübertragen!",
						""}));
		languageKeys.put("CmdAsh.Update.StorageChestDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Lagerkiste mit der gewählten Verteilerkiste existiert nicht!",
						"&cThe storage box with the selected distribution box does not exist!"}));
		languageKeys.put("CmdAsh.Update.MayDelete", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie gewählte Verteilerkiste existiert nicht mehr. Falls+Lagerkiste+gelöscht+werden+soll+klicke+hier!~click@SUGGEST_COMMAND@/ash+delete+s+%sc% Oder+wähle+eine+andere+Verteilerkiste+aus+und+klicke+hier+für+den+Änderungs-Modus~click@RUN_COMMAND@/ash+override~hover@SHOW_TEXT@&eÄnderungsmodus+bedeutet,+dass+die+Lagerkiste+nicht+nur+normal+geupdatet+wird,~!~&esonder+auch,+dass+die+Verteilerkiste+überschrieben+wird!~!~&cSofern+es+nicht+mehr+gebraucht+wird,+unbedingt+deaktivieren!",
						"&cThe selected distribution box no longer exists. If+storage box+delete+should+click+here! ~click@SUGGEST_COMMAND@/ash+delete+s+%sc% Or+select+an+other+distribution+box+off+and+click+here+for+the+change-mode~click@RUN_COMMAND@/ash+override~hover@SHOW_TEXT@&change-mode+means,+that+the+storage+box+will+not+only+normally+be+updated,~!~&special+also,+that+the+distribution+box+is+overridden+overridden!~!~&cSofar+it+is+no+longer+needed+to+deactivate!"}));
		languageKeys.put("CmdAsh.Update.IsUpdated", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aLagerkiste ist aktualisiert! Aktualisierungsanzahl: %i% | Klicke+hier+für+weitere+Informationen~click@RUN_COMMAND@/ash+info+s+%sc%",
						"&aStorage box is updated! Update count: %i% | click+here+for+more+information~click@RUN_COMMAND@/ash+info+s+%sc%"}));
		languageKeys.put("CmdAsh.Update.MayDeleteNone", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cLagerkiste+löschen?+Klicke+hier!~click@SUGGEST_COMMAND@/ash+delete+s+%sc% &cOder+wähle+eine+andere+Verteilerkiste+aus~click@RUN_COMMAND@/ash+distributionchest+select+<ID> und+klicke+hier+für+den+ÄnderungsModus~click@RUN_COMMAND@/ash+override~hover@SHOW_TEXT@&eÄnderungmodus+bedeutet,+dass+die+Lagerkiste+nicht+nur+normal+geupdatet+wird,~!~&esonder+auch,+dass+die+Verteilerkiste+überschrieben+wird!~!~&cSofern+es+nicht+mehr+gebraucht+wird,+unbedingt+deaktivieren!",
						"&cStorage box+delete?+click+here! ~click@SUGGEST_COMMAND@/ash+delete+s+%sc% &cOder+select+an+other+distributionbox+click@RUN_COMMAND@/ash+distributionchest+select+<ID> and+click+here+for+the+changemode~click@RUN_COMMAND@/ash+override~hover@SHOW_TEXT@&changemode+means+that+the+storage+box+not+just+normally+updated+ ~&special+also,+that+the+distribution+box+is+overridden+overridden!~!~&cSofar+it+is+no+longer+needed+to+deactivate!"}));
		languageKeys.put("CmdAsh.Update.NoSelectedDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast den Änderungsmodus aktiv, jedoch keine Andere Verteilerkiste ausgewählt!",
						"&cYou have activated the change mode, but have not selected another distribution box!"}));
		languageKeys.put("CmdAsh.Update.SameSelectedDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast den Änderungsmodus aktiv, jedoch hast du die gleiche Verteilerkiste ausgewählt, mit welcher die Lagerkiste schon verlinkt ist",
						"&cYou have activated the change mode, but you have selected the same distribution box to which the storage box is already linked"}));
		languageKeys.put("CmdAsh.Update.NotOwnerOrMemberSelectedDC", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast den Änderungsmodus aktiv, jedoch bist du weder der Eigentümer oder Mitglied der neuen Verteilerkiste, welche du mit der Lagerkiste verlinken willst.",
						"&cYou have change mode active, but you are neither the owner nor member of the new distribution box you want to link to the storage box."}));
		
		
		/*languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"",
				""}))*/
	}
}
