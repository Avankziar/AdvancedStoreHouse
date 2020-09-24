package main.java.me.avankziar.general.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import main.java.me.avankziar.general.objects.MatchApi;

public class TimeHandler
{
	private final static long ss = 1000;
	private static long mm = 1000*60;
	private static long HH = 1000*60*60;
	private static long dd = 1000*60*60*24;
	//private final static long yyyy = dd*365;
	
	public static String getTime(long l)
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"));
	}
	
	public static long getTime(String l)
	{
		return LocalDateTime.parse(l, DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static String getRepeatingTime(long l) // dd-HH:mm
	{
		long ll = l;
		ChestHandler.debug("ll = "+ ll);
		String time = "";
		int d = 0;
		while(ll >= dd)
		{
			ChestHandler.debug("d++");
			ll = ll - dd;
			d++;
		}
		ChestHandler.debug("ll = "+ ll);
		time += String.valueOf(d)+"-";
		int H = 0;
		while(ll >= HH)
		{
			ChestHandler.debug("H++");
			ll = ll - HH;
			H++;
		}
		if(H < 10)
		{
			time += String.valueOf(0);
		}
		ChestHandler.debug("ll = "+ ll);
		time += String.valueOf(H)+":";
		int m = 0;
		while(ll >= mm)
		{
			ChestHandler.debug("m++");
			ll = ll - mm;
			m++;
		}
		if(m < 10)
		{
			time += String.valueOf(0);
		}
		ChestHandler.debug("ll = "+ ll);
		time += String.valueOf(m)+":";
		int s = 0;
		while(ll >= ss)
		{
			ChestHandler.debug("s++");
			ll = ll - ss;
			s++;
		}
		if(s < 10)
		{
			time += String.valueOf(0);
		}
		ChestHandler.debug("ll = "+ ll);
		time += String.valueOf(s);
		return time;
	}
	
	public static long getRepeatingTime(String l) //dd-HH:mm
	{
		String[] a = l.split("-");
		if(!MatchApi.isInteger(a[0]))
		{
			return 0;
		}
		int d = Integer.parseInt(a[0]);
		String[] b = a[1].split(":");
		if(!MatchApi.isInteger(b[0]))
		{
			return 0;
		}
		if(!MatchApi.isInteger(b[1]))
		{
			return 0;
		}
		int H = Integer.parseInt(b[0]);
		int m = Integer.parseInt(b[1]);
		long time = d*dd + H*HH + m*mm;
		return time;
	}
}
