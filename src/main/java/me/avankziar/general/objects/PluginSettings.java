package main.java.me.avankziar.general.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class PluginSettings
{
	private String server;
	private boolean mysql;
	
	private boolean automaticDistribution;
	private List<String> serverRestartTime = new ArrayList<>();
	private int minimumTickPerDistributionChest;
	private long directPauseValue;
	private long indirectPauseValue;
	
	private int delayedTicks;
	private int delayChainChest;
	private int delayedChainTicks;
	private int chestsPerTick;
	
	private int storageChestAmountWhereShowParticels;
	
	private LinkedHashMap<String, String> commands = new LinkedHashMap<>(); //To save commandstrings
	
	public static PluginSettings settings;
	
	public PluginSettings(String server, boolean mysql, 
			boolean automaticDistribution, List<String> serverRestartTime, int minimumTickPerDistributionChest,
			long directPauseValue, long indirectPauseValue,
			int delayedTicks, int delayChainChest, int delayedChainTicks, int chestsPerTick,
			int storageChestAmountWhereShowParticels)
	{
		setServer(server);
		setMysql(mysql);
		
		setAutomaticDistribution(automaticDistribution);
		setServerRestartTime(serverRestartTime);
		setMinimumTickPerDistributionChest(minimumTickPerDistributionChest);
		setDirectPauseValue(indirectPauseValue);
		setIndirectPauseValue(indirectPauseValue);
		
		setDelayedTicks(delayedTicks);
		setDelayChainChest(delayChainChest);
		setDelayedTicks(delayedChainTicks);
		setChestsPerTick(chestsPerTick);
		
		setStorageChestAmountWhereShowParticels(storageChestAmountWhereShowParticels);
	}
	
	public static void init(AdvancedStoreHouse plugin)
	{
		String server = plugin.getYamlHandler().get().getString("Servername", "hub");
		boolean mysql = plugin.getYamlHandler().get().getBoolean("Mysql.Status", false);
		
		boolean automaticDistribution = plugin.getYamlHandler().get().getBoolean("IsAutomaticDistribution", false);
		List<String> serverRestartTime = plugin.getYamlHandler().get().getStringList("ServerRestartTimes");
		int minimumTickPerDistributionChest = plugin.getYamlHandler().get().getInt("MinimumTickPerDistributionChest", 10);
		long directPauseValue = plugin.getYamlHandler().get().getLong("DirectPauseValue", 20*60*10);
		long indirectPauseValue = plugin.getYamlHandler().get().getLong("IndirectPauseValue", 20*60*10);
		
		int delayedTicks = plugin.getYamlHandler().get().getInt("DelayedTicks", 1);
		int delayChainChest = plugin.getYamlHandler().get().getInt("DelayChainChests", 10);
		int delayedChainTicks = plugin.getYamlHandler().get().getInt("DelayedChainTicks", 10);
		int chestsPerTick = plugin.getYamlHandler().get().getInt("ChestsPerTick", 10);
		
		int storageChestAmountWhereShowParticels = plugin.getYamlHandler().get().getInt("StorageChestAmountWhereShowParticels", 10);
		settings = new PluginSettings(server, mysql,
				automaticDistribution, serverRestartTime, minimumTickPerDistributionChest, directPauseValue, indirectPauseValue,
				delayedTicks, delayChainChest, delayedChainTicks, chestsPerTick,
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

	public int getStorageChestAmountWhereShowParticels()
	{
		return storageChestAmountWhereShowParticels;
	}

	public void setStorageChestAmountWhereShowParticels(int storageChestAmountWhereShowParticels)
	{
		this.storageChestAmountWhereShowParticels = storageChestAmountWhereShowParticels;
	}

}
