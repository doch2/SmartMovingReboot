// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package api.player.model;

public interface IModelPlayer
{
	ModelPlayerBase getModelPlayerBase(String baseId);

	java.util.Set<String> getModelPlayerBaseIds();

	float getExpandParameter();

	float getYOffsetParameter();

	int getTextureWidthParameter();

	int getTextureHeightParameter();

	boolean getSmallArmsParameter();

	String getModelPlayerType();

	Object dynamic(String key, Object[] parameters);

	net.minecraft.client.model.ModelRenderer realGetArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide);

	net.minecraft.client.model.ModelRenderer superGetArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide);

	net.minecraft.client.model.ModelRenderer localGetArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide);

	net.minecraft.util.EnumHandSide realGetMainHand(net.minecraft.entity.Entity paramEntity);

	net.minecraft.util.EnumHandSide superGetMainHand(net.minecraft.entity.Entity paramEntity);

	net.minecraft.util.EnumHandSide localGetMainHand(net.minecraft.entity.Entity paramEntity);

	net.minecraft.client.model.ModelRenderer realGetRandomModelBox(java.util.Random paramRandom);

	net.minecraft.client.model.ModelRenderer superGetRandomModelBox(java.util.Random paramRandom);

	net.minecraft.client.model.ModelRenderer localGetRandomModelBox(java.util.Random paramRandom);

	net.minecraft.client.model.TextureOffset realGetTextureOffset(String paramString);

	net.minecraft.client.model.TextureOffset superGetTextureOffset(String paramString);

	net.minecraft.client.model.TextureOffset localGetTextureOffset(String paramString);

	void realPostRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide);

	void superPostRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide);

	void localPostRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide);

	void realRender(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

	void superRender(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

	void localRender(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

	void realRenderCape(float paramFloat);

	void localRenderCape(float paramFloat);

	void realRenderDeadmau5Head(float paramFloat);

	void localRenderDeadmau5Head(float paramFloat);

	void realSetLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3);

	void superSetLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3);

	void localSetLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3);

	void realSetModelAttributes(net.minecraft.client.model.ModelBase paramModelBase);

	void superSetModelAttributes(net.minecraft.client.model.ModelBase paramModelBase);

	void localSetModelAttributes(net.minecraft.client.model.ModelBase paramModelBase);

	void realSetRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity);

	void superSetRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity);

	void localSetRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity);

	void realSetTextureOffset(String paramString, int paramInt1, int paramInt2);

	void superSetTextureOffset(String paramString, int paramInt1, int paramInt2);

	void localSetTextureOffset(String paramString, int paramInt1, int paramInt2);

	void realSetVisible(boolean paramBoolean);

	void superSetVisible(boolean paramBoolean);

	void localSetVisible(boolean paramBoolean);

	net.minecraft.client.model.ModelRenderer getBipedBodyField();

	void setBipedBodyField(net.minecraft.client.model.ModelRenderer bipedBody);

	net.minecraft.client.model.ModelRenderer getBipedBodyWearField();

	void setBipedBodyWearField(net.minecraft.client.model.ModelRenderer bipedBodyWear);

	net.minecraft.client.model.ModelRenderer getBipedCapeField();

	net.minecraft.client.model.ModelRenderer getBipedDeadmau5HeadField();

	net.minecraft.client.model.ModelRenderer getBipedHeadField();

	void setBipedHeadField(net.minecraft.client.model.ModelRenderer bipedHead);

	net.minecraft.client.model.ModelRenderer getBipedHeadwearField();

	void setBipedHeadwearField(net.minecraft.client.model.ModelRenderer bipedHeadwear);

	net.minecraft.client.model.ModelRenderer getBipedLeftArmField();

	void setBipedLeftArmField(net.minecraft.client.model.ModelRenderer bipedLeftArm);

	net.minecraft.client.model.ModelRenderer getBipedLeftArmwearField();

	void setBipedLeftArmwearField(net.minecraft.client.model.ModelRenderer bipedLeftArmwear);

	net.minecraft.client.model.ModelRenderer getBipedLeftLegField();

	void setBipedLeftLegField(net.minecraft.client.model.ModelRenderer bipedLeftLeg);

	net.minecraft.client.model.ModelRenderer getBipedLeftLegwearField();

	void setBipedLeftLegwearField(net.minecraft.client.model.ModelRenderer bipedLeftLegwear);

	net.minecraft.client.model.ModelRenderer getBipedRightArmField();

	void setBipedRightArmField(net.minecraft.client.model.ModelRenderer bipedRightArm);

	net.minecraft.client.model.ModelRenderer getBipedRightArmwearField();

	void setBipedRightArmwearField(net.minecraft.client.model.ModelRenderer bipedRightArmwear);

	net.minecraft.client.model.ModelRenderer getBipedRightLegField();

	void setBipedRightLegField(net.minecraft.client.model.ModelRenderer bipedRightLeg);

	net.minecraft.client.model.ModelRenderer getBipedRightLegwearField();

	void setBipedRightLegwearField(net.minecraft.client.model.ModelRenderer bipedRightLegwear);

	java.util.List<?> getBoxListField();

	void setBoxListField(java.util.List<?> boxList);

	boolean getIsChildField();

	void setIsChildField(boolean isChild);

	boolean getIsRidingField();

	void setIsRidingField(boolean isRiding);

	boolean getIsSneakField();

	void setIsSneakField(boolean isSneak);

	net.minecraft.client.model.ModelBiped.ArmPose getLeftArmPoseField();

	void setLeftArmPoseField(net.minecraft.client.model.ModelBiped.ArmPose leftArmPose);

	net.minecraft.client.model.ModelBiped.ArmPose getRightArmPoseField();

	void setRightArmPoseField(net.minecraft.client.model.ModelBiped.ArmPose rightArmPose);

	boolean getSmallArmsField();

	float getSwingProgressField();

	void setSwingProgressField(float swingProgress);

	int getTextureHeightField();

	void setTextureHeightField(int textureHeight);

	int getTextureWidthField();

	void setTextureWidthField(int textureWidth);

}
