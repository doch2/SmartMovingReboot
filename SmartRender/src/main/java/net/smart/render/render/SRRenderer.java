// ==================================================================
// This file is part of Smart Render.
//
// Smart Render is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Render is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Render. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.render.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.smart.render.SRUtilities;
import net.smart.render.model.IModelPlayer;
import net.smart.render.model.SRModel;
import net.smart.render.statistics.SmartStatistics;
import net.smart.render.statistics.SmartStatisticsFactory;

public class SRRenderer {
	public static SRModel CurrentMainModel;
	private final SRModel modelBipedMain;

	private IRenderPlayer irp;

	private static Map<EntityPlayer, SRRenderData> previousRendererData = new HashMap<EntityPlayer, SRRenderData>();
	private static int previousRendererDataAccessCounter = 0;

	public SRRenderer(IRenderPlayer irp) {
		this.irp = irp;

		modelBipedMain = irp.createModel(irp.getModelBipedMain(), 0.0F, irp.getSmallArms()).getRenderModel();
		SRModel modelArmor = irp.createModel(irp.getModelArmor(), 0.5F, false).getRenderModel();

		irp.initialize((ModelPlayer) modelBipedMain.mp, modelArmor.mp);
	}

	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks) {
		SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityplayer);
		if (statistics != null) {
			boolean isInventory = d == 0.0F && d1 == 0.0F && d2 == 0.0F && f == 0.0F && renderPartialTicks == 1.0F;
			boolean isSleeping = entityplayer.isPlayerSleeping();

			float totalVerticalDistance = statistics.getTotalVerticalDistance(renderPartialTicks);
			float currentVerticalSpeed = statistics.getCurrentVerticalSpeed(renderPartialTicks);
			float totalDistance = statistics.getTotalDistance(renderPartialTicks);
			float currentSpeed = statistics.getCurrentSpeed(renderPartialTicks);

			double distance = 0;
			double verticalDistance = 0;
			double horizontalDistance = 0;
			float currentCameraAngle = 0;
			float currentVerticalAngle = 0;
			float currentHorizontalAngle = 0;

			if (!isInventory) {
				double xDiff = entityplayer.posX - entityplayer.prevPosX;
				double yDiff = entityplayer.posY - entityplayer.prevPosY;
				double zDiff = entityplayer.posZ - entityplayer.prevPosZ;

				verticalDistance = Math.abs(yDiff);
				horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
				distance = Math.sqrt(horizontalDistance * horizontalDistance + verticalDistance * verticalDistance);

				currentCameraAngle = entityplayer.rotationYaw / SRUtilities.RadiantToAngle;
				currentVerticalAngle = (float) Math.atan(yDiff / horizontalDistance);
				if (Float.isNaN(currentVerticalAngle))
					currentVerticalAngle = SRUtilities.Quarter;

				currentHorizontalAngle = (float) -Math.atan(xDiff / zDiff);
				if (Float.isNaN(currentHorizontalAngle))
					if (Float.isNaN(statistics.prevHorizontalAngle))
						currentHorizontalAngle = currentCameraAngle;
					else
						currentHorizontalAngle = statistics.prevHorizontalAngle;
				else if (zDiff < 0)
					currentHorizontalAngle += SRUtilities.Half;

				statistics.prevHorizontalAngle = currentHorizontalAngle;
			}

			IModelPlayer[] modelPlayers = irp.getRenderModels();

			for (int i = 0; i < modelPlayers.length; i++) {
				SRModel modelPlayer = modelPlayers[i].getRenderModel();

				modelPlayer.isInventory = isInventory;

				modelPlayer.totalVerticalDistance = totalVerticalDistance;
				modelPlayer.currentVerticalSpeed = currentVerticalSpeed;
				modelPlayer.totalDistance = totalDistance;
				modelPlayer.currentSpeed = currentSpeed;

				modelPlayer.distance = distance;
				modelPlayer.verticalDistance = verticalDistance;
				modelPlayer.horizontalDistance = horizontalDistance;
				modelPlayer.currentCameraAngle = currentCameraAngle;
				modelPlayer.currentVerticalAngle = currentVerticalAngle;
				modelPlayer.currentHorizontalAngle = currentHorizontalAngle;
				modelPlayer.prevOuterRenderData = getPreviousRendererData(entityplayer);
				modelPlayer.isSleeping = isSleeping;
			}
		}

		CurrentMainModel = modelBipedMain;
		irp.superDoRender(entityplayer, d, d1, d2, f, renderPartialTicks);
		CurrentMainModel = null;
	}

	public void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2) {
		boolean isLocal = entityplayer instanceof EntityPlayerSP;
		boolean isInventory = f2 == 1.0F && isLocal && Minecraft.getMinecraft().currentScreen instanceof GuiInventory;
		float forwardRotation = 0;
		if (!isInventory) {
			forwardRotation = entityplayer.prevRotationYaw
					+ (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f2;

			if (entityplayer.isPlayerSleeping()) {
				actualRotation = 0;
				forwardRotation = 0;
			}

			float workingAngle;
			Minecraft minecraft = Minecraft.getMinecraft();
			if (!isLocal) {
				workingAngle = -entityplayer.rotationYaw;
				workingAngle += minecraft.getRenderViewEntity().rotationYaw;
			} else
				workingAngle = actualRotation
						- getPreviousRendererData(entityplayer).rotateAngleY * SRUtilities.RadiantToAngle;

			if (minecraft.gameSettings.thirdPersonView == 2
					&& !((EntityPlayer) minecraft.getRenderViewEntity()).isPlayerSleeping())
				workingAngle += 180F;

			IModelPlayer[] modelPlayers = irp.getRenderModels();

			for (int i = 0; i < modelPlayers.length; i++) {
				SRModel modelPlayer = modelPlayers[i].getRenderModel();

				modelPlayer.actualRotation = actualRotation;
				modelPlayer.forwardRotation = forwardRotation;
				modelPlayer.workingAngle = workingAngle;
			}

			actualRotation = 0;
		}

		irp.superRotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	public void renderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7) {
		modelBipedMain.bipedEars.beforeRender(entityPlayer);
		modelBipedMain.bipedCloak.beforeRender(entityPlayer, f3);
		irp.superRenderSpecials(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
		modelBipedMain.bipedCloak.afterRender();
		modelBipedMain.bipedEars.afterRender();
	}

	public void beforeHandleRotationFloat(AbstractClientPlayer entityPlayer, float f) {
		SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityPlayer);
		if (statistics != null)
			entityPlayer.ticksExisted += statistics.ticksRiding;
	}

	public void afterHandleRotationFloat(AbstractClientPlayer entityPlayer, float f) {
		SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityPlayer);
		if (statistics != null)
			entityPlayer.ticksExisted -= statistics.ticksRiding;
	}

	public static SRRenderData getPreviousRendererData(EntityPlayer entityplayer) {
		if (++previousRendererDataAccessCounter > 1000) {
			List<?> players = Minecraft.getMinecraft().world.playerEntities;

			Iterator<EntityPlayer> iterator = previousRendererData.keySet().iterator();
			while (iterator.hasNext())
				if (!players.contains(iterator.next()))
					iterator.remove();

			previousRendererDataAccessCounter = 0;
		}

		SRRenderData result = previousRendererData.get(entityplayer);
		if (result == null)
			previousRendererData.put(entityplayer, result = new SRRenderData());
		return result;
	}
}
