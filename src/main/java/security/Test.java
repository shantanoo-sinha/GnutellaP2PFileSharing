package security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import model.FileConsistencyState;
import model.P2PFile;

/**
 * The Class Test.
 */
public class Test {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
//		RSAEncryption rsa = new RSAEncryption(new BigInteger("33461111308295467131652260359252754555563812261705615469344235564299640409763"), new BigInteger("332338396668082383032822234929595546483"), new BigInteger("404474471493745102611113761973445214764898579875285444360885495576443231723"));
//		RSA1 rsa = new RSA1(new BigInteger("11743467437058566874276910167868077558707630053594470802237709977548017822402931693568711761260304561060049404096295130294958422300841820216423416667323251"), new BigInteger("5744288465385020881359370805509988522445064890847656201069551814847661720777211317549764537271730334249734798522528390839918835419957017594151274480589144961250593652215725996517434229823806695943371005961706687882904063586811301173568544174102573198925349036857930649313189884966515031881404577850468305824894585795229931605296602464121448464727737128583401947404993017770096090114601933315116345801916158559196505850550903032725895828763184914289666801040119926254370745171285920661739094784245507854204352918287126169369801292669441003274161449519074580757140502084262796137629341862330544201340129919618792194491"), new BigInteger("11194680160736648460238203145959822468978073490526746476152181312569074436078159969349743002885107206432430338598611252212336411478014506116045504736345765283417931077177982407555917872557229561724184493602675607247192616855535819423046496021373450625220302596032353609972217053697238525090226043468732354773425841035659469855215489842322350805245069321297435203759864535478199277316529415460702044468772393232147347221272580882017987269275088576163322561519456071551594786408734219117748118526062902583837111610808590402274255158891553813830981311026425041985991030019611891086546573036499930185512051992684069099789"));
//		RSA1 rsa = new RSA1();
		RSAEncryption rsa = new RSAEncryption(1024);
		Test test = new Test();
		System.out.println("N: " + rsa.getModulus());
		System.out.println("e: " + rsa.getPublicKey());
		System.out.println("d: " + rsa.getPrivateKey());
		System.out.println("**************************");
		
		String text1 = "Yellow and Black Border Collies.";
		System.out.println("Plaintext: " + text1);

		/*byte[] ciphertext = rsa.encryptMessage(text1.getBytes());
		System.out.println("Ciphertext: " + ciphertext);
		byte[] plaintext = rsa.decryptMessage(ciphertext);*/
		
		/*BigInteger ciphertext1 = rsa.encrypt(new BigInteger("65537"));
		System.out.println("Ciphertext: " + ciphertext1);
		BigInteger plaintext1 = rsa.decrypt(ciphertext1);
		System.out.println("Plaintext: " + plaintext1);
		System.out.println("**************************");*/
		
		byte[] ciphertext = rsa.encrypt(text1.getBytes()).toByteArray();
		System.out.println("Ciphertext: " + ciphertext);
		byte[] plaintext = rsa.decrypt(ciphertext).toByteArray();

		String text2 = new String(plaintext);
		System.out.println("Plaintext: " + text2);
		System.out.println("**************************");

		/*String text = "Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.1Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.2Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.Yellow and Black Border Collies.3";
		System.out.println("Plaintext: " + text);
		System.out.println("Plaintext " + Arrays.toString(text.getBytes()));

		byte[] encryptedData = rsa.encryptData(text.getBytes());
		System.out.println("Glued Encoded data " + Arrays.toString(encryptedData));
        System.out.println("---------------------------------------");
        byte[] decryptedData = rsa.decryptData(encryptedData);
        String plainText = new String(decryptedData);
        System.out.println("Decrypted message:" + plainText);*/
		
		
        /*MessageID messageID = new MessageID("rmi://localhost:2001", 5l);
        System.out.println("Plaintext message:" + messageID.getLeafNodeId());
        System.out.println("Plaintext message:" + messageID.getSequenceNumber());
        System.out.println("Plaintext message:" + messageID);
        
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objOutputStream = new ObjectOutputStream(byteOutputStream);
        objOutputStream.writeObject(messageID);
        byte[] encryptedData = rsa.encryptData(byteOutputStream.toByteArray());
		System.out.println("Glued Encoded data " + Arrays.toString(encryptedData));
        
		System.out.println("---------------------------------------");
        
		byte[] decryptedData = rsa.decryptData(encryptedData);
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decryptedData);
        ObjectInputStream objInputStream = new ObjectInputStream(byteInputStream);
        MessageID decryptedMessageID = (MessageID)objInputStream.readObject();
        
        System.out.println("Decrypted message:" + decryptedMessageID.getLeafNodeId());
        System.out.println("Decrypted message:" + decryptedMessageID.getSequenceNumber());
        System.out.println("Decrypted message:" + decryptedMessageID);*/
        
