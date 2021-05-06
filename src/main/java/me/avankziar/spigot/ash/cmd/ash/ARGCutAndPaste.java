package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

public class ARGCutAndPaste extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGCutAndPaste(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		final String uuid = player.getUniqueId().toString();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
					.replace("%cmd%", "/ash cutandpaste")));
			return;
		}
		if(user.getDistributionChestID() == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CutAndPaste.NoDcSelected")));
			return;
		}
		final int disID = user.getDistributionChestID();
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(Type.DISTRIBUTIONCHEST, "`id`", disID);
		if(user.getSelectedStorageChest().isEmpty() || user.getSelectedStorageChest().size() == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CutAndPaste.NoScSelected")));
			return;
		}
		final ArrayList<Integer> list = user.getSelectedStorageChest();
		final String disName = (dc != null) ? dc.getChestName() : "";
		new BukkitRunnable()
		{
			int count = 0;
			int countglobal = 0;
			int amount = 15;
			@Override
			public void run()
			{
				if(countglobal >= (list.size()-1))
				{
					cancel();
					if(player != null)
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CutAndPaste.Finished")
								.replace("%dc%", disID+" | "+disName)
								.replace("%amount%", String.valueOf(list.size()))));
					}
					return;
				}
				while(count < amount)
				{
					int id = list.get(countglobal);
					StorageChest sc = null;
					try
					{
						sc = (StorageChest) plugin.getMysqlHandler().getData(Type.STORAGECHEST, "`id` = ?", id);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					if(sc == null)
					{
						count++;
						countglobal++;
						continue;
					}
					DistributionChest dc = null;
					try
					{
						dc = (DistributionChest) plugin.getMysqlHandler().getData(Type.DISTRIBUTIONCHEST,
								"`id` = ?", sc.getDistributionChestID());
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					if(dc == null)
					{
						count++;
						countglobal++;
						continue;
					}
					if(!uuid.equals(dc.getOwneruuid()))
					{
						count++;
						countglobal++;
						continue;
					}
					sc.setDistributionChestID(disID);
					plugin.getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
					count++;
					countglobal++;
				}
				count = 0;
			}
		}.runTaskTimerAsynchronously(plugin, 5L, 5L);
	}
}