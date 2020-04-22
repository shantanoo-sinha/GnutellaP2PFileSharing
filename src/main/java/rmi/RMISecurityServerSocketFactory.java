package rmi;

import java.io.*;
import java.net.*;
import java.rmi.server.*;

import security.RSAPrivateKey;
import security.RSAPublicKey;

public class RMISecurityServerSocketFactory implements RMIServerSocketFactory {

	private RSAPublicKey rsaPublicKey = null;
	private RSAPrivateKey rsaPrivateKey = null;
	
	public RMISecurityServerSocketFactory(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
		this.rsaPublicKey = rsaPublicKey;
		this.rsaPrivateKey = rsaPrivateKey;
	}

	public ServerSocket createServerSocket(int port) throws IOException {
		return new RMISecurityServerSocket(port, rsaPublicKey, rsaPrivateKey);
	}

	public int hashCode() {
		return getClass().hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}
}
