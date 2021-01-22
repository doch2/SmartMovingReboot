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

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.smart.render.SRInstall;
import net.smart.render.model.IModelPlayer;
import net.smart.render.model.SRModelBiped;
import net.smart.render.model.SRModelPlayer;
import net.smart.utilities.Reflect;

public class SRRenderPlayer extends RenderPlayer implements IRenderPlayer {
	private IModelPlayer[] allIModelPlayers;

	private final SRRenderer render;

	private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class,
			SRInstall.LayerArmorBase_modelArmor);
	private final static Field _playerHead = Reflect.GetField(LayerCustomHead.class,
			SRInstall.LayerCustomHead_playerHead);

	public SRRenderPlayer(RenderManager renderManager) {
		this(renderManager, false);
	}

	public SRRenderPlayer(RenderManager renderManager, boolean b) {
		super(renderManager, b);
		render = new SRRenderer(this);
	}

	@Override
	public IModelPlayer createModel(ModelBiped existing, float f, boolean b) {
		if (existing instanceof ModelPlayer)
			return new SRModelPlayer(existing, f, b);
		return new SRModelBiped(existing, f);
	}

	@Override
	public boolean getSmallArms() {
		return (Boolean) Reflect.GetField(RenderPlayer.class, this, SRInstall.RenderPlayer_smallArms);
	}

	@Override
	public void initialize(ModelPlayer modelBipedMain, ModelBiped modelArmor) {
		mainModel = modelBipedMain;

		for (Object layer : this.layerRenderers) {
			if (layer instanceof LayerArmorBase)
				Reflect.SetField(_modelArmor, layer, modelArmor);
			if (layer instanceof LayerCustomHead)
				Reflect.SetField(_playerHead, layer, modelBipedMain.bipedHead);
		}
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks) {
		render.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks) {
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	protected void applyRotations(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2) {
		render.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2) {
		super.applyRotations(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	protected void renderLayers(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7) {
		render.renderSpecials(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	public void superRenderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7) {
		super.renderLayers(entityPlayer, f1, f2, f3, f4, f5, f6, f7);
	}

	@Override
	protected float handleRotationFloat(AbstractClientPlayer entityPlayer, float f) {
		render.beforeHandleRotationFloat(entityPlayer, f);
		float result = super.handleRotationFloat(entityPlayer, f);
		render.afterHandleRotationFloat(entityPlayer, f);
		return result;
	}

	@Override
	public RenderManager getRenderRenderManager() {
		return renderManager;
	}

	@Override
	public ModelPlayer getModelBipedMain() {
		return this.getMainModel();
	}

	@Override
	public ModelBiped getModelArmor() {
		for (Object layer : this.layerRenderers)
			if (layer instanceof LayerArmorBase)
				return (ModelBiped) Reflect.GetField(_modelArmor, layer);
		return null;
	}

	public IModelPlayer getRenderModelBipedMain() {
		return (IModelPlayer) getModelBipedMain();
	}

	public IModelPlayer getRenderModelArmor() {
		return (IModelPlayer) getModelArmor();
	}

	@Override
	public IModelPlayer[] getRenderModels() {
		if (allIModelPlayers == null)
			allIModelPlayers = new IModelPlayer[] { getRenderModelBipedMain(), getRenderModelArmor() };
		return allIModelPlayers;
	}
}
