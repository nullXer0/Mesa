package com.crimsonvalkyrie.mesa.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.crimsonvalkyrie.mesa.misc.ChatUIUtils;
import com.crimsonvalkyrie.mesa.misc.TagUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

@CommandAlias("title|prefix|suffix")
@CommandPermission("mesa.tag")
@SuppressWarnings("unused")
public class TagCommand extends BaseCommand
{
	final TextComponent titleButtons = createChangeClearButtons("/title list", "/title clear");
	final TextComponent prefixButtons = createChangeClearButtons("/prefix list", "/prefix clear");
	final TextComponent suffixButtons = createChangeClearButtons("/suffix list", "/suffix clear");

	@Default
	public void onDefault(Player player)
	{
		TagUtils.TagType type = TagUtils.TagType.valueOf(getExecCommandLabel().toUpperCase());

		String currentTag = TagUtils.getTagOfPlayer(player, type);
		if(currentTag == null || currentTag.isEmpty())
		{
			player.sendMessage("You Currently don't have a " + getExecCommandLabel());
		}
		else
		{
			player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("Current " + getExecCommandLabel().toLowerCase() + ": " + PlaceholderAPI.setPlaceholders(player, currentTag)));
		}

		switch(type)
		{
			case TITLE -> player.sendMessage(titleButtons);
			case PREFIX -> player.sendMessage(prefixButtons);
			case SUFFIX -> player.sendMessage(suffixButtons);
		}
	}

	@Subcommand("list")
	public void onList(Player player, @Default("1") int page)
	{
		TagUtils.TagType type = TagUtils.TagType.valueOf(getExecCommandLabel().toUpperCase());
		player.sendMessage(ChatUIUtils.generateTagPage(player, type, page, null));
	}

	@Private
	@Subcommand("variants")
	public void onVariants(Player player, String tagCode, @Default("1") int page)
	{
		TagUtils.TagType type = TagUtils.TagType.valueOf(getExecCommandLabel().toUpperCase());
		player.sendMessage(ChatUIUtils.generateTagPage(player, type, page, tagCode));
	}

	@Private
	@Subcommand("set")
	public void onSet(Player player, String tagCode)
	{
		TagUtils.TagType type = TagUtils.TagType.valueOf(getExecCommandLabel().toUpperCase());

		if(TagUtils.setTag(player, type, tagCode))
		{
			player.sendMessage(Component.text("Your " + getExecCommandLabel() + " has been updated"));
		}
		else
		{
			player.sendMessage(Component.text("Unknown title"));
		}
	}

	@Subcommand("clear")
	public void onClear(Player player)
	{
		TagUtils.TagType type = TagUtils.TagType.valueOf(getExecCommandLabel().toUpperCase());

		TagUtils.clearTag(player, type);

		player.sendMessage("Your " + getExecCommandLabel().toLowerCase() + " has been cleared");
	}

	private TextComponent createChangeClearButtons(String changeCommand, String clearCommand)
	{
		return Component.empty().color(NamedTextColor.GREEN)
				.append(Component.text("[Change]").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, changeCommand)))
				.append(Component.text(" "))
				.append(Component.text("[Clear]").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, clearCommand)));
	}
}
