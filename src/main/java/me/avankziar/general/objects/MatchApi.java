package main.java.me.avankziar.general.objects;

import java.util.UUID;

public class MatchApi
{	
	public static boolean isBoolean(String booleansValue)
	{
		if(booleansValue.equalsIgnoreCase("true"))
		{
			return true;
		} else if(booleansValue.equalsIgnoreCase("false"))
		{
			return true;
		} else if(booleansValue.equalsIgnoreCase("0"))
		{
			return true;
		} else if(booleansValue.equalsIgnoreCase("1"))
		{
			return true;
		}
		return false;
	}
	
	public static Boolean returnBoolean(Object object)
	{
		if(object instanceof String)
		{
			String b = (String) object;
			String.valueOf(b);
		} else if(object instanceof Integer)
		{
			Integer i = (Integer) object;
			if(i == 0)
			{
				return false;
			} else if(i == 1)
			{
				return true;
			}
		}
		return null;
	}
	
	public static boolean isNumber(String numberstring)
	{
		if(numberstring == null)
		{
			return false;
		}
		if(numberstring.matches("(([0-9]+[.]?[0-9]*)|([0-9]*[.]?[0-9]+))"))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isInteger(String number)
	{
		if(number == null)
		{
			return false;
		}
		try
		{
			Integer.parseInt(number);
			return true;
		} catch (Exception e) 
		{
			return false;
		}
	}
	
	public static boolean isDouble(String number)
	{
		if(number == null)
		{
			return false;
		}
		try
		{
			Double.parseDouble(number);
			return true;
		} catch (Exception e) 
		{
			return false;
		}
	}
	
	public static boolean isUUID(String string)
	{
		try 
		{
			UUID uuid = UUID.fromString(string);
			if(uuid != null)
			{
				return true;
			}
		} catch (IllegalArgumentException e)
		{
			return false;
		}
		return false;
	}
	
	public static boolean isPositivNumber(int number)
	{
		if(number >= 0)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isPositivNumber(double number)
	{
		if(number >= 0.0)
		{
			return true;
		}
		return false;
	}
}
