package com.crimsonvalkyrie.mesa.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.crimsonvalkyrie.mesa.Mesa;
import org.bukkit.ChatColor;

@CommandAlias("mesa")
public class MesaCommand extends BaseCommand
{
	@Subcommand("reload")
	@CommandPermission("mesa.reload")
	@Description("Reload the plugin")
	public void onReload()
	{
		Mesa.reload();
		getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Plugin reloaded");
	}
}
