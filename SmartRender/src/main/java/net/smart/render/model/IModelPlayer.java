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

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public interface IModelPlayer {
	SRModel getRenderModel();

	void initialize(SRModelRotationRenderer bipedBody, SRModelRotationRenderer bipedBodywear,
			SRModelRotationRenderer bipedHead, SRModelRotationRenderer bipedHeadwear,
			SRModelRotationRenderer bipedRightArm, SRModelRotationRenderer bipedRightArmwear,
			SRModelRotationRenderer bipedLeftArm, SRModelRotationRenderer bipedLeftArmwear,
			SRModelRotationRenderer bipedRightLeg, SRModelRotationRenderer bipedRightLegwear,
			SRModelRotationRenderer bipedLeftLeg, SRModelRotationRenderer bipedLeftLegwear,
			SRModelCapeRenderer bipedCloak, SRModelEarsRenderer bipedEars);

	void superRender(Entity entity, float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void superSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor, Entity entity);

	void superRenderCloak(float f);

	ModelRenderer getOuter();

	ModelRenderer getTorso();

	ModelRenderer getBody();

	ModelRenderer getBreast();

	ModelRenderer getNeck();

	ModelRenderer getHead();

	ModelRenderer getHeadwear();

	ModelRenderer getRightShoulder();

	ModelRenderer getRightArm();

	ModelRenderer getLeftShoulder();

	ModelRenderer getLeftArm();

	ModelRenderer getPelvic();

	ModelRenderer getRightLeg();

	ModelRenderer getLeftLeg();

	ModelRenderer getEars();

	ModelRenderer getCloak();

	void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateSleeping(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateRiding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateLeftArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateRightArmItemHolding(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateWorkingArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateSneaking(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateArms(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);

	void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime,
			float viewHorizontalAngelOffset, float viewVerticalAngelOffset, float factor);
}
