/**
 * 
 */
package security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.BitSet;

/**
 * @author Shantanoo
 *
 */
public class RSAEncryption {

	private BigInteger n, d, e;
	private int bitlen = 1024;

	private RSAPrivateKey rsaPrivateKey;
	private RSAPublicKey rsaPublicKey;

	/** Create an instance that can encrypt using someone else's public key. */
	public RSAEncryption(BigInteger newn, BigInteger newe, BigInteger newd) {
		n = newn;
		e = newe;
		d = newd;
	}

	/** Create an instance that can both encrypt and decrypt. */
	public RSAEncryption(int bits) {
		bitlen = bits;
		/*
		 * SecureRandom r = new SecureRandom(); BigInteger p = new BigInteger(bitlen /
		 * 2, 100, r); BigInteger q = new BigInteger(bitlen / 2, 100, r); n =
		 * p.multiply(q); BigInteger phi =
		 * (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)); e =
		 * BigInteger.probablePrime(bitlen / 2, r); while
		 * (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
		 * e.add(BigInteger.ONE); } d = e.modInverse(phi);
		 */
	}

	/** Encrypt the given plaintext message. */
	public String encrypt(String message) {
		return (new BigInteger(message.getBytes())).modPow(e, n).toString();
	}

	/** Encrypt the given plaintext message. */
	public BigInteger encrypt(BigInteger message) {
		return message.modPow(e, n);
	}

	public byte[] encrypt(byte[] message) {
		return new BigInteger(message).modPow(e, n).toByteArray();
	}

	/** Decrypt the given ciphertext message. */
	public String decrypt(String message) {
		return new String((new BigInteger(message)).modPow(d, n).toByteArray());
	}

	/** Decrypt the given ciphertext message. */
	public BigInteger decrypt(BigInteger message) {
		return message.modPow(d, n);
	}

	public byte[] decrypt(byte[] message) {
		return new BigInteger(message).modPow(d, n).toByteArray();
	}

	/** Generate a new public and private key set. */
	public void generateKeys() {
		SecureRandom r = new SecureRandom();
		BigInteger p = BigInteger.probablePrime(bitlen, r);
		BigInteger q = BigInteger.probablePrime(bitlen, r);
		n = p.multiply(q);
		BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		e = BigInteger.probablePrime(bitlen, r);
		while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
			e.add(BigInteger.ONE);
		}
		d = e.modInverse(phi);

