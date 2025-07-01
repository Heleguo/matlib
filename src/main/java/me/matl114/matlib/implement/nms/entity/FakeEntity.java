package me.matl114.matlib.implement.nms.entity;

import me.matl114.matlib.implement.nms.network.ClientInformation;
import org.apache.maven.model.Activation;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface FakeEntity {
    @Nullable
    World getWorld();

    double getX();

    double getY();

    double getZ();

    void moveTo(double xt, double yt, double zt);

    double getPitch();

    double getYaw();

    void rotate(double pitch, double yaw);

    void changeWorld(World world);

    default void setData(int id, Object value){
        setData(id, value, false);
    }

    void setData(int id, Object value, boolean force);

    Object getData(int id);

    void broadcastSyncData(boolean force);

    void syncData(Player player, boolean force);

    void syncData(Object nmsPlayer, boolean force);

    FakeEntityManager getFakeEntityManager();
}
