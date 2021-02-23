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

public abstract class ModelPlayerBase
{
	public ModelPlayerBase(ModelPlayerAPI modelPlayerAPI)
	{
		this.internalModelPlayerAPI = modelPlayerAPI;
		this.modelPlayerAPI = modelPlayerAPI.modelPlayer;
		this.modelBiped = modelPlayerAPI.modelPlayer.getModelPlayer();
		this.modelPlayer = this.modelBiped instanceof net.minecraft.client.model.ModelPlayer ? (net.minecraft.client.model.ModelPlayer)this.modelBiped : null;
		this.modelPlayerArmor = this.modelBiped instanceof api.player.model.ModelPlayerArmor ? (api.player.model.ModelPlayerArmor)this.modelBiped : null;
	}

	public void beforeBaseAttach(boolean onTheFly)
	{
	}

	public void afterBaseAttach(boolean onTheFly)
	{
	}

	public void beforeLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
	{
	}

	public void afterLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
	{
	}

	public void beforeBaseDetach(boolean onTheFly)
	{
	}

	public void afterBaseDetach(boolean onTheFly)
	{
	}

	public Object dynamic(String key, Object[] parameters)
	{
		return internalModelPlayerAPI.dynamicOverwritten(key, parameters, this);
	}

	@Override
	public final int hashCode()
	{
		return super.hashCode();
	}

