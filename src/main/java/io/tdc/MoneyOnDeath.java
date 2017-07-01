package io.tdc;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MoneyOnDeath extends JavaPlugin implements Listener{
    private static final Logger log = Logger.getLogger("Minecraft");
    private Vault vault;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();

        vault = new Vault();
        Server server = getServer();

        if (!vault.setupEconomy(server)) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            server.getPluginManager().disablePlugin(this);
            return;
        }

        vault.setupPermissions(server);
        vault.setupChat(server);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Economy econ = vault.econ;
        Player player = e.getEntity();

        if ( !this.getConfig().getBoolean("conf.op") && player.isOp())
            return;

        double balance = econ.getBalance(player.getName());
        String setting = this.getConfig().getString("conf.setting");
        Player killer = e.getEntity().getKiller();

        switch (setting) {
            case "clear": {
                econ.withdrawPlayer(player, balance);
                assert 0 == econ.getBalance(player.getName());

                if(this.getConfig().getBoolean("conf.pvp") && killer != null) {
                    econ.depositPlayer(killer, balance);
                    playerKilled(player, killer, balance);
                    break;
                }
                player.sendMessage("You have lost all of your money ($" + balance + ")");
                break;
            }
            case "amount": {
                double deduction = this.getConfig().getDouble("conf.amount.val");
                econ.withdrawPlayer(player, deduction);

                if(this.getConfig().getBoolean("conf.pvp") && killer != null) {
                    econ.depositPlayer(killer, deduction);
                    playerKilled(player, killer, deduction);
                    break;
                }

                player.sendMessage("$" + deduction + " lost on death");
                break;
            }
            case "percentage": {
                double percentage = this.getConfig().getDouble("conf.percent.val");
                percentage = percentage / 100;
                double deductable = Math.round(balance - (balance * percentage));
                econ.withdrawPlayer(player, deductable);

                if(this.getConfig().getBoolean("conf.pvp") && killer != null) {
                    econ.depositPlayer(killer, deductable);
                    playerKilled(player, killer, deductable);
                    break;
                }
                player.sendMessage("You have lost $" + balance);
                break;
            }
        }
    }

    public void playerKilled(Player player, Player killer, double amount) {
        player.sendMessage("By killing you $" + amount + " has been transferred to " + killer.getName());
        killer.sendMessage("By killing" + player.getName() + " $" + amount + "has been transferred to you");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("You don't have permission");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("DeathMoney")) {
            if (args.length == 0) {
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "clear":
                    sender.sendMessage("Death Penalty set to All");
                    this.getConfig().set("conf.setting", "clear");
                    break;
                case "amount":
                    if (args[1].isEmpty())
                        return false;
                    else {
                        try {
                            this.getConfig().set("conf.setting", "amount");
                            double amount = Double.parseDouble(args[1]);
                            this.getConfig().set("conf.amount.val", amount);
                            sender.sendMessage("Death Penalty amount set to $" + amount);
                        } catch (Exception e) {
                            sender.sendMessage("Amount must be a number");
                            return false;
                        }
                    }
                    break;
                case "percent":
                    if (args[1].isEmpty())
                        return false;
                    else {
                        try {
                            this.getConfig().set("conf.setting", "percent");
                            double amount = Double.parseDouble(args[1]);
                            if (amount > 100 && amount < 0) {
                                this.getConfig().set("conf.amount.val", amount);
                                sender.sendMessage("Death Penalty amount set to " + amount + "%");
                            } else {
                                sender.sendMessage("Invalid amount for percentage");
                                return false;
                            }
                        } catch (Exception e) {
                            sender.sendMessage("Amount must be a number");
                            return false;
                        }
                    }
                    break;
                case "pvp":
                    if (args[1].isEmpty())
                        return false;
                    else {
                        if (args[1].toLowerCase().equals("true")) {
                            this.getConfig().set("conf.pvp", true);
                            sender.sendMessage("PVP death penalty turned on");
                        } else if (args[1].toLowerCase().equals("false")) {
                            this.getConfig().set("conf.pvp", false);
                            sender.sendMessage("PVP death penalty turned off");
                        } else {
                            sender.sendMessage("Invalid input");
                            return false;
                        }
                    }
                    break;
                case "op":
                    if (args[1].isEmpty())
                        return false;
                    else {
                        if (args[1].equals("true")) {
                            this.getConfig().set("conf.op", true);
                            sender.sendMessage("OP death penalty turned on");
                        } else if (args[1].equals("false")) {
                            this.getConfig().set("conf.op", false);
                            sender.sendMessage("OP death penalty turned off");
                        } else {
                            sender.sendMessage("Invalid input");
                            return false;
                        }
                    }
                    break;
                case "help":
                    sender.sendMessage(
                    "/DeathMoney clear - Loose all money on death.\n" +
                    "/DeathMoney amount <amount> - Loose specific amount of money on death.\n" +
                    "/DeathMoney percent <percent> -Loose specific percentage of money on death.\n" +
                    "/DeathMoney pvp  <true|false> - Enable transferring of money lost to Killer.\n" +
                    "/DeathMoney op  <true|false> - Exclude or include OP's.\n");
                    break;
                case "info":
                    String setting = this.getConfig().getString("conf.setting");
                    String output =  "Setting: " + setting;
                    if (setting.equals("percent"))
                        output += "\nValue: " + this.getConfig().getDouble("conf.percent.val") + "%";
                    else if  (setting.equals("amount"))
                        output += "\nValue: $" + this.getConfig().getDouble("conf.percent.val");
                    output += "\nPVP: " + this.getConfig().getBoolean("conf.pvp");
                    output += "\nOP: " + this.getConfig().getBoolean("conf.op");
                    sender.sendMessage(output);
            }
            this.saveConfig();
            return true;
        }
        return false;
    }
}
