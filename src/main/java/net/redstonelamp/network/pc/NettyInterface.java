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