		rsaPrivateKey = new RSAPrivateKey(n, d);
		rsaPublicKey = new RSAPublicKey(n, e);
	}

	public RSAKeyPair getRSAKeyPair() {
		return new RSAKeyPair(rsaPrivateKey, rsaPublicKey);
	}

	/** Return the modulus. */
	public BigInteger getN() {
		return n;
	}

	/** Return the public key. */
	public BigInteger getE() {
		return e;
	}

	/** Return the private key. */
	public BigInteger getD() {
		return d;
	}

	public int getBitlen() {
		return bitlen;
	}

	public void setBitlen(int bitlen) {
		this.bitlen = bitlen;
	}

	/**
	 * Trivial test program.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		RSAEncryption rsa = new RSAEncryption(128);
		rsa.generateKeys();
		System.out.println("N: " + rsa.getN());
		System.out.println("e: " + rsa.getE());
		System.out.println("d: " + rsa.d);
		System.out.println("**************************");
		String nStr = Base64.getEncoder().encodeToString(("" + rsa.getN()).getBytes());
		System.out.println("N: " + nStr);
		String eStr = Base64.getEncoder().encodeToString(("" + rsa.getE()).getBytes());
		System.out.println("e: " + eStr);
		String dStr = Base64.getEncoder().encodeToString(("" + rsa.d).getBytes());
		System.out.println("d: " + dStr);
		System.out.println("**************************");
		System.out.println("N: " + new String(Base64.getDecoder().decode(nStr)).toString());
		System.out.println("e: " + new String(Base64.getDecoder().decode(eStr)).toString());
		System.out.println("d: " + new String(Base64.getDecoder().decode(dStr)).toString());
		System.out.println("**************************");

		String text1 = "Yellow and Black Border Collies.";
		System.out.println("Plaintext: " + text1);
		BigInteger plaintext = new BigInteger(text1.getBytes());

		BigInteger ciphertext = rsa.encrypt(plaintext);
		System.out.println("Ciphertext: " + ciphertext);
		plaintext = rsa.decrypt(ciphertext);

		String text2 = new String(plaintext.toByteArray());
		System.out.println("Plaintext: " + text2);
		System.out.println("**************************");

		String text = "Yellow and Black Border Collies.1";
		System.out.println("Plaintext: " + text);

		int byteLength = getByteLength(rsa.getN());
		int maxDataSize = (byteLength - 11);
		//byte[] buffer = new byte[byteLength];
		int bufl;
		/*int i = 0;
		while(i<byteLength) {
			buffer[i++] = 0;
		}*/
		byte[] data;
		if(text.getBytes().length<=byteLength) {
			data = new byte[byteLength];
			System.arraycopy(text.getBytes(), 0, data, 0, text.getBytes().length);
			//data[byteLength-1] = (byte) (byteLength-text.getBytes().length);
		} else {
			int size = (text.getBytes().length + byteLength - 1) / byteLength;
			data = new byte[size*byteLength];
			System.arraycopy(text.getBytes(), 0, data, 0, text.getBytes().length);
			data[byteLength-1] = (byte) (byteLength-text.getBytes().length);
		}
		//System.arraycopy(text.getBytes(), 0, buffer, 0, text.getBytes().length);
		// start FileIO
		byte[] buffer = new byte[byteLength];
		ByteArrayOutputStream outputWriter = null;
		ByteArrayInputStream inputReader = null;
		outputWriter = new ByteArrayOutputStream();
		inputReader = new ByteArrayInputStream(data);
		while ((bufl = inputReader.read(buffer, 0, byteLength)) != -1) {
			byte[] encText = rsa.encrypt(buffer);
			outputWriter.write(encText);
		}
		outputWriter.flush();
		byte[] encrypt = outputWriter.toByteArray();
		System.out.println("Ciphertext: " + new String(encrypt));

		buffer = new byte[byteLength];
		// start FileIO
		outputWriter = null;
		inputReader = null;
		outputWriter = new ByteArrayOutputStream();
		inputReader = new ByteArrayInputStream(encrypt);
		while ((bufl = inputReader.read(buffer, 0, byteLength)) != -1) {
			byte[] encText = rsa.decrypt(buffer);
			outputWriter.write(encText);
		}
		outputWriter.flush();
		
		byte[] byteArray = outputWriter.toByteArray();
		int lim = byteArray[byteArray.length-1];
		boolean isPadding = false;
		if(lim>0) {
			isPadding = true;
			for(int i=1; i<lim; i++) {
				if(byteArray[byteArray.length-i-1]!=0) {
					isPadding = false;
					break;
				}
			}
		}
		byte[] decoded;
		if(isPadding) {
			BitSet bitSet = BitSet.valueOf(byteArray);
			bitSet.clear(byteArray.length-lim, byteArray.length); //This will clear 41st to 56th Bit
			decoded = bitSet.toByteArray();
		} else {
			decoded = byteArray;
		}
		
		String textOut = new String(decoded);
		System.out.println("Plaintext: " + textOut);

		/*
		 * int fromIndex = 0, paramInt2 = text.getBytes().length; byte[]
		 * paramArrayOfByte = text.getBytes(); int blockSize = (rsa.getBitlen() / 8) -
		 * 11; byte[] obuffer = new byte[blockSize];
		 * 
		 * ByteArrayOutputStream out = new
		 * ByteArrayOutputStream();while(fromIndex<paramInt2) {
		 * System.arraycopy(paramArrayOfByte, fromIndex, obuffer, 0, Math.min(blockSize,
		 * paramInt2 - fromIndex)); fromIndex += blockSize;
		 * out.write(rsa.encrypt(obuffer)); }out.flush();
		 * 
		 * byte[] encrypt = out.toByteArray();System.out.println("Ciphertext: "+new
		 * String(encrypt));
		 * 
		 * obuffer=new byte[blockSize];fromIndex=0;paramInt2=encrypt.length;out=new
		 * ByteArrayOutputStream();while(fromIndex<paramInt2) {
		 * System.arraycopy(encrypt, fromIndex, obuffer, 0, Math.min(blockSize,
		 * paramInt2 - fromIndex)); fromIndex += blockSize;
		 * out.write(rsa.decrypt(obuffer)); }out.flush(); String textOut = new
		 * String(out.toByteArray());System.out.println("Plaintext: "+textOut);
		 */
	}

	public static byte[] copyBytes(byte[] arr, int length) {
		byte[] newArr = null;
		if (arr.length == length) {
			newArr = arr;
		} else {
			newArr = new byte[length];
			for (int i = 0; i < length; i++) {
				newArr[i] = (byte) arr[i];
			}
		}
		return newArr;
	}
	
	public static int getByteLength(BigInteger paramBigInteger) {
		int i = paramBigInteger.bitLength();
		return i + 7 >> 3;
	}
	
	/*private byte[] padV15(byte[] paramArrayOfByte, int paddedSize) {
		byte[] arrayOfByte1 = new byte[paddedSize];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, paddedSize - paramArrayOfByte.length,
				paramArrayOfByte.length);

		int i = paddedSize - 3 - paramArrayOfByte.length;
		int j = 0;
		arrayOfByte1[(j++)] = 0;
		arrayOfByte1[(j++)] = ((byte) type);
		if (type == 1) {
			while (i-- > 0) {
				arrayOfByte1[(j++)] = -1;
			}
		}
		if (random == null) {
			random = JCAUtil.getSecureRandom();
		}
		byte[] arrayOfByte2 = new byte[64];
		int k = -1;
		while (i-- > 0) {
			int m;
			do {
				if (k < 0) {
					random.nextBytes(arrayOfByte2);
					k = arrayOfByte2.length - 1;
				}
				m = arrayOfByte2[(k--)] & 0xFF;
			} while (m == 0);
			arrayOfByte1[(j++)] = ((byte) m);
		}
		return arrayOfByte1;
	}

	private byte[] unpadV15(byte[] paramArrayOfByte) throws BadPaddingException {
		int i = 0;
		int j = 0;
		if (paramArrayOfByte[(i++)] != 0) {
			j = 1;
		}
		if (paramArrayOfByte[(i++)] != type) {
			j = 1;
		}
		int k = 0;
		while (i < paramArrayOfByte.length) {
			m = paramArrayOfByte[(i++)] & 0xFF;
			if ((m == 0) && (k == 0)) {
				k = i;
			}
			if ((i == paramArrayOfByte.length) && (k == 0)) {
				j = 1;
			}
			if ((type == 1) && (m != 255) && (k == 0)) {
				j = 1;
			}
		}
		int m = paramArrayOfByte.length - k;
		if (m > maxDataSize) {
			j = 1;
		}
		byte[] arrayOfByte1 = new byte[k];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, k);

		byte[] arrayOfByte2 = new byte[m];
		System.arraycopy(paramArrayOfByte, k, arrayOfByte2, 0, m);

		BadPaddingException localBadPaddingException = new BadPaddingException("Decryption error");
		if (j != 0) {
			throw localBadPaddingException;
		}
		return arrayOfByte2;
	}*/
}