// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import api.player.forge.PlayerAPIPlugin;
import api.player.model.ModelPlayerAPI;
import api.player.model.ModelPlayerBaseSorting;
import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBaseSorting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.smart.moving.config.SMConfig;
import net.smart.moving.config.SMOptions;
import net.smart.moving.model.SMModelPlayerBase;
import net.smart.moving.render.SMRenderPlayerBase;
import net.smart.render.SRInfo;
import net.smart.utilities.Reflect;

@Mod(modid = SMMod.ID, name = SMMod.NAME, version = SMMod.VERSION, useMetadata = true)
public class SMMod {
	final static String ID = "smartmoving";
	final static String NAME = "Smart Moving";
	final static String VERSION = "@VERSION@";

	protected static String ModComVersion = "2.4";

	private final boolean isClient;

	private boolean hasRenderer = false;

	public SMMod() {
		isClient = FMLCommonHandler.instance().getSide().isClient();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (isClient)
			register();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		SMPacketHandler.registerPackets();
		
		if (isClient) {
			SMPlayerBase.registerPlayerBase();
			SMServerPlayerBase.registerPlayerBase();
			SMServerComm.localUserNameProvider = new SMLocalUserNameProvider();
			MinecraftForge.EVENT_BUS.register(this);
			SMFactory.initialize();
			checkForMods();
			SMContext.initialize();
		} else
			SMServer.initialize(new File("."),
					FMLCommonHandler.instance().getMinecraftServerInstance().getGameType().getID(), new SMConfig());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (!isClient)
			SMServerPlayerBase.registerPlayerBase();
	}

	@SubscribeEvent
	public void tickStart(ClientTickEvent event) {
		SMContext.onTickInGame();
	}

	private void register() {
		String[] inferiors = new String[] { SRInfo.ModName };

		RenderPlayerBaseSorting renderSorting = new RenderPlayerBaseSorting();
		renderSorting.setAfterLocalConstructingInferiors(inferiors);
		renderSorting.setOverrideDoRenderInferiors(inferiors);
		renderSorting.setOverrideRotateCorpseInferiors(inferiors);
		renderSorting.setOverrideRenderLivingAtInferiors(inferiors);
		RenderPlayerAPI.register(NAME, SMRenderPlayerBase.class, renderSorting);

		ModelPlayerBaseSorting modelSorting = new ModelPlayerBaseSorting();
		modelSorting.setAfterLocalConstructingInferiors(inferiors);
		ModelPlayerAPI.register(NAME, SMModelPlayerBase.class, modelSorting);
	}

	private void checkForMods() {
		List<ModContainer> modList = Loader.instance().getActiveModList();
		boolean hasRedPowerWiring = false;
		boolean hasBuildCraftTransport = false;
		boolean hasFiniteLiquid = false;
		boolean hasBetterThanWolves = false;
		boolean hasSinglePlayerCommands = false;
		boolean hasRopesPlus = false;
		boolean hasASGrapplingHook = false;
		boolean hasBetterMisc = false;

		for (int i = 0; i < modList.size(); i++) {
			ModContainer mod = modList.get(i);
			String name = mod.getName();

			if (name.contains("RedPowerWiring"))
				hasRedPowerWiring = true;
			else if (name.contains("BuildCraftTransport"))
				hasBuildCraftTransport = true;
			else if (name.contains("Liquid"))
				hasFiniteLiquid = true;
			else if (name.contains("FCBetterThanWolves"))
				hasBetterThanWolves = true;
			else if (name.contains("SinglePlayerCommands"))
				hasSinglePlayerCommands = true;
			else if (name.contains("ASGrapplingHook"))
				hasASGrapplingHook = true;
			else if (name.contains("BetterMisc"))
				hasBetterMisc = true;
		}

		hasRopesPlus = Reflect.CheckClasses(SMMod.class, SMInstall.RopesPlusCore);

		SMOptions.initialize(hasRedPowerWiring, hasBuildCraftTransport, hasFiniteLiquid, hasBetterThanWolves,
				hasSinglePlayerCommands, hasRopesPlus, hasASGrapplingHook, hasBetterMisc);
	}
}
