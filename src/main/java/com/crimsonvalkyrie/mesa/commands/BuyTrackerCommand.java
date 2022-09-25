package com.crimsonvalkyrie.mesa.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.crimsonvalkyrie.mesa.Mesa;
import com.crimsonvalkyrie.mesa.misc.TrackerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

@CommandAlias("buytracker")
@CommandPermission("mesa.buytracker")
@SuppressWarnings("unused")
public class BuyTrackerCommand extends BaseCommand
{
	private static final TextComponent BROKE_BITCH_MESSAGE = Component.text("You do not have enough money to buy a tracker").color(NamedTextColor.RED);
	private static final TextComponent GREEDY_BITCH_MESSAGE = Component.text("You can't afford that many trackers").color(NamedTextColor.RED);
	private static final TextComponent STUPID_BITCH_MESSAGE = Component.text("You can't buy less than 1 tracker").color(NamedTextColor.RED);
	private static final TextComponent CHEAP_BITCH_MESSAGE = Component.text("You have successfully bought a tracker for ").color(NamedTextColor.GREEN);
	private static final TextComponent WEALTHY_BITCH_MESSAGE = Component.text("You have successfully bought %amount% trackers for ").color(NamedTextColor.GREEN);

	TextReplacementConfig.Builder replaceBuilder = TextReplacementConfig.builder().match("%amount%");

	@Default
	@Description("Gives a player a tracker for another player")
	@CommandCompletion("@players @range:1-5 @nothing")
	public void onBuy(Player player, String playerToTrack, @Default("1") int amount)
	{
		Economy economy = Mesa.getEconomy();

		if(amount < 1)
		{
			player.sendMessage(STUPID_BITCH_MESSAGE);
			return;
		}

		if(economy.getBalance(player) >= TrackerUtils.getPrice() * amount)
		{
			economy.withdrawPlayer(player, TrackerUtils.getPrice());
			TrackerUtils.givePlayerTracker(player, playerToTrack, amount);
			player.sendMessage(amount > 1 ? WEALTHY_BITCH_MESSAGE.replaceText(replaceBuilder.replacement(String.valueOf(amount)).build()).append(Component.text(playerToTrack))
					: CHEAP_BITCH_MESSAGE.append(Component.text(playerToTrack)));
		}
		else
		{
			player.sendMessage(amount > 1 ? GREEDY_BITCH_MESSAGE : BROKE_BITCH_MESSAGE);
		}
	}
}
