package main.java.me.avankziar.spigot.ash.listener;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class BlockBreakListener implements Listener
{
	private AdvancedStoreHouse plugin;
	
	public BlockBreakListener(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) throws IOException
	{
		Block block = event.getBlock();
		if(block == null)
		{
			//debug(event.getPlayer(), "BlockBreak block == null");
			return;
		}
		if(block.getState() == null)
		{
			//debug(event.getPlayer(), "BlockBreak block.State == null");
			return;
		}
		if(!(block.getState() instanceof Chest)
				&& !(block.getState() instanceof Barrel))
		{
			//debug(event.getPlayer(), "BlockBreak !instanceof Chest...");
			return;
		}
		if(event.isCancelled())
		{
			return;
		}
		final String server = plugin.getYamlHandler().getConfig().getString("Servername");
		final Location loc = event.getBlock().getLocation();
		if(loc == null || server == null)
		{
			return;
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			debug(event.getPlayer(), "BlockBreak delete dc");
			final DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			if(!dc.getOwneruuid().equals(event.getPlayer().getUniqueId().toString()) 
					&& !event.getPlayer().hasPermission(Utility.PERMBYPASSDELETE))
			{
				event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwner")));
				event.setCancelled(true);
				return;
			}
			PluginUser user = PluginUserHandler.getUser(event.getPlayer().getUniqueId());
			if(user != null)
			{
				if(!user.canDistributionChestBreak())
				{
					event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SafetyBreak")));
					event.setCancelled(true);
					return;
				}
			}
			final int id = dc.getId();
			event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockBreak.DeleteDC")
					.replace("%id%", String.valueOf(dc.getId()))
					.replace("%name%", dc.getChestName())));
			if(plugin.getYamlHandler().getConfig().getBoolean("BlockBreakDistributionChestDeleteLinkedStorageChest", true))
			{
				int count = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, 
						"`distributionchestid` = ?", id);
				plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`distributionchestid` = ?", id);
				event.getPlayer().sendMessage(ChatApi.tl(
						plugin.getYamlHandler().getLang().getString("CmdAsh.Delete.LinkedSChestDeleted")
						.replace("%count%", String.valueOf(count))
						.replace("%name%", dc.getChestName())
						.replace("%id%", String.valueOf(dc.getId()))));
			}
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.DISTRIBUTIONCHEST, 
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			return;
		}
		
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			debug(event.getPlayer(), "BlockBreak delete sc");
			final StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			String scid = "";
			if(sc != null)
			{
				String.valueOf(sc.getId());
				if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
						" `id` = ?", sc.getDistributionChestID()))
				{
					DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
							" `id` = ?", sc.getDistributionChestID());
					if(!dc.getOwneruuid().equals(event.getPlayer().getUniqueId().toString()) 
							&& !event.getPlayer().hasPermission(Utility.PERMBYPASSDELETE))
					{
						event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwner")));
						event.setCancelled(true);
						return;
					}
				}
			}
			event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockBreak.DeleteSC")
					.replace("%id%", scid)));
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, 
					" `server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			return;
		}
		debug(event.getPlayer(), "BlockBreak didnt find any chest");
	}
}
