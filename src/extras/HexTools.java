package extras;

/**
 * Class with static methods to get Hexidecimal representation of bytestreams.
 * @author Heavily inspired from github user Romus https://github.com/romus/sha/blob/master/sha3/src/main/java/com/theromus/utils/HexUtils.java
 * @author Dylan Miller
 */
public class HexTools {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String generateHexFromByteArray(byte[] in) {
        final int length = in.length;
        final char[] returnMe = new char[length << 1];

        for (int i = 0, j = 0; i < length; i++) {
            returnMe[j++] = DIGITS[(0xF0 & in[i]) >>> 4];
            returnMe[j++] = DIGITS[0x0F & in[i]];
        }

        return new String(returnMe);
    }

    public static String generateReverseHexFromByteArray(byte[] in) {
        return generateHexFromByteArray(reverse(in));
    }

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
}
