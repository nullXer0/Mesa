package com.crimsonvalkyrie.mesa.misc;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;

public class ChatUIUtils
{
	public static TextComponent generateTagPage(Player player, TagUtils.TagType type, int page, String base)
	{
		LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
		TextComponent.Builder pageBuilder = Component.text();
		LinkedHashMap<String, String> tagMap;
		List<String> tagList;

		if(base == null)
		{
			tagMap = TagUtils.getBaseMap(type);
			tagList = TagUtils.getPermissibleBaseTagCodesOfType(player, type);
		}
		else
		{
			tagMap = TagUtils.getVariantMap(type).get(base);
			tagList = TagUtils.getPermissibleVariantTagCodesOfType(player, type, base);
		}

		String currentTag = TagUtils.getTagOfPlayer(player, type);
		if(currentTag == null || currentTag.isEmpty())
		{
			pageBuilder.append(Component.text("You Currently don't have a " + type.toString().toLowerCase()), Component.newline());
		}
		else
		{
			pageBuilder.append(serializer.deserialize("Current " + type.toString().toLowerCase() + ": " + PlaceholderAPI.setPlaceholders(player, currentTag)), Component.newline());
		}

		int offset = Math.max(18 * (page - 1), 0);
		for(int i = offset; i < tagList.size() && i < offset + 18; i++)
		{
			String s = tagList.get(i);

			TextComponent.Builder entryBuilder = serializer.deserialize(PlaceholderAPI.setPlaceholders(player, tagMap.get(s))).toBuilder();

			if(TagUtils.getVariantMap(type).containsKey(s))
			{
				entryBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString().toLowerCase() + " variants " + s));
			}
			else
			{
				entryBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString().toLowerCase() + " set " + s));
			}

			pageBuilder.append(entryBuilder.build(), Component.newline());
		}

		int totalPages = tagList.size() / 18;
		if(tagList.size() % 18 != 0)
		{
			totalPages++;
		}

		if(page > 1)
		{
			pageBuilder.append(Component.text("\u25C0").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString().toLowerCase() + " list " + (page - 1))), Component.space());
		}

		pageBuilder.append(Component.text(page + "/" + totalPages));

		if(page < totalPages)
		{
			pageBuilder.append(Component.space(), Component.text("\u25B6").clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString().toLowerCase() + " list " + (page + 1))));
		}

		return pageBuilder.build();
	}
}
