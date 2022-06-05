package top.iseason.bukkit.bukkittemplate.dependency;

import me.lucko.jarrelocator.JarRelocator;
import top.iseason.bukkit.bukkittemplate.TemplatePlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class that handles relocation
 */
public abstract class FileRelocator {
    private static final PluginLib asm = PluginLib.builder()
            .groupId("org.ow2.asm")
            .artifactId("asm")
            .version("9.3")
            .build();

    private static final PluginLib asm_commons = PluginLib.builder()
            .groupId("org.ow2.asm")
            .artifactId("asm-commons")
            .version("9.3")
            .build();

    private static final PluginLib jarRelocator = PluginLib.builder()
            .groupId("me.lucko")
            .artifactId("jar-relocator")
            .version("1.5")
            .build();

    private FileRelocator() {
        throw new AssertionError("Cannot create instances of " + getClass().getName() + ".");
    }

    public static void loadLibs() {
        asm.load(TemplatePlugin.class);
        asm_commons.load(TemplatePlugin.class);
        jarRelocator.load(TemplatePlugin.class);
    }

    public static void remap(File input, File output, Set<Relocation> relocations) throws Exception {
        Map<String, String> mappings = new HashMap<>();
        for (Relocation relocation : relocations) {
            mappings.put(relocation.getPath(), relocation.getNewPath());
        }
        new JarRelocator(input, output, mappings).run();
    }

}
