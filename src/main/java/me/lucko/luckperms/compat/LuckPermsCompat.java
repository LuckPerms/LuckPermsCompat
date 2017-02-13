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

import com.google.common.base.Splitter;

import me.lucko.luckperms.bukkit.BukkitCommand;
import me.lucko.luckperms.bukkit.LPBukkitPlugin;
import me.lucko.luckperms.common.commands.utils.Util;
import me.lucko.luckperms.common.constants.Patterns;
import me.lucko.luckperms.compat.mappings.GroupManagerMapping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

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
    private BukkitCommand commandManager;

    // cached command map instance
    private CommandMap commandMap = null;

    private Map<String, MappedCommand> groupManagerMapping = GroupManagerMapping.buildMapping();

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
        registerMapping(groupManagerMapping);

        getLogger().info("Successfully enabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        msg(sender, "&2Running &bLuckPermsCompat v" + getDescription().getVersion() + "&2, hooked with &bLuckPerms v" + luckPerms.getVersion() + "&2.");

        if (!sender.hasPermission("luckpermscompat.use")) {
            return true;
        }

        msg(sender, "&bMapped commands: &7(GroupManager)");
        for (Map.Entry<String, MappedCommand> cmd : groupManagerMapping.entrySet()) {
            msg(sender, "&3> &a/" + cmd.getKey() + " " + cmd.getValue().getUsage());
        }
        return true;
    }

    /**
     * Registers a command mapping with the server
     * @param commandMap the command map
     */
    public void registerMapping(Map<String, MappedCommand> commandMap) {
        for (Map.Entry<String, MappedCommand> e : commandMap.entrySet()) {
            e.getValue().setPlugin(this);
            hijackCommand(e.getKey(), e.getValue());
        }
    }

    /**
     * Executes a LuckPerms command directly.
     * @param sender the sender
     * @param cmd the command string, without the "/luckperms" part
     */
    public void executeCommand(CommandSender sender, String cmd) {
        commandManager.onCommand(
                luckPerms.getSenderFactory().wrap(sender),
                "luckperms",
                Util.stripQuotes(Splitter.on(Patterns.COMMAND_SEPARATOR).omitEmptyStrings().splitToList(cmd))
        );
    }

    /**
     * Forcefully registers a command with the server.
     * This means we can always override registered commands, even if they've already been bound to a plugin.
     * @param alias the command name
     * @param executor the executor instance for the command
     */
    private void hijackCommand(String alias, CommandExecutor executor) {
        PluginCommand cmd = getServer().getPluginCommand(alias);
        if (cmd == null) {
            try {
                cmd = (PluginCommand) commandConstructor.newInstance(alias, this);
            } catch (Exception ex) {
                getLogger().severe("Could not register command: " + alias);
                return;
            }

            // Get the command map to register the command to
            if (commandMap == null) {
                try {
                    PluginManager pluginManager = getServer().getPluginManager();
                    Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
                    commandMapField.setAccessible(true);
                    commandMap = (CommandMap) commandMapField.get(pluginManager);
                } catch (Exception ex) {
                    getLogger().severe("Could not register command: " + alias);
                    return;
                }
            }

            commandMap.register(this.getDescription().getName(), cmd);
        } else {
            // we may need to change the owningPlugin, since this was already registered
            try {
                owningPluginField.set(cmd, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cmd.setExecutor(executor);
        cmd.setTabCompleter(null);
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
