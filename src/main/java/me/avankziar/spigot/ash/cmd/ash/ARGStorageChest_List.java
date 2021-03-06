package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGStorageChest_List extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_List(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		String pageString = "";
		int page = 0;
		String otherplayer = player.getName();
		String otheruuid = player.getUniqueId().toString();
		if(args.length >= 3)
		{
			pageString = args[2];
			if(MatchApi.isInteger(pageString))
			{
				page = Integer.parseInt(pageString);
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("IllegalArgument")));
				return;
			}
		}
		if(args.length >= 4)
		{
			if(!otherplayer.equals(args[3]))
			{
				if(!player.hasPermission(Utility.PERMBYPASSLIST))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
					return;
				}
				otherplayer = args[3];
				UUID uuid = Utility.convertNameToUUID(otherplayer);
				if(uuid == null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
					return;
				}
				otheruuid = uuid.toString();
			}
		}
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash StorageChest list")));
			return;
		}
		int quantity = plugin.getYamlHandler().getConfig().getInt("AmountToDisplayStorageChestInListCommand",10);
		int start = page*quantity;		
		ArrayList<StorageChest> dcList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`id`",
						true, start, quantity, "`owner_uuid` = ?", otheruuid));
		if(dcList == null || dcList.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.Empty")));
			return;
		}
		int last = plugin.getMysqlHandler().lastID(Type.STORAGECHEST, "?", 1);
		int secondLast = dcList.get(dcList.size()-1).getId();
		boolean lastpage = false;
		if(secondLast >= last)
		{
			lastpage = true;
		}
		LinkedHashMap<String,ArrayList<StorageChest>> map = new LinkedHashMap<>();
		for(StorageChest sc : dcList)
		{
			int dcid = sc.getDistributionChestID();
			String name = "=)(?%!-_null_-!%?)(=";
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dcid))
			{
				name = ((DistributionChest) 
						plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dcid)).getChestName();
			}
			if(map.containsKey(name))
			{
				ArrayList<StorageChest> scarray = map.get(name);
				scarray.add(sc);
				map.replace(name, scarray);
			} else
			{
				ArrayList<StorageChest> scarray = new ArrayList<>();
				scarray.add(sc);
				map.put(name, scarray);
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.Headline")
				.replace("%player%", otherplayer)));
		for(String name : map.keySet())
		{
			ArrayList<StorageChest> scarray = map.get(name);
			List<BaseComponent> bclist = new ArrayList<>();
			if(!name.equals("=)(?%!-_null_-!%?)(="))
			{
				player.spigot().sendMessage(ChatApi.clickEvent("&c"+name+"&f:",
						ClickEvent.Action.RUN_COMMAND, 
						PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)+name));
				bclist.add(ChatApi.tc("  "));
				for(StorageChest sc : scarray)
				{
					TextComponent x = ChatApi.clickEvent("&6"+sc.getId()+"&f:",
							ClickEvent.Action.RUN_COMMAND, 
							PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getId());
					bclist.add(x);
					TextComponent y = ChatApi.apiChat("&eⓘ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.InfoHover")
							+plugin.getYamlHandler().getLang().getString("BeforeSelect"));
					bclist.add(y);
					TextComponent z = ChatApi.apiChat("&aⓄ",
							ClickEvent.Action.RUN_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION),
							HoverEvent.Action.SHOW_TEXT, 
							plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.OpenHover")
							+plugin.getYamlHandler().getLang().getString("BeforeSelect"));
					bclist.add(z);
					TextComponent alpha = ChatApi.apiChat("&c✖",
							ClickEvent.Action.SUGGEST_COMMAND,
							PluginSettings.settings.getCommands().get(KeyHandler.SC_DELETE),
							HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("BeforeSelect"));
					bclist.add(alpha);
					bclist.add(ChatApi.tctl(" &1| "));
				}
				if(scarray.isEmpty())
				{
					bclist.add(ChatApi.tctl("&4/"));
				}
				TextComponent tc = ChatApi.tc("");
				tc.setExtra(bclist);
				player.spigot().sendMessage(tc);
			}
		}
		
		ArrayList<StorageChest> scarray = map.get("=)(?%!-_null_-!%?)(=");
		List<BaseComponent> bclist = new ArrayList<>();
		player.spigot().sendMessage(ChatApi.clickEvent(
				plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.LostChests")+"&f:",
				ClickEvent.Action.RUN_COMMAND, 
				PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)));
		if(scarray != null)
		{
			for(StorageChest sc : scarray)
			{
				TextComponent x = ChatApi.clickEvent("  &6"+sc.getId()+"&f:",
						ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT+sc.getId()));
				bclist.add(x);
				TextComponent y = ChatApi.apiChat("&eⓘ",
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO),
						HoverEvent.Action.SHOW_TEXT, 
						plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.InfoHover")
						+plugin.getYamlHandler().getLang().getString("BeforeSelect"));
				bclist.add(y);
				TextComponent z = ChatApi.apiChat("&aⓄ",
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION),
						HoverEvent.Action.SHOW_TEXT, 
						plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChestList.OpenHover")
						+plugin.getYamlHandler().getLang().getString("BeforeSelect"));
				bclist.add(z);
				TextComponent alpha = ChatApi.apiChat("&e✖",
						ClickEvent.Action.SUGGEST_COMMAND,
						PluginSettings.settings.getCommands().get(KeyHandler.SC_DELETE),
						HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("BeforeSelect"));
				bclist.add(alpha);
				bclist.add(ChatApi.tctl(" &1| "));
			}
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(bclist);
			player.spigot().sendMessage(tc);
		}
		plugin.getCommandHelper().pastNextPage(player, "CmdAsh.BaseInfo", page, lastpage,
				PluginSettings.settings.getCommands().get(KeyHandler.SC_LIST), otherplayer);
		return;
	}
}