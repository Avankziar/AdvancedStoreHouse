package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGStorageChest_List extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	private void debug(String s)
	{
		boolean boo = true;
		if(boo)
		{
			System.out.println(s);
		}
	}
	
	public ARGStorageChest_List(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		debug("is Player != null:"+(player!=null));
		String pageString = "";
		int page = 0;
		String otherplayer = player.getName();
		debug("is playerName != null:"+(otherplayer!=null));
		String otheruuid = player.getUniqueId().toString();
		debug("is playerUUID != null:"+(otheruuid!=null));
		if(args.length >= 3)
		{
			pageString = args[2];
			debug("is pageString != null:"+(pageString!=null));
			if(MatchApi.isInteger(pageString))
			{
				page = Integer.parseInt(pageString);
				debug("is pageString:"+page);
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("IllegalArgument")));
				return;
			}
		}
		if(args.length >= 4)
		{
			if(!otherplayer.equals(args[3]))
			{
				if(!player.hasPermission(Utility.PERMBYPASSLIST))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NoPermission")));
					return;
				}
				otherplayer = args[3];
				UUID uuid = Utility.convertNameToUUID(otherplayer);
				if(uuid == null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotExist")));
					return;
				}
				otheruuid = uuid.toString();
			}
		}
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash StorageChest list")));
			return;
		}
		int quantity = plugin.getYamlHandler().get().getInt("AmountToDisplayStorageChestInListCommand",10);
		int start = page*quantity;		
		ArrayList<StorageChest> dcList = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.STORAGECHEST, "`id`",
						true, start, quantity, "`owner_uuid` = ?", otheruuid));
		if(dcList == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.Empty")));
			return;
		}
		if(dcList.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.Empty")));
			return;
		}
		int last = ((StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, "`owner_uuid` = ?",
				otheruuid)).getId();
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
				debug("dcId:"+dcid+" exist");
				name = ((DistributionChest) 
						plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dcid)).getChestName();
				debug("dcId:"+dcid+" Name:"+name+" exist");
			}
			if(map.containsKey(name))
			{
				debug("dcId:"+dcid+" map contains Name, replace");
				ArrayList<StorageChest> scarray = map.get(name);
				scarray.add(sc);
				map.replace(name, scarray);
			} else
			{
				debug("dcId:"+dcid+" map didnt contains Name, put");
				ArrayList<StorageChest> scarray = new ArrayList<>();
				scarray.add(sc);
				map.put(name, scarray);
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.Headline")
				.replace("%player%", otherplayer)));
		for(String name : map.keySet())
		{
			ArrayList<StorageChest> scarray = map.get(name);
			List<BaseComponent> bclist = new ArrayList<>();
			if(!name.equals("=)(?%!-_null_-!%?)(="))
			{
				debug("Output OuterLoop: "+name);
				player.spigot().sendMessage(ChatApi.clickEvent("&c"+name+"&f:",
						ClickEvent.Action.RUN_COMMAND, 
						plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRun")
						.replace("%name%", name)));
				bclist.add(ChatApi.tc("  "));
				for(StorageChest sc : scarray)
				{
					debug("Output InnerLoop: "+name+" scid: "+sc.getId());
					TextComponent x = ChatApi.clickEvent("&6"+sc.getId()+"&f:",
							ClickEvent.Action.RUN_COMMAND, 
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRun")
							.replace("%id%", String.valueOf(sc.getId())));
					bclist.add(x);
					TextComponent y = ChatApi.apiChat("&eⓘ",
							ClickEvent.Action.RUN_COMMAND,
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunInfo"),
							HoverEvent.Action.SHOW_TEXT,
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.InfoHover")
							+plugin.getYamlHandler().getL().getString("BeforeSelect"));
					bclist.add(y);
					TextComponent z = ChatApi.apiChat("&aⓄ",
							ClickEvent.Action.RUN_COMMAND,
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunOpen"),
							HoverEvent.Action.SHOW_TEXT, 
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.OpenHover")
							+plugin.getYamlHandler().getL().getString("BeforeSelect"));
					bclist.add(z);
					TextComponent alpha = ChatApi.apiChat("&c✖",
							ClickEvent.Action.SUGGEST_COMMAND,
							plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunDelete"),
							HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getL().getString("BeforeSelect"));
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
				plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.LostChests")+"&f:",
				ClickEvent.Action.RUN_COMMAND, 
				plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRun")));
		if(scarray != null)
		{
			for(StorageChest sc : scarray)
			{
				debug("Output Loop: LostChests");
				TextComponent x = ChatApi.clickEvent("  &6"+sc.getId()+"&f:",
						ClickEvent.Action.RUN_COMMAND, plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRun")
						.replace("%id%", String.valueOf(sc.getId())));
				bclist.add(x);
				TextComponent y = ChatApi.apiChat("&eⓘ",
						ClickEvent.Action.RUN_COMMAND,
						plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunInfo"),
						HoverEvent.Action.SHOW_TEXT, 
						plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.InfoHover")
						+plugin.getYamlHandler().getL().getString("BeforeSelect"));
				bclist.add(y);
				TextComponent z = ChatApi.apiChat("&aⓄ",
						ClickEvent.Action.RUN_COMMAND,
						plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunOpen"),
						HoverEvent.Action.SHOW_TEXT, 
						plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.OpenHover")
						+plugin.getYamlHandler().getL().getString("BeforeSelect"));
				bclist.add(z);
				TextComponent alpha = ChatApi.apiChat("&e✖",
						ClickEvent.Action.SUGGEST_COMMAND,
						plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandRunDelete"),
						HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getL().getString("BeforeSelect"));
				bclist.add(alpha);
				bclist.add(ChatApi.tctl(" &1| "));
			}
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(bclist);
			player.spigot().sendMessage(tc);
		}
		debug("Next and Past Pages");
		plugin.getCommandHelper().pastNextPage(player, "CmdAsh.BaseInfo", page, lastpage,
				plugin.getYamlHandler().getL().getString("CmdAsh.StorageChestList.CommandString"), otherplayer);
		return;
	}
}