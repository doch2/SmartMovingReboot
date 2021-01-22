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

import java.io.File;
import java.util.List;

import api.player.server.IServerPlayerAPI;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.smart.moving.config.SMConfig;
import net.smart.moving.config.SMServerOptions;
import net.smart.properties.Property;

public class SMServer {
	public static SMConfig Options = null;
	private static SMServerOptions optionsHandler = null;

	public static final float SmallSizeItemGrabHeight = 0.25F;

	protected final IEntityPlayerMP mp;
	private boolean resetFallDistance = false;
	private boolean resetTicksForFloatKick = false;
	private boolean initialized = false;

	public boolean crawlingInitialized;
	public int crawlingCooldown;
	public boolean isCrawling;
	public boolean isSmall;

	private boolean isSneakButtonPressed;

	public SMServer(IEntityPlayerMP mp, boolean onTheFly) {
		this.mp = mp;
		if (onTheFly)
			initialize(true);
	}

	public void initialize(boolean alwaysSendMessage) {
		if (Options._globalConfig.value)
			SMPacketHandler.sendConfigContent(mp, optionsHandler.writeToProperties(), null);
		else if (Options._serverConfig.value)
			SMPacketHandler.sendConfigContent(mp, optionsHandler.writeToProperties(mp, false), null);
		else if (alwaysSendMessage)
			SMPacketHandler.sendConfigContent(mp, Options.enabled ? new String[0] : null, null);
		initialized = true;
	}

	public void processStatePacket(IMessage message, long state) {
		if (!initialized)
			initialize(false);

		boolean isCrawling = ((state >>> 13) & 1) != 0;
		setCrawling(isCrawling);

		boolean isSmall = ((state >>> 15) & 1) != 0;
		setSmall(isSmall);

		boolean isClimbing = ((state >>> 14) & 1) != 0;
		boolean isCrawlClimbing = ((state >>> 12) & 1) != 0;
		boolean isCeilingClimbing = ((state >>> 18) & 1) != 0;

		boolean isWallJumping = ((state >>> 31) & 1) != 0;

		isSneakButtonPressed = ((state >>> 33) & 1) != 0;

		resetFallDistance = isClimbing || isCrawlClimbing || isCeilingClimbing || isWallJumping;
		resetTicksForFloatKick = isClimbing || isCrawlClimbing || isCeilingClimbing;
		SMPacketHandler.INSTANCE.sendToAllTracking(message, mp.getEntityPlayerMP());
	}

	public void processConfigPacket(String clientConfigurationVersion) {
		boolean warn = true;
		String type = "unknown";
		if (clientConfigurationVersion != null)
			for (int i = 0; i < SMConfig._all.length; i++)
				if (clientConfigurationVersion.equals(SMConfig._all[i])) {
					warn = i > 0;
					type = warn ? "outdated" : "matching";
					break;
				}

		String message = "Smart Moving player \"" + mp.getUsername() + "\" connected with " + type
				+ " configuration system";
		if (clientConfigurationVersion != null)
			message += " version \"" + clientConfigurationVersion + "\"";

		if (warn)
			FMLLog.warning(message);
		else
			FMLLog.info(message);
	}

	public void processConfigChangePacket(String localUserName) {
		if (!Options._globalConfig.value) {
			toggleSingleConfig();
			return;
		}

		String username = mp.getUsername();

		if (localUserName == username) {
			toggleConfig();
			return;
		}

		String[] rightPlayerNames = Options._usersWithChangeConfigRights.value;
		for (int i = 0; i < rightPlayerNames.length; i++)
			if (rightPlayerNames[i].equals(username)) {
				toggleConfig();
				return;
			}

		SMPacketHandler.sendConfigChange(mp);
	}

	public void processSpeedChangePacket(int difference, String localUserName) {
		if (!Options._globalConfig.value) {
			changeSingleSpeed(difference);
			return;
		}

		if (!hasRight(localUserName, Options._usersWithChangeSpeedRights))
			SMPacketHandler.sendSpeedChange(mp, 0, null);
		else
			changeSpeed(difference);
	}

	public void processHungerChangePacket(float hunger) {
		mp.localAddExhaustion(hunger);
	}

	public void processSoundPacket(String soundId, float volume, float pitch) {
		mp.localPlaySound(soundId, volume, pitch);
	}

