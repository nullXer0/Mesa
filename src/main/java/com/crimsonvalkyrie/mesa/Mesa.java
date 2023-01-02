package com.crimsonvalkyrie.mesa;

import co.aikar.commands.PaperCommandManager;
import com.crimsonvalkyrie.mesa.commands.BuyTrackerCommand;
import com.crimsonvalkyrie.mesa.commands.MesaCommand;
import com.crimsonvalkyrie.mesa.commands.SpyCommand;
import com.crimsonvalkyrie.mesa.commands.TagCommand;
import com.crimsonvalkyrie.mesa.listeners.CommandListener;
import com.crimsonvalkyrie.mesa.listeners.JoinListener;
import com.crimsonvalkyrie.mesa.listeners.TrackerListener;
import com.crimsonvalkyrie.mesa.misc.Database;
import com.crimsonvalkyrie.mesa.misc.SpyStorage;
import com.crimsonvalkyrie.mesa.misc.TagUtils;
import com.crimsonvalkyrie.mesa.misc.TrackerUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Mesa extends JavaPlugin
{
	private static Plugin plugin;
	private static LuckPerms luckPerms;
	private static PaperCommandManager commandManager;
	private static Economy economy;

	private static Database database;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		reloadConfig();

		plugin = this;

		try
		{
			database = new Database(this);
		}
		catch(SQLException exception)
		{
			exception.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}

		luckPerms = LuckPermsProvider.get();
		commandManager = new PaperCommandManager(this);
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp != null)
		{
			economy = rsp.getProvider();
		}

		TagUtils.loadTagsFromConfig();
		CommandListener.loadBlacklist();

		registerCommands();
		registerListeners();
		SpyStorage.initialize(getDataFolder());
		ConfigurationSection trackerSection = plugin.getConfig().getConfigurationSection("player-tracker");
		TrackerUtils.setDropType(trackerSection.getInt("drop-type", 0));
		TrackerUtils.setPrice(trackerSection.getDouble("price", 1000));
	}

	@Override
	public void onDisable()
	{
		SpyStorage.save();
	}

	public static void reload()
	{
		plugin.saveDefaultConfig();
		plugin.reloadConfig();

		TagUtils.loadTagsFromConfig();
		CommandListener.loadBlacklist();
		SpyStorage.initialize(plugin.getDataFolder());
		ConfigurationSection trackerSection = plugin.getConfig().getConfigurationSection("player-tracker");
		TrackerUtils.setDropType(trackerSection.getInt("drop-type", 0));
		TrackerUtils.setPrice(trackerSection.getDouble("price", 1000));
	}

	public static void registerCommands()
	{
		commandManager.registerCommand(new SpyCommand());
		commandManager.registerCommand(new MesaCommand());
		commandManager.registerCommand(new TagCommand());
		commandManager.registerCommand(new BuyTrackerCommand());
	}

	public static void registerListeners()
	{
		PluginManager pluginManager = plugin.getServer().getPluginManager();

		pluginManager.registerEvents(new CommandListener(), plugin);
		pluginManager.registerEvents(new TrackerListener(), plugin);
		pluginManager.registerEvents(new JoinListener(), plugin);
	}

	public static Plugin getPlugin()
	{
		return plugin;
	}

	public static LuckPerms getLuckPerms()
	{
		return luckPerms;
	}

	public static Economy getEconomy()
	{
		return economy;
	}

	public static Database getDatabase()
	{
		return database;
	}
}