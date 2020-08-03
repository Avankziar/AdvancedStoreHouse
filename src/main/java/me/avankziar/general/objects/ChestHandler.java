package main.java.me.avankziar.general.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ChestHandler
{	
	public static ArrayList<Enchantment> enchantments;
	
	private static void debug(String s)
	{
		boolean bo = false;
		if(bo)
		{
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.spigot().sendMessage(ChatApi.tctl(s));
			}			
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
	
	public static ItemStack[] distribute(Inventory inventory, ItemStack[] filter, ItemStack[] itemStacks, Inventory toRemoveInv,
			boolean endstorage)
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
		for(ItemStack is : similarItems)
		{
			if(is != null)
			{
				HashMap<Integer, ItemStack> hm = inventory.addItem(is);
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
	
	public static boolean isSimilar(ItemStack item, ItemStack... filter)
	{
		for(ItemStack is : filter)
		{
			if (is == null || item == null) 
	        {
	            continue;
	        }
	        final ItemStack i = is.clone();
	        final ItemStack o = item.clone();
	        if(i.getType() != o.getType())
	        {
	        	continue;
	        }
	        if(i.hasItemMeta() == true && o.hasItemMeta() == true)
	        {
	        	if(i.getItemMeta() != null && o.getItemMeta() != null)
	        	{
	        		if(i.getItemMeta() instanceof Damageable && o.getItemMeta() instanceof Damageable)
	        		{
	        			Damageable id = (Damageable) i.getItemMeta();
	        			id.setDamage(0);
	        			i.setItemMeta((ItemMeta) id);
	        			Damageable od = (Damageable) o.getItemMeta();
	        			od.setDamage(0);
	        			o.setItemMeta((ItemMeta) od);
	        		}
		        	if(i.getItemMeta() instanceof Repairable && o.getItemMeta() instanceof Repairable)
	            	{
	            		Repairable ir = (Repairable) i.getItemMeta();
	            		ir.setRepairCost(0);
	            		i.setItemMeta((ItemMeta) ir);
	            		Repairable or = (Repairable) o.getItemMeta();
	            		or.setRepairCost(0);
	            		o.setItemMeta((ItemMeta) or);
	            	}
		        	if(i.getItemMeta() instanceof EnchantmentStorageMeta && o.getItemMeta() instanceof EnchantmentStorageMeta)
		        	{
		        		EnchantmentStorageMeta iesm = (EnchantmentStorageMeta) i.getItemMeta();
		        		i.setItemMeta(orderStorageEnchantments(iesm));
		        		EnchantmentStorageMeta oesm = (EnchantmentStorageMeta) o.getItemMeta();
		        		o.setItemMeta(orderStorageEnchantments(oesm));
		        	}
		        	if(i.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK 
		        			&& o.getItemMeta().hasEnchants() && i.getType() != Material.ENCHANTED_BOOK)
		        	{
		        		i.setItemMeta(orderEnchantments(i.getItemMeta()));
		        		o.setItemMeta(orderEnchantments(o.getItemMeta()));
		        	}
		        	i.setAmount(1);
		        	o.setAmount(1);
		        	if(i.getItemMeta().toString().equals(o.getItemMeta().toString()))
		        	{
		        		return true;
		        	}
	        	}
	        } else
	        {
	        	i.setAmount(1);
	        	o.setAmount(1);
	        	if(i.toString().equals(o.toString()))
	        	{
	        		return true;
	        	}
	        }
		}
		return false;
	}
	
	public static ItemMeta orderEnchantments(ItemMeta i)
	{
		ItemMeta ri = i;
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
		EnchantmentStorageMeta resm = esm;
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
}
