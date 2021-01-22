package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGItemFilterSet_Delete extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGItemFilterSet_Delete(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
				.replace("%cmd%", "/ash itemfilterset delete")));
			return;
		}
		if(user.getItemFilterSet() == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSet.NotExist")));
			return;
		}
		ItemFilterSet ifs = null;
		int id = user.getItemFilterSet().getID();
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.ITEMFILTERSET, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.NotExist")
					.replace("%name%", String.valueOf(id))));
			return;
		}
		ifs = (ItemFilterSet) plugin.getMysqlHandler().getData(MysqlHandler.Type.ITEMFILTERSET, "`id` = ?", id);
		if(ifs == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.NotExist")
					.replace("%name%", String.valueOf(id))));
			return;
		}
		if(!ifs.getOwneruuid().equals(user.getUUID()) && !player.hasPermission(Utility.PERMBYPASSITEMFILTERSET))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("NotOwner")));
			return;
		}
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.ITEMFILTERSET, "`id` = ?", ifs.getID());
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.ItemFilterSetDelete.Deleted")
				.replace("%id%", String.valueOf(ifs.getID()))
				.replace("%name%", ifs.getName())));
		return;
	}
}