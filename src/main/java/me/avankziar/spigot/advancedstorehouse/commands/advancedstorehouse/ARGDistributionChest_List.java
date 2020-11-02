package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;
import java.util.ArrayList;
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
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGDistributionChest_List extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_List(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest list")));
			return;
		}
		int quantity = plugin.getYamlHandler().get().getInt("AmountToDisplayDistributuionChestInListCommand", 25);
		int start = page*quantity;		
		ArrayList<DistributionChest> dcList = ConvertHandler.convertListII(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id`",
						true, start, quantity, "`owner_uuid` = ?", otheruuid));
		if(dcList == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.Empty")));
			return;
		}
		if(dcList.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.Empty")));
			return;
		}
		int last = ((DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`owner_uuid` = ?",
				otheruuid)).getId();
		int secondLast = dcList.get(dcList.size()-1).getId();
		boolean lastpage = false;
		if(secondLast >= last)
		{
			lastpage = true;
		}
		List<BaseComponent> bclist = new ArrayList<>();
		for(DistributionChest dc : dcList)
		{
			TextComponent x = ChatApi.clickEvent("&6"+dc.getChestName()+"&f:",
					ClickEvent.Action.RUN_COMMAND,
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRun")
					.replace("%name%", String.valueOf(dc.getChestName()) +" "+ otherplayer));
			bclist.add(x);
			TextComponent y = ChatApi.apiChat("&eⓘ",
					ClickEvent.Action.RUN_COMMAND,
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRunInfo"),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getL().getString("BeforeSelect"));
			bclist.add(y);
			TextComponent z = ChatApi.apiChat("&c✖",
					ClickEvent.Action.SUGGEST_COMMAND,
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandRunDelete"),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getL().getString("BeforeSelect"));
			bclist.add(z);
			bclist.add(ChatApi.tctl(" &1| "));
		}
		TextComponent tc = ChatApi.tc("");
		tc.setExtra(bclist);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.Headline")
				.replace("%player%", otherplayer)));
		player.spigot().sendMessage(tc);
		plugin.getCommandHelper().pastNextPage(player, "CmdAsh.BaseInfo", page, lastpage,
				plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestList.CommandString"), otherplayer);
		return;
	}
}