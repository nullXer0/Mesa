package com.crimsonvalkyrie.mesa.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.crimsonvalkyrie.mesa.misc.SpyStorage;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("commandspy|cspy")
@CommandPermission("commandspy.admin")
public class SpyCommand extends BaseCommand
{
	@Default
	public void onCommand(Player player)
	{
		String uuid = player.getUniqueId().toString();
		List<String> spies = SpyStorage.getSpyList();
		if(spies.contains(uuid))
		{
			spies.remove(uuid);
			getCurrentCommandIssuer().sendMessage("You are no longer spying on player commands");
		}
		else
		{
			spies.add(uuid);
			getCurrentCommandIssuer().sendMessage("You are now spying on player commands");
		}
	}
}