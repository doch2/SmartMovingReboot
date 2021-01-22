// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import static net.smart.moving.SMContext.Config;
import static net.smart.moving.SMContext.Options;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.smart.moving.config.SMConfig;
import net.smart.moving.config.SMOptions;
import net.smart.moving.config.SMServerConfig;
import net.smart.properties.Property;

public class SMComm implements IPacketReceiver, IPacketSender {
	public static final SMServerConfig ServerConfig = new SMServerConfig();

	@Override
	public boolean processStatePacket(IMessage message, IEntityPlayerMP player, int entityId, long state) {
		Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);

		if (entity != null && entity instanceof EntityOtherPlayerMP) {
			SMOther moving = SMFactory.getOtherSmartMoving((EntityOtherPlayerMP) entity);
			if (moving != null)
				moving.processStatePacket(state);
		}
		
		return true;
	}

	@Override
	public boolean processConfigInfoPacket(IMessage message, IEntityPlayerMP player, String info) {
		return false;
	}

	@Override
	public boolean processConfigContentPacket(IMessage message, IEntityPlayerMP player, String[] content,
			String username) {
		processConfigPacket(content, username, false);
		return true;
	}

	@Override
	public boolean processConfigChangePacket(IMessage message, IEntityPlayerMP player) {
		SMOptions.writeNoRightsToChangeConfigMessageToChat(isConnectedToRemoteServer());
		return true;
	}

	@Override
	public boolean processSpeedChangePacket(IMessage message, IEntityPlayerMP player, int difference,
			String username) {
		if (difference == 0)
			SMOptions.writeNoRightsToChangeSpeedMessageToChat(isConnectedToRemoteServer());
		else {
			Config.changeSpeed(difference);
			Options.writeServerSpeedMessageToChat(username, Config._globalConfig.value);
		}
		return true;
	}

	@Override
	public boolean processHungerChangePacket(IMessage message, IEntityPlayerMP player, float hunger) {
		player.localAddExhaustion(hunger);
		return true;
	}

	@Override
	public boolean processSoundPacket(IMessage message, IEntityPlayerMP player, String soundId, float distance,
			float pitch) {
		return false;
	}

	private static boolean isConnectedToRemoteServer() {
		IntegratedServer integratedServer = Minecraft.getMinecraft().getIntegratedServer();
		return FMLCommonHandler.instance().getMinecraftServerInstance() == null
				|| (integratedServer == null || !integratedServer.isSinglePlayer());
	}

	public static void processConfigPacket(String[] content, String username, boolean blockCode) {
		boolean isGloballyConfigured = false;
		if (content != null && content.length == 2 && Options._globalConfig.getCurrentKey().equals(content[0])) {
			isGloballyConfigured = "true".equals(content[1]);
			content = null;
		}

		boolean wasEnabled = Config.enabled;
		boolean first = Config != ServerConfig;
		if (first)
			ServerConfig.reset();

		if (content != null)
			if (content.length != 0) {
				ServerConfig.loadFromProperties(content, blockCode);
				isGloballyConfigured = ServerConfig._globalConfig.value;
			} else {
				Config = Options;
				Options.writeServerDeconfigMessageToChat();
				return;
			}
		else {
			ServerConfig.load(false);
			ServerConfig.setCurrentKey(null);
		}

		ServerConfig._globalConfig.value = isGloballyConfigured;

		if (!first) {
			Options.writeServerReconfigMessageToChat(wasEnabled, username, isGloballyConfigured);
			return;
		}

		Config = ServerConfig;
		Options.writeServerConfigMessageToChat();
		if (!blockCode)
			SMPacketHandler.sendConfigInfo(SMComm.instance, SMConfig._sm_current);
	}

	@Override
	public void sendPacket(IMessage message) {
		SMPacketHandler.INSTANCE.sendToServer(message);
	}

	public static boolean processBlockCode(String text) {
		if (!text.startsWith("§0§1") || !text.endsWith("§f§f"))
			return false;

		String codes = text.substring(4, text.length() - 4);
		processBlockCode(codes, "§0", Options._baseClimb, "standard");
		processBlockCode(codes, "§1", Options._freeClimb);
		processBlockCode(codes, "§2", Options._ceilingClimbing);
		processBlockCode(codes, "§3", Options._swim);
		processBlockCode(codes, "§4", Options._dive);
		processBlockCode(codes, "§5", Options._crawl);
		processBlockCode(codes, "§6", Options._slide);
		processBlockCode(codes, "§7", Options._fly);
		processBlockCode(codes, "§8", Options._jumpCharge);
		processBlockCode(codes, "§9", Options._headJump);
		processBlockCode(codes, "§a", Options._angleJumpSide);
		processBlockCode(codes, "§b", Options._angleJumpBack);
		return true;
	}

	private static void processBlockCode(String text, String blockCode, Property<?> property, String... value) {
		if (text.contains(blockCode))
			processConfigPacket(new String[] { property.getCurrentKey(), value.length > 0 ? value[0] : "false" }, null,
					true);
	}

	public static final SMComm instance = new SMComm();
}
