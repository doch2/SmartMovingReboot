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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.smart.moving.config.SMClientConfig;
import net.smart.moving.config.SMOptions;
import net.smart.render.statistics.SmartStatisticsContext;

public abstract class SMContext {
	public static final SMClient Client = new SMClient();
	public static final SMOptions Options = new SMOptions();
	public static SMClientConfig Config = Options;

	private static MinecraftServer lastMinecraftServer = null;

	private static boolean initialized;

	public static void onTickInGame() {
		Minecraft minecraft = Minecraft.getMinecraft();

		if (minecraft.world != null && minecraft.world.isRemote)
			SMFactory.handleMultiPlayerTick(minecraft);

		Options.initializeForGameIfNeccessary();

		initializeServerIfNecessary();
	}

	public static void initialize() {
		if (!initialized)
			SmartStatisticsContext.setCalculateHorizontalStats(true);
		else
			return;

		ClientRegistry.registerKeyBinding(Options.keyBindGrab);
		ClientRegistry.registerKeyBinding(Options.keyBindConfigToggle);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedIncrease);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedDecrease);

		initialized = true;
	}

	public static void initializeServerIfNecessary() {
		MinecraftServer currentMinecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (currentMinecraftServer != null && currentMinecraftServer != lastMinecraftServer) {
			int gameTypeID = currentMinecraftServer.getGameType().getID();
			SMServer.initialize(SMOptions.optionsPath, gameTypeID, Options);
		}
		lastMinecraftServer = currentMinecraftServer;
	}

	public static IBlockState getState(World world, BlockPos blockPos) {
		return world.getBlockState(blockPos);
	}

	public static IBlockState getState(World world, int x, int y, int z) {
		return world.getBlockState(new BlockPos(x, y, z));
	}
}
