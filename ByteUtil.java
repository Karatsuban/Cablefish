import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;


final class ByteUtil
{

	public byte[] data = null;
	public int offset;

	public ByteUtil(byte[] data){
		this.data = data;
		this.offset = 0;
	}



	// Conversion methods
	
    public int toInt(){
        // convert by to int
        return ByteBuffer.wrap(this.data).getInt();
    }

    public short toShort(){
        // converts b to short
        return ByteBuffer.wrap(this.data).getShort();
    }


	// Comparison methods

    public boolean equals(CharSequence b){
        // compare a byte array and a charsequence representing bytes
        return this.equals(HexFormat.of().parseHex(b));
    }

    public boolean equals( byte[] b){
        // compare two byte arrays
        return Arrays.equals(this.data, b);
    }


	// Reading methods

    public void skipBytes(int len){
        // move offset to skip bytes
        this.offset += len;
    }

    public ByteUtil readBytes(int len){
        // read len bytes from this.data
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.offset+len);
        this.offset += len;
        return new ByteUtil(b);
    }

    public ByteUtil getRemainingBytes(){
        // read the remaining bytes from this.data
        byte[] b = Arrays.copyOfRange(this.data, this.offset, this.data.length);
        return new ByteUtil(b);
    }


	// ToString method

	public String toString(){
        String out = "";
        for (int i=0; i<this.data.length; i++){
            out += String.format("%02x ", this.data[i]);
        }
        return out;
	}	

}
