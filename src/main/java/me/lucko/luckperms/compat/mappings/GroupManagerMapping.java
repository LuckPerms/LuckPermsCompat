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

package me.lucko.luckperms.compat.mappings;

import lombok.experimental.UtilityClass;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import me.lucko.luckperms.compat.LuckPermsCompat;
import me.lucko.luckperms.compat.MappedCommand;

import java.util.Map;

/**
 * GroupManager mapping, based upon http://wiki.mc-ess.net/wiki/Group_Manager/Commands
 */
@UtilityClass
public class GroupManagerMapping {

    public Map<String, MappedCommand> buildMapping() {
        ImmutableMap.Builder<String, MappedCommand> commands = ImmutableMap.builder();

        /*
         * User commands
         */
        commands.put("manuadd", MappedCommand.of(ImmutableList.of("player", "group"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String group = arguments.get("group");

            plugin.executeCommand(sender, "user " + player + " parent set " + group);
        }));

        commands.put("manudel", MappedCommand.of(ImmutableList.of("player"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            plugin.executeCommand(sender, "user " + player + " clear");
        }));

        commands.put("manuaddsub", MappedCommand.of(ImmutableList.of("player", "group"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String group = arguments.get("group");

            plugin.executeCommand(sender, "user " + player + " parent add " + group);
        }));

        commands.put("manudelsub", MappedCommand.of(ImmutableList.of("player", "group"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String group = arguments.get("group");

            plugin.executeCommand(sender, "user " + player + " parent remove " + group);
        }));

        commands.put("manpromote", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "Promotions in LuckPerms are performed using tracks. &7(see /lp track)");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Tracks");
        }));

        commands.put("mandemote", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "Promotions in LuckPerms are performed using tracks. &7(see /lp track)");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Tracks");
        }));

        commands.put("manuwhois", MappedCommand.of(ImmutableList.of("player"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            plugin.executeCommand(sender, "user " + player + " info");
        }));

        commands.put("manuaddp", MappedCommand.of(ImmutableList.of("player", "permission"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String permission = arguments.get("permission");

            boolean value = true;
            if (permission.startsWith("-") || permission.startsWith("!")) {
                value = false;
                permission = permission.substring(1);
            }

            plugin.executeCommand(sender, "user " + player + " permission set " + permission + " " + value);
        }));

        commands.put("manudelp", MappedCommand.of(ImmutableList.of("player", "permission"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String permission = arguments.get("permission");

            plugin.executeCommand(sender, "user " + player + " permission unset " + permission);
        }));

        commands.put("manulistp", MappedCommand.of(ImmutableList.of("player"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            plugin.executeCommand(sender, "user " + player + " permission info");
        }));

        commands.put("manucheckp", MappedCommand.of(ImmutableList.of("player", "permission"), (plugin, sender, arguments) -> {
            String player = arguments.get("player");
            String permission = arguments.get("permission");

            plugin.executeCommand(sender, "user " + player + " permission checkinherits " + permission);
        }));

        commands.put("manuaddv", MappedCommand.of(ImmutableList.of("user", "variable", "value"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String variable = arguments.get("variable");
            String value = arguments.get("value");

            if (variable.equalsIgnoreCase("prefix")) {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta addprefix <weight> " + value);
                return;
            }

            if (variable.equalsIgnoreCase("suffix")) {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta addsuffix <weight> " + value);
                return;
            }

            plugin.executeCommand(sender, "user " + user + " meta set " + variable + " " + value);
        }));

        commands.put("manudelv", MappedCommand.of(ImmutableList.of("user", "variable"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            String variable = arguments.get("variable");

            if (variable.equalsIgnoreCase("prefix")) {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta removeprefix <weight>");
                return;
            }

            if (variable.equalsIgnoreCase("suffix")) {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp user " + user + " meta removesuffix <weight>");
                return;
            }

            plugin.executeCommand(sender, "user " + user + " meta unset " + variable);
        }));

        commands.put("manulistv", MappedCommand.of(ImmutableList.of("user"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            plugin.executeCommand(sender, "user " + user + " meta info");
        }));

        commands.put("manucheckv", MappedCommand.of(ImmutableList.of("user"), (plugin, sender, arguments) -> {
            String user = arguments.get("user");
            plugin.executeCommand(sender, "user " + user + " meta info");
        }));


        /*
         * Group commands
         */
        commands.put("mangadd", MappedCommand.of(ImmutableList.of("group"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            plugin.executeCommand(sender, "creategroup " + group);
        }));

        commands.put("mangdel", MappedCommand.of(ImmutableList.of("group"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            plugin.executeCommand(sender, "deletegroup " + group);
        }));

        commands.put("mangaddi", MappedCommand.of(ImmutableList.of("group1", "group2"), (plugin, sender, arguments) -> {
            String group1 = arguments.get("group1");
            String group2 = arguments.get("group2");

            plugin.executeCommand(sender, "group " + group1 + " parent add " + group2);
        }));

        commands.put("mangdeli", MappedCommand.of(ImmutableList.of("group1", "group2"), (plugin, sender, arguments) -> {
            String group1 = arguments.get("group1");
            String group2 = arguments.get("group2");

            plugin.executeCommand(sender, "group " + group1 + " parent remove " + group2);
        }));

        commands.put("listgroups", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "listgroups");
        }));

        commands.put("mangaddp", MappedCommand.of(ImmutableList.of("group", "permission"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            String permission = arguments.get("permission");

            boolean value = true;
            if (permission.startsWith("-") || permission.startsWith("!")) {
                value = false;
                permission = permission.substring(1);
            }

            plugin.executeCommand(sender, "group " + group + " permission set " + permission + " " + value);
        }));

        commands.put("mangdelp", MappedCommand.of(ImmutableList.of("group", "permission"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            String permission = arguments.get("permission");

            plugin.executeCommand(sender, "group " + group + " permission unset " + permission);
        }));

        commands.put("manglistp", MappedCommand.of(ImmutableList.of("group"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            plugin.executeCommand(sender, "group " + group + " permission info");
        }));

        commands.put("mangcheckp", MappedCommand.of(ImmutableList.of("group", "permission"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            String permission = arguments.get("permission");

            plugin.executeCommand(sender, "group " + group + " permission checkinherits " + permission);
        }));

        commands.put("mangaddv", MappedCommand.of(ImmutableList.of("group", "variable", "value"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            String variable = arguments.get("variable");
            String value = arguments.get("value");

            if (variable.equalsIgnoreCase("prefix")) {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp group " + group + " meta addprefix <weight> " + value);
                return;
            }

            if (variable.equalsIgnoreCase("suffix")) {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp group " + group + " meta addsuffix <weight> " + value);
                return;
            }

            plugin.executeCommand(sender, "group " + group + " meta set " + variable + " " + value);
        }));

        commands.put("mangdelv", MappedCommand.of(ImmutableList.of("group", "variable"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            String variable = arguments.get("variable");

            if (variable.equalsIgnoreCase("prefix")) {
                LuckPermsCompat.msg(sender, "Prefixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp group " + group + " meta removeprefix <weight>");
                return;
            }

            if (variable.equalsIgnoreCase("suffix")) {
                LuckPermsCompat.msg(sender, "Suffixes in LuckPerms are applied with weights.");
                LuckPermsCompat.msg(sender, "&cUsage: /lp group " + group + " meta removesuffix <weight>");
                return;
            }

            plugin.executeCommand(sender, "group " + group + " meta unset " + variable);
        }));

        commands.put("manglistv", MappedCommand.of(ImmutableList.of("group"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            plugin.executeCommand(sender, "group " + group + " meta info");
        }));

        commands.put("mangcheckv", MappedCommand.of(ImmutableList.of("group"), (plugin, sender, arguments) -> {
            String group = arguments.get("group");
            plugin.executeCommand(sender, "group " + group + " meta info");
        }));


        /*
         * Utility commands
         */
        commands.put("mansave", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "sync");
        }));

        commands.put("manload", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            plugin.executeCommand(sender, "sync");
        }));

        commands.put("manworld", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "World specific permissions are granted via added command arguments.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Command-Usage");
        }));

        commands.put("manselect", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "World specific permissions are granted via added command arguments.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Command-Usage");
        }));

        commands.put("manclear", MappedCommand.of(ImmutableList.of(), (plugin, sender, arguments) -> {
            LuckPermsCompat.msg(sender, "World specific permissions are granted via added command arguments.");
            LuckPermsCompat.msg(sender, "&7More info can be found here: https://github.com/lucko/LuckPerms/wiki/Command-Usage");
        }));

        return commands.build();
    }

}
