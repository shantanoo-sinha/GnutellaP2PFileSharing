package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class TestRSA {

    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
    private static String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm";

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
            String encryptedString = Base64.getEncoder().encodeToString(encrypt("Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is 12", publicKey));
            System.out.println(encryptedString);
            String decryptedString = TestRSA.decrypt(encryptedString, privateKey);
            System.out.println(decryptedString);
        	/*String text = "Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.Dhiraj is the author.";
            TestRSA rsa = new TestRSA();
            int keySize = rsa.getByteLength()128;
    		//int maxDataSize = (keySize - 11);
    		int maxBlockSize = (1024 / 8 - 11);
    		int blocksCount = (int) Math.ceil((double) text.getBytes().length / maxBlockSize);
    		byte[][] blocksCollection = new byte[blocksCount][];
    		
    		byte[] encrypted = null;
            int i = 0;
            int startIndex;
            int endIndex;
            int sizeOfBlocks = 0;
            while (i < blocksCount) {
                startIndex = i * (maxBlockSize);
                endIndex = startIndex + maxBlockSize;
                try {
                	byte[] paddedMessage = null;
                    byte[] message = Arrays.copyOfRange(text.getBytes(), startIndex, Math.min(text.getBytes().length,endIndex));
                    if(message.length < keySize) {
                    	paddedMessage = new byte[maxBlockSize];
                    	System.arraycopy(message, 0, paddedMessage, 0, message.length);
                    } else
                    	paddedMessage = message;
    				encrypted = rsa.encrypt(new String(paddedMessage), publicKey);
                    sizeOfBlocks += encrypted.length;
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
            System.out.println("Glued Encoded data " + Arrays.toString(encryptedMessage));
            System.out.println("---------------------------------------");
            int blockSize = (1024 / 8);
            blocksCount = encryptedMessage.length / blockSize;
            i = 0;
            startIndex=0;
            endIndex=0;
            byte[] byteChunkData;
            byte[] decryptedChunk;
            byte[] decryptedMessage = new byte[0];
            while (i < blocksCount) {
                startIndex = i * (blockSize);
                endIndex = startIndex + blockSize;
                byteChunkData = Arrays.copyOfRange(encryptedMessage, startIndex, endIndex);
                decryptedChunk = rsa.decrypt(new String(byteChunkData), privateKey).getBytes();
                decryptedMessage = concatenateByteArrays(decryptedMessage, decryptedChunk);
                i++;
            }
            System.out.println("Decrypted message:" + Arrays.toString(decryptedMessage));
            String plainText = new String(decryptedMessage);
            System.out.println("Decrypted message:" + plainText);*/
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

    }
 
    /*public int getByteLength() {
    	PublicKey publicKey1 = getPublicKey(publicKey);
		int i = publicKey1.getAlgorithm().getEncoded().getModulus().bitLength();
		return i + 7 >> 3;
	}*/
    
    private static byte[] concatenateByteArrays(byte[] decryptedMessage, byte[] decryptedChunk) {
		byte[] c = new byte[decryptedMessage.length + decryptedChunk.length];
		System.arraycopy(decryptedMessage, 0, c, 0, decryptedMessage.length);
		System.arraycopy(decryptedChunk, 0, c, decryptedMessage.length, decryptedChunk.length);
		return c;
	}
}