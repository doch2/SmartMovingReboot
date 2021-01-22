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

package net.smart.render.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.smart.render.SRInstall;
import net.smart.render.SRUtilities;
import net.smart.render.render.SRRenderData;
import net.smart.render.render.SRRenderer;
import net.smart.utilities.Reflect;

public class SRModel {
	private IModelPlayer imp;
	public ModelBiped mp;

	private boolean isModelPlayer;
	private boolean smallArms;

	public boolean isInventory;

	public float totalVerticalDistance;
	public float currentVerticalSpeed;
	public float totalDistance;
	public float currentSpeed;

	public double distance;
	public double verticalDistance;
	public double horizontalDistance;
	public float currentCameraAngle;
	public float currentVerticalAngle;
	public float currentHorizontalAngle;

	public float actualRotation;
	public float forwardRotation;
	public float workingAngle;

	public SRModelRotationRenderer bipedOuter;
	public SRModelRotationRenderer bipedTorso;
	public SRModelRotationRenderer bipedBody;
	public SRModelRotationRenderer bipedBreast;
	public SRModelRotationRenderer bipedNeck;
	public SRModelRotationRenderer bipedHead;
	public SRModelRotationRenderer bipedRightShoulder;
	public SRModelRotationRenderer bipedRightArm;
	public SRModelRotationRenderer bipedLeftShoulder;
	public SRModelRotationRenderer bipedLeftArm;
	public SRModelRotationRenderer bipedPelvic;
	public SRModelRotationRenderer bipedRightLeg;
	public SRModelRotationRenderer bipedLeftLeg;

	public SRModelRotationRenderer bipedBodywear;
	public SRModelRotationRenderer bipedHeadwear;
	public SRModelRotationRenderer bipedRightArmwear;
	public SRModelRotationRenderer bipedLeftArmwear;
	public SRModelRotationRenderer bipedRightLegwear;
	public SRModelRotationRenderer bipedLeftLegwear;

	public SRModelEarsRenderer bipedEars;
	public SRModelCapeRenderer bipedCloak;

	public SRRenderData prevOuterRenderData;
	public boolean isSleeping;

	private static final Field _textureOffsetX = Reflect.GetField(ModelRenderer.class,
			SRInstall.ModelRenderer_textureOffsetX);
	private static final Field _textureOffsetY = Reflect.GetField(ModelRenderer.class,
			SRInstall.ModelRenderer_textureOffsetY);

	public SRModel(boolean b, ModelBiped mb, IModelPlayer imp, ModelRenderer originalBipedBody,
			ModelRenderer originalBipedBodywear, ModelRenderer originalBipedHead, ModelRenderer originalBipedHeadwear,
			ModelRenderer originalBipedRightArm, ModelRenderer originalBipedRightArmwear,
			ModelRenderer originalBipedLeftArm, ModelRenderer originalBipedLeftArmwear,
			ModelRenderer originalBipedRightLeg, ModelRenderer originalBipedRightLegwear,
			ModelRenderer originalBipedLeftLeg, ModelRenderer originalBipedLeftLegwear, ModelRenderer originalBipedCape,
			ModelRenderer originalBipedDeadmau5Head) {
		this.imp = imp;
		this.mp = mb;

		isModelPlayer = mp instanceof ModelPlayer;
		smallArms = b;

		mb.boxList.clear();

		bipedOuter = create(null);
		bipedOuter.fadeEnabled = true;

		bipedTorso = create(bipedOuter);
		bipedBody = create(bipedTorso, originalBipedBody);
		bipedBreast = create(bipedTorso);
		bipedNeck = create(bipedBreast);
		bipedHead = create(bipedNeck, originalBipedHead);
		bipedRightShoulder = create(bipedBreast);
		bipedRightArm = create(bipedRightShoulder, originalBipedRightArm);
		bipedLeftShoulder = create(bipedBreast);
		bipedLeftShoulder.mirror = true;
		bipedLeftArm = create(bipedLeftShoulder, originalBipedLeftArm);
		bipedPelvic = create(bipedTorso);
		bipedRightLeg = create(bipedPelvic, originalBipedRightLeg);
		bipedLeftLeg = create(bipedPelvic, originalBipedLeftLeg);

		bipedBodywear = create(bipedBody, originalBipedBodywear);
		bipedHeadwear = create(bipedHead, originalBipedHeadwear);
		bipedRightArmwear = create(bipedRightArm, originalBipedRightArmwear);
		bipedLeftArmwear = create(bipedLeftArm, originalBipedLeftArmwear);
		bipedRightLegwear = create(bipedRightLeg, originalBipedRightLegwear);
		bipedLeftLegwear = create(bipedLeftLeg, originalBipedLeftLegwear);

		if (originalBipedCape != null) {
			bipedCloak = new SRModelCapeRenderer(mb, 0, 0, bipedBreast, bipedOuter);
			copy(bipedCloak, originalBipedCape);
		}

		if (originalBipedDeadmau5Head != null) {
			bipedEars = new SRModelEarsRenderer(mb, 24, 0, bipedHead);
			copy(bipedEars, originalBipedDeadmau5Head);
		}

		reset(); // set default rotation points

		imp.initialize(bipedBody, bipedBodywear, bipedHead, bipedHeadwear, bipedRightArm, bipedRightArmwear,
				bipedLeftArm, bipedLeftArmwear, bipedRightLeg, bipedRightLegwear, bipedLeftLeg, bipedLeftLegwear,
				bipedCloak, bipedEars);

		if (SRRenderer.CurrentMainModel != null) {
			isInventory = SRRenderer.CurrentMainModel.isInventory;

			totalVerticalDistance = SRRenderer.CurrentMainModel.totalVerticalDistance;
			currentVerticalSpeed = SRRenderer.CurrentMainModel.currentVerticalSpeed;
			totalDistance = SRRenderer.CurrentMainModel.totalDistance;
			currentSpeed = SRRenderer.CurrentMainModel.currentSpeed;

			distance = SRRenderer.CurrentMainModel.distance;
			verticalDistance = SRRenderer.CurrentMainModel.verticalDistance;
			horizontalDistance = SRRenderer.CurrentMainModel.horizontalDistance;
			currentCameraAngle = SRRenderer.CurrentMainModel.currentCameraAngle;
			currentVerticalAngle = SRRenderer.CurrentMainModel.currentVerticalAngle;
			currentHorizontalAngle = SRRenderer.CurrentMainModel.currentHorizontalAngle;
			prevOuterRenderData = SRRenderer.CurrentMainModel.prevOuterRenderData;
			isSleeping = SRRenderer.CurrentMainModel.isSleeping;

			actualRotation = SRRenderer.CurrentMainModel.actualRotation;
			forwardRotation = SRRenderer.CurrentMainModel.forwardRotation;
			workingAngle = SRRenderer.CurrentMainModel.workingAngle;
		}
	}

