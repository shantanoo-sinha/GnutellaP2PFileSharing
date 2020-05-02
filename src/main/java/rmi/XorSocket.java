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

/**
 * The Class XorSocket.
 */
class XorSocket extends Socket {

	/** The pattern. */
	/*
	 * The pattern used to "encrypt" and "decrypt" each byte sent or received by the
	 * socket.
	 */
	private final byte pattern;

	/** The in. */
	/* The InputStream used by the socket. */
	private InputStream in = null;

	/** The out. */
	/* The OutputStream used by the socket */
	private OutputStream out = null;

	/**
	 * Instantiates a new xor socket.
	 *
	 * @param pattern the pattern
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * Constructor for class XorSocket.
	 */
	public XorSocket(byte pattern) throws IOException {
		super();
		this.pattern = pattern;
	}

	/**
	 * Instantiates a new xor socket.
	 *
	 * @param host the host
	 * @param port the port
	 * @param pattern the pattern
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/*
	 * Constructor for class XorSocket.
	 */
	public XorSocket(String host, int port, byte pattern) throws IOException {
		super(host, port);
		this.pattern = pattern;
	}

	/* (non-Javadoc)
	 * @see java.net.Socket#getInputStream()
	 */
	/*
	 * Returns a stream of type XorInputStream.
	 */
	public synchronized InputStream getInputStream() throws IOException {
		if (in == null) {
			in = new XorInputStream(super.getInputStream(), pattern);
		}
		return in;
	}

	/* (non-Javadoc)
	 * @see java.net.Socket#getOutputStream()
	 */
	/*
	 * Returns a stream of type XorOutputStream.
	 */
	public synchronized OutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new XorOutputStream(super.getOutputStream(), pattern);
		}
		return out;
	}
}
