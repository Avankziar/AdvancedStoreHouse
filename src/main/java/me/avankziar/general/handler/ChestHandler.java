package main.java.me.avankziar.general.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.eventhandler.InteractHandler;

public class ChestHandler
{	
	public static ArrayList<Enchantment> enchantments;
	
	public static void debug(String s)
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
	
	private static void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			AdvancedStoreHouse.log.info(s);
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
	
	public static boolean isContentEmpty(ItemStack[] content)
	{
		for(ItemStack i : content)
		{
			if(i != null)
			{
				if(i.getType() != Material.AIR)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isMember(Player player, DistributionChest dc)
	{
		if(dc == null)
		{
			return false;
		}
		if(dc.getMemberList() == null)
		{
			return false;
		}
		for(String member : dc.getMemberList())
		{
			if(member.equals(player.getUniqueId().toString()))
			{
				return true;
			}
		}
		return false;
	}
	
	public static Location getLocationDistributionChest(DistributionChest dc)
	{
		return new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
	}
	
	public static Location getLocationStorageChest(StorageChest dc)
	{
		return new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
	}
	
	public static ArrayList<DistributionChest> getChainChest(
			AdvancedStoreHouse plugin, Player player,
			ArrayList<StorageChest> prioList, ArrayList<StorageChest> endList,
			String server) throws IOException
	{
		ArrayList<DistributionChest> chain = new ArrayList<>();
		for(StorageChest sc : prioList)
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()))
			{
				ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
						plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
						"`id`", false, 
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
				chain.addAll(allAt);
				debug(player, "Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug(player, "Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug(player, "Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(player, "Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					DoubleChestInventory dcInv = (DoubleChestInventory) inventoryc;
					lo = isDoubleChest(plugin, player, server, lo, dcInv);
					if(lo == null)
					{
						debug(player, "Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug(player, "Chain - II Adding: "+allAt.size());
				} else
				{
					debug(player, "ChainDc dont exist here");
					continue;
				}
			}
		}
		
		for(StorageChest sc : endList)
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()))
			{
				ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
						plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
						"`id`", false, 
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
				chain.addAll(allAt);
				debug(player, "Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug(player, "Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug(player, "Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug(player, "Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug(player, "Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug(player, "Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					DoubleChestInventory dcInv = (DoubleChestInventory) inventoryc;
					lo = isDoubleChest(plugin, player, server, lo, dcInv);
					if(lo == null)
					{
						debug(player, "Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id`", false, 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug(player, "Chain - II Adding: "+allAt.size());
				} else
				{
					debug(player, "ChainDC dont exist here");
					continue;
				}
			}
		}
		return chain;
	}
	
	public static ItemStack[] distribute(Inventory toAddInv, ItemStack[] filter, ItemStack[] itemStacks, Inventory toRemoveInv,
			boolean endstorage//, double durabiltyPercent, 
			)
	{
		ArrayList<HashMap<Integer, ItemStack>> notDistributetItems = new ArrayList<>();
		ArrayList<ItemStack> similarItems = new ArrayList<>();
		ArrayList<ItemStack> filteredItems = new ArrayList<>();
		
		for(ItemStack is : itemStacks)
		{
			if(endstorage)
			{
				similarItems.add(is);
			} else
			{
				if(isSimilar(is, filter))
				{
					similarItems.add(is);
				} else if(is != null)
				{
					if(is.getType() != Material.AIR)
					{
						filteredItems.add(is);
					}
				}
			}
		}
		debug("similar Items:"+similarItems.size() + " | filtered Items:"+filteredItems.size());
		for(ItemStack is : similarItems)
		{
			if(is != null)
			{
				debug("add Item");
				HashMap<Integer, ItemStack> hm = toAddInv.addItem(is);
				if(!hm.isEmpty())
				{
					debug("!hm.isEmpty");
					notDistributetItems.add(hm);
					for(ItemStack nDItems : hm.values())
					{
						//Hier eigentliches Löschen der Items aus der Verteilerkiste
						ItemStack toRemove = getNotDistributed(is, nDItems);
						debug("toRemove Amount: "+toRemove.getAmount());
						if(toRemove.getAmount() == 0)
						{
							debug("isAmount == toRemove.Amount");
							continue; //Es konnte nichts verteilt werden.
						} else if(toRemove.getAmount() == toRemove.getMaxStackSize())
						{
							debug("Amount == maxStackSize:"+toRemove.getMaxStackSize());
							toRemoveInv.remove(is);
						} else
						{
							debug("setToRemove.getAmount");
							is.setAmount(toRemove.getAmount());
						}
					}
				} else
				{
					debug("hm.isEmpty, Remove complet");
					toRemoveInv.remove(is);
				}
			}
		}
		
		ArrayList<ItemStack> re = new ArrayList<>();
		for(ItemStack is : filteredItems)
		{
			re.add(is);
		}
		for(HashMap<Integer, ItemStack> map : notDistributetItems)
		{
			for(ItemStack is : map.values())
			{
				if(is != null)
				{
					if(is.getType() != Material.AIR)
					{
						re.add(is);
						debug("EndLoop ++");
					}
				}
			}
		}
		debug("re size "+re.size());
		ItemStack[] r = new ItemStack[re.size()];
		re.toArray(r);
		return r;
	}
	
	public static ItemStack[] distributeRandom(Inventory toAddInv, ItemStack[] filter, ItemStack[] itemStacks, Inventory toRemoveInv,
			boolean endstorage//, double durabiltyPercent, 
			)
	{
		ArrayList<HashMap<Integer, ItemStack>> notDistributetItems = new ArrayList<>();
		ArrayList<ItemStack> similarItems = new ArrayList<>();
		ArrayList<ItemStack> filteredItems = new ArrayList<>();
		
		for(ItemStack is : itemStacks)
		{
			ItemStack[] arr = new ItemStack[similarItems.size()];
			if(endstorage && !isSimilar(is, toAddInv.getContents()) 
					&& !isSimilar(is, similarItems.toArray(arr)))
			{
				similarItems.add(is);
			} else
			{
				if(isSimilar(is, filter) && !isSimilar(is, toAddInv.getContents())
						&& !isSimilar(is, similarItems.toArray(arr)))
				{
					similarItems.add(is);
				} else if(is != null)
				{
					if(is.getType() != Material.AIR)
					{
						filteredItems.add(is);
					}
				}
			}
		}
		debug("similar Items:"+similarItems.size() + " | filtered Items:"+filteredItems.size());
		for(ItemStack is : similarItems)
		{
			if(is != null)
			{
				debug("add Item");
				HashMap<Integer, ItemStack> hm = toAddInv.addItem(is);
				if(!hm.isEmpty())
				{
					debug("!hm.isEmpty");
					notDistributetItems.add(hm);
					for(ItemStack nDItems : hm.values())
					{
						//Hier eigentliches Löschen der Items aus der Verteilerkiste
						ItemStack toRemove = getNotDistributed(is, nDItems);
						debug("toRemove Amount: "+toRemove.getAmount());
						if(toRemove.getAmount() == 0)
						{
							debug("isAmount == toRemove.Amount");
							continue; //Es konnte nichts verteilt werden.
						} else if(toRemove.getAmount() == toRemove.getMaxStackSize())
						{
							debug("Amount == maxStackSize:"+toRemove.getMaxStackSize());
							toRemoveInv.remove(is);
						} else
						{
							debug("setToRemove.getAmount");
							is.setAmount(toRemove.getAmount());
						}
					}
				} else
				{
					debug("hm.isEmpty, Remove complet");
					toRemoveInv.remove(is);
				}
			}
		}
		
		ArrayList<ItemStack> re = new ArrayList<>();
		for(ItemStack is : filteredItems)
		{
			re.add(is);
		}
		for(HashMap<Integer, ItemStack> map : notDistributetItems)
		{
			for(ItemStack is : map.values())
			{
				if(is != null)
				{
					if(is.getType() != Material.AIR)
					{
						re.add(is);
						debug("EndLoop ++");
					}
				}
			}
		}
		debug("re size "+re.size());
		ItemStack[] r = new ItemStack[re.size()];
		re.toArray(r);
		return r;
	}
	
	public static ItemStack getNotDistributed(ItemStack is, ItemStack other)
	{
		if(!isSimilar(is, other))
		{
			debug("getNotDistributed !isSimilar");
			return is;
		}
		int amount = is.getAmount()-other.getAmount(); //64-30 = 34
		debug("getNotDistributed: is "+is.getAmount()+" - other "+other.getAmount()+" = "+amount);
		ItemStack i = is.clone();
		i.setAmount(amount);
		return i;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack item, ItemStack... filter)
	{
		for(ItemStack is : filter)
		{
			if (is == null || item == null) 
	        {
				//debug("i || o == null || i:"+(is == null)+" | o:"+(item == null));
	            continue;
	        }
	        final ItemStack i = item.clone();
	        final ItemStack f = is.clone();
	        debug("i & f getType != |||| i:"+i.getType()+" | f:"+f.getType());
	        if(i.getType() != f.getType())
	        {
	        	debug("i & f getType != || i:"+i.getType()+" | f:"+f.getType());
	        	continue;
	        }
	        if(i.hasItemMeta() == true && f.hasItemMeta() == true)
	        {
	        	debug("i & f hasItemMeta == true");
	        	if(i.getItemMeta() != null && f.getItemMeta() != null)
	        	{
	        		debug("i & f getItemMeta != null");
	        		if(i.getItemMeta() instanceof Damageable && f.getItemMeta() instanceof Damageable)
	        		{
	        			Damageable id = (Damageable) i.getItemMeta();
	        			id.setDamage(0);
	        			i.setItemMeta((ItemMeta) id);
	        			Damageable od = (Damageable) f.getItemMeta();
	        			od.setDamage(0);
	        			f.setItemMeta((ItemMeta) od);
	        		}
		        	if(i.getItemMeta() instanceof Repairable && f.getItemMeta() instanceof Repairable)
	            	{
	            		Repairable ir = (Repairable) i.getItemMeta();
	            		ir.setRepairCost(0);
	            		i.setItemMeta((ItemMeta) ir);
	            		Repairable or = (Repairable) f.getItemMeta();
	            		or.setRepairCost(0);
	            		f.setItemMeta((ItemMeta) or);
	            	}
		        	if(i.getItemMeta() instanceof EnchantmentStorageMeta && f.getItemMeta() instanceof EnchantmentStorageMeta)
		        	{
		        		EnchantmentStorageMeta iesm = (EnchantmentStorageMeta) i.getItemMeta();
		        		i.setItemMeta(orderStorageEnchantments(iesm));
		        		EnchantmentStorageMeta oesm = (EnchantmentStorageMeta) f.getItemMeta();
		        		f.setItemMeta(orderStorageEnchantments(oesm));
		        	}
		        	if(i.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK 
		        			&& f.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK)
		        	{
		        		i.setItemMeta(orderEnchantments(i.getItemMeta()));
		        		f.setItemMeta(orderEnchantments(f.getItemMeta()));
		        	}
		        	i.setAmount(1);
		        	f.setAmount(1);
		        	if(i.getItemMeta().toString().equals(f.getItemMeta().toString()))
		        	{
		        		debug("isSimliar : long");
		        		return true;
		        	}
	        	}
	        } else
	        {
	        	i.setAmount(1);
	        	f.setAmount(1);
	        	i.setDurability((short) 0);
	        	f.setDurability((short) 0);
	        	if(i.toString().equals(f.toString()))
	        	{
	        		debug("isSimliar : short");
	        		return true;
	        	}
	        }
		}
		debug("!isSimilar");
		return false;
	}
	
	public static ItemMeta orderEnchantments(ItemMeta i)
	{
		ItemMeta ri = i.clone();
		for(Enchantment enchan : i.getEnchants().keySet())
		{
			ri.removeEnchant(enchan);
		}
		for(Enchantment enchan : enchantments)
		{
			if(i.hasEnchant(enchan))
			{
				ri.addEnchant(enchan, i.getEnchantLevel(enchan), true);
			}
		}
		return ri;
	}
	
	public static EnchantmentStorageMeta orderStorageEnchantments(EnchantmentStorageMeta esm)
	{
		EnchantmentStorageMeta resm = esm.clone();
		for(Enchantment enchan : esm.getStoredEnchants().keySet())
		{
			resm.removeStoredEnchant(enchan);
		}
		for(Enchantment enchan : enchantments)
		{
			if(esm.hasStoredEnchant(enchan))
			{
				resm.addStoredEnchant(enchan, esm.getStoredEnchantLevel(enchan), true);
			}
		}
		return resm;
	}
	
	public static void initEnchantments()
	{
		enchantments = new ArrayList<>();
		enchantments.add(Enchantment.ARROW_DAMAGE);
		enchantments.add(Enchantment.ARROW_FIRE);
		enchantments.add(Enchantment.ARROW_INFINITE);
		enchantments.add(Enchantment.ARROW_KNOCKBACK);
		enchantments.add(Enchantment.BINDING_CURSE);
		enchantments.add(Enchantment.CHANNELING);
		enchantments.add(Enchantment.DAMAGE_ALL);
		enchantments.add(Enchantment.DAMAGE_ARTHROPODS);
		enchantments.add(Enchantment.DAMAGE_UNDEAD);
		enchantments.add(Enchantment.DEPTH_STRIDER);
		enchantments.add(Enchantment.DIG_SPEED);
		enchantments.add(Enchantment.DURABILITY);
		enchantments.add(Enchantment.FIRE_ASPECT);
		enchantments.add(Enchantment.FROST_WALKER);
		enchantments.add(Enchantment.IMPALING);
		enchantments.add(Enchantment.KNOCKBACK);
		enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
		enchantments.add(Enchantment.LOOT_BONUS_MOBS);
		enchantments.add(Enchantment.LOYALTY);
		enchantments.add(Enchantment.LUCK);
		enchantments.add(Enchantment.LURE);
		enchantments.add(Enchantment.MENDING);
		enchantments.add(Enchantment.MULTISHOT);
		enchantments.add(Enchantment.OXYGEN);
		enchantments.add(Enchantment.PIERCING);
		enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
		enchantments.add(Enchantment.PROTECTION_FALL);
		enchantments.add(Enchantment.PROTECTION_FIRE);
		enchantments.add(Enchantment.PROTECTION_PROJECTILE);
		enchantments.add(Enchantment.QUICK_CHARGE);
		enchantments.add(Enchantment.RIPTIDE);
		enchantments.add(Enchantment.SILK_TOUCH);
		enchantments.add(Enchantment.SOUL_SPEED);
		enchantments.add(Enchantment.SWEEPING_EDGE);
		enchantments.add(Enchantment.THORNS);
		enchantments.add(Enchantment.VANISHING_CURSE);
		enchantments.add(Enchantment.WATER_WORKER);
	}
	
	public static int getItemsCount(ItemStack[] items)
	{
		int i = 0;
		for(ItemStack item : items)
		{
			if(item != null)
			{
				i++;
			}
		}
		return i;
	}
	
	public static String getMaterialList(ItemStack[] items)
	{
		int i = 0;
		String s = "~!~";
		for(ItemStack item : items)
		{
			if(item != null)
			{
				if(i==(items.length-1))
				{
					s += item.getType();
				} else
				{
					s += "&eItemtype: &r"+item.getType()+"~!~";
				}
			}
			i++;
		}
		return s;
	}
	
	public static Location isDoubleChest(AdvancedStoreHouse plugin, Player player, String server, final Location loc,
			DoubleChestInventory dchestInv)
	{
		debug(player, "Loc == "+loc.getWorld()
				+" | "+loc.getBlockX()
				+" | "+loc.getBlockY()
				+" | "+loc.getBlockZ());
		debug(player, "RightSide == "+dchestInv.getRightSide().getLocation().getWorld()
				+" | "+dchestInv.getRightSide().getLocation().getBlockX()
				+" | "+dchestInv.getRightSide().getLocation().getBlockY()
				+" | "+dchestInv.getRightSide().getLocation().getBlockZ());
		debug(player, "LeftSide == "+dchestInv.getLeftSide().getLocation().getWorld()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockX()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockY()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockZ());
		if(isLocationsEquals(loc, dchestInv.getLeftSide().getLocation()))
		{
			debug(player, "Loc maybe LeftSide");
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getRightSide().getLocation().getWorld().getName(),
					dchestInv.getRightSide().getLocation().getBlockX(),
					dchestInv.getRightSide().getLocation().getBlockY(),
					dchestInv.getRightSide().getLocation().getBlockZ()))
			{
				debug(player, "Loc == RightSide");
				return dchestInv.getRightSide().getLocation();
			}
		} else if(isLocationsEquals(loc, dchestInv.getRightSide().getLocation()))
		{
			debug(player, "Loc maybe LeftSide");
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getLeftSide().getLocation().getWorld().getName(),
					dchestInv.getLeftSide().getLocation().getBlockX(),
					dchestInv.getLeftSide().getLocation().getBlockY(),
					dchestInv.getLeftSide().getLocation().getBlockZ()))
			{
				debug(player, "Loc == LeftSide");
				return dchestInv.getLeftSide().getLocation();
			}
		}
		debug(player, "Loc == null");
		return null;
		/*Location l1 = loc;
		l1.add(1, 0, 0);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, 1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Distributionchest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		return null;*/
	}
	
