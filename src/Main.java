import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * @author Dylan Miller
 * TCSS 487 Cryptography - Practical Project
 * @version Deliverable One
 * Written and tested using IntelliJ IDEA Community Edition 2018.1 on Ubuntu 17.10
 */

//TODO implement SHA3/KMACXOF256 see that one c implementation for help
//TODO bonus: let the user type text into the terminal app to encrypt instead of specifying a file.

public class Main implements KeccakAttributes {
    /** OS-independent method to add line breaks to String Builder object. */
    public static final String LINE_BREAK = System.getProperty("line.separator");

    /** StringBuilder object for re-use through the program anytime we need to print text. */
    private static StringBuilder mySB = new StringBuilder();

    /** Scanner object to take user input from keyboard for menus and manual message input. */
    private static Scanner myScanner = new Scanner(System.in);

    /** BigInteger array for the keccak rounds. Needs to be initialized before use. */
    private static BigInteger[] keccakf_rdnc = new BigInteger[KECCAKF_ROUNDS];

    public static void main(final String[] theArgs) {
        boolean done = false;
        initKeccakRounds();

        while (!done) {
            printMainMenu();
            int theChoice = myScanner.nextInt();

            switch (theChoice) {
                case 1: //file chooser
                    System.out.println("file chooser!");
                    JFileChooser myChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                    int retValue = myChooser.showOpenDialog(null);
                    File selectedFile = null;

                    if (retValue == JFileChooser.APPROVE_OPTION) {
                        selectedFile = myChooser.getSelectedFile();


                    }

                    break;

                case 2: //new message to encode
                    System.out.println("new message!");
                    break;

                case 3: //exit
                    done = true;
                    break;
            }
        }
    }

    /**
     * Prints the main menu using the StringBuilder.
     * @author Dylan Miller
     */
    private static void printMainMenu() {
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        mySB.append("----Main Menu----");
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        mySB.append("1) Choose a file to hash using SHA3");
        mySB.append(LINE_BREAK);
        mySB.append("2) Type a new message to hash using SHA3");
        mySB.append(LINE_BREAK);
        mySB.append("3) Exit Program");
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        System.out.print(mySB.toString());
        System.out.print("Enter a command: ");
        mySB.delete(0, mySB.capacity());
    }

    /**
     * Compute a suqre root of v mod p with a specified
     * least significant bit, if such a root exists
     * @param v the radicand.
     * @param p the modulus (must satisfy p mod 4 = 3)
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
     * if such a root exists, otherwise return null.
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

    private static String sha3(File theInputFile) {
//TODO read email, I just sent the teacher a message about the example code to follow.
//If that doesn't help, implement the sample code one component at a time and just hope it works.
        /*
        sha3Context ctx = new sha3Context();
        //BigInteger md = new BigInteger(null);

        sha3InitializeContext();
        sha3Update();
        sha3Final();
        */
        return null;
    }

    private static void initKeccakRounds() {
        for (int i =0; i < KECCAKF_ROUNDS; i++) {
            keccakf_rdnc[i] = new BigInteger(keccakf_rndc_strings[i], 16);
        }
    }

    private static void ROTL64(BigInteger x, BigInteger y) {
        //TODO figure out what params ROTL64 are used on to get the datatypes right
        //TODO maybe look into examples of bit shifting on BigIntegers?
        //(((x) << (y)) | ((x) >> (64 - (y))));
    }

    private static void convertToLittleEndian() {
        //TODO next time i open this code, start here
    }
}
