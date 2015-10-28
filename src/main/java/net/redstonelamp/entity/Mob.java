/*
 * This file is part of RedstoneLamp.
 *
 * RedstoneLamp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RedstoneLamp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RedstoneLamp.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.redstonelamp.entity;

import net.redstonelamp.Player;
import net.redstonelamp.entity.ai.AI;
import net.redstonelamp.entity.ai.SimpleMobAI;
import net.redstonelamp.level.position.Position;
import net.redstonelamp.response.AddMobResponse;

/**
 * An entity subclass that represents a Minecraft mob, with an AI.
 *
 * @author RedstoneLamp Team
 */
public abstract class Mob extends Entity {
    private AI ai;

    public Mob(EntityManager manager, Position position) {
        super(manager, position);
        initEntity();
    }

    @Override
    protected void initEntity() {
        ai = new SimpleMobAI(this, getViewRange(), getPosition());
        super.initEntity();
    }

    @Override
    public void doTick(long tick) {
        super.doTick(tick);
    }

    @Override
    public void spawnTo(Player player) {
        player.sendResponse(new AddMobResponse(this));
    }

    public abstract int getViewRange();

    public AI getAi() {
        return ai;
    }
}
