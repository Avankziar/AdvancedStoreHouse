package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

public class ARGPlayerInfo extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGPlayerInfo(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		String name = player.getName();
		if(args.length >= 2)
		{
			name = args[1];
			if(!name.equals(player.getName()))
			{
				if(!player.hasPermission(Utility.PERMBYPASSPLAYERINFO))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
					return;
				}
			}
		}
		UUID uuid = Utility.convertNameToUUID(name);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		PluginUser user = PluginUserHandler.getUser(uuid);
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotOnline")));
			return;
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.Headline")
				.replace("%name%", user.getName())));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.Mode")
				.replace("%mode%", returnMode(user.getMode()))));
		int id = user.getDistributionChestID();
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id))
		{
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
					MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", id);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.DC")
					+user.getDistributionChestID() + " &r| "+dc.getChestName()));
			
		}
		int idsc = user.getStorageChestID();
		if(plugin.getMysqlHandler().exist(Type.STORAGECHEST, "`id` = ?", idsc))
		{
			StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(Type.STORAGECHEST, "`id` = ?", idsc);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.SC")
					+user.getStorageChestID() +  " &r| "+sc.getChestName()));
		}
		
		if(user.getItemFilterSet() != null)
		{
			if(user.getItemFilterSet().getID() != 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.ItemFilterID")
						+user.getItemFilterSet().getID()));
			}
		}
		if(!user.getSelectedStorageChest().isEmpty())
		{
			String s = "";
			for(int i : user.getSelectedStorageChest())
			{
				s += i+", ";
			}
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.SelectedStorageChests")
					+s));
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.SearchType")
				+user.getSearchType()));
		return;
	}
	
	private String returnMode(Mode mode)
	{
		switch(mode)
		{
		case CHANGEITEMFILTERSET:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.CHANGEITEMFILTERSET");
		case CONSTRUCT:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.CONSTRUCT");
		case CREATEDISTRIBUTIONCHEST:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.CREATEDISTRIBUTIONCHEST");
		case CREATEITEMFILTERSET:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.CREATEITEMFILTERSET");
		case CREATESTORAGE:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.CREATESTORAGE");
		case UPDATESTORAGEITEMFILTERSET:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.UPDATESTORAGEITEMFILTERSET");
		case OPTIONGUI:
			return plugin.getYamlHandler().getLang().getString("CmdAsh.PlayerInfo.OPTIONGUI");
		}
		return "";
	}
}