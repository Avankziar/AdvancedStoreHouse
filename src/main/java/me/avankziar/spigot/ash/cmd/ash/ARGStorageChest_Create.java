package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGStorageChest_Create extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_Create(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash storagechest create")));
			return;
		}
		int id = user.getDistributionChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
			return;
		}
		user.setDistributionChestID(dc.getId());
		user.setMode(Mode.CREATESTORAGE);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.StorageChest.CreateActive")
				.replace("%id%", String.valueOf(id))));
		PluginUserHandler.addUser(user);
		return;
	}
}