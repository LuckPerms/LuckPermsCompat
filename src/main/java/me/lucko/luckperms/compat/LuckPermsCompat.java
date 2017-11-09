/*
 * Copyright (c) 2017 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.compat;

import me.lucko.luckperms.bukkit.BukkitCommandExecutor;
import me.lucko.luckperms.bukkit.LPBukkitPlugin;
import me.lucko.luckperms.compat.groupmanager.GroupManagerMapping;
import me.lucko.luckperms.compat.permissionsex.PermissionsExMapping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * A plugin that provides command aliases for LuckPerms commands.
 */
public class LuckPermsCompat extends JavaPlugin implements CommandExecutor {

    // Used for registering commands at runtime
    private static Constructor<?> commandConstructor;
    private static Field owningPluginField;
    static {
        try {
            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            owningPluginField = PluginCommand.class.getDeclaredField("owningPlugin");
            owningPluginField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // the internal plugin instance
    private LPBukkitPlugin luckPerms;
    // the LP command executor
    private BukkitCommandExecutor commandManager;

    // cached command map instance
    private CommandMap commandMap = null;

    @Override
    public void onEnable() {
        getLogger().info("Hooking with LuckPerms.");

        Object instance = Bukkit.getPluginManager().getPlugin("LuckPerms");
        if (!(instance instanceof LPBukkitPlugin)) {
            throw new RuntimeException("Illegal plugin instance: " + instance.getClass().getName());
        }
        luckPerms = (LPBukkitPlugin) instance;
        commandManager = luckPerms.getCommandManager();

        hijackCommand("lpc", this);

        // Group Manager
        getLogger().info("Remapping GroupManager commands");
        GroupManagerMapping.registerMapping(this);

        // PermissionsEx
        getLogger().info("Remapping PEX commands");
        PermissionsExMapping.registerMapping(this);

        getLogger().info("Successfully enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        msg(sender, "&2Running &bLuckPermsCompat v" + getDescription().getVersion() + "&2, hooked with &bLuckPerms v" + luckPerms.getVersion() + "&2.");

        if (!sender.hasPermission("luckpermscompat.use")) {
            return true;
        }

        if (label.equalsIgnoreCase("pex")) {
            PermissionsExMapping.sendUsage(sender);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("pex")) {
            PermissionsExMapping.sendUsage(sender);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("groupmanager")) {
            GroupManagerMapping.sendUsage(sender);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("gm")) {
            GroupManagerMapping.sendUsage(sender);
        } else {
            PermissionsExMapping.sendUsage(sender);
            GroupManagerMapping.sendUsage(sender);
        }

        return true;
    }

    /**
     * Executes a LuckPerms command directly.
     * @param sender the sender
     * @param cmd the command string, without the "/luckperms" part
     */
    public void executeCommand(CommandSender sender, String cmd) {
        commandManager.onCommand(sender, null, "luckperms", cmd.split(" "));
    }

    /**
     * Forcefully registers a command with the server.
     * This means we can always override registered commands, even if they've already been bound to a plugin.
     * @param alias the command name
     * @param executor the executor instance for the command
     */
    public void hijackCommand(String alias, CommandExecutor executor) {
        CommandMapUtil.registerCommand(this, executor, alias);
    }

    /**
     * Sends a formatted message to a sender
     * @param sender the object to send to
     * @param msg the message
     */
    public static void msg(CommandSender sender, String msg) {
        sender.sendMessage(colorize("&7&l[&b&lL&3&lP&c&lC&7&l] &a" + msg));
    }

    private static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
