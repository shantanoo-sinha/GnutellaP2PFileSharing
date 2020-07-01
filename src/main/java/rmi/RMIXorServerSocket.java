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
import java.net.ServerSocket;
import java.net.Socket;

import security.RSAPrivateKey;
import security.RSAPublicKey;

/**
 * The Class RMIXorServerSocket.
 */
class RMIXorServerSocket extends ServerSocket {

	/** The rsa public key. */
	/*
	 * The pattern used to "encrypt" and "decrypt" each byte sent or received by the
	 * socket.
	 */
	private RSAPublicKey rsaPublicKey;
	
	/** The rsa private key. */
	private RSAPrivateKey rsaPrivateKey;
	
	/**
	 * Instantiates a new RMI xor server socket.
	 *
	 * @param port the port
	 * @param rsaPublicKey the rsa public key
	 * @param rsaPrivateKey the rsa private key
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * Constructor for class XorServerSocket.
	 */
	public RMIXorServerSocket(int port, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
		super(port);
		this.rsaPublicKey = rsaPublicKey;
		this.rsaPrivateKey = rsaPrivateKey;
	}

	/* (non-Javadoc)
	 * @see java.net.ServerSocket#accept()
	 */
	/*
	 * Creates a socket of type XorSocket and then calls implAccept to wait for a
	 * client connection.
	 */
	public Socket accept() throws IOException {
		Socket s = new RMIXorSocket(rsaPublicKey, rsaPrivateKey);
		implAccept(s);
		return s;
	}
}