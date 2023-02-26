package com.example.bukkit.templateplugin;

import top.iseason.bukkittemplate.BukkitPlugin;

public final class MyPlugin implements BukkitPlugin {

    private static final MyPlugin INSTANCE = new MyPlugin();

    public static MyPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        System.out.println("这是一个java环境示例");
        System.out.println(MyPlugin.getInstance().getJavaPlugin());
    }
}
