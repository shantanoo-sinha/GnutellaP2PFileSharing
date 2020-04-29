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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import security.RSAEncryption;
import security.RSAPrivateKey;
import security.RSAPublicKey;

class RMIXorOutputStream extends FilterOutputStream {

	/*
	 * The byte used to "encrypt" each byte of data.
	 */
	private RSAPublicKey rsaPublicKey;
//	private RSAPrivateKey rsaPrivateKey;
	private byte[] ibuffer = new byte[1];
	private RSAEncryption rsa;

	/*
	 * Constructs an output stream that uses the specified pattern to "encrypt" each
	 * byte of data.
	 */
	/*public RMIXorOutputStream(OutputStream out, byte pattern) {
		super(out);
		this.pattern = pattern;
	}*/
	public RMIXorOutputStream(OutputStream out, RSAPublicKey publicKey/*, RSAPrivateKey privateKey*/) {
		super(out);
		this.rsaPublicKey = publicKey;
		rsa = new RSAEncryption(this.rsaPublicKey.getModulus(), this.rsaPublicKey.getPublicExponent(), null);
	}

	/*
	 * XOR's the byte being written with the pattern and writes the result.
	 */
	/*public void write(int b) throws IOException {
//		System.out.println("before write:" + b);
		System.out.println("before write:" + ((b ^ pattern) & 0xFF));
//		out.write((b ^ pattern) & 0xFF);
	}*/
	/*
	 * Encrypt the byte being written with the private key and writes the result.
	 */	
	public void write(int paramInt) throws IOException {
		ibuffer[0] = ((byte) paramInt);
		if (ibuffer != null) {
			out.write(rsa.encryptData(ibuffer));
		}
	}

	public void write(byte[] paramArrayOfByte) throws IOException {
		write(paramArrayOfByte, 0, paramArrayOfByte.length);
	}

	public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
		byte[] encryptData = rsa.encryptData(paramArrayOfByte);
		out.write(encryptData);
	}
}
