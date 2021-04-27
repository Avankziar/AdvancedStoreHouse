package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash distributionchest info")));
			return;
		}
		DistributionChest dc = null;
		if(args.length == 2)
		{
			int id = user.getDistributionChestID();
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		} else if(args.length == 3 && MatchApi.isInteger(args[2]))
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", Integer.parseInt(args[2]));
		} else if(args.length == 3)
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`owner_uuid` = ? AND `chestname` = ?", 
					player.getUniqueId().toString(), args[2]);
		}
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSINFO))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
			return;
		}
		int storagechestamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `endstorage` = ?", dc.getId(), false);
		int storagechestamountend = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
				"`distributionchestid` = ? AND `endstorage` = ?", dc.getId(), true);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.HeadlineDC")
				.replace("%id%", String.valueOf(dc.getId()))
				.replace("%name%", dc.getChestName())));
		String owner = Utility.convertUUIDToName(dc.getOwneruuid());
		if(owner != null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Owner")
					.replace("%owner%", owner)));
		} else
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Owner")
					.replace("%owner%", "/")));
		}
		String loc = dc.getServer()+" &a"+dc.getWorld()+" &d"+dc.getBlockX()+" "+dc.getBlockY()+" "+dc.getBlockZ();
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Location")
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
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Member")
				.replace("%member%", "["+String.join(" ", list)+"]")));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.AutoDistribution")
				.replace("%auto%", String.valueOf(dc.isAutomaticDistribution()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.NormalPriority")
				.replace("%nprio%",  String.valueOf(dc.isNormalPriority()))));
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.PriorityType")
				.replace("%priotype%",  String.valueOf(dc.getPriorityType()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.PriorityNumber")
				.replace("%prion%",  String.valueOf(dc.getPriorityNumber()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Random")
				.replace("%random%",  String.valueOf(dc.isDistributeRandom()))));
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.SCAmount")+storagechestamount));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.SCAmountEnd")+storagechestamountend));
		return;
	}
}