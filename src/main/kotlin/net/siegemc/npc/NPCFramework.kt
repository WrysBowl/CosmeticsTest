package net.siegemc.npc

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class KotlinPlugin: JavaPlugin() {
    // while this is singleton, a class must be initialized by Bukkit, so we can't use 'object'
    companion object {
        var instance: KotlinPlugin? = null
            private set;
    }

    override fun onEnable() {
        instance = this;

        Bukkit.getLogger().info("Enabled!")
    }
}