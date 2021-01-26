package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGItemFilterSet_Select extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGItemFilterSet_Select(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		String nameOrID = args[2];
		String otherplayer = "";
		String otheruuid = player.getUniqueId().toString();
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash itemfilterset select")));
			return;
		}
		if(args.length >= 4)
		{
			otherplayer = args[3];
			if(!otherplayer.equals(player.getName()))
			{
				if(!player.hasPermission(Utility.PERMBYPASSITEMFILTERSETSELECT))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NoPermission")));
					return;
				}
				otherplayer = args[3];
				UUID uuid = Utility.convertNameToUUID(otherplayer);
				if(uuid == null)
				{
					String nameconvert = Utility.convertUUIDToName(otherplayer);
					if(nameconvert == null)
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("PlayerNotExist")));
						return;
					}
					otheruuid = otherplayer; //Es wurde eine UUId angegeben.
				} else
				{
					otheruuid = uuid.toString();
				}
			}
		}
		ItemFilterSet ifs = null;
		if(MatchApi.isInteger(nameOrID))
		{
			int id = Integer.parseInt(nameOrID);
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.ITEMFILTERSET, "`id` = ? AND `owner_uuid` = ?", id, otheruuid))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.NotExist")
						.replace("%name%", nameOrID)));
				return;
			}
			ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET,
					"`id` = ? AND `owner_uuid` = ?", id, otheruuid);
		} else
		{
			if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.ITEMFILTERSET,
					"`itemfiltersetname` = ? AND `owner_uuid` = ?", nameOrID, otheruuid))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.NotExist")
						.replace("%name%", nameOrID)));
				return;
			}
			ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET, 
					"`itemfiltersetname` = ? AND `owner_uuid` = ?", nameOrID, otheruuid);
		}
		if(ifs == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.NotExist")
					.replace("%name%", nameOrID)));
			return;
		}
		if(!ifs.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSITEMFILTERSETSELECT))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwner")));
			return;
		}
		user.setItemFilterSet(ifs);
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetSelect.Selected")
				.replace("%name%", ifs.getName())
				.replace("%id%", String.valueOf(ifs.getID()))));
		return;
	}
}
