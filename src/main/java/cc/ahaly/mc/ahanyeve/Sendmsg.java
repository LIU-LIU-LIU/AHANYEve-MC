package cc.ahaly.mc.ahanyeve;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sendmsg {

    public Sendmsg(Player player, String message) {
        player.sendTitle(message, "§7§l祝你新年快乐🎉ヾ(≧▽≦*)o", 3, 8, 3);
        playRandomSound(player);
    }

    //随机播放声音
    public static void playRandomSound(Player player) {
        Random random = new Random();
        Sound[] sounds = Sound.values(); // 获取所有声音
        Sound randomSound = sounds[random.nextInt(sounds.length)]; // 随机选择一个声音

        float volume = 0.5f; // 设置音量
        float pitch = 1.0f; // 设置音调

        player.playSound(player.getLocation(), randomSound, volume, pitch); // 在玩家位置播放声音
    }

    public static void launchFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();

        // 获取烟花效果列表并随机选择一个
        List<FireworkEffect> effects = createFireworkEffects();
        Random random = new Random();
        FireworkEffect effect = effects.get(random.nextInt(effects.size()));

        fwm.addEffect(effect);
        fwm.setPower(1); // 可以调整烟花的飞行高度

        fw.setFireworkMeta(fwm);
    }

    public static List<FireworkEffect> createFireworkEffects() {
        List<FireworkEffect> effects = new ArrayList<>();

        // 红色、蓝色爆炸效果
        effects.add(FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLUE).with(FireworkEffect.Type.BURST).build());

        // 绿色星形效果
        effects.add(FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.STAR).build());

        // 多彩球形效果
        effects.add(FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.ORANGE).withColor(Color.PURPLE).with(FireworkEffect.Type.BALL).build());

        // 白色闪烁效果
        effects.add(FireworkEffect.builder().withColor(Color.WHITE).withFlicker().with(FireworkEffect.Type.BALL).build());

        // 多彩星形效果
        effects.add(FireworkEffect.builder().withColor(Color.RED).withColor(Color.GREEN).withColor(Color.BLUE).withTrail().with(FireworkEffect.Type.STAR).build());

        return effects;
    }
}
