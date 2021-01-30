package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.YamlManager.GuiType;

public class ItemGenerator
{	
	@SuppressWarnings("deprecation")
	public static ItemStack create(String ID, YamlConfiguration itm, int amount, GuiType type, 
			DistributionChest dc, StorageChest sc, boolean mustReplaceLore) throws IOException
	{
		ItemStack is = null;
		if(itm.getString(ID+".Material") == null)
		{
			return null;
		}
		Material mat = Material.matchMaterial(itm.getString(ID+".Material"));
		if(mat == Material.PLAYER_HEAD && itm.getString(ID+".PlayerHeadTexture") != null)
		{
			is = getSkull(itm.getString(ID+".PlayerHeadTexture"));
		} else
		{
			is = new ItemStack(mat);
		}
		ItemMeta im = is.getItemMeta();
		if(itm.getString(ID+".Name") == null)
		{
			return null;
		}
		String name = "";
		if(dc != null)
		{
			name = itm.getString(ID+".Name")
					.replace("%name%", dc.getChestName())
					.replace("%id%", String.valueOf(dc.getId()));
		}
		if(sc != null)
		{
			name = itm.getString(ID+".Name")
					.replace("%chestname%", sc.getChestName())
					.replace("%id%", String.valueOf(sc.getId()));
		}
		im.setDisplayName(ChatApi.tl(name));
		ArrayList<String> itf = null;
		if(itm.getStringList(ID+".Itemflag") != null)
		{
			itf = (ArrayList<String>) itm.getStringList(ID+".Itemflag");
			for(int i = 0 ; i < itf.size() ; i++)
			{
				ItemFlag it = ItemFlag.valueOf(itf.get(i));
				im.addItemFlags(it);
			}
		}
		ArrayList<String> ech = null;
		if(itm.getStringList(ID+".Enchantments") != null)
		{
			ech = (ArrayList<String>) itm.getStringList(ID+".Enchantments");
			for(int i = 0 ; i < ech.size() ; i++)
			{
				String[] a = ech.get(i).split(";");
				String b = a[0].toUpperCase();
				Enchantment eh = EnchantmentWrapper.getByName(b);
				int d = Integer.parseInt(a[1]);
				if(eh != null)
				{
					im.addEnchant(eh, d, true);	
				}
			}
		}
		ArrayList<String> desc = null;
		if(itm.getStringList(ID+".Lore") != null)
		{
			if(mustReplaceLore)
			{
				desc = (ArrayList<String>) replace(itm.getStringList(ID+".Lore"), dc, sc);
			} else
			{
				desc = (ArrayList<String>) itm.getStringList(ID+".Lore");
			}
		}
		im.setLore(color(desc));
		
		NamespacedKey gt = new NamespacedKey(AdvancedStoreHouse.getPlugin(), "guitype");
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(gt, PersistentDataType.STRING, type.toString());
		
		is.setItemMeta(im);
		is.setAmount(amount);
		return is;
	}
	
	public static ArrayList<String> replace(List<String> lore, DistributionChest dc, StorageChest sc) throws IOException
	{
		ArrayList<String> desc = new ArrayList<String>();
		for(String s : lore)
		{
			s = replace(s, dc, sc);
			desc.add(s);
		}
		return desc;
	}
	
	public static String replace(String s, DistributionChest dc, StorageChest sc) throws IOException
	{
		String st = "";
		if(dc != null)
		{
			List<String> list = new ArrayList<>();
			for(String uuid : dc.getMemberList())
			{
				String name = Utility.convertUUIDToName(uuid);
				if(name != null)
				{
					list.add(name);
				}
			}
			int storagechestamount = AdvancedStoreHouse.getPlugin().getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
					"`distributionchestid` = ? AND `endstorage` = ?", dc.getId(), false);
			int storagechestamountend = AdvancedStoreHouse.getPlugin().getMysqlHandler().countWhereID(MysqlHandler.Type.STORAGECHEST,
					"`distributionchestid` = ? AND `endstorage` = ?", dc.getId(), true);
			st = s
			.replace("%name%", dc.getChestName())
			.replace("%id%", String.valueOf(dc.getId()))
			.replace("%creationdate%", String.valueOf(dc.getCreationDate()))
			.replace("%prioritytype%", dc.getPriorityType().toString())
			.replace("%prioritynumber%", String.valueOf(dc.getPriorityNumber()))
			.replace("%normalpriority%", String.valueOf(dc.isNormalPriority()))
			.replace("%automaticdistribution%", String.valueOf(dc.isAutomaticDistribution()))
			.replace("%randomdistribution%", String.valueOf(dc.isDistributeRandom()))
			.replace("%member%", "["+String.join(" ", list)+"]")
			.replace("%location%", dc.getServer()+"-"+dc.getWorld()+"-"+dc.getBlockX()+"|"+dc.getBlockY()+"|"+dc.getBlockZ()+"|")
			.replace("%storagechestamount%", String.valueOf(storagechestamount))
			.replace("%storagechestendamount%", String.valueOf(storagechestamountend));
		}
		if(sc != null)
		{
			st = s
			.replace("%name%", sc.getChestName())
			.replace("%id%", String.valueOf(sc.getId()))
			.replace("%owner%", Utility.convertUUIDToName(sc.getOwneruuid()))
			.replace("%creationdate%", String.valueOf(sc.getCreationDate()))
			.replace("%distributionchestid%", String.valueOf(sc.getDistributionChestID()))
			.replace("%priority%", String.valueOf(sc.getPriorityNumber()))
			.replace("%isendstorage%", String.valueOf(sc.isEndstorage()))
			.replace("%isvoid%", String.valueOf(sc.isOptionVoid()))
			.replace("%isdurability%", String.valueOf(sc.isOptionDurability()))
			.replace("%durability%", String.valueOf(sc.getDurability()))
			.replace("%isenchantment%", String.valueOf(sc.isOptionEnchantment()))
			.replace("%isrepair%", String.valueOf(sc.isOptionRepair()))
			.replace("%repaircost%", String.valueOf(sc.getRepairCost()))
			.replace("%location%", sc.getServer()+"-"+sc.getWorld()+"-"+sc.getBlockX()+"|"+sc.getBlockY()+"|"+sc.getBlockZ()+"|");
		}
		return st;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getSkull(String url) 
	{
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }
	
	private static List<String> color(List<String> lore)
	{
		ArrayList<String> list = new ArrayList<String>();
		for(String s : lore)
		{
			list.add(ChatApi.tl(s));
		}
	    return list;
	}
	
	public static double getNumberFormat(double d)//FIN
	{
		BigDecimal bd = new BigDecimal(d).setScale(1, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}
	
	public static double getNumberFormat(double d, int scale)//FIN
	{
		BigDecimal bd = new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}

}
