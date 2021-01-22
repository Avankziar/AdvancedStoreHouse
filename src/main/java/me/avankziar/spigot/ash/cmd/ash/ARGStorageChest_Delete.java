package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGStorageChest_Delete extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_Delete(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash delete")));
			return;
		}
		int id = user.getStorageChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.StorageChestDontExist")
					.replace("%id%", String.valueOf(id))));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
				"`id` = ?", id);
		if(!sc.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSDELETE))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwner")));
			return;
		}
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`id` = ?", sc.getId());
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.Delete.SChestDeleted")
				.replace("%id%", String.valueOf(sc.getId()))));
		return;
	}
}
