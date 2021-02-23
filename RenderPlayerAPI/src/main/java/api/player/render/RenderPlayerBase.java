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

package api.player.render;

public abstract class RenderPlayerBase
{
	public RenderPlayerBase(RenderPlayerAPI renderPlayerAPI)
	{
		this.internalRenderPlayerAPI = renderPlayerAPI;
		this.renderPlayerAPI = renderPlayerAPI.renderPlayer;
		this.renderPlayer = renderPlayerAPI.renderPlayer.getRenderPlayer();
	}

	public void beforeBaseAttach(boolean onTheFly)
	{
	}

	public void afterBaseAttach(boolean onTheFly)
	{
	}

	public void beforeLocalConstructing(net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
	{
	}

	public void afterLocalConstructing(net.minecraft.client.renderer.entity.RenderManager paramRenderManager, boolean paramBoolean)
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
		return internalRenderPlayerAPI.dynamicOverwritten(key, parameters, this);
	}

	@Override
	public final int hashCode()
	{
		return super.hashCode();
	}

	public void beforeAddLayer(net.minecraft.client.renderer.entity.layers.LayerRenderer paramLayerRenderer)
	{
	}

	public boolean addLayer(net.minecraft.client.renderer.entity.layers.LayerRenderer paramLayerRenderer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenAddLayer(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localAddLayer(paramLayerRenderer);
		else if(overwritten != this)
			_result = overwritten.addLayer(paramLayerRenderer);
		else
			_result = false;

		return _result;
	}

	public void afterAddLayer(net.minecraft.client.renderer.entity.layers.LayerRenderer paramLayerRenderer)
	{
	}

	public void beforeBindEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public boolean bindEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenBindEntityTexture(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localBindEntityTexture(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.bindEntityTexture(paramAbstractClientPlayer);
		else
			_result = false;

		return _result;
	}

	public void afterBindEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeBindTexture(net.minecraft.util.ResourceLocation paramResourceLocation)
	{
	}

	public void bindTexture(net.minecraft.util.ResourceLocation paramResourceLocation)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenBindTexture(this);

		if(overwritten == null)
			renderPlayerAPI.localBindTexture(paramResourceLocation);
		else if(overwritten != this)
			overwritten.bindTexture(paramResourceLocation);

	}

	public void afterBindTexture(net.minecraft.util.ResourceLocation paramResourceLocation)
	{
	}

	public void beforeCanRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public boolean canRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenCanRenderName(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localCanRenderName(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.canRenderName(paramAbstractClientPlayer);
		else
			_result = false;

		return _result;
	}

	public void afterCanRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeDoRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void doRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenDoRender(this);

		if(overwritten == null)
			renderPlayerAPI.localDoRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else if(overwritten != this)
			overwritten.doRender(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	public void afterDoRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void beforeDoRenderShadowAndFire(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void doRenderShadowAndFire(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenDoRenderShadowAndFire(this);

		if(overwritten == null)
			renderPlayerAPI.localDoRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else if(overwritten != this)
			overwritten.doRenderShadowAndFire(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	public void afterDoRenderShadowAndFire(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void beforeGetColorMultiplier(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2)
	{
	}

	public int getColorMultiplier(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetColorMultiplier(this);

		int _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);
		else if(overwritten != this)
			_result = overwritten.getColorMultiplier(paramAbstractClientPlayer, paramFloat1, paramFloat2);
		else
			_result = 0;

		return _result;
	}

	public void afterGetColorMultiplier(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2)
	{
	}

	public void beforeGetDeathMaxRotation(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public float getDeathMaxRotation(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetDeathMaxRotation(this);

		float _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetDeathMaxRotation(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.getDeathMaxRotation(paramAbstractClientPlayer);
		else
			_result = 0;

		return _result;
	}

	public void afterGetDeathMaxRotation(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeGetEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public net.minecraft.util.ResourceLocation getEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetEntityTexture(this);

		net.minecraft.util.ResourceLocation _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetEntityTexture(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.getEntityTexture(paramAbstractClientPlayer);
		else
			_result = null;

		return _result;
	}

	public void afterGetEntityTexture(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeGetFontRendererFromRenderManager()
	{
	}

	public net.minecraft.client.gui.FontRenderer getFontRendererFromRenderManager()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetFontRendererFromRenderManager(this);

		net.minecraft.client.gui.FontRenderer _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetFontRendererFromRenderManager();
		else if(overwritten != this)
			_result = overwritten.getFontRendererFromRenderManager();
		else
			_result = null;

		return _result;
	}

	public void afterGetFontRendererFromRenderManager()
	{
	}

	public void beforeGetMainModel()
	{
	}

	public net.minecraft.client.model.ModelPlayer getMainModel()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetMainModel(this);

		net.minecraft.client.model.ModelPlayer _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetMainModel();
		else if(overwritten != this)
			_result = overwritten.getMainModel();
		else
			_result = null;

		return _result;
	}

	public void afterGetMainModel()
	{
	}

	public void beforeGetRenderManager()
	{
	}

	public net.minecraft.client.renderer.entity.RenderManager getRenderManager()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetRenderManager(this);

		net.minecraft.client.renderer.entity.RenderManager _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetRenderManager();
		else if(overwritten != this)
			_result = overwritten.getRenderManager();
		else
			_result = null;

		return _result;
	}

	public void afterGetRenderManager()
	{
	}

	public void beforeGetSwingProgress(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public float getSwingProgress(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetSwingProgress(this);

		float _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetSwingProgress(paramAbstractClientPlayer, paramFloat);
		else if(overwritten != this)
			_result = overwritten.getSwingProgress(paramAbstractClientPlayer, paramFloat);
		else
			_result = 0;

		return _result;
	}

	public void afterGetSwingProgress(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void beforeGetTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public int getTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenGetTeamColor(this);

		int _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localGetTeamColor(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.getTeamColor(paramAbstractClientPlayer);
		else
			_result = 0;

		return _result;
	}

	public void afterGetTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeHandleRotationFloat(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public float handleRotationFloat(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenHandleRotationFloat(this);

		float _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localHandleRotationFloat(paramAbstractClientPlayer, paramFloat);
		else if(overwritten != this)
			_result = overwritten.handleRotationFloat(paramAbstractClientPlayer, paramFloat);
		else
			_result = 0;

		return _result;
	}

	public void afterHandleRotationFloat(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void beforeInterpolateRotation(float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public float interpolateRotation(float paramFloat1, float paramFloat2, float paramFloat3)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenInterpolateRotation(this);

		float _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localInterpolateRotation(paramFloat1, paramFloat2, paramFloat3);
		else if(overwritten != this)
			_result = overwritten.interpolateRotation(paramFloat1, paramFloat2, paramFloat3);
		else
			_result = 0;

		return _result;
	}

	public void afterInterpolateRotation(float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public void beforeIsMultipass()
	{
	}

	public boolean isMultipass()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenIsMultipass(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localIsMultipass();
		else if(overwritten != this)
			_result = overwritten.isMultipass();
		else
			_result = false;

		return _result;
	}

	public void afterIsMultipass()
	{
	}

	public void beforeIsVisible(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public boolean isVisible(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenIsVisible(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localIsVisible(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.isVisible(paramAbstractClientPlayer);
		else
			_result = false;

		return _result;
	}

	public void afterIsVisible(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforePreRenderCallback(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void preRenderCallback(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenPreRenderCallback(this);

		if(overwritten == null)
			renderPlayerAPI.localPreRenderCallback(paramAbstractClientPlayer, paramFloat);
		else if(overwritten != this)
			overwritten.preRenderCallback(paramAbstractClientPlayer, paramFloat);

	}

	public void afterPreRenderCallback(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void beforePrepareScale(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public float prepareScale(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenPrepareScale(this);

		float _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localPrepareScale(paramAbstractClientPlayer, paramFloat);
		else if(overwritten != this)
			_result = overwritten.prepareScale(paramAbstractClientPlayer, paramFloat);
		else
			_result = 0;

		return _result;
	}

	public void afterPrepareScale(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void beforeRenderEntityName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4)
	{
	}

	public void renderEntityName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderEntityName(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);
		else if(overwritten != this)
			overwritten.renderEntityName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramString, paramDouble4);

	}

	public void afterRenderEntityName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, String paramString, double paramDouble4)
	{
	}

	public void beforeRenderLayers(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
	{
	}

	public void renderLayers(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderLayers(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);
		else if(overwritten != this)
			overwritten.renderLayers(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7);

	}

	public void afterRenderLayers(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7)
	{
	}

	public void beforeRenderLeftArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void renderLeftArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderLeftArm(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderLeftArm(paramAbstractClientPlayer);
		else if(overwritten != this)
			overwritten.renderLeftArm(paramAbstractClientPlayer);

	}

	public void afterRenderLeftArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeRenderLivingAt(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public void renderLivingAt(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderLivingAt(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else if(overwritten != this)
			overwritten.renderLivingAt(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

	}

	public void afterRenderLivingAt(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public void beforeRenderLivingLabel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
	{
	}

	public void renderLivingLabel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderLivingLabel(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);
		else if(overwritten != this)
			overwritten.renderLivingLabel(paramAbstractClientPlayer, paramString, paramDouble1, paramDouble2, paramDouble3, paramInt);

	}

	public void afterRenderLivingLabel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3, int paramInt)
	{
	}

	public void beforeRenderModel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
	}

	public void renderModel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderModel(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
		else if(overwritten != this)
			overwritten.renderModel(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);

	}

	public void afterRenderModel(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
	{
	}

	public void beforeRenderMultipass(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void renderMultipass(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderMultipass(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		else if(overwritten != this)
			overwritten.renderMultipass(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);

	}

	public void afterRenderMultipass(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2)
	{
	}

	public void beforeRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public void renderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderName(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);
		else if(overwritten != this)
			overwritten.renderName(paramAbstractClientPlayer, paramDouble1, paramDouble2, paramDouble3);

	}

	public void afterRenderName(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public void beforeRenderRightArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void renderRightArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRenderRightArm(this);

		if(overwritten == null)
			renderPlayerAPI.localRenderRightArm(paramAbstractClientPlayer);
		else if(overwritten != this)
			overwritten.renderRightArm(paramAbstractClientPlayer);

	}

	public void afterRenderRightArm(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeRotateCorpse(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public void rotateCorpse(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenRotateCorpse(this);

		if(overwritten == null)
			renderPlayerAPI.localRotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);
		else if(overwritten != this)
			overwritten.rotateCorpse(paramAbstractClientPlayer, paramFloat1, paramFloat2, paramFloat3);

	}

	public void afterRotateCorpse(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, float paramFloat3)
	{
	}

	public void beforeSetBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat, boolean paramBoolean)
	{
	}

	public boolean setBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat, boolean paramBoolean)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenSetBrightness(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localSetBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);
		else if(overwritten != this)
			_result = overwritten.setBrightness(paramAbstractClientPlayer, paramFloat, paramBoolean);
		else
			_result = false;

		return _result;
	}

	public void afterSetBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat, boolean paramBoolean)
	{
	}

	public void beforeSetDoRenderBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public boolean setDoRenderBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenSetDoRenderBrightness(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localSetDoRenderBrightness(paramAbstractClientPlayer, paramFloat);
		else if(overwritten != this)
			_result = overwritten.setDoRenderBrightness(paramAbstractClientPlayer, paramFloat);
		else
			_result = false;

		return _result;
	}

	public void afterSetDoRenderBrightness(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, float paramFloat)
	{
	}

	public void beforeSetModelVisibilities(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void setModelVisibilities(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenSetModelVisibilities(this);

		if(overwritten == null)
			renderPlayerAPI.localSetModelVisibilities(paramAbstractClientPlayer);
		else if(overwritten != this)
			overwritten.setModelVisibilities(paramAbstractClientPlayer);

	}

	public void afterSetModelVisibilities(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeSetRenderOutlines(boolean paramBoolean)
	{
	}

	public void setRenderOutlines(boolean paramBoolean)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenSetRenderOutlines(this);

		if(overwritten == null)
			renderPlayerAPI.localSetRenderOutlines(paramBoolean);
		else if(overwritten != this)
			overwritten.setRenderOutlines(paramBoolean);

	}

	public void afterSetRenderOutlines(boolean paramBoolean)
	{
	}

	public void beforeSetScoreTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public boolean setScoreTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenSetScoreTeamColor(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localSetScoreTeamColor(paramAbstractClientPlayer);
		else if(overwritten != this)
			_result = overwritten.setScoreTeamColor(paramAbstractClientPlayer);
		else
			_result = false;

		return _result;
	}

	public void afterSetScoreTeamColor(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer)
	{
	}

	public void beforeShouldRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, net.minecraft.client.renderer.culling.ICamera paramICamera, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public boolean shouldRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, net.minecraft.client.renderer.culling.ICamera paramICamera, double paramDouble1, double paramDouble2, double paramDouble3)
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenShouldRender(this);

		boolean _result;
		if(overwritten == null)
			_result = renderPlayerAPI.localShouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);
		else if(overwritten != this)
			_result = overwritten.shouldRender(paramAbstractClientPlayer, paramICamera, paramDouble1, paramDouble2, paramDouble3);
		else
			_result = false;

		return _result;
	}

	public void afterShouldRender(net.minecraft.client.entity.AbstractClientPlayer paramAbstractClientPlayer, net.minecraft.client.renderer.culling.ICamera paramICamera, double paramDouble1, double paramDouble2, double paramDouble3)
	{
	}

	public void beforeTransformHeldFull3DItemLayer()
	{
	}

	public void transformHeldFull3DItemLayer()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenTransformHeldFull3DItemLayer(this);

		if(overwritten == null)
			renderPlayerAPI.localTransformHeldFull3DItemLayer();
		else if(overwritten != this)
			overwritten.transformHeldFull3DItemLayer();

	}

	public void afterTransformHeldFull3DItemLayer()
	{
	}

	public void beforeUnsetBrightness()
	{
	}

	public void unsetBrightness()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenUnsetBrightness(this);

		if(overwritten == null)
			renderPlayerAPI.localUnsetBrightness();
		else if(overwritten != this)
			overwritten.unsetBrightness();

	}

	public void afterUnsetBrightness()
	{
	}

	public void beforeUnsetScoreTeamColor()
	{
	}

	public void unsetScoreTeamColor()
	{
		RenderPlayerBase overwritten = internalRenderPlayerAPI.GetOverwrittenUnsetScoreTeamColor(this);

		if(overwritten == null)
			renderPlayerAPI.localUnsetScoreTeamColor();
		else if(overwritten != this)
			overwritten.unsetScoreTeamColor();

	}

	public void afterUnsetScoreTeamColor()
	{
	}

	protected final net.minecraft.client.renderer.entity.RenderPlayer renderPlayer;
	protected final IRenderPlayer renderPlayerAPI;
	private final RenderPlayerAPI internalRenderPlayerAPI;
}
