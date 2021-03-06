package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;

public class ARGCancel extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGCancel(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
					.replace("%cmd%", "/ash cancel")));
			return;
		}
		user.setMode(Mode.CONSTRUCT);
		user.setDistributionChestID(0);
		Inventory inv = Bukkit.createInventory(null, 6*9);
		user.setItemFilterSet(new ItemFilterSet(0, "", user.getUUID(), inv.getContents()));
		user.setStorageChestID(0);
		user.setSelectedStorageChest(new ArrayList<>());
		if(user.getCompassLocation() != null)
		{
			player.setCompassTarget(user.getCompassLocation());
			user.setCompassLocation(null);
		}
		PluginUserHandler.addUser(user);
		player.sendMessage(ChatApi.tl(plugin.getUtility().getPrefix()+" "+
				plugin.getYamlHandler().getLang().getString("CmdAsh.Cancel.IsCancel")));
		return;
	}
}
