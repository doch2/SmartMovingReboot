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

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import api.player.model.*;

public class LayerPlayerArmor extends LayerBipedArmor
{
	public LayerPlayerArmor(RenderLivingBase<?> entity)
	{
		super(entity);
	}

	protected void func_177177_a()
	{
		try
		{
			try
			{
				LayerArmorBase.class.getDeclaredField("field_177189_c").set(this, new ModelPlayerArmor(0.5F));
				LayerArmorBase.class.getDeclaredField("field_177186_d").set(this, new ModelPlayerArmor(1.0F));
			}
			catch(NoSuchFieldException e)
			{
				this.modelLeggings = new ModelPlayerArmor(0.5F);
				this.modelArmor = new ModelPlayerArmor(1.0F);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}