package main.java.me.avankziar.general.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;

public class ConvertHandler
{
	public static ArrayList<PluginUser> convertListI(ArrayList<?> list)
	{
		ArrayList<PluginUser> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof PluginUser)
			{
				el.add((PluginUser) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static ArrayList<DistributionChest> convertListII(ArrayList<?> list)
	{
		ArrayList<DistributionChest> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof DistributionChest)
			{
				el.add((DistributionChest) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static ArrayList<StorageChest> convertListIII(ArrayList<?> list)
	{
		ArrayList<StorageChest> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof StorageChest)
			{
				el.add((StorageChest) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static ArrayList<ItemFilterSet> convertListIV(ArrayList<?> list)
	{
		ArrayList<ItemFilterSet> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof ItemFilterSet)
			{
				el.add((ItemFilterSet) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static String ToBase64itemStackArray(ItemStack[] items) throws IllegalStateException  //FIN
    {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.length);
            
            for (int i = 0; i < items.length; i++) 
            {
                dataOutput.writeObject(items[i]);
            }
            
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    public static ItemStack[] FromBase64itemStackArray(String data) throws IOException  //FIN
    {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) 
            {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) 
    	{
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
