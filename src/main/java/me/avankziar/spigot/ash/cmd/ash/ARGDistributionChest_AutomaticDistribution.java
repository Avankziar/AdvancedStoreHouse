package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDistributionChest_AutomaticDistribution extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_AutomaticDistribution(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				&& !player.hasPermission(Utility.PERMBYPASSSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
			return;
		}
		if(dc.isAutomaticDistribution() == true)
		{
			dc.setAutomaticDistribution(false);
			player.sendMessage(ChatApi.tl(
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestAutomaticDistribution.Deactive")
					.replace("%name%", dc.getChestName())));
		} else
		{
			dc.setAutomaticDistribution(true);
			player.sendMessage(ChatApi.tl(
					plugin.getYamlHandler().getL().getString("CmdAsh.DistributionChestAutomaticDistribution.Active")
					.replace("%name%", dc.getChestName())));
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
		return;
	}
}