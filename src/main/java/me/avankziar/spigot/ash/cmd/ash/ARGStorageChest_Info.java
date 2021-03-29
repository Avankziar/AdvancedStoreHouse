package main.java.me.avankziar.spigot.ash.cmd.ash;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.MatchApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentConstructor;
import main.java.me.avankziar.spigot.ash.cmd.tree.ArgumentModule;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;

public class ARGStorageChest_Info extends ArgumentModule
{
	private AdvancedStoreHouse plugin;
	
	public ARGStorageChest_Info(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
	{
		super(plugin, argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		PluginUser user = PluginUserHandler.getUser(player.getUniqueId());
		if(user == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DatabaseError")
				.replace("%cmd%", "/ash storagechest info")));
			return;
		}
		int id = user.getStorageChestID();
		if(args.length == 3 && MatchApi.isInteger(args[2]))
		{
			id = Integer.parseInt(args[2]);
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Select.SChestDontExist")));
			return;
		}
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, "`id` = ?", id);
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(
				MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID());
		if(dc != null)
		{
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSINFO))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
				return;
			}
		}
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.HeadlineSC")
				.replace("%name%", sc.getChestName())
				.replace("%id%", String.valueOf(id))));
		if(dc != null)
		{
			String owner = Utility.convertUUIDToName(dc.getOwneruuid());
			if(owner != null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Owner")
						.replace("%owner%", owner)));
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Owner")
						.replace("%owner%", "/")));
			}
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.DCName")
					.replace("%id%", String.valueOf(dc.getId()))
					.replace("%name%", dc.getChestName())));
		}
		String loc = sc.getServer()+" &a"+sc.getWorld()+" &d"+sc.getBlockX()+" "+sc.getBlockY()+" "+sc.getBlockZ();
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Location")
				.replace("%pos%", loc)));
		if(dc != null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Member")
					.replace("%member%", "["+String.join(" ", dc.getMemberList())+"]")));
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Priority")
				.replace("%prio%", String.valueOf(sc.getPriorityNumber()))));
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.Endstorage")
				.replace("%end%", String.valueOf(sc.isEndstorage()))));
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.OptionDurability")
				.replace("%opdur%", String.valueOf(sc.isOptionDurability()))));
		if(sc.isOptionDurability())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.DurabilityType")
					.replace("%durtype%", String.valueOf(sc.getDurabilityType()))));
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.DurabilityValue")
					.replace("%dur%", String.valueOf(sc.getDurability()))));
		}		
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.OptionRepair")
				.replace("%oprepair%", String.valueOf(sc.isOptionRepair()))));
		if(sc.isOptionRepair())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.RepairType")
					.replace("%repairtype%", String.valueOf(sc.getRepairType()))));
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.RepairCost")
					.replace("%repaircost%", String.valueOf(sc.getRepairCost()))));
		}
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.OptionEnchantment")
				.replace("%opench%", String.valueOf(sc.isOptionEnchantment()))));
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.OptionMaterial")
				.replace("%opmat%", String.valueOf(sc.isOptionMaterial()))));
		
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAsh.Info.OptionVoid")
				.replace("%opvoid%", String.valueOf(sc.isOptionVoid()))));
		
		
		
						
		return;
	}
}