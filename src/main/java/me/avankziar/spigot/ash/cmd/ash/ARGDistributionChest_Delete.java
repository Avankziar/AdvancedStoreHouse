package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDistributionChest_Delete extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Delete(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest delete")));
			return;
		}
		DistributionChest dc = null;
		int id = user.getDistributionChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.DistributionChestDontExist")
					.replace("%id%", String.valueOf(user.getDistributionChestID()))));
			return;
		}
		dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", id);
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.DistributionChestDontExist")
					.replace("%id%", user.getDistributionChestName())));
			return;
		}
		if(!dc.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSDELETE))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwner")));
			return;
		}
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dc.getId());
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.DChestDeleted")
				.replace("%name%", dc.getChestName())
				.replace("%id%", String.valueOf(dc.getId()))));
		int count = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, 
				"`distributionchestid` = ?", dc.getId());
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`distributionchestid` = ?", dc.getId());
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.LinkedSChestDeleted")
				.replace("%count%", String.valueOf(count))
				.replace("%name%", dc.getChestName())
				.replace("%id%", String.valueOf(dc.getId()))));
		return;
	}
}