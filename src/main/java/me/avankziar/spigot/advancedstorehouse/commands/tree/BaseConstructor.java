package main.java.me.avankziar.spigot.advancedstorehouse.commands.tree;

public class BaseConstructor
{
	private String name;
	private String path;
	private String permission;
	private String suggestion;
	
	public BaseConstructor(String name, String path, String permission, String suggestion)
	{
		setName(name);
		setPath(path);
		setPermission(permission);
		setSuggestion(suggestion);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getPermission()
	{
		return permission;
	}

	public void setPermission(String permission)
	{
		this.permission = permission;
	}

	public String getSuggestion()
	{
		return suggestion;
	}

	public void setSuggestion(String suggestion)
	{
		this.suggestion = suggestion;
	}

}
