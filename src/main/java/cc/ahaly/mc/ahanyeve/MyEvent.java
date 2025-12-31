package cc.ahaly.mc.ahanyeve;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    // 监听玩家使用树苗进行传送
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // 检查玩家手中的物品是否是传送树苗
        if (item != null && item.getType() == Material.SPRUCE_SAPLING && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(ChatColor.GREEN + "传送树苗")) {
                // 传送玩家到指定地点
                Location targetLocation = new Location(Bukkit.getWorld("world"), -3410, 64, -1102); // 设置目标位置
                player.teleport(targetLocation);

                // 添加传送特效
                player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation(), 50);

                // 发送传送成功消息
                player.sendMessage(ChatColor.GOLD + "你已成功传送到跨年广场！");
                event.setCancelled(true); // 阻止其他交互行为
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 欢迎消息
        player.sendMessage("§a欢迎加入服务器！2026跨年活动正在进行中！" +
                "\n§a物品耐久不会消耗，送你一些雪球打雪仗吧！" +
                "\n§e右键树苗可以传送到跨年广场！"+
                "\n§c注意：服务器数据已备份，当前所有操作将不会保存！");

        // 创建传送用的树苗
        ItemStack sapling = new ItemStack(Material.SPRUCE_SAPLING); // 使用橡树树苗
        ItemMeta meta = sapling.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "传送树苗");
            meta.setLore(java.util.Arrays.asList(ChatColor.GRAY + "右键或左键点击即可传送到跨年广场！"));
            sapling.setItemMeta(meta);
        }
        // 将树苗给予玩家
        player.getInventory().addItem(sapling);

        // 给予玩家雪球
        player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 16));
        player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 16));

        // 特效和烟花展示
        new SendEffect(player, ""); // 自定义方法，确保有实际功能
        new FireworkShow(plugin).launchFireworkShow(player.getLocation(), 3);

        // 如果计时器 BossBar 存在，添加玩家到 BossBar
        if (CountdownCommand.bossBar != null) {
            CountdownCommand.bossBar.addPlayer(player);
        }

        // 新年结束时的奖励及特权
        if (isEnd) {
            if (!player.isOp()) {
                player.setOp(true); // 给予 OP 权限
                player.sendMessage("§6§l新年快乐！" +
                        "\n§a当前所有人已获得 OP 权限，死亡不掉落，尽情玩耍吧！" +
                        "\n§c注意：服务器数据已备份，当前所有操作将不会保存！");
            }
        }
    }

}