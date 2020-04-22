package rmi;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import security.RSA4;
import security.RSAPrivateKey;

class RMISecurityInputStream extends FilterInputStream {
  
	/*
	 * The privateKey used to "decrypt" each byte of data.
	 */
	private final RSAPrivateKey privateKey;
	private final RSA4 rsa;
	private InputStream input;
	/*private byte[] ibuffer = new byte[512];
	private boolean done = false;
	private byte[] obuffer;
	private int ostart = 0;
	private int ofinish = 0;
	private boolean closed = false;*/

	/*
     * Constructs an input stream that uses the privateKey
     * to "decrypt" each byte of data.
     */
    public RMISecurityInputStream(InputStream in, RSAPrivateKey privateKey) {
        super(in);
        input = in;
		this.privateKey = privateKey;
		rsa = new RSA4(this.privateKey.getModulus(), null, this.privateKey.getPrivateExponent());
    }
  
    /*
     * Reads in a byte and decrypt the byte with the private key.
     * Returns the byte.
     */
	public int read() throws IOException {
		int b = in.read();
		// If not end of file or an error, truncate b to one byte
		if (b != -1) {
			byte[] by = new byte[1];
			by[0] = (byte)b;
			try {
				by = rsa.decryptData(by);
				return by[0];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return b;
	}

	/*
	 * Reads up to len bytes and decrypt the byte with the private key.
	 * Returns the byte.
	 */
	public int read(byte b[], int off, int len) throws IOException {
		int numBytes = input.read(b, off, len);

		if (numBytes <= 0)
			return numBytes;
		
		try {
			b = rsa.decryptData(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return numBytes;
	}
    
    /*private int getMoreData() throws IOException {
		if (done) {
			return -1;
		}
		int i = input.read(ibuffer);
		if (i == -1) {
			done = true;
			try {
				obuffer = rsa.decryptData(ibuffer);
			} catch (Exception e) {
				obuffer = null;
			}
			if (obuffer == null) {
				return -1;
			}
			ostart = 0;
			ofinish = obuffer.length;
			return ofinish;
		}
//		try {
//			obuffer = cipher.update(ibuffer, 0, i);
//		} catch (IllegalStateException localIllegalStateException) {
//			obuffer = null;
//			throw localIllegalStateException;
//		}
		ostart = 0;
		if (obuffer == null) {
			ofinish = 0;
		} else {
			ofinish = obuffer.length;
		}
		return ofinish;
	}

	public int read() throws IOException {
		if (ostart >= ofinish) {
			int i = 0;
			while (i == 0) {
				i = getMoreData();
			}
			if (i == -1) {
				return -1;
			}
		}
		return obuffer[(ostart++)] & 0xFF;
	}

	public int read(byte[] paramArrayOfByte) throws IOException {
		return read(paramArrayOfByte, 0, paramArrayOfByte.length);
	}

	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
		if (ostart >= ofinish) {
			int i = 0;
			while (i == 0) {
				i = getMoreData();
			}
			if (i == -1) {
				return -1;
			}
		}
		if (paramInt2 <= 0) {
			return 0;
		}
		int i = ofinish - ostart;
		if (paramInt2 < i) {
			i = paramInt2;
		}
		if (paramArrayOfByte != null) {
			System.arraycopy(obuffer, ostart, paramArrayOfByte, paramInt1, i);
		}
		ostart += i;
		return i;
	}

	public long skip(long paramLong) throws IOException {
		int i = ofinish - ostart;
		if (paramLong > i) {
			paramLong = i;
		}
		if (paramLong < 0L) {
			return 0L;
		}
		ostart = ((int) (ostart + paramLong));
		return paramLong;
	}

	public int available() throws IOException {
		return ofinish - ostart;
	}

	public void close() throws IOException {
		if (closed) {
			return;
		}
		closed = true;
		input.close();
		if (!done) {
//			try {
//				cipher.doFinal();
//			} catch (BadPaddingException | IllegalBlockSizeException localBadPaddingException) {
//			}
		}
		ostart = 0;
		ofinish = 0;
	}

	public boolean markSupported() {
		return false;
	}*/
}
