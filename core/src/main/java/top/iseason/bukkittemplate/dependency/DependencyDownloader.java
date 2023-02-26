package top.iseason.bukkittemplate.dependency;

import org.bukkit.Bukkit;
import org.xml.sax.SAXException;
import top.iseason.bukkittemplate.BukkitTemplate;
import top.iseason.bukkittemplate.ReflectionUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * 依赖下载器 仅支持 group:artifact:version 的格式
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DependencyDownloader {
    /**
     * 被加载到插件Classloader的依赖，默认是加载到 IsolatedClassLoader
     */
    public static final Set<String> assembly = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * 不下载重复的依赖,此为缓存
     */
    private static final Set<String> exists = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * 默认为plugin.yml中声明的依赖，覆盖子依赖中的相同依赖的不同版本
     */
    public static final Set<String> parallel = new HashSet<>();
    /**
     * 储存路径
     */
    public static File parent = new File("libraries");
    /**
     * 下载源
     */
    public List<String> repositories = new ArrayList<>();
    /**
     * 依赖 group:artifact:version to maxDepth
     * maxDepth表示最大依赖解析层数
     */
    public Map<String, Integer> dependencies = new LinkedHashMap<>();

    /**
     * 下载依赖
     * 比如 org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10
     *
     * @param dependency 依赖地址
     * @param depth      依赖深度
     * @param maxDepth   最大依赖深度
     * @return true 表示加载依赖成功
     */
    public static boolean downloadDependency(String dependency, int depth, int maxDepth, List<String> repositories) {
        String[] split = dependency.split(":");
        if (split.length != 3) {
            Bukkit.getLogger().warning("Invalid dependency " + dependency);
            return false;
        }
        String groupId = split[0];
        String artifact = split[1];
        String classId = groupId + ":" + artifact;
        if (exists.contains(classId) || (depth > 1 && parallel.contains(classId))) return true;
        exists.add(classId);
        String version = split[2];
        String suffix = groupId.replace(".", "/") + "/" + artifact + "/" + version + "/";
        File saveLocation = new File(parent, suffix.replace("/", File.separator));
        String jarName = artifact + "-" + version + ".jar";
        String pomName = artifact + "-" + version + ".pom";
        File jarFile = new File(saveLocation, jarName);
        File pomFile = new File(saveLocation, pomName);
        String type = "isolated";
        //已经存在
        if (jarFile.exists()) {
            try {
                type = addUrl(classId, jarFile.toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            boolean downloaded = false;
            for (String repository : repositories) {
                try {
                    String jarStr = repository + suffix + jarName;
                    URL url = new URL(jarStr);
                    if (!downloadFile(url, jarFile)) {
                        jarFile.delete();
                        continue;
                    }
                    type = addUrl(classId, jarFile.toURI().toURL());
                    downloaded = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!downloaded) return false;
        }
        Bukkit.getLogger().info("[" + BukkitTemplate.getPlugin().getName() + "] Loaded library " + dependency + " " + type);
        if (depth == maxDepth) return true;

        for (String repository : repositories) {
            try {
                URL pomUrl = new URL(repository + suffix + pomName);
                if (!pomFile.exists() && !downloadFile(pomUrl, pomFile)) {
                    pomFile.delete();
                    continue;
                }
                try {
                    XmlParser xmlDependency = new XmlParser(pomFile);
                    for (String subDependency : xmlDependency.getDependency()) {
                        if (!downloadDependency(subDependency, depth + 1, maxDepth, repositories)) {
                            Bukkit.getLogger().warning("Loading sub dependency" + subDependency + " error!");
                        }
                    }
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    Bukkit.getLogger().warning("Loading file " + pomFile + " error!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static String addUrl(String classId, URL url) {
        if (assembly.contains(classId)) {
            ReflectionUtil.addSubURL(url);
            return "assemble";
        } else {
            ReflectionUtil.addURL(url);
            return "isolated";
        }
    }

    /**
     * 下载文件并校验
     *
     * @param url  下载地址
     * @param file 保存目录
     * @return 是否下载并校验成功
     */
    public static boolean downloadFile(URL url, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (!download(url, file)) {
            return false;
        }
        //下载sha文件
        try {
            File sha = new File(file + ".sha1");
            URL shaUrl = new URL(url + ".sha1");
            sha.createNewFile();
            if (!download(shaUrl, sha)) return false;
            return checkSha(file, sha);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 下載文件，超时5秒
     *
     * @param url  文件链接
     * @param file 保存路径
     * @return true if success
     */
    private static boolean download(URL url, File file) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) return false;
        } catch (Exception e) {
            return false;
        }
        if (url.toString().endsWith(".jar"))
            Bukkit.getLogger().info("Downloading " + url);
        try (InputStream is = connection.getInputStream()) {
            Files.copy(is, file.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件的sha1值。
     */
    static String sha1(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            StringBuilder sha1 = new StringBuilder(new BigInteger(1, digest.digest()).toString(16));
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1.insert(0, "0");
                }
            }
            return sha1.toString();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 校验文件sha值
     *
     * @param file 待校验的文件
     * @param sha  sha文件
     * @return true 如果通过的话，反之false
     */
    static boolean checkSha(File file, File sha) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(sha));
            String shaStr = buffer.readLine();
            buffer.close();
            String s = sha1(file);
            return Objects.equals(s, shaStr);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 添加仓库地址
     *
     * @param repository 仓库地址，请以 "/" 结尾
     * @return 自身
     */
    public DependencyDownloader addRepository(String repository) {
        String temp = repository;
        if (!repository.endsWith("/")) temp = repository + "/";
        repositories.add(temp);
        return this;
    }

    /**
     * 添加需要的依赖
     *
     * @param dependency 依赖, 将会下载依赖的子依赖(2层)
     * @return 自身
     */
    public DependencyDownloader addDependency(String dependency) {
        dependencies.put(dependency, 2);
        return this;
    }

    /**
     * 下载所有积压的依赖
     *
     * @return true if success
     */
    public boolean start(boolean parallel) {
        if (parallel) {
            AtomicBoolean failure = new AtomicBoolean(false);
            dependencies.entrySet().parallelStream().forEach(entry -> {
                        if (failure.get()) return;
                        failure.set(!downloadDependency(entry.getKey(), entry.getValue()));
                    }
            );
            dependencies.clear();
            return !failure.get();
        } else {
            for (Map.Entry<String, Integer> entry : dependencies.entrySet()) {
                if (!downloadDependency(entry.getKey(), entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 直接下载并加载依赖
     *
     * @param dependency 依赖
     * @param maxDepth   依赖解析层数
     * @return true if success
     */
    public boolean downloadDependency(String dependency, int maxDepth) {
        return downloadDependency(dependency, 1, maxDepth, repositories);
    }

    /**
     * 直接下载并加载依赖以及依赖的子依赖
     *
     * @param dependency 依赖
     * @return true if success
     */
    public boolean downloadDependency(String dependency) {
        return downloadDependency(dependency, 1, 2, repositories);
    }
}
