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
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class CountdownCommand implements CommandExecutor {
    //创建一个静态变量用于确认倒计时结束
    static boolean isEnd = false;
    boolean isCommand = false;
    static BossBar bossBar;
    private JavaPlugin plugin;
    private Random random = new Random();
    private int currentRadius = 0; // 当前处理的半径
    private final int MAXRADIUS = 256; // 最大半径，您可以根据需要调整
    private final int RADIUS_INCREMENT = 32; // 每次增加的半径，您可以根据需要调整
    public CountdownCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isCommand){
            if (cmd.getName().equalsIgnoreCase("countdown")) {
                if (sender instanceof Player && sender.isOp()) {
                    startCountdown((Player) sender);
                    isCommand = true;
                } else {
                    sender.sendMessage("只有OP可以执行这个命令。");
                }
                return true;
            }
        } else {
            sender.sendMessage("请勿重复运行。");
        }
        return false;
    }

    private void startCountdown(Player player) {
        bossBar = getServer().createBossBar("倒计时", BarColor.RED, BarStyle.SOLID);
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            bossBar.addPlayer(onlinePlayer);
        }
        createWinterWonderland(player.getLocation());
        ScoreboardHandler.startRotatingDisplay(plugin);
// 设置目标时间为 2025 年 1 月 1 日 00:00:00
//        LocalDateTime targetTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0);
        LocalDateTime targetTime = LocalDateTime.now().plusMinutes(2); // 设置目标时间为当前时间加1分钟
        // 计算当前时间与目标时间之间的秒数差
        long secondsUntilTarget = ChronoUnit.SECONDS.between(LocalDateTime.now(), targetTime);
        // 计算目标时间应该对应的游戏时间（Minecraft的游戏时间是24000一周期，0对应日出）
        long targetGameTime = (24000L * ChronoUnit.DAYS.between(LocalDateTime.now(), targetTime)) % 24000;
        // 更新游戏时间为目标时间对应的游戏时间
        getServer().getWorlds().forEach(world -> {
            world.setFullTime(targetGameTime);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(targetTime) ) { // 检查当前时间是否已经到了
                    bossBar.setTitle("2025新年快乐!");
                    isEnd = true;
                    for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                        new SendEffect(onlinePlayer, "§c§l新年快乐！\n现在所有人都有op，且死亡不掉落随意玩耍吧。");
                        player.sendMessage("§c§l新年快乐！\n现在所有人都有op，且死亡不掉落随意玩耍吧。");
                        SendEffect.launchFirework(onlinePlayer.getLocation());
                        new FireworkShow(plugin).launchFireworkShow(onlinePlayer.getLocation(), 10);
                    }
                    bossBar.setColor(BarColor.GREEN);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");
                    // 设置天气为晴天，保证可以看到太阳
                    Objects.requireNonNull(Bukkit.getWorld("world")).setStorm(false);
                    grantAllPlayersOP();
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


    // 授予所有在线玩家OP
    private void grantAllPlayersOP() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp()) {
                player.setOp(true);
                player.sendMessage("§a你已获得 OP 权限！");
            }
        }
    }

    //创建冬季仙境
    public void createWinterWonderland(Location location) {
        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();

        new BukkitRunnable() {
            @Override
            public void run() {
                // 检查是否已处理完整个区域
                if (currentRadius >= MAXRADIUS) {
                    this.cancel();
                    return;
                }

                // 更新群系
                setSnowBiome(world, centerX, centerZ, currentRadius, currentRadius + RADIUS_INCREMENT);
                currentRadius += RADIUS_INCREMENT;

                // 生成雪人
                if (currentRadius % (RADIUS_INCREMENT * 5) == 0) { // 每扩展一定范围时生成雪人
                    for (int i = 0; i < 5; i++) {
                        Location randomLocation = getRandomLocation(world, location, currentRadius);
                        spawnSnowman(world, randomLocation);
                    }
                }

                // 开始下雪
                if (currentRadius == RADIUS_INCREMENT) {
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
