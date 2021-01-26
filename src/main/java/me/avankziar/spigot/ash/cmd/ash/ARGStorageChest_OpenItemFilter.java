package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGStorageChest_OpenItemFilter extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_OpenItemFilter(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash openitemfiler")));
			return;
		}
		int id = user.getStorageChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.OpenItemFilter.DontExist")
					.replace("%id%", String.valueOf(id))));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id);
		String name = "/";
		String iddc = "/";
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID()))
		{
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`id` = ?", user.getDistributionChestID());
			if(dc != null)
			{
				name = dc.getChestName();
				iddc = ""+dc.getId();
				if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString()))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwnerOrMember")));
					return;
				}
			}
		}
		user.setMode(Mode.UPDATESTORAGEITEMFILTERSET);
		user.setStorageChestID(sc.getId());
		PluginUserHandler.addUser(user);
		Inventory inv = Bukkit.createInventory(null, 6*9, 
				plugin.getYamlHandler().getL().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriority()))
				.replace("%name%", name)
				.replace("%dcid%", iddc)
				.replace("%id%", String.valueOf(sc.getId())));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}
}