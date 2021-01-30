package main.java.me.avankziar.general.handler;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.general.objects.PluginUser;
import main.java.me.avankziar.general.objects.PluginUser.Mode;

public class PluginUserHandler
{
	private static ArrayList<PluginUser> userList;

	public static ArrayList<PluginUser> getUserList()
	{
		return userList;
	}

	public static void setUserList(ArrayList<PluginUser> userList)
	{
		PluginUserHandler.userList = userList;
	}
	
	public static void removeUser(PluginUser user)
	{
		PluginUser pu = null;
		for(PluginUser p : userList)
		{
			if(p.getUUID().equals(user.getUUID()))
			{
				pu = p;
				break;
			}
		}
		if(pu != null)
		{
			userList.remove(pu);
		}		
	}
	
	public static void addUser(PluginUser user)
	{
		removeUser(user);
		userList.add(user);
	}
	
	public static PluginUser getUser(UUID uuid)
	{
		PluginUser pu = null;
		for(PluginUser p : userList)
		{
			if(p.getUUID().equals(uuid.toString()))
			{
				pu = p;
				break;
			}
		}
		return pu;
	}
	
	public static void cancelAction(Player player, PluginUser user, Mode mode, String message)
	{
		switch(mode)
		{
		default:
			user.setMode(Mode.CONSTRUCT);
			addUser(user);
			player.sendMessage(ChatApi.tl(message));
			return;
		case CONSTRUCT:
			return;
		}
	}
}
