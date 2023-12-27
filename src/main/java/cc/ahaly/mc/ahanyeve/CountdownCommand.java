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
    private JavaPlugin plugin;

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
        BossBar bossBar = getServer().createBossBar("倒计时", BarColor.RED, BarStyle.SOLID);
        bossBar.addPlayer(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT);
                long secondsUntilMidnight = now.until(midnight, ChronoUnit.SECONDS);

                if (secondsUntilMidnight <= 0) {
                    bossBar.setTitle("新的一年开始了!");
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        new SendEffect(player,"§c§l新年快乐！");
                        SendEffect.launchFirework(player.getLocation());
                    }
                    bossBar.setColor(BarColor.GREEN);
                    // 从玩家位置创建冬季
                    createWinterWonderland(player.getLocation(),512);
                    //显示计分板
                    ScoreboardHandler.startRotatingDisplay(plugin);
                    this.cancel();
                } else {
                    bossBar.setTitle("距离新年还有: " + formatTime(secondsUntilMidnight));
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

    //过零点，创建冬季仙境
    public void createWinterWonderland(Location location, int radius) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();
        setSnowBiome(world, centerX, centerZ, radius);
        startSnowing(world);
        // 在半径区域内随机生成雪人
        int snowmenCount = 25; // 假设你想生成10个雪人
        for (int i = 0; i < snowmenCount; i++) {
            Location randomLocation = getRandomLocation(world, location, radius);
            spawnSnowman(world, randomLocation);
        }
    }

    private Random random = new Random();
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
    public void setSnowBiome(World world, int centerX, int centerZ, int radius) {
        int chunkRadius = radius / 16; // 将半径从方块转换为区块
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                Chunk chunk = world.getChunkAt(centerX + dx, centerZ + dz);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = world.getMaxHeight() - 1; y >= 0; y--) {
                            if (chunk.getBlock(x, y, z).getType().isSolid()) {
                                world.setBiome((centerX + dx) * 16 + x, y, (centerZ + dz) * 16 + z, Biome.SNOWY_PLAINS);
                                break;
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
