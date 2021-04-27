package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;

public class ARGCheckUnboundChest extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	public static boolean inProgress = false;
	
	public ARGCheckUnboundChest(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		if(inProgress)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CheckUnboundChest.InProgress")));
			return;
		}
		int dis = plugin.getMysqlHandler().getCount(Type.DISTRIBUTIONCHEST, "`id`", "?", 1);
		int sto = plugin.getMysqlHandler().getCount(Type.STORAGECHEST, "`id`", "?", 1);
		int count = dis+sto;
		long time = (count/60)*1000;
		if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("bestätigen") || args[1].equalsIgnoreCase("confirm"))
			{
				inProgress = true;
				time += System.currentTimeMillis();
				checkStartI(player, dis);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CheckUnboundChest.Start")
						.replace("%time%", TimeHandler.getDateTime(time))));
				return;
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.CheckUnboundChest.PleaseConfirm")
				.replace("%count%", String.valueOf(count))
				.replace("%time%", TimeHandler.getRepeatingTime(time, "HH:mm:ss"))));
		return;
	}
	
	private void checkStartI(final Player player, int lastid)
	{
		new BukkitRunnable()
		{
			int start = 0;
			final int amount = 15;
			@Override
			public void run()
			{
				if(start >= lastid)
				{
					cancel();
					player.sendMessage(ChatApi.tl("&6CheckUnboundChest Distributionchest finish, progress with StorageChest!"));
					int lastidsc = plugin.getMysqlHandler().getCount(Type.STORAGECHEST, "`id`", "?", 1);
					checkStartII(player, lastidsc);
					return;
				}
				start += amount - plugin.getMysqlHandler().checkUnboundChest(player, start, amount);
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 5L);
	}
	
	private void checkStartII(Player player, int lastid)
	{
		new BukkitRunnable()
		{
			int start = 0;
			final int amount = 15;
			@Override
			public void run()
			{
				if(start >= lastid)
				{
					cancel();
					if(player != null)
					{
						player.sendMessage(ChatApi.tl("&6Storagechest and CheckUnboundChest finish!"));
						inProgress = false;
					}
					return;
				}
				start += amount - plugin.getMysqlHandler().checkUnboundChestII(player, start, amount);
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 5L);
	}
}