package cc.ahaly.mc.ahanyeve;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AHANYEve extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new MyEvent(this), this);
        // 将命令 "countdown" 的处理器设置为 CountdownCommand 的实例
        Objects.requireNonNull(this.getCommand("countdown")).setExecutor(new CountdownCommand(this));
        getLogger().info("跨年插件已启用.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ScoreboardHandler.stopRotatingDisplay();
        CountdownCommand.bossBar.removeAll();  // 移除所有玩家，从而使得boss栏不再显示
        getLogger().info("跨年插件已禁用.");
    }

}
