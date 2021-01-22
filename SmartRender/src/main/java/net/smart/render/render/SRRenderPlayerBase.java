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

import java.lang.reflect.Field;
import java.util.List;

import api.player.model.ModelPlayerAPI;
import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.smart.render.SRContext;
import net.smart.render.SRInstall;
import net.smart.render.model.IModelPlayer;
import net.smart.utilities.Reflect;

public class SRRenderPlayerBase extends RenderPlayerBase implements IRenderPlayer {
	private SRRenderer renderer;
	private ModelBiped[] allModelPlayers;
	private IModelPlayer[] allIModelPlayers;

	private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class,
			SRInstall.LayerArmorBase_modelArmor);
	private final static Field _layerRenderers = Reflect.GetField(RenderLivingBase.class,
			SRInstall.RenderLivingBase_layerRenderers);

	public SRRenderPlayerBase(RenderPlayerAPI renderPlayerAPI) {
		super(renderPlayerAPI);
	}

	private SRRenderer getRenderer() {
		if (renderer == null)
			renderer = new SRRenderer(this);
		return renderer;
	}

	@Override
	public IModelPlayer createModel(ModelBiped existing, float f, boolean b) {
		return SRContext.getPlayerBase(existing);
	}

	@Override
	public boolean getSmallArms() {
		return renderPlayerAPI.getSmallArmsField();
	}

	@Override
	public void initialize(ModelPlayer modelBipedMain, ModelBiped modelArmor) {
		/* Fix broken layer renderers with custom layers */
		List<LayerRenderer<AbstractClientPlayer>> layerRenderers = (List<LayerRenderer<AbstractClientPlayer>>) Reflect
				.GetField(_layerRenderers, renderPlayer);
		layerRenderers.removeIf(layer -> layer.getClass() == LayerElytra.class
				|| layer.getClass() == LayerCustomHead.class || layer.getClass() == LayerEntityOnShoulder.class);

		renderPlayer.addLayer(new SRLayerEntityOnShoulder(renderPlayer.getRenderManager(), this));
		renderPlayer.addLayer(new SRLayerElytra(renderPlayer, this));
		renderPlayer.addLayer(new SRLayerCustomHead(modelBipedMain.bipedHead));
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks) {
		getRenderer().doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks) {
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2) {
		getRenderer().rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2) {
		super.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void renderLayers(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7) {
		getRenderer().renderSpecials(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void superRenderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7) {
		super.renderLayers(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void beforeHandleRotationFloat(AbstractClientPlayer entityliving, float f) {
		getRenderer().beforeHandleRotationFloat(entityliving, f);
	}

	@Override
	public void afterHandleRotationFloat(AbstractClientPlayer entityliving, float f) {
		getRenderer().afterHandleRotationFloat(entityliving, f);
	}

	@Override
	public RenderManager getRenderRenderManager() {
		return renderPlayerAPI.getRenderManagerField();
	}

	@Override
	public ModelPlayer getModelBipedMain() {
		return renderPlayer.getMainModel();
	}

	@Override
	public ModelBiped getModelArmor() {
		for (Object layer : renderPlayerAPI.getLayerRenderersField())
			if (layer instanceof LayerArmorBase)
				return (ModelBiped) Reflect.GetField(_modelArmor, layer);
		return null;
	}

	@Override
	public IModelPlayer[] getRenderModels() {
		ModelBiped[] modelPlayers = ModelPlayerAPI.getAllInstances();
		if (allModelPlayers != null
				&& (allModelPlayers == modelPlayers || modelPlayers.length == 0 && allModelPlayers.length == 0))
			return allIModelPlayers;

		allModelPlayers = modelPlayers;
		allIModelPlayers = new IModelPlayer[modelPlayers.length];
		for (int i = 0; i < allIModelPlayers.length; i++)
			allIModelPlayers[i] = SRContext.getPlayerBase(allModelPlayers[i]);
		return allIModelPlayers;
	}
}
