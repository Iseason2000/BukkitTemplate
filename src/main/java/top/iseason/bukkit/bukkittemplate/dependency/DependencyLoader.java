package top.iseason.bukkit.bukkittemplate.dependency;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.Unsafe;
import top.iseason.bukkit.bukkittemplate.TemplatePlugin;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.requireNonNull;

/**
 * 加载依赖，如歌不存在则下载
 */
@SuppressWarnings("restriction")
public class DependencyLoader {

    private static final List<DependencyLoader> toInstall = new ArrayList<>();
    private static final MethodHandle addUrlHandle;
    private static final Object ucp;
    private static final Supplier<LibrariesOptions> librariesOptions = memoize(() -> {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(requireNonNull(TemplatePlugin.class.getClassLoader().getResourceAsStream("plugin.yml"), "Jar does not contain plugin.yml")));
        if (yamlConfiguration.contains("runtime-libraries"))
            return LibrariesOptions.fromMap((requireNonNull(yamlConfiguration.getConfigurationSection("runtime-libraries"))));
        return null;
    });
    private static final Supplier<File> libFile = memoize(() -> {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(requireNonNull(TemplatePlugin.class.getClassLoader().getResourceAsStream("plugin.yml"), "Jar does not contain plugin.yml")));
        String name = yamlConfiguration.getString("name");
        LibrariesOptions options = librariesOptions.get();
        String folder = options.librariesFolder;
        for (Entry<String, RuntimeLib> lib : options.libraries.entrySet()) {
            RuntimeLib runtimeLib = lib.getValue();
            Builder b = runtimeLib.builder();
            toInstall.add(b.build());
        }
        File file;
        if (folder.contains("@Server:")) file = new File(folder.replace("@Server:", ""));
        else file = new File(Bukkit.getUpdateFolderFile().getParentFile() + File.separator + name, folder);
        file.mkdirs();
        return file;
    });

    static {
        //通过反射获取ClassLoader addUrl 方法，因为涉及java17 无奈使用UnSafe方法
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucp = unsafe.getObject(DependencyLoader.class.getClassLoader(), unsafe.objectFieldOffset(ucpField));
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            addUrlHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadLibs() {
        libFile.get();
        for (DependencyLoader dependencyLoader : toInstall) {
            dependencyLoader.load();
        }
    }

    public final String groupId, artifactId, version, repository;


    public DependencyLoader(String groupId, String artifactId, String version, String repository) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
    }


    /**
     * Creates a standard builder
     *
     * @return The newly created builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} that downloads its dependency from a URL.
     *
     * @param url URL to download
     * @return The newly created builder
     */
    public static Builder fromURL(@NotNull String url) {
        return new Builder().fromURL(url);
    }

    /**
     * Returns a new {@link Builder}
     *
     * @param xml XML to parse. Must be exactly like the one in maven.
     * @return A new {@link Builder} instance, derived from the XML.
     * @throws IllegalArgumentException If the specified XML cannot be parsed.
     */
    public static Builder parseXML(@Language("XML") @NotNull String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return builder()
                    .groupId(doc.getElementsByTagName("groupId").item(0).getTextContent())
                    .artifactId(doc.getElementsByTagName("artifactId").item(0).getTextContent())
                    .version(doc.getElementsByTagName("version").item(0).getTextContent());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Failed to parse XML: " + e.getMessage());
        }
    }

    /**
     * 判断类是否存在
     *
     * @param className 类名
     * @return true 如果存在的话
     */
    public static boolean classExists(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public void addURL(URL url) {
        try {
            addUrlHandle.invoke(ucp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Supplier<T> memoize(@NotNull Supplier<T> delegate) {
        return new MemoizingSupplier<>(delegate);
    }

    /**
     * 加载这个依赖
     */
    public void load() {
        LibrariesOptions options = librariesOptions.get();
        if (options == null) return;
        String name = artifactId + "-" + version;
        File parent = libFile.get();
        String folder = parent.toString() + File.separatorChar + groupId.replace('.', File.separatorChar) + File.separatorChar + artifactId + File.separatorChar + version;
        File saveLocation = new File(folder, name + ".jar");
        //不存在则下载
        if (!saveLocation.exists()) {
            Bukkit.getLogger().info("[DependencyDownloader] Downloading library " + name + " please wait");
            try {
                URL url = asURL();
                saveLocation.getParentFile().mkdirs();
                saveLocation.createNewFile();
                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath(), REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to download dependency: " + artifactId);
            }
        }
        try {
            addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation, e);
        }
    }

    /**
     * 创建这个依赖的下载链接
     *
     * @return 依赖的下载链接
     * @throws MalformedURLException If the URL is malformed.
     */
    public URL asURL() throws MalformedURLException {
        String repo = repository;
        if (!repo.endsWith("/")) {
            repo += "/";
        }
        repo += "%s/%s/%s/%s-%s.jar";

        String url = String.format(repo, groupId.replace(".", "/"), artifactId, version, artifactId, version);
        return new URL(url);
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", repository='" + repository + '\'' +
                '}';
    }

    public static class Builder {

        private String url = null;
        private String group, artifact, version, repository = "https://repo1.maven.org/maven2/";

        protected Builder() {
        }

        private static <T> T n(T t, String m) {
            return requireNonNull(t, m);
        }

        /**
         * 从直链下载依赖
         */
        public Builder fromURL(@NotNull String url) {
            this.url = n(url, "provided URL is null!");
            return this;
        }

        /**
         * 设置依赖包名
         */
        public Builder groupId(@NotNull String group) {
            this.group = n(group, "groupId is null!");
            return this;
        }

        /**
         * 设置依赖ID
         */
        public Builder artifactId(@NotNull String artifact) {
            this.artifact = n(artifact, "artifactId is null!");
            return this;
        }

        /**
         * 设置依赖版本 比如version("1.2.3")
         */
        public Builder version(@NotNull String version) {
            this.version = n(version, "version is null!");
            return this;
        }

        /**
         * 设置依赖版本，比如version(1,2,3)
         *
         * @param numbers An array of numbers to join using "."
         * @return This builder
         */
        public Builder version(int... numbers) {
            StringJoiner version = new StringJoiner(".");
            for (int i : numbers) version.add(Integer.toString(i));
            return version(version.toString());
        }

        /**
         * 设置仓库地址，默认为maven
         *
         * @param repository New repository to set
         * @return This builder
         */
        public Builder repository(@NotNull String repository) {
            this.repository = requireNonNull(repository);
            return this;
        }

        /**
         * 设置仓库为jitpack
         */
        public Builder jitpack() {
            return repository("https://jitpack.io/");
        }

        /**
         * 设置仓库为jcenter
         */
        public Builder jcenter() {
            return repository("https://jcenter.bintray.com/");
        }

        /**
         * 设置仓库为mavenCentral
         */
        public Builder mavenCentral() {
            return repository("https://repo1.maven.org/maven2/");
        }

        /**
         * 设置仓库为aikar
         */
        public Builder aikar() {
            return repository("https://repo.aikar.co/content/groups/aikar/");
        }

        /**
         * 构建依赖，对象不可修改
         *
         * @throws NullPointerException 如果没有这个依赖
         */
        public DependencyLoader build() {
            if (url != null)
                return new StaticURLDependencyLoader(group, n(artifact, "artifactId"), n(version, "version"), repository, url);
            return new DependencyLoader(n(group, "groupId"), n(artifact, "artifactId"), n(version, "version"), n(repository, "repository"));
        }

    }

    private static class LibrariesOptions {

        private String librariesFolder = "libraries";
        private Map<String, RuntimeLib> libraries = Collections.emptyMap();

        public static LibrariesOptions fromMap(@NotNull ConfigurationSection section) {
            LibrariesOptions options = new LibrariesOptions();
            options.librariesFolder = section.getString("libraries-folder", "libs");
            options.librariesFolder = options.librariesFolder.replace('\\', File.separatorChar).replace('/', File.separatorChar);
            options.libraries = new HashMap<>();
            Map<String, Map<String, Object>> declaredLibs = new HashMap<>();
            ConfigurationSection libs = section.getConfigurationSection("libraries");
            if (libs == null) throw new NullPointerException("必须至少有一个依赖");
            for (String lib : libs.getKeys(false)) {
                Map<String, Object> values = requireNonNull(libs.getConfigurationSection(lib)).getValues(true);
                declaredLibs.put(lib, values);
            }
            if (!declaredLibs.isEmpty())
                for (Entry<String, Map<String, Object>> lib : declaredLibs.entrySet()) {
                    options.libraries.put(lib.getKey(), RuntimeLib.fromMap(lib.getValue()));
                }
            return options;
        }

    }

    private static class RuntimeLib {

        @Language("XML")
        private String xml = null;
        private String url = null;
        private String groupId = null, artifactId = null, version = null;
        private String repository = null;

        static RuntimeLib fromMap(Map<String, Object> map) {
            RuntimeLib lib = new RuntimeLib();
            lib.xml = (String) map.get("xml");
            lib.url = (String) map.get("url");
            lib.groupId = (String) map.get("groupId");
            lib.artifactId = (String) map.get("artifactId");
            lib.version = (String) map.get("version");
            lib.repository = (String) map.get("repository");
            return lib;
        }

        Builder builder() {
            Builder b;
            if (url != null)
                b = fromURL(url);
            else if (xml != null)
                b = parseXML(xml);
            else
                b = new Builder();
            if (groupId != null) b.groupId(groupId);
            if (artifactId != null) b.artifactId(artifactId);
            if (version != null) b.version(version);
            if (repository != null) b.repository(repository);
            return b;
        }

    }

    private static class StaticURLDependencyLoader extends DependencyLoader {

        private final String url;

        public StaticURLDependencyLoader(String groupId, String artifactId, String version, String repository, String url) {
            super(groupId, artifactId, version, repository);
            this.url = url;
        }

        @Override
        public URL asURL() throws MalformedURLException {
            return new URL(url);
        }
    }

    // legally stolen from guava's Suppliers.memoize
    static class MemoizingSupplier<T> implements Supplier<T>, Serializable {

        final Supplier<T> delegate;
        transient volatile boolean initialized;
        // "value" does not need to be volatile; visibility piggy-backs
        // on volatile read of "initialized".
        transient T value;

        MemoizingSupplier(Supplier<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            // A 2-field variant of Double Checked Locking.
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        T t = delegate.get();
                        value = t;
                        initialized = true;
                        return t;
                    }
                }
            }
            return value;
        }
    }
}
