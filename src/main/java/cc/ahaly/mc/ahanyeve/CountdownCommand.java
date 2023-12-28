package cc.ahaly.mc.ahanyeve;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class CountdownCommand implements CommandExecutor {
    static BossBar bossBar;
    private JavaPlugin plugin;
    private Random random = new Random();

    private int currentRadius = 0; // 当前处理的半径
    private final int maxRadius = 256; // 最大半径，您可以根据需要调整
    private final int radiusIncrement = 32; // 每次增加的半径，您可以根据需要调整

    public CountdownCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("countdown")) {
            if (sender instanceof Player && sender.isOp()) {
                startCountdown((Player) sender);
                return true;
            } else {
                sender.sendMessage("只有OP可以执行这个命令。");
                return true;
            }
        }
        return false;
    }

    private void startCountdown(Player player) {
        bossBar = getServer().createBossBar("倒计时", BarColor.RED, BarStyle.SOLID);
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                // 将目标时间设置为明天的午夜（今天日期加一天，时间设为零点）
                LocalDateTime targetTime = now.toLocalDate().plusDays(1).atStartOfDay();

                if (now.isAfter(targetTime) || now.equals(targetTime)) { // 检查当前时间是否已经到了或过了目标时间
                    bossBar.setTitle("新的一年开始了!");
                    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                        new SendEffect(onlinePlayer, "§c§l新年快乐！");
                        SendEffect.launchFirework(onlinePlayer.getLocation());
                    }
                    bossBar.setColor(BarColor.GREEN);
                    createWinterWonderland(player.getLocation());
                    ScoreboardHandler.startRotatingDisplay(plugin);
                    this.cancel();
                } else {
                    long secondsUntilTarget = ChronoUnit.SECONDS.between(now, targetTime);
                    bossBar.setTitle("距离新年还有: " + formatTime(secondsUntilTarget));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 每秒更新一次
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    //到点创建冬季仙境
    public void createWinterWonderland(Location location) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();

        new BukkitRunnable() {
            @Override
            public void run() {
                // 检查是否已处理完整个区域
                if (currentRadius >= maxRadius) {
                    this.cancel();
                    return;
                }

                // 更新群系
                setSnowBiome(world, centerX, centerZ, currentRadius, currentRadius + radiusIncrement);
                currentRadius += radiusIncrement;

                // 生成雪人
                if (currentRadius % (radiusIncrement * 5) == 0) { // 每扩展一定范围时生成雪人
                    for (int i = 0; i < 5; i++) {
                        Location randomLocation = getRandomLocation(world, location, currentRadius);
                        spawnSnowman(world, randomLocation);
                    }
                }

                // 开始下雪
                if (currentRadius == radiusIncrement) {
                    startSnowing(world);
                }
            }
        }.runTaskTimer(plugin, 0L, 20 * 60L); // 每分钟更新一次
    }


    // 获取半径内的随机位置
    private Location getRandomLocation(World world, Location center, int radius) {
        int randomX = random.nextInt(radius * 2) - radius;
        int randomZ = random.nextInt(radius * 2) - radius;
        int y = world.getHighestBlockYAt(center.getBlockX() + randomX, center.getBlockZ() + randomZ);
        // 确保不在水中生成雪人
        while (world.getBlockAt(new Location(world, center.getBlockX() + randomX, y, center.getBlockZ() + randomZ)).getType() == Material.WATER) {
            y--;
        }
        return new Location(world, center.getBlockX() + randomX, y, center.getBlockZ() + randomZ);
    }


    //设置群系
    public void setSnowBiome(World world, int centerX, int centerZ, int startRadius, int endRadius) {
        // 将半径从方块转换为区块
        int startChunkRadius = startRadius / 16;
        int endChunkRadius = endRadius / 16;

        for (int dx = -endChunkRadius; dx <= endChunkRadius; dx++) {
            for (int dz = -endChunkRadius; dz <= endChunkRadius; dz++) {
                // 计算当前区块与中心点的距离
                double distance = Math.sqrt(dx * dx + dz * dz);

                // 检查当前区块是否在指定的半径范围内
                if (distance >= startChunkRadius && distance <= endChunkRadius) {
                    Chunk chunk = world.getChunkAt(centerX / 16 + dx, centerZ / 16 + dz);
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                                if (chunk.getBlock(x, y, z).getType().isSolid()) {
                                    world.setBiome((centerX + dx * 16) + x, y, (centerZ + dz * 16) + z, Biome.SNOWY_PLAINS);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //下雪
    public void startSnowing(World world) {
        world.setStorm(true);
        world.setWeatherDuration(Integer.MAX_VALUE); // 设置持续时间
    }

    //生成雪人
    public void spawnSnowman(World world, Location location) {
        world.spawnEntity(location, EntityType.SNOWMAN);
    }
}
