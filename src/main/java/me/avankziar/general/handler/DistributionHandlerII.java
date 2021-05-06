package main.java.me.avankziar.general.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.general.objects.ItemDistributeObject;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.general.objects.StorageChest.Type;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class DistributionHandlerII
{
	public static void debug(int lvl, String s)
	{
		int level = 3;
		boolean bo = false;
		if(bo && lvl >= level)
		{
			AdvancedStoreHouse.log.info(s);
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}			
		}
	}
	
	public static void distributeStartVersionPhysical(String server, Location loc, Inventory inv) throws IOException
	{
		if(!AdvancedStoreHouse.getPlugin().getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			debug(0, "distribution not found");
			if(inv instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcInv = (DoubleChestInventory) inv;
				debug(0, "distribution == DoubleChestInv");
				loc = ChestHandler.isDoubleChest(AdvancedStoreHouse.getPlugin(), server, loc, dcInv);
				if(loc == null)
				{
					debug(0, "Distributionchest dont exist: ");
					return;
				}
			} else
			{
				debug(0, "Distributionchest dont exist: "
						+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
				return;
			}
		}
		debug(3, "Distrute VersionPhysical start");
		DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(server, dc, inv, true, "Normal ");
	}
	
	public static void distributeStartVersionButton(String server, Location loc, Inventory inv, DistributionChest dc) throws IOException
	{
		if(dc == null)
		{
			debug(0, "Distrute VersionButton dc == null");
			return;
		}
		debug(3, "Distrute VersionButton start");
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(server, dc, inv, true, "Normal ");
	}
	
	public static void distributeStartVersionRemoteTriggering(DistributionChest dc) throws IOException
	{
		Block dcblock = new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ()).getBlock();
		if(dcblock == null)
		{
			return;
		}
		if(dcblock.getState() == null)
		{
			return;
		}
		if(!(dcblock.getState() instanceof Container))
		{
			return;
		}
		Inventory inv = ((Container)dcblock.getState()).getInventory();
		if(inv == null)
		{
			return;
		}
		debug(3, "Distrute VersionRemoteTriggering start");
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(dc.getServer(), dc, inv, true, "Normal ");
	}
	
	public static void distributeStartVersionAutomatic(String server, DistributionChest dc, Inventory inv) throws IOException
	{
		debug(3, "Distrute VersionAutomatic start");
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(server, dc, inv, true, "Normal ");
	}
	
	private static void distributeMiddle(String server, DistributionChest dc, Inventory inv, boolean activateChain, String debug) throws IOException
	{
		debug(3, "DistruteMiddle start");
		if(ChestHandler.isContentEmpty(inv.getContents()))
		{
			debug(2, "Normal Dc Content is Empty | Normal End");
			return;
		}
		final ArrayList<StorageChest> endList = ConvertHandler.convertListIII(
				AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
						!dc.isNormalPriority(),
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
		LinkedHashMap<String, LinkedHashMap<Integer, ItemStack>> map = new LinkedHashMap<>();
		ChestHandler.setDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc, 100, inv.getLocation());
		int count = 0;
		int i = 0;
		if(inv instanceof DoubleChestInventory)
		{
			DoubleChestInventory dinv = (DoubleChestInventory) inv;
			while(i < dinv.getContents().length)
			{
				ItemStack is = dinv.getItem(i);
				if(is == null || is.getType() == Material.AIR)
				{
					i++;
					continue;
				}
				String data = ChestHandler.getGroundSpecs(is);
				if(map.containsKey(data))
				{
					LinkedHashMap<Integer, ItemStack> submap = map.get(data);
					submap.put(i, is);
					map.replace(data, submap);
				} else
				{
					LinkedHashMap<Integer, ItemStack> submap = new LinkedHashMap<>();
					submap.put(i, is);
					map.put(data, submap);
					count++;
				}
				i++;
			}
		} else
		{
			while(i < inv.getContents().length)
			{
				ItemStack is = inv.getItem(i);
				if(is == null || is.getType() == Material.AIR)
				{
					i++;
					continue;
				}
				String data = ChestHandler.getGroundSpecs(is);
				if(map.containsKey(data))
				{
					LinkedHashMap<Integer, ItemStack> submap = map.get(data);
					submap.put(i, is);
					map.replace(data, submap);
				} else
				{
					LinkedHashMap<Integer, ItemStack> submap = new LinkedHashMap<>();
					submap.put(i, is);
					map.put(data, submap);
					count++;
				}
				i++;
			}
		}
		debug(2, "Counted "+count+" different ItemStacks");
		int lastScChestAmount = 0;
		for(String data : map.keySet())
		{
			debug(2, "Data: "+data);
			ArrayList<StorageChest> prioList = new ArrayList<>();
			if(dc.getPriorityType() == PriorityType.SWITCH)
			{
				prioList = ConvertHandler.convertListIII(
						AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
								!dc.isNormalPriority(),
								"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ? AND `searchcontent` LIKE ?",
								dc.getId(), false, server, "%"+data+"%"));
			} else
			{
				prioList = ConvertHandler.convertListIII(
						AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
								!dc.isNormalPriority(),
								"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ? AND `priority` = ? AND `searchcontent` LIKE ?",
								dc.getId(), false, server, dc.getPriorityNumber(), "%"+data+"%"));
			}
			debug(3, "Amount PrioList: "+prioList.size()+" | Amount Endlist: "+endList.size());
			int actuallyScAmount = prioList.size()+endList.size();
			ChestHandler.setDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc, actuallyScAmount, inv.getLocation());
			distributionPost(server, dc, inv, map.get(data), prioList, endList, actuallyScAmount, lastScChestAmount, activateChain,
					debug);
			lastScChestAmount += actuallyScAmount;
		}
	}
	
	private static void distributionPost(String server, DistributionChest dc, Inventory inv,
			final LinkedHashMap<Integer, ItemStack> map,
			final ArrayList<StorageChest> prioListPre, final ArrayList<StorageChest> endListPre, int storagechestamount, int waitextra,
			boolean activateChain, String debug)
	{
		int wait = (int)((double)((double)storagechestamount+(double)waitextra)/(double)PluginSettings.settings.getWaitBeforStartFactor());
		debug(3, "DistributionPost start | Wait: "+wait);
		new BukkitRunnable()
		{
			ArrayList<StorageChest> prioList = prioListPre;
			ArrayList<StorageChest> endList = endListPre;
			@Override
			public void run()
			{
				ItemDistributeObject ido = new ItemDistributeObject(null, null);
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
				ido.itemDistribute(AdvancedStoreHouse.getPlugin(),
						inv, map, prioList, endList, server, dc.isDistributeRandom(), debug);
				
				if(activateChain)
				{
					//here Chainstart
					float supposeCooldown = ((float)storagechestamount+(float)waitextra)/ (float)PluginSettings.settings.getChestsPerTick()
							+(float)PluginSettings.settings.getDelayChainChest() *1000.0F / 50.0F;
					try
					{
						debug(0, "SupposeCooldown: "+storagechestamount+"/"+PluginSettings.settings.getChestsPerTick()+"+"+
						+PluginSettings.settings.getDelayChainChest()+"*"+1000+"/"+50);
						debug(3, "SupposeCooldown: "+(long)supposeCooldown);
						distributeChain(server, (long)supposeCooldown, prioList, endList);
					} catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}.runTaskLater(AdvancedStoreHouse.getPlugin(), 1L*wait);
	}
	
	public static void distributeChain(String server, long supposeCooldown,
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList) throws IOException
	{
		debug(3, "ChainDc distribution starts");
		final ArrayList<DistributionChest> chain = ChestHandler.getChainChest(AdvancedStoreHouse.getPlugin(), prioList, endList, server);
		debug(0, "ChainDc.size: "+chain.size());
		if(chain.size() == 0)
		{
			debug(0, "ChainDc distribution: chain.size == 0 || chain End");
			return;
		}
		new BukkitRunnable()
		{
			int i = 0;
			@Override
			public void run()
			{
				if(i >= chain.size())
				{
					debug(2, "ChainDc distribution: i >= chain.size || chain End");
					cancel();
					return;
				}
				DistributionChest dcc = chain.get(i);
				debug(2, "ChainDc: "+dcc.getChestName());
				if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dcc))
				{
					debug(2, "ChainDc Dcc is already in distribution");
					i++;
					return;
				}
				World world = Bukkit.getWorld(dcc.getWorld());
				if(world == null)
				{
					debug(2, "ChainDc world == null");
					i++;
					return;
				}
				Block dcblock = new Location(world, dcc.getBlockX(), dcc.getBlockY(), dcc.getBlockZ()).getBlock();
				if(dcblock == null)
				{
					debug(2, "ChainDc block == null");
					i++;
					return;
				}
				if(dcblock.getState() == null)
				{
					debug(2, "ChainDc block.getstate == null");
					i++;
					return;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(2, "ChainDc block.getstate not a container");
					i++;
					return;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(2, "ChainDc container inv == null");
					i++;
					return;
				}
				try
				{
					distributeMiddle(server, dcc, inventoryc, false, "ChainDc ");
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				i++;
			}
		}.runTaskTimer(AdvancedStoreHouse.getPlugin(), supposeCooldown,
				1L*PluginSettings.settings.getDelayedChainTicks());
	}
	
	public static boolean distribute(Inventory sender, Inventory reciever, ItemStack[] filter,
			LinkedHashMap<Integer, ItemStack> map,
			boolean endstorage, boolean isRandom,
			boolean optionDurability, StorageChest.Type durabilityType, int durability, 
			boolean optionRepair, StorageChest.Type repairType, int repaircost,
			boolean optionEnchantments,
			boolean optionMaterial
			)
	{
		int base = map.size();
		int check = 0;
		for(Entry<Integer, ItemStack> set : map.entrySet())
		{
			final int slot = set.getKey();
			final ItemStack is = set.getValue();
			if(		is == null || is.getType() == Material.AIR
					|| sender.getItem(slot) == null || sender.getItem(slot).getType() == Material.AIR
					|| sender.getItem(slot).getType() != is.getType()
					|| sender.getItem(slot).getAmount() != is.getAmount())
			{
				//INFO:If the Item in this slot is already distribute, continue. If Hopper refill, and the type arent equal, continue. 
				//Hopper Ticks auf 38+ stellen.
				check++;
				continue;
			}
			if(isRandom)
			{
				Random r = new Random();
				int random = r.nextInt(100);
				if(random > 50)
				{
					HashMap<Integer, ItemStack> sended = reciever.addItem(sender.getItem(slot));
					if(sended.isEmpty())
					{
						sender.setItem(slot, null);
						check++;
					} else
					{
						sender.setItem(slot, sended.get(0));
					}
					check++;
				} else
				{
					//Do nothing?
				}
			} else
			{
				if(endstorage)
				{
					debug(2, "Endstorage is Sending");
					HashMap<Integer, ItemStack> sended = reciever.addItem(sender.getItem(slot));
					if(sended.isEmpty())
					{
						debug(2, "Endstorage sended all");
						sender.setItem(slot, null);
						check++;
					} else
					{
						debug(2, "Endstorage sended a part");
						sender.setItem(slot, sended.get(0));
					}
				} else
				{
					if(isSimilar(is, filter,
							optionDurability, durabilityType, durability,
							optionRepair, repairType, repaircost,
							optionEnchantments, optionMaterial))
					{
						HashMap<Integer, ItemStack> sended = reciever.addItem(sender.getItem(slot));
						if(sended.isEmpty())
						{
							sender.setItem(slot, null);
							check++;
						} else
						{
							sender.setItem(slot, sended.get(0));
						}
					} else
					{
						//Do nothing?
					}
				}
			}
		}
		return (base == check) ? true : false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack item, ItemStack[] filter, 
			boolean optionDurability, StorageChest.Type durabilityType, int durability, //In full percentage
			boolean optionRepair, StorageChest.Type repairType, int repaircost, //in level
			boolean optionEnchantments,
			boolean optionMaterial)
	{
		for(ItemStack is : filter)
		{
			if (is == null || item == null) 
	        {
				//debug(0, "i || o == null || i:"+(is == null)+" | o:"+(item == null));
	            continue;
	        }
	        final ItemStack i = item.clone();
	        final ItemStack f = is.clone();
	        
	        if(optionMaterial)
	        {
	        	//FIXME Flussbild noch korrigieren
	        	if(i.getType() == f.getType())
		        {
	        		debug(0, "OpMaterial >> i & f getType == || i:"+i.getType()+" | f:"+f.getType());
	        		return true;
		        } else
		        {
		        	debug(0, "OpMaterial >> i & f getType != || i:"+i.getType()+" | f:"+f.getType());
		        	continue;
		        }
	        }
	        if(i.getType() != f.getType())
	        {
	        	debug(0, "i & f getType != || i:"+i.getType()+" | f:"+f.getType());
	        	continue;
	        } else
	        {
	        	debug(0, "i & f getType == || i:"+i.getType()+" | f:"+f.getType());
	        }
	        if(i.hasItemMeta() == true && f.hasItemMeta() == true)
	        {
	        	debug(0, "i & f hasItemMeta == true");
	        	if(i.getItemMeta() != null && f.getItemMeta() != null)
	        	{
	        		debug(0, "i & f getItemMeta != null");
	        		if(i.getItemMeta() instanceof Damageable && f.getItemMeta() instanceof Damageable)
	        		{
	        			Damageable id = (Damageable) i.getItemMeta();
	        			if(optionDurability)
	        			{
	        				//(132-5/132)*100 =
	        				double maxdamage = ChestHandler.getMaxDamage(i.getType()); //132
	        				double damage = id.getDamage(); //64
	        				double dpercent = ((maxdamage-damage)/maxdamage)*100;
	        				int percent = (int) dpercent;
	        				debug(0, "similar Long is Option Durability");
	        				debug(0, "similar Long Op Dura| maxdamage:"+maxdamage+" | damage: "+damage);
	        				debug(0, "similar Long Op Dura| dpercent:"+dpercent+" | percent: "+percent);
	        				//the percentage muss be OVER the value, to be distributed!
	        				if(durabilityType == Type.LESSTHAN)
	        				{
	        					debug(0, "similar Long OpDur LESSTHAN; "+percent+" % < "+durability+" == "+(percent < durability));
	        					if(percent > durability)
		        				{
			        				continue;
		        				}
	        				} else
	        				{
	        					debug(0, "similar Long OpDur LARGERTHAN; "+percent+" % > "+durability+" == "+(percent > durability));
	        					if(percent < durability)
		        				{
			        				continue;
		        				}
	        				}
	        			}
	        			id.setDamage(0);
	        			i.setItemMeta((ItemMeta) id);
	        			Damageable od = (Damageable) f.getItemMeta();
	        			od.setDamage(0);
	        			f.setItemMeta((ItemMeta) od);
	        		}
		        	if(i.getItemMeta() instanceof Repairable && f.getItemMeta() instanceof Repairable)
	            	{
	            		Repairable ir = (Repairable) i.getItemMeta();
	            		if(optionRepair)
	            		{
	            			debug(0, "similar Long is Option Repair");
	            			final int rc = ir.getRepairCost()+1;
	            			if(repairType == Type.LESSTHAN)
	            			{
	            				debug(0, "similar Long OpRep LESSTHAN; "+rc+" lvl < "
	            						+repaircost+" == "+(rc < repaircost));
	            				if(rc > repaircost)
		            			{
		            				continue;
		            			}
	            			} else
	            			{
	            				debug(0, "similar Long OpRep LARGERTHAN; "+rc+" lvl > "
	            						+repaircost+" == "+(rc > repaircost));
	            				if(ir.getRepairCost() < repaircost)
		            			{
		            				continue;
		            			}
	            			}
	            		}
	            		ir.setRepairCost(0);
	            		i.setItemMeta((ItemMeta) ir);
	            		Repairable or = (Repairable) f.getItemMeta();
	            		or.setRepairCost(0);
	            		f.setItemMeta((ItemMeta) or);
	            	}
		        	if(i.getItemMeta() instanceof EnchantmentStorageMeta && f.getItemMeta() instanceof EnchantmentStorageMeta)
		        	{
		        		debug(0, "similar is EnchantmentStorageMeta");
		        		EnchantmentStorageMeta iesm = (EnchantmentStorageMeta) i.getItemMeta();
		        		EnchantmentStorageMeta fesm = (EnchantmentStorageMeta) f.getItemMeta();
		        		if(optionEnchantments)
		        		{
		        			debug(0, "similar is Option Enchantment");
		        			if(!iesm.hasStoredEnchants())
		        			{
		        				debug(0, "similar has no StoreEnchants");
		        				continue;
		        			} else
		        			{
		        				for(Enchantment e : ChestHandler.enchantments)
		        				{
		        					if(iesm.hasStoredEnchant(e))
		        					{
		        						debug(0, "similar OpEnch: StoredEnch."+e.getKey().getKey());
		        						iesm.removeStoredEnchant(e);
		        					}
		        					if(fesm.hasStoredEnchant(e))
		        					{
		        						fesm.removeStoredEnchant(e);
		        					}
		        				}
		        				i.setItemMeta(iesm);
				        		f.setItemMeta(fesm);
		        			}
		        		} else
		        		{
		        			debug(0, "similar no OpEnch");
		        			i.setItemMeta(ChestHandler.orderStorageEnchantments(iesm));
			        		f.setItemMeta(ChestHandler.orderStorageEnchantments(fesm));
		        		}
		        	}
		        	if(i.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK 
		        			&& f.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK
		        			)
		        	{
		        		debug(0, "similar is normal Enchantment");
		        		if(optionEnchantments)
		        		{
		        			ItemMeta im = i.getItemMeta();
		        			ItemMeta fm = f.getItemMeta();
		        			if(!im.hasEnchants())
		        			{
		        				continue;
		        			} else
		        			{
		        				for(Enchantment e : ChestHandler.enchantments)
		        				{
		        					if(im.hasEnchant(e))
		        					{
		        						im.removeEnchant(e);
		        					}
		        					if(fm.hasEnchant(e))
		        					{
		        						fm.removeEnchant(e);
		        					}
		        				}
		        				i.setItemMeta(im);
				        		f.setItemMeta(fm);
		        			}
		        		} else
		        		{
		        			i.setItemMeta(ChestHandler.orderEnchantments(i.getItemMeta()));
			        		f.setItemMeta(ChestHandler.orderEnchantments(f.getItemMeta()));
		        		}
		        	}
		        	i.setAmount(1);
		        	f.setAmount(1);
		        	if(i.getItemMeta().toString().equals(f.getItemMeta().toString()))
		        	{
		        		debug(0, "isSimliar : long");
		        		return true;
		        	}
	        	}
	        } else
	        {
	        	if(i.hasItemMeta() && !f.hasItemMeta())
	        	{
	        		if(i.getItemMeta() instanceof Damageable)
	        		{
	        			Damageable id = (Damageable) i.getItemMeta();
	        			if(optionDurability)
	        			{
	        				int percent = ChestHandler.getMaxDamage(i.getType())/(id.getDamage() == 0 ? 1 : id.getDamage());
	        				debug(0, "similar Middle is Option Durability");
	        				//the percentage muss be OVER the value, to be distributed!
	        				if(durabilityType == Type.LESSTHAN)
	        				{
	        					debug(0, "similar Middle OpDur LESSTHAN; "+percent+" % < "+durability+" == "+(percent < durability));
	        					if(percent > durability)
		        				{
			        				continue;
		        				}
	        				} else
	        				{
	        					debug(0, "similar Middle OpDur LARGERTHAN; "+percent+" % > "+durability+" == "+(percent > durability));
	        					if(percent < durability)
		        				{
			        				continue;
		        				}
	        				}
	        			}
	        			debug(0,"i.toString == f.toString : "+(i.toString().equals(f.toString())));
	        			debug(0,"im.toString : "+i.getItemMeta().toString());
	        			debug(0,"fm.toString : "+f.getItemMeta().toString());
	        			debug(0,"1. im.toString == fm.toString : "+(i.getItemMeta().toString().equals(f.getItemMeta().toString())));
	        			id.setDamage(0);
	        			i.setItemMeta((ItemMeta) id);
	        			debug(0,"im.toString : "+i.getItemMeta().toString());
	        			debug(0,"2. im.toString == fm.toString : "+(i.getItemMeta().toString().equals(f.getItemMeta().toString())));
	        		}        			
        			if(i.getItemMeta() instanceof Repairable)
	            	{
	            		Repairable ir = (Repairable) i.getItemMeta();
	            		if(optionRepair)
	            		{
	            			debug(0, "similar Middle is Option Repair");
	            			final int rc = ir.getRepairCost()+1;
	            			if(repairType == Type.LESSTHAN)
	            			{
	            				debug(0, "similar Middle OpRep LESSTHAN; "+rc+" lvl < "
	            						+repaircost+" == "+(rc < repaircost));
	            				if(rc > repaircost)
		            			{
		            				continue;
		            			}
	            			} else
	            			{
	            				debug(0, "similar Middle OpRep LARGERTHAN; "+rc+" lvl > "
	            						+repaircost+" == "+(rc > repaircost));
	            				if(ir.getRepairCost() < repaircost)
		            			{
		            				continue;
		            			}
	            			}
	            		}
	            		debug(0,"i.toString == f.toString : "+(i.toString().equals(f.toString())));
	        			debug(0,"im.toString : "+i.getItemMeta().toString());
	        			debug(0,"fm.toString : "+f.getItemMeta().toString());
	        			debug(0,"1. im.toString == fm.toString : "+(i.getItemMeta().toString().equals(f.getItemMeta().toString())));
	            		ir.setRepairCost(0);
	            		i.setItemMeta((ItemMeta) ir);
	            		debug(0,"im.toString : "+i.getItemMeta().toString());
	        			debug(0,"2. im.toString == fm.toString : "+(i.getItemMeta().toString().equals(f.getItemMeta().toString())));
	            	}
        			if(i.getItemMeta().toString().equals(f.getItemMeta().toString()))
		        	{
		        		debug(0, "isSimliar : middle");
		        		return true;
		        	}
	        	} else
	        	{
	        		i.setAmount(1);
		        	f.setAmount(1);
		        	i.setDurability((short) 0);
		        	f.setDurability((short) 0);
		        	if(i.toString().equals(f.toString()))
		        	{
		        		debug(0, "isSimliar : short");
		        		return true;
		        	}
	        	}
	        }
	        debug(0, "!isSimilar");
		}
		return false;
	}	
}