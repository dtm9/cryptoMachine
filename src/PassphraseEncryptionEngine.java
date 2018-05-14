import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static extras.HexTools.*;

public class PassphraseEncryptionEngine implements KeccakAttributes {
    //TODO IMPLEMENT NIST PADDING AND PADDING-STRIP

    private SecureRandom sr;

    private static byte[] EMPTY_MESSAGE = asciiStringToByteArray("");
    private static byte[] S = asciiStringToByteArray("S");

    /** Constructor that initializes the KMACXOF256 encryption and other utilities. */
    public PassphraseEncryptionEngine() {
        sr = new SecureRandom();
    }

    //TODO once we confirm this works, accept a byte array as message instead of a string so we can accept ANYTHING
    public SymmetricCryptogram encrypt(String plaintext, String passphrase) {

        System.out.println("encryption started!");
        //get z: z <-- Random(512)
        byte[] z = new byte[512];
        sr.nextBytes(z);
        System.out.println("z generated");

        //get ke and ka: (ke || ka) <-- KMACXOF256(z || pw, "", 1024, "S")
        byte[] keka_key = catArray(z, passphrase.getBytes());

        byte[] keka = SHAKE.KMACXOF256(keka_key, EMPTY_MESSAGE, 1024, S);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);



        //get c: c <-- KMACXOF256(ke, "", |m|, "SKE") xor m
        //TODO consider xor'ing the byte arrays as byte arrays. Might not be any prettier/better than converting them into BigIntegers though.
        byte[] ctmp_string = asciiStringToByteArray("SKE");
        int c_length = raiseToMultipleOf8(plaintext.length());
        byte[] ctmp = SHAKE.KMACXOF256(ke, EMPTY_MESSAGE, c_length, ctmp_string);
        String ctmp_hexstring = generateHexFromByteArray(ctmp);
        BigInteger ctmp_bigint = new BigInteger(ctmp_hexstring, 16);
        String ctmp_m_hexstring = generateHexFromByteArray(plaintext.getBytes());
        BigInteger ctmp_m_bigint = new BigInteger(ctmp_m_hexstring, 16);
        BigInteger c_bigint = ctmp_bigint.xor(ctmp_m_bigint);
        byte[] c = c_bigint.toByteArray();

        //get t: t <-- KMACXOF256(ka, m, 512, "SKA")
        byte[] t_string = asciiStringToByteArray("SKA");
        byte[] t = SHAKE.KMACXOF256(ka, passphrase.getBytes(), 512, t_string);

        return new SymmetricCryptogram(z, c ,t);
    }

    public byte[] decrypt(SymmetricCryptogram itemToDecrypt, String passphrase) {
        //get keka: (ke || ka) <-- KMACXOF256(z || pw, “”, 1024, “S”)
        byte[] keka_key = catArray(itemToDecrypt.getZ(), passphrase.getBytes());
        byte[] keka = SHAKE.KMACXOF256(keka_key, EMPTY_MESSAGE, 1024, S);

        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        //get m: m <-- KMACXOF256(ke, “”, |c|, “SKE”) xor c
        byte[] m_string = asciiStringToByteArray("SKE");
        int m_length = raiseToMultipleOf8(itemToDecrypt.getC().length);
        byte[] mtmp = SHAKE.KMACXOF256(ke, EMPTY_MESSAGE, m_length, m_string);
        String mtmp_hexstring = generateHexFromByteArray(mtmp);
        BigInteger mtmp_bigint = new BigInteger(mtmp_hexstring, 16);
        String mtmp_c_hexstring = generateHexFromByteArray(itemToDecrypt.getC());
        BigInteger mtmp_c_bigint = new BigInteger(mtmp_c_hexstring, 16);
        BigInteger m_bigint = mtmp_bigint.xor(mtmp_c_bigint);
        byte[] m = m_bigint.toByteArray();

        //get t': t' <-- KMACXOF256(ka, m, 512, “SKA”)
        byte[] tprime_string = asciiStringToByteArray("SKA");
        byte[] tprime = SHAKE.KMACXOF256(ka, m, 512, tprime_string);

        //validate t' = t
        String thex = generateHexFromByteArray(itemToDecrypt.getT());
        String tprimehex = generateHexFromByteArray(tprime);
        if (tprimehex.equals(thex)) {
            System.out.println("They match!");
        } else {
            System.out.println("They don't match!\nT': " + tprimehex + "\nT: " + thex);
        }

        return m;
    }

    /*
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
    */


}
