package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

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
import main.java.me.avankziar.spigot.ash.eventhandler.InteractSubHandler;

public class ARGDistributionChest_RemoteTriggerAnimation extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_RemoteTriggerAnimation(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest breaking")));
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
				&& !player.hasPermission(Utility.PERMBYPASSSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
			return;
		}
		new InteractSubHandler().animation(plugin, player, dc);
	}
}