	private boolean hasRight(String localUserName, Property<String[]> rights) {
		String username = mp.getUsername();

		if (localUserName == username)
			return true;

		String[] rightPlayerNames = rights.value;
		for (int i = 0; i < rightPlayerNames.length; i++)
			if (rightPlayerNames[i].equals(username))
				return true;

		return false;
	}

	public void toggleSingleConfig() {
		SMPacketHandler.sendConfigContent(mp, optionsHandler.writeToProperties(mp, true), mp.getUsername());
	}

	public IEntityPlayerMP[] getAllPlayers() {
		List<?> playerEntityList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
				.getPlayers();
		IEntityPlayerMP[] result = new IEntityPlayerMP[playerEntityList.size()];
		for (int i = 0; i < playerEntityList.size(); i++)
			result[i] = (IEntityPlayerMP) ((IServerPlayerAPI) playerEntityList.get(i))
					.getServerPlayerBase(SMInfo.ModName);
		return result;
	}

	public void toggleConfig() {
		optionsHandler.toggle(mp);
		String[] config = optionsHandler.writeToProperties();

		IEntityPlayerMP[] players = getAllPlayers();
		for (int n = 0; n < players.length; n++)
			SMPacketHandler.sendConfigContent(players[n], config, mp.getUsername());
	}

	public void changeSingleSpeed(int difference) {
		optionsHandler.changeSingleSpeed(mp, difference);
		SMPacketHandler.sendSpeedChange(mp, difference, mp.getUsername());
	}

	public void changeSpeed(int difference) {
		optionsHandler.changeSpeed(difference, mp);
		IEntityPlayerMP[] players = getAllPlayers();
		for (int n = 0; n < players.length; n++)
			SMPacketHandler.sendSpeedChange(players[n], difference, mp.getUsername());
	}

	public void afterOnUpdate() {
		if (resetFallDistance)
			mp.resetFallDistance();
		if (resetTicksForFloatKick)
			mp.resetTicksForFloatKick();
	}

	public static void initialize(File optionsPath, int gameType, SMConfig config) {
		Options = config;
		optionsHandler = new SMServerOptions(Options, optionsPath, gameType);
	}

	public void setCrawling(boolean crawling) {
		if (!crawling && isCrawling)
			crawlingCooldown = 10;
		isCrawling = crawling;
	}

	public void setSmall(boolean isSmall) {
		mp.setHeight(isSmall ? 0.8F : 1.8F);
		this.isSmall = isSmall;
	}

	public void afterSetPosition(double d, double d1, double d2) {
		if (!crawlingInitialized)
			mp.setMaxY(mp.getMinY() + mp.getHeight() - 1);
	}

	public void beforeIsPlayerSleeping() {
		if (!crawlingInitialized) {
			mp.setMaxY(mp.getMinY() + mp.getHeight());
			crawlingInitialized = true;
		}
	}

	public void beforeOnUpdate() {
		if (crawlingCooldown > 0)
			crawlingCooldown--;
	}

	public void afterOnLivingUpdate() {
		if (!isSmall)
			return;

		if (mp.doGetHealth() <= 0)
			return;

		double offset = SmallSizeItemGrabHeight;
		AxisAlignedBB box = mp.expandBox(mp.getBox(), 1, offset, 1);

		List<?> offsetEntities = mp.getEntitiesExcludingPlayer(box);
		if (offsetEntities != null && offsetEntities.size() > 0) {
			Object[] offsetEntityArray = offsetEntities.toArray();

			box = mp.expandBox(box, 0, -offset, 0);
			List<?> standardEntities = mp.getEntitiesExcludingPlayer(box);

			for (int i = 0; i < offsetEntityArray.length; i++) {
				Entity offsetEntity = (Entity) offsetEntityArray[i];
				if (standardEntities != null && standardEntities.contains(offsetEntity))
					continue;

				if (!mp.isDeadEntity(offsetEntity))
					mp.onCollideWithPlayer(offsetEntity);
			}
		}
	}

	public boolean isEntityInsideOpaqueBlock() {
		if (crawlingCooldown > 0)
			return false;

		return mp.localIsEntityInsideOpaqueBlock();
	}

	public void addMovementStat(double var1, double var3, double var5) {
		mp.localAddMovementStat(var1, var3, var5);
	}

	public void addExhaustion(float exhaustion) {
		mp.localAddExhaustion(exhaustion);
	}

	public boolean isSneaking() {
		return mp.getItemInUseCount() > 0 || mp.localIsSneaking();
	}
}
