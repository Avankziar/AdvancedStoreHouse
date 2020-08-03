package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ChestHandler;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;
import main.java.me.avankziar.spigot.advancedstorehouse.database.MysqlHandler;

public class ARGDistributionChest_Info extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Info(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest info")));
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
		int storagechestamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `endstorage` = ?", id, false);
		int storagechestamountend = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `endstorage` = ?", id, true);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.HeadlineD")
				.replace("%id%", String.valueOf(id))
				.replace("%name%", dc.getChestName())));
		String owner = Utility.convertUUIDToName(dc.getOwneruuid());
		if(owner != null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.Owner")
					.replace("%owner%", owner)));
		} else
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.Owner")
					.replace("%owner%", "/")));
		}
		String loc = dc.getServer()+" &a"+dc.getWorld()+" &d"+dc.getBlockX()+" "+dc.getBlockY()+" "+dc.getBlockZ();
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.Location")
				.replace("%pos%", loc)));
		List<String> list = new ArrayList<>();
		for(String uuid : dc.getMemberList())
		{
			String name = Utility.convertUUIDToName(uuid);
			if(name != null)
			{
				list.add(name);
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.Member")
				.replace("%member%", "["+String.join(" ", list)+"]")));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.AutoDistribution")
				.replace("%auto%", String.valueOf(dc.isAutomaticDistribution()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.NormalPriority")
				.replace("%nprio%",  String.valueOf(dc.isNormalPriority()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.SChestAmount")+storagechestamount));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Info.SChestAmountEnd")+storagechestamountend));
		return;
	}
}