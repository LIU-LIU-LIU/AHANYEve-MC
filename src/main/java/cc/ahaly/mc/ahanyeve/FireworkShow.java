package cc.ahaly.mc.ahanyeve;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class FireworkShow {

    private final Plugin plugin; // 插件实例
    private final Random random = new Random(); // 用于随机效果

    public FireworkShow(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 在指定位置触发烟花秀
     *
     * @param location 目标位置
     * @param duration 持续时间（秒）
     */
    public void launchFireworkShow(Location location, int duration) {
        new BukkitRunnable() {
            int time = duration;

            @Override
            public void run() {
                if (time <= 0) {
                    this.cancel();
                    return;
                }

                // 生成烟花
                spawnFirework(location);
                // 添加粒子效果
                spawnParticles(location);

                time--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 每秒触发一次
    }

    /**
     * 在指定位置生成一个随机烟花
     *
     * @param location 目标位置
     */
    private void spawnFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        // 随机颜色和效果
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(randomColor())
                .withFade(randomColor())
                .with(randomEffectType())
                .flicker(random.nextBoolean())
                .trail(random.nextBoolean())
                .build();

        meta.addEffect(effect);
        meta.setPower(random.nextInt(2) + 1); // 随机烟花飞行高度
        firework.setFireworkMeta(meta);
    }

    /**
     * 在指定位置生成粒子效果
     *
     * @param location 目标位置
     */
    private void spawnParticles(Location location) {
        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 50, 1, 1, 1, 0.1);
        location.getWorld().spawnParticle(Particle.CLOUD, location, 20, 0.5, 0.5, 0.5, 0.05);
    }

    /**
     * 生成一个随机颜色
     *
     * @return 随机颜色
     */
    private Color randomColor() {
        return Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 生成一个随机烟花效果类型
     *
     * @return 随机烟花效果类型
     */
    private FireworkEffect.Type randomEffectType() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        return types[random.nextInt(types.length)];
    }
}
