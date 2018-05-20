import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static extras.HexTools.*;

public class PassphraseEncryptionEngine implements KeccakAttributes {
//TODO uncomment the print statements to demonstrate SHAKE.java producing wrong hash length on big files
    private SecureRandom sr;

    private static byte[] EMPTY_MESSAGE = asciiStringToByteArray("");
    private static byte[] S = asciiStringToByteArray("S");

    /** Constructor that initializes the KMACXOF256 encryption and other utilities. */
    public PassphraseEncryptionEngine() {
        sr = new SecureRandom();
    }

    public SymmetricCryptogram encrypt(byte[] plaintext, String passphrase) {
        //get z: z <-- Random(512)
        byte[] z = new byte[512];
        sr.nextBytes(z);

        //get ke and ka: (ke || ka) <-- KMACXOF256(z || pw, "", 1024, "S")
        byte[] keka_key = catArray(z, passphrase.getBytes());
        byte[] keka = SHAKE.KMACXOF256(keka_key, EMPTY_MESSAGE, 1024, S);
        byte[] ke = Arrays.copyOfRange(keka, 0, keka.length/2);
        byte[] ka = Arrays.copyOfRange(keka, keka.length/2, keka.length);

        //get c: c <-- KMACXOF256(ke, "", |m|, "SKE") xor m
        byte[] ctmp_string = asciiStringToByteArray("SKE");
        byte[] padded_message = addNISTPadding(plaintext, 8);
        //System.out.println("padded message length after method call: " + padded_message.length);
        byte[] ctmp = SHAKE.KMACXOF256(ke, EMPTY_MESSAGE, padded_message.length, ctmp_string);
        //System.out.println("size of ctmp: " + ctmp.length);
        String ctmp_hexstring = generateHexFromByteArray(ctmp);
        BigInteger ctmp_bigint = new BigInteger(ctmp_hexstring, 16);
        String ctmp_m_hexstring = generateHexFromByteArray(padded_message);
        BigInteger ctmp_m_bigint = new BigInteger(ctmp_m_hexstring, 16);
        BigInteger c_bigint = ctmp_bigint.xor(ctmp_m_bigint);
        byte[] c = c_bigint.toByteArray();

        //get t: t <-- KMACXOF256(ka, m, 512, "SKA")
        byte[] t_string = asciiStringToByteArray("SKA");
        byte[] t = SHAKE.KMACXOF256(ka, padded_message, 512, t_string);

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

        //System.out.println("C length: " + itemToDecrypt.getC().length);
        byte[] mtmp = SHAKE.KMACXOF256(ke, EMPTY_MESSAGE, itemToDecrypt.getC().length, m_string);
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
        if (!tprimehex.equals(thex)) {
            throw new RuntimeException("Bad password");
        }

        //remove padding from M
        byte[] originalM = stripNISTPadding(m);

        return originalM;
    }
}
