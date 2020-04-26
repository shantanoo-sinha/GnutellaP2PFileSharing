/**
 * 
 */
package security;

import java.io.Serializable;

/**
 * @author Shantanoo
 *
 */
public class RSAKeyPair implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8484275969755245333L;
	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;
	
	/**
	 * @param privateKey
	 * @param publicKey
	 */
	public RSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		super();
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public RSAPrivateKey getPrivate() {
		return privateKey;
	}

	public RSAPublicKey getPublic() {
		return publicKey;
	}
}