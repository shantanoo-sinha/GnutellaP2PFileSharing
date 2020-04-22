package rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import security.RSAPrivateKey;
import security.RSAPublicKey;

class RMISecuritySocket extends Socket {
  
    /* The InputStream used by the socket. */
    private InputStream in = null;
  
    /* The OutputStream used by the socket */
    private OutputStream out = null;

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

	/* 
     * Constructor for class RMISecuritySocket. 
     */
    public RMISecuritySocket(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
        super();
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }
  
    /* 
     * Constructor for class RMISecuritySocket. 
     */
    public RMISecuritySocket(String host, int port, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) throws IOException {
        super(host, port);
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }
  
    /* 
     * Returns a stream of type RMISecurityInputStream. 
     */
    public synchronized InputStream getInputStream() throws IOException {
        if (in == null) {
            in = new RMISecurityInputStream(super.getInputStream(), rsaPrivateKey);
        }
        return in;
    }
  
    /* 
     *Returns a stream of type RMISecurityOutputStream. 
     */
    public synchronized OutputStream getOutputStream() throws IOException {
        if (out == null) {
            out = new RMISecurityOutputStream(super.getOutputStream(), rsaPublicKey);
        }
        return out;
    }
}
