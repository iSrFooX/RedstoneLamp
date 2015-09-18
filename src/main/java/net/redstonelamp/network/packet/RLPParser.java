package net.redstonelamp.network.packet;

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

import net.redstonelamp.request.Request;
import net.redstonelamp.response.Response;

import java.io.*;
import java.util.*;

public class RLPParser implements Closeable{
    private BufferedReader reader;
    private String sourcePath;
    private String name = null, description = "(no description)",
            defaultRequestPackage = "net.redstonelamp.request", defaultResponsePackage = "net.redstonelamp.response";
    private Map<Byte, Packet> declaredPackets = new HashMap<>();

    private Packet currentlyDeclaringPacket = null;
    private int currentLine = 0;
    private int incId = 0;

    public RLPParser(File file) throws IOException{
        this(new FileInputStream(file), file.getCanonicalPath());
    }
    public RLPParser(InputStream is, String sourcePath){
        this(new InputStreamReader(is), sourcePath);
    }
    public RLPParser(Reader in, String sourcePath){
        this.sourcePath = sourcePath;
        reader = new BufferedReader(in);
    }

    public void parse() throws IOException, RLPFormatException{
        String line;
        String prefix = "";
        while((line = readLine()) != null){
            line = line.trim();
            if(line.charAt(0) == '#'){
                continue;
            }
            if(line.charAt(line.length() - 1) == '\\'){
                prefix += line + "\n"; // or System.lineSeparator()?
                continue;
            }
            line = prefix + line;
            prefix = "";
            String[] words = line.split(" ");
            List<String> args = new ArrayList<>(words.length);
            for(String word : words){
                if(!word.trim().isEmpty()){
                    args.add(word);
                }
            }
            try{
                handleCommand(args);
            }catch(RuntimeException e){
                throw error(e);
            }
        }
        currentLine = 0;
        if(name == null){
            throw error("ProtocolName declaration is missing");
        }
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Map<Byte, Packet> getDeclaredPackets(){
        return declaredPackets;
    }

    public String getSourcePath(){
        return sourcePath;
    }

    public int getCurrentLine(){
        return currentLine;
    }

    @Override
    public void close() throws IOException{
        reader.close();
    }

    @SuppressWarnings("unchecked")
    private void handleCommand(List<String> args) throws RLPFormatException{
        String cmd = args.remove(0);
        if(cmd.equalsIgnoreCase("ProtocolName")){
            name = implode(" ", args);
        }else if(cmd.equalsIgnoreCase("ProtocolDescription")){
            description = implode(" ", args);
        }else if(cmd.equalsIgnoreCase("DeclarePacket")){
            if(currentlyDeclaringPacket != null){
                throw error("Attempt to declare packet while older packet not commited");
            }
            currentlyDeclaringPacket = new Packet();
            if(args.isEmpty()){
                throw error("Incorrect syntax. Correct syntax: DeclarePacket <hexadecimal packet ID>");
            }
            currentlyDeclaringPacket.pid = Byte.parseByte(args.get(0), 16);
        }else if(cmd.equalsIgnoreCase("PacketName")){
            if(currentlyDeclaringPacket == null){
                throw error("Attempt to declare packet name while not declaring a packet");
            }
            currentlyDeclaringPacket.name = args.get(0);
        }else if(cmd.equalsIgnoreCase("PacketField")){
            if(currentlyDeclaringPacket == null){
                throw error("Attempt to declare packet name while not declaring a packet");
            }
            PacketField field = new PacketField();
            if(args.size() < 2){
                throw error("Incorrect syntax. Correct syntax: PacketField <type> [-unsigned] <field name> [comments ...]");
            }
            boolean isUnsigned = Objects.equals(args.get(1), "-unsigned");
            if(isUnsigned){
                args.remove(1);
            }
            if(args.size() < 2){
                throw error("Incorrect syntax. Correct syntax: PacketField <type> [-unsigned] <field name> [comments ...]");
            }
            String type = args.get(0);
            if(type.equalsIgnoreCase("skip")){
                throw error("Incorrect syntax. Correct syntax: PacketField SKIP <length>");
                field.type = new PacketFieldType.SkipField(Integer.parseInt(args.get(1)));
                field.name = "skipped field #" + incId++;
            }else{
                field.type = PacketFieldType.fromString(type, isUnsigned);
                if(field.type == null){
                    throw error("Unknown type " + args.get(0));
                }
                field.name = args.get(1);
            }
            currentlyDeclaringPacket.fields.add(field);
        }else if(cmd.equalsIgnoreCase("CommitPacket")){
            declaredPackets.put(currentlyDeclaringPacket.pid, currentlyDeclaringPacket);
            currentlyDeclaringPacket = null;
        }else if(cmd.equalsIgnoreCase("AssocRequest")){
            if(args.isEmpty()){
                throw error("Incorrect syntax. Correct syntax: AssocRequest <class name>");
            }
            String name = args.get(0);
            Class<? extends Request> req;
            try{
                req = Class.forName(defaultRequestPackage + "." + name).asSubclass(Request.class);
            }catch(ClassNotFoundException e){
                try{
                    req = Class.forName(name).asSubclass(Request.class);
                }catch(ClassNotFoundException e1){
                    throw error("Unknown class " + name);
                }
            }
            currentlyDeclaringPacket.requestClass = req;
        }else if(cmd.equalsIgnoreCase("AssocResponse")){
            if(args.isEmpty()){
                throw error("Incorrect syntax. Correct syntax: AssocResponse <class name>");
            }
            String name = args.get(0);
            Class<? extends Response> req;
            try{
                req = Class.forName(defaultResponsePackage + "." + name).asSubclass(Response.class);
            }catch(ClassNotFoundException e){
                try{
                    req = Class.forName(name).asSubclass(Response.class);
                }catch(ClassNotFoundException e1){
                    throw error("Unknown class " + name);
                }
            }
            currentlyDeclaringPacket.responseClass = req;
        }
    }
    private String implode(String glue, List<String> list){
        StringBuilder builder = new StringBuilder();
        for(String word : list){
            builder.append(word).append(glue);
        }
        return builder.substring(0, builder.length() - glue.length());
    }
    private String readLine() throws IOException{
        currentLine++;
        return reader.readLine();
    }

    private RLPFormatException error(String msg){
        return new RLPFormatException(msg + " on line " + currentLine + " at " + sourcePath);
    }
    private RLPFormatException error(Throwable t){
        return new RLPFormatException("Exception caught when executing line " + currentLine + " at " + sourcePath, t);
    }

    private class Packet{
        public byte pid;
        public String name;
        public Class<? extends Request> requestClass;
        public Class<? extends Response> responseClass;
        public List<PacketField> fields;
    }

    private class PacketField{
        public String name;
        public PacketFieldType type;
    }
}
