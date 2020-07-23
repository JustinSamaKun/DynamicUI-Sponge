package net.eterniamc.dynamicui;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;

@Getter
@SuppressWarnings("unused")
public abstract class DynamicUI {
    /** Sponge inventory reference */
    protected Inventory inventory;

    /** Sponge players viewing the UI */
    protected Player player;

    /** Custom slot listeners */
    private final Map<Integer, ClickListener> listeners = Maps.newHashMap();

    /**
     * Listener for when player clicks there own inventory
     * if null no listeners will be called
     */
    @Setter
    private ClickListener playerInventoryListener;

    /** List of other DynamicUIs that should update as this one does */
    private final List<DynamicUI> responders = Lists.newArrayList();

    @Setter
    private boolean intractable = true;

    private boolean built = false;

    @Setter
    private boolean preventCloseEvent = false;

    public void build() {
        generateInventory();
        render();
        InterfaceController.INSTANCE.getActiveInterfaces().add(this);
        built = true;
    }

    public void open(Player player) {
        open(player, !built);
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public void open(Player player, boolean build) {
        try {
            Preconditions.checkArgument(Sponge.getServer().isMainThread(), "Inventories must be opened on the main thread!");
            player.closeInventory();

            InterfaceController.INSTANCE.getActiveInterfaces().add(this);

            if (this.player == null) {
                this.player = player;
            }

            if (build) {
                build();
            }

            openInventory(player);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Text.of(TextColors.DARK_RED, "Something went very wrong and the UI was unable to be opened"));
            InterfaceController.INSTANCE.getActiveInterfaces().remove(this);
            player.closeInventory();
            onClose();
        }
    }

    public Inventory createInventory(String title, int rows) {
        return Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .property(InventoryTitle.of(TextSerializers.FORMATTING_CODE.deserialize(title)))
                .property(InventoryDimension.of(9, rows))
                .build(InterfaceController.INSTANCE.getPlugin());

    }

    public void addListener(int slot, ClickListener listener) {
        listeners.put(slot, listener);
    }

    public void addResponder(DynamicUI ui) {
        responders.add(ui);
    }

    public abstract void generateInventory();

    public void loadListeners() {

    }

    /**
     * This is required if there are two UIs that listen to each other's updates.
     */
    public boolean shouldUpdate() {
        return true;
    }

    public void render() {
        loadListeners();
        for (DynamicUI responder : responders) {
            if (responder.isValid()) {
                if (responder.shouldUpdate()) {
                    responder.render();
                }
            }
        }
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).set(stack);
    }

    public void setTitle(Text title) {
        inventory = Inventory.builder()
                .from(inventory)
                .property(InventoryTitle.of(title))
                .build(InterfaceController.INSTANCE.getPlugin());
        player.openInventory(inventory);
    }

    public void close() {
        if (player != null) {
            player.closeInventory();
        }
    }

    public boolean onClose() {
        for (DynamicUI responder : responders) {
            responder.close();
        }
        player = null;
        InterfaceController.INSTANCE.getActiveInterfaces().remove(this);

        return true;
    }

    public boolean isValid() {
        return player != null;
    }
}
