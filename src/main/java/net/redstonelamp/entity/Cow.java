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

import net.redstonelamp.level.position.Position;

/**
 * A passive Animal implementation of a Cow.
 *
 * @author RedstoneLamp Team
 */
public class Cow extends Animal {
    public float width = 1.6f;
    public float height = 1.12f;

    public Cow(EntityManager manager, Position position) {
        super(manager, position);
    }

    @Override
    protected void initEntity() {
        setMaxHealth(10);
        setHealth(10);
        super.initEntity();
    }


}
