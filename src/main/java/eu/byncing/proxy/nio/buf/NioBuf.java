package eu.byncing.proxy.nio.buf;

import java.nio.ByteBuffer;

public class NioBuf extends ByteBuf {

    public static final NioBuf EMPTY = new NioBuf(0, true);

    private ByteBuffer buffer;
    private final boolean direct;

    public NioBuf(ByteBuffer buffer) {
        this.buffer = buffer;
        this.direct = buffer.isDirect();
    }

    public NioBuf(int capacity, boolean direct) {
        this.buffer = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
        this.direct = direct;
    }

    public NioBuf(byte[] bytes) {
        this.buffer = ByteBuffer.wrap(bytes);
        this.direct = false;
    }

    public static int size(int value) {
        if ((value & 0xFFFFFF80) == 0) return 1;
        if ((value & 0xFFFFC000) == 0) return 2;
        if ((value & 0xFFE00000) == 0) return 3;
        if ((value & 0xF0000000) == 0) return 4;
        return 5;
    }

    @Override
    public void enlarge(int capacity) {
        ByteBuffer byteBuffer = direct ? ByteBuffer.allocateDirect((capacity + capacity())) : ByteBuffer.allocate((capacity + capacity()));

        int offset = offset();

        offset(0);

        byteBuffer.put(buffer);

        buffer.clear();
        buffer = byteBuffer;
        buffer.position(offset());
    }

    @Override
    public void writeByte(byte value) {
        enlarge(1);
        buffer.put(value);
    }

    @Override
    public byte readByte() {
        return buffer.get();
    }

    @Override
    public void writeInt(int value) {
        do {
            int part = value & 0x7F;
            value >>>= 7;
            if (value != 0) part |= 0x80;
            writeByte((byte) part);
        } while (value != 0);
    }

    @Override
    public int readInt() {
        int value = 0, bytes = 0;
        byte b;
        do {
            b = readByte();
            value |= (b & 0x7F) << (bytes++ * 7);
            if (bytes > 5) return value;
        } while ((b & 0x80) == 0x80);
        return value;
    }

    @Override
    public void writeBoolean(boolean value) {
        writeByte((byte) (value ? 1 : 0));
    }

    @Override
    public boolean readBoolean() {
        return buffer.get() == 1;
    }

    @Override
    public void writeShort(short value) {
        enlarge(Short.BYTES);
        buffer.putShort(value);
    }

    @Override
    public short readShort() {
        return buffer.getShort();
    }

    @Override
    public void writeLong(long value) {
        enlarge(Long.BYTES);
        buffer.putLong(value);
    }

    @Override
    public long readLong() {
        return buffer.getLong();
    }

    @Override
    public void writeFloat(float value) {
        enlarge(Float.BYTES);
        buffer.putFloat(value);
    }

    @Override
    public float readFloat() {
        return buffer.getFloat();
    }

    @Override
    public void writeDouble(double value) {
        enlarge(Double.BYTES);
        buffer.putDouble(value);
    }

    @Override
    public double readDouble() {
        return buffer.get();
    }

    @Override
    public void writeChar(char value) {
        enlarge(Character.BYTES);
        buffer.putChar(value);
    }

    @Override
    public char readChar() {
        return buffer.getChar();
    }

    @Override
    public void writeBuf(ByteBuf buf) {
        int readable = buf.readable();

        byte[] bytes = new byte[readable];
        buf.readBytes(bytes, 0, readable);

        enlarge(readable);

        buffer.put(bytes);
    }

    @Override
    public void writeBuf(ByteBuffer buf) {
        buf.flip().rewind();
        buffer.put(buf);
    }

    @Override
    public void readBytes(byte[] bytes, int offset, int length) {
        buffer.get(bytes, offset, length);
    }

    @Override
    public void flush() {
        buffer.clear();
        buffer = new NioBuf(0, direct).buffer;
    }

    @Override
    public void reset() {
        buffer.flip().rewind();
    }

    @Override
    public int readable() {
        return buffer.remaining();
    }

    @Override
    public int offset() {
        return buffer.position();
    }

    @Override
    public void offset(int offset) {
        buffer.position(offset);
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public byte[] array() {
        return buffer.array();
    }

    public boolean isDirect() {
        return direct;
    }

    @Override
    public boolean isReadable() {
        return buffer.hasRemaining();
    }

    public ByteBuffer toNio() {
        return buffer;
    }
}