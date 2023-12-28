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

public class SendEffect {

    public SendEffect(Player player, String message) {
        player.sendTitle(message, "§c§l祝你新年快乐🎉" + getRandomKaomoji(), 5, 20, 5);
        playRandomSound(player);
    }

    //随机产生颜文字字符串
    public static String getRandomKaomoji() {
        String[] kaomojis = {
                "(¬‿¬)", "(•̀ᴗ•́)و ̑̑", "φ(*￣0￣)", "(づ｡◕‿‿◕｡)づ", "q(≧▽≦q)",
                "ヾ(≧▽≦*)o", "(✿╹◡╹)", "φ(゜▽゜*)♪", "(≧∇≦)ﾉ",
                "(o゜▽゜)o☆", "(≧ω≦)", "ʕっ•ᴥ•ʔっ", "(⌐■_■)", "(ಥ﹏ಥ)"
        };
        Random random = new Random();

        int index = random.nextInt(kaomojis.length);
        return kaomojis[index];
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

    //随机产生烟花
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

        // 多彩爆炸带尾迹
        effects.add(FireworkEffect.builder().withColor(Color.RED, Color.BLUE, Color.GREEN).withFade(Color.ORANGE, Color.PURPLE).withTrail().with(FireworkEffect.Type.BURST).build());

// 金色星形带闪烁
        effects.add(FireworkEffect.builder().withColor(Color.YELLOW).withFlicker().with(FireworkEffect.Type.STAR).build());

// 彩虹球形效果
        effects.add(FireworkEffect.builder().withColor(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE).with(FireworkEffect.Type.BALL_LARGE).build());

// 蓝绿色球形带尾迹和闪烁
        effects.add(FireworkEffect.builder().withColor(Color.BLUE, Color.AQUA).withFlicker().withTrail().with(FireworkEffect.Type.BALL).build());

// 红色爱心效果
        effects.add(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BURST).build());

// 夜空效果（深蓝带白色闪烁）
        effects.add(FireworkEffect.builder().withColor(Color.NAVY).withFlicker().with(FireworkEffect.Type.BALL_LARGE).build());

// 神秘紫色带闪烁
        effects.add(FireworkEffect.builder().withColor(Color.PURPLE, Color.FUCHSIA).withFlicker().with(FireworkEffect.Type.BALL).build());


        return effects;
    }
}
