package main.java.me.avankziar.spigot.ash.cmd.ash;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;

public class ARGDebug extends ArgumentModule
{
	//private AdvancedStoreHouse plugin;
	
	public ARGDebug(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		//this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR)
		{
			player.sendMessage("Item in Hand == null");
			return;
		}
		ItemStack is = player.getInventory().getItemInMainHand();
		player.sendMessage(is.toString());
		/*Set<Material> set = new HashSet<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, Material.HOPPER));
		Block b = getLineOfSight(player, set, 10, 20);
		if(b == null)
		{
			player.sendMessage(ChatApi.tl("Block == null"));
			return;
		}
		BlockState bs = b.getState();
		BlockData bd = b.getBlockData();
		if(bd instanceof Hopper)
		{
			player.sendMessage(ChatApi.tl("BlockData is Hopper (BlockDataType)"));
			if((((Hopper) bd).isEnabled()))
			{
				player.sendMessage(ChatApi.tl("is Powered(Enabled)! Unenabled!"));
				((Hopper) bd).setEnabled(false);
				bs.setBlockData(bd);
				bs.update();
				return;
			}
			((Hopper) bd).setEnabled(true);
			player.sendMessage(ChatApi.tl("is not Powered(Enabled)! Enabled"));
			bs.setBlockData(bd);
			bs.update();
			return;
		}
		player.sendMessage(ChatApi.tl("-----------------------------"));*/
	}
}