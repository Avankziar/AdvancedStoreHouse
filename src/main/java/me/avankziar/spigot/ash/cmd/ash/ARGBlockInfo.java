package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
		final Block b = player.getTargetBlock(set, 5);
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
									server, b.getWorld().getName(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
					sclist = ConvertHandler.convertListIII(
							plugin.getMysqlHandler().getAllListAt(Type.STORAGECHEST, "`id`", false,
									"`server` = ? AND `world` = ? AND `blockx` = ? AND `blocky` = ? AND `blockz` = ?",
									server, b.getWorld().getName(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ()));
				} catch (IOException e){}
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
						TextComponent t1 = ChatApi.apiChat(dc.getId()+":"+dc.getChestName(),
								ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.DC_SELECT)+dc.getChestName(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
						TextComponent t2 = ChatApi.apiChat("Ⓘ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.DC_INFO),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
						TextComponent t3 = ChatApi.apiChat("Ⓞ ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.DC_OPENOPTION),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
						dcbc.add(t1);
						dcbc.add(t2);
						dcbc.add(t3);
					}
				}
				if(!sclist.isEmpty())
				{
					for(StorageChest sc : sclist)
					{
						TextComponent t1 = ChatApi.apiChat(sc.getId()+":"+sc.getChestName(),
								ClickEvent.Action.RUN_COMMAND, PluginSettings.settings.getCommands().get(KeyHandler.SC_SELECT)+sc.getChestName(),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverSelect"));
						TextComponent t2 = ChatApi.apiChat("Ⓘ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.SC_INFO),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverInfo"));
						TextComponent t3 = ChatApi.apiChat("Ⓞ ",
								ClickEvent.Action.RUN_COMMAND,
								PluginSettings.settings.getCommands().get(KeyHandler.SC_OPENOPTION),
								HoverEvent.Action.SHOW_TEXT,
								plugin.getYamlHandler().getLang().getString("CmdAsh.BlockInfo.ChestHoverOpenGui"));
						scbc.add(t1);
						scbc.add(t2);
						scbc.add(t3);
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
}