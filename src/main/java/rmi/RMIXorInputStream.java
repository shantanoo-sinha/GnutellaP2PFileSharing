/*
 * Copyright 2002 Sun Microsystems, Inc. All  Rights Reserved.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * -Redistributions of source code must retain the above copyright  
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright 
 *  notice, this list of conditions and the following disclaimer in 
 *  the documentation and/or other materials provided with the 
 *  distribution.
 *  
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY 
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY 
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF OR 
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR 
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, 
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that Software is not designed, licensed or 
 * intended for use in the design, construction, operation or 
 * maintenance of any nuclear facility. 
 */

package rmi;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import security.RSAEncryption;
import security.RSAPrivateKey;

/**
 * The Class RMIXorInputStream.
 */
class RMIXorInputStream extends FilterInputStream {

	/*
	 * The byte being used to "decrypt" each byte of data.
	 */
/** The rsa private key. */
	//	private RSAPublicKey rsaPublicKey;
	private RSAPrivateKey rsaPrivateKey;
	
	/** The rsa. */
	private RSAEncryption rsa;

	/**
	 * Instantiates a new RMI xor input stream.
	 *
	 * @param in the in
	 * @param privateKey the private key
	 */
	/*
	 * Constructs an input stream that uses the specified pattern to "decrypt" each
	 * byte of data.
	 */
	public RMIXorInputStream(InputStream in, /*RSAPublicKey publicKey, */RSAPrivateKey privateKey) {
		super(in);
		this.rsaPrivateKey = privateKey;
		rsa = new RSAEncryption(this.rsaPrivateKey.getModulus(), null, this.rsaPrivateKey.getPrivateExponent());
	}

	/*
	 * Reads in a byte and xor's the byte with the pattern. Returns the byte.
	 */
	/*public int read() throws IOException {
		int b = in.read();
//		System.out.println("before read:" + b);
		// If not end of file or an error, truncate b to one byte
		if (b != -1)
			b = (b ^ pattern) & 0xFF;

//		System.out.println("after read:" + b);
		return b;
	}*/

	/*
	 * Reads up to len bytes
	 */
	/*public int read(byte b[], int off, int len) throws IOException {
		int numBytes = in.read(b, off, len);
//		System.out.println("before read:" + Arrays.toString(b));
		if (numBytes <= 0)
			return numBytes;

		for (int i = 0; i < numBytes; i++) {
			b[off + i] = (byte) ((b[off + i] ^ pattern) & 0xFF);
		}
//		System.out.println("after read:" + Arrays.toString(b));
		return numBytes;
	}*/
	
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
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

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	/*
	 * Reads up to len bytes and decrypt the byte with the private key.
	 * Returns the byte.
	 */
	public int read(byte b[], int off, int len) throws IOException {
		int numBytes = in.read(b, off, len);

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
}
