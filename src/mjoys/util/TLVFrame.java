package mjoys.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TLVFrame {
    private int type;
    private int length;
    private byte[] value;
    
    public final static int HeadLength = 4;
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }
    
    public static final List<TLVFrame> parseTLVFrames(byte[] buffer, int offset, int length) {
        List<TLVFrame> frames = new ArrayList<TLVFrame>();
        int remainLength = length;
        ByteBuffer bf = ByteBuffer.wrap(buffer, offset, length);
        
        while (remainLength > HeadLength) {
            int type = bf.getShort();
            int valueLength = bf.getShort();
            remainLength -= HeadLength;
            
            if (remainLength >= valueLength) {
                TLVFrame frame = new TLVFrame();
                frame.setType(type);
                frame.setLength(valueLength);
                frame.setValue(new byte[valueLength]);
                System.arraycopy(buffer, HeadLength, frame.getValue(), 0, valueLength);
                
                frames.add(frame);
                remainLength -= valueLength;
            } else {
                break;
            }
        }
        
        return frames;
    }
    
    public static final List<TLVFrame> parseTLVFrames(byte[] buffer, int length) {
        return parseTLVFrames(buffer, 0, length);
    }
    
    public static final List<TLVFrame> parseTLVFrames(byte[] buffer) {
        return parseTLVFrames(buffer, 0, buffer.length);
    }
    
    public static final TLVFrame parseTLVFrame(byte[] buffer, int offset, int length) {
        List<TLVFrame> frames = parseTLVFrames(buffer, offset, length);
        if (frames.size() != 1) {
            return null;
        }
        return frames.get(0);
    }
    
    public static final TLVFrame parseTLVFrame(byte[] buffer, int length) {
        return parseTLVFrame(buffer, 0, length);
    }
    
    public static final TLVFrame parseTLVFrame(byte[] buffer) {
        return parseTLVFrame(buffer, 0, buffer.length);
    }
}