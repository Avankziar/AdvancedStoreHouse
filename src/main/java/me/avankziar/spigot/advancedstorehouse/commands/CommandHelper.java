package main.java.me.avankziar.spigot.advancedstorehouse.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import main.java.me.avankziar.general.objects.ChatApi;
import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHelper
{
	private AdvancedStoreHouse plugin;
	
	public CommandHelper(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
	}
	
	private void debug(String s)
	{
		boolean boo = true;
		if(boo)
		{
			System.out.println(s);
		}
	}
	
	public void pastNextPage(Player player, String path,
			int page, boolean lastpage, String cmdstring, String...objects)
	{
		debug("player != null:"+(player != null));
		debug("player:"+player.getName());
		debug("path:"+path);
		debug("page:"+page);
		debug("lastpage:"+lastpage);
		debug("cmdString:"+cmdstring);
		for(Object o : objects)
		{
			debug("oject:"+o.toString());
		}
		if(page == 0 && lastpage)
		{
			return;
		}
		int i = page+1;
		int j = page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> pages = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			debug("page != 0");
			TextComponent msg2 = ChatApi.tctl(
					plugin.getYamlHandler().getL().getString(path+".Past"));
			String cmd = cmdstring+" "+String.valueOf(j);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			pages.add(msg2);
			debug("Exist page != 0");
		}
		if(!lastpage)
		{
			debug("!lastpage");
			TextComponent msg1 = ChatApi.tctl(
					plugin.getYamlHandler().getL().getString(path+".Next"));
			String cmd = cmdstring+" "+String.valueOf(i);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			if(pages.size()==1)
			{
				pages.add(ChatApi.tc(" | "));
			}
			pages.add(msg1);
			debug("exist lastpage");
		}
		debug("Send player message");
		MSG.setExtra(pages);	
		player.spigot().sendMessage(MSG);
		debug("Exist Methode");
	}
}