package cc.ahaly.mc.ahanyeve;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class MyEvent implements Listener {

    //物品耐久度
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        // 取消事件，防止耐久度减少
        event.setCancelled(true);
        new Sendmsg(event.getPlayer(),"§c§l本次耐久消耗已经为你抵消");
    }

    //扔雪球
    @EventHandler
    public void onSnowballThrow(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Snowball) {
            Snowball snowball = (Snowball) projectile;
            if (snowball.getShooter() instanceof Player) {
                // 放烟花的代码
                Sendmsg.launchFirework(snowball.getLocation());
            }
        }
    }
}