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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import security.RSAPrivateKey;
import security.RSAPublicKey;

class RMIXorSocket extends Socket {

	/*
	 * The pattern used to "encrypt" and "decrypt" each byte sent or received by the
	 * socket.
	 */
	private RSAPublicKey rsaPublicKey;
	private RSAPrivateKey rsaPrivateKey;
	
	/* The InputStream used by the socket. */
	private InputStream in = null;

	/* The OutputStream used by the socket */
	private OutputStream out = null;

	/*
	 * Constructor for class XorSocket.
	 */
	public RMIXorSocket(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
		super();
		this.rsaPublicKey = rsaPublicKey;
		this.rsaPrivateKey = rsaPrivateKey;
	}

	/*
	 * Constructor for class XorSocket.
	 */
	public RMIXorSocket(String host, int port, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
		super(host, port);
		this.rsaPublicKey = rsaPublicKey;
		this.rsaPrivateKey = rsaPrivateKey;
	}

	/*
	 * Returns a stream of type XorInputStream.
	 */
	public synchronized InputStream getInputStream() throws IOException {
		if (in == null) {
			in = new RMIXorInputStream(super.getInputStream(), rsaPublicKey, rsaPrivateKey);
		}
		return in;
	}

	/*
	 * Returns a stream of type XorOutputStream.
	 */
	public synchronized OutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new RMIXorOutputStream(super.getOutputStream(), rsaPublicKey, rsaPrivateKey);
		}
		return out;
	}
}
