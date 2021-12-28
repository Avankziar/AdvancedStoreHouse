package main.java.me.avankziar.general.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.eventhandler.InteractHandler;
import main.java.me.avankziar.spigot.ash.listener.InventoryClickBlockerListener;

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
	
	public static DistributionChest getDistributionChest(AdvancedStoreHouse plugin, Location loc) throws IOException
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		Block block = loc.getBlock();
		if(block == null)
		{
			return null;
		}
		if(block.getState() instanceof Chest)
		{
			Chest chest = (Chest) block.getState();
			if(chest.getInventory() instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcinv = (DoubleChestInventory) chest.getInventory();
				Location left = dcinv.getLeftSide().getLocation();
				Location right = dcinv.getRightSide().getLocation();
				if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						server, left.getWorld().getName(), left.getBlockX(), left.getBlockY(), left.getBlockZ()))
				{
					DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, left.getWorld().getName(), left.getBlockX(), left.getBlockY(), left.getBlockZ());
					return dc;
				} else
				{
					if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, right.getWorld().getName(), right.getBlockX(), right.getBlockY(), right.getBlockZ()))
					{
						DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, right.getWorld().getName(), right.getBlockX(), right.getBlockY(), right.getBlockZ());
						return dc;
					}
				}
			}
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			return dc;
		}
		return null;
	}
	
	public static StorageChest getStorageChest(AdvancedStoreHouse plugin, Location loc) throws IOException
	{
		String server = plugin.getYamlHandler().getConfig().getString("Servername");
		Block block = loc.getBlock();
		if(block == null)
		{
			return null;
		}
		if(block.getState() instanceof Chest)
		{
			Chest chest = (Chest) block.getState();
			if(chest.getInventory() instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcinv = (DoubleChestInventory) chest.getInventory();
				Location left = dcinv.getLeftSide().getLocation();
				Location right = dcinv.getRightSide().getLocation();
				if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						server, left.getWorld().getName(), left.getBlockX(), left.getBlockY(), left.getBlockZ()))
				{
					StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, left.getWorld().getName(), left.getBlockX(), left.getBlockY(), left.getBlockZ());
					return sc;
				} else
				{
					if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							server, right.getWorld().getName(), right.getBlockX(), right.getBlockY(), right.getBlockZ()))
					{
						StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
								"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
								server, right.getWorld().getName(), right.getBlockX(), right.getBlockY(), right.getBlockZ());
						return sc;
					}
				}
			}
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
		{
			StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			return sc;
		}
		return null;
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
		if(dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return true;
		}
		return false;
	}
	
	public static Location getLocationDistributionChest(DistributionChest dc)
	{
		return new Location(Bukkit.getWorld(dc.getWorld()), dc.getBlockX(), dc.getBlockY(), dc.getBlockZ());
	}
	
	public static Location getLocationStorageChest(StorageChest sc)
	{
		return new Location(Bukkit.getWorld(sc.getWorld()), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
	}
	
	public static ArrayList<DistributionChest> getChainChest(
			AdvancedStoreHouse plugin,
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
						"`id` ASC", 
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
				chain.addAll(allAt);
				debug("Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug("Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug("Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug("Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug("Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug("Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					DoubleChestInventory dcInv = (DoubleChestInventory) inventoryc;
					lo = isDoubleChest(plugin, server, lo, dcInv);
					if(lo == null)
					{
						debug("Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id` ASC", 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug("Chain - II Adding: "+allAt.size());
				} else
				{
					debug("ChainDc dont exist here");
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
						"`id` ASC", 
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						sc.getServer(), sc.getWorld(), sc.getBlockX(), sc.getBlockY(), sc.getBlockZ()));
				chain.addAll(allAt);
				debug("Chain - I Adding: "+allAt.size());
			} else
			{
				World world = Bukkit.getWorld(sc.getWorld());
				if(world == null)
				{
					debug("Chain Prio: World == null");
					continue;
				}
				Location lo = new Location(world, sc.getBlockX(), sc.getBlockY(), sc.getBlockZ());
				Block dcblock = lo.getBlock();
				if(dcblock == null)
				{
					debug("Chain Prio: dcblock == null");
					continue;
				}
				if(dcblock.getState() == null)
				{
					debug("Chain Prio: dcblock.State == null");
					continue;
				}
				if(!(dcblock.getState() instanceof Container))
				{
					debug("Chain Prio: !instanceof Container");
					continue;
				}
				Inventory inventoryc = ((Container)dcblock.getState()).getInventory();
				if(inventoryc == null)
				{
					debug("Chain Prio: inventoryc == null");
					continue;
				}
				if(inventoryc instanceof DoubleChestInventory)
				{
					DoubleChestInventory dcInv = (DoubleChestInventory) inventoryc;
					lo = isDoubleChest(plugin, server, lo, dcInv);
					if(lo == null)
					{
						debug("Loop DoubleChest Loc == null");
						continue;
					}
					ArrayList<DistributionChest> allAt = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(MysqlHandler.Type.DISTRIBUTIONCHEST,
							"`id` ASC", 
							"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
							sc.getServer(), lo.getWorld().getName(), lo.getBlockX(), lo.getBlockY(), lo.getBlockZ()));
					chain.addAll(allAt);
					debug("Chain - II Adding: "+allAt.size());
				} else
				{
					debug("ChainDC dont exist here");
					continue;
				}
			}
		}
		return chain;
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
	
	public static Location isDoubleChest(AdvancedStoreHouse plugin, String server, final Location loc,
			DoubleChestInventory dchestInv)
	{
		debug("Loc == "+loc.getWorld()
				+" | "+loc.getBlockX()
				+" | "+loc.getBlockY()
				+" | "+loc.getBlockZ());
		debug("RightSide == "+dchestInv.getRightSide().getLocation().getWorld()
				+" | "+dchestInv.getRightSide().getLocation().getBlockX()
				+" | "+dchestInv.getRightSide().getLocation().getBlockY()
				+" | "+dchestInv.getRightSide().getLocation().getBlockZ());
		debug("LeftSide == "+dchestInv.getLeftSide().getLocation().getWorld()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockX()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockY()
				+" | "+dchestInv.getLeftSide().getLocation().getBlockZ());
		if(isLocationsEquals(loc, dchestInv.getLeftSide().getLocation()))
		{
			debug("Loc maybe LeftSide");
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getRightSide().getLocation().getWorld().getName(),
					dchestInv.getRightSide().getLocation().getBlockX(),
					dchestInv.getRightSide().getLocation().getBlockY(),
					dchestInv.getRightSide().getLocation().getBlockZ()))
			{
				debug("Loc == RightSide");
				return dchestInv.getRightSide().getLocation();
			}
		} else if(isLocationsEquals(loc, dchestInv.getRightSide().getLocation()))
		{
			debug("Loc maybe LeftSide");
			if(plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST,
					"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
					server, dchestInv.getLeftSide().getLocation().getWorld().getName(),
					dchestInv.getLeftSide().getLocation().getBlockX(),
					dchestInv.getLeftSide().getLocation().getBlockY(),
					dchestInv.getLeftSide().getLocation().getBlockZ()))
			{
				debug("Loc == LeftSide");
				return dchestInv.getLeftSide().getLocation();
			}
		}
		debug("Loc == null");
		return null;
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
			DistributionChest dc, int storagechestamount, Location loc)
	{
		//int amount = plugin.getMysqlHandler().countWhereID(MysqlHandler.Type.DISTRIBUTIONCHEST, "`distributionchestid` = ?", dc.getId());
		//3000/10 = 300
		//1000 + (300*25*20)/(10) = 16000 ms = 16 sekunden
		long storage = storagechestamount / PluginSettings.settings.getChestsPerTick();
		long dif = PluginSettings.settings.getDelayedTicks();
		
		long start = System.currentTimeMillis();
		long cooldown = start
				+ 2000 //+ Eine Sekunde cooldown
				+ storagechestamount/PluginSettings.settings.getWaitBeforStartFactor()
				+ storage*20*25*dif/10; //300*20*2,5/2
		if(InteractHandler.distributionCooldown.containsKey(dc.getId()))
		{
			InteractHandler.distributionCooldown.replace(dc.getId(), cooldown);
			InteractHandler.distributionCooldownStartTime.replace(dc.getId(), start);
		} else
		{
			InteractHandler.distributionCooldown.put(dc.getId(), cooldown);
			InteractHandler.distributionCooldownStartTime.put(dc.getId(), start);
		}
		InventoryClickBlockerListener.setLocationCooldown(loc, cooldown);
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
	
	public static int getMaxDamage(Material material)
	{
		int damage = 0;
		switch(material)
		{
		case WOODEN_AXE: //Fallthrough
		case WOODEN_HOE:
		case WOODEN_PICKAXE:
		case WOODEN_SHOVEL:
		case WOODEN_SWORD:
			damage = 60;
			break;
		case LEATHER_BOOTS:
			damage = 65;
			break;
		case LEATHER_CHESTPLATE:
			damage = 80;
			break;
		case LEATHER_HELMET:
			damage = 55;
			break;
		case LEATHER_LEGGINGS:
			damage = 75;
			break;
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SHOVEL:
		case STONE_SWORD:
			damage = 132;
			break;
		case CHAINMAIL_BOOTS:
			damage = 196;
			break;
		case CHAINMAIL_CHESTPLATE:
			damage = 241;
			break;
		case CHAINMAIL_HELMET:
			damage = 166;
			break;
		case CHAINMAIL_LEGGINGS:
			damage = 226;
			break;
		case GOLDEN_AXE:
		case GOLDEN_HOE:
		case GOLDEN_PICKAXE:
		case GOLDEN_SHOVEL:
		case GOLDEN_SWORD:
			damage = 33;
			break;
		case GOLDEN_BOOTS:
			damage = 91;
			break;
		case GOLDEN_CHESTPLATE:
			damage = 112;
			break;
		case GOLDEN_HELMET:
			damage = 77;
			break;
		case GOLDEN_LEGGINGS:
			damage = 105;
			break;
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SHOVEL:
		case IRON_SWORD:
			damage = 251;
			break;
		case IRON_BOOTS:
			damage = 195;
			break;
		case IRON_CHESTPLATE:
			damage = 40;
			break;
		case IRON_HELMET:
			damage = 165;
			break;
		case IRON_LEGGINGS:
			damage = 225;
			break;
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SHOVEL:
		case DIAMOND_SWORD:
			damage = 1562;
			break;
		case DIAMOND_BOOTS:
			damage = 429;
			break;
		case DIAMOND_CHESTPLATE:
			damage = 528;
			break;
		case DIAMOND_HELMET:
			damage = 363;
			break;
		case DIAMOND_LEGGINGS:
			damage = 495;
			break;
		case NETHERITE_AXE:
		case NETHERITE_HOE:
		case NETHERITE_PICKAXE:
		case NETHERITE_SHOVEL:
		case NETHERITE_SWORD:
			damage = 2031;
			break;
		case NETHERITE_BOOTS:
			damage = 482;
			break;
		case NETHERITE_CHESTPLATE:
			damage = 592;
			break;
		case NETHERITE_HELMET:
			damage = 408;
			break;
		case NETHERITE_LEGGINGS:
			damage = 556;
			break;
		case SHIELD:
			damage = 337;
			break;
		case TURTLE_HELMET:
			damage = 276;
			break;
		case TRIDENT:
			damage = 251;
			break;
		case FISHING_ROD:
			damage = 65;
			break;
		case CARROT_ON_A_STICK:
			damage = 26;
			break;
		case WARPED_FUNGUS_ON_A_STICK:
			damage = 100;
			break;
		case ELYTRA:
			damage = 432;
			break;
		case SHEARS:
			damage = 238;
			break;
		case BOW:
			damage = 385;
			break;
		case CROSSBOW:
			damage = 326;
			break;
		case FLINT_AND_STEEL:
			damage = 65;
			break;
		default:
			damage = 0;
			break;
		}
		return damage;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isSimilarShort(ItemStack item, ItemStack[] filter)
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
		        		i.setItemMeta(ChestHandler.orderStorageEnchantments(iesm));
		        		EnchantmentStorageMeta oesm = (EnchantmentStorageMeta) f.getItemMeta();
		        		f.setItemMeta(ChestHandler.orderStorageEnchantments(oesm));
		        	}
		        	if(i.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK 
		        			&& f.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK)
		        	{
		        		i.setItemMeta(ChestHandler.orderEnchantments(i.getItemMeta()));
		        		f.setItemMeta(ChestHandler.orderEnchantments(f.getItemMeta()));	
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
	
	@SuppressWarnings("deprecation")
	public static String getGroundSpecs(ItemStack item)
	{
		if (item == null) 
        {
            return null;
        }
        final ItemStack i = item.clone();
        if(i.hasItemMeta() == true)
        {
        	if(i.getItemMeta() instanceof Damageable)
    		{
    			Damageable id = (Damageable) i.getItemMeta();
    			id.setDamage(0);
    			i.setItemMeta((ItemMeta) id);
    		}
        	if(i.getItemMeta() instanceof Repairable)
        	{
        		Repairable ir = (Repairable) i.getItemMeta();
        		ir.setRepairCost(0);
        		i.setItemMeta((ItemMeta) ir);
        	}
        	if(i.getItemMeta() instanceof EnchantmentStorageMeta)
        	{
        		EnchantmentStorageMeta iesm = (EnchantmentStorageMeta) i.getItemMeta();
        		i.setItemMeta(ChestHandler.orderStorageEnchantments(iesm));
        	}
        	if(i.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK)
        	{
        		i.setItemMeta(ChestHandler.orderEnchantments(i.getItemMeta()));
        	}
        	i.setAmount(1);
        	return i.toString();
        } else
        {
        	i.setAmount(1);
        	i.setDurability((short) 0);
        	return i.toString();
        }
	}
}
