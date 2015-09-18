package net.redstonelamp.network.packet;

import net.redstonelamp.nio.BinaryBuffer;

import java.net.InetSocketAddress;
import java.util.UUID;

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
public abstract class PacketFieldType<T>{
    public abstract T read(BinaryBuffer bb);
    public abstract void write(BinaryBuffer bb, T value);
    public static PacketFieldType fromString(String typeName){
        return fromString(typeName, false);
    }
    public static PacketFieldType fromString(String typeName, boolean unsigned){
        typeName = typeName.toLowerCase();
        switch(typeName){
            case "string":
                return new StringField();
            case "byte":
                return new ByteField(!unsigned);
            case "short":
                return new ShortField(!unsigned);
            case "triad":
            case "be_triad":
            case "big_endian_triad":
                return new BigEndianTriadField();
            case "l_triad":
            case "le_triad":
            case "little_endian_triad":
                return new LittleEndianTriadField();
            case "int":
            case "integer":
                return new IntField();
            case "long":
                return new LongField();
            case "varint":
            case "var_int":
                return new VarIntField();
            case "float":
                return new FloatField();
            case "double":
                return new DoubleField();
            case "uuid":
                return new UUIDField();
            case "boolean":
                return new BooleanField();
            case "address":
                return new AddressField();
            case "metadata":
                // TODO implement
                return null;
        }
        // TODO add literal support
        return null;
    }

    public static class ByteField extends PacketFieldType<Short>{
        private boolean signed;
        public ByteField(boolean signed){
            this.signed = signed;
        }
        @Override
        public Short read(BinaryBuffer bb){
            return signed ? bb.getByte() : bb.getUnsignedByte();
        }
        @Override
        public void write(BinaryBuffer bb, Short value){
            bb.putByte((byte) (value & 0xFF));
        }
    }

    public static class ShortField extends PacketFieldType<Integer>{
        private boolean signed;
        public ShortField(boolean signed){
            this.signed = signed;
        }
        @Override
        public Integer read(BinaryBuffer bb){
            return signed ? bb.getShort() : bb.getUnsignedShort();
        }
        @Override
        public void write(BinaryBuffer bb, Integer value){
            bb.putShort((short) (int) value);
        }
    }

    @SuppressWarnings("UnnecessaryParentheses")
    public static class BigEndianTriadField extends PacketFieldType<Integer>{
        @Override
        public Integer read(BinaryBuffer bb){
            return (bb.getByte() << 16) | (bb.getByte() << 8) | bb.getByte();
        }
        @Override
        public void write(BinaryBuffer bb, Integer value){
            bb.putByte((byte) (value >> 16));
            bb.putByte((byte) ((value >> 8) & 0xFF));
            bb.putByte((byte) (value & 0xFF));
        }
    }

    @SuppressWarnings("UnnecessaryParentheses")
    public static class LittleEndianTriadField extends PacketFieldType<Integer>{
        @Override
        public Integer read(BinaryBuffer bb){
            return bb.getByte() | (bb.getByte() << 8) | (bb.getByte() << 16);
        }
        @Override
        public void write(BinaryBuffer bb, Integer value){
            bb.putByte((byte) (value & 0xFF));
            bb.putByte((byte) ((value >> 8) & 0xFF));
            bb.putByte((byte) (value >> 16));
        }
    }

    public static class IntField extends PacketFieldType<Integer>{
        @Override
        public Integer read(BinaryBuffer bb){
            return bb.getInt();
        }
        @Override
        public void write(BinaryBuffer bb, Integer value){
            bb.putInt(value);
        }
    }

    public static class LongField extends PacketFieldType<Long>{
        @Override
        public Long read(BinaryBuffer bb){
            return bb.getLong();
        }
        @Override
        public void write(BinaryBuffer bb, Long value){
            bb.putLong(value);
        }
    }

    public static class VarIntField extends PacketFieldType<Integer>{
        @Override
        public Integer read(BinaryBuffer bb){
            return bb.getVarInt();
        }
        @Override
        public void write(BinaryBuffer bb, Integer value){
            bb.putVarInt(value);
        }
    }

    public static class StringField extends PacketFieldType<String>{
        @Override
        public String read(BinaryBuffer bb){
            return bb.getString();
        }
        @Override
        public void write(BinaryBuffer bb, String value){
            bb.putString(value);
        }
    }

    public static class VarStringField extends PacketFieldType<String>{
        @Override
        public String read(BinaryBuffer bb){
            return bb.getVarString();
        }
        @Override
        public void write(BinaryBuffer bb, String value){
            bb.putVarString(value);
        }
    }

    public static class FloatField extends PacketFieldType<Float>{
        @Override
        public Float read(BinaryBuffer bb){
            return bb.getFloat();
        }
        @Override
        public void write(BinaryBuffer bb, Float value){
            bb.putFloat(value);
        }
    }

    public static class DoubleField extends PacketFieldType<Double>{
        @Override
        public Double read(BinaryBuffer bb){
            return bb.getDouble();
        }
        @Override
        public void write(BinaryBuffer bb, Double value){
            bb.putDouble(value);
        }
    }

    public static class UUIDField extends PacketFieldType<UUID>{
        @Override
        public UUID read(BinaryBuffer bb){
            return bb.getUUID();
        }
        @Override
        public void write(BinaryBuffer bb, UUID value){
            bb.putUUID(value);
        }
    }

    public static class BooleanField extends PacketFieldType<Boolean>{
        @Override
        public Boolean read(BinaryBuffer bb){
            return bb.getBoolean();
        }
        @Override
        public void write(BinaryBuffer bb, Boolean value){
            bb.putBoolean(value);
        }
    }

    public static class AddressField extends PacketFieldType<InetSocketAddress>{
        @Override
        public InetSocketAddress read(BinaryBuffer bb){
            boolean ipv4 = bb.getByte() == 4;
            if(ipv4){
                String address = (~bb.getByte() & 0xFF) + "." + (~bb.getByte() & 0xFF) + "." + (~bb.getByte() & 0xFF) + "." + (~bb.getByte() & 0xFF);
                short port = bb.getShort();
                return new InetSocketAddress(address, port);
            }else{
                return null; // documentation does not exist
            }
        }
        @Override
        public void write(BinaryBuffer bb, InetSocketAddress value){
            String name = value.getHostName();
            if(!name.matches("^([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.\n" +
                    "([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])\\\\.([01]?\\\\d\\\\d?|2[0-4]\\\\d|25[0-5])$")){
                return;
            }
            String[] nums = name.split("\\.");
            bb.putByte((byte) 4);
            for(String nm : nums){
                byte bite = Byte.decode(nm);
                bb.putByte(bite);
            }
            bb.putShort((short) value.getPort());
        }
    }

    public static class SkipField extends PacketFieldType<Void>{
        private int length;
        public SkipField(int length){
            this.length = length;
        }
        @Override
        public Void read(BinaryBuffer bb){
            bb.skip(length);
            return null;
        }
        @Override
        public void write(BinaryBuffer bb, Void value){
            bb.put(new byte[length]);
        }
    }
}
