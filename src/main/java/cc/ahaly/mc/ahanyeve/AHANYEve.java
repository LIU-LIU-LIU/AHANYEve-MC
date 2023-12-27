package cc.ahaly.mc.ahanyeve;

import org.bukkit.plugin.java.JavaPlugin;

public final class AHANYEve extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new MyEvent(), this);
        getLogger().info("跨年插件已启用.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("跨年插件已禁用.");
    }
}
