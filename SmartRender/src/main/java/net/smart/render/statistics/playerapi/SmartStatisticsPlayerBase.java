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

package net.smart.render.statistics.playerapi;

import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import net.smart.render.statistics.IEntityPlayerSP;

public class SmartStatisticsPlayerBase extends ClientPlayerBase implements IEntityPlayerSP {
	public SmartStatisticsPlayerBase(ClientPlayerAPI playerApi) {
		super(playerApi);
		statistics = new net.smart.render.statistics.SmartStatistics(player);
	}

	@Override
	public void afterMoveEntityWithHeading(float f, float f1, float f2) {
		statistics.calculateAllStats(false);
	}

	@Override
	public void afterUpdateRidden() {
		statistics.calculateRiddenStats();
	}

	@Override
	public net.smart.render.statistics.SmartStatistics getStatistics() {
		return statistics;
	}

	public net.smart.render.statistics.SmartStatistics statistics;
}
