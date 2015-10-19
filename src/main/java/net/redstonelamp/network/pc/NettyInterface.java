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
package net.redstonelamp.network.pc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.redstonelamp.network.LowLevelNetworkException;
import net.redstonelamp.network.UniversalPacket;
import net.redstonelamp.network.netInterface.AdvancedNetworkInterface;

/**
 * Created by jython234 on 10/18/2015.
 *
 * @author RedstoneLamp Team
 */
public class NettyInterface implements AdvancedNetworkInterface {

    @Override
    public void setName(String name) {

    }

    @Override
    public UniversalPacket readPacket() throws LowLevelNetworkException {
        return null;
    }

    @Override
    public void sendPacket(UniversalPacket packet, boolean immediate) throws LowLevelNetworkException {

    }

    @Override
    public void shutdown() throws LowLevelNetworkException {

    }
}
