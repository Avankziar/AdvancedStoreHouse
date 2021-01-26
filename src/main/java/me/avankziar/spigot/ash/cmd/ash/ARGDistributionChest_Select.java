package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDistributionChest_Select extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Select(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest select")));
			return;
		}
		String name = args[2];
		String otherplayer = player.getName();
		String otheruuid = player.getUniqueId().toString();
		if(args.length >= 4)
		{
			if(!otherplayer.equals(args[3]))
			{
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
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`chestname` = ? AND `owner_uuid` = ?", name, otheruuid))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`chestname` = ? AND `owner_uuid` = ?", name, otheruuid);
		if(!ChestHandler.isMember(
				player,
				dc) && 
				!dc
				.getOwneruuid()
				.equals(player
						.getUniqueId()
						.toString())
				&& !player
				.hasPermission(Utility.PERMBYPASSSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
			return;
		}
		user.setDistributionChestID(dc.getId());
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Select.SelectDChest")
				.replace("%iddc%", String.valueOf(dc.getId()))
				.replace("%name%", dc.getChestName())));
		PluginUserHandler.addUser(user);
		return;
	}
}