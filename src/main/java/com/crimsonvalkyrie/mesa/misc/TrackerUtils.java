package com.crimsonvalkyrie.mesa.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TrackerUtils
{
	public static final ItemStack TRACKER_EYE = new ItemStack(Material.ENDER_EYE);
	public static final Component TRACKER_EYE_LORE = Component.text("Throw to show direction of target.");

	static
	{
		ItemMeta meta = TRACKER_EYE.getItemMeta();
		meta.displayName(Component.text("Player Locator").decoration(TextDecoration.ITALIC, false));
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		TRACKER_EYE.setItemMeta(meta);
		TRACKER_EYE.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		TRACKER_EYE.lore(Collections.singletonList(TRACKER_EYE_LORE));
	}

	public static final Component TRACK_FAILED_MESSAGE = Component.text("Unable to locate player. They may be offline or in another world.");

	private static int dropType;
	private static double price;

	public static void givePlayerTracker(Player playerToGive, String playerToTrack, int amount)
	{
		ItemStack stack = TRACKER_EYE.clone();
		List<Component> lore = stack.lore();
		//noinspection DataFlowIssue
		lore.add(Component.text("Tracking: " + playerToTrack));
		stack.lore(lore);
		stack.setAmount(amount);
		HashMap<Integer, ItemStack> remainingItems = playerToGive.getInventory().addItem(stack);
		if(!remainingItems.isEmpty())
		{
			remainingItems.values().forEach(remainingItem -> playerToGive.getWorld().dropItem(playerToGive.getLocation().add(0, 1, 0), remainingItem));
		}
	}

	public static int getDropType()
	{
		return dropType;
	}

	public static void setDropType(int dropType)
	{
		TrackerUtils.dropType = dropType;
	}

	public static double getPrice()
	{
		return price;
	}

	public static void setPrice(double price)
	{
		TrackerUtils.price = price;
	}
}
