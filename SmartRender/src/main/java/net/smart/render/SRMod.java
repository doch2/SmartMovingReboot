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

package net.smart.render;

import api.player.model.IModelPlayerAPI;
import api.player.model.ModelPlayerAPI;
import api.player.render.RenderPlayerAPI;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.smart.render.model.SRModelPlayerBase;
import net.smart.render.render.SRRenderPlayerBase;
import net.smart.render.statistics.SmartStatisticsContext;
import net.smart.render.statistics.playerapi.SmartStatistics;
import net.smart.render.statistics.playerapi.SmartStatisticsFactory;

@Mod(modid = SRMod.ID, name = SRMod.NAME, version = SRMod.VERSION, useMetadata = true, clientSideOnly = true)
public class SRMod {
	static final String ID = "smartrender";
	static final String NAME = "Smart Render";
	static final String VERSION = "@VERSION@";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		RenderPlayerAPI.register(SRInfo.ModName, SRRenderPlayerBase.class);
		ModelPlayerAPI.register(SRInfo.ModName, SRModelPlayerBase.class);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		SmartStatistics.register();
		SmartStatisticsFactory.initialize();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tickStart(ClientTickEvent event) {
		SmartStatisticsContext.onTickInGame();
	}
}
