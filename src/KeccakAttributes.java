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
    int KECCAKF_ROUNDS = 24;

    /** String representation of each Keccak round in Hex. Use this to make array of BigIntegers. */
    String keccakf_rndc_strings[] = {
            "0000000000000001",
            "0000000000008082",
            "800000000000808a",
            "8000000080008000",
            "000000000000808b",
            "0000000080000001",
            "8000000080008081",
            "8000000000008009",
            "000000000000008a",
            "0000000000000088",
            "0000000080008009",
            "000000008000000a",
            "000000008000808b",
            "800000000000008b",
            "8000000000008089",
            "8000000000008003",
            "8000000000008002",
            "8000000000000080",
            "000000000000800a",
            "800000008000000a",
            "8000000080008081",
            "8000000000008080",
            "0000000080000001",
            "8000000080008008"
    };

    int[] keccakf_rotc = new int[] {
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
    };

    int[] keccakf_piln = new int[] {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    };
}
