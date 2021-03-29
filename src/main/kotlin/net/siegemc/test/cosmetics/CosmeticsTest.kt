package net.siegemc.test.cosmetics

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import co.aikar.commands.PaperCommandManager
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.inventory.meta.LeatherArmorMeta

import com.comphenix.protocol.events.PacketEvent

import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.Color
import org.bukkit.Material
//import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitScheduler
import java.util.*
import org.bukkit.scheduler.BukkitRunnable





class CosmeticsTest: JavaPlugin(), Listener {
    // while this is singleton, a class must be initialized by Bukkit, so we can't use 'object'
    companion object {
        var instance: CosmeticsTest? = null
            private set;
    }

    val lastLoggedOutItems = hashMapOf<UUID, ItemStack?>()
    private lateinit var protocolManager: ProtocolManager

    override fun onEnable() {
        instance = this;
        val manager = PaperCommandManager(this)
        manager.registerCommand(CosmeticCommand())

        protocolManager = ProtocolLibrary.getProtocolManager()
        server.pluginManager.registerEvents(this, this)

        protocolManager.addPacketListener(object : PacketAdapter(
            this,
            PacketType.Play.Server.ENTITY_EQUIPMENT
        ) {
            override fun onPacketSending(event: PacketEvent) {

                //stack = packet.itemModifier.read(0)
                val pairList = event.packet.slotStackPairLists.read(0)

                // Color that depends on the player's name
//                val receiverName = event.player.name
//                val color = receiverName.hashCode() and 0x85B670

                // Update the color
                for (pair in pairList) {
                    if (pair.first == EnumWrappers.ItemSlot.FEET) {
                        Bukkit.getLogger().info("equipment packet to ${event.player.name}")
                        Bukkit.getLogger().info("FROM: ${pair.second.type}")
                        if (pair.first == EnumWrappers.ItemSlot.FEET) pair.second = ItemStack(Material.GOLDEN_BOOTS)
                        Bukkit.getLogger().info("TO: ${pair.second.type}")
                    }

                }

                event.packet.slotStackPairLists.write(0, pairList)

            }
        })

        Bukkit.getLogger().info("Enabled!")
    }

    fun changeCosmetic(player: Player) {
        Bukkit.getLogger().info("hi")
        val testPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)
        testPacket.integers.write(0, player.entityId)
        testPacket.slotStackPairLists.write(0, mutableListOf(Pair(EnumWrappers.ItemSlot.FEET, ItemStack(Material.GOLDEN_BOOTS))))
        val pairList = testPacket.slotStackPairLists.read(0)
        //Bukkit.getLogger().info(pairList[0].second.type.toString())
        lastLoggedOutItems[player.uniqueId] = player.inventory.boots
        protocolManager.sendServerPacket(player, testPacket)


//        val wrapperPlayServerEntityEquipment = WrapperPlayServerEntityEquipment()
//        wrapperPlayServerEntityEquipment.entityID = player.entityId
//        wrapperPlayServerEntityEquipment.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, ItemStack(Material.LEATHER_BOOTS))
//        wrapperPlayServerEntityEquipment.sendPacket(player)
    }

    fun unhideCosmetic(player: Player) {
        val testPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)
        testPacket.integers.write(0, player.entityId)
        testPacket.slotStackPairLists.write(0, mutableListOf(Pair(EnumWrappers.ItemSlot.FEET, player.inventory.boots)))
        val pairList = testPacket.slotStackPairLists.read(0)
        //Bukkit.getLogger().info(pairList[0].second.type.toString())
        protocolManager.sendServerPacket(player, testPacket)
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        lastLoggedOutItems[e.player.uniqueId] = e.player.inventory.boots
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (lastLoggedOutItems.containsKey(e.player.uniqueId)) {
            e.player.inventory.boots = lastLoggedOutItems[e.player.uniqueId]
            object : BukkitRunnable() {
                override fun run() {
                    // What you want to schedule goes here
                    changeCosmetic(e.player)
                }
            }.runTaskLater(this, 60)
            //changeCosmetic(e.player)
        }
    }

    @EventHandler
    fun onPlayerCloseInventory(e: InventoryCloseEvent) {
        if (e.player !is Player) return
        e.player.inventory.boots = lastLoggedOutItems[e.player.uniqueId]
        object : BukkitRunnable() {
            override fun run() {
                // What you want to schedule goes here
                changeCosmetic(e.player as Player)
            }
        }.runTaskLater(this, 30)
    }
}