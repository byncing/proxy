package eu.byncing.proxy.nio.buf;

import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ByteBuf implements Flushable {

    public abstract void enlarge(int capacity);

    public abstract void writeByte(byte value);

    public abstract byte readByte();

    public abstract void writeInt(int value);

    public abstract int readInt();

    public abstract void writeBoolean(boolean value);

    public abstract boolean readBoolean();

    public abstract void writeShort(short value);

    public abstract short readShort();

    public abstract void writeLong(long value);

    public abstract long readLong();

    public abstract void writeFloat(float value);

    public abstract float readFloat();

    public abstract void writeDouble(double value);

    public abstract double readDouble();

    public abstract void writeChar(char value);

    public abstract char readChar();

    public abstract void writeString(String value);

    public abstract String readString();

    public abstract void writeBuf(ByteBuf buf);

    public abstract void writeBuf(ByteBuffer buf);

    public abstract void readBytes(byte[] bytes, int offset, int length);

    @Override
    public abstract void flush() throws IOException;

    public abstract void reset();

    public abstract int readable();

    public abstract int offset();

    public abstract void offset(int offset);

    public abstract int capacity();

    public abstract byte[] array();

    public abstract boolean isReadable();
}