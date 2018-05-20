package extras;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Class with static methods to get Hexidecimal representation of bytestreams.
 * @author Dylan Miller
 */
public class HexTools {
    /** Static representation of the HEX digits. */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Calculates the hexidecimal representation of a byte array and returns a print friendly string of the hex.
     * @author Dylan Miller
     * @author Heavily inspired from github user Romus https://github.com/romus/sha/blob/master/sha3/src/main/java/com/theromus/utils/HexUtils.java
     * @param in the message to convert
     * @return print friendly string
     */
    public static String generateHexFromByteArray(byte[] in) {
        final int length = in.length;
        final char[] returnMe = new char[length << 1];

        for (int i = 0, j = 0; i < length; i++) {
            returnMe[j++] = DIGITS[(0xF0 & in[i]) >>> 4];
            returnMe[j++] = DIGITS[0x0F & in[i]];
        }

        return new String(returnMe);
    }

    /**
     * Calculates the hexidecimal representation of a byte array and returns a print friendly string of the hex backwards.
     * @author Dylan Miller
     * @author Heavily inspired from github user Romus https://github.com/romus/sha/blob/master/sha3/src/main/java/com/theromus/utils/HexUtils.java
     * @param in message to conver
     * @return reverse print friendly string
     */
    public static String generateReverseHexFromByteArray(byte[] in) {
        return generateHexFromByteArray(reverse(in));
    }

    /**
     * Helper function to reverse the hex string.
     * @author Dylan Miller
     * @author Heavily inspired from github user Romus https://github.com/romus/sha/blob/master/sha3/src/main/java/com/theromus/utils/HexUtils.java
     * @param reverseMe byte array to reverse
     * @return reversed bytes
     */
    private static byte[] reverse(byte[] reverseMe) {
        int i = 0;
        int j = reverseMe.length -1;
        byte tmp;

        while (j > i) {
            tmp = reverseMe[j];
            reverseMe[j] = reverseMe[i];
            reverseMe[i] = tmp;
            j--;
            i++;
        }

        return reverseMe;
    }

    /**
     * Concatenate one byte array with another.
     * @author Dylan Miller
     * @param a first array
     * @param b array to add on to the end
     * @return the concatenation of a and b
     */
    public static byte[] catArray(byte[] a, byte[] b) {
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Convert an ASCII string to a byte array.
     * Note: this was taken from the Main.java file provided for the SHAKE+KMACXOF implementation. I had to make it public to work from a different package. I didn't change anything else.
     * @author Paulo S. L. M. Barreto
     * @param s string to convert to a byte array
     * @return  s converted to a byte array (all characters must be in range [0..255])
     */
    public static byte[] asciiStringToByteArray(String s) {
        byte[] val = new byte[s.length()];
        for (int i = 0; i < val.length; i++) {
            char c = s.charAt(i);
            if (c >= 256) {
                throw new RuntimeException("Non-ASCII character found");
            }
            val[i] = (byte)c;
        }
        return val;
    }

    /**
     * Adds NIST padding to a byte array by extending to the next full block, appending a 1, then however many zeroes is necessary to fill the block.
     * @author Dylan Miller
     * @param array input array to be padded
     * @param blockSize blocksize to pad to
     * @return padded aray
     */
    public static byte[] addNISTPadding(byte[] array, int blockSize) {
        int difference = blockSize - (array.length % blockSize); //if array is correct size already difference will equal (blocksize - zero) so we append a full block

        //System.out.println("Padding:\nSource length: " + array.length + "\nBlocksize - array.length: " + difference + "\nNew Size: " + (array.length + difference));
        byte[] paddedArray = new byte[array.length + difference];

        for (int i = 0; i < array.length; i++) {
            paddedArray[i] = array[i];
        }

        if (difference == 1) { //case: only one char to pad
            paddedArray[paddedArray.length - 1] = (byte)0x01; //length - 1 to prevent off-by-one error
        } else { //case: append one and then loop zeroes onto the end until we reach the end
            paddedArray[array.length] = (byte)0x01; //array.length is the index number of the first new value
            for (int i = array.length +1; i < paddedArray.length; i++) {
                paddedArray[i] = (byte)0x00; //append zero in every index to the end of the new array for padding.
            }
        }
        //System.out.println("Actual size of paddedArray returned from padding method: " + paddedArray.length);
        return paddedArray;
    }

    /**
     * Removes NIST padding from a byte array by calculating the size of the padding by reading backwards all zeroes until it reaches the 1
     * that signifies the beginning of the padding. Then creates a new array of appropriate size and copys the contents to fit inside, thus leaving the padding behind.
     * @author Dylan Miller
     * @param array array to have padding removed
     * @return new unpadded array
     */
    public static byte[] stripNISTPadding(byte[] array) {
        boolean done = false;
        int i = array.length - 1;
        int paddingStartIndex = 0;

        while (!done) {
            if (array[i] == (byte)0x00) {
                i--;
            } else if (array[i] == (byte)0x01) {
                paddingStartIndex = i;
                done = true;
            } else {
                throw new RuntimeException("Input array is not NIST padded");
            }
        }

        byte[] unpaddedArray = new byte[paddingStartIndex];

        for (int j = 0; j < paddingStartIndex; j++) {
            unpaddedArray[j] = array[j];
        }

        return unpaddedArray;
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
}
