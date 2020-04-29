/**
 * 
 */
package security;

import java.io.Serializable;

/**
 * The Class RSAKeyPair.
 *
 * @author Shantanoo
 */
public class RSAKeyPair implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8484275969755245333L;
	
	/** The private key. */
	private final RSAPrivateKey privateKey;
	
	/** The public key. */
	private final RSAPublicKey publicKey;
	
	/**
	 * Instantiates a new RSA key pair.
	 *
	 * @param privateKey the private key
	 * @param publicKey the public key
	 */
	public RSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		super();
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	/**
	 * Gets the private.
	 *
	 * @return the private
	 */
	public RSAPrivateKey getPrivate() {
		return privateKey;
	}

	/**
	 * Gets the public.
	 *
	 * @return the public
	 */
	public RSAPublicKey getPublic() {
		return publicKey;
	}
}