package eu.byncing.proxy.protocol;

public interface PacketSender {

    void sendPacket(MinecraftPacket packet);
}