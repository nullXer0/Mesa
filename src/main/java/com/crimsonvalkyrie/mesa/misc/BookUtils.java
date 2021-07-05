package com.crimsonvalkyrie.mesa.misc;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class BookUtils
{
	public static Book generateTagMenu(Player player, TagUtils.TagType type)
	{
		LinkedHashMap<String, String> tagMap = TagUtils.getTagMap(type);
		LinkedHashSet<String> variantedSet = TagUtils.getVariantSet(type);
		List<String> tagList = TagUtils.getPermissibleTagCodesOfType(player, type);
		Book.Builder bookBuilder = Book.builder();
		TextComponent.Builder pageBuilder = Component.text();

		int entries = 0;
		for(String s : tagList)
		{
			if(entries % 14 == 0 && entries != 0)
			{
				bookBuilder.addPage(pageBuilder.build());
				pageBuilder = Component.text();
			}

			if(!s.contains(".variants."))
			{
				TextComponent.Builder entryBuilder = LegacyComponentSerializer.legacyAmpersand().deserialize(tagMap.get(s)).toBuilder();

				if(variantedSet.contains(s))
				{
					entryBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString() + " variants " + s));
				}
				else
				{
					entryBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString() + " set " + s));
				}

				pageBuilder.append(entryBuilder.build(), Component.newline());
				entries++;
			}
		}

		if(entries % 14 != 0)
		{
			bookBuilder.addPage(pageBuilder.build());
		}

		return bookBuilder.build();
	}

	public static Book generateVariantsMenu(Player player, TagUtils.TagType type, String mainTagCode)
	{
		LinkedHashMap<String, String> tagMap = TagUtils.getTagMap(type);
		List<String> tagList = TagUtils.getPermissibleTagCodesOfType(player, type);
		Book.Builder bookBuilder = Book.builder();
		TextComponent.Builder pageBuilder = Component.text();

		int entries = 0;
		for(String s : tagList)
		{
			if(entries % 14 == 0 && entries != 0)
			{
				bookBuilder.addPage(pageBuilder.build());
				pageBuilder = Component.text();
			}

			if(s.startsWith(mainTagCode))
			{
				TextComponent.Builder entryBuilder = LegacyComponentSerializer.legacyAmpersand().deserialize(tagMap.get(s)).toBuilder();

				entryBuilder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + type.toString() + " set " + s));

				pageBuilder.append(entryBuilder.build(), Component.newline());
				entries++;
			}
		}
		bookBuilder.addPage(pageBuilder.build());

		return bookBuilder.build();
	}
}
