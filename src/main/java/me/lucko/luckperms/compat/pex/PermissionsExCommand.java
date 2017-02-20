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

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import me.lucko.luckperms.compat.LuckPermsCompat;
import me.lucko.luckperms.compat.MappingFunction;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract command instance for remapped commands
 */
@RequiredArgsConstructor(staticName = "of")
public class PermissionsExCommand {

    /**
     * Plugin instance. This is set when the command is registered with the server
     */
    @Setter
    private LuckPermsCompat plugin = null;

    /**
     * The commands structure
     */
    private final List<String> structure;

    /**
     * The function responsible for remapping and executing the LP command equivalent
     */
    private final MappingFunction function;

    public boolean tryPerform(CommandSender sender, List<String> args) {
        if (args.size() < structure.size()) {
            return false;
        }

        Map<String, String> values = new HashMap<>();

        for (int i = 0; i < structure.size(); i++) {
            String s = structure.get(i);
            boolean present = i >= args.size();

            if (!isChangeable(s)) {
                // it must match
                if (!present) {
                    return false;
                }

                String value = args.get(i);
                if (!s.equalsIgnoreCase(value)) {
                    return false;
                }

                continue;
            }

            // Pull the value
            String val = null;
            if (!isOptional(s) && !present) {
                return false;
            }
            if (present) {
                val = args.get(i);
            }

            if (val != null) {
                values.put(stripIndicators(s), val);
            }
        }

        function.perform(plugin, sender, values);
        return true;
    }

    public String getUsage() {
        return structure.stream().collect(Collectors.joining(" "));
    }

    private static boolean isOptional(String arg) {
        return arg.startsWith("[") && arg.endsWith("]");
    }

    private static boolean isPositional(String arg) {
        return arg.startsWith("<") && arg.endsWith(">");
    }

    private static boolean isChangeable(String arg) {
        return isOptional(arg) || isPositional(arg);
    }

    private static String stripIndicators(String arg) {
        if (isChangeable(arg)) {
            return arg.substring(1, arg.length() - 1);
        } else {
            return arg;
        }
    }
}
