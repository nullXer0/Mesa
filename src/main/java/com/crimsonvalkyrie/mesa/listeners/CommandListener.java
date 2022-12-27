package com.crimsonvalkyrie.mesa.listeners;

import com.crimsonvalkyrie.mesa.Mesa;
import com.crimsonvalkyrie.mesa.misc.SpyStorage;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;
import java.util.UUID;

public class CommandListener implements Listener
{
	static List<String> commandBlacklist;

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		String command = event.getMessage();

		for(String bCommand : commandBlacklist)
		{
			if(command.startsWith('/' + bCommand))
			{
				return;
			}
		}

		for(String spy : SpyStorage.getSpyList())
		{
			if(!spy.equals(event.getPlayer().getUniqueId().toString()))
			{
				Player player = Bukkit.getPlayer(UUID.fromString(spy));
				if(player != null && player.isOnline())
				{
					player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "!" + ChatColor.GOLD + "] " + ChatColor.RESET
							+ event.getPlayer().getName() + " executed: " + command);
				}
			}
		}

		if(Bukkit.getPluginManager().isPluginEnabled("DiscordSRV"))
		{
			JDA jda = DiscordSRV.getPlugin().getJda();
			if(jda != null)
			{
				TextChannel channel = jda.getTextChannelById(Mesa.getPlugin().getConfig().getString("discord.channel", "0"));
				if(channel != null)
				{
					channel.sendMessage(generateMessage(event.getPlayer(), event.getMessage())).queue();
				}
			}
		}
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent event)
	{
		String command = event.getCommand();

		for(String bCommand : commandBlacklist)
		{
			if(command.startsWith(bCommand))
			{
				return;
			}
		}

		if(Bukkit.getPluginManager().isPluginEnabled("DiscordSRV"))
		{
			JDA jda = DiscordSRV.getPlugin().getJda();
			if(jda != null)
			{
				TextChannel channel = jda.getTextChannelById(Mesa.getPlugin().getConfig().getString("discord.channel", "0"));
				if(channel != null)
				{
					channel.sendMessage(generateServerCommandMessage(command)).queue();
				}
			}
		}
	}

	public static void loadBlacklist()
	{
		commandBlacklist = Mesa.getPlugin().getConfig().getStringList("commandspy-blacklist");
	}

	private Message generateMessage(Player player, String command)
	{
		Location location = player.getLocation();
		return new MessageBuilder(
				player.getName() + " has executed the following command at ")
				.append("X: ").append(location.getBlockX())
				.append(", Y: ").append(location.getBlockY())
				.append(", Z: ").append(location.getBlockZ())
				.append(", In World: ").append(location.getWorld().getName())
				.append("\n`").append(command).append("`").build();
	}

	private Message generateServerCommandMessage(String command)
	{
		return new MessageBuilder("Console has executed the following command\n`").append(command).append("`").build();
	}
}
