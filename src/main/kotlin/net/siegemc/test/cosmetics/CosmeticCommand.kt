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
        CosmeticsTest.instance?.changeCosmetic(player)
    }

    @Subcommand("test")
    fun cosmeticTest(player: Player) {
        player.sendMessage(player.inventory.boots?.type.toString())
    }

    @Subcommand("unhide")
    fun cosmeticUnhide(player: Player) {
        CosmeticsTest.instance?.unhideCosmetic(player)
    }
}