import java.math.BigInteger;

/**
 * Basic interface that implements all of the global contstants required for a Keccak implementation.
 * Based on Dr. Saarinen's tiny_sha3 implementation from github.
 * https://github.com/mjosaarinen/tiny_sha3
 * @author Dylan Miller
 * @author Markku-Juhani O. Saarinen <mjos@iki.fi>
 */
public interface KeccakAttributes {


    /** Number of Keccak rounds. */
    int ROUND_NUM = 24;

    /** The Keccak rounds as BigInteger objects. */
    BigInteger[] ROUND_CONSTANTS = new BigInteger[] {
            new BigInteger("0000000000000001", 16),
            new BigInteger("0000000000008082", 16),
            new BigInteger("800000000000808a", 16),
            new BigInteger("8000000080008000", 16),
            new BigInteger("000000000000808b", 16),
            new BigInteger("0000000080000001", 16),
            new BigInteger("8000000080008081", 16),
            new BigInteger("8000000000008009", 16),
            new BigInteger("000000000000008a", 16),
            new BigInteger("0000000000000088", 16),
            new BigInteger("0000000080008009", 16),
            new BigInteger("000000008000000a", 16),
            new BigInteger("000000008000808b", 16),
            new BigInteger("800000000000008b", 16),
            new BigInteger("8000000000008089", 16),
            new BigInteger("8000000000008003", 16),
            new BigInteger("8000000000008002", 16),
            new BigInteger("8000000000000080", 16),
            new BigInteger("000000000000800a", 16),
            new BigInteger("800000008000000a", 16),
            new BigInteger("8000000080008081", 16),
            new BigInteger("8000000000008080", 16),
            new BigInteger("0000000080000001", 16),
            new BigInteger("8000000080008008", 16),
    };

    /** Largest unsigned long possible as a BigInteger. */
    BigInteger MAX_ULONG = new BigInteger("18446744073709551615");

    int[] keccakf_rotc = new int[] {
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
    };

    int[] keccakf_piln = new int[] {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    };

    // 2d array of keccak rotation offsets
    int[][] rotations = new int[][] {
            {0, 36, 3, 41, 18},
            {1, 44, 10, 45, 2},
            {62, 6, 43, 15, 61},
            {28, 55, 25, 21, 56},
            {27, 20, 39, 8, 14}
    };


}
