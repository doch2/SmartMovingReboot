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

import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import api.player.client.IClientPlayerAPI;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class SMPlayerBase extends ClientPlayerBase implements IEntityPlayerSP {
	public SMSelf moving;

	public static void registerPlayerBase() {
		ClientPlayerAPI.register(SMInfo.ModName, SMPlayerBase.class);
	}

	public static SMPlayerBase getPlayerBase(EntityPlayerSP player) {
		return (SMPlayerBase) ((IClientPlayerAPI) player).getClientPlayerBase(SMInfo.ModName);
	}

	public SMPlayerBase(ClientPlayerAPI playerApi) {
		super(playerApi);
		moving = new SMSelf(player, this);
	}

	@Override
	public void beforeMoveEntity(MoverType mover, double d, double d1, double d2) {
		moving.beforeMoveEntity(d, d1, d2);
	}

	@Override
	public void afterMoveEntity(MoverType mover, double d, double d1, double d2) {
		moving.afterMoveEntity(d, d1, d2);
	}

	@Override
	public void localMoveEntity(MoverType mover, double d, double d1, double d2) {
		super.moveEntity(mover, d, d1, d2);
	}

	@Override
	public void beforeTrySleep(BlockPos pos) {
		moving.beforeSleepInBedAt(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public EntityPlayer.SleepResult localSleepInBedAt(int i, int j, int k) {
		return super.trySleep(new BlockPos(i, j, k));
	}

	@Override
	public float getBrightness() {
		return moving.getBrightness();
	}

	@Override
	public float localGetBrightness() {
		return super.getBrightness();
	}

	@Override
	public int getBrightnessForRender() {
		return moving.getBrightnessForRender();
	}

	@Override
	public int localGetBrightnessForRender() {
		return super.getBrightnessForRender();
	}

	@Override
	public boolean pushOutOfBlocks(double d, double d1, double d2) {
		return moving.pushOutOfBlocks(d, d1, d2);
	}

	@Override
	public void beforeOnUpdate() {
		moving.beforeOnUpdate();
	}

	@Override
	public void afterOnUpdate() {
		moving.afterOnUpdate();
	}

	@Override
	public void beforeOnLivingUpdate() {
		moving.beforeOnLivingUpdate();
	}

	@Override
	public void afterOnLivingUpdate() {
		moving.afterOnLivingUpdate();
	}

	@Override
	public boolean getSleepingField() {
		return playerAPI.getSleepingField();
	}

	@Override
	public boolean getIsInWebField() {
		return playerAPI.getIsInWebField();
	}

	@Override
	public void setIsInWebField(boolean newIsInWeb) {
		playerAPI.setIsInWebField(newIsInWeb);
	}

	@Override
	public boolean getIsJumpingField() {
		return playerAPI.getIsJumpingField();
	}

	@Override
	public Minecraft getMcField() {
		return playerAPI.getMcField();
	}

	@Override
	public void moveEntityWithHeading(float f, float f1, float f2) {
		moving.moveEntityWithHeading(f, f2);
	}

	@Override
	public boolean canTriggerWalking() {
		return moving.canTriggerWalking();
	}

	@Override
	public boolean isOnLadder() {
		return moving.isOnLadderOrVine();
	}

	@Override
	public SMSelf getMoving() {
		return moving;
	}

	@Override
	public void updateEntityActionState() {
		moving.updateEntityActionState(false);
	}

	@Override
	public void localUpdateEntityActionState() {
		super.updateEntityActionState();
	}

	@Override
	public void setIsJumpingField(boolean flag) {
		playerAPI.setIsJumpingField(flag);
	}

	@Override
	public void setMoveForwardField(float f) {
		player.moveForward = f;
	}

	@Override
	public void setMoveStrafingField(float f) {
		player.moveStrafing = f;
	}

	@Override
	public boolean isInsideOfMaterial(Material material) {
		return moving.isInsideOfMaterial(material);
	}

	@Override
	public boolean localIsInsideOfMaterial(Material material) {
		return super.isInsideOfMaterial(material);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nBTTagCompound) {
		moving.writeEntityToNBT(nBTTagCompound);
	}

	@Override
	public void localWriteEntityToNBT(NBTTagCompound nBTTagCompound) {
		super.writeEntityToNBT(nBTTagCompound);
	}

	@Override
	public boolean isSneaking() {
		return moving.isSneaking();
	}

	@Override
	public boolean localIsSneaking() {
		return super.isSneaking();
	}

	@Override
	public float getFovModifier() {
		return moving.getFOVMultiplier();
	}

	@Override
	public float localGetFOVMultiplier() {
		return playerAPI.localGetFovModifier();
	}

	@Override
	public void beforeSetPositionAndRotation(double d, double d1, double d2, float f, float f1) {
		moving.beforeSetPositionAndRotation();
	}

	@Override
	public void beforeGetSleepTimer() {
		moving.beforeGetSleepTimer();
	}

	@Override
	public void jump() {
		moving.jump();
	}
}
