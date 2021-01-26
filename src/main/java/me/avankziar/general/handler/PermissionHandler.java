package main.java.me.avankziar.general.handler;

import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.DistributionChest;
import main.java.me.avankziar.spigot.ash.assistance.Utility;

public class PermissionHandler
{
	public static boolean canCreate(Player player, String bypassPerm, String permBegin,
			int atTimeAmount, int maxAmount, boolean canEqual)
	{
		if(!player.hasPermission(bypassPerm))
		{
			for(int i = maxAmount; i >= 0; i--)
			{
				if(player.hasPermission(permBegin+i))
				{
					if(i > atTimeAmount)
					{
						return true;
					} else if(canEqual && i == atTimeAmount)
					{
						return true;
					} else
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean canReposition(Player player, DistributionChest dc)
	{
		if(!player.hasPermission(Utility.PERMBYPASSREPOSITION)
				&& !dc.getOwneruuid().equalsIgnoreCase(player.getUniqueId().toString())
				&& !dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return false;
		}
		return true;
	}
	
	public static boolean canOpenOption(Player player, DistributionChest dc)
	{
		if(!player.hasPermission(Utility.PERMBYPASSOPENOPTION)
				&& !dc.getOwneruuid().equalsIgnoreCase(player.getUniqueId().toString())
				&& !dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return false;
		}
		return true;
	}
	
	public static boolean canViewIFSOrVisual(Player player, DistributionChest dc)
	{
		if(!player.hasPermission(Utility.PERMBYPASSIFSORVISUAL)
				&& !dc.getOwneruuid().equalsIgnoreCase(player.getUniqueId().toString())
				&& !dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return false;
		}
		return true;
	}
	
	public static boolean canSelect(Player player, DistributionChest dc)
	{
		if(!player.hasPermission(Utility.PERMBYPASSSELECT)
				&& !dc.getOwneruuid().equalsIgnoreCase(player.getUniqueId().toString())
				&& !dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return false;
		}
		return true;
	}
	
	public static boolean canCopyAndPaste(Player player, DistributionChest dc)
	{
		if(!player.hasPermission(Utility.PERMBYPASSCOPYANDPASTE)
				&& !dc.getOwneruuid().equalsIgnoreCase(player.getUniqueId().toString())
				&& !dc.getMemberList().contains(player.getUniqueId().toString()))
		{
			return false;
		}
		return true;
	}
}