		P2PFile p2pFile = new P2PFile(95, 550, "Client2", "rmi://localhost:2002/PeerServer", "rmi://localhost:2001/PeerServer", "rmi://localhost:2003/PeerServer", null, new File("C:\\\\SHantanoo\\\\959.txt"), "959.txt", FileConsistencyState.EXPIRED);
        System.out.println("Plaintext message:" + p2pFile.getVersion());
        System.out.println("Plaintext message:" + p2pFile.getTTR());
        System.out.println("Plaintext message:" + p2pFile.getOriginServerID());
        System.out.println("Plaintext message:" + p2pFile.getOriginServerSuperPeerID());
        System.out.println("Plaintext message:" + p2pFile.getCurrentAddress());
        System.out.println("Plaintext message:" + p2pFile.getFileContent());
        System.out.println("Plaintext message:" + p2pFile.getFile());
        System.out.println("Plaintext message:" + p2pFile.getFileName());
        System.out.println("Plaintext message:" + p2pFile.getState());
        System.out.println("Plaintext message:" + p2pFile);
        
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objOutputStream = new ObjectOutputStream(byteOutputStream);
        objOutputStream.writeObject(p2pFile);
        byte[] encryptedData = rsa.encryptData(byteOutputStream.toByteArray());
		System.out.println("Glued Encoded data " + Arrays.toString(encryptedData));
        
		System.out.println("---------------------------------------");
        
		byte[] decryptedData = rsa.decryptData(encryptedData);
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decryptedData);
        ObjectInputStream objInputStream = new ObjectInputStream(byteInputStream);
        P2PFile decryptedP2PFile = (P2PFile)objInputStream.readObject();

        System.out.println("Decrypted message:" + decryptedP2PFile.getVersion());
        System.out.println("Decrypted message:" + decryptedP2PFile.getTTR());
        System.out.println("Decrypted message:" + decryptedP2PFile.getOriginServerID());
        System.out.println("Decrypted message:" + decryptedP2PFile.getOriginServerSuperPeerID());
        System.out.println("Decrypted message:" + decryptedP2PFile.getCurrentAddress());
        System.out.println("Decrypted message:" + decryptedP2PFile.getFileContent());
        System.out.println("Decrypted message:" + decryptedP2PFile.getFile());
        System.out.println("Decrypted message:" + decryptedP2PFile.getFileName());
        System.out.println("Decrypted message:" + decryptedP2PFile.getState());
        System.out.println("Decrypted message:" + decryptedP2PFile);
		
		/*int keySize = rsa.getByteLength();
		int maxBlockSize = (keySize - 11);
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
                byte[] message = Arrays.copyOfRange(text.getBytes(), startIndex, Math.min(text.getBytes().length,endIndex));
                byte[] paddedMessage = rsa.pad(message, keySize);
				encrypted = rsa.encryptToBytes(paddedMessage);
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
        	encryptedMessage = rsa.concatenateByteArrays(encryptedMessage, blocksCollection[i]);
            i++;
        }
        System.out.println("Glued Encoded data " + Arrays.toString(encryptedMessage));
        System.out.println("---------------------------------------");
        int blockSize = (keySize);
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
            decryptedChunk = rsa.decryptToBytes(byteChunkData);
            byte[] unpaddedDecryptedChunk = rsa.unpad(decryptedChunk, maxBlockSize); 
            decryptedMessage = rsa.concatenateByteArrays(decryptedMessage, unpaddedDecryptedChunk);
            i++;
        }
        System.out.println("Decrypted message:" + Arrays.toString(decryptedMessage));
        String plainText = new String(decryptedMessage);
        System.out.println("Decrypted message:" + plainText);*/
	}
	
	/*private static byte[] concatenateByteArrays(byte[] decryptedMessage, byte[] decryptedChunk) {
		byte[] c = new byte[decryptedMessage.length + decryptedChunk.length];
		System.arraycopy(decryptedMessage, 0, c, 0, decryptedMessage.length);
		System.arraycopy(decryptedChunk, 0, c, decryptedMessage.length, decryptedChunk.length);
		return c;
	}*/
	
	/** The Constant HEX_ARRAY. */
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	/**
	 * Bytes to hex.
	 *
	 * @param bytes the bytes
	 * @return the string
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars);
	}

    /*private byte[] pad(byte[] data, int paddedSize) {
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
    
    private byte[] unpad(byte[] paramArrayOfByte, int maxDataSize) {
		int i = 0;
		int j = 0;
		if (paramArrayOfByte[(i++)] != 0) {
			j = 1;
		}
		int k = 0;
		while (i < paramArrayOfByte.length) {
			int m = paramArrayOfByte[(i++)] & 0xFF;
			if ((m == 0) && (k == 0)) {
				k = i;
			}
			if ((i == paramArrayOfByte.length) && (k == 0)) {
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

		return arrayOfByte2;
	}*/
    
    /*private byte[] encryptData() {
    	int keySize = rsa.getByteLength();
		//int maxDataSize = (keySize - 11);
		int maxBlockSize = (1024 / 8keySize - 11);
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
                byte[] message = Arrays.copyOfRange(text.getBytes(), startIndex, Math.min(text.getBytes().length,endIndex));
                byte[] paddedMessage = test.pad(message, keySize);
				encrypted = rsa.encryptToBytes(paddedMessage);
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
        return encryptedMessage;
    }*/
}
