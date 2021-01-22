package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;

public class ARGBlockInfo extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGBlockInfo(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("DatabaseError")
				.replace("%cmd%", "/ash check")));
			return;
		}
		if(user.getMode() == Mode.BLOCKINFO)
		{
			user.setMode(Mode.CONSTRUCT);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.BlockInfo.Deactive")));
		} else
		{
			user.setMode(Mode.BLOCKINFO);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.BlockInfo.Active")));
		}
		PluginUserHandler.addUser(user);
		return;
	}
}