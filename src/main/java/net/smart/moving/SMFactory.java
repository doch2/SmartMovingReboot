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

import java.util.Hashtable;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class SMFactory {
	private static SMFactory factory;

	private Hashtable<Integer, SMOther> otherSmartMovings;

	public SMFactory() {
		if (factory != null)
			throw new RuntimeException("FATAL: Can only create one instance of type 'SmartMovingFactory'");
		factory = this;
	}

	protected static boolean isInitialized() {
		return factory != null;
	}

	public static void initialize() {
		if (!isInitialized())
			new SMFactory();
	}

	public static void handleMultiPlayerTick(Minecraft minecraft) {
		factory.doHandleMultiPlayerTick(minecraft);
	}

	public static SMBase getInstance(EntityPlayer entityPlayer) {
		return factory.doGetInstance(entityPlayer);
	}

	public static SMBase getOtherSmartMoving(int entityId) {
		return factory.doGetOtherSmartMoving(entityId);
	}

	public static SMOther getOtherSmartMoving(EntityOtherPlayerMP entity) {
		return factory.doGetOtherSmartMoving(entity);
	}

	protected void doHandleMultiPlayerTick(Minecraft minecraft) {
		Iterator<?> others = minecraft.world.playerEntities.iterator();
		while (others.hasNext()) {
			Entity player = (Entity) others.next();
			if (player instanceof EntityOtherPlayerMP) {
				EntityOtherPlayerMP otherPlayer = (EntityOtherPlayerMP) player;
				SMOther moving = doGetOtherSmartMoving(otherPlayer);
				moving.spawnParticles(minecraft, otherPlayer.posX - otherPlayer.prevPosX,
						otherPlayer.posZ - otherPlayer.prevPosZ);
				moving.foundAlive = true;
			}
		}

		if (otherSmartMovings == null || otherSmartMovings.isEmpty())
			return;

		Iterator<Integer> entityIds = otherSmartMovings.keySet().iterator();
		while (entityIds.hasNext()) {
			Integer entityId = entityIds.next();
			SMOther moving = otherSmartMovings.get(entityId);
			if (moving.foundAlive)
				moving.foundAlive = false;
			else
				entityIds.remove();
		}
	}

	protected SMBase doGetInstance(EntityPlayer entityPlayer) {
		if (entityPlayer instanceof EntityOtherPlayerMP)
			return doGetOtherSmartMoving(entityPlayer.getEntityId());
		else if (entityPlayer instanceof IEntityPlayerSP)
			return ((IEntityPlayerSP) entityPlayer).getMoving();
		if (entityPlayer instanceof EntityPlayerSP)
			return SMPlayerBase.getPlayerBase((EntityPlayerSP) entityPlayer).getMoving();
		return null;
	}

	protected SMBase doGetOtherSmartMoving(int entityId) {
		SMBase moving = tryGetOtherSmartMoving(entityId);
		if (moving == null) {
			Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
			if (entity != null && entity instanceof EntityOtherPlayerMP)
				moving = addOtherSmartMoving((EntityOtherPlayerMP) entity);
		}
		return moving;
	}

	protected SMOther doGetOtherSmartMoving(EntityOtherPlayerMP entity) {
		SMOther moving = tryGetOtherSmartMoving(entity.getEntityId());
		if (moving == null)
			moving = addOtherSmartMoving(entity);
		return moving;
	}

	protected final SMOther tryGetOtherSmartMoving(int entityId) {
		if (otherSmartMovings == null)
			otherSmartMovings = new Hashtable<Integer, SMOther>();
		return otherSmartMovings.get(entityId);
	}

	protected final SMOther addOtherSmartMoving(EntityOtherPlayerMP entity) {
		SMOther moving = new SMOther(entity);
		otherSmartMovings.put(entity.getEntityId(), moving);
		return moving;
	}
}
