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
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGStorageChest_Select extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_Select(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash delete")));
			return;
		}
		String idString = args[2];
		if(!MatchApi.isInteger(idString))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("IllegalArgument")));
			return;
		}
		int id = Integer.parseInt(idString);
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SChestDontExist")));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id);
		boolean dcExist = true;
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID()))
		{
			dcExist = false;
		}
		if(dcExist)
		{
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID());
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSSELECT))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
				return;
			}
			user.setStorageChestID(sc.getId());
			user.addSelectedStorageChest(sc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectSC")
					.replace("%idsc%", String.valueOf(sc.getId()))
					.replace("%iddc%", String.valueOf(dc.getId()))
					.replace("%namedc%", dc.getChestName())
					.replace("%namesc%", sc.getChestName())));
		} else
		{
			if(sc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSSELECT))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwner")));
				return;
			}
			user.setStorageChestID(sc.getId());
			user.addSelectedStorageChest(sc.getId());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SelectSCWithOutDC")
					.replace("%id%", String.valueOf(sc.getId()))));
		}
		PluginUserHandler.addUser(user);
		return;
	}
}