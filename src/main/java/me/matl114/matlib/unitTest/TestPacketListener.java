package me.matl114.matlib.unitTest;

import com.mojang.authlib.GameProfile;
import me.matl114.matlib.implement.nms.network.ClientInformation;
import me.matl114.matlib.implement.nms.network.PacketEvent;
import me.matl114.matlib.implement.nms.network.PacketHandler;
import me.matl114.matlib.implement.nms.network.PacketListener;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.utils.Debug;

public class TestPacketListener implements PacketListener {
    ClientInformation information;
    @PacketHandler(type = GamePacket.ALL_PLAY)
    public void onPacket0(PacketEvent event){
        if(event.getClient() == information){
            Debug.logger("packet",event.getPacket().getClass().getSimpleName());
        }
    }
    @PacketHandler(type = GamePacket.CLIENT_REGISTER)
    public void onRegister(PacketEvent event){
        GameProfile player= event.getClient().getGameProfile();
        if(player.getName().equals("Mashirorima_ovo")){
            Debug.logger("Find player");
            this.information = event.getClient();
        }
    }
    @PacketHandler(type = GamePacket.CLIENT_UNREGISTER)
    public void onUnregister(PacketEvent event){
        if(information == event.getClient()){
            information = null;
        }
    }
}
