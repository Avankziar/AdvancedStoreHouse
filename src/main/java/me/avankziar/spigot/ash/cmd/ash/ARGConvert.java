package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

public class ARGConvert extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGConvert(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		int dis = plugin.getMysqlHandler().getCount(Type.DISTRIBUTIONCHEST, "`id`", "`server` = ?", server);
		int sto = plugin.getMysqlHandler().getCount(Type.STORAGECHEST, "`id`", "`server` = ?", server);
		final int count = dis+sto;
		long time = (count/60)*1000;
		if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("bestätigen") || args[1].equalsIgnoreCase("confirm"))
			{
				time += System.currentTimeMillis();
				plugin.getMysqlHandler().startConvert(server, player, dis, sto);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Convert.Start")
						.replace("%time%", TimeHandler.getDateTime(time))));
				return;
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Convert.PleaseConfirm")
				.replace("%count%", String.valueOf(count))
				.replace("%time%", TimeHandler.getRepeatingTime(time, "HH:mm:ss"))));
		player.sendMessage(ChatApi.tl("&eDistributionChest Count: "+dis));
		player.sendMessage(ChatApi.tl("&EStorageChest Count: "+sto));
		return;
	}
}