package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGItemFilterSet_List extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGItemFilterSet_List(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				if(!player.hasPermission(Utility.PERMBYPASSITEMFILTERSETLIST))
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
				.replace("%cmd%", "/ash itemfilterset list")));
			return;
		}
		int start = page*25;
		int quantity = 25;
		ArrayList<ItemFilterSet> ifsList = ConvertHandler.convertListIV(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.ITEMFILTERSET, "`id`",
						true, start, quantity, "`owner_uuid` = ?", otheruuid));
		if(ifsList == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetList.Empty")));
			return;
		}
		if(ifsList.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetList.Empty")));
			return;
		}
		int last = ((ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET, "`owner_uuid` = ?", otheruuid)).getID();
		int secondLast = ifsList.get(ifsList.size()-1).getID();
		boolean lastpage = false;
		if(secondLast >= last)
		{
			lastpage = true;
		}
		List<BaseComponent> bclist = new ArrayList<>();
		for(ItemFilterSet ifs : ifsList)
		{
			int amount = ChestHandler.getItemsCount(ifs.getContents());
			String hovertext = "";
			if(amount > 0)
			{
				hovertext += plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetList.LineOne")
						.replace("%amount%", String.valueOf(amount));
				hovertext += ChestHandler.getMaterialList(ifs.getContents());
			}
			TextComponent x = ChatApi.apiChat("&e"+ifs.getName()+"&f:",
					ClickEvent.Action.RUN_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.IFS_SELECT)+ifs.getID()+" "+ifs.getOwneruuid(),
					HoverEvent.Action.SHOW_TEXT,
					hovertext);
			bclist.add(x);
			TextComponent y = ChatApi.apiChat("&aⓄ",
					ClickEvent.Action.RUN_COMMAND,
					PluginSettings.settings.getCommands().get(KeyHandler.IFS_UPDATE)+ifs.getOwneruuid(),
					HoverEvent.Action.SHOW_TEXT, 
					plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetList.OpenHover")
					+plugin.getYamlHandler().getLang().getString("BeforeSelect"));
			bclist.add(y);
			TextComponent z = ChatApi.apiChat("&c✖",
					ClickEvent.Action.SUGGEST_COMMAND,
					PluginSettings.settings.getCommands().get(KeyHandler.IFS_DELETE),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("BeforeSelect"));
			bclist.add(z);
			bclist.add(ChatApi.tctl(" &1| "));
		}
		TextComponent tc = ChatApi.tc("");
		tc.setExtra(bclist);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.ItemFilterSetList.Headline")
				.replace("%player%", otherplayer)));
		player.spigot().sendMessage(tc);
		plugin.getCommandHelper().pastNextPage(player, "CmdAsh.BaseInfo", page, lastpage,
				PluginSettings.settings.getCommands().get(KeyHandler.IFS_LIST), otherplayer);
		return;
	}
}
