package cc.ahaly.mc.ahanyeve;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHandler {
    private Scoreboard scoreboard = null;
    private static Objective killsObjective;
    private static Objective deathsObjective;
    private static Objective snowballObjective;
    private static int displayState = 0;

    public ScoreboardHandler() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getNewScoreboard();
        }

        // 创建击杀数记分项
        killsObjective = scoreboard.registerNewObjective("kills", "playerKillCount", "击杀数");
        killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 创建死亡数记分项
        deathsObjective = scoreboard.registerNewObjective("deaths", "deathCount", "死亡数");

        // 创建一个用于跟踪扔雪球次数的记分项
        snowballObjective = scoreboard.registerNewObjective("snowballCount", "dummy", "扔雪球数");
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    // 轮播显示计分板
    public static void startRotatingDisplay(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (displayState) {
                    case 0:
                        // 显示击杀数
                        killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                    case 1:
                        // 显示死亡数
                        deathsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                    case 2:
                        // 显示扔雪球数
                        snowballObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        break;
                }
                displayState = (displayState + 1) % 3; // 循环显示
            }
        }.runTaskTimer(plugin, 0L, 20L * 10); // 每10秒切换一次
    }
}