import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;


final class ByteUtil
{

	public byte[] data = null;
	public int offset;
	public int length;

	public ByteUtil(byte[] data){
		this.data = data;
		this.offset = 0;
		this.length = this.data.length;
	}

	public ByteUtil(byte data){
		this(new byte[]{data});
	}

	// Conversion methods

	public long toLong(){
		// takes 8 bytes, convert them to long
		long out = 0;
		if (this.length < 8)
			out = ByteBuffer.wrap(this.padToLength(8)).getLong();
		else
			out = ByteBuffer.wrap(this.data).getLong();
		return out;
	}


    public int toInt(){
        // takes 4 bytes, convert them to int
		
		int out = 0;
		if (this.length < 4)
			out = ByteBuffer.wrap(this.padToLength(4)).getInt();
		else
	        out = ByteBuffer.wrap(this.data).getInt();
		return out;
    }

    public short toShort(){
        // takes 2 bytes, converts them to short
		short out = 0;
		if (this.length < 2)
			out = ByteBuffer.wrap(this.padToLength(2)).getShort();
        else
			out = ByteBuffer.wrap(this.data).getShort();
		return out;
    }

	public byte toByte(){
		// converts b to byte
        return this.data[0];
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


	// Getters

	public byte[] getData(){
		return this.data;
	}

	public int getBit(int n){
		// return the nth bit from the first byte currently being read
		int out = 0;
		if (n>=8 || n < 0){
			out = -1;
		}else{
			out = (this.data[this.offset] >> n) & 1;
		}
		return out;
	}

	// ToString method

	public String toString(){
        String out = "";
        for (int i=0; i<this.data.length; i++){
            out += String.format("%02x ", this.data[i]);
        }
        return out;
	}

	public String asIPv4Addr(){
		String out = "";
		if (this.data.length < 4){
			out += " ";
		}else{
			for (int i=0; i<4; i++){
				out += this.data[i] & 0xF; // from byte to int
				if (i != 3) out += ".";
			}
		}
		return out;
	}
	

	// Helper functions

	private byte[] padToLength(int length){
		// pad this.data up to lenght by putting 0 bytes BEFORE this.data
		if (this.length >= length)
			return this.data; // already long enough

		int pad_length = length-this.length;
		byte[] paddArr = new byte[pad_length];
		Arrays.fill(paddArr, (byte) 0);

		byte[] both = Arrays.copyOf(paddArr, paddArr.length + this.length);
		System.arraycopy(this.data, 0, both, paddArr.length, this.length);

		return both;
	}

}
