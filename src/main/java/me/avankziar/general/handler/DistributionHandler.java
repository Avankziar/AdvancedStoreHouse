package main.java.me.avankziar.general.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class DistributionHandler
{
	public static void debug(int lvl, String s)
	{
		int level = -1;
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
		debug(0, "Distrute VersionPhysical start");
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
		DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(server, dc, inv);
	}
	
	public static void distributeStartVersionButton(String server, Location loc, Inventory inv, DistributionChest dc) throws IOException
	{
		debug(0, "Distrute VersionButton start");
		if(dc == null)
		{
			debug(0, "Distrute VersionButton dc == null");
			return;
		}
		if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc))
		{
			debug(0, "Dc is already in distribution");
			return;
		}
		distributeMiddle(server, dc, inv);
	}
	
	public static void distributeStartVersionRemoteTriggering(DistributionChest dc) throws IOException
	{
		debug(0, "Distrute VersionRemoteTriggering start");
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
		distributeMiddle(dc.getServer(), dc, inv);
	}
	
	public static void distributeStartVersionAutomatic(String server, DistributionChest dc, Inventory inv) throws IOException
	{
		debug(0, "Distrute VersionAutomatic start");
		distributeMiddle(server, dc, inv);
	}
	
	private static void distributeMiddle(String server, DistributionChest dc, Inventory inv) throws IOException
	{
		debug(0, "DistruteMiddle start");
		ArrayList<StorageChest> prioList = new ArrayList<>();
		if(dc.getPriorityType() == PriorityType.SWITCH)
		{
			prioList = ConvertHandler.convertListIII(
					AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
							!dc.isNormalPriority(),
							"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?",
							dc.getId(), false, server));
		} else
		{
			prioList = ConvertHandler.convertListIII(
					AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
							!dc.isNormalPriority(),
							"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ? AND `priority` = ?",
							dc.getId(), false, server, dc.getPriorityNumber()));
		}
		ArrayList<StorageChest> endList = ConvertHandler.convertListIII(
				AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(MysqlHandler.Type.STORAGECHEST, "`priority`",
						!dc.isNormalPriority(),
						"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dc.getId(), true, server));
		int storagechestamount = prioList.size()+endList.size();
		
		
		if(ChestHandler.isContentEmpty(inv.getContents()))
		{
			debug(0, "Normal Dc Content is Empty | Normal End");
			return;
		}
		ChestHandler.setDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dc, storagechestamount);
		
		ItemStack[] cloneInvL = null;
		ItemStack[] cloneInvR = null;
		if(inv instanceof DoubleChestInventory)
		{
			debug(0, "distribution is DoubleChestInv");
			DoubleChestInventory dcinv = (DoubleChestInventory) inv;
			cloneInvL = dcinv.getLeftSide().getContents();
			cloneInvR = dcinv.getRightSide().getContents();
		} else
		{
			debug(0, "distribution dci == false");
			cloneInvL = inv.getContents();
			cloneInvR = inv.getContents();
			int j = 0;
			for(int i = 0; i < cloneInvR.length; i++)
			{
				cloneInvR[i] = null;
				j = i;
			}
			debug(0, "distribution Right side set all null | i = "+j);
		}
		
		//Normal Lager
		//PrioList und EndList
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
		ido.chestDistribute(AdvancedStoreHouse.getPlugin(),
				inv, prioList, endList, cloneInvL, cloneInvR, server, dc.isDistributeRandom(), "Normal ");
		
		//here Chainstart
		long supposeCooldown = storagechestamount*PluginSettings.settings.getDelayChainChest()*20+10;
		distributeChain(server, supposeCooldown, prioList, endList);
	}
	
	public static void distributeChain(String server, long supposeCooldown,
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList) throws IOException
	{
		debug(0, "ChainDc distribution starts");
		//Kettekisten bestimmung
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
					debug(0, "ChainDc distribution: i >= chain.size || chain End");
					cancel();
					return;
				}
				DistributionChest dcc = chain.get(i);
				debug(0, "ChainDc: "+dcc.getChestName());
				if(ChestHandler.isDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dcc))
				{
					debug(0, "ChainDc Dcc is already in distribution");
					i++;
					return;
				}
				ArrayList<StorageChest> prioListc = new ArrayList<>();
				try
				{
					prioListc = ConvertHandler.convertListIII(
							AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(
									MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), false, server));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				ArrayList<StorageChest> endListc = new ArrayList<>();
				try
				{
					endListc = ConvertHandler.convertListIII(
							AdvancedStoreHouse.getPlugin().getMysqlHandler().getAllListAt(
									MysqlHandler.Type.STORAGECHEST, "`priority`", dcc.isNormalPriority(),
									"`distributionchestid` = ? AND `endstorage` = ? AND `server` = ?", dcc.getId(), true, server));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				World world = Bukkit.getWorld(dcc.getWorld());
				if(world == null)
				{
					debug(0, "ChainDc world == null");
					i++;
					return;
				}
				Block dcblock = new Location(world, dcc.getBlockX(), dcc.getBlockY(), dcc.getBlockZ()).getBlock();
				if(dcblock == null)
				{
					debug(0, "ChainDc block == null");
					i++;
					return;
				}
				if(dcblock.getState() == null)
				{
					debug(0, "ChainDc block.getstate == null");
					i++;
					return;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(0, "ChainDc block.getstate not a container");
					i++;
					return;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(0, "ChainDc container inv == null");
					i++;
					return;
				}
				int storagechestamountc = prioListc.size()+endListc.size();
				ChestHandler.setDistributionChestOnCooldown(AdvancedStoreHouse.getPlugin(), dcc, storagechestamountc);
				ItemStack[] cloneInvLc = null;
				ItemStack[] cloneInvRc = null;
				if(inventoryc instanceof DoubleChestInventory)
				{
					debug(0, "ChainDc distribution dci == true");
					DoubleChestInventory dcinv = (DoubleChestInventory) inventoryc;
					cloneInvLc = dcinv.getLeftSide().getContents();
					cloneInvRc = dcinv.getRightSide().getContents();
				} else
				{
					debug(0, "ChainDc distribution dci == false");
					cloneInvLc = inventoryc.getContents();
					cloneInvRc = cloneInvLc;
					int j = 0;
					for(int i = 0; i < cloneInvLc.length; i++)
					{
						cloneInvRc[i] = null;
						j = i;
					}
					debug(0, "ChainDc distribution Right side set all null | i = "+j);
				}
				
				ItemDistributeObject idoc = new ItemDistributeObject(null, null);
				if(dcc.isDistributeRandom())
				{
					int[] excludes = new int[0];
					ArrayList<StorageChest> clonePrioListc = new ArrayList<>();
					for(int i = 0; i < prioListc.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, prioListc.size()-1, excludes);
						clonePrioListc.add(prioListc.get(n));
					}
					prioListc = clonePrioListc;
					int[] excludesEnd = new int[0];
					ArrayList<StorageChest> cloneEndListc = new ArrayList<>();
					for(int i = 0; i < endListc.size(); i++)
					{
						int n = ChestHandler.getRandomWithExclusion(new Random(), 0, endListc.size()-1, excludesEnd);
						cloneEndListc.add(endListc.get(n));
					}
					endListc = cloneEndListc;
				}
				idoc.chestDistribute(AdvancedStoreHouse.getPlugin(),
						inventoryc, prioListc, endListc, cloneInvLc, cloneInvRc, server, dcc.isDistributeRandom(), "Chain ");
				i++;
			}
		}.runTaskTimer(AdvancedStoreHouse.getPlugin(), supposeCooldown,
				1L*PluginSettings.settings.getDelayedChainTicks());
	}
	
	public static ItemStack[] distribute(Inventory sender, Inventory reciever, ItemStack[] filter, ItemStack[] itemStacks,
			boolean endstorage, boolean isRandom,
			//boolean optionVoid, Is only in the Background
			boolean optionDurability, StorageChest.Type durabilityType, int durability, //In full percentage
			boolean optionRepair, StorageChest.Type repairType, int repaircost, //in level
			boolean optionEnchantments,
			boolean optionMaterial
			)
	{
		ArrayList<ItemStack> similarItems = new ArrayList<>();
		ArrayList<ItemStack> sendBack = new ArrayList<>();
		
		for(int i = 0; i < itemStacks.length; i++)
		{
			final ItemStack is = itemStacks[i];
			if(is == null || is.getType() == Material.AIR)
			{
				continue;
			}
			//ItemStack isc = is.clone();
			//debug(0, "is == null "+(is == null)); true
			//final ItemStack iscf = is.clone(); true
			//debug(0, "final isClone == null "+(iscf == null));
			if(isRandom)
			{
				Random r = new Random();
				int random = r.nextInt(100);
				if(random > 50)
				{
					similarItems.add(is);
					sender.remove(is);
				} else
				{
					sendBack.add(is);
				}
			} else
			{
				if(endstorage)
				{
					similarItems.add(is);
					sender.remove(is);
				} else
				{
					if(isSimilar(is, filter,
							optionDurability, durabilityType, durability,
							optionRepair, repairType, repaircost,
							optionEnchantments, optionMaterial))
					{
						similarItems.add(is);
						sender.remove(is);
					} else
					{
						sendBack.add(is);
					}
				}
			}
		}
		
		debug(5, "similar Items:"+similarItems.size());
		ItemStack[] siArray = new ItemStack[similarItems.size()];
		similarItems.toArray(siArray);
		
		HashMap<Integer, ItemStack> map = reciever.addItem(siArray);
		//Add all not distributed Items back
		ItemStack[] returns = new ItemStack[map.size()];
		map.values().toArray(returns);
		sender.addItem(returns);
		//Add the not distributed item to the return value!
		sendBack.addAll(map.values());
		debug(5, "sendBack.size: "+sendBack.size());
		String s = "";
		for(ItemStack b : sendBack)
		{
			s += b.getType()+" ";
		}
		debug(1, "sendBack.Content: "+s);
		ItemStack[] sendBackArray = new ItemStack[sendBack.size()];
		sendBack.toArray(sendBackArray);
		
		return sendBackArray; //INFO:Funktioniert
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
	        				int percent = ChestHandler.getMaxDamage(i.getType())/(id.getDamage() == 0 ? 1 : id.getDamage());
	        				debug(0, "similar Long is Option Durability");
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
