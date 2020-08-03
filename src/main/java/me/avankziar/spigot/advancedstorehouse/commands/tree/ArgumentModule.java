package main.java.me.avankziar.spigot.advancedstorehouse.commands.tree;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.me.avankziar.spigot.advancedstorehouse.AdvancedStoreHouse;

public abstract class ArgumentModule
{
	public ArgumentConstructor argumentConstructor;

    public ArgumentModule(AdvancedStoreHouse plugin, ArgumentConstructor argumentConstructor)
    {
       this.argumentConstructor = argumentConstructor;
       plugin.getArgumentMap().put(argumentConstructor.getPath(), this);
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args) throws IOException;

}
