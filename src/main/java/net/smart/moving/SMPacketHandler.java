package net.smart.moving;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smart.moving.packet.ConfigChangePacket;
import net.smart.moving.packet.ConfigContentPacket;
import net.smart.moving.packet.ConfigInfoPacket;
import net.smart.moving.packet.HungerChangePacket;
import net.smart.moving.packet.SoundPacket;
import net.smart.moving.packet.SpeedChangePacket;
import net.smart.moving.packet.StatePacket;

public class SMPacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SMInfo.ModComId);
	
	public static void registerPackets() {
		INSTANCE.registerMessage(StatePacket.ClientHandler.class, StatePacket.class, StatePacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(StatePacket.ServerHandler.class, StatePacket.class, StatePacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(ConfigInfoPacket.ClientHandler.class, ConfigInfoPacket.class, ConfigInfoPacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(ConfigInfoPacket.ServerHandler.class, ConfigInfoPacket.class, ConfigInfoPacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(ConfigContentPacket.ClientHandler.class, ConfigContentPacket.class, ConfigContentPacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(ConfigContentPacket.ServerHandler.class, ConfigContentPacket.class, ConfigContentPacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(ConfigChangePacket.ClientHandler.class, ConfigChangePacket.class, ConfigChangePacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(ConfigChangePacket.ServerHandler.class, ConfigChangePacket.class, ConfigChangePacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(SpeedChangePacket.ClientHandler.class, SpeedChangePacket.class, SpeedChangePacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(SpeedChangePacket.ServerHandler.class, SpeedChangePacket.class, SpeedChangePacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(HungerChangePacket.ClientHandler.class, HungerChangePacket.class, HungerChangePacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(HungerChangePacket.ServerHandler.class, HungerChangePacket.class, HungerChangePacket.PacketId, Side.SERVER);
		INSTANCE.registerMessage(SoundPacket.ClientHandler.class, SoundPacket.class, SoundPacket.PacketId, Side.CLIENT);
		INSTANCE.registerMessage(SoundPacket.ServerHandler.class, SoundPacket.class, SoundPacket.PacketId, Side.SERVER);
	}
	
	public static final Set<StackTraceElement> errors = new HashSet<StackTraceElement>();

	public static void receivePacket(IMessage message, IPacketReceiver comm, IEntityPlayerMP player) {
		try {
			if (message instanceof StatePacket) {
				int entityId = ((StatePacket) message).entityId;
				long state = ((StatePacket) message).state;
				comm.processStatePacket(message, player, entityId, state);
			} else if (message instanceof ConfigInfoPacket) {
				String info = ((ConfigInfoPacket) message).info;
				comm.processConfigInfoPacket(message, player, info);
			} else if (message instanceof ConfigContentPacket) {
				String username = ((ConfigContentPacket) message).username;
				String[] content = ((ConfigContentPacket) message).content;
				comm.processConfigContentPacket(message, player, content, username);
			} else if (message instanceof ConfigChangePacket) {
				comm.processConfigChangePacket(message, player);
			} else if (message instanceof SpeedChangePacket) {
				int difference = ((SpeedChangePacket) message).difference;
				String username = ((SpeedChangePacket) message).username;
				comm.processSpeedChangePacket(message, player, difference, username);
			} else if (message instanceof HungerChangePacket) {
				float hunger = ((HungerChangePacket) message).hunger;
				comm.processHungerChangePacket(message, player, hunger);
			} else if (message instanceof SoundPacket) {
				String soundId = ((SoundPacket) message).soundId;
				float volume = ((SoundPacket) message).volume;
				float pitch = ((SoundPacket) message).pitch;
				comm.processSoundPacket(message, player, soundId, volume, pitch);
			}
		} catch (Throwable t) {
			if (errors.add(t.getStackTrace()[0]))
				t.printStackTrace();
			else
				System.err.println(t.getClass().getName() + ": " + t.getMessage());
		}
	}
	
	public static void sendState(IPacketSender comm, int entityId, long state) {
		comm.sendPacket(new StatePacket(entityId, state));
	}

	public static void sendConfigInfo(IPacketSender comm, String info) {
		comm.sendPacket(new ConfigInfoPacket(info));
	}

	public static void sendConfigContent(IPacketSender comm, String[] content, String username) {
		comm.sendPacket(new ConfigContentPacket(username, content));
	}

	public static void sendConfigChange(IPacketSender comm) {
		comm.sendPacket(new ConfigChangePacket());
	}

	public static void sendSpeedChange(IPacketSender comm, int difference, String username) {
		comm.sendPacket(new SpeedChangePacket(difference, username));
	}

	public static void sendHungerChange(IPacketSender comm, float hunger) {
		comm.sendPacket(new HungerChangePacket(hunger));
	}

	public static void sendSound(IPacketSender comm, String soundId, float volume, float pitch) {
		comm.sendPacket(new SoundPacket(soundId, volume, pitch));
	}
}
