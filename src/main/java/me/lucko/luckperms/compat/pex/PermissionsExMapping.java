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

package me.lucko.luckperms.compat.pex;

import lombok.experimental.UtilityClass;

import com.google.common.collect.ImmutableList;

import me.lucko.luckperms.compat.LuckPermsCompat;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * PermissionsEx mapping, based upon https://github.com/PEXPlugins/PermissionsEx/wiki/Commands
 */
@UtilityClass
public class PermissionsExMapping {
    private static final List<PermissionsExCommand> MAPPING = buildMapping();

    public static void sendUsage(CommandSender sender) {
        LuckPermsCompat.msg(sender, "&bMapped commands: &7(PermissionsEx)");
        for (PermissionsExCommand cmd : MAPPING) {
            LuckPermsCompat.msg(sender, "&3> &a/pex " + cmd.getUsage());
        }
    }

    public static void registerMapping(LuckPermsCompat plugin) {
        plugin.hijackCommand("pex", new PermissionsExCommandExecutor(plugin, MAPPING));
    }

    private static List<PermissionsExCommand> buildMapping() {
        ImmutableList.Builder<PermissionsExCommand> commands = ImmutableList.builder();

        /*
         * Utility commands
         */
        commands.add(PermissionsExCommand.of(ImmutableList.of("toggle", "debug"), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "LuckPerms has a verbose monitoring system instead of a debug mode. &7(see /lp verbose)");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Verbose");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "toggle", "debug"), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "LuckPerms has a verbose monitoring system instead of a debug mode. &7(see /lp verbose)");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Verbose");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "check", "<permission>"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String permission = arguments.get("permission");

            plugin.executeCommand(sender, "check " + user + " " + permission);
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("reload"), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "reloadconfig");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("config"), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "info");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("backend"), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "info");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("import"), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "The import command cannot be automatically remapped. LuckPerms uses a slightly different system.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Switching-storage-types");
        }));


        /*
         * World inheritance
         */
        commands.add(PermissionsExCommand.of(ImmutableList.of("world"), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "World specific permissions are granted via added command arguments.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Command-Usage");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("worlds"), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "World specific permissions are granted via added command arguments.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Command-Usage");
        }));


        /*
         * User commands
         */
        commands.add(PermissionsExCommand.of(ImmutableList.of("users"), (plugin, sender, arguments) -> {
            // just search for all users in the default group. that's the best remap of this functionality.
            plugin.executeCommand(sender, "search group.default");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "list"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");

            plugin.executeCommand(sender, "user " + user + " permission info");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "prefix", "[new prefix]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String prefix = arguments.get("new prefix");
            
            if (prefix != null) {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta addprefix <weight> " + prefix);
            } else {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta removeprefix <weight>");
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "suffix", "[new suffix]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String suffix = arguments.get("new suffix");

            if (suffix != null) {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta addsuffix <weight> " + suffix);
            } else {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta removesuffix <weight>");
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "delete"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");

            plugin.executeCommand(sender, "user " + user + " clear");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "add", "<permission>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String permission = arguments.get("permission");
            String world = arguments.getOrDefault("world", null);

            boolean value = true;
            if (permission.startsWith("-") || permission.startsWith("!")) {
                value = false;
                permission = permission.substring(1);
            }

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " permission set " + permission + " " + value + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " permission set " + permission + " " + value);
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "remove", "<permission>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String permission = arguments.get("permission");
            String world = arguments.getOrDefault("world", null);

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " permission unset " + permission + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " permission unset " + permission);
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "timed", "add", "<permission>", "<lifetime>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String permission = arguments.get("permission");
            String lifetime = arguments.get("lifetime") + "seconds";
            String world = arguments.getOrDefault("world", null);

            boolean value = true;
            if (permission.startsWith("-") || permission.startsWith("!")) {
                value = false;
                permission = permission.substring(1);
            }

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " permission settemp " + permission + " " + value + " " + lifetime + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " permission unset " + permission + " " + value + " " + lifetime);
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "timed", "remove", "<permission>", "<lifetime>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String permission = arguments.get("permission");
            String world = arguments.getOrDefault("world", null);

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " permission unsettemp " + permission + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " permission unsettemp " + permission);
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "set", "<option>", "<value>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String option = arguments.get("option");
            String value = arguments.get("value");
            String world = arguments.getOrDefault("world", null);

            if (value.equals("\"\"")) {
                // unset
                if (world != null) {
                    plugin.executeCommand(sender, "user " + user + " meta unset " + option + " global " + world);
                } else {
                    plugin.executeCommand(sender, "user " + user + " meta unset " + option);
                }
            } else {
                if (world != null) {
                    plugin.executeCommand(sender, "user " + user + " meta set " + option + " " + value + " global " + world);
                } else {
                    plugin.executeCommand(sender, "user " + user + " meta set " + option + " " + value);
                }
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "group", "list"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");

            plugin.executeCommand(sender, "user " + user + " parent info");
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "group", "add", "<group>", "[world]", "[lifetime]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String group = arguments.get("group");
            String world = arguments.getOrDefault("world", "*");
            String lifetime = arguments.getOrDefault("lifetime", null);

            if (lifetime != null) {
                lifetime = lifetime + "seconds";

                if (!world.equals("*")) {
                    plugin.executeCommand(sender, "user " + user + " parent addtemp " + group + " " + lifetime + " global " + world);
                } else {
                    plugin.executeCommand(sender, "user " + user + " parent addtemp " + group + " " + lifetime);
                }
            } else {
                if (!world.equals("*")) {
                    plugin.executeCommand(sender, "user " + user + " parent set " + group + " global " + world);
                } else {
                    plugin.executeCommand(sender, "user " + user + " parent set " + group);
                }
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "group", "set", "<group>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String group = arguments.get("group");
            String world = arguments.getOrDefault("world", null);

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " parent set " + group + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " parent set " + group);
            }
        }));

        commands.add(PermissionsExCommand.of(ImmutableList.of("user", "<user>", "group", "remove", "<group>", "[world]"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String group = arguments.get("group");
            String world = arguments.getOrDefault("world", null);

            if (world != null) {
                plugin.executeCommand(sender, "user " + user + " parent remove " + group + " global " + world);
            } else {
                plugin.executeCommand(sender, "user " + user + " parent remove " + group);
            }
        }));

        return commands.build();
    }


}
