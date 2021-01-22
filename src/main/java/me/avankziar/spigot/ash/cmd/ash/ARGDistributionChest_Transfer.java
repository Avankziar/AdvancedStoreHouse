package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDistributionChest_Transfer extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Transfer(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash distributionchest transfer")));
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
		if(!dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSTRANSFER))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwner")));
			return;
		}
		String othername = args[2];
		String otheruuid = Utility.convertNameToUUID(othername).toString();
		if(otheruuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotExist")));
			return;
		}
		dc.setOwneruuid(otheruuid);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Transfer.TransferDc")
				.replace("%id%", String.valueOf(id))
				.replace("%name%", dc.getChestName())
				.replace("%player%", othername)));
		Player other = Bukkit.getPlayer(UUID.fromString(otheruuid));
		if(other != null)
		{
			other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Transfer.TransferDcToYou")
					.replace("%id%", String.valueOf(id))
					.replace("%name%", dc.getChestName())
					.replace("%player%", player.getName())));
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", id);
		return;
	}
}