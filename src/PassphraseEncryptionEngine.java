import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static extras.HexTools.catArray;
import static extras.HexTools.generateHexFromByteArray;

public class PassphraseEncryptionEngine implements KeccakAttributes {

    private KMACXOF256EncryptionEngine ee;
    private SecureRandom sr;

    /** Constructor that initializes the KMACXOF256 encryption and other utilities. */
    public PassphraseEncryptionEngine() {
        ee = new KMACXOF256EncryptionEngine();
        sr = new SecureRandom();
    }

    public SymmetricCryptogram encrypt(String plaintext, String passphrase) {

        //get z: z <-- Random(512)
        byte[] z = sr.generateSeed(512);

        //get ke and ka: (ke || ka) <-- KMACXOF256(z || pw, "", 1024, "S")
        String keka_key = catArray(z, passphrase.getBytes()).toString();
        byte[] keka = ee.getHash("", 1024, keka_key).getBytes();

        byte[] ke = new byte[keka.length/2];
        byte[] ka = new byte[keka.length/2];
        System.arraycopy(keka, 0, ke, 0, ke.length);
        System.arraycopy(keka, ke.length, ka, 0, ka.length);

        //get c: c <-- KMACXOF256(ke, "", |m|, "SKE") xor m
        String ctemp1 = ee.getHash("", plaintext.length(), ke.toString());
        BigInteger ctemp1_bi = new BigInteger(ctemp1, 16);
        byte[] ctemp2 = plaintext.getBytes();
        String ctemp2_ba = generateHexFromByteArray(ctemp2);
        BigInteger ctemp2_bi = new BigInteger(ctemp2_ba, 16);

        BigInteger ctemp_last_bi = ctemp1_bi.xor(ctemp2_bi);
        byte[] c = ctemp_last_bi.toByteArray();

        //get t: t <-- KMACXOF256(ka, m, 512, "SKA")
        String ka_string = generateHexFromByteArray(ka);
        String ttemp1 = ee.getHash(plaintext, 512, ka_string);
        byte[] t = ttemp1.getBytes();

        return new SymmetricCryptogram(z, c, t);
    }

    public String decrypt(SymmetricCryptogram itemToDecrypt, String passphrase) {
        //get z || pw
        byte[] keka_key_bytes = catArray(itemToDecrypt.getZ(), passphrase.getBytes());
        String keka_key = generateHexFromByteArray(keka_key_bytes);

        //get (ke || ka): (ke||ka) <-- KMACXOF256(z || pw, "", 1024, "S")
        String keka = ee.getHash("", 1024, keka_key);
        byte[] ke = new byte[keka.length()/2];
        byte[] ka = new byte[keka.length()/2];
        System.arraycopy(keka, 0, ke, 0, ke.length);
        System.arraycopy(keka, ke.length, ka, 0, ka.length);

        //get m: m <-- KMACXOF256(ke, "", |c|, "SKE")
        String ke_string = generateHexFromByteArray(ke);
        String mtemp1 = ee.getHash("", itemToDecrypt.getC().length, ke_string);
        BigInteger mtemp1_bi = new BigInteger(mtemp1, 16);
        String c_string = generateHexFromByteArray(itemToDecrypt.getC());
        BigInteger c_bi = new BigInteger(c_string, 16);
        BigInteger m_bi = mtemp1_bi.xor(c_bi);
        byte[] m = m_bi.toByteArray();

        //get t': t' <-- KMACXOF256(ka, m, 512, "SKA")
        String m_string = generateHexFromByteArray(m);
        String ka_string = generateHexFromByteArray(ka);
        String ttemp1 = ee.getHash(m_string, 512, ka_string);

        return null;

        //TODO ask the teacher what the decryption method is supposed to return. Also confirm the encryption method returns tuple (z,c,t) cuz that's confusing.
        //TODO test the shit out of this. I'm expecting there is an issue with my keccak impelemntation with byte[] conversion. To conform with the homework spec i should modify it to accept a byte array and work properly on that. Right now it takes a message string
        //TODO (continued) but the key is ambiguous. Is that a string of anything, or a stirng of hex numbers? Need to make sure that part is correct. If this doesn't work I bet the issue is that thing right there. If my keccack engine took byte arrays for input this code
        //TODO (continued) would be way cleaner.
    }
}
