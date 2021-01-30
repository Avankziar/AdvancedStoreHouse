package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGDelete extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGDelete(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		String server = args[1];
		String world = args[2];
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`server` = ? AND `world` = ?", server, world);
		plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`server` = ? AND `world` = ?", server, world);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Delete.IsDeleted")
				.replace("%server%", server)
				.replace("%world%", world)));
		return;
	}
}