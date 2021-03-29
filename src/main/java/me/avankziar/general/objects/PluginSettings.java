package main.java.me.avankziar.general.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class PluginSettings
{
	private String server;
	private boolean mysql;
	private boolean lwc;
	
	private boolean automaticDistribution;
	private List<String> serverRestartTime = new ArrayList<>();
	private int minimumTickPerDistributionChest;
	private long directPauseValue;
	private long indirectPauseValue;
	
	private int waitBeforStartFactor;
	private int delayedTicks;
	private int delayChainChest;
	private int delayedChainTicks;
	private int chestsPerTick;
	
	private int voidChestRun;
	private int voidChestsPerTick;
	
	private int storageChestAmountWhereShowParticels;
	
	private LinkedHashMap<String, String> commands = new LinkedHashMap<>(); //To save commandstrings
	
	public static PluginSettings settings;
	
	public PluginSettings(String server, boolean mysql, boolean lwc,
			boolean automaticDistribution, List<String> serverRestartTime, int minimumTickPerDistributionChest,
			long directPauseValue, long indirectPauseValue,
			int waitBeforStartFactor, int delayedTicks, int delayChainChest, int delayedChainTicks, int chestsPerTick,
			int voidChestRun, int voidChestsPerTick,
			int storageChestAmountWhereShowParticels)
	{
		setServer(server);
		setMysql(mysql);
		
		setAutomaticDistribution(automaticDistribution);
		setServerRestartTime(serverRestartTime);
		setMinimumTickPerDistributionChest(minimumTickPerDistributionChest);
		setDirectPauseValue(indirectPauseValue);
		setIndirectPauseValue(indirectPauseValue);
		
		setWaitBeforStartFactor(waitBeforStartFactor);
		setDelayedTicks(delayedTicks);
		setDelayChainChest(delayChainChest);
		setDelayedChainTicks(delayedChainTicks);
		setChestsPerTick(chestsPerTick);
		
		setVoidChestRun(voidChestRun);
		setVoidChestsPerTick(voidChestsPerTick);
		
		setStorageChestAmountWhereShowParticles(storageChestAmountWhereShowParticels);
	}
	
	public static void init(AdvancedStoreHouse plugin)
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername", "hub");
		boolean mysql = plugin.getYamlHandler().getConfig().getBoolean("Mysql.Status", false);
		boolean lwc = plugin.existHook("LWC");
		boolean automaticDistribution = plugin.getYamlHandler().getConfig().getBoolean("IsAutomaticDistribution", false);
		List<String> serverRestartTime = plugin.getYamlHandler().getConfig().getStringList("ServerRestartTimes");
		int minimumTickPerDistributionChest = plugin.getYamlHandler().getConfig().getInt("MinimumTickPerDistributionChest", 10);
		long directPauseValue = plugin.getYamlHandler().getConfig().getLong("DirectPauseValue", 20*60*10);
		long indirectPauseValue = plugin.getYamlHandler().getConfig().getLong("IndirectPauseValue", 20*60*10);
		
		int waitBeforStartFactor = plugin.getYamlHandler().getConfig().getInt("WaitBeforeStartFactor", 100);
		int delayedTicks = plugin.getYamlHandler().getConfig().getInt("DelayedTicks", 2);
		int delayChainChest = plugin.getYamlHandler().getConfig().getInt("DelayChainChests", 10);
		int delayedChainTicks = plugin.getYamlHandler().getConfig().getInt("DelayedChainTicks", 10);
		int chestsPerTick = plugin.getYamlHandler().getConfig().getInt("ChestsPerTick", 5);
		
		int voidChestRun = plugin.getYamlHandler().getConfig().getInt("VoidChestRun", 300);
		int voidChestsPerTick = plugin.getYamlHandler().getConfig().getInt("VoidChestsPerTick", 10);
		
		int storageChestAmountWhereShowParticels = plugin.getYamlHandler().getConfig().getInt("StorageChestAmountWhereShowParticles", 10);
		settings = new PluginSettings(server, mysql, lwc,
				automaticDistribution, serverRestartTime, minimumTickPerDistributionChest, directPauseValue, indirectPauseValue,
				waitBeforStartFactor, delayedTicks, delayChainChest, delayedChainTicks, chestsPerTick,
				voidChestRun, voidChestsPerTick,
				storageChestAmountWhereShowParticels);
	}

	public boolean isMysql()
	{
		return mysql;
	}

	public void setMysql(boolean mysql)
	{
		this.mysql = mysql;
	}

	public LinkedHashMap<String, String> getCommands()
	{
		return commands;
	}

	public void setCommands(LinkedHashMap<String, String> commands)
	{
		this.commands = commands;
	}

	public int getDelayedTicks()
	{
		return delayedTicks;
	}

	public void setDelayedTicks(int delayedTicks)
	{
		this.delayedTicks = delayedTicks;
	}

	public int getDelayChainChest()
	{
		return delayChainChest;
	}

	public void setDelayChainChest(int delayChainChest)
	{
		this.delayChainChest = delayChainChest;
	}

	public int getDelayedChainTicks()
	{
		return delayedChainTicks;
	}

	public void setDelayedChainTicks(int delayedChainTicks)
	{
		this.delayedChainTicks = delayedChainTicks;
	}

	public int getChestsPerTick()
	{
		return chestsPerTick;
	}

	public void setChestsPerTick(int chestsPerTick)
	{
		this.chestsPerTick = chestsPerTick;
	}

	public boolean isAutomaticDistribution()
	{
		return automaticDistribution;
	}

	public void setAutomaticDistribution(boolean automaticDistribution)
	{
		this.automaticDistribution = automaticDistribution;
	}

	public List<String> getServerRestartTime()
	{
		return serverRestartTime;
	}

	public void setServerRestartTime(List<String> serverRestartTime)
	{
		this.serverRestartTime = serverRestartTime;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public int getMinimumTickPerDistributionChest()
	{
		return minimumTickPerDistributionChest;
	}

	public void setMinimumTickPerDistributionChest(int minimumTickPerDistributionChest)
	{
		this.minimumTickPerDistributionChest = minimumTickPerDistributionChest;
	}

	public long getDirectPauseValue()
	{
		return directPauseValue;
	}

	public void setDirectPauseValue(long directPauseValue)
	{
		this.directPauseValue = directPauseValue;
	}

	public long getIndirectPauseValue()
	{
		return indirectPauseValue;
	}

	public void setIndirectPauseValue(long indirectPauseValue)
	{
		this.indirectPauseValue = indirectPauseValue;
	}

	public int getStorageChestAmountWhereShowParticles()
	{
		return storageChestAmountWhereShowParticels;
	}

	public void setStorageChestAmountWhereShowParticles(int storageChestAmountWhereShowParticels)
	{
		this.storageChestAmountWhereShowParticels = storageChestAmountWhereShowParticels;
	}

	public boolean isLwc()
	{
		return lwc;
	}

	public void setLwc(boolean lwc)
	{
		this.lwc = lwc;
	}

	public int getVoidChestRun()
	{
		return voidChestRun;
	}

	public void setVoidChestRun(int voidChestRun)
	{
		this.voidChestRun = voidChestRun;
	}

	public int getVoidChestsPerTick()
	{
		return voidChestsPerTick;
	}

	public void setVoidChestsPerTick(int voidChestsPerTick)
	{
		this.voidChestsPerTick = voidChestsPerTick;
	}

	public int getWaitBeforStartFactor()
	{
		return waitBeforStartFactor;
	}

	public void setWaitBeforStartFactor(int waitBeforStartFactor)
	{
		this.waitBeforStartFactor = waitBeforStartFactor;
	}

}
