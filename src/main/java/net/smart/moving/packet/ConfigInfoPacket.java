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

public class ConfigInfoPacket implements IMessage {
	
	public static final byte PacketId = 1;
		
	public ConfigInfoPacket() {}
	
	public String info;
	
	public ConfigInfoPacket(String info) {
		this.info = info;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int infoLength = buf.readInt();
		info = buf.readCharSequence(infoLength, StandardCharsets.UTF_8).toString();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(info.length());
		buf.writeCharSequence(info, StandardCharsets.UTF_8);
	}
	
	public static class ClientHandler implements IMessageHandler<ConfigInfoPacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(ConfigInfoPacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<ConfigInfoPacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(ConfigInfoPacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
