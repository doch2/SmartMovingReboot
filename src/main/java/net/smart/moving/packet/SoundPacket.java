package net.smart.moving.packet;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SMComm;
import net.smart.moving.SMPacketHandler;
import net.smart.moving.SMServerComm;
import net.smart.moving.SMServerPlayerBase;

public class SoundPacket implements IMessage {
	
	public static final byte PacketId = 6;
		
	public SoundPacket() {}
	
	public String soundId;
	public float volume;
	public float pitch;
	
	public SoundPacket(String soundId, float volume, float pitch) {
		this.soundId = soundId;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int soundIdLength = buf.readInt();
		soundId = buf.readCharSequence(soundIdLength, StandardCharsets.UTF_8).toString();
		volume = buf.readFloat();
		pitch = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(soundId.length());
		buf.writeCharSequence(soundId, StandardCharsets.UTF_8);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
	}
	
	public static class ClientHandler implements IMessageHandler<SoundPacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(SoundPacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<SoundPacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(SoundPacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
