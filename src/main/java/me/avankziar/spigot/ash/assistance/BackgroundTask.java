package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.ItemDistributeObject;
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
		if(plugin.getYamlHandler().get().getBoolean("RunTransferSchedularTimer", false))
		{
			runTaskAutoDistribution();
		}
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
		if(plugin.getYamlHandler().get().getStringList("ServerRestartTimes") != null)
		{
			for(String tl : plugin.getYamlHandler().get().getStringList("ServerRestartTimes"))
			{
				serverRestart.add(LocalTime.parse((CharSequence)tl,
						DateTimeFormatter.ofPattern("~HH:mm")));
			}
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
		boolean b = true;
		if(b)
		{
			String server = plugin.getYamlHandler().get().getString("Servername", "");
			long minTickPerDc = plugin.getYamlHandler().get().getInt("MinimumTickPerDistributionChest", 10);
			
			long directTickPause = plugin.getYamlHandler().get().getInt("DirectPauseValue", 20*60*10);
			long indirectTickPause = plugin.getYamlHandler().get().getInt("IndirectPauseValue", 20*60*10);
			
			int scamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, "`server` = ?", server);
			BackgroundTask.schedularTotalTicks = ((scamount + directTickPause));
			runPreDistribution(server, minTickPerDc, directTickPause, indirectTickPause);
		} else
		{
			runTask();
		}
	}
	
	private void runPreDistribution(final String server, final long minTickPerDc, final long directTickPause, final long indirectTickPause)
	{
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				long before = System.currentTimeMillis()-BackgroundTask.lastCyclusTime;
				if(before >= 0)
				{
					int scamount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST, "`server` = ?", server);
					BackgroundTask.schedularTotalTicks = scamount + directTickPause;
					BackgroundTask.lastCyclusTime = System.currentTimeMillis()+BackgroundTask.schedularTotalTicks*20;
					
					long now = System.currentTimeMillis();
					final long erg = (lastCyclusTime - now);
					plugin.getLogger().info("==> Neuer Zyklus der Verteilung beginnt! Milli: "+now);
					plugin.getLogger().info("=> Tatsächliche Zeit zwischen Zyklus: "+erg);
					plugin.getLogger().info("=> Errechnete Zeit zwischen Zyklus: "+BackgroundTask.schedularTotalTicks*20);
					int dcamount = 1 + plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `automaticdistribution` = ?", server, true);
					
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
										"`id`", false, "`automaticdistribution` = ? AND `server` = ?", true, server));
					} catch (IOException e) {}
					long schedularTicksPerDc = ((scamount + indirectTickPause) / dcamount) + minTickPerDc;
					debug("=> dclist: "+dcList.size()+" | scamount: "+scamount+" | indirectTickPause: "+indirectTickPause
							+" | dcamount:"+dcamount+" | minTickPerDc: "+minTickPerDc);
					BackgroundTask.schedularTicksPerDc = (schedularTicksPerDc*10)/25;
					runDistribution(server, dcList);
				}
			}
		}.runTaskTimer(plugin, 0L, 20L*5);
	}
	
	private void runDistribution(final String server, final ArrayList<DistributionChest> dcList)
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
				ArrayList<StorageChest> prioList = new ArrayList<>();
				try
				{
					prioList = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				ArrayList<StorageChest> endList = new ArrayList<>();
				try
				{
					endList = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
				} catch (IOException e)
				{
					e.printStackTrace();
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
				int storagechestamount = prioList.size()+endList.size();
				ChestHandler.setDistributionChestOnCooldown(plugin, dc, storagechestamount, false);
				ItemStack[] cloneInvLc = null;
				ItemStack[] cloneInvRc = null;
				if(inventoryc instanceof DoubleChestInventory)
				{
					debug("AutoDistribution dci == true");
					DoubleChestInventory dcinv = (DoubleChestInventory) inventoryc;
					cloneInvLc = dcinv.getLeftSide().getContents();
					cloneInvRc = dcinv.getRightSide().getContents();
				} else
				{
					//debug("AutoDistribution  dci == false");
					cloneInvLc = inventoryc.getContents();
					cloneInvRc = cloneInvLc;
					int j = 0;
					for(int i = 0; i < cloneInvLc.length; i++)
					{
						cloneInvRc[i] = null;
						j = i;
					}
					debug("AutoDistribution  Right side set all null | i = "+j);
				}
				
				ItemDistributeObject idoc = new ItemDistributeObject(null, null);
				if(dc.isDistributeRandom())
				{
					int[] excludes = new int[0];
					ArrayList<StorageChest> clonePrioList = new ArrayList<>();
					for(int i = 0; i < prioList.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, prioList.size()-1, excludes);
						clonePrioList.add(prioList.get(n));
					}
					prioList = clonePrioList;
					int[] excludesEnd = new int[0];
					ArrayList<StorageChest> cloneEndList = new ArrayList<>();
					for(int i = 0; i < endList.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, endList.size()-1, excludesEnd);
						cloneEndList.add(endList.get(n));
					}
					endList = cloneEndList;
				}
				idoc._chestDistribute(plugin, null, inventoryc, prioList, endList, cloneInvLc, cloneInvRc, dc.isDistributeRandom());
				i++;
				if(i < dcList.size())
				{
					BackgroundTask.nextDcId = dcList.get(i).getId();
				}
			}
		}.runTaskTimer(plugin, 0L, BackgroundTask.schedularTicksPerDc);
	}
	
	private void runTask()
	{
		int schedular = plugin.getYamlHandler().get().getInt("TransferSchedularTimer", 15);
		String server = plugin.getYamlHandler().get().getString("Servername", "");
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				ArrayList<DistributionChest> dcList = new ArrayList<>();
				try
				{
					dcList = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
									"`id`", false, "`automaticdistribution` = ?", true));
				} catch (IOException e) {}
				for(DistributionChest dc : dcList)
				{
					if(Bukkit.getWorld(dc.getWorld()) == null)
					{
						continue;
					}
					Location dcloc = new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
					Block dcblock = dcloc.getBlock();
					if(dcblock == null)
					{
						continue;
					}
					if(dcblock.getState() == null)
					{
						continue;
					}
					if(!(dcblock.getState() instanceof Chest)
							&& !(dcblock.getState() instanceof ShulkerBox)
							&& !(dcblock.getState() instanceof Barrel)
							&& !(dcblock.getState() instanceof Furnace)
							&& !(dcblock.getState() instanceof BlastFurnace)
							&& !(dcblock.getState() instanceof Smoker)
							&& !(dcblock.getState() instanceof Hopper)
							&& !(dcblock.getState() instanceof Dropper)
							&& !(dcblock.getState() instanceof Dispenser)
							&& !(dcblock.getState() instanceof BrewingStand)
							)
					{
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", dc.getId());
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.STORAGECHEST, "`distributionchestid` = ?", dc.getId());
						continue;
					}
					int last = plugin.getMysqlHandler().lastID(MysqlHandler.Type.STORAGECHEST);
					ArrayList<StorageChest> prioList = new ArrayList<>();
					ArrayList<StorageChest> endList = new ArrayList<>();
					try
					{
						prioList = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(
										MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), false, server));
						endList = ConvertHandler.convertListIII(
								plugin.getMysqlHandler().getList(
										MysqlHandler.Type.STORAGECHEST, "`priority`", dc.isNormalPriority(), 0, last,
										"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
					} catch (IOException e) {}
					
					//Verteilung Beginn
					if(!(dcblock.getState() instanceof Container))
					{
						continue;
					}
					Container dccontainer = (Container) dcblock.getState();
					Inventory inventory = dccontainer.getInventory();
					ItemStack[] cloneInvL = null;
					ItemStack[] cloneInvR = null;
					if(inventory instanceof DoubleChestInventory)
					{
						DoubleChestInventory dcinv = (DoubleChestInventory) inventory;
						cloneInvL = dcinv.getLeftSide().getContents();
						cloneInvR = dcinv.getRightSide().getContents();
					} else
					{
						cloneInvL = inventory.getContents();
						cloneInvR = inventory.getContents();
						for(int i = 0; i < cloneInvR.length; i++)
						{
							cloneInvR[i] = null;
						}
					}
					
					//Normal Lager
					for(StorageChest sc : prioList)
					{
						if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
						{
							break;
						}
						Block block = new Location(Bukkit.getWorld(sc.getWorld()),
								sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
						if(block == null)
						{
							continue;
						}
						if(block.getState() == null)
						{
							continue;
						}
						if(!(block.getState() instanceof Container))
						{
							continue;
						}
						Container container = (Container) block.getState();
						Inventory cinv = container.getInventory();
						if(cinv == null)
						{
							continue;
						}
						cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, false);
						cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, false);
					}
					//Endlager
					if(!ChestHandler.isContentEmpty(cloneInvL) || !ChestHandler.isContentEmpty(cloneInvR))
					{
						for(StorageChest sc : endList)
						{
							if(ChestHandler.isContentEmpty(cloneInvL) && ChestHandler.isContentEmpty(cloneInvR))
							{
								break;
							}
							Block block = new Location(Bukkit.getWorld(sc.getWorld()),
									sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()).getBlock();
							if(block == null)
							{
								continue;
							}
							if(block.getState() == null)
							{
								continue;
							}
							if(!(block.getState() instanceof Container))
							{
								continue;
							}
							Container container = (Container) block.getState();
							Inventory cinv = container.getInventory();
							if(cinv == null)
							{
								continue;
							}
							cloneInvL = ChestHandler.distribute(cinv, sc.getContents(), cloneInvL, inventory, true);
							cloneInvR = ChestHandler.distribute(cinv, sc.getContents(), cloneInvR, inventory, true);
						}
					}
					if(inventory instanceof DoubleChestInventory)
					{
						DoubleChestInventory dcinv = (DoubleChestInventory) inventory;
						dcinv.getLeftSide().setContents(cloneInvL);
						dcinv.getRightSide().setContents(cloneInvR);
					} else
					{
						inventory.setContents(cloneInvL);
					}
				}
			}
		}.runTaskTimer(plugin, 20L*60, 20L*60*schedular);
	}
}
