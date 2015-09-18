package net.redstonelamp.network;

import jdk.nashorn.api.scripting.URLReader;
import net.redstonelamp.Player;
import net.redstonelamp.network.packet.RLPFormatException;
import net.redstonelamp.network.packet.RLPParser;
import net.redstonelamp.request.Request;
import net.redstonelamp.response.Response;

import java.io.IOException;

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
public abstract class RLPBasedProtocol extends Protocol{
    private RLPParser parser;
    public RLPBasedProtocol(NetworkManager mgr, String name) throws IOException, RLPFormatException{
        super(mgr);
        parser = new RLPParser(new URLReader(getClass().getResource(name)), "//resources/" + name);
        parser.parse();
    }
    @Override
    public String getName(){
        return parser.getName();
    }
    @Override
    public String getDescription(){
        return parser.getDescription();
    }
    @Override
    public Request[] handlePacket(UniversalPacket packet){
        return new Request[0]; // TODO
    }
    @Override
    protected UniversalPacket[] _sendResponse(Response response, Player player){
        return new UniversalPacket[0]; // TODO
    }
    @Override
    protected UniversalPacket[] _sendQueuedResponses(Response[] responses, Player player){
        return new UniversalPacket[0]; // TODO
    }
}
