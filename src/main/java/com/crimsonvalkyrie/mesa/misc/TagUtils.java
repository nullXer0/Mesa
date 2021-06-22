package com.crimsonvalkyrie.mesa.misc;

import com.crimsonvalkyrie.mesa.Mesa;
import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class TagUtils
{
	private static final int PRIORITY = 1000;

	private static final String PERMISSION_PREFIX = "mesa.tag.";
	private static final String ADMIN_PERMISSION = "mesa.admin";

	private static HashMap<TagType, HashMap<String, String>> tagMap;
	private static HashMap<TagType, HashSet<String>> variantMap;

	private static final String TAGS = "tags";
	private static final String TITLES = "titles";
	private static final String PREFIXES = "prefixes";
	private static final String SUFFIXES = "suffixes";

	private static final String DOT = ".";
	private static final String DOT_DEFAULT = ".default";
	private static final String DOT_VARIANTS = ".variants";

	public static List<String> getPermissibleTagCodesOfType(Player player, TagType type)
	{
		HashMap<String, String> map = tagMap.get(type);

		if(player.hasPermission(ADMIN_PERMISSION))
		{
			return new ArrayList<>(map.keySet());
		}

		String permPrefix = PERMISSION_PREFIX;
		switch(type)
		{
			case TITLE -> permPrefix += TITLES + DOT;
			case PREFIX -> permPrefix += PREFIXES + DOT;
			case SUFFIX -> permPrefix += SUFFIXES + DOT;
		}

		ArrayList<String> tagCodeList = new ArrayList<>();

		String finalPermPrefix = permPrefix;
		map.keySet().forEach(key ->
		{
			Mesa.getPlugin().getLogger().info("Checking to see if " + player.getName() + " has permission: " + finalPermPrefix + key);
			if(player.hasPermission(finalPermPrefix + key))
			{
				tagCodeList.add(key);
			}
		});

		return tagCodeList;
	}

	public static String getTagOfPlayer(Player player, TagType type)
	{
		if(type == TagType.TITLE)
		{
			return TABAPI.getPlayer(player.getUniqueId()).getOriginalValue(EnumProperty.ABOVENAME);
		}
		else
		{
			@SuppressWarnings("ConstantConditions")
			CachedMetaData metaData = Mesa.getLuckPerms().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData();
			if(type == TagType.PREFIX)
			{
				return metaData.getPrefix();
			}
			else
			{
				return metaData.getSuffix();
			}
		}
	}

	public static boolean setTag(Player player, TagType type, String tagCode)
	{
		String permPrefix = PERMISSION_PREFIX;
		switch(type)
		{
			case TITLE -> permPrefix += TITLES + DOT;
			case PREFIX -> permPrefix += PREFIXES + DOT;
			case SUFFIX -> permPrefix += SUFFIXES + DOT;
		}

		if(player.hasPermission(permPrefix + tagCode) || player.hasPermission(ADMIN_PERMISSION))
		{
			UUID uuid = player.getUniqueId();
			String tag = tagMap.get(type).get(tagCode);
			if(tag == null)
			{
				Mesa.getPlugin().getLogger().info(player.getName() + " attempted to set an invalid " + type.toString().toLowerCase() + ": " + tagCode);
				return false;
			}

			if(type == TagType.TITLE)
			{
				Mesa.getPlugin().getLogger().info("Setting title for " + player.getName() + ": " + tagCode);
				TABAPI.getPlayer(uuid).setValuePermanently(EnumProperty.ABOVENAME, tag);
			}
			else
			{
				UserManager userManager = Mesa.getLuckPerms().getUserManager();

				userManager.modifyUser(uuid, user ->
				{
					Node node;
					//noinspection rawtypes
					NodeType nodeType;

					//Create the node and set node type
					if(type == TagType.PREFIX)
					{
						node = PrefixNode.builder(tag, PRIORITY).build();
						nodeType = NodeType.PREFIX;
					}
					else
					{
						node = SuffixNode.builder(tag, PRIORITY).build();
						nodeType = NodeType.SUFFIX;

					}

					//Clear previous prefix/suffix
					user.data().clear(nodeType::matches);

					//Add the node to the user
					user.data().add(node);
				});
			}
			return true;
		}
		return false;
	}

	public static void clearTag(Player player, TagType type)
	{
		UUID uuid = player.getUniqueId();

		if(type == TagType.TITLE)
		{
			Mesa.getPlugin().getLogger().info("Clearing title for " + player.getName());
			TABAPI.getPlayer(uuid).setValuePermanently(EnumProperty.ABOVENAME, "");
		}
		else
		{
			UserManager userManager = Mesa.getLuckPerms().getUserManager();

			userManager.modifyUser(uuid, user ->
			{
				//noinspection rawtypes
				NodeType nodeType = type == TagType.PREFIX ? NodeType.PREFIX : NodeType.SUFFIX;

				//Clear previous prefix/suffix
				user.data().clear(nodeType::matches);
			});
		}
	}

	public static void loadTagsFromConfig()
	{
		tagMap = new HashMap<>();
		variantMap = new HashMap<>();

		ConfigurationSection tagsSection = Mesa.getPlugin().getConfig().getConfigurationSection(TAGS);

		@SuppressWarnings("ConstantConditions")
		ConfigurationSection titleSection = tagsSection.getConfigurationSection(TITLES);
		if(titleSection != null)
		{
			loadTagsOfType(TagType.TITLE, titleSection);
		}
		else
		{
			Mesa.getPlugin().getLogger().severe("Titles section is empty! Attempting to set titles might break");
		}

		ConfigurationSection prefixSection = tagsSection.getConfigurationSection(PREFIXES);
		if(prefixSection != null)
		{
			loadTagsOfType(TagType.PREFIX, prefixSection);
		}
		else
		{
			Mesa.getPlugin().getLogger().severe("Prefixes section is empty! Attempting to set prefixes might break");
		}

		ConfigurationSection suffixSection = tagsSection.getConfigurationSection(SUFFIXES);
		if(suffixSection != null)
		{
			loadTagsOfType(TagType.SUFFIX, suffixSection);
		}
		else
		{
			Mesa.getPlugin().getLogger().severe("Suffixes section is empty! Attempting to set suffixes might break");
		}
	}

	private static void loadTagsOfType(TagType type, ConfigurationSection configSection)
	{
		HashMap<String, String> map = new HashMap<>();
		HashSet<String> variantSet = new HashSet<>();

		Set<String> keys = configSection.getKeys(true);
		keys.forEach(key ->
		{
			if(configSection.get(key) instanceof String)
			{
				if(key.endsWith(DOT_DEFAULT))
				{
					String tagName = key.substring(0, key.lastIndexOf(DOT_DEFAULT));
					map.put(tagName, (String) configSection.get(key));
					variantSet.add(tagName);
				}
				else
				{
					map.put(key, (String) configSection.get(key));
				}
			}
			else
			{
				if(!key.endsWith(DOT_VARIANTS) && !keys.contains(key + DOT_DEFAULT))
				{
					Mesa.getPlugin().getLogger().warning("Invalid " + type.toString().toLowerCase() + " in config: " + key);
				}
			}
		});

		tagMap.put(type, map);
		variantMap.put(type, variantSet);
	}

	public static HashMap<String, String> getTagMap(TagType type)
	{
		return tagMap.get(type);
	}

	public static HashSet<String> getVariantSet(TagType type)
	{
		return variantMap.get(type);
	}

	public static boolean isVarianted(TagType type, String tag)
	{
		return variantMap.get(type).contains(tag);
	}

	public enum TagType
	{
		TITLE,
		PREFIX,
		SUFFIX
	}
}
