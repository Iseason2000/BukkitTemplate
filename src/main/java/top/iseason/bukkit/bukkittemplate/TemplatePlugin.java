package top.iseason.bukkit.bukkittemplate;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig;
import top.iseason.bukkit.bukkittemplate.dependency.PluginLib;
import top.iseason.bukkit.bukkittemplate.ui.UIListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class TemplatePlugin extends JavaPlugin {
    private static final List<Class<?>> classes = loadClass();
    private static final KotlinPlugin ktPlugin = findInstance();
    private static TemplatePlugin plugin;

    static {
        //加载重定向库
//        FileRelocator.loadLibs();
        //加载依赖
        PluginLib.loadLibs();
    }

    private static KotlinPlugin findInstance() {
        for (Class<?> aClass : classes) {
            if (KotlinPlugin.class.isAssignableFrom(aClass)) {
                try {
                    Field instance = aClass.getDeclaredField("INSTANCE");
                    instance.setAccessible(true);
                    return (KotlinPlugin) instance.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException();
    }

    private static void callConfigsInstance() {
        for (Class<?> aClass : classes) {
            if (SimpleYAMLConfig.class.isAssignableFrom(aClass)) {
                try {
                    Field instance = aClass.getDeclaredField("INSTANCE");
                    instance.setAccessible(true);
                    instance.get(null);
                    instance.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                }
            }
        }
    }

    private static List<Class<?>> loadClass() {
        URL location = TemplatePlugin.class.getProtectionDomain().getCodeSource().getLocation();
        ArrayList<Class<?>> classes = new ArrayList<>();
        File srcFile;
        try {
            srcFile = new File(location.toURI());
        } catch (URISyntaxException e) {
            try {
                URI uri = ((JarURLConnection) location.openConnection()).getJarFileURL().toURI();
                srcFile = new File(uri);
            } catch (URISyntaxException | IOException ex) {
                srcFile = new File(location.getPath());
            }
        }
        try (JarFile jarFile = new JarFile(srcFile)) {
            jarFile.stream().forEach((it) -> {
                String name = it.getName();
                if (!name.endsWith(".class") || name.startsWith("META-INF")) {
                    return;
                }
                Class<?> aClass;
                try {
                    aClass = Class.forName(name.replace('/', '.').substring(0, name.length() - 6), false, TemplatePlugin.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    return;
                }
                if (SimpleYAMLConfig.class.isAssignableFrom(aClass)) {
                    classes.add(aClass);
                    return;
                }
                if (KotlinPlugin.class.isAssignableFrom(aClass) && !KotlinPlugin.class.getName().equals(aClass.getName())) {
                    classes.add(aClass);
                }
            });
        } catch (IOException ignored) {
        }
        return classes;
    }

    public static TemplatePlugin getPlugin() {
        return plugin;
    }

    public static KotlinPlugin getKtPlugin() {
        return ktPlugin;
    }

    @Override
    public void onLoad() {
        plugin = this;
        ktPlugin.onLoad();
    }

    @Override
    public void onDisable() {
        UIListener.INSTANCE.onDisable();
        ktPlugin.onDisable();
    }

    @Override
    public void onEnable() {
        UIListener.INSTANCE.hashCode();
        callConfigsInstance();
        ktPlugin.onEnable();
        if (ktPlugin.getBstatsID() > 0) {
            new Metrics(this, ktPlugin.getBstatsID());
        }
    }
}
