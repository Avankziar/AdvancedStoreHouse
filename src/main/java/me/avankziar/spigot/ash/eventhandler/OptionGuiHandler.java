package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import main.java.me.avankziar.general.handler.ChestHandler;
import main.java.me.avankziar.general.handler.KeyHandler;
import main.java.me.avankziar.general.handler.PluginUserHandler;
import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.general.objects.DistributionChest.PriorityType;
import main.java.me.avankziar.general.objects.ItemFilterSet;
import main.java.me.avankziar.general.objects.PluginSettings;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;
import main.java.me.avankziar.general.objects.SettingLevel;
import main.java.me.avankziar.general.objects.StorageChest;
import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.assistance.ItemGenerator;
import main.java.me.avankziar.spigot.ash.assistance.Utility;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler;
import main.java.me.avankziar.spigot.ash.database.MysqlHandler.Type;
import main.java.me.avankziar.spigot.ash.database.YamlManager.GuiType;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class OptionGuiHandler
{	
	public void clickStart(InventoryClickEvent event, Player player, PluginUser user, boolean isTopInventory) throws IOException
	{
		if(isTopInventory == false)
		{
			event.setCancelled(true);
			event.setResult(Result.DENY);
			return;
		}
		if(event.getClickedInventory() == null)
		{
			event.setCancelled(true);
			event.setResult(Result.DENY);
			return;
		}
		ItemStack clicked = event.getCurrentItem().clone();
		final int slot = event.getSlot();
		ItemMeta cim = clicked.getItemMeta();
		
		PersistentDataContainer pdc = cim.getPersistentDataContainer();
		NamespacedKey gt = new NamespacedKey(AdvancedStoreHouse.getPlugin(), "guitype");
		
		if(!pdc.has(gt, PersistentDataType.STRING))
		{
			return;
		}
		GuiType guitype = GuiType.valueOf(pdc.get(gt, PersistentDataType.STRING));
		switch(guitype)
		{
		case DC_MAIN:
			switchDc_Main(event, player, user, slot);
			break;
		case DC_NUMPAD:
			switchDc_Numpad(event, player, user, slot);
			break;
		case SC_MAIN:
			switchSc_Main(event, player, user, slot);
			break;
		case SC_PRIORITY_NUMPAD:
			switchSc_Priority_Numpad(event, player, user, slot);
			break;
		case SC_DURABILITY_NUMPAD:
			switchSc_Durability_Numpad(event, player, user, slot);
			break;
		case SC_REPAIR_NUMPAD:
			switchSc_Repair_Numpad(event, player, user, slot);
			break;			
		}
	}
	
	public void openDcGuiMain(Player player, PluginUser user, DistributionChest dc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.DC_MAIN;
		/*
		 * Slot 4 Alle Infos zur Dc
		 * Slot 10 Namen ändern
		 * Slot 16 Priorität switchen {Experte}
		 * Slot 25 Prorität umschalten (Zwischen Switch oder Place) {Experte}
		 * Slot 28 Automatische Verteilung ein oder ausschalten
		 * Slot 34 Priorität setzten (Taschenrechner like) {Experte}
		 * Slot 46 Random ein- auschalten {Experte}
		 * Slot 52 Mitglieder
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.DcTitle")
							.replace("%id%", String.valueOf(dc.getId()))
							.replace("%name%", dc.getChestName())));
		}
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 0)
			{
				if(player.hasPermission(Utility.PERMBYPASSEXPERTMODUS))
				{
					ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, false, user.getSettingLevel());
					inventory.setItem(slot, is);
				}
			} else if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else if(slot == 46)
			{
				if(player.hasPermission(Utility.PERMBYPASSRANDOM))
				{
					ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true, user.getSettingLevel());
					inventory.setItem(slot, is);
				}				
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, false, user.getSettingLevel());
				inventory.setItem(slot, is);
			}
		}
		user.setMode(Mode.OPTIONGUI);
		PluginUserHandler.addUser(user);
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void openDcGuiNumPad(Player player, PluginUser user, DistributionChest dc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.DC_NUMPAD;
		/*
		 * Slot 4 Alle Infos zur Dc Prioritätzahl
		 * Slot 13 Num C
		 * Slot 14 Negativieren
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.DcTitle")
							.replace("%id%", String.valueOf(dc.getId()))
							.replace("%name%", dc.getChestName())));
		}
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, false, user.getSettingLevel());
				if(is != null)
				{
					inventory.setItem(slot, is);
				}
			}
		}
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void switchDc_Main(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		if(dc == null)
		{
			return;
		}
		final Location loc = player.getLocation();
		switch(slot)
		{
		default:
			break;
		case 0:
			guiSound(loc);
			if(user.getSettingLevel() == SettingLevel.BASE)
			{
				user.setSettingLevel(SettingLevel.EXPERT);
			} else
			{
				user.setSettingLevel(SettingLevel.BASE);
			}
			PluginUserHandler.addUser(user);
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		case 10:
			guiSound(loc);
			player.closeInventory();
			player.spigot().sendMessage(ChatApi.clickEvent(
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Dc.Chestname"),
					Action.SUGGEST_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.DC_CHESTNAME)));
			break;
		case 16:
			guiSound(loc);
			if(dc.isNormalPriority())
			{
				dc.setNormalPriority(false);
			} else
			{
				dc.setNormalPriority(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		case 25:
			guiSound(loc);
			if(dc.getPriorityType() == PriorityType.SWITCH)
			{
				dc.setPriorityType(PriorityType.PLACE);
			} else
			{
				dc.setPriorityType(PriorityType.SWITCH);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		case 28:
			guiSound(loc);
			if(!dc.getOwneruuid().equals(player.getUniqueId().toString()))
			{
				return;
			}
			if(dc.isAutomaticDistribution())
			{
				dc.setAutomaticDistribution(false);
			} else
			{
				final int count = AdvancedStoreHouse.getPlugin().getMysqlHandler().getCount(Type.DISTRIBUTIONCHEST, "`id`",
						"`owner_uuid` = ? AND `automaticdistribution` = ?", player.getUniqueId().toString(), true);
				int check = 0;
				for(int i = 500; i > 0; i--)
				{
					if(player.hasPermission(AdvancedStoreHouse.getPlugin().getYamlHandler()
							.getLimits().getString("AutomaticDistributionChestLimitPermission")+i))
					{
						check = i;
						break;
					}
				}
				if(count >= check)
				{
					player.closeInventory();
					player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang()
							.getString("CmdAsh.Limit.AutomaticLimit")
							.replace("%amount%", String.valueOf(count))
							.replace("%limit%", String.valueOf(check))));
					return;
				}
				dc.setAutomaticDistribution(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		case 34:
			guiSound(loc);
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 46:
			guiSound(loc);
			if(player.hasPermission(Utility.PERMBYPASSRANDOM))
			{
				if(dc.isDistributeRandom())
				{
					dc.setDistributeRandom(false);
				} else
				{
					dc.setDistributeRandom(true);
				}
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
				openDcGuiMain(player, user, dc, event.getClickedInventory());
			}
			break;
		case 52:
			guiSound(loc);
			player.closeInventory();
			player.spigot().sendMessage(ChatApi.clickEvent(
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Dc.Member"),
					Action.SUGGEST_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.DC_MEMBER)));
			break;
		}
	}
	
	public void switchDc_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		final Location loc = player.getLocation();
		/*
		 * Slot 4 Alle Infos zur Dc Prioritätzahl
		 * Slot 13 Num C
		 * Slot 14 Negativieren
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		switch(slot)
		{
		default:
			break;
		case 13:
			guiSound(loc);
			dc.setPriorityNumber(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 14:
			guiSound(loc);
			dc.setPriorityNumber(dc.getPriorityNumber()*-1);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 49:
			guiSound(loc);
			if(dc.getPriorityNumber() != 0)
			{
				dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
				openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			}
			break;
		case 39:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 40:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 41:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 30:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 31:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 32:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 21:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 22:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 23:
			guiSound(loc);
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 53:
			guiSound(loc);
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		}
	}
	
	public void openScGuiMain(Player player, PluginUser user, StorageChest sc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.SC_MAIN;
		/*
		 * Slot 4 Sc info
		 * Slot 10 Chestname ändern
		 * Slot 19 Endstorage boolean
		 * Slot 16 Prioritätnumber Numpad {Experte}
		 * Slot 25 Void Option {Experte}
		 * Slot 29 Durability Option {Experte}
		 * Slot 38 Durability Numpad {Experte}
		 * Slot 33 Repair Option {Experte}
		 * Slot 42 Repaircost Numpad {Experte}
		 * Slot 39 Enchantment Option {Experte}
		 * Slot 48 Material Option {Experte}
		 * Slot 41 FilterSet der Kiste aufrufen
		 * Slot 50 ItemFilterSet überschreiben
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.ScTitle")
							.replace("%id%", String.valueOf(sc.getId()))
							.replace("%name%", sc.getChestName())));
		}
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 0)
			{
				if(player.hasPermission(Utility.PERMBYPASSEXPERTMODUS))
				{
					ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false, user.getSettingLevel());
					inventory.setItem(slot, is);
				}
			} else if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false, user.getSettingLevel());
				if(is != null)
				{
					inventory.setItem(slot, is);
				}
			}
		}
		user.setMode(Mode.OPTIONGUI);
		PluginUserHandler.addUser(user);
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void switchSc_Main(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		if(sc == null)
		{
			return;
		}
		final Location loc = player.getLocation();
		switch(slot)
		{
		default:
			break;
		case 0:
			guiSound(loc);
			if(user.getSettingLevel() == SettingLevel.BASE)
			{
				user.setSettingLevel(SettingLevel.EXPERT);
			} else
			{
				user.setSettingLevel(SettingLevel.BASE);
			}
			PluginUserHandler.addUser(user);
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 11:
			guiSound(loc);
			player.closeInventory();
			player.spigot().sendMessage(ChatApi.clickEvent(
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.Chestname"),
					Action.SUGGEST_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.SC_CHESTNAME)));
			break;
		case 12:
			guiSound(loc);
			if(sc.isEndstorage())
			{
				sc.setEndstorage(false);
			} else
			{
				sc.setEndstorage(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 14:
			guiSound(loc);
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 15:
			guiSound(loc);
			if(sc.isOptionVoid())
			{
				sc.setOptionVoid(false);
			} else
			{
				sc.setOptionVoid(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 19:
			guiSound(loc);
			if(sc.isOptionDurability())
			{
				sc.setOptionDurability(false);
			} else
			{
				sc.setOptionDurability(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 28:
			guiSound(loc);
			if(sc.getDurabilityType() == StorageChest.Type.LESSTHAN)
			{
				sc.setDurabilityType(StorageChest.Type.LARGERTHAN);
			} else
			{
				sc.setDurabilityType(StorageChest.Type.LESSTHAN);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 37:
			guiSound(loc);
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 25:
			guiSound(loc);
			if(sc.isOptionRepair())
			{
				sc.setOptionRepair(false);
			} else
			{
				sc.setOptionRepair(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 34:
			guiSound(loc);
			if(sc.getRepairType() == StorageChest.Type.LESSTHAN)
			{
				sc.setRepairType(StorageChest.Type.LARGERTHAN);
			} else
			{
				sc.setRepairType(StorageChest.Type.LESSTHAN);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 43:
			guiSound(loc);
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 39:
			guiSound(loc);
			if(sc.isOptionEnchantment())
			{
				sc.setOptionEnchantment(false);
			} else
			{
				sc.setOptionEnchantment(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 48:
			guiSound(loc);
			if(sc.isOptionMaterial())
			{
				sc.setOptionMaterial(false);
			} else
			{
				sc.setOptionMaterial(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			guiSound(loc);
			player.closeInventory();
			openStorageChestItemFilterSet(AdvancedStoreHouse.getPlugin(), player, user, sc.getId());
			break;
		case 53:
			guiSound(loc);
			ItemFilterSet ifs = user.getItemFilterSet();
			if(ifs == null || ifs.getContents() == null)
			{
				player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.IFSIsNull")));
				player.closeInventory();
				return;
			}
			sc.setContents(ifs.getContents());
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.IFSIsOverriden")));
			break;
		case 45:
			guiSound(loc);
			final int id = sc.getId();
			AdvancedStoreHouse.getPlugin().getMysqlHandler().deleteData(Type.STORAGECHEST, "`id` = ?", sc.getId());
			player.closeInventory();
			player.sendMessage(ChatApi.tl(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("CmdAsh.BlockBreak.DeleteSC")
					.replace("%id%", String.valueOf(id))));
			break;
		}
	}
	
	public void openScGuiPriorityNumPad(Player player, PluginUser user, StorageChest sc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.SC_PRIORITY_NUMPAD;
		/*
		 * Slot 4 Alle Infos zur sc Prioritätzahl
		 * Slot 13 Num C
		 * Slot 14 Negativieren
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = Bukkit.createInventory(null, 6*9, 
				ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.ScTitle")
						.replace("%id%", String.valueOf(sc.getId()))
						.replace("%name%", sc.getChestName())));
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false, user.getSettingLevel());
				inventory.setItem(slot, is);
			}
		}
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void switchSc_Priority_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		if(sc == null)
		{
			return;
		}
		final Location loc = player.getLocation();
		/*
		 * Slot 4 Alle Infos zur Dc Prioritätzahl
		 * Slot 13 Num C
		 * Slot 14 Negativieren
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		switch(slot)
		{
		default:
			break;
		case 13:
			guiSound(loc);
			sc.setPriorityNumber(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 14:
			guiSound(loc);
			sc.setPriorityNumber(sc.getPriorityNumber()*-1);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			guiSound(loc);
			if(sc.getPriorityNumber() != 0)
			{
				sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 21:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 22:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			guiSound(loc);
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
			guiSound(loc);
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		}
	}
	
	public void openScGuiDurabilityNumPad(Player player, PluginUser user, StorageChest sc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.SC_DURABILITY_NUMPAD;
		/*
		 * Slot 4 Alle Infos zur sc Haltbarkeitprozent
		 * Slot 13 Num C
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.ScTitle")
							.replace("%id%", String.valueOf(sc.getId()))
							.replace("%name%", sc.getChestName())));
		}
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false, user.getSettingLevel());
				inventory.setItem(slot, is);
			}
		}
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void switchSc_Durability_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		if(sc == null)
		{
			return;
		}
		final Location loc = player.getLocation();
		/*
		 * Slot 4 Alle Infos zur Sc Haltbarkeit
		 * Slot 13 Num C
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		switch(slot)
		{
		default:
			break;
		case 13:
			guiSound(loc);
			sc.setDurability(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			guiSound(loc);
			if(sc.getDurability() != 0)
			{
				sc.setDurability(addIntPercent(sc.getDurability(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 21:
			sc.setDurability(addIntPercent(sc.getDurability(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 22:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			guiSound(loc);
			sc.setDurability(addIntPercent(sc.getDurability(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
			guiSound(loc);
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		}
	}
	
	public void openScGuiRepairNumPad(Player player, PluginUser user, StorageChest sc, Inventory inv) throws IOException
	{
		AdvancedStoreHouse plugin = AdvancedStoreHouse.getPlugin();
		GuiType type = GuiType.SC_REPAIR_NUMPAD;
		/*
		 * Slot 4 Alle Infos zur sc Reparaturkosten
		 * Slot 13 Num C
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		YamlConfiguration yml = plugin.getYamlHandler().getGui(type.toString());
		if(yml == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.GuiNotExist")
					.replace("%file%", type.toString())));
			return;
		}
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.ScTitle")
							.replace("%id%", String.valueOf(sc.getId()))
							.replace("%name%", sc.getChestName())));
		}
		ItemStack air = new ItemStack(Material.AIR);
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+"."+user.getSettingLevel().getName()+".Name") == null)
			{
				inventory.setItem(slot, air);
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true, user.getSettingLevel());
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false, user.getSettingLevel());
				inventory.setItem(slot, is);
			}
		}
		if(inv == null)
		{
			player.openInventory(inventory);
		} else
		{
			for(int slot = 0; slot < 54; slot++)
			{
				inv.setItem(slot, inventory.getItem(slot));
			}
		}
	}
	
	public void switchSc_Repair_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		if(sc == null)
		{
			return;
		}
		final Location loc = player.getLocation();
		/*
		 * Slot 4 Alle Infos zur Sc Haltbarkeit
		 * Slot 13 Num C
		 * Slot 23 Num 9
		 * Slot 22 Num 8
		 * Slot 21 Num 7
		 * Slot 32 Num 6
		 * Slot 31 Num 5
		 * Slot 30 Num 4
		 * Slot 41 Num 3
		 * Slot 40 Num 2
		 * Slot 39 Num 1 
		 * Slot 49 Num 0
		 * Slot 53 Zurück
		 */
		switch(slot)
		{
		default:
			break;
		case 13:
			guiSound(loc);
			sc.setRepairCost(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			guiSound(loc);
			if(sc.getRepairCost() != 0)
			{
				sc.setRepairCost(addInt(sc.getRepairCost(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 21:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 22:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			guiSound(loc);
			sc.setRepairCost(addInt(sc.getRepairCost(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
			guiSound(loc);
			openScGuiMain(player, user, sc, event.getClickedInventory());
			break;
		}
	}
	
	public int addInt(int prio, int add)
	{
		String num = String.valueOf(prio)+add;
		int i = Integer.parseInt(num);
		return i;
	}
	
	public int addIntPercent(int prio, int add)
	{
		String num = String.valueOf(prio)+add;
		int i = Integer.parseInt(num);
		if(i > 100)
		{
			return 100;
		} else if(i < 0)
		{
			return 0;
		}
		return i;
	}
	
	public void guiSound(Location loc)
	{
		loc.getWorld().playSound(loc,
				Registry.SOUNDS.get(NamespacedKey.minecraft(AdvancedStoreHouse.getPlugin().getYamlHandler().getConfig()
						.getString("GUISound", "BLOCK_ANCIENT_DEBRIS_HIT").toLowerCase())),
				3.0F, 0.5F);
	}
	
	public void openStorageChestItemFilterSet(AdvancedStoreHouse plugin, Player player, PluginUser user,
			int storagechestID)
			throws IOException
	{
		StorageChest sc = (StorageChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.STORAGECHEST, 
				"`id` = ?",	storagechestID);
		if(sc == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDontExist")));
			return;
		}
		if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.DISTRIBUTIONCHEST, "`id` = ?", sc.getDistributionChestID()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Interact.Base.ScDcDontExist")));
			return;
		}
		DistributionChest dc = (DistributionChest) plugin.getMysqlHandler().getData(MysqlHandler.Type.DISTRIBUTIONCHEST,
				"`id` = ?", sc.getDistributionChestID());
		String name = "/";
		String id = "/";
		if(dc != null)
		{
			if(!ChestHandler.isMember(player, dc) && !dc.getOwneruuid().equals(player.getUniqueId().toString())
					&& !player.hasPermission(Utility.PERMBYPASSSELECT))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NotOwnerOrMember")));
				return;
			}
			name = dc.getChestName();
			id = ""+dc.getId();
		}
		user.setMode(PluginUser.Mode.UPDATESTORAGEITEMFILTERSET);
		user.setStorageChestID(sc.getId());
		PluginUserHandler.addUser(user);
		Inventory inv = Bukkit.createInventory(null, 6*9, 
				ChatApi.tl(plugin.getYamlHandler().getLang().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriorityNumber()))
				.replace("%e%", ItemGenerator.getColor(sc.isEndstorage()))
				.replace("%dcname%", name)
				.replace("%dcid%", id)
				.replace("%scname%", sc.getChestName())
				.replace("%scid%", String.valueOf(sc.getId()))));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}
}
