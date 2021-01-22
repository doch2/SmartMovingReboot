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

public class SpeedChangePacket implements IMessage {
	
	public static final byte PacketId = 4;
		
	public SpeedChangePacket() {}
	
	public int difference;
	public String username;
	
	public SpeedChangePacket(int difference, String username) {
		this.difference = difference;
		this.username = username;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		difference = buf.readInt();
		int usernameLength = buf.readInt();
		username = buf.readCharSequence(usernameLength, StandardCharsets.UTF_8).toString();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(difference);
		buf.writeInt(username.length());
		buf.writeCharSequence(username, StandardCharsets.UTF_8);
	}
	
	public static class ClientHandler implements IMessageHandler<SpeedChangePacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(SpeedChangePacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<SpeedChangePacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(SpeedChangePacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
