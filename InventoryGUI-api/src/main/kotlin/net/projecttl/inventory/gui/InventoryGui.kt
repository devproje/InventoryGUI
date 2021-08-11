package net.projecttl.inventory.gui

import net.kyori.adventure.text.Component
import net.projecttl.inventory.gui.utils.InventoryType
import net.projecttl.inventory.gui.utils.Slot
import org.bukkit.Bukkit
import org.bukkit.block.Container
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

val inventoryIDs = ArrayList<InventoryGuiBuilder>()

fun Plugin.gui(slotType: InventoryType, title: Component, init: InventoryGuiBuilder.() -> Unit) : Inventory {
    val a = InventoryGuiBuilder(slotType, title, this)
    inventoryIDs.add(a)
    return a.apply(init).build()
}

fun gui(slotType: InventoryType, title: Component, plugin: Plugin, init: InventoryGuiBuilder.() -> Unit) : Inventory {
    val a = InventoryGuiBuilder(slotType, title, plugin)
    inventoryIDs.add(a)
    return a.apply(init).build()
}

class InventoryGuiBuilder(val slotType: InventoryType, val title: Component, val plugin: Plugin) : Listener {

    private val slots = hashMapOf<Int, Slot>()

    fun slot(slot: Int, item: ItemStack, handler: InventoryClickEvent.() -> Unit) {
        slots[slot] = Slot(item, handler)
    }

    fun slot(slot: Int, item: ItemStack) {
        slot(slot, item) {}
    }

    fun build() : Inventory {
        val inv = Bukkit.createInventory(null, slotType.name.split("_")[1].toInt(), title)
        for (slot in slots.entries) {
            inv.setItem(slot.key, slot.value.stack)
        }
        Bukkit.getServer().pluginManager.registerEvents(this, plugin)
        return inv
    }

    @EventHandler
    private fun listener(event: InventoryClickEvent) {
        if(event.view.title() == this.title) {
            event.isCancelled = true
            if (event.currentItem != null && !inventoryIDs.contains(this)) {
                for (slot in slots.entries) {
                    if (slot.key == event.slot) {
                        slot.value.click(event)
                    }
                }
            }
        }
    }

    @EventHandler
    private fun liistener2(event: InventoryMoveItemEvent) {
        if(!inventoryIDs.contains(this) && event.source.holder is Container && (event.source.holder as Container).customName() == this.title) {
            event.isCancelled = true
        }
    }

    @EventHandler
    private fun close(event: InventoryCloseEvent) {
        inventoryIDs.remove(this)
    }

}