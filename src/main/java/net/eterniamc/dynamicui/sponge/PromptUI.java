package net.eterniamc.dynamicui.sponge;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public abstract class PromptUI<T> extends DynamicUI {

    private final DynamicUI parent;

    private Consumer<T> callback = t -> {};

    @Override
    public boolean onClose() {
        Player player1 = player;
        Task.builder()
                .delayTicks(10)
                .execute(() -> {
                    try {
                        parent.open(player1, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .submit(InterfaceController.INSTANCE.getPlugin());
        return super.onClose();
    }

    public PromptUI<T> callback(Consumer<T> function) {
        this.callback = function;
        return this;
    }
}

