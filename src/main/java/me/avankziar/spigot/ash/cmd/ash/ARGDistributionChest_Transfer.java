package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash distributionchest transfer")));
			return;
		}
		final int id = user.getDistributionChestID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
		if(!dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSTRANSFER))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwner")));
			return;
		}
		String othername = args[2];
		UUID uuid = Utility.convertNameToUUID(othername);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		String otheruuid = Utility.convertNameToUUID(othername).toString();
		dc.setOwneruuid(otheruuid);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Transfer.TransferDc")
				.replace("%id%", String.valueOf(id))
				.replace("%name%", dc.getChestName())
				.replace("%player%", othername)));
		Player other = Bukkit.getPlayer(UUID.fromString(otheruuid));
		if(other != null)
		{
			other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Transfer.TransferDcToYou")
					.replace("%id%", String.valueOf(id))
					.replace("%name%", dc.getChestName())
					.replace("%player%", player.getName())));
		}
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.DISTRIBUTIONCHEST, dc, "`id` = ?", id);
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				ArrayList<StorageChest> sclist = new ArrayList<>();
				try
				{
					sclist = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST, "`id`", false, "`distributionchestid` = ?", id));
				} catch (IOException e){}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Transfer.TransferScStart")
						.replace("%amount%", String.valueOf(sclist.size()))));
				for(StorageChest sc : sclist)
				{
					sc.setOwneruuid(otheruuid);
					plugin.getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Transfer.TransferScEnd")));
			}
		}.runTaskAsynchronously(plugin);
		
		return;
	}
}