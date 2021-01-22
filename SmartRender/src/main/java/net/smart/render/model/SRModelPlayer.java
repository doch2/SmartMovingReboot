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
import java.util.Random;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.smart.render.SRInstall;
import net.smart.utilities.Reflect;

public class SRModelPlayer extends ModelPlayer implements IModelPlayer {
	private final SRModel model;

	private static final Field _bipedCape = Reflect.GetField(ModelPlayer.class, SRInstall.ModelPlayer_bipedCape);
	private static final Field _bipedDeadmau5Head = Reflect.GetField(ModelPlayer.class,
			SRInstall.ModelPlayer_bipedDeadmau5Head);

	public SRModelPlayer(ModelBiped existing, float f, boolean b) {
		super(f, b);

		ModelRenderer bipedCape = (ModelRenderer) Reflect.GetField(_bipedCape, this);
		ModelRenderer bipedDeadmau5Head = (ModelRenderer) Reflect.GetField(_bipedDeadmau5Head, this);

		model = new SRModel(b, this, this, existing.bipedBody, bipedBodyWear, existing.bipedHead,
				existing.bipedHeadwear, existing.bipedRightArm, bipedRightArmwear, existing.bipedLeftArm,
				bipedLeftArmwear, existing.bipedRightLeg, bipedRightLegwear, existing.bipedLeftLeg, bipedLeftLegwear,
				bipedCape, bipedDeadmau5Head);
	}

	@Override
	public void render(Entity entity, float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.render(entity, totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);
	}

	@Override
	public void superRender(Entity entity, float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		super.render(entity, totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor);
	}

	@Override
	public SRModel getRenderModel() {
		return model;
	}

	@Override
	public void initialize(SRModelRotationRenderer bipedBody, SRModelRotationRenderer bipedBodywear,
			SRModelRotationRenderer bipedHead, SRModelRotationRenderer bipedHeadwear,
			SRModelRotationRenderer bipedRightArm, SRModelRotationRenderer bipedRightArmwear,
			SRModelRotationRenderer bipedLeftArm, SRModelRotationRenderer bipedLeftArmwear,
			SRModelRotationRenderer bipedRightLeg, SRModelRotationRenderer bipedRightLegwear,
			SRModelRotationRenderer bipedLeftLeg, SRModelRotationRenderer bipedLeftLegwear,
			SRModelCapeRenderer bipedCloak, SRModelEarsRenderer bipedEars) {
		this.bipedBody = bipedBody;
		this.bipedHead = bipedHead;
		this.bipedRightArm = bipedRightArm;
		this.bipedLeftArm = bipedLeftArm;
		this.bipedRightLeg = bipedRightLeg;
		this.bipedLeftLeg = bipedLeftLeg;

		this.bipedBodyWear = bipedBodywear;
		this.bipedHeadwear = bipedHeadwear;
		this.bipedRightArmwear = bipedRightArmwear;
		this.bipedLeftArmwear = bipedLeftArmwear;
		this.bipedRightLegwear = bipedRightLegwear;
		this.bipedLeftLegwear = bipedLeftLegwear;

		Reflect.SetField(_bipedCape, this, bipedCloak);
		Reflect.SetField(_bipedDeadmau5Head, this, bipedEars);
	}

	@Override
	public void setRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor, Entity entity) {
		model.setRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor, entity);
	}

	@Override
	public void superSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor, Entity entity) {
		super.setRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset,
				viewVerticalAngelOffset, factor, entity);
	}

	@Override
	public void renderCape(float f) {
		model.renderCloak(f);
	}

	@Override
	public void superRenderCloak(float f) {
		super.renderCape(f);
	}

	@Override
	public ModelRenderer getRandomModelBox(Random random) {
		return model.getRandomBox(random);
	}

	@Override
	public ModelRenderer getOuter() {
		return model.bipedOuter;
	}

	@Override
	public ModelRenderer getTorso() {
		return model.bipedTorso;
	}

	@Override
	public ModelRenderer getBody() {
		return model.bipedBody;
	}

	@Override
	public ModelRenderer getBreast() {
		return model.bipedBreast;
	}

	@Override
	public ModelRenderer getNeck() {
		return model.bipedNeck;
	}

	@Override
	public ModelRenderer getHead() {
		return model.bipedHead;
	}

	@Override
	public ModelRenderer getHeadwear() {
		return model.bipedHeadwear;
	}

	@Override
	public ModelRenderer getRightShoulder() {
		return model.bipedRightShoulder;
	}

	@Override
	public ModelRenderer getRightArm() {
		return model.bipedRightArm;
	}

	@Override
	public ModelRenderer getLeftShoulder() {
		return model.bipedLeftShoulder;
	}

	@Override
	public ModelRenderer getLeftArm() {
		return model.bipedLeftArm;
	}

	@Override
	public ModelRenderer getPelvic() {
		return model.bipedPelvic;
	}

	@Override
	public ModelRenderer getRightLeg() {
		return model.bipedRightLeg;
	}

	@Override
	public ModelRenderer getLeftLeg() {
		return model.bipedLeftLeg;
	}

	@Override
	public ModelRenderer getEars() {
		return model.bipedEars;
	}

	@Override
	public ModelRenderer getCloak() {
		return model.bipedCloak;
	}

	@Override
	public void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateHeadRotation(viewHorizontalAngelOffset, viewVerticalAngelOffset);
	}

	@Override
	public void animateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateSleeping();
	}

	@Override
	public void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed);
	}

	@Override
	public void animateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateRiding();
	}

	@Override
	public void animateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateLeftArmItemHolding();
	}

	@Override
	public void animateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateRightArmItemHolding();
	}

	@Override
	public void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateWorkingBody();
	}

	@Override
	public void animateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateWorkingArms();
	}

	@Override
	public void animateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateSneaking();
	}

	@Override
	public void animateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateArms(totalTime);
	}

	@Override
	public void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor) {
		model.animateBowAiming(totalTime);
	}
}
