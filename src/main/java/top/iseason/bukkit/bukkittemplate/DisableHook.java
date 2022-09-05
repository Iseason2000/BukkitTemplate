package top.iseason.bukkit.bukkittemplate;

import java.util.ArrayList;

/**
 * 在插件 onDisable 时自动调用 onDisable() 方法
 */
public class DisableHook {
    private static final ArrayList<DisableTask> tasks = new ArrayList<>();

    public static void disableAll() {
        for (DisableTask task : tasks) {
            try {
                task.onDisable();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * 添加一个在插件onDisable 时调用的任务
     *
     * @param task 任务
     */
    public static void addTask(DisableTask task) {
        tasks.add(task);
    }

    public interface DisableTask {
        void onDisable();
    }
}
