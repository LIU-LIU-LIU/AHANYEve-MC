package cc.ahaly.mc.ahanyeve;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;


public class MyEvent implements Listener {
    private JavaPlugin plugin;
    public MyEvent(JavaPlugin plugin){
        this.plugin = plugin;
    }

    //物品耐久度
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        // 取消事件，防止耐久度减少
        event.setCancelled(true);
        event.getPlayer().sendTitle("", "§c§l本次耐久消耗已经为你抵消", 3, 6, 3);
    }

    //扔雪球
    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getEntity();

            if (snowball.getShooter() instanceof Player) {
                Player player = (Player) snowball.getShooter();

                // 更新计分板
                ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
                Score score = scoreboardHandler.getScoreboard().getObjective("snowballCount").getScore(player.getName());
                score.setScore(score.getScore() + 1); // 增加扔雪球的计数

                // 在雪球命中的地点放烟花
                if (event.getHitBlock() != null) {
                    // 雪球命中了一个方块
                    SendEffect.launchFirework(event.getHitBlock().getLocation().add(0, 1, 0));
                } else if (event.getHitEntity() != null) {
                    // 雪球命中了一个实体
                    SendEffect.launchFirework(event.getHitEntity().getLocation());
                }

                // 发送消息给玩家
                new SendEffect(player, "");
            }
        }
    }
}