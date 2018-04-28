import java.math.BigInteger;

import static extras.HexTools.generateHexFromByteArray;
import static extras.HexTools.generateReverseHexFromByteArray;

/**
 * This class is where the Keccak algorithm will be implemented. It was made with heavy inspiration from two
 * github projects. First tiny_sha3 from Dr. Markku Saarinen and sha3 from github user Romus.
 * @author Dylan Miller
 * @author Dr. Markku-Juhani O. Saarinen <mjos@iki.fi>
 * @author Romus <https://github.com/romus>
 */
public class KMACXOF256EncryptionEngine implements KeccakAttributes {


    private int w;
    private int n;



    public KMACXOF256EncryptionEngine() {
        init();
    }

    /**
     * Compute a suqre root of v mod p with a specified
     * least significant bit, if such a root exists
     * @param v the radicand.
     * @param p the modulus (must satisfy p mod 4 = 3)
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
     * if such a root exists, otherwise return null.
     * @author this method was provided in the assignment handout.
     */
    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }

    private void init() {
        w = PERMUTATION_WIDTH / 25;
        int l = (int) (Math.log(w) / Math.log(2));
        n = 12 + 2 * l;
    }

    private void initArray(BigInteger[] fillMe) {
        for (int i = 0; i < fillMe.length; i++) {
            fillMe[i] = new BigInteger("0", 16);
        }
    }

    private String addZero(String fixMe, int length) {
        String returnMe = fixMe;
        for (int i = 0; i < length - fixMe.length(); i++) {
            returnMe += "0";
        }
        return returnMe;
    }

    private BigInteger[][] addPadding(String message) {
        int size;
        message = message + SHAKE256_D;

        while (((message.length() / 2) * 8 % SHAKE256_ROTATION) != SHAKE256_ROTATION - 8) {
            message = message + "00";
        }

        message = message + "80";
        size = (((message.length() / 2) * 8) / SHAKE256_ROTATION);

        BigInteger[][] messageArray = new BigInteger[size][];
        messageArray[0] = new BigInteger[PERMUTATION_WIDTH / w];
        initArray(messageArray[0]);

        int count = 0;
        int j = 0;
        int i = 0;

        for (int _n = 0; _n < message.length(); _n++) {
            if (j > SHAKE256_ROTATION / w - 1) {
                j = 0;
                i++;
                messageArray[i] = new BigInteger[PERMUTATION_WIDTH / w];
                initArray(messageArray[i]);
            }

            count++;

            if ((count * 4 % w) == 0) {
                String subString = message.substring((count - w / 4), (w / 4) + (count - w / 4));
                messageArray[i][j] = new BigInteger(subString, 16);
                String revertString = generateReverseHexFromByteArray(messageArray[i][j].toByteArray());
                revertString = addZero(revertString, subString.length());
                messageArray[i][j] = new BigInteger(revertString, 16);
                j++;
            }
        }

        return messageArray;
    }

    private BigInteger getShiftLeft64(BigInteger value, int shift) {
        BigInteger returnMe = value.shiftLeft(shift);
        BigInteger temp = value.shiftLeft(shift);

        if (returnMe.compareTo(MAX_ULONG) > 0) {
            for (int i = 64; i < 64 + shift; i ++) {
                temp = temp.clearBit(i);
            }

            temp = temp.setBit(64 + shift);
            returnMe = temp.and(returnMe);
        }

        return returnMe;
    }

    private BigInteger rotate(BigInteger x, int n) {
        n = n % w;

        BigInteger leftShift = getShiftLeft64(x, n);
        BigInteger rightShift = x.shiftRight(w - n);

        return leftShift.or(rightShift);
    }

    /**
     * This is the function to be called from the menu that will actually encrypt the message or file. It will call the below Keccak functions to perform the encryption.
     * cSHAKE in NIST.SP.800-185 takes 4 arguments, the N and S args are to enable users of the implementation to pick a variant of cSHAKE and in this implementation they do not have a choice.
     * @param messageToEncrypt X per NIST.SP.800-185 the input bit string. This can be any length including zero. Cannot be null
     * @param outputLength L per NIST.SP.800-185 the output hash length.
     * @param key K per NIST.SP.800-185 user specified key. Can be empty.
     */
    public String getHash(String messageToEncrypt, int outputLength, String key) {
        //init Phase

        //NIST newX creation to pass into cSHAKE
        String hexKey = generateHexFromByteArray(key.getBytes());
        String kmac_zero = generateHexFromByteArray(KMAC_ZEROLENGTH_ENCODING.toByteArray());
        messageToEncrypt = hexKey + messageToEncrypt + kmac_zero;

        //squeeze array initialization
        BigInteger[][] S = new BigInteger[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                S[i][j] = new BigInteger("0", 16);
            }
        }

        //padding array
        BigInteger[][] P = addPadding(messageToEncrypt);

        //Absorbing Phase

        for (BigInteger[] Pi : P) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if ((i + j * 5) < SHAKE256_ROTATION / w) {
                        S[i][j] = S[i][j].xor(Pi[i + j * 5]);
                    }
                }
            }
            keccackf(S);
        }

        //Squeezing Phase

        String Z = "";

        do {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if ((5 * i + j) < SHAKE256_ROTATION / w) {
                        Z = Z + addZero(generateReverseHexFromByteArray(S[j][i].toByteArray()), 16).substring(0, 16);
                    }
                }
            }

        } while (Z.length() < outputLength);

        return Z.substring(0, outputLength * 2);
    }

    /**
     * This function iterates the round constants and invokes the roundB method that performs the Keccak shifting.
     */
    private BigInteger[][] keccackf(BigInteger[][] A) {
        for (int i = 0; i < n; i++) {
            A = roundB(A, ROUND_CONSTANTS[i]);
        }

        return A;
    }

    /**
     * This method performs several of the Keccak rotations. This is the meat and potatoes of the algorithm.
     */
    private BigInteger[][] roundB(BigInteger[][] A, BigInteger RCrow) {
        BigInteger[] C = new BigInteger[5];
        BigInteger[] D = new BigInteger[5];
        BigInteger[][] B = new BigInteger[5][5];

        //Step 0

        for (int i = 0; i < 5; i++) {
            C[i] = A[i][0].xor(A[i][1]).xor(A[i][2]).xor(A[i][3]).xor(A[i][4]);
        }

        for (int i = 0; i < 5; i++) {
            D[i] = C[(i + 4) % 5].xor(rotate(C[(i + 1) % 5], 1));
        }


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                A[i][j] = A[i][j].xor(D[i]);
            }
        }

        //Step ρ and π
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                B[j][(2 * i + 3 * j) % 5] = rotate(A[i][j], rotations[i][j]);
            }
        }


        //Step X
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                A[i][j] = B[i][j].xor(B[(i + 1) % 5][j].not().and(B[(i + 2) % 5][j]));
            }
        }

        //Step ι
        A[0][0] = A[0][0].xor(RCrow);

        return A;
    }
}
