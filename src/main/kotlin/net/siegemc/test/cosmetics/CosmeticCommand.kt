package net.siegemc.test.cosmetics

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import org.bukkit.entity.Player

@CommandAlias("cosmetic")
class CosmeticCommand: BaseCommand() {
    @Default
    fun cosmeticCommand(player: Player) {
        //CosmeticsTest.instance?.changeCosmetic(player)
    }

    @Subcommand("test")
    fun cosmeticTest(player: Player) {
        player.sendMessage(player.inventory.helmet?.type.toString())
    }

    @Subcommand("enable")
    fun cosmeticEnable(player: Player) {
        CosmeticsTest.instance?.cosmeticEnabled?.set(player.uniqueId, true)
        CosmeticsTest.instance?.showCosmetic(player)
    }

    @Subcommand("disable")
    fun cosmeticDisable(player: Player) {
        CosmeticsTest.instance?.cosmeticEnabled?.set(player.uniqueId, false)
        CosmeticsTest.instance?.unhideCosmetic(player)
    }

    @Subcommand("my")
    fun cosmeticMy(player: Player) {
        player.sendMessage("Your cosmetic is " + (CosmeticsTest.instance?.cosmetics?.get(player.uniqueId) ?: "null"))
    }
}