package net.eterniamc.dynamicui.sponge;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum InterfaceController {
    INSTANCE;

    @Getter
    private Object plugin;

    @Getter
    private final Set<DynamicUI> activeInterfaces = Sets.newConcurrentHashSet();

    @SuppressWarnings("unused")
    public void initialize(Object plugin) {
        this.plugin = plugin;
        Sponge.getEventManager().unregisterListeners(this);
        Sponge.getEventManager().registerListeners(plugin, this);
    }


    @Listener(order = Order.FIRST)
    public void onInventoryClick(ClickInventoryEvent event, @First Player player) {
        for (DynamicUI ui : activeInterfaces) {
            Player entity = ui.getPlayer();
            if (entity.getUniqueId().equals(player.getUniqueId())) {
                // We found the interface, now cancel the event and find the button they clicked.
                event.setCancelled(true);

                // Block all shift clicks
                if (event instanceof ClickInventoryEvent.Shift) {
                    return;
                }

                if (!ui.isIntractable()) {
                    continue;
                }

                List<Slot> slots = event.getTransactions().stream()
                        .map(SlotTransaction::getSlot)
                        .collect(Collectors.toList());
                if (!slots.isEmpty()) {
                    event.setCancelled(true);
                    for (Slot slot : slots) {
                        Optional<SlotIndex> index = slot.getInventoryProperty(SlotIndex.class).filter(i -> i.getValue() != null);
                        if (index.isPresent()) {
                            System.out.println(index.get().getValue());

                            //They clicked in the player inventory
                            if (index.get().getValue() > ui.getInventory().capacity()) {
                                if (ui.getPlayerInventoryListener() != null) {
                                    ui.getPlayerInventoryListener().onClick(player, new ClickAction(event, slot));
                                    return;
                                }
                            } else {
                                //They clicked on one of the specific slot listeners
                                ClickListener listener = ui.getListeners().get(index.get().getValue());
                                if (listener != null) {
                                    listener.onClick(player, new ClickAction(event, slot));
                                    return;
                                }
                            }
                        }
                    }
                }

                return;
            }
        }
    }

    @Listener
    public void onInventoryClose(InteractInventoryEvent.Close event, @First Player player) {
        for (DynamicUI ui : getActiveInterfaces()) {
            if (ui.isValid() && ui.getPlayer().equals(player)) {
                getActiveInterfaces().remove(ui);
                if (!ui.isPreventCloseEvent() && !ui.onClose()) {
                    ui.open(player);
                }
            }
        }
    }
}
