package com.crimsonvalkyrie.mesa.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.crimsonvalkyrie.mesa.Mesa;
import com.crimsonvalkyrie.mesa.misc.TrackerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mesa")
@CommandPermission("mesa.reload")
@SuppressWarnings("unused")
public class MesaCommand extends BaseCommand
{
	@Subcommand("reload")
	@Description("Reload the plugin")
	public void onReload()
	{
		Mesa.reload();
		getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Plugin reloaded");
	}

	@Subcommand("giveTracker")
	@Description("Gives a player a tracker for another player")
	@CommandCompletion("@players @players @range:1-5 @nothing")
	public void onGiveTracker(CommandSender sender, String player, String playerToTrack, @Default("1") int amount)
	{
		Player playerToGive = Bukkit.getPlayerExact(player);
		if(playerToGive != null && playerToGive.isOnline())
		{
			TrackerUtils.givePlayerTracker(playerToGive, playerToTrack, amount);
		}
		else
		{
			sender.sendMessage("The given player is not online");
		}
	}
}


