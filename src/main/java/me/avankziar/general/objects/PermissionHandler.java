package main.java.me.avankziar.general.objects;

import org.bukkit.entity.Player;

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
}
