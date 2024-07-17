package main.java.me.avankziar.spigot.ash;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.assistance.BackgroundTask;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.AshCommandExecutor;
import main.java.me.avankziar.spigot.ash.cmd.CommandHelper;
import main.java.me.avankziar.spigot.ash.cmd.TabCompletion;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGAutomaticDistributionInfo;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGBlockInfo;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGCancel;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGCheckUnboundChest;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGConvert;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGCutAndPaste;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDebug;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDebug_ItemMeta;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDelete;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Breaking;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Chestname;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Create;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Delete;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Info;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_List;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Member;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_OpenOption;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_RemoteTriggerAnimation;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Search;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Select;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Switch;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGDistributionChest_Transfer;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_Create;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_Delete;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_List;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_Name;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_Select;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGItemFilterSet_Update;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGMode;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGPlayerInfo;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Chestname;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Create;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Delete;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Info;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_List;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_OpenOption;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Search;
import main.java.me.avankziar.spigot.ash.cmd.ash.ARGStorageChest_Select;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.cmd.tree.BaseConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.CommandConstructor;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlSetup;
import main.java.me.avankziar.spigot.ash.database.YamlHandler;
import main.java.me.avankziar.spigot.ash.database.YamlManager;
import main.java.me.avankziar.spigot.ash.eventhandler.InteractHandler;
import main.java.me.avankziar.spigot.ash.eventhandler.InventoryClickHandler;
import main.java.me.avankziar.spigot.ash.eventhandler.InventoryCloseHandler;
import main.java.me.avankziar.spigot.ash.listener.BlockBreakListener;
import main.java.me.avankziar.spigot.ash.listener.InventoryClickBlockerListener;
import main.java.me.avankziar.spigot.ash.listener.JoinQuitListener;
import main.java.me.avankziar.spigot.ash.listener.PlayerCommandPreprocessListener;
import main.java.me.avankziar.spigot.ash.listener.SignChangeListener;
import main.java.me.avankziar.spigot.ash.metrics.Metrics;
import me.avankziar.ifh.spigot.administration.Administration;
import me.avankziar.ifh.spigot.comparison.ItemStackComparison;

public class AdvancedStoreHouse extends JavaPlugin
{
	private static AdvancedStoreHouse plugin;
	public static Logger log;
	public static String pluginName = "AdvancedStoreHouse";
	private static YamlHandler yamlHandler;
	private static YamlManager yamlManager;
	private static MysqlSetup mysqlSetup;
	private static MysqlHandler mysqlHandler;
	private static BackgroundTask backgroundtask;
	private static CommandHelper commandHelper;
	private static Utility utility;
	
	private static Administration administrationConsumer;	
	private ItemStackComparison itemStackComparisonConsumer;
	
	public static ArrayList<String> editorplayers;
	private ArrayList<String> players;
	
	private ArrayList<CommandConstructor> commandTree;
	private ArrayList<BaseConstructor> helpList;
	private LinkedHashMap<String, ArgumentModule> argumentMap;
	public static String baseCommandI = "ash"; //Pfad angabe + ürspungliches Commandname
	
	public static String baseCommandIName = ""; //CustomCommand name
	
	public static String infoCommandPath = "CmdAsh";
	public static String infoCommand = "/"; //InfoComamnd
	
