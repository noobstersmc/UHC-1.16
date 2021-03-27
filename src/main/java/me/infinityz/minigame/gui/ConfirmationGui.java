package me.infinityz.minigame.gui;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;

import lombok.Getter;
import net.noobsters.kern.paper.guis.RapidInv;

public class ConfirmationGui {
    private @Getter RapidInv previous;
    private @Getter RapidInv next;
    private @Getter Consumer<InventoryClickEvent> confirm;
    private @Getter Consumer<InventoryClickEvent> deny;

    public ConfirmationGui(RapidInv previous, RapidInv next, Consumer<InventoryClickEvent> confirm,
            Consumer<InventoryClickEvent> deny) {
        this.previous = previous;
        this.next = next;
        this.confirm = confirm;
        this.deny = deny;
    }

    //Example
    void method() {
        new ConfirmationGui(null, null, (confirm) -> {
    

        }, (cancel) -> {

        });
    }

}
