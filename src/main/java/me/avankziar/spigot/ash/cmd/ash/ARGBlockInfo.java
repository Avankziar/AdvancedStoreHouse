package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import main.java.me.avankziar.general.handler.ConvertHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGBlockInfo extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGBlockInfo(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		Set<Material> set = new HashSet<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL));
		//Block b = player.getTargetBlock(set, 5);
		Block b = getLineOfSight(player, set, 10, 20);
		if(b == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.BlockHasNoStoragesystem")));
			return;
		}
		final String world = b.getWorld().getName();
		final int x = b.getLocation().getBlockX();
		final int y = b.getLocation().getBlockY();
		final int z = b.getLocation().getBlockZ();
		final String server = plugin.getYamlHandler().getConfig().getString("Servername");
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				ArrayList<DistributionChest> dclist = new ArrayList<>();
				ArrayList<StorageChest> sclist = new ArrayList<>();
				try
				{
					dclist = ConvertHandler.convertListII(
							plugin.getMysqlHandler().getAllListAt(Type.DISTRIBUTIONCHEST, "`id`", false,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, world, x, y, z));
					sclist = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST, "`id`", false,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, world, x, y, z));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				if(dclist.isEmpty() && sclist.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.BlockHasNoStoragesystem")));
					return;
				}
				List<BaseComponent> dcbc = new ArrayList<>();
				List<BaseComponent> scbc = new ArrayList<>();
				if(!dclist.isEmpty())
				{
					for(DistributionChest dc : dclist)
					{
						TextComponent t1 = ChatApi.apiChat("&f"+dc.getId()+"-&e"+dc.getChestName(),
								ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)+dc.getChestName(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
						TextComponent t2 = ChatApi.apiChat("&aⒾ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.DC_INFO)+dc.getId(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
						TextComponent t3 = ChatApi.apiChat("&bⓄ ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.DC_OPENOPTION)+dc.getId(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
						TextComponent t4 = ChatApi.tctl(" &1| ");
						dcbc.add(t1);
						dcbc.add(t2);
						dcbc.add(t3);
						dcbc.add(t4);
					}
				}
				if(!sclist.isEmpty())
				{
					for(StorageChest sc : sclist)
					{
						TextComponent t1 = ChatApi.apiChat("&f"+sc.getId()+"-&e"+sc.getChestName(),
								ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getId(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
						TextComponent t2 = ChatApi.apiChat("&aⒾ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO)+sc.getId(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
						TextComponent t3 = ChatApi.apiChat("&bⓄ ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION)+sc.getId(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
						TextComponent t4 = ChatApi.tctl(" &1| ");
						scbc.add(t1);
						scbc.add(t2);
						scbc.add(t3);
						scbc.add(t4);
					}
				}
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.Headline")));
				if(!dcbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.DistributionChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(dcbc);
					player.spigot().sendMessage(tc);
				}
				if(!scbc.isEmpty())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.StorageChestsIntro")));
					TextComponent tc = ChatApi.tc("");
					tc.setExtra(scbc);
					player.spigot().sendMessage(tc);
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	
	private Block getLineOfSight(Player player, Set<Material> materialOfDesire, int maxDistance, int maxLength) 
	{
        if (maxDistance > 120) 
        {
            maxDistance = 120;
        }
        Block b = null;
        Iterator<Block> itr = new BlockIterator(player, maxDistance);
        while (itr.hasNext()) 
        {
            Block block = itr.next();
            Material material = block.getType();
            if (materialOfDesire.contains(material)) 
            {
            	b = block;
                break;
            }
        }
        return b;
    }
}