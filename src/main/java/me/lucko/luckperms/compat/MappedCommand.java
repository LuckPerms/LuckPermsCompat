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

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Abstract command executor for remapped commands
 */
@RequiredArgsConstructor(staticName = "of")
public class MappedCommand implements CommandExecutor {

    /**
     * Plugin instance. This is set when the command is registered with the server
     */
    @Setter
    private LuckPermsCompat plugin = null;

    /**
     * A list of expected arguments
     */
    private final List<String> arguments;

    /**
     * The function responsible for remapping and executing the LP command equivalent
     */
    private final MappingFunction function;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] a) {
        if (!sender.hasPermission("luckpermscompat.use")) {
            LuckPermsCompat.msg(sender, "&cNo permission.");
            return true;
        }

        List<String> args = new ArrayList<>(Arrays.asList(a));

        if (args.size() < arguments.size()) {
            LuckPermsCompat.msg(sender, "&cUsage: /" + s + " " + getUsage());
            return true;
        }

        Map<String, String> values = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(-1);
        for (String expected : arguments) {
            values.put(expected, args.get(counter.incrementAndGet()));
        }

        function.perform(plugin, sender, values);
        return true;
    }

    public String getUsage() {
        return arguments.stream().map(str -> "<" + str + ">").collect(Collectors.joining(" "));
    }
}
