package net.smart.moving.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smart.moving.SMComm;
import net.smart.moving.SMPacketHandler;
import net.smart.moving.SMServerComm;
import net.smart.moving.SMServerPlayerBase;

public class ConfigContentPacket implements IMessage {
	
	public static final byte PacketId = 2;

	public String username;
	public String[] content;
		
	public ConfigContentPacket() {}
	
	public ConfigContentPacket(String username, String[] content) {
		this.username = username;
		this.content = content;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try {
			byte[] inputBytes = new byte[buf.readableBytes()];
			buf.readBytes(inputBytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			username = (String) ois.readObject();
			content = (String[]) ois.readObject();
			ois.close();
			bais.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(username);
			oos.writeObject(content);
			buf.writeBytes(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class ClientHandler implements IMessageHandler<ConfigContentPacket, IMessage> {
		
		public ClientHandler() {}

		@Override
		public IMessage onMessage(ConfigContentPacket message, MessageContext ctx) {
			SMPacketHandler.receivePacket(message, SMComm.instance, null);
			return null;
		}
	}
	
	public static class ServerHandler implements IMessageHandler<ConfigContentPacket, IMessage> {
		
		public ServerHandler() {}

		@Override
		public IMessage onMessage(ConfigContentPacket message, MessageContext ctx) {
			SMServerPlayerBase serverPlayer = SMServerPlayerBase.getPlayerBase(ctx.getServerHandler().player);
			SMPacketHandler.receivePacket(message, SMServerComm.instance, serverPlayer);
			return null;
		}
	}
}
