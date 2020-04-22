package rmi;

import java.io.*;
import java.net.*;

import security.RSAPrivateKey;
import security.RSAPublicKey;

class RMISecurityServerSocket extends ServerSocket {
  
	/*
     * The keys used to "encrypt" and "decrypt" each byte sent
     * or received by the socket.
     */
    private RSAPublicKey rsaPublicKey = null;
    private RSAPrivateKey rsaPrivateKey = null;
    
    public RSAPublicKey getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(RSAPublicKey rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public RSAPrivateKey getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(RSAPrivateKey rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}
	
    /**
     * @param port
	 * @param rsaPublicKey
	 * @param rsaPrivateKey
	 * @throws IOException
	 */
	public RMISecurityServerSocket(int port, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
		super(port);
		this.rsaPublicKey = rsaPublicKey;
		this.rsaPrivateKey = rsaPrivateKey;
	}

	/* 
     * Creates a socket of type RMISecuritySocket and then calls 
     * implAccept to wait for a client connection.
     */
    public Socket accept() throws IOException {
        Socket s = new RMISecuritySocket(rsaPublicKey, rsaPrivateKey);
        implAccept(s);
        return s;
    }
}




