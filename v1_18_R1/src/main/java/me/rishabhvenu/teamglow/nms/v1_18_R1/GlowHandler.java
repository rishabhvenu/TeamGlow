package me.rishabhvenu.teamglow.nms.v1_18_R1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.rishabhvenu.teamglow.IGlowHandler;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class GlowHandler implements IGlowHandler {
    @Override
    public void register(Player player) {
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext context, Object obj, ChannelPromise promise) {
                try {
                    if (obj instanceof PacketPlayOutEntityMetadata entityDataPacket) {
                        Team team = player.getScoreboard().getPlayerTeam(player);
                        if (team != null) {
                            boolean isPlayer = false;
                            Player teammate = null;
                            for (Player entity : Bukkit.getOnlinePlayers()) {
                                if (entity.getEntityId() == entityDataPacket.c()) {
                                    isPlayer = true;
                                    teammate = entity;
                                    break;
                                }
                            }
                            if (isPlayer && team.equals(player.getScoreboard().getPlayerTeam(teammate))) {
                                DataWatcher data = ((CraftPlayer)teammate).getHandle().ai();
                                DataWatcherObject<Byte> accessor = new DataWatcherObject<>(0, DataWatcherRegistry.a);
                                data.b(accessor, (byte) (data.a(accessor) | 0x40));
                            }
                        }
                    }
                    super.write(context, obj, promise);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        (((CraftPlayer)player).getHandle()).b.a.k.pipeline()
                .addBefore("packet_handler", player.getName(), handler);
    }
}
