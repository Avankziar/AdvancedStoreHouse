package main.java.me.avankziar.spigot.ash.cmd.tree;

import java.util.ArrayList;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;

public class CommandConstructor extends BaseConstructor
{
    public ArrayList<ArgumentConstructor> subcommands;
    public ArrayList<String> tablist;

	public CommandConstructor(String path, boolean canConsoleAccess,
    		ArgumentConstructor...argumentConstructors)
    {
		super(AdvancedStoreHouse.getPlugin().getYamlHandler().getCom().getString(path+".Name"),
				path,
				AdvancedStoreHouse.getPlugin().getYamlHandler().getCom().getString(path+".Permission"),
				AdvancedStoreHouse.getPlugin().getYamlHandler().getCom().getString(path+".Suggestion"),
				AdvancedStoreHouse.getPlugin().getYamlHandler().getCom().getString(path+".CommandString"),
				AdvancedStoreHouse.getPlugin().getYamlHandler().getCom().getString(path+".HelpInfo"),
				canConsoleAccess);
        this.subcommands = new ArrayList<>();
        this.tablist = new ArrayList<>();
        for(ArgumentConstructor ac : argumentConstructors)
        {
        	this.subcommands.add(ac);
        	this.tablist.add(ac.getName());
        }
        AdvancedStoreHouse.getPlugin().getCommandTree().add(this);
    }
}