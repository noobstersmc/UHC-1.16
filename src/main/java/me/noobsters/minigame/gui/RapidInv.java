package me.noobsters.minigame.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import lombok.Data;

/**
 * Small and easy Bukkit inventory API with 1.7 to 1.16 support. The project is
 * on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>
 *
 * @author MrMicky
 * @version 3.0
 */
@Data
public class RapidInv implements InventoryHolder {
    // Slot event handler.
    Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    // Event Handlers
    Set<Consumer<InventoryOpenEvent>> openHandlers;
    Set<Consumer<InventoryCloseEvent>> closeHandlers;
    Set<Consumer<InventoryClickEvent>> clickHandlers;
    Predicate<Player> closeFilter;
    Inventory inventory;
    // Parent child and clone behavior
    RapidInv parentInventory;
    Set<RapidInv> children, clones;

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size The size of the inventory.
     */
    public RapidInv(int size) {
        this(size, InventoryType.CHEST.getDefaultTitle());
    }

    /**
     * Clones the current inventory and returns a new object with the exact same
     * properties as the parent.
     * 
     * @param title Title of the cloned inventory
     * @return A new cloned inventory
     */
    public RapidInv clone(String title) {
        var newInventory = new RapidInv(this.getInventory().getSize(), this.getInventory().getType(), title);
        newInventory.openHandlers = this.openHandlers;
        newInventory.closeHandlers = this.closeHandlers;
        newInventory.clickHandlers = this.clickHandlers;
        newInventory.itemHandlers.putAll(this.itemHandlers);
        newInventory.inventory.setContents(this.getInventory().getContents());
        newInventory.parentInventory = this;

        if (clones == null) {
            clones = new HashSet<>();
        }

        this.clones.add(newInventory);

        return newInventory;
    }

    /**
     * Utility function to close the inventory for all open instances.
     */
    public void closeAll() {
        getInventory().getViewers().forEach(HumanEntity::closeInventory);
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  The size of the inventory.
     * @param title The title (name) of the inventory.
     */
    public RapidInv(int size, String title) {
        this(size, InventoryType.CHEST, title);
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type The type of the inventory.
     */
    public RapidInv(InventoryType type) {
        this(type, type.getDefaultTitle());
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  The type of the inventory.
     * @param title The title of the inventory.
     */
    public RapidInv(InventoryType type, String title) {
        this(0, type, title);
    }

    private RapidInv(int size, InventoryType type, String title) {
        if (type == InventoryType.CHEST && size > 0) {
            inventory = Bukkit.createInventory(this, size, title);
        } else {
            inventory = Bukkit.createInventory(this, Objects.requireNonNull(type, "type"), title);
        }

        if (inventory.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not RapidInv, found: " + inventory.getHolder());
        }
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot.
     *
     * @param item The ItemStack to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a
     * click handler.
     *
     * @param item    The item to add.
     * @param handler The the click handler for the item.
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click
     * handler.
     *
     * @param slot    The slot where to add the item.
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        inventory.setItem(slot, item);

        if (handler != null) {
            itemHandlers.put(slot, handler);
        } else {
            itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click
     * handler.
     *
     * @param slot    The slot where to add the item.
     * @param item    The Updated item
     * @param handler The click handler for the item
     */
    public void updateItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        // Notify clones of the change.
        clones.parallelStream().forEach(clones -> clones.updateItem(slot, item, handler));
        // Update the item if there is one, otherwise add it
        var oldItem = inventory.getItem(slot);
        if (oldItem != null) {
            oldItem.setType(item.getType());
            oldItem.setItemMeta(item.getItemMeta());
            if (handler != null) {
                itemHandlers.put(slot, handler);
            } else {
                itemHandlers.remove(slot);
            }

        } else {
            setItem(slot, item, handler);
        }
    }

    /**
     * Updates the {@link ItemStack} on a slot while keeping its previous
     * {@link InventoryClickEvent} behavior.
     *
     * @param slot The slot that needs to be updated.
     * @param item The new itemstack.
     */
    public void updateItem(int slot, ItemStack item) {
        setItem(slot, item, itemHandlers.get(slot));
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots.
     *
     * @param slotFrom Starting slot to add the item in.
     * @param slotTo   Ending slot to add the item in.
     * @param item     The item to add.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click
     * handler.
     *
     * @param slotFrom Starting slot to put the item in.
     * @param slotTo   Ending slot to put the item in.
     * @param item     The item to add.
     * @param handler  The click handler for the item
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots.
     *
     * @param slots The slots where to add the item
     * @param item  The item to add.
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click
     * handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory
     *
     * @param slot The slot where to remove the item
     */
    public void removeItem(int slot) {
        inventory.clear(slot);
        itemHandlers.remove(slot);
    }

    /**
     * Remove multiples {@link ItemStack} from the inventory
     *
     * @param slots The slots where to remove the items
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Clears all items. Keep it simple, silly
     */
    public void clearAllItems() {
        inventory.clear();
        itemHandlers.clear();
    }

    /**
     * Add a close filter to prevent players from closing the inventory. To prevent
     * a player from closing the inventory the predicate should return {@code true}
     *
     * @param closeFilter The close filter
     */
    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    /**
     * Add a handler to handle inventory open.
     *
     * @param openHandler The handler to add.
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        if (openHandlers == null) {
            openHandlers = new HashSet<>();
        }
        openHandlers.add(openHandler);
    }

    /**
     * Add a handler to handle inventory close.
     *
     * @param closeHandler The handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        if (closeHandlers == null) {
            closeHandlers = new HashSet<>();
        }
        closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler to handle inventory click.
     *
     * @param clickHandler The handler to add.
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        if (clickHandlers == null) {
            clickHandlers = new HashSet<>();
        }
        clickHandlers.add(clickHandler);
    }

    /**
     * Open the inventory to a player.
     *
     * @param player The player to open the menu.
     */
    public void open(Player player) {
        player.openInventory(inventory);
    }

    /**
     * Utility function that allows developer to open inventories from an
     * asynchronous thread.
     * 
     * @param player Player that will see the inventory
     * @param plugin Plugin instance to schedule the inventory openning task.
     */
    public void open(Player player, Plugin plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(inventory));
    }

    /**
     * Get borders of the inventory. If the inventory size is under 27, all slots
     * are returned
     *
     * @return inventory borders
     */
    public int[] getBorders() {
        int size = inventory.getSize();
        return IntStream.range(0, size)
                .filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get corners of the inventory.
     *
     * @return inventory corners
     */
    public int[] getCorners() {
        int size = inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     * Get the Bukkit inventory
     *
     * @return The Bukkit inventory.
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        if (openHandlers != null) {
            openHandlers.forEach(c -> c.accept(e));
        }
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        if (closeHandlers != null) {
            closeHandlers.forEach(c -> c.accept(e));
        }

        return closeFilter != null && closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        if (clickHandlers != null) {
            clickHandlers.forEach(c -> c.accept(e));
        }

        Consumer<InventoryClickEvent> clickConsumer = itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }
}