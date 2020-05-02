package security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.Server;

/**
 * The Class RSAEncryption.
 */
public class RSAEncryption {
	
	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Server.class);
	
	/** The Constant one. */
	private final static BigInteger one = new BigInteger("1");
	
	/** The Constant random. */
	private final static SecureRandom random = new SecureRandom();

	/** The private key. */
	private BigInteger privateKey;
	
	/** The public key. */
	private BigInteger publicKey;
	
	/** The modulus. */
	private BigInteger modulus;

	/** The bit len. */
	private int bitLen = 1024;
	
	/** The rsa private key. */
	private RSAPrivateKey rsaPrivateKey;
	
	/** The rsa public key. */
	private RSAPublicKey rsaPublicKey;

	/**
	 * Instantiates a new RSA encryption.
	 *
	 * @param N the n
	 */
	// generate an N-bit (roughly) public and private key
	public RSAEncryption(int N) {
		this.bitLen = N;

		BigInteger p = BigInteger.probablePrime(bitLen / 2, random);
		BigInteger q = BigInteger.probablePrime(bitLen / 2, random);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

		modulus = p.multiply(q);
		publicKey = new BigInteger("65537"); // common value in practice = 2^16 + 1
		privateKey = publicKey.modInverse(phi);
		
		rsaPrivateKey = new RSAPrivateKey(modulus, privateKey);
		rsaPublicKey = new RSAPublicKey(modulus, publicKey);
	}
	
	/**
	 *  Create an instance that can encrypt using someone else's public key.
	 *
	 * @param newn the newn
	 * @param newe the newe
	 * @param newd the newd
	 */
	public RSAEncryption(BigInteger newn, BigInteger newe, BigInteger newd) {
		this.modulus = newn;
		this.publicKey = newe;
		this.privateKey = newd;
	}

	/**
	 * Encrypt.
	 *
	 * @param message the message
	 * @return the big integer
	 */
	public BigInteger encrypt(BigInteger message) {
		return message.modPow(publicKey, modulus);
	}

	/**
	 * Decrypt.
	 *
	 * @param encrypted the encrypted
	 * @return the big integer
	 */
	public BigInteger decrypt(BigInteger encrypted) {
		return encrypted.modPow(privateKey, modulus);
	}

	/**
	 * Encrypt.
	 *
	 * @param message the message
	 * @return the big integer
	 */
	public BigInteger encrypt(byte[] message) {
		return new BigInteger(message).modPow(publicKey, modulus);
	}

	/**
	 * Decrypt.
	 *
	 * @param encrypted the encrypted
	 * @return the big integer
	 */
	public BigInteger decrypt(byte[] encrypted) {
		return new BigInteger(encrypted).modPow(privateKey, modulus);
	}
	
	/**
	 * Encrypt to bytes.
	 *
	 * @param message the message
	 * @return the byte[]
	 */
	public byte[] encryptToBytes(byte[] message) {
		BigInteger bigInteger = new BigInteger(1, message).modPow(publicKey, modulus);
		return toByteArray(bigInteger, getByteLength());
	}
	
	/**
	 * Encrypt to bytes with private key.
	 *
	 * @param message the message
	 * @return the byte[]
	 */
	public byte[] encryptToBytesWithPrivateKey(byte[] message) {
		BigInteger bigInteger = new BigInteger(1, message).modPow(privateKey, modulus);
		return toByteArray(bigInteger, getByteLength());
	}

	/**
	 * Decrypt to bytes.
	 *
	 * @param encrypted the encrypted
	 * @return the byte[]
	 */
	public byte[] decryptToBytes(byte[] encrypted) {
		BigInteger bigInteger = new BigInteger(1, encrypted).modPow(privateKey, modulus);
		return toByteArray(bigInteger, getByteLength());
	}
	
	/**
	 * Decrypt to bytes with public key.
	 *
	 * @param encrypted the encrypted
	 * @return the byte[]
	 */
	public byte[] decryptToBytesWithPublicKey(byte[] encrypted) {
		BigInteger bigInteger = new BigInteger(1, encrypted).modPow(publicKey, modulus);
		return toByteArray(bigInteger, getByteLength());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s += "public  = " + publicKey + "\n";
		s += "private = " + privateKey + "\n";
		s += "modulus = " + modulus;
		return s;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		int N = 1024;
		RSAEncryption key = new RSAEncryption(N);
		logger.info(key);

		// create random message, encrypt and decrypt
		BigInteger message = new BigInteger(N - 50, random);

		//// create message by converting string to integer
		// String s = "test";
		// byte[] bytes = s.getBytes();
		// BigInteger message = new BigInteger(bytes);

		BigInteger encrypt = key.encrypt(message);
		BigInteger decrypt = key.decrypt(encrypt);
		logger.info("message   = " + message);
		logger.info("encrypted = " + encrypt);
		logger.info("decrypted = " + decrypt);

		logger.info("***********************");
		BigInteger encrypt1 = key.encrypt(message.toByteArray());
		BigInteger decrypt1 = key.decrypt(encrypt1.toByteArray());
		logger.info("message   = " + message);
		logger.info("encrypted = " + encrypt1);
		logger.info("decrypted = " + decrypt1);
	}
	
	/**
	 * Gets the byte length.
	 *
	 * @return the byte length
	 */
	public int getByteLength() {
		int i = getModulus().bitLength();
		return i + 7 >> 3;
	}

	/**
	 * Gets the private key.
	 *
	 * @return the private key
	 */
	public BigInteger getPrivateKey() {
		return privateKey;
	}

	/**
	 * Gets the public key.
	 *
	 * @return the public key
	 */
	public BigInteger getPublicKey() {
		return publicKey;
	}

	/**
	 * Gets the modulus.
	 *
	 * @return the modulus
	 */
	public BigInteger getModulus() {
		return modulus;
	}
	
	/**
	 * To byte array.
	 *
	 * @param bi the bi
	 * @param len the len
	 * @return the byte[]
	 */
	private static byte[] toByteArray(BigInteger bi, int len) {
        byte[] b = bi.toByteArray();
        int n = b.length;
        if (n == len) {
            return b;
        }
        // BigInteger prefixed a 0x00 byte for 2's complement form, remove it
        if ((n == len + 1) && (b[0] == 0)) {
            byte[] t = new byte[len];
            System.arraycopy(b, 1, t, 0, len);
            return t;
        }
        // must be smaller
        assert (n < len);
        byte[] t = new byte[len];
        System.arraycopy(b, 0, t, (len - n), n);
        return t;
    }
	
	/**
	 * Encrypt data.
	 *
	 * @param plainMessage the plain message
	 * @return the byte[]
	 */
	public byte[] encryptData(byte[] plainMessage) {
    	int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
		int blocksCount = (int) Math.ceil((double) plainMessage.length / maxBlockSize);
		byte[][] blocksCollection = new byte[blocksCount][];
		
		byte[] encrypted = null;
        int i = 0;
        int startIndex;
        int endIndex;
//        int sizeOfBlocks = 0;
        while (i < blocksCount) {
            startIndex = i * (maxBlockSize);
            endIndex = startIndex + maxBlockSize;
            try {
                byte[] message = Arrays.copyOfRange(plainMessage, startIndex, Math.min(plainMessage.length,endIndex));
                byte[] paddedMessage = pad(message, keySize);
				encrypted = encryptToBytes(paddedMessage);
//                sizeOfBlocks += encrypted.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
            blocksCollection[i] = encrypted;
            i++;
        }
        i = 0;
        int n = blocksCollection.length;
        byte[] encryptedMessage = new byte[0];
        while (i < n) {
        	encryptedMessage = concatenateByteArrays(encryptedMessage, blocksCollection[i]);
            i++;
        }
        return encryptedMessage;
    }
	
	/**
	 * Encrypt data with private key.
	 *
	 * @param plainMessage the plain message
	 * @return the byte[]
	 */
	public byte[] encryptDataWithPrivateKey(byte[] plainMessage) {
    	int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
		int blocksCount = (int) Math.ceil((double) plainMessage.length / maxBlockSize);
		byte[][] blocksCollection = new byte[blocksCount][];
		
		byte[] encrypted = null;
        int i = 0;
        int startIndex;
        int endIndex;
//        int sizeOfBlocks = 0;
        while (i < blocksCount) {
            startIndex = i * (maxBlockSize);
            endIndex = startIndex + maxBlockSize;
            try {
                byte[] message = Arrays.copyOfRange(plainMessage, startIndex, Math.min(plainMessage.length,endIndex));
                byte[] paddedMessage = pad(message, keySize);
				encrypted = encryptToBytesWithPrivateKey(paddedMessage);
//                sizeOfBlocks += encrypted.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
            blocksCollection[i] = encrypted;
            i++;
        }
        i = 0;
        int n = blocksCollection.length;
        byte[] encryptedMessage = new byte[0];
        while (i < n) {
        	encryptedMessage = concatenateByteArrays(encryptedMessage, blocksCollection[i]);
            i++;
        }
        return encryptedMessage;
    }
	
	/**
	 * Decrypt data.
	 *
	 * @param encryptedMessage the encrypted message
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	public byte[] decryptData(byte[] encryptedMessage) throws Exception {
		int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
        int blocksCount = encryptedMessage.length / keySize;
        
        int i = 0, startIndex=0, endIndex=0;
        byte[] byteChunkData, decryptedChunk;
        byte[] decryptedMessage = new byte[0];
        
        while (i < blocksCount) {
            startIndex = i * (keySize);
            endIndex = startIndex + keySize;
            byteChunkData = Arrays.copyOfRange(encryptedMessage, startIndex, endIndex);
            decryptedChunk = decryptToBytes(byteChunkData);
            byte[] unpaddedDecryptedChunk = unpad(decryptedChunk, maxBlockSize); 
            decryptedMessage = concatenateByteArrays(decryptedMessage, unpaddedDecryptedChunk);
            i++;
        }
//        logger.info("Decrypted message:" + Arrays.toString(decryptedMessage));
        return decryptedMessage;
	}
	
	/**
	 * Decrypt data with public key.
	 *
	 * @param encryptedMessage the encrypted message
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	public byte[] decryptDataWithPublicKey(byte[] encryptedMessage) throws Exception {
		int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
        int blocksCount = encryptedMessage.length / keySize;
        
        int i = 0, startIndex=0, endIndex=0;
        byte[] byteChunkData, decryptedChunk;
        byte[] decryptedMessage = new byte[0];
        
        while (i < blocksCount) {
            startIndex = i * (keySize);
            endIndex = startIndex + keySize;
            byteChunkData = Arrays.copyOfRange(encryptedMessage, startIndex, endIndex);
            decryptedChunk = decryptToBytesWithPublicKey(byteChunkData);
            byte[] unpaddedDecryptedChunk = unpad(decryptedChunk, maxBlockSize); 
            decryptedMessage = concatenateByteArrays(decryptedMessage, unpaddedDecryptedChunk);
            i++;
        }
//        logger.info("Decrypted message:" + Arrays.toString(decryptedMessage));
        return decryptedMessage;
	}
	
	/**
	 * Pad.
	 *
	 * @param data the data
	 * @param paddedSize the padded size
	 * @return the byte[]
	 */
	public byte[] pad(byte[] data, int paddedSize) {
    	byte[] byteArray = new byte[paddedSize];
    	System.arraycopy(data, 0, byteArray, paddedSize - data.length, data.length);
    	
    	int i = paddedSize - data.length - 2;
		int j = 0;
		byteArray[(j++)] = 0;
		
		byte[] byteArray2 = new byte[64];
		int k = -1;
		while (i-- > 0) {
			int m;
			do {
				if (k < 0) {
					SecureRandom random = new SecureRandom();
                    random.nextBytes(byteArray2);
					k = byteArray2.length - 1;
				}
				m = byteArray2[(k--)] & 0xFF;
			} while (m == 0);
			byteArray[(j++)] = ((byte) m);
		}
    	return byteArray;
    }
    
	/**
	 * Unpad.
	 *
	 * @param paramArrayOfByte the param array of byte
	 * @param maxDataSize the max data size
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	public byte[] unpad(byte[] paramArrayOfByte, int maxDataSize) throws Exception {
		int i = 0;
		if (paramArrayOfByte[(i++)] != 0) {
			throw new Exception("Bad Padding Exception");
		}
		int k = 0;
		while (i < paramArrayOfByte.length) {
			int m = paramArrayOfByte[(i++)] & 0xFF;
			if ((m == 0) && (k == 0)) {
				k = i;
			}
			if ((i == paramArrayOfByte.length) && (k == 0)) {
				throw new Exception("Bad Padding Exception");
			}
		}
		int m = paramArrayOfByte.length - k;
		if (m > maxDataSize) {
			throw new Exception("Bad Padding Exception");
		}
		byte[] arrayOfByte1 = new byte[k];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, k);

		byte[] arrayOfByte2 = new byte[m];
		System.arraycopy(paramArrayOfByte, k, arrayOfByte2, 0, m);

		return arrayOfByte2;
	}
    
    /**
     * Concatenate byte arrays.
     *
     * @param decryptedMessage the decrypted message
     * @param decryptedChunk the decrypted chunk
     * @return the byte[]
     */
    public static byte[] concatenateByteArrays(byte[] decryptedMessage, byte[] decryptedChunk) {
		byte[] c = new byte[decryptedMessage.length + decryptedChunk.length];
		System.arraycopy(decryptedMessage, 0, c, 0, decryptedMessage.length);
		System.arraycopy(decryptedChunk, 0, c, decryptedMessage.length, decryptedChunk.length);
		return c;
	}
    
    /**
     * Gets the RSA key pair.
     *
     * @return the RSA key pair
     */
    public RSAKeyPair getRSAKeyPair() {
		return new RSAKeyPair(rsaPrivateKey, rsaPublicKey);
	}
}
