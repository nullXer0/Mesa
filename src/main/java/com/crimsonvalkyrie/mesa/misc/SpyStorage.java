package com.crimsonvalkyrie.mesa.misc;

import com.crimsonvalkyrie.mesa.Mesa;
import com.google.common.base.Throwables;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpyStorage
{
	private static File configFile;

	private static FileConfiguration spyConfig;

	private static List<String> spyList;

	public static void initialize(File dataFolder)
	{
		configFile = new File(dataFolder, "spies.yml");

		load();
	}

	public static List<String> getSpyList()
	{
		return spyList;
	}

	public static void load()
	{
		spyConfig = YamlConfiguration.loadConfiguration(configFile);
		spyList = spyConfig.getStringList("spies");
	}

	public static void save()
	{
		if(spyConfig != null)
		{
			spyConfig.set("spies", spyList);
			try
			{
				spyConfig.save(configFile);
			}
			catch(IOException e)
			{
				Logger logger = Mesa.getPlugin().getLogger();
				logger.log(Level.WARNING, "Unable to save spy data file!");
				logger.log(Level.WARNING, Throwables.getStackTraceAsString(e));
			}
		}
	}
}
