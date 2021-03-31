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
import de.tr7zw.nbtapi.NBTItem
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

    val cosmetics = hashMapOf<UUID, ItemStack?>()
    val cosmeticEnabled = hashMapOf<UUID, Boolean>()
    private lateinit var protocolManager: ProtocolManager

    override fun onEnable() {
        instance = this;
        val manager = PaperCommandManager(this)
        manager.registerCommand(CosmeticCommand())

        protocolManager = ProtocolLibrary.getProtocolManager()
        server.pluginManager.registerEvents(this, this)
        val item = ItemStack(Material.PAPER)
        val nbtItem = NBTItem(item)
        nbtItem.setInteger("CustomModelData", 1)
        cosmetics[UUID.fromString("19d9822a-83ad-44f3-bb79-f87bf6dd53d0")] = nbtItem.item

        protocolManager.addPacketListener(object : PacketAdapter(
            this,
            PacketType.Play.Server.ENTITY_EQUIPMENT
        ) {
            override fun onPacketSending(event: PacketEvent) {

                val entity = event.packet.getEntityModifier(event.player.world).read(0)
                if (entity is Player) {
                    Bukkit.getLogger().info("the player name is ${entity.name}, they ${if (cosmeticEnabled[entity.uniqueId] == true) "do" else "don't"} have cosmetics enabled, and the type of their cosmetic is ${cosmetics[entity.uniqueId]?.type}")

                    if (cosmetics[entity.uniqueId] == null || cosmeticEnabled[entity.uniqueId] == false) return

                    val pairList = event.packet.slotStackPairLists.read(0)


                    for (pair in pairList) {
                        if (pair.first == EnumWrappers.ItemSlot.HEAD) {
                            Bukkit.getLogger().info("equipment packet to ${event.player.name}")
                            Bukkit.getLogger().info("FROM: ${pair.second.type}")
                            pair.second = cosmetics[entity.uniqueId]
                            Bukkit.getLogger().info("TO: ${pair.second.type}")
                        }

                    }

                    event.packet.slotStackPairLists.write(0, pairList)
                }


            }
        })

        Bukkit.getLogger().info("Enabled!")
    }

    fun showCosmetic(player: Player) {
        //Bukkit.getLogger().info("hi")
        val testPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)
        testPacket.integers.write(0, player.entityId)
        testPacket.slotStackPairLists.write(0, mutableListOf(Pair(EnumWrappers.ItemSlot.HEAD, cosmetics[player.uniqueId])))
        //val pairList = testPacket.slotStackPairLists.read(0)
        //Bukkit.getLogger().info(pairList[0].second.type.toString())
        protocolManager.sendServerPacket(player, testPacket)


//        val wrapperPlayServerEntityEquipment = WrapperPlayServerEntityEquipment()
//        wrapperPlayServerEntityEquipment.entityID = player.entityId
//        wrapperPlayServerEntityEquipment.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, ItemStack(Material.LEATHER_BOOTS))
//        wrapperPlayServerEntityEquipment.sendPacket(player)
    }

    fun unhideCosmetic(player: Player) {
        val testPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)
        testPacket.integers.write(0, player.entityId)
        testPacket.slotStackPairLists.write(0, mutableListOf(Pair(EnumWrappers.ItemSlot.HEAD, player.inventory.helmet)))
        //val pairList = testPacket.slotStackPairLists.read(0)
        //Bukkit.getLogger().info(pairList[0].second.type.toString())
        protocolManager.sendServerPacket(player, testPacket)
    }


    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (cosmeticEnabled[e.player.uniqueId] == null) {
            cosmeticEnabled[e.player.uniqueId] = false
        } else if (cosmeticEnabled[e.player.uniqueId] == true) {
            object : BukkitRunnable() {
                override fun run() {
                    showCosmetic(e.player)
                }
            }.runTaskLater(this, 30)

        }
    }

//    @EventHandler
//    fun onPlayerCloseInventory(e: InventoryCloseEvent) {
//        if (e.player !is Player) return
//        e.player.inventory.boots = lastLoggedOutItems[e.player.uniqueId]
//        object : BukkitRunnable() {
//            override fun run() {
//                // What you want to schedule goes here
//                changeCosmetic(e.player as Player)
//            }
//        }.runTaskLater(this, 30)
//    }
}