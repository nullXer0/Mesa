package com.crimsonvalkyrie.mesa;

import co.aikar.commands.PaperCommandManager;
import com.crimsonvalkyrie.mesa.commands.MesaCommand;
import com.crimsonvalkyrie.mesa.commands.SpyCommand;
import com.crimsonvalkyrie.mesa.commands.TagCommand;
import com.crimsonvalkyrie.mesa.listeners.CommandListener;
import com.crimsonvalkyrie.mesa.misc.SpyStorage;
import com.crimsonvalkyrie.mesa.misc.TagUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mesa extends JavaPlugin
{
	private static Plugin plugin;
	private static LuckPerms luckPerms;
	private static PaperCommandManager commandManager;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();

		plugin = this;
		luckPerms = LuckPermsProvider.get();
		commandManager = new PaperCommandManager(this);

		TagUtils.loadTagsFromConfig();
		CommandListener.loadBlacklist();

		registerCommands();
		registerListeners();
		SpyStorage.initialize(getDataFolder());
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
	}

	public static void registerCommands()
	{
		commandManager.registerCommand(new SpyCommand());
		commandManager.registerCommand(new MesaCommand());
		commandManager.registerCommand(new TagCommand());
	}

	public static void registerListeners()
	{
		PluginManager pluginManager = plugin.getServer().getPluginManager();

		pluginManager.registerEvents(new CommandListener(), plugin);
	}

	public static Plugin getPlugin()
	{
		return plugin;
	}

	public static LuckPerms getLuckPerms()
	{
		return luckPerms;
	}

	public static PaperCommandManager getCommandManager()
	{
		return commandManager;
	}
}