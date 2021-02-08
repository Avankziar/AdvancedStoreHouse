package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.DistributionHandler;
import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class BackgroundTask 
{
	private AdvancedStoreHouse plugin;
	public static long schedularTotalTicks = 15000;
	public static long nextDcId = 0;
	public static long schedularTicksPerDc = 1;
	private static LocalDateTime nextRestart = null;
	public static long lastCyclusTime = System.currentTimeMillis();
	public static long lastDcTime = System.currentTimeMillis();
	
	public BackgroundTask(AdvancedStoreHouse plugin) 
	{
		this.plugin = plugin;
		initTimes();
		if(PluginSettings.settings.isAutomaticDistribution())
		{
			runTaskAutoDistribution();
		}
		runTaskPreVoid();
	}
	
	private static void debug(String s)
	{
		boolean bo = false;
		if(bo)
		{
			AdvancedStoreHouse.log.info(s);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}			
		}
	}
	
	private void initTimes()
	{
		ArrayList<LocalTime> serverRestart = new ArrayList<>();
		for(String tl : PluginSettings.settings.getServerRestartTime())
		{
			serverRestart.add(LocalTime.parse((CharSequence)tl,
					DateTimeFormatter.ofPattern("~HH:mm")));
		}
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		ArrayList<LocalDateTime> serverRestarts = new ArrayList<>();
		for(LocalTime lt : serverRestart)
		{
			serverRestarts.add(lt.atDate(today));
		}
		for(LocalTime lt : serverRestart)
		{
			serverRestarts.add(lt.atDate(tomorrow));
		}
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime than = now.plusDays(5);
		for(LocalDateTime lt : serverRestarts)
		{
			if(lt.isAfter(now) && lt.isBefore(than))
			{
				than = lt;
			}
		}
		nextRestart = than;
	}
	
	private void runTaskAutoDistribution()
	{
		long minTickPerDc = PluginSettings.settings.getMinimumTickPerDistributionChest();
		
		long directTickPause = PluginSettings.settings.getDirectPauseValue();
		long indirectTickPause = PluginSettings.settings.getIndirectPauseValue();
		
		int scamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, "`server` = ?",
				PluginSettings.settings.getServer());
		BackgroundTask.schedularTotalTicks = ((scamount + directTickPause));
		runPreDistribution(minTickPerDc, directTickPause, indirectTickPause);
	}
	
	private void runPreDistribution(final long minTickPerDc, final long directTickPause, final long indirectTickPause)
	{
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				long before = System.currentTimeMillis()-BackgroundTask.lastCyclusTime;
				if(before >= 0)
				{
					int scamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, "`server` = ?",
							PluginSettings.settings.getServer());
					BackgroundTask.schedularTotalTicks = scamount + directTickPause;
					BackgroundTask.lastCyclusTime = System.currentTimeMillis()+BackgroundTask.schedularTotalTicks*20;
					
					//long now = System.currentTimeMillis();
					//final long erg = (lastCyclusTime - now);
					//plugin.getLogger().info("==> Neuer Zyklus der Verteilung beginnt! Milli: "+now);
					//plugin.getLogger().info("=> Tatsächliche Zeit zwischen Zyklus: "+erg);
					//plugin.getLogger().info("=> Errechnete Zeit zwischen Zyklus: "+BackgroundTask.schedularTotalTicks*20);
					int dcamount = 1 + plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `automaticdistribution` = ?", PluginSettings.settings.getServer(), true);
					
					long than = System.currentTimeMillis() + BackgroundTask.schedularTotalTicks * 20;
					plugin.getLogger().info("=> Nächster Zyklus: "+TimeHandler.getTime(than));
					long nextRestart = TimeHandler.getTime(BackgroundTask.nextRestart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")));
					
					debug("Nächster Restart: " + BackgroundTask.nextRestart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")));
					if(than >= nextRestart)
					{
						debug("=> Neuer Zyklus kann nicht gestartet werden, da der Serverrestart bevorsteht!");
						cancel();
						return;
					}
					
					ArrayList<DistributionChest> dcList = new ArrayList<>();
					try
					{
						dcList = ConvertHandler.convertListII(
								plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
										"`id`", false, "`automaticdistribution` = ? AND `server` = ?", true, PluginSettings.settings.getServer()));
					} catch (IOException e) {}
					long schedularTicksPerDc = ((scamount + indirectTickPause) / dcamount) + minTickPerDc;
					debug("=> dclist: "+dcList.size()+" | scamount: "+scamount+" | indirectTickPause: "+indirectTickPause
							+" | dcamount:"+dcamount+" | minTickPerDc: "+minTickPerDc);
					BackgroundTask.schedularTicksPerDc = (schedularTicksPerDc*10)/25;
					runDistribution(dcList);
				}
			}
		}.runTaskTimer(plugin, 0L, 20L*5);
	}
	
	private void runDistribution(final ArrayList<DistributionChest> dcList)
	{
		new BukkitRunnable()
		{
			int i = 0;
			@Override
			public void run()
			{
				long now = System.currentTimeMillis();
				final long erg = (now - lastDcTime);
				lastDcTime = now;
				debug("=> Listenplatz: "+i+" | Milli: "+now);
				debug("=> Tatsächliche Zeit zwischen Dcs: "+erg);
				debug("=> Errechnete Zeit zwischen Dcs: "+BackgroundTask.schedularTicksPerDc*20*25/10);
				if(i >= dcList.size())
				{
					debug("AutoDistribution: List is finished!");
					cancel();
					return;
				}
				DistributionChest dc = dcList.get(i);
				if(dc == null)
				{
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				debug("=> Verteilung der Kiste %id% beginnt!"
						.replace("%id%", ""+dc.getId()));
				debug("AutoDistribution Dc: "+dc.getChestName());
				if(ChestHandler.isDistributionChestOnCooldown(plugin, dc))
				{
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				World world = Bukkit.getWorld(dc.getWorld());
				if(world == null)
				{
					debug("AutoDistribution Dc world == null");
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				Block dcblock = new Location(world, dc.getBlockX(), dc.getBlockY(), dc.getBlockZ()).getBlock();
				if(dcblock == null)
				{
					debug("AutoDistribution block == null");
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				if(dcblock.getState() == null)
				{
					debug("AutoDistribution Dc block.getstate == null");
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug("AutoDistribution Dc block.getstate not a container");
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug("AutoDistribution Dc container inv == null");
					i++;
					if(i < dcList.size())
					{
						BackgroundTask.nextDcId = dcList.get(i).getId();
					}
					return;
				}
				try
				{
					DistributionHandler.distributeStartVersionAutomatic(PluginSettings.settings.getServer(), dc, inventoryc);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				i++;
				if(i < dcList.size())
				{
					BackgroundTask.nextDcId = dcList.get(i).getId();
				}
			}
		}.runTaskTimer(plugin, 0L, BackgroundTask.schedularTicksPerDc);
	}
	
	public void runTaskPreVoid()
	{
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				ArrayList<StorageChest> voidchest = null;
				try
				{
					voidchest = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST,
							"`id`", false, "`server` = ? AND `optionvoid` = ?", PluginSettings.settings.getServer(), true));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if(voidchest == null)
				{
					return;
				}
				runTaskVoid(voidchest);
			}
		}.runTaskTimer(plugin, 0L, PluginSettings.settings.getVoidChestRun()*20L);
	}
	
	public void runTaskVoid(final ArrayList<StorageChest> voidchest)
	{
		new BukkitRunnable()
		{
			final int chestPerTick = PluginSettings.settings.getVoidChestsPerTick()*2;
			int lastindex = 0;
			@Override
			public void run()
			{
				for(int i = 0; i < chestPerTick; i++)
				{
					int ii = i+lastindex;
					if(ii >= voidchest.size())
					{
						cancel();
						return;
					}
					StorageChest sc = voidchest.get(ii);
					Block b = new Location(Bukkit.getWorld(sc.getWorld()), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
					if(b == null || b.getState() == null || !(b.getState() instanceof Container))
					{
						continue;
					}
					Inventory inv = ((Container)b.getState()).getInventory();
					if(inv == null)
					{
						continue;
					}
					inv.clear();
				}
				lastindex += 10;
			}
		}.runTaskTimer(plugin, 0L, 2L);
	}
}
