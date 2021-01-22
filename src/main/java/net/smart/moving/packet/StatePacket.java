package net.smart.moving.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SMComm;
import net.smart.moving.SMPacketHandler;
import net.smart.moving.SMServerComm;
import net.smart.moving.SMServerPlayerBase;

public class StatePacket implements IMessage {
	
	public static final byte PacketId = 0;
		
	public StatePacket() {}
	
	public int entityId;
	public long state;
	
	public StatePacket(int entityId, long state) {
		this.entityId = entityId;
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		state = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		 buf.writeInt(entityId);
		 buf.writeLong(state);
	}
	
	public static class ClientHandler implements IMessageHandler<StatePacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(StatePacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<StatePacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(StatePacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
