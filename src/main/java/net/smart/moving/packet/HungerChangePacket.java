package net.smart.moving.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SMComm;
import net.smart.moving.SMPacketHandler;
import net.smart.moving.SMServerComm;
import net.smart.moving.SMServerPlayerBase;

public class HungerChangePacket implements IMessage {
	
	public static final byte PacketId = 5;
		
	public HungerChangePacket() {}
	
	public float hunger;
	
	public HungerChangePacket(float hunger) {
		this.hunger = hunger;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		hunger = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(hunger);
	}
	
	public static class ClientHandler implements IMessageHandler<HungerChangePacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(HungerChangePacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<HungerChangePacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(HungerChangePacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
