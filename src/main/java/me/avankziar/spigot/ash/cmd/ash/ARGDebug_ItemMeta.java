package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;

public class ARGDebug_ItemMeta extends ArgumentModule
{
	
	public ARGDebug_ItemMeta(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		ItemStack hand = player.getInventory().getItemInMainHand();
		boolean boo = false;
		if(hand != null)
		{
			player.sendMessage(ChatApi.tl("&7ItemString : &r"+hand.toString()));	
			if(hand instanceof Damageable && boo)
    		{
    			Damageable id = (Damageable) hand;
    			id.setDamage(0);
    			hand.setItemMeta((ItemMeta) id);
    			player.sendMessage(ChatApi.tl("&7ItemString &8ModifiedI : &r"+hand.toString()));	
    		} else
    		{
    			if(boo)
    			{
    				hand.setDurability((short) 0);
        			player.sendMessage(ChatApi.tl("&7ItemString &8ModifiedII : &r"+hand.toString()));
    			}
    		}
			if(hand.getItemMeta() != null)
			{
				player.sendMessage(ChatApi.tl("&7ItemMeta : &r"+hand.getItemMeta().toString()));
				if(hand.getItemMeta() instanceof Damageable && boo)
	    		{
	    			Damageable id = (Damageable) hand.getItemMeta();
	    			id.setDamage(0);
	    			hand.setItemMeta((ItemMeta) id);
	    			player.sendMessage(ChatApi.tl("&7ItemMeta &8Modified : &r"+hand.getItemMeta().toString()));	
	    		}
			}
		}
		return;
	}
}