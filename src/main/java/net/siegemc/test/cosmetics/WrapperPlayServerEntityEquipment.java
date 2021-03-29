package net.siegemc.test.cosmetics;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;

import java.util.List;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.ENTITY_EQUIPMENT;

    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityEquipment(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity whose equipment will be changed.
     *
     * @param world - the current world of the entity.
     * @return The affected entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity whose equipment will be changed.
     *
     * @param event - the packet event.
     * @return The affected entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve list of ItemSlot - ItemStack pairs.
     *
     * @return The current Item
     */
    public List<Pair<ItemSlot, ItemStack>> getSlotStackPairs() {
        return handle.getSlotStackPairLists().read(0);
    }

    /**
     * Set a ItemSlot - ItemStack pair.
     * @param slot The slot the item will be equipped in. If matches an existing pair, will overwrite the old one
     * @param item The item to equip
     */
    public void setSlotStackPair(ItemSlot slot, ItemStack item) {
        List<Pair<ItemSlot, ItemStack>> slotStackPairs = handle.getSlotStackPairLists().read(0);
        slotStackPairs.removeIf(pair -> pair.getFirst().equals(slot));
        slotStackPairs.add(new Pair<>(slot, item));
    }

    /**
     * Removes the ItemSlot ItemStack pair matching the provided slot. If doesn't exist does nothing
     * @param slot the slot to remove the pair from
     */
    public void removeSlotStackPair(ItemSlot slot) {
        handle.getSlotStackPairLists().read(0).removeIf(pair -> pair.getFirst().equals(slot));
    }

    /**
     * Check whether the provided is to be affected
     * @param slot the slot to check for
     * @return true if is set, false otherwise
     */
    public boolean isSlotSet(ItemSlot slot) {
        return handle.getSlotStackPairLists().read(0).stream().anyMatch(pair -> pair.getFirst().equals(slot));
    }

    /**
     * Get the item being equipped to the provided slot
     * @param slot the slot to retrieve the item from
     * @return the equipping item, or null if doesn't exist
     */
    public ItemStack getItem(ItemSlot slot) {
        for (Pair<ItemSlot, ItemStack> pair : handle.getSlotStackPairLists().read(0)) {
            if (pair.getFirst().equals(slot)) return pair.getSecond();
        }
        return null;
    }

    /**
     * @deprecated This format is no longer supported in Minecraft 1.16+
     * For 1.16+ use the SlotStack methods
     */
    @Deprecated
    public ItemSlot getSlot() {
        return handle.getItemSlots().read(0);
    }

    /**
     * @deprecated This format is no longer supported in Minecraft 1.16+
     * For 1.16+ use the SlotStack methods
     */
    @Deprecated
    public void setSlot(ItemSlot value) {
        handle.getItemSlots().write(0, value);
    }

    /**
     * @deprecated This format is no longer supported in Minecraft 1.16+
     * For 1.16+ use the SlotStack methods
     *
     * Retrieve Item.
     * <p>
     * Notes: item in slot format
     *
     * @return The current Item
     */
    @Deprecated
    public ItemStack getItem() {
        return handle.getItemModifier().read(0);
    }

    /**
     * @deprecated This format is no longer supported in Minecraft 1.16+
     * For 1.16+ use the SlotStack methods
     *
     * Set Item.
     *
     * @param value - new value.
     */
    @Deprecated
    public void setItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }
}