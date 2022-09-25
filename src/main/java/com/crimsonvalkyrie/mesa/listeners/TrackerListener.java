package com.crimsonvalkyrie.mesa.listeners;

import com.crimsonvalkyrie.mesa.misc.TrackerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.crimsonvalkyrie.mesa.misc.TrackerUtils.TRACKER_EYE_LORE;
import static com.crimsonvalkyrie.mesa.misc.TrackerUtils.TRACK_FAILED_MESSAGE;

public class TrackerListener implements Listener
{
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event)
	{
		if(event.getEntityType() == EntityType.ENDER_SIGNAL)
		{
			EnderSignal signal = (EnderSignal) event.getEntity();
			ItemStack stack = signal.getItem();

			if(stack.getType() == Material.ENDER_EYE && stack.lore() != null)
			{
				List<Component> lore = stack.lore();
				if(lore.size() == 2)
				{
					if(lore.get(0).equals(TRACKER_EYE_LORE))
					{
						String playerName = PlainTextComponentSerializer.plainText().serialize(lore.get(1)).replaceAll("Tracking: ", "");
						Player playerToTrack = Bukkit.getPlayerExact(playerName);

						if(playerToTrack != null && playerToTrack.isOnline() && playerToTrack.getWorld().equals(signal.getWorld()))
						{
							signal.setTargetLocation(playerToTrack.getLocation().clone().add(0, 1, 0));
							switch(TrackerUtils.getDropType())
							{
								case 1 -> signal.setDropItem(true);
								case 2 -> signal.setDropItem(false);
							}
						}
						else
						{
							signal.setTargetLocation(signal.getLocation().clone().add(0, 1, 0));
							signal.setDropItem(true);
							signal.getLocation().getNearbyPlayers(5).forEach(player -> player.sendMessage(TRACK_FAILED_MESSAGE));
						}
					}
				}
			}
		}
	}
}
