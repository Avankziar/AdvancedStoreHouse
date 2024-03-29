package main.java.me.avankziar.spigot.ash.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.spigot.ash.AdvancedStoreHouse;
import main.java.me.avankziar.spigot.ash.database.Language.ISO639_2B;

public class YamlHandler
{
	private AdvancedStoreHouse plugin;
	private File config = null;
	private YamlConfiguration cfg = new YamlConfiguration();
	
	private File commands = null;
	private YamlConfiguration com = new YamlConfiguration();
	
	private String languages;
	private File language = null;
	private YamlConfiguration lang = new YamlConfiguration();
	
	private File limits = null;
	private YamlConfiguration lim = new YamlConfiguration();
	
	private LinkedHashMap<String, File> guifiles = new LinkedHashMap<>();
	private LinkedHashMap<String, YamlConfiguration> gui = new LinkedHashMap<>();

	public YamlHandler(AdvancedStoreHouse plugin)
	{
		this.plugin = plugin;
		loadYamlHandler();
	}
	
	public YamlConfiguration getConfig()
	{
		return cfg;
	}
	
	public YamlConfiguration getCom()
	{
		return com;
	}
	
	public YamlConfiguration getLang()
	{
		return lang;
	}
	
	public YamlConfiguration getGui(String guitype)
	{
		return gui.get(guitype);
	}
	
	public YamlConfiguration getLimits()
	{
		return lim;
	}
	
	public boolean loadYamlHandler()
	{
		if(!mkdirStaticFiles())
		{
			return false;
		}
		
		if(!mkdirDynamicFiles()) //Per language one file
		{
			return false;
		}
		return true;
	}
	
	public boolean mkdirStaticFiles()
	{
		//Erstellen aller Werte FÜR die Config.yml
		plugin.setYamlManager(new YamlManager());
		
		File directory = new File(plugin.getDataFolder()+"");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		
		//Initialisierung der config.yml
		config = new File(plugin.getDataFolder(), "config.yml");
		if(!config.exists()) 
		{
			AdvancedStoreHouse.log.info("Create config.yml...");
			try(InputStream in = plugin.getResource("default.yml"))
			{
				//Erstellung einer "leere" config.yml
				Files.copy(in, config.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		//Laden der config.yml
		cfg = loadYamlTask(config, cfg);
        if(cfg == null)
        {
        	return false;
        }
		
		//Niederschreiben aller Werte für die Datei
		writeFile(config, cfg, plugin.getYamlManager().getConfigKey());
		
		languages = plugin.getAdministration() == null 
				? cfg.getString("Language", "ENG").toUpperCase() 
				: plugin.getAdministration().getLanguage();
		
		commands = new File(plugin.getDataFolder(), "commands.yml");
		if(!commands.exists()) 
		{
			AdvancedStoreHouse.log.info("Create commands.yml...");
			try(InputStream in = plugin.getResource("default.yml"))
			{
				//Erstellung einer "leere" config.yml
				Files.copy(in, commands.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		com = loadYamlTask(commands, com);
        if(com == null)
        {
        	return false;
        }
		writeFile(commands, com, plugin.getYamlManager().getCommandsKey());
		
		limits = new File(plugin.getDataFolder(), "limits.yml");
		if(!limits.exists()) 
		{
			AdvancedStoreHouse.log.info("Create limits.yml...");
			try(InputStream in = plugin.getResource("default.yml"))
			{
				//Erstellung einer "leere" config.yml
				Files.copy(in, limits.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		lim = loadYamlTask(limits, lim);
        if(lim == null)
        {
        	return false;
        }
		writeFile(limits, lim, plugin.getYamlManager().getLimitsKey());
		return true;
	}
	
	private boolean mkdirDynamicFiles()
	{
		//Vergleich der Sprachen
		List<Language.ISO639_2B> types = new ArrayList<Language.ISO639_2B>(EnumSet.allOf(Language.ISO639_2B.class));
		ISO639_2B languageType = ISO639_2B.ENG;
		for(ISO639_2B type : types)
		{
			if(type.toString().equals(languages))
			{
				languageType = type;
				break;
			}
		}
		//Setzen der Sprache
		plugin.getYamlManager().setLanguageType(languageType);
		
		if(!mkdirLanguage())
		{
			return false;
		}
		
		if(!mkdirGuis())
		{
			return false;
		}
		return true;
	}
	
	private boolean mkdirLanguage()
	{
		String languageString = plugin.getYamlManager().getLanguageType().toString().toLowerCase();
		File directory = new File(plugin.getDataFolder()+"/Languages/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		language = new File(directory.getPath(), languageString+".yml");
		if(!language.exists()) 
		{
			AdvancedStoreHouse.log.info("Create %lang%.yml...".replace("%lang%", languageString));
			try(InputStream in = plugin.getResource("default.yml"))
			{
				Files.copy(in, language.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		//Laden der Datei
		lang = loadYamlTask(language, lang);
        if(lang == null)
        {
        	return false;
        }
		//Niederschreiben aller Werte in die Datei
		writeFile(language, lang, plugin.getYamlManager().getLanguageKey());
		return true;
	}
	
	private boolean mkdirGuis()
	{
		File directory = new File(plugin.getDataFolder()+"/Guis/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		List<String> guilist = getConfig().getStringList("GuiList");
		if(guilist == null || guilist.isEmpty())
		{
			return false;
		}
		for(String g : guilist)
		{
			if(guifiles.containsKey(g))
			{
				guifiles.remove(g);
			}
			File guifile = new File(directory.getPath(), g+".yml");
			if(!guifile.exists()) 
			{
				AdvancedStoreHouse.log.info("Create %file%.yml...".replace("%file%", g));
				try(InputStream in = plugin.getResource("default.yml"))
				{
					Files.copy(in, guifile.toPath());
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			YamlConfiguration gyaml = new YamlConfiguration();
			//Laden der Datei
			gyaml = loadYamlTask(guifile, gyaml);
	        if(gyaml == null)
	        {
	        	return false;
	        }
			//Niederschreiben aller Werte in die Datei
			writeFile(guifile, gyaml, plugin.getYamlManager().getGuiKeys(g));
			gui.put(g, gyaml);
		}
		return true;
	}
	
	private YamlConfiguration loadYamlTask(File file, YamlConfiguration yaml)
	{
		try 
		{
			yaml.load(file);
		} catch (IOException | InvalidConfigurationException e) 
		{
			AdvancedStoreHouse.log.severe(
					"Could not load the %file% file! You need to regenerate the %file%! Error: ".replace("%file%", file.getName())
					+ e.getMessage());
			e.printStackTrace();
		}
		return yaml;
	}
	
	@SuppressWarnings("deprecation")
	private boolean writeFile(File file, YamlConfiguration yml, LinkedHashMap<String, Language> keyMap)
	{
		yml.options().header(" For more explanation see \n https://www.spigotmc.org/resources/AdvancedStoreHouse.80677/");
		for(String key : keyMap.keySet())
		{
			Language languageObject = keyMap.get(key);
			if(languageObject.languageValues.containsKey(plugin.getYamlManager().getLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getLanguageType());
			} else if(languageObject.languageValues.containsKey(plugin.getYamlManager().getDefaultLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getDefaultLanguageType());
			}
		}
		try
		{
			yml.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}
}