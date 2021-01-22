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

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.smart.render.model.IModelPlayer;

public interface IRenderPlayer {
	IModelPlayer createModel(ModelBiped existing, float f, boolean b);

	void initialize(ModelPlayer modelBipedMain, ModelBiped mb);

	void superDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f,
			float renderPartialTicks);

	void superRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2);

	void superRenderSpecials(AbstractClientPlayer entityPlayer, float f1, float f2, float f3, float f4, float f5,
			float f6, float f7);

	RenderManager getRenderRenderManager();

	ModelPlayer getModelBipedMain();

	ModelBiped getModelArmor();

	boolean getSmallArms();

	IModelPlayer[] getRenderModels();
}
