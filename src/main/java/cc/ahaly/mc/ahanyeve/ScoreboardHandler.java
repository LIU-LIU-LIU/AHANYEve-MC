package cc.ahaly.mc.ahanyeve;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHandler {
    private static Objective killsObjective;
    private static Objective deathsObjective;
    private static Objective snowballObjective;
    private static int displayState = 0;
    private static Scoreboard scoreboard;
    private static BukkitTask rotatingTask = null;

    static {
        // 静态初始化块，只执行一次
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getNewScoreboard();
            // 初始化 Objective 实例
            killsObjective = scoreboard.registerNewObjective("kills", "playerKillCount", "击杀数");
            deathsObjective = scoreboard.registerNewObjective("deaths", "deathCount", "死亡数");
            snowballObjective = scoreboard.registerNewObjective("snowballCount", "dummy", "扔雪球数");
        }
    }


    // 轮播显示计分板

    public static void startRotatingDisplay(JavaPlugin plugin) {
        if (scoreboard == null) {
            return; // 如果 scoreboard 为空，则直接返回
        }

        if (rotatingTask != null && !rotatingTask.isCancelled()) {
            rotatingTask.cancel(); // 取消已有的轮播任务
        }

        rotatingTask = new BukkitRunnable() {
            @Override
            public void run() {
                switch (displayState) {
                    case 0:
                        killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                    case 1:
                        deathsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                    case 2:
                        snowballObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                }
                displayState = (displayState + 1) % 3; // 循环显示
                applyScoreboardToAllOnlinePlayers(); // 更新所有在线玩家的计分板
            }
        }.runTaskTimer(plugin, 0L, 20L * 5); // 每5秒切换一次
    }

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }


    public static void stopRotatingDisplay() {
        if (rotatingTask != null) {
            rotatingTask.cancel();
            rotatingTask = null;
        }
        // 清除计分板显示
        if (scoreboard != null) {
            killsObjective.setDisplaySlot(null);
            deathsObjective.setDisplaySlot(null);
            snowballObjective.setDisplaySlot(null);
        }
    }

    public static void applyScoreboardToPlayer(Player player) {
        if (scoreboard != null) {
            player.setScoreboard(scoreboard);
        }
    }

    public static void applyScoreboardToAllOnlinePlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            applyScoreboardToPlayer(player);
        }
    }
}