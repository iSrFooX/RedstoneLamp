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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;
import net.redstonelamp.Player;
import net.redstonelamp.Server;
import net.redstonelamp.network.LowLevelNetworkException;
import net.redstonelamp.network.UniversalPacket;
import net.redstonelamp.network.netInterface.AdvancedNetworkInterface;
import net.redstonelamp.ui.ConsoleOut;
import net.redstonelamp.ui.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;

/**
 * Created by jython234 on 10/18/2015.
 *
 * @author RedstoneLamp Team
 */
public class NettyInterface implements AdvancedNetworkInterface {
    @Getter private final Server server;
    @Getter private Logger logger;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;

    public NettyInterface(Server server) {
        this.server = server;
        setupLogger();

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4); //4 NIO Workers

        final NettyHandler handler = new NettyHandler(this);
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new LoggingHandler(LogLevel.INFO));
                        p.addLast(handler);
                    }
                });

        try {
            channelFuture = bootstrap.bind(server.getConfig().getInt("mcpc-port")).sync();
            logger.info("Bound to: 0.0.0.0:"+server.getConfig().getInt("mcpc-port"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void setupLogger() {
        try{
            Constructor c = server.getLogger().getConsoleOutClass().getConstructor(String.class);
            logger = new net.redstonelamp.ui.Logger((ConsoleOut) c.newInstance("NettyInterface"));
            logger.debug("Logger created.");
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

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
        channelFuture.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    protected void onClose(SocketAddress address, String reason) {
        Player player = server.getPlayer(address);
        if(player != null) {
            player.close(player.isSpawned() ? "redstonelamp.translation.player.left" : "", reason, false);
        }
    }
}
