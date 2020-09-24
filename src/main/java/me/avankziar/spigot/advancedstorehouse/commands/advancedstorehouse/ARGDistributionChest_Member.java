package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGDistributionChest_Member extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Member(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash distributionchest member")));
			return;
		}
		int id = user.getDistributionChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSINFO))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
			return;
		}
		String otherplayername = args[2];
		UUID uuid = Utility.convertNameToUUID(otherplayername);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotExist")));
			return;
		}
		String otherplayeruuid = uuid.toString();
		if(dc.getMemberList() != null)
		{
			if(dc.getMemberList().contains(otherplayeruuid))
			{
				List<String> list = new ArrayList<>();
				list.addAll(dc.getMemberList());
				list.remove(otherplayeruuid);
				dc.setMemberList(list);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestMember.Remove")
						.replace("%dc%", dc.getChestName())
						.replace("%player%", otherplayername)));
			} else
			{
				List<String> list = new ArrayList<>();
				list.addAll(dc.getMemberList());
				list.add(otherplayeruuid);
				dc.setMemberList(list);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestMember.Add")
						.replace("%dc%", dc.getChestName())
						.replace("%player%", otherplayername)));
			}
		} else
		{
			List<String> list = new ArrayList<>();
			list.add(otherplayeruuid);
			dc.setMemberList(list);
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
		return;
	}
}