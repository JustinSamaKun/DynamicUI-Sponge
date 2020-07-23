package net.eterniamc.dynamicui;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

public interface ClickListener {

    /** The event is by default cancelled. Can be renewed via the callback */
    void onClick(Player player, ClickAction event);
}
