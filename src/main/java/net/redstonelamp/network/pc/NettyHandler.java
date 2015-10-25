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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import net.redstonelamp.network.UniversalPacket;
import net.redstonelamp.nio.BinaryBuffer;

import java.nio.ByteOrder;

/**
 * Handler that processes incoming data and encodes outgoing.
 *
 * @author RedstoneLamp Team
 */
@ChannelHandler.Sharable
public class NettyHandler extends ChannelInboundHandlerAdapter {
    @Getter private final NettyInterface nettyInterface;
    private ByteBuf queue;

    public NettyHandler(NettyInterface nettyInterface) {
        this.nettyInterface = nettyInterface;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        queue = ctx.alloc().heapBuffer();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        queue.release();
        queue = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        queue.writeBytes(m);
        m.release();

        int length = checkReadVarInt(ctx, queue);
        if(length != -1) {
            if(queue.array().length >= length) {
                byte[] data = new byte[length];
                queue.readBytes(data);
                UniversalPacket up = new UniversalPacket(data, ByteOrder.BIG_ENDIAN, ctx.channel().remoteAddress());
                System.out.println(up.bb().singleLineHexDump());
                queue.discardReadBytes();
            }
        }
    }

    private int checkReadVarInt(ChannelHandlerContext ctx, ByteBuf buf) {
        ByteBuf bb = ctx.alloc().heapBuffer();
        boolean readCorrect = false;
        while(buf.isReadable()) {
            byte b = buf.readByte();
            if((b & 0xff) >> 7 > 0){ //Check if there is more
                bb.writeByte(b);
            }else{ //no more
                bb.writeByte(b);
                readCorrect = true;
                break;
            }
        }
        byte[] array = bb.array();
        bb.release();
        return readCorrect ? BinaryBuffer.wrapBytes(array, ByteOrder.BIG_ENDIAN).getVarInt() : -1;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        nettyInterface.onClose(ctx.channel().remoteAddress(), "Stream closed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nettyInterface.getLogger().warning("An exception has been caught: "+cause.getMessage());
        nettyInterface.getLogger().trace(cause);

        ctx.close();
        nettyInterface.onClose(ctx.channel().remoteAddress(), "Exception: " + cause.getClass().getName());
    }
}
