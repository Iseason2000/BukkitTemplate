package top.iseason.bukkit.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class SimpleLogger extends PluginLogger {
    public static boolean isDebugEnabled = false;
    public static String prefix;
    private static ArrayList<Pattern> filters = new ArrayList<>();

    /**
     * Creates a new PluginLogger that extracts the name from a plugin.
     *
     * @param context A reference to the plugin
     */
    public SimpleLogger(@NotNull Plugin context) {
        super(context);
        prefix = context.getDescription().getPrefix();
        if (prefix == null) prefix = toColor("[" + context.getDescription().getName() + "] ");
    }

    public static void addFilter(String pattern) {
        filters.add(Pattern.compile(pattern));
    }

    @Override
    public void log(Level level, String msg) {
        for (Pattern filter : filters) {
            if (filter.matcher(msg).find()) return;
        }
        Bukkit.getLogger().log(level, prefix + msg);
    }

    @Override
    public void log(@NotNull LogRecord logRecord) {
        logRecord.setMessage(prefix + logRecord.getMessage());
        Bukkit.getLogger().log(logRecord);
    }

    public void debug(String msg) {
        if (isDebugEnabled)
            info(msg);
    }

    private String toColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