	public static Location isDoubleChestII(AdvancedStoreHouse plugin, Player player, String server, final Location loc,
			DoubleChestInventory dchestInv)
	{
		if(isLocationsEquals(loc, dchestInv.getLeftSide().getLocation()))
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getRightSide().getLocation().getWorld().getName(),
					dchestInv.getRightSide().getLocation().getBlockX(),
					dchestInv.getRightSide().getLocation().getBlockY(),
					dchestInv.getRightSide().getLocation().getBlockZ()))
			{
				
				return dchestInv.getRightSide().getLocation();
			}
		} else if(isLocationsEquals(loc, dchestInv.getRightSide().getLocation()))
		{
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getLeftSide().getLocation().getWorld().getName(),
					dchestInv.getLeftSide().getLocation().getBlockX(),
					dchestInv.getLeftSide().getLocation().getBlockY(),
					dchestInv.getLeftSide().getLocation().getBlockZ()))
			{
				
				return dchestInv.getLeftSide().getLocation();
			}
		}
		return null;
		/*debug(player, "Storagechest dont find, search: "
				+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
		Location l1 = loc;
		l1.add(1, 0, 0);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, 1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(-1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		l1.add(1, 0, -1);
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, l1.getWorld().getName(), l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()))
		{
			return l1;
		}
		debug(player, "Storagechest dont find, search: "
				+server+" "+l1.getWorld().getName()+" "+l1.getBlockX()+" "+l1.getBlockY()+" "+l1.getBlockZ());
		return null;*/
	}
	
	public static boolean isFull(Inventory inv)
	{
		int i = 0;
		for(ItemStack is : inv.getContents())
		{
			if(is != null)
			{
				if(is.getType() != Material.AIR)
				{
					i++;
				}
			}
		}
		if(i<54)
		{
			return false;
		}
		return true;
	}
	
	public static boolean isDistributionChestOnCooldown(AdvancedStoreHouse plugin, DistributionChest dc)
	{
		if(InteractHandler.distributionCooldown.containsKey(dc.getId()))
		{
			if(InteractHandler.distributionCooldown.get(dc.getId()) > System.currentTimeMillis())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void setDistributionChestOnCooldown(AdvancedStoreHouse plugin,
			DistributionChest dc, int storagechestamount,
			boolean useFastDelay)
	{
		//int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST, "`distributionchestid` = ?", dc.getId());
		long storage = storagechestamount;
		long dif = plugin.getYamlHandler().get().getInt("DelayedTicks", 1);
		if(useFastDelay && plugin.getYamlHandler().get().getBoolean("UseFastDelayedDistribution", true))
		{
			storage = storagechestamount / plugin.getYamlHandler().get().getInt("ChestsPerTick", 10);
		}
		long start = System.currentTimeMillis();
		long cooldown = start
				+ 1000 //+ Eine Sekunde cooldown
				+ (storage*(dif*25)/10)*20; //+ Anzahl an Lagerkisten(* ChestsPerTick) * DelayedTicks * Mulitplikator von 2,5 * Ticks zu MilliSekunde
		if(InteractHandler.distributionCooldown.containsKey(dc.getId()))
		{
			InteractHandler.distributionCooldown.replace(dc.getId(), cooldown);
			InteractHandler.distributionCooldownStartTime.replace(dc.getId(), start);
		} else
		{
			InteractHandler.distributionCooldown.put(dc.getId(), cooldown);
			InteractHandler.distributionCooldownStartTime.put(dc.getId(), start);
		}
	}
	
	public static boolean isLocationsEquals(Location one, Location two)
	{
		if(one == null && two != null)
		{
			return false;
		}
		if(one != null && two == null)
		{
			return false;
		}
		if(one.getWorld().getName().equals(two.getWorld().getName())
				&& one.getX() == two.getX()
				&& one.getY() == two.getY()
				&& one.getZ() == two.getZ()
				&& one.getYaw() == two.getYaw()
				&& one.getPitch() == two.getPitch())
		{
			return true;
		} else if(one.getWorld().getName().equals(two.getWorld().getName())
				&& one.getBlockX() == two.getBlockX()
				&& one.getBlockY() == two.getBlockY()
				&& one.getBlockZ() == two.getBlockZ())
		{
			return true;
		}
		return false;
	}
	
	public static int getRandomWithExclusion(Random rnd, int start, int end, int... exclude)
	{
	    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
	    for (int ex : exclude) 
	    {
	        if (random < ex) 
	        {
	            break;
	        }
	        random++;
	    }
	    return random;
	}
	
	public static int[] addElement(int[] a, int e) 
	{
	    a  = Arrays.copyOf(a, a.length + 1);
	    a[a.length - 1] = e;
	    return a;
	}
}
