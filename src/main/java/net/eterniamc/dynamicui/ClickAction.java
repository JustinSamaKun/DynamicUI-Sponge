package net.eterniamc.dynamicui;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ClickAction {
    private final ClickInventoryEvent event;
    private final Slot slot;

    public Set<ClickType> getTypes() {
        Set<ClickType> types = Sets.newHashSet();
        if (event instanceof ClickInventoryEvent.Drag) {
            types.add(ClickType.DRAG);
        }
        if (event instanceof ClickInventoryEvent.Drop) {
            types.add(ClickType.DROP);
        }
        if (event instanceof ClickInventoryEvent.Double) {
            types.add(ClickType.DOUBLE);
        }
        if (event instanceof ClickInventoryEvent.Shift) {
            types.add(ClickType.SHIFT);
        }
        if (event instanceof ClickInventoryEvent.Creative) {
            types.add(ClickType.CREATIVE);
        }
        if (event instanceof ClickInventoryEvent.Secondary) {
            types.add(ClickType.SECONDARY);
        }
        if (event instanceof ClickInventoryEvent.Middle) {
            types.add(ClickType.MIDDLE);
        }
        if (event instanceof ClickInventoryEvent.Primary) {
            types.add(ClickType.PRIMARY);
        }
        return types;
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    public int getSlotIndex() {
        return getSlot().getInventoryProperty(SlotIndex.class).get().getValue();
    }
}
