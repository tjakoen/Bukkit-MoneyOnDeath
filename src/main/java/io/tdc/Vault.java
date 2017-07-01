package io.tdc;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

class Vault  {
    Economy econ = null;
    Permission perms = null;
    Chat chat = null;

    boolean setupEconomy(Server server) {
        if (server.getPluginManager().getPlugin("Vault") == null) return false;

        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        econ = rsp.getProvider();
        return econ != null;
    }

    boolean setupChat(Server server) {
        RegisteredServiceProvider<Chat> rsp = server.getServicesManager().getRegistration(Chat.class);
        if (rsp != null)
            chat = rsp.getProvider();
        return chat != null;
    }

    void setupPermissions(Server server) {
        RegisteredServiceProvider<Permission> rsp = server.getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
    }
}
