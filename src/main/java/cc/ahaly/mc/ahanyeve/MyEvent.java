package cc.ahaly.mc.ahanyeve;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import static cc.ahaly.mc.ahanyeve.CountdownCommand.isEnd;

public class MyEvent implements Listener {
    private JavaPlugin plugin;

    public MyEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //物品耐久度
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        // 取消事件，防止耐久度减少
        event.setCancelled(true);
        // 使用ActionBar显示简短提示
        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7耐久度已为你抵消"));
    }


    //不消耗雪球
    @EventHandler
    public void onSnowballThrow(ProjectileLaunchEvent event) {
        // 检查投掷者是否是玩家，且投掷物是否是雪球
        if (event.getEntity().getShooter() instanceof Player && event.getEntity().getType().toString().equals("SNOWBALL")) {
            Player player = (Player) event.getEntity().getShooter();

            // 给玩家重新添加一个雪球
            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
        }
    }
    //扔雪球
    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getEntity();

            if (snowball.getShooter() instanceof Player) {
                Player player = (Player) snowball.getShooter();

                // 更新计分板
                Score score = ScoreboardHandler.getScoreboard().getObjective("snowballCount").getScore(player.getName());
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("§a欢迎进入服务器！现在是2025跨年祭事件。" +
                "\n§a雪球和物品耐久不会消耗，送你些雪球打雪仗吧。");
        // 发送消息给玩家
        new SendEffect(player, "");
        new FireworkShow(plugin).launchFireworkShow(player.getLocation(), 3);

        if (CountdownCommand.bossBar != null) {
            CountdownCommand.bossBar.addPlayer(event.getPlayer());
        }
        if (isEnd) {
            // 添加一些雪球和苹果
            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 16));
            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 16));
            if (!player.isOp()) {
                player.setOp(true);
                player.sendMessage("§a欢迎加入服务器！2025新年快乐！" +
                        "\n§a死亡不掉落，且所有人均获得 OP 权限！" +
                        "\n§e§l服务器所有数据均已备份，请不要进行生产工作，现在服务器不会保留数据!");
            }
        }
    }
}