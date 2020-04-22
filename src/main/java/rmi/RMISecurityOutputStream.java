package rmi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import security.RSA4;
import security.RSAEncryption;
import security.RSAPublicKey;

class RMISecurityOutputStream extends FilterOutputStream {

	/*
	 * The publicKey used to "encrypt" each byte of data.
	 */
	private final RSAPublicKey publicKey;
	private final RSA4 rsa;
	private OutputStream output;
	private byte[] ibuffer = new byte[1];
	/*private byte[] obuffer;
	private boolean closed = false;*/

	/*
	 * Constructs an output stream that uses the publicKey to "encrypt" each
	 * byte of data.
	 */
	public RMISecurityOutputStream(OutputStream out, RSAPublicKey publicKey) {
		super(out);
		output = out;
		this.publicKey = publicKey;
		rsa = new RSA4(this.publicKey.getModulus(), this.publicKey.getPublicExponent(), null);
	}

	/*
	 * Encrypt the byte being written with the private key and writes the result.
	 */	
	public void write(int paramInt) throws IOException {
		ibuffer[0] = ((byte) paramInt);
		if (ibuffer != null) {
			output.write(rsa.encryptData(ibuffer));
		}
	}

	public void write(byte[] paramArrayOfByte) throws IOException {
		write(paramArrayOfByte, 0, paramArrayOfByte.length);
	}

	public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
		byte[] encryptData = rsa.encryptData(paramArrayOfByte);
		output.write(encryptData);
	}
	
	/*public void write(int b) throws IOException {
        ibuffer[0] = (byte) b;
        obuffer = new byte[1];
        if (obuffer != null) {
            output.write(obuffer);
            obuffer = null;
        }
    };

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        obuffer = cipher.update(b, off, len);
        if (obuffer != null) {
            output.write(obuffer);
            obuffer = null;
        }
    }

    public void flush() throws IOException {
        if (obuffer != null) {
            output.write(obuffer);
            obuffer = null;
        }
        output.flush();
    }

    public void close() throws IOException {
        if (closed) {
            return;
        }

        closed = true;
        try {
            obuffer = cipher.doFinal();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            obuffer = null;
        }
        try {
            flush();
        } catch (IOException ignored) {}
        out.close();
    }*/
}
