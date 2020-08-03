package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.Utility;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;

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
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NoPermission")));
					return;
				}
			}
		}
		UUID uuid = Utility.convertNameToUUID(name);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotExist")));
			return;
		}
		PluginUser user = PluginUserHandler.getUser(uuid);
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotOnline")));
			return;
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.Headline")
				.replace("%name%", user.getName())));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.Mode")
				.replace("%mode%", returnMode(user.getMode()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.DC")
				+user.getDistributionChestID()));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.SC")
				+user.getStorageChestID()));
		if(user.getItemFilterSet() != null)
		{
			if(user.getItemFilterSet().getID() != 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.ItemFilterID")
						+user.getItemFilterSet().getID()));
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.Priority")
				+user.getPriority()));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.SearchType")
				+user.getSearchType()));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.Override")
				+user.isOverride()));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.EndStorage")
				+user.isEndStorage()));
		return;
	}
	
	private String returnMode(Mode mode)
	{
		switch(mode)
		{
		case BLOCKINFO:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.BLOCKINFO");
		case CHANGEITEMFILTERSET:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.CHANGEITEMFILTERSET");
		case CONSTRUCT:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.CONSTRUCT");
		case CREATEDISTRIBUTIONCHEST:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.CREATEDISTRIBUTIONCHEST");
		case CREATEITEMFILTERSET:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.CREATEITEMFILTERSET");
		case CREATESTORAGE:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.CREATESTORAGE");
		case NONE:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.NONE");
		case POSITIONUPDATEDISTRIBUTION:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.POSITIONUPDATEDISTRIBUTION");
		case POSITIONUPDATESTORAGE: 
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.POSITIONUPDATESTORAGE");
		case UPDATESTORAGE:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.UPDATESTORAGE");
		case UPDATESTORAGEITEMFILTERSET:
			return plugin.getYamlHandler().getL().getString("CmdAsh.PlayerInfo.UPDATESTORAGEITEMFILTERSET");
		}
		return "";
	}
}