package main.java.me.avankziar.spigot.advancedstorehouse.commands.advancedstorehouse;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.advancedstorehouse.assistance.BackgroundTask;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.advancedstorehouse.commands.tree.ArgumentModule;

public class ARGAutomaticDistributionInfo extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGAutomaticDistributionInfo(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.AutoDistribution.Headline")));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.AutoDistribution.TotalTick")
				.replace("%time%", 
						TimeHandler.getRepeatingTime(new Long(BackgroundTask.schedularTotalTicks*20)))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.AutoDistribution.TicksPerDc")
				.replace("%time%", 
						TimeHandler.getRepeatingTime(new Long(BackgroundTask.schedularTicksPerDc*20)))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getL().getString("CmdAsh.AutoDistribution.NextDc")
				.replace("%id%", String.valueOf(BackgroundTask.nextDcId))));
		return;
	}
}