package main.java.me.avankziar.spigot.ash.eventhandler;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
		 * Slot 16 Priorität switchen
		 * Slot 25 Prorität umschalten (Zwischen Switch oder Place)
		 * Slot 28 Automatische Verteilung ein oder ausschalten
		 * Slot 34 Priorität setzten (Taschenrechner like)
		 * Slot 46 Random ein- auschalten
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
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true);
				inventory.setItem(slot, is);
			} else if(slot == 46)
			{
				if(player.hasPermission(Utility.PERMBYPASSRANDOM))
				{
					ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true);
					inventory.setItem(slot, is);
				}				
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
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
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, true);
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, dc, null, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
		}
	}
	
	public void switchDc_Main(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final DistributionChest dc = (DistributionChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.DISTRIBUTIONCHEST, "`id` = ?", user.getDistributionChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		switch(slot)
		{
		default:
			break;
		case 10:
			player.closeInventory();
			player.spigot().sendMessage(ChatApi.clickEvent(
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Dc.Chestname"),
					Action.SUGGEST_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.DC_CHESTNAME)));
			break;
		case 16:
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
			if(dc.isAutomaticDistribution())
			{
				dc.setAutomaticDistribution(false);
			} else
			{
				dc.setAutomaticDistribution(true);
			}
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiMain(player, user, dc, event.getClickedInventory());
			break;
		case 34:
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 46:
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
			dc.setPriorityNumber(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 14:
			dc.setPriorityNumber(dc.getPriorityNumber()*-1);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 49:
			if(dc.getPriorityNumber() != 0)
			{
				dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
				openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			}
			break;
		case 39:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 40:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 41:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 30:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 31:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 32:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 21:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 22:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 23:
			dc.setPriorityNumber(addInt(dc.getPriorityNumber(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.DISTRIBUTIONCHEST, dc, "`id` = ?", dc.getId());
			openDcGuiNumPad(player, user, dc, event.getClickedInventory());
			break;
		case 53:
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
		 * Slot 16 Prioritätnumber Numpad
		 * Slot 25 Void Option
		 * Slot 29 Durability Option
		 * Slot 38 Durability Numpad
		 * Slot 33 Repair Option
		 * Slot 42 Repaircost Numpad
		 * Slot 39 Enchantment Option
		 * Slot 48 Material Option
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
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true);
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
		}
	}
	
	public void switchSc_Main(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
		event.setCancelled(true);
		event.setResult(Result.DENY);
		switch(slot)
		{
		default:
			break;
		case 11:
			player.closeInventory();
			player.spigot().sendMessage(ChatApi.clickEvent(
					AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.Chestname"),
					Action.SUGGEST_COMMAND, 
					PluginSettings.settings.getCommands().get(KeyHandler.SC_CHESTNAME)));
			break;
		case 12:
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
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 15:
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
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 25:
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
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 39:
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
			player.closeInventory();
			openStorageChestItemFilterSet(AdvancedStoreHouse.getPlugin(), player, user, sc.getId());
			break;
		case 50:
			ItemFilterSet ifs = user.getItemFilterSet();
			if(ifs == null || ifs.getContents() == null)
			{
				player.sendMessage(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.IFSIsNull"));
				player.closeInventory();
				return;
			}
			sc.setContents(ifs.getContents());
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			player.sendMessage(AdvancedStoreHouse.getPlugin().getYamlHandler().getLang().getString("Gui.Sc.IFSIsOverriden"));
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
		Inventory inventory = inv;
		if(inventory == null)
		{
			inventory = Bukkit.createInventory(null, 6*9, 
					ChatApi.tl(plugin.getYamlHandler().getLang().getString("Gui.Base.ScTitle")
							.replace("%id%", String.valueOf(sc.getId()))
							.replace("%name%", sc.getChestName())));
		}
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true);
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
		}
	}
	
	public void switchSc_Priority_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
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
			sc.setPriorityNumber(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 14:
			sc.setPriorityNumber(sc.getPriorityNumber()*-1);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			if(sc.getPriorityNumber() != 0)
			{
				sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 21:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 22:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			sc.setPriorityNumber(addInt(sc.getPriorityNumber(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiPriorityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
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
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true);
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
		}
	}
	
	public void switchSc_Durability_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
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
			sc.setDurability(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			if(sc.getDurability() != 0)
			{
				sc.setDurability(addIntPercent(sc.getDurability(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			sc.setDurability(addIntPercent(sc.getDurability(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			sc.setDurability(addIntPercent(sc.getDurability(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			sc.setDurability(addIntPercent(sc.getDurability(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			sc.setDurability(addIntPercent(sc.getDurability(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			sc.setDurability(addIntPercent(sc.getDurability(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
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
			sc.setDurability(addIntPercent(sc.getDurability(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			sc.setDurability(addIntPercent(sc.getDurability(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiDurabilityNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
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
		for(int slot = 0; slot < 54; slot++)
		{
			if(yml.getString(slot+".Name") == null)
			{
				continue;
			}
			if(slot == 4)
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, true);
				inventory.setItem(slot, is);
			} else
			{
				ItemStack is = ItemGenerator.create(String.valueOf(slot), yml, 1, type, null, sc, false);
				inventory.setItem(slot, is);
			}
		}
		if(player.getOpenInventory() == null)
		{
			player.openInventory(inv);
		}
	}
	
	public void switchSc_Repair_Numpad(InventoryClickEvent event, Player player, PluginUser user, int slot) throws IOException
	{
		final StorageChest sc = (StorageChest) AdvancedStoreHouse.getPlugin().getMysqlHandler().getData(
				Type.STORAGECHEST, "`id` = ?", user.getStorageChestID());
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
			sc.setRepairCost(0);
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 49:
			if(sc.getRepairCost() != 0)
			{
				sc.setRepairCost(addInt(sc.getRepairCost(), 0));
				AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
				openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			}
			break;
		case 39:
			sc.setRepairCost(addInt(sc.getRepairCost(), 1));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 40:
			sc.setRepairCost(addInt(sc.getRepairCost(), 2));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 41:
			sc.setRepairCost(addInt(sc.getRepairCost(), 3));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 30:
			sc.setRepairCost(addInt(sc.getRepairCost(), 4));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 31:
			sc.setRepairCost(addInt(sc.getRepairCost(), 5));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 32:
			sc.setRepairCost(addInt(sc.getRepairCost(), 6));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 21:
			sc.setRepairCost(addInt(sc.getRepairCost(), 7));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 22:
			sc.setRepairCost(addInt(sc.getRepairCost(), 8));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 23:
			sc.setRepairCost(addInt(sc.getRepairCost(), 9));
			AdvancedStoreHouse.getPlugin().getMysqlHandler().updateData(Type.STORAGECHEST, sc, "`id` = ?", sc.getId());
			openScGuiRepairNumPad(player, user, sc, event.getClickedInventory());
			break;
		case 53:
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
				plugin.getYamlHandler().getLang().getString("GUI", "StorageChest GUI ID: &c%id% &bP:%p% &f| %dcid% %name%")
				.replace("%p%", String.valueOf(sc.getPriorityNumber()))
				.replace("%name%", name)
				.replace("%dcid%", id)
				.replace("%id%", String.valueOf(sc.getId())));
		inv.setContents(sc.getContents());
		player.openInventory(inv);
		return;
	}
}
