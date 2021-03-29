package main.java.me.avankziar.general.objects;

public enum SettingLevel
{
	BASE, EXPERT;
	
	public String getName()
	{
		switch(this)
		{
		case BASE:
			return "Base";
		case EXPERT:
			return "Expert";
		}
		return null;
	}
}
