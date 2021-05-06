package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
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
import main.java.me.avankziar.spigot.ash.eventhandler.InteractSubHandler;

public class ARGDistributionChest_Search extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDistributionChest_Search(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash search")));
			return;
		}
		if(player.getInventory().getItemInMainHand() == null | player.getInventory().getItemInMainHand().getType() == Material.AIR)
		{
			searchChest(player, user, args);
		} else
		{
			searchItem(player, user, args);
		}
	}
	
	private void searchChest(Player player, PluginUser user, String[] args) throws IOException
	{
		DistributionChest dc = null;
		if(args.length == 2)
		{
			int id = user.getDistributionChestID();
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
			
		} else if(args.length == 3 && MatchApi.isInteger(args[2]))
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", Integer.parseInt(args[2]));
		} else if(args.length == 3)
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`owner_uuid` = ? AND `chestname` = ?", 
					player.getUniqueId().toString(), args[2]);
		}
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		if(!dc.getServer().equals(server))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotSameServer")
					.replace("%yourserver%", server)
					.replace("%server%", dc.getServer())));
			return;
		}
		if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
				&& !player.hasPermission(Utility.PERMBYPASSSEARCH))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
			return;
		}
		if(InteractSubHandler.chestAnimationCooldown.containsKey(dc.getId()))
		{
			if(InteractSubHandler.chestAnimationCooldown.get(dc.getId()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.AnimationCooldown"))
						.replace("%dcid%", String.valueOf(dc.getId()))
						.replace("%dcname%", dc.getChestName()));
				return;
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Search.SearchDc")
				.replace("%dcid%", String.valueOf(dc.getId()))
				.replace("%dcname%", dc.getChestName())));
		new InteractSubHandler().animation(plugin, player, dc, new ArrayList<StorageChest>());
	}
	
	private void searchItem(Player player, PluginUser user, String[] args) throws IOException
	{
		DistributionChest dc = null;
		if(args.length == 2)
		{
			int id = user.getDistributionChestID();
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
			
		} else if(args.length == 3 && MatchApi.isInteger(args[2]))
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", Integer.parseInt(args[2]));
		} else if(args.length == 3)
		{
			dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`owner_uuid` = ? AND `chestname` = ?", 
					player.getUniqueId().toString(), args[2]);
		}
		if(dc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.DChestDontExist")));
			return;
		}
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		if(!dc.getServer().equals(server))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotSameServer")
					.replace("%yourserver%", server)
					.replace("%server%", dc.getServer())));
			return;
		}
		if(InteractSubHandler.chestAnimationCooldown.containsKey(dc.getId()))
		{
			if(InteractSubHandler.chestAnimationCooldown.get(dc.getId()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Option.AnimationCooldown"))
						.replace("%dcid%", String.valueOf(dc.getId()))
						.replace("%dcname%", dc.getChestName()));
				return;
			}
		}
		final ItemStack is = player.getInventory().getItemInMainHand().clone();
		final String data = ChestHandler.getGroundSpecs(is);
		final String hoveritem = plugin.getUtility().convertItemStackToJson(is);
		final String displayname = (is.getItemMeta() != null) 
				? (is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : is.getType().toString()) 
				: is.getType().toString();
		ArrayList<StorageChest> sclist = ConvertHandler.convertListIII(
				plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`id`", false,
						"`distributionchestid` = ? AND `server` = ? AND `searchcontent` LIKE ?",
						dc.getId(), server, "%"+data+"%"));
		player.spigot().sendMessage(ChatApi.apiChatItem(plugin.getYamlHandler().getLang().getString("CmdAsh.Search.SearchItem")
				.replace("%item%", displayname)
				.replace("%dcid%", String.valueOf(dc.getId()))
				.replace("%dcname%", dc.getChestName())
				.replace("%count%", String.valueOf(sclist.size())), 
				null, null, hoveritem));
		new InteractSubHandler().animation(plugin, player, dc, sclist);
	}
}