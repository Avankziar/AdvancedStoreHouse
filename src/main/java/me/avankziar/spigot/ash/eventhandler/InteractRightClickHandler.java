package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.TimeHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUserHandler;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

/*
 * Class is for the handling of Creation, Options etc for dc and sc.
 */
public class InteractRightClickHandler
{
	//INFO Start methode
	public void onRightClick(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		ItemStack inHand = event.getItem();
		if(inHand == null
				|| (inHand != null && inHand.getType() == Material.AIR))
		{
			//Hier legt man dc und sc an.
		} else
		{
			//hier geht man durch die anderen Items.
		}
		PluginUserHandler.cancelAction(player, user, user.getMode(), 
				AdvancedStoreHouse.getPlugin().getYamlHandler().getL().getString("CancelAction"));
		checkIfDistributionChest(event, player, user);
	}
	
	/*
	 * Methode is for check, if the block is a dc.
	 * If it a dc, and the cooldown is on, it denying the access.
	 */
	public void checkIfDistributionChest(PlayerInteractEvent event, Player player, PluginUser user) throws IOException
	{
		Block block = event.getClickedBlock();
		if(block.getState() == null)
		{
			debug(event.getPlayer(), "Block.State == null");
			return;
		}
		if(!(block.getState() instanceof Container))
		{
			debug(event.getPlayer(), "Block != Container | Type: "+block.getType().toString());
			return;
		}
		String server = AdvancedStoreHouse.getPlugin().getYamlHandler().get().getString("Servername");
		Location loc = event.getClickedBlock().getLocation();
		if(loc == null)
		{
			debug(event.getPlayer(), "Location == null");
			return;
		}
		DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
				server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if(dc == null)
		{
			debug(event.getPlayer(), "Block == Container | Type: "+block.getType().toString());
			debug(player, "Distributionchest dont find, search: "
					+server+" "+block.getLocation().getWorld().getName()+" "+block.getLocation().getBlockX()+" "+
					block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ());
			Container c = (Container) block.getState();
			Inventory inv = c.getInventory();
			if(inv instanceof DoubleChestInventory)
			{
				DoubleChestInventory dcInv = (DoubleChestInventory) inv;
				debug(player, "distribution == DoubleChestInv");
				loc = ChestHandler.isDoubleChest(AdvancedStoreHouse.getPlugin(), server, loc, dcInv);
				if(loc == null)
				{
					debug(player, "Distributionchest dont exist: ");
					return;
				}
				dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
						"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
						server, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			} else
			{
				debug(player, "Distributionchest dont exist: "
						+server+" "+loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());
				return;
			}
		}
		if(dc != null)
		{
			if(InteractHandler.distributionCooldown.containsKey(dc.getId()))
			{
				long dcc = InteractHandler.distributionCooldown.get(dc.getId());
				long start = InteractHandler.distributionCooldownStartTime.get(dc.getId());
				debug(player, "Cooldown: "+dcc+" | Milli: "+System.currentTimeMillis());
				if(dcc > System.currentTimeMillis())
				{
					event.setCancelled(true);
					player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getL().getString("DistributionIsRunning")
							.replace("%start%", TimeHandler.getTime(start))
							.replace("%time%", TimeHandler.getTime(dcc))));
				}
			}
		}
	}
	
	private void debug(Player player, String s)
	{
		boolean bo = false;
		if(bo)
		{
			player.spigot().sendMessage(ChatApi.tctl(s));
		}
	}
}