	public void beforeGetArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
	}

	public net.minecraft.client.model.ModelRenderer getArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenGetArmForSide(this);

		net.minecraft.client.model.ModelRenderer _result;
		if(overwritten == null)
			_result = modelPlayerAPI.localGetArmForSide(paramEnumHandSide);
		else if(overwritten != this)
			_result = overwritten.getArmForSide(paramEnumHandSide);
		else
			_result = null;

		return _result;
	}

	public void afterGetArmForSide(net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
	}

	public void beforeGetMainHand(net.minecraft.entity.Entity paramEntity)
	{
	}

	public net.minecraft.util.EnumHandSide getMainHand(net.minecraft.entity.Entity paramEntity)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenGetMainHand(this);

		net.minecraft.util.EnumHandSide _result;
		if(overwritten == null)
			_result = modelPlayerAPI.localGetMainHand(paramEntity);
		else if(overwritten != this)
			_result = overwritten.getMainHand(paramEntity);
		else
			_result = null;

		return _result;
	}

	public void afterGetMainHand(net.minecraft.entity.Entity paramEntity)
	{
	}

	public void beforeGetRandomModelBox(java.util.Random paramRandom)
	{
	}

	public net.minecraft.client.model.ModelRenderer getRandomModelBox(java.util.Random paramRandom)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenGetRandomModelBox(this);

		net.minecraft.client.model.ModelRenderer _result;
		if(overwritten == null)
			_result = modelPlayerAPI.localGetRandomModelBox(paramRandom);
		else if(overwritten != this)
			_result = overwritten.getRandomModelBox(paramRandom);
		else
			_result = null;

		return _result;
	}

	public void afterGetRandomModelBox(java.util.Random paramRandom)
	{
	}

	public void beforeGetTextureOffset(String paramString)
	{
	}

	public net.minecraft.client.model.TextureOffset getTextureOffset(String paramString)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenGetTextureOffset(this);

		net.minecraft.client.model.TextureOffset _result;
		if(overwritten == null)
			_result = modelPlayerAPI.localGetTextureOffset(paramString);
		else if(overwritten != this)
			_result = overwritten.getTextureOffset(paramString);
		else
			_result = null;

		return _result;
	}

	public void afterGetTextureOffset(String paramString)
	{
	}

	public void beforePostRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
	}

	public void postRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenPostRenderArm(this);

		if(overwritten == null)
			modelPlayerAPI.localPostRenderArm(paramFloat, paramEnumHandSide);
		else if(overwritten != this)
			overwritten.postRenderArm(paramFloat, paramEnumHandSide);

	}

	public void afterPostRenderArm(float paramFloat, net.minecraft.util.EnumHandSide paramEnumHandSide)
	{
	}

	public void beforeRender(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
	}

	public void render(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenRender(this);

		if(overwritten == null)
			modelPlayerAPI.localRender(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else if(overwritten != this)
			overwritten.render(paramEntity, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

	}

	public void afterRender(net.minecraft.entity.Entity paramEntity, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
	}

	public void beforeRenderCape(float paramFloat)
	{
	}

	public void renderCape(float paramFloat)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenRenderCape(this);

		if(overwritten == null)
			modelPlayerAPI.localRenderCape(paramFloat);
		else if(overwritten != this)
			overwritten.renderCape(paramFloat);

	}

	public void afterRenderCape(float paramFloat)
	{
	}

	public void beforeRenderDeadmau5Head(float paramFloat)
	{
	}

	public void renderDeadmau5Head(float paramFloat)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenRenderDeadmau5Head(this);

		if(overwritten == null)
			modelPlayerAPI.localRenderDeadmau5Head(paramFloat);
		else if(overwritten != this)
			overwritten.renderDeadmau5Head(paramFloat);

	}

	public void afterRenderDeadmau5Head(float paramFloat)
	{
	}

	public void beforeSetLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public void setLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenSetLivingAnimations(this);

		if(overwritten == null)
			modelPlayerAPI.localSetLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);
		else if(overwritten != this)
			overwritten.setLivingAnimations(paramEntityLivingBase, paramFloat1, paramFloat2, paramFloat3);

	}

	public void afterSetLivingAnimations(net.minecraft.entity.EntityLivingBase paramEntityLivingBase, float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public void beforeSetModelAttributes(net.minecraft.client.model.ModelBase paramModelBase)
	{
	}

	public void setModelAttributes(net.minecraft.client.model.ModelBase paramModelBase)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenSetModelAttributes(this);

		if(overwritten == null)
			modelPlayerAPI.localSetModelAttributes(paramModelBase);
		else if(overwritten != this)
			overwritten.setModelAttributes(paramModelBase);

	}

	public void afterSetModelAttributes(net.minecraft.client.model.ModelBase paramModelBase)
	{
	}

	public void beforeSetRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity)
	{
	}

	public void setRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenSetRotationAngles(this);

		if(overwritten == null)
			modelPlayerAPI.localSetRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);
		else if(overwritten != this)
			overwritten.setRotationAngles(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramEntity);

	}

	public void afterSetRotationAngles(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, net.minecraft.entity.Entity paramEntity)
	{
	}

	public void beforeSetTextureOffset(String paramString, int paramInt1, int paramInt2)
	{
	}

	public void setTextureOffset(String paramString, int paramInt1, int paramInt2)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenSetTextureOffset(this);

		if(overwritten == null)
			modelPlayerAPI.localSetTextureOffset(paramString, paramInt1, paramInt2);
		else if(overwritten != this)
			overwritten.setTextureOffset(paramString, paramInt1, paramInt2);

	}

	public void afterSetTextureOffset(String paramString, int paramInt1, int paramInt2)
	{
	}

	public void beforeSetVisible(boolean paramBoolean)
	{
	}

	public void setVisible(boolean paramBoolean)
	{
		ModelPlayerBase overwritten = internalModelPlayerAPI.GetOverwrittenSetVisible(this);

		if(overwritten == null)
			modelPlayerAPI.localSetVisible(paramBoolean);
		else if(overwritten != this)
			overwritten.setVisible(paramBoolean);

	}

	public void afterSetVisible(boolean paramBoolean)
	{
	}

	protected final net.minecraft.client.model.ModelBiped modelBiped;
	protected final net.minecraft.client.model.ModelPlayer modelPlayer;
	protected final api.player.model.ModelPlayerArmor modelPlayerArmor;
	protected final IModelPlayer modelPlayerAPI;
	private final ModelPlayerAPI internalModelPlayerAPI;
}
