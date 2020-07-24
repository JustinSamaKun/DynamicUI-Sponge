package net.eterniamc.dynamicui.sponge;

import org.spongepowered.api.entity.living.player.Player;

public interface ClickListener {

    /** The event is by default cancelled. Can be renewed via the callback */
    void onClick(Player player, ClickAction event);
}