	public static CommandConstructor cc = null; 
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=ASH
		log.info("  █████╗ ███████╗██╗  ██╗ | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ██╔══██╗██╔════╝██║  ██║ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ███████║███████╗███████║ | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██╔══██║╚════██║██╔══██║ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ██║  ██║███████║██║  ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info(" ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		editorplayers = new ArrayList<>();
		commandTree = new ArrayList<>();
		argumentMap = new LinkedHashMap<>();
		helpList = new ArrayList<>();
		
		setupIFHAdministration();
		
		PluginUserHandler.setUserList(new ArrayList<>());
		ChestHandler.initEnchantments();
		yamlHandler = new YamlHandler(this);
		utility = new Utility(this);
		commandHelper = new CommandHelper(this);
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(this, adm, path);
		} else
		{
			log.severe("MySQL is not set in the Plugin "+pluginName+"!");
			Bukkit.getPluginManager().getPlugin("AdvancedStoreHouse").getPluginLoader().disablePlugin(this);
			return;
		}
		PluginSettings.init(plugin);
		//setupPlayers();
		backgroundtask = new BackgroundTask(this);
		setupStrings();
		try {setupCommandTree();} catch (IOException e)	{}
		ListenerSetup();
		setupIFHItemStackComparison();
		setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);		
		log.info(pluginName + " is disabled!");
	}
	
	public static AdvancedStoreHouse getPlugin()
	{
		return plugin;
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		AdvancedStoreHouse.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundtask;
	}
	
	public CommandHelper getCommandHelper()
	{
		return commandHelper;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	private void setupStrings()
	{
		//Hier baseCommands
		baseCommandIName = plugin.getYamlHandler().getCom().getString(baseCommandI+".Name");
		
		//Zuletzt infoCommand deklarieren
		infoCommand += baseCommandIName;
	}
	
	private void setupCommandTree() throws IOException
	{
		/*LinkedHashMap<Integer, ArrayList<String>> playerMap = new LinkedHashMap<>();
		
		ArrayList<String> playerarray = getPlayers();
		Collections.sort(playerarray);
		playerMap.put(1, playerarray);*/
		
		LinkedHashMap<Integer, ArrayList<String>> playerMapI = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIV = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapV = new LinkedHashMap<>();
		
		setupPlayers();
		ArrayList<String> playerarray = getPlayers();
		
		Collections.sort(playerarray);
		playerMapI.put(1, playerarray);
		playerMapII.put(2, playerarray);
		playerMapIII.put(3, playerarray);
		playerMapIV.put(4, playerarray);
		playerMapV.put(5, playerarray);
		
		LinkedHashMap<Integer, ArrayList<String>> lhmmode = new LinkedHashMap<>(); 
		List<PluginUser.Mode> modes = new ArrayList<PluginUser.Mode>(EnumSet.allOf(PluginUser.Mode.class));
		ArrayList<String> modeList = new ArrayList<String>();
		for(PluginUser.Mode m : modes) {modeList.add(m.toString());}
		lhmmode.put(1, modeList);
		
		ArgumentConstructor autodistributioninfo = new ArgumentConstructor(baseCommandI+"_automaticdistributioninfo", 0, 0, 0, false, null);
		
		ArgumentConstructor blockinfo = new ArgumentConstructor(baseCommandI+"_blockinfo", 0, 0, 0, false, null);
		ArgumentConstructor cancel = new ArgumentConstructor(baseCommandI+"_cancel", 0, 0, 0, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.CANCEL, cancel.getCommandString());
		
		ArgumentConstructor cap = new ArgumentConstructor(baseCommandI+"_cutandpaste", 0, 0, 0, false, null);
		
		ArgumentConstructor checkunboundchest = new ArgumentConstructor(baseCommandI+"_checkunboundchest", 0, 0, 1, false, null);
		ArgumentConstructor convert = new ArgumentConstructor(baseCommandI+"_convert", 0, 0, 1, false, null);
		
		ArgumentConstructor debug_im = new ArgumentConstructor(baseCommandI+"_debug_itemmeta", 1, 1, 1, false, null);
		ArgumentConstructor debug = new ArgumentConstructor(baseCommandI+"_debug", 0, 0, 0, false, null, debug_im);
		
		ArgumentConstructor delete = new ArgumentConstructor(baseCommandI+"_delete", 0, 2, 2, false, null);
		
		ArgumentConstructor dc_breaking = new ArgumentConstructor(baseCommandI+"_dc_breaking", 1, 1, 1, false, null);
		ArgumentConstructor dc_chestname = new ArgumentConstructor(baseCommandI+"_dc_chestname", 1, 2, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_CHESTNAME, dc_chestname.getCommandString());
		ArgumentConstructor dc_create = new ArgumentConstructor(baseCommandI+"_dc_create", 1, 2, 2, false, null);
		ArgumentConstructor dc_delete = new ArgumentConstructor(baseCommandI+"_dc_delete", 1, 1, 1, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_DELETE, dc_delete.getCommandString());
		ArgumentConstructor dc_info = new ArgumentConstructor(baseCommandI+"_dc_info", 1, 1, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_INFO, dc_info.getCommandString());
		ArgumentConstructor dc_list = new ArgumentConstructor(baseCommandI+"_dc_list", 1, 1, 3, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_LIST, dc_list.getCommandString());
		ArgumentConstructor dc_member = new ArgumentConstructor(baseCommandI+"_dc_member", 1, 2, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_MEMBER, dc_member.getCommandString());
		ArgumentConstructor dc_openoption = new ArgumentConstructor(baseCommandI+"_dc_openoption", 1, 1, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_OPENOPTION, dc_openoption.getCommandString());
		ArgumentConstructor dc_remotetriggeranimation = new ArgumentConstructor(baseCommandI+"_dc_remotetriggeranimation", 1, 1, 2, false, null);
		ArgumentConstructor dc_select = new ArgumentConstructor(baseCommandI+"_dc_select", 1, 2, 3, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.DC_SELECT, dc_select.getCommandString());
		ArgumentConstructor dc_search = new ArgumentConstructor(baseCommandI+"_dc_search", 1, 1, 2, false, null);
		ArgumentConstructor dc_switch = new ArgumentConstructor(baseCommandI+"_dc_switch", 1, 1, 1, false, null);
		ArgumentConstructor dc_transfer = new ArgumentConstructor(baseCommandI+"_dc_transfer", 1, 2, 2, false, playerMapII);
		ArgumentConstructor dc = new ArgumentConstructor(baseCommandI+"_dc", 0, 0, 0, false, null,
				dc_breaking, dc_chestname, dc_create, dc_delete, dc_info, dc_list, dc_member, 
				dc_openoption, dc_remotetriggeranimation, dc_select, dc_search, dc_switch, dc_transfer);
		
		ArgumentConstructor ifs_create = new ArgumentConstructor(baseCommandI+"_itemfilterset_create", 1, 2, 2, false, null);
		ArgumentConstructor ifs_delete = new ArgumentConstructor(baseCommandI+"_itemfilterset_delete", 1, 1, 1, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.IFS_DELETE, ifs_delete.getCommandString());
		ArgumentConstructor ifs_list = new ArgumentConstructor(baseCommandI+"_itemfilterset_list", 1, 1, 3, false, playerMapIII);
		PluginSettings.settings.getCommands().put(KeyHandler.IFS_LIST, ifs_list.getCommandString());
		ArgumentConstructor ifs_name = new ArgumentConstructor(baseCommandI+"_itemfilterset_name", 1, 2, 2, false, null);
		ArgumentConstructor ifs_select = new ArgumentConstructor(baseCommandI+"_itemfilterset_select", 1, 2, 3, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.IFS_SELECT, ifs_select.getCommandString());
		ArgumentConstructor ifs_update = new ArgumentConstructor(baseCommandI+"_itemfilterset_update", 1, 1, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.IFS_UPDATE, ifs_update.getCommandString());
		ArgumentConstructor itemfilterset = new ArgumentConstructor(baseCommandI+"_itemfilterset", 0, 0, 0, false, null,
				ifs_create, ifs_delete, ifs_list, ifs_name, ifs_select, ifs_update);
		
		ArgumentConstructor mode = new ArgumentConstructor(baseCommandI+"_mode", 0, 1, 1, false, lhmmode);
		ArgumentConstructor playerinfo = new ArgumentConstructor(baseCommandI+"_playerinfo", 0, 0, 1, false, playerMapI);
	
		ArgumentConstructor sc_create = new ArgumentConstructor(baseCommandI+"_sc_create", 1, 1, 1, false, null);
		ArgumentConstructor sc_chestname = new ArgumentConstructor(baseCommandI+"_sc_chestname", 1, 2, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_CHESTNAME, sc_chestname.getCommandString());
		ArgumentConstructor sc_delete = new ArgumentConstructor(baseCommandI+"_sc_delete", 1, 1, 1, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_DELETE, sc_delete.getCommandString());
		ArgumentConstructor sc_info = new ArgumentConstructor(baseCommandI+"_sc_info", 1, 1, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_INFO, sc_info.getCommandString());
		ArgumentConstructor sc_list = new ArgumentConstructor(baseCommandI+"_sc_list", 1, 1, 3, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_LIST, sc_list.getCommandString());
		ArgumentConstructor sc_openoption = new ArgumentConstructor(baseCommandI+"_sc_openoption", 1, 1, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_OPENOPTION, sc_openoption.getCommandString());
		ArgumentConstructor sc_select = new ArgumentConstructor(baseCommandI+"_sc_select", 1, 2, 2, false, null);
		PluginSettings.settings.getCommands().put(KeyHandler.SC_SELECT, sc_select.getCommandString());
		ArgumentConstructor sc_search = new ArgumentConstructor(baseCommandI+"_sc_search", 1, 1, 2, false, null);
		ArgumentConstructor sc = new ArgumentConstructor(baseCommandI+"_sc", 0, 0, 0, false, null,
				sc_create, sc_chestname, sc_delete, sc_info, sc_list, sc_openoption, sc_select, sc_search);
		
		CommandConstructor ash = new CommandConstructor(baseCommandI, false,
				autodistributioninfo, blockinfo, cancel, cap, checkunboundchest, convert, debug, delete, dc, itemfilterset, mode, playerinfo, sc);
		
		cc = ash;
		
		registerCommand(ash.getPath(), ash.getName());
		getCommand(ash.getName()).setExecutor(new AshCommandExecutor(plugin, ash));
		getCommand(ash.getName()).setTabCompleter(new TabCompletion(plugin));
		
		addingHelps(ash,
				autodistributioninfo, blockinfo, cancel, cap,
				checkunboundchest, convert,
				debug, debug_im,
				delete,
				dc, dc_breaking, dc_chestname, dc_create, dc_delete, dc_info, dc_list, dc_member,
				dc_openoption, dc_remotetriggeranimation, dc_select, dc_search, dc_switch, dc_transfer,
				itemfilterset, ifs_create, ifs_delete, ifs_list, ifs_name, ifs_select, ifs_update,
				mode, playerinfo,
				sc, sc_chestname, sc_create, sc_delete, sc_info, sc_list, sc_openoption, sc_select, sc_search);
		
		new ARGAutomaticDistributionInfo(plugin, autodistributioninfo);
		new ARGBlockInfo(plugin, blockinfo);
		new ARGCancel(plugin, cancel);
		
		new ARGCheckUnboundChest(plugin, checkunboundchest);
		new ARGConvert(plugin, convert);
		
		new ARGCutAndPaste(plugin, cap);
		
		new ARGDebug(plugin, debug);
		new ARGDebug_ItemMeta(plugin, debug_im);
		
		new ARGDelete(plugin, delete); 
		
		new ARGDistributionChest(plugin, dc);
		new ARGDistributionChest_Breaking(plugin, dc_breaking);
		new ARGDistributionChest_Chestname(plugin, dc_chestname);
		new ARGDistributionChest_Create(plugin, dc_create);
		new ARGDistributionChest_Delete(plugin, dc_delete);
		new ARGDistributionChest_Info(plugin, dc_info);
		new ARGDistributionChest_List(plugin, dc_list);
		new ARGDistributionChest_Member(plugin, dc_member);
		new ARGDistributionChest_OpenOption(plugin, dc_openoption);
		new ARGDistributionChest_RemoteTriggerAnimation(plugin, dc_remotetriggeranimation);
		new ARGDistributionChest_Select(plugin, dc_select);
		new ARGDistributionChest_Search(plugin, dc_search);
		new ARGDistributionChest_Switch(plugin, dc_switch);
		new ARGDistributionChest_Transfer(plugin, dc_transfer);
		
		new ARGItemFilterSet(plugin, itemfilterset);
		new ARGItemFilterSet_Create(plugin, ifs_create);
		new ARGItemFilterSet_Delete(plugin, ifs_delete);
		new ARGItemFilterSet_List(plugin, ifs_list);
		new ARGItemFilterSet_Name(plugin, ifs_name);
		new ARGItemFilterSet_Select(plugin, ifs_select);
		new ARGItemFilterSet_Update(plugin, ifs_update);
		
		new ARGMode(plugin, mode);
		new ARGPlayerInfo(plugin, playerinfo);
		
		new ARGStorageChest(plugin, sc);
		new ARGStorageChest_Chestname(plugin, sc_chestname);
		new ARGStorageChest_Create(plugin, sc_create);
		new ARGStorageChest_Delete(plugin, sc_delete);
		new ARGStorageChest_Info(plugin, sc_info);
		new ARGStorageChest_List(plugin, sc_list);
		new ARGStorageChest_OpenOption(plugin, sc_openoption);
		new ARGStorageChest_Select(plugin, sc_select);
		new ARGStorageChest_Search(plugin, sc_search);
	}
	
	public void ListenerSetup()
	{
		PluginManager pm = getServer().getPluginManager();
		/*getServer().getMessenger().registerIncomingPluginChannel(this, "AdvancedStoreHouse:sccbungee", new ServerListener(this));
		getServer().getMessenger().registerOutgoingPluginChannel(this, "AdvancedStoreHouse:sccbungee");*/
		pm.registerEvents(new JoinQuitListener(plugin), plugin);
		pm.registerEvents(new BlockBreakListener(plugin), plugin);
		pm.registerEvents(new PlayerCommandPreprocessListener(cc), plugin);
		pm.registerEvents(new SignChangeListener(), plugin);
		pm.registerEvents(new InteractHandler(plugin), plugin);
		pm.registerEvents(new InventoryClickHandler(plugin), plugin);
		pm.registerEvents(new InventoryCloseHandler(plugin), plugin);
		pm.registerEvents(new InventoryClickBlockerListener(), plugin);
	}
	
	public ArrayList<BaseConstructor> getHelpList()
	{
		return helpList;
	}
	
	public void addingHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}

	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public ArrayList<String> getPlayers()
	{
		return players;
	}

	public void setPlayers(ArrayList<String> players)
	{
		this.players = players;
	}
	
	public void setupPlayers() throws IOException
	{
		ArrayList<PluginUser> cu = ConvertHandler.convertListI(
				plugin.getMysqlHandler().getTop(MysqlHandler.Type.PLUGINUSER,
						"`id`", true, 0,
						plugin.getMysqlHandler().lastID(MysqlHandler.Type.PLUGINUSER, "?", 1)));
		ArrayList<String> cus = new ArrayList<>();
		for(PluginUser chus : cu) 
		{
			cus.add(chus.getName());	
		}
		setPlayers(cus);
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, AdvancedStoreHouse plugin) 
	{
		PluginCommand command = null;
	 
		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		try
	    {
	    	RegisteredServiceProvider<me.avankziar.ifh.spigot.administration.Administration> rsp = 
                     getServer().getServicesManager().getRegistration(Administration.class);
		    if (rsp == null) 
		    {
		        return;
		    }
		    administrationConsumer = rsp.getProvider();
		    log.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	    } catch(NoClassDefFoundError e) 
	    {}
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	private void setupIFHItemStackComparison() 
	{
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
        new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
				try
				{
					if(i == 20)
				    {
						cancel();
				    	return;
				    }
				    RegisteredServiceProvider<me.avankziar.ifh.spigot.comparison.ItemStackComparison> rsp = 
		                             getServer().getServicesManager().getRegistration(
		                            		 me.avankziar.ifh.spigot.comparison.ItemStackComparison.class);
				    if(rsp == null) 
				    {
				    	i++;
				        return;
				    }
				    itemStackComparisonConsumer = rsp.getProvider();
				    log.info(pluginName + " detected InterfaceHub >>> ItemStackComparison.class is consumed!");
				    cancel();
				} catch(NoClassDefFoundError e)
				{
					cancel();
				}			    
			}
        }.runTaskTimer(plugin, 0L, 20*2);
	}
	
	public ItemStackComparison getItemStackComparison()
	{
		return itemStackComparisonConsumer;
	}
	
	public boolean existHook(String externPluginName)
	{
		if(plugin.getServer().getPluginManager().getPlugin(externPluginName) == null
				|| !plugin.getServer().getPluginManager().getPlugin(externPluginName).isEnabled())
		{
			return false;
		}
		return true;
	}
	
	public void setupBstats()
	{
		int pluginId = 8264;
        new Metrics(this, pluginId);
	}
}