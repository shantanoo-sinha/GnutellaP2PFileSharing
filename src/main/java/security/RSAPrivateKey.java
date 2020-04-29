/**
 * 
 */
package security;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * The Class RSAPrivateKey.
 *
 * @author Shantanoo
 */
public class RSAPrivateKey implements Serializable {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7003287257884027407L;
	
	/** The modulus. */
	private BigInteger modulus;
    
    /** The private exponent. */
    private BigInteger privateExponent;

    /**
     * Creates a new RSAPrivateKey.
     *
     * @param modulus the modulus
     * @param privateExponent the private exponent
     */
    public RSAPrivateKey(BigInteger modulus, BigInteger privateExponent) {
        this.modulus = modulus;
        this.privateExponent = privateExponent;
    }

    /**
     * Returns the modulus.
     *
     * @return the modulus
     */
    public BigInteger getModulus() {
        return this.modulus;
    }

    /**
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
}