	private SRModelRotationRenderer create(SRModelRotationRenderer base) {
		return new SRModelRotationRenderer(mp, -1, -1, base);
	}

	private SRModelRotationRenderer create(SRModelRotationRenderer base, ModelRenderer original) {
		if (original == null)
			return null;

		int textureOffsetX = (int) Reflect.GetField(_textureOffsetX, original);
		int textureOffsetY = (int) Reflect.GetField(_textureOffsetY, original);
		SRModelRotationRenderer local = new SRModelRotationRenderer(mp, textureOffsetX, textureOffsetY, base);
		copy(local, original);
		return local;
	}

	private static void copy(SRModelRotationRenderer local, ModelRenderer original) {
		if (original.childModels != null)
			for (Object childModel : original.childModels)
				local.addChild((ModelRenderer) childModel);
		if (original.cubeList != null)
			for (Object cube : original.cubeList)
				local.cubeList.add((ModelBox) cube);
		local.mirror = original.mirror;
		local.isHidden = original.isHidden;
		local.showModel = original.showModel;
	}

	public void render(Entity entity, float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		GL11.glPushMatrix();
		if (entity.isSneaking())
			GL11.glTranslatef(0.0F, 0.2F, 0.0F);

		bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = true;
		if (isModelPlayer)
			bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = true;
		imp.superRender(entity, totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);
		if (isModelPlayer)
			bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = false;
		bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = false;

		bipedOuter.render(factor);
		bipedOuter.renderIgnoreBase(factor);
		bipedTorso.renderIgnoreBase(factor);
		bipedBody.renderIgnoreBase(factor);
		bipedBreast.renderIgnoreBase(factor);
		bipedNeck.renderIgnoreBase(factor);
		bipedHead.renderIgnoreBase(factor);
		bipedRightShoulder.renderIgnoreBase(factor);
		bipedRightArm.renderIgnoreBase(factor);
		bipedLeftShoulder.renderIgnoreBase(factor);
		bipedLeftArm.renderIgnoreBase(factor);
		bipedPelvic.renderIgnoreBase(factor);
		bipedRightLeg.renderIgnoreBase(factor);
		bipedLeftLeg.renderIgnoreBase(factor);

		if (isModelPlayer) {
			bipedBodywear.renderIgnoreBase(factor);
			bipedHeadwear.renderIgnoreBase(factor);
			bipedRightArmwear.renderIgnoreBase(factor);
			bipedLeftArmwear.renderIgnoreBase(factor);
			bipedRightLegwear.renderIgnoreBase(factor);
			bipedLeftLegwear.renderIgnoreBase(factor);
		}

		GL11.glPopMatrix();
	}

	public void setRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor, Entity entity) {
		reset();

		if (isInventory) {
			bipedBody.ignoreBase = true;
			bipedHead.ignoreBase = true;
			bipedRightArm.ignoreBase = true;
			bipedLeftArm.ignoreBase = true;
			bipedRightLeg.ignoreBase = true;
			bipedLeftLeg.ignoreBase = true;

			if (isModelPlayer) {
				bipedBodywear.ignoreBase = true;
				bipedHeadwear.ignoreBase = true;
				bipedRightArmwear.ignoreBase = true;
				bipedLeftArmwear.ignoreBase = true;
				bipedRightLegwear.ignoreBase = true;
				bipedLeftLegwear.ignoreBase = true;
				bipedEars.ignoreBase = true;
				bipedCloak.ignoreBase = true;
			}

			bipedBody.forceRender = false;
			bipedHead.forceRender = false;
			bipedRightArm.forceRender = false;
			bipedLeftArm.forceRender = false;
			bipedRightLeg.forceRender = false;
			bipedLeftLeg.forceRender = false;

			if (isModelPlayer) {
				bipedBodywear.forceRender = false;
				bipedHeadwear.forceRender = false;
				bipedRightArmwear.forceRender = false;
				bipedLeftArmwear.forceRender = false;
				bipedRightLegwear.forceRender = false;
				bipedLeftLegwear.forceRender = false;
				bipedEars.forceRender = false;
				bipedCloak.forceRender = false;
			}

			bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
			bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
			bipedRightLeg.setRotationPoint(-2F, 12F, 0.0F);
			bipedLeftLeg.setRotationPoint(2.0F, 12F, 0.0F);

			imp.superSetRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime,
					viewHorizontalAngelOffset, viewVerticalAngelOffset, factor, entity);
			return;
		}

		if (isSleeping) {
			prevOuterRenderData.rotateAngleX = 0;
			prevOuterRenderData.rotateAngleY = 0;
			prevOuterRenderData.rotateAngleZ = 0;
		}

		bipedOuter.previous = prevOuterRenderData;

		bipedOuter.rotateAngleY = actualRotation / SRUtilities.RadiantToAngle;
		bipedOuter.fadeRotateAngleY = !entity.isRiding();

		imp.animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);

		if (isSleeping)
			imp.animateSleeping(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
					viewVerticalAngelOffset, factor);

		imp.animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);

		if (mp.isRiding)
			imp.animateRiding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
					viewVerticalAngelOffset, factor);

		if (mp.leftArmPose != ArmPose.EMPTY)
			imp.animateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime,
					viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if (mp.rightArmPose != ArmPose.EMPTY)
			imp.animateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime,
					viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

		if (mp.swingProgress > -9990F) {
			imp.animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, totalTime,
					viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
			imp.animateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime,
					viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
		}

		if (mp.isSneak)
			imp.animateSneaking(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
					viewVerticalAngelOffset, factor);

		imp.animateArms(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);

		if (mp.rightArmPose == ArmPose.BOW_AND_ARROW || mp.leftArmPose == ArmPose.BOW_AND_ARROW)
			imp.animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
					viewVerticalAngelOffset, factor);

		if (bipedOuter.previous != null && !bipedOuter.fadeRotateAngleX)
			bipedOuter.previous.rotateAngleX = bipedOuter.rotateAngleX;

		if (bipedOuter.previous != null && !bipedOuter.fadeRotateAngleY)
			bipedOuter.previous.rotateAngleY = bipedOuter.rotateAngleY;

		bipedOuter.fadeIntermediate(totalTime);
		bipedOuter.fadeStore(totalTime);

		if (isModelPlayer) {
			bipedCloak.ignoreBase = false;
			bipedCloak.rotateAngleX = SRUtilities.Sixtyfourth;
		}
	}

	public void animateHeadRotation(float viewHorizontalAngelOffset, float viewVerticalAngelOffset) {
		bipedNeck.ignoreBase = true;
		bipedHead.rotateAngleY = (actualRotation + viewHorizontalAngelOffset) / SRUtilities.RadiantToAngle;
		bipedHead.rotateAngleX = viewVerticalAngelOffset / SRUtilities.RadiantToAngle;
	}

	public void animateSleeping() {
		bipedNeck.ignoreBase = false;
		bipedHead.rotateAngleY = 0F;
		bipedHead.rotateAngleX = SRUtilities.Eighth;
		bipedTorso.rotationPointZ = -17F;
	}

	public void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed) {
		bipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + SRUtilities.Half) * 2.0F
				* currentHorizontalSpeed * 0.5F;
		bipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed
				* 0.5F;

		bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed;
		bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + SRUtilities.Half) * 1.4F
				* currentHorizontalSpeed;
	}

	public void animateRiding() {
		bipedRightArm.rotateAngleX += -0.6283185F;
		bipedLeftArm.rotateAngleX += -0.6283185F;
		bipedRightLeg.rotateAngleX = -1.256637F;
		bipedLeftLeg.rotateAngleX = -1.256637F;
		bipedRightLeg.rotateAngleY = 0.3141593F;
		bipedLeftLeg.rotateAngleY = -0.3141593F;
	}

	public void animateLeftArmItemHolding() {
		bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;
	}

	public void animateRightArmItemHolding() {
		bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;
	}

	public void animateWorkingBody() {
		float angle = MathHelper.sin(MathHelper.sqrt(mp.swingProgress) * SRUtilities.Whole) * 0.2F;
		bipedBreast.rotateAngleY = bipedBody.rotateAngleY += angle;
		bipedBreast.rotationOrder = bipedBody.rotationOrder = SRModelRotationRenderer.YXZ;
		bipedLeftArm.rotateAngleX += angle;
	}

	public void animateWorkingArms() {
		float f6 = 1.0F - mp.swingProgress;
		f6 = 1.0F - f6 * f6 * f6;
		float f7 = MathHelper.sin(f6 * SRUtilities.Half);
		float f8 = MathHelper.sin(mp.swingProgress * SRUtilities.Half) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
		bipedRightArm.rotateAngleX -= f7 * 1.2D + f8;
		bipedRightArm.rotateAngleY += MathHelper.sin(MathHelper.sqrt(mp.swingProgress) * SRUtilities.Whole) * 0.4F;
		bipedRightArm.rotateAngleZ -= MathHelper.sin(mp.swingProgress * SRUtilities.Half) * 0.4F;
	}

	public void animateSneaking() {
		bipedTorso.rotateAngleX += 0.5F;
		bipedRightLeg.rotateAngleX += -0.5F;
		bipedLeftLeg.rotateAngleX += -0.5F;
		bipedRightArm.rotateAngleX += -0.1F;
		bipedLeftArm.rotateAngleX += -0.1F;

		bipedPelvic.offsetY = -0.13652F;
		bipedPelvic.offsetZ = -0.05652F;

		bipedBreast.offsetY = -0.01872F;
		bipedBreast.offsetZ = -0.07502F;

		bipedNeck.offsetY = 0.0621F;
	}

	public void animateArms(float totalTime) {
		bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
	}

	public void animateBowAiming(float totalTime) {
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY;
		bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F - bipedOuter.rotateAngleY;
		bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
		bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
		bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
	}

	public void reset() {
		bipedOuter.reset();
		bipedTorso.reset();
		bipedBody.reset();
		bipedBreast.reset();
		bipedNeck.reset();
		bipedHead.reset();
		bipedRightShoulder.reset();
		bipedRightArm.reset();
		bipedLeftShoulder.reset();
		bipedLeftArm.reset();
		bipedPelvic.reset();
		bipedRightLeg.reset();
		bipedLeftLeg.reset();

		if (isModelPlayer) {
			bipedBodywear.reset();
			bipedHeadwear.reset();
			bipedRightArmwear.reset();
			bipedLeftArmwear.reset();
			bipedRightLegwear.reset();
			bipedLeftLegwear.reset();
			bipedEars.reset();
			bipedCloak.reset();
		}

		/*
		 * Rotation of extremities is on joints to torso; Also accounts for small arms
		 * setting
		 */
		bipedRightShoulder.setRotationPoint(-5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
		bipedLeftShoulder.setRotationPoint(5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
		bipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
		bipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
		bipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);

		if (isModelPlayer)
			bipedCloak.setRotationPoint(0.0F, 0.0F, 2.0F);
	}

	public void renderCloak(float f) {
		imp.superRenderCloak(f);
	}

	public ModelRenderer getRandomBox(Random par1Random) {
		List<?> boxList = mp.boxList;
		int size = boxList.size();
		int renderersWithBoxes = 0;

		for (int i = 0; i < size; i++) {
			ModelRenderer renderer = (ModelRenderer) boxList.get(i);
			if (canBeRandomBoxSource(renderer))
				renderersWithBoxes++;
		}

		if (renderersWithBoxes != 0) {
			int random = par1Random.nextInt(renderersWithBoxes);
			renderersWithBoxes = -1;

			for (int i = 0; i < size; i++) {
				ModelRenderer renderer = (ModelRenderer) boxList.get(i);
				if (canBeRandomBoxSource(renderer))
					renderersWithBoxes++;
				if (renderersWithBoxes == random)
					return renderer;
			}
		}

		return null;
	}

	private static boolean canBeRandomBoxSource(ModelRenderer renderer) {
		return renderer.cubeList != null && renderer.cubeList.size() > 0
				&& (!(renderer instanceof SRModelRotationRenderer)
						|| ((SRModelRotationRenderer) renderer).canBeRandomBoxSource());
	}
}
