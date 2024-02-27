package main.java.me.avankziar.spigot.ash.assistance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.general.objects.SettingLevel;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.YamlManager.GuiType;

public class ItemGenerator
{	
	@SuppressWarnings("deprecation")
	public static ItemStack create(String ID, YamlConfiguration itm, int amount, GuiType type, 
			DistributionChest dc, StorageChest sc, boolean mustReplaceLore,
			SettingLevel settingLevel) throws IOException
	{
		ItemStack is = null;
		if(itm.getString(ID+"."+settingLevel.getName()+".Material") == null)
		{
			return null;
		}
		Material mat = Material.matchMaterial(itm.getString(ID+"."+settingLevel.getName()+".Material"));
		if(mat == Material.PLAYER_HEAD && itm.getString(ID+"."+settingLevel.getName()+".PlayerHeadTexture") != null)
		{
			is = getSkull(itm.getString(ID+"."+settingLevel.getName()+".PlayerHeadTexture"));
		} else
		{
			is = new ItemStack(mat);
		}
		ItemMeta im = is.getItemMeta();
		if(itm.getString(ID+"."+settingLevel.getName()+".Name") == null)
		{
			return null;
		}
		String name = "";
		if(dc != null)
		{
			name = itm.getString(ID+"."+settingLevel.getName()+".Name")
					.replace("%name%", dc.getChestName())
					.replace("%id%", String.valueOf(dc.getId()));
		}
		if(sc != null)
		{
			name = itm.getString(ID+"."+settingLevel.getName()+".Name")
					.replace("%name%", sc.getChestName())
					.replace("%id%", String.valueOf(sc.getId()));
		}
		im.setDisplayName(ChatApi.tl(name));
		ArrayList<String> itf = null;
		if(itm.getStringList(ID+"."+settingLevel.getName()+".Itemflag") != null)
		{
			itf = (ArrayList<String>) itm.getStringList(ID+"."+settingLevel.getName()+".Itemflag");
			for(int i = 0 ; i < itf.size() ; i++)
			{
				ItemFlag it = ItemFlag.valueOf(itf.get(i));
				im.addItemFlags(it);
			}
		}
		ArrayList<String> ech = null;
		if(itm.getStringList(ID+"."+settingLevel.getName()+".Enchantments") != null)
		{
			ech = (ArrayList<String>) itm.getStringList(ID+"."+settingLevel.getName()+".Enchantments");
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
		if(itm.getStringList(ID+"."+settingLevel.getName()+".Lore") != null)
		{
			if(mustReplaceLore)
			{
				desc = (ArrayList<String>) replace(itm.getStringList(ID+"."+settingLevel.getName()+".Lore"), dc, sc);
			} else
			{
				desc = (ArrayList<String>) color(itm.getStringList(ID+"."+settingLevel.getName()+".Lore"));
			}
		}
		im.setLore(desc);
		
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
			s = ChatApi.tl(replace(s, dc, sc));
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
			.replace("%creationdate%", TimeHandler.getTime(dc.getCreationDate()))
			.replace("%type%", getType(dc.getPriorityType()))
			.replace("%status%", getSort(dc.isNormalPriority()))
			.replace("%number%", String.valueOf(dc.getPriorityNumber()))
			.replace("%automaticdistribution%", getColor(dc.isAutomaticDistribution()))
			.replace("%random%", getColor(dc.isDistributeRandom()))
			.replace("%member%", "["+String.join(" ", list)+"]")
			.replace("%locationone%", dc.getServer()+" &b>>&r "+dc.getWorld())
			.replace("%locationtwo%", "&r"+dc.getBlockX()+"&7|&r"+dc.getBlockY()+"&7|&r"+dc.getBlockZ())
			.replace("%storagechestamount%", String.valueOf(storagechestamount))
			.replace("%storagechestendamount%", String.valueOf(storagechestamountend));
		}
		if(sc != null)
		{
			st = s
			.replace("%name%", sc.getChestName())
			.replace("%id%", String.valueOf(sc.getId()))
			.replace("%owner%", Utility.convertUUIDToName(sc.getOwneruuid()))
			.replace("%creationdate%", TimeHandler.getTime(sc.getCreationDate()))
			.replace("%distributionchestid%", String.valueOf(sc.getDistributionChestID()))
			.replace("%material%", getColor(sc.isOptionMaterial()))
			.replace("%priority%", String.valueOf(sc.getPriorityNumber()))
			.replace("%isendstorage%", getColor(sc.isEndstorage()))
			.replace("%isvoid%", getColor(sc.isOptionVoid()))
			.replace("%isdurability%", getColor(sc.isOptionDurability()))
			.replace("%durabilitytype%", getThan(sc.getDurabilityType()))
			.replace("%durability%", String.valueOf(sc.getDurability()))
			.replace("%isenchantment%", getColor(sc.isOptionEnchantment()))
			.replace("%isrepair%", getColor(sc.isOptionRepair()))
			.replace("%repairtype%", getThan(sc.getRepairType()))
			.replace("%repaircost%", String.valueOf(sc.getRepairCost()))
			.replace("%locationone%", sc.getServer()+" &b>> &r"+sc.getWorld())
			.replace("%locationtwo%", "&r"+sc.getBlockX()+"&7|&r"+sc.getBlockY()+"&7|&r"+sc.getBlockZ());
		}
		return st;
	}
	
	public static ItemStack getSkull(String url) 
	{
	    PlayerProfile profile = getProfile(url);
	    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta meta = (SkullMeta) head.getItemMeta();
	    meta.setOwnerProfile(profile); // Set the owning player of the head to the player profile
	    head.setItemMeta(meta);
	    return head;
        /*ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
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
        return skull;*/
    }
	
	/*
	 * BIG Thanks to https://blog.jeff-media.com/creating-custom-heads-in-spigot-1-18-1/
	 */
	private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time
	private static PlayerProfile getProfile(String url) {
	    PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID); // Get a new player profile
	    PlayerTextures textures = profile.getTextures();
	    URL urlObject;
	    try {
	        urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
	    } catch (MalformedURLException exception) {
	        throw new RuntimeException("Invalid URL", exception);
	    }
	    textures.setSkin(urlObject); // Set the skin of the player profile to the URL
	    profile.setTextures(textures); // Set the textures back to the profile
	    return profile;
	}
	
	public static String getColor(boolean boo)
	{
		if(boo)
		{
			return "&a✔";
		} else
		{
			return "&c✖";
		}
	}
	
	private static String getSort(boolean boo)
	{
		if(boo)
		{
			return "&a↗";
		} else
		{
			return "&c↘";
		}
	}
	
	private static String getType(PriorityType pt)
	{
		switch(pt)
		{
		case PLACE:
			return "&d»۝«";
		case SWITCH:
			return "&b↔";
		}
		return "&c〒";
	}
	
	private static String getThan(StorageChest.Type type)
	{
		switch(type)
		{
		case LARGERTHAN:
			return "&c>";
		case LESSTHAN:
			return "&a<";
		}
		return "&c〒";
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
