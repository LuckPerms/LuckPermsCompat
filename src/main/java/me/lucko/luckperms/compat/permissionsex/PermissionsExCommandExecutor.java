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

package me.lucko.luckperms.compat.permissionsex;

import lombok.RequiredArgsConstructor;

import me.lucko.luckperms.compat.LuckPermsCompat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class PermissionsExCommandExecutor implements CommandExecutor {
    private final LuckPermsCompat plugin;
    private final List<PermissionsExCommand> commands;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] a) {
        if (!sender.hasPermission("luckpermscompat.use")) {
            LuckPermsCompat.msg(sender, "&cNo permission.");
            return true;
        }

        List<String> args = new ArrayList<>(Arrays.asList(a));
        if (args.isEmpty()) {
            return plugin.onCommand(sender, command, s, a);
        }

        boolean found = false;
        for (PermissionsExCommand cmd : commands) {
            if (cmd.tryPerform(sender, args)) {
                found = true;
                break;
            }
        }

        return found || plugin.onCommand(sender, command, s, a);
    }
}
