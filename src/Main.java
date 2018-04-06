import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import static extras.HexTools.generateReverseHexFromByteArray;


/**
 * @author Dylan Miller
 * TCSS 487 Cryptography - Practical Project
 * @version Deliverable One
 * Written and tested using IntelliJ IDEA Community Edition 2018.1 on Ubuntu 17.10
 * This code depends on Java8 or newer, as in Java8 the Long object was updated to support unsigned Integers.
 */

public class Main implements KeccakAttributes {
    /** OS-independent method to add line breaks to String Builder object. */
    private static final String LINE_BREAK = System.getProperty("line.separator");

    /** StringBuilder object for re-use through the program anytime we need to print text. */
    private static StringBuilder mySB = new StringBuilder();

    /** Scanner object to take user input from keyboard for menus and manual message input. */
    private static Scanner myScanner = new Scanner(System.in);

    /** Standard length for SHA3 512 in FIPS 202. */
    private static final int SHA3_512_OUTPUT_LENGTH = 64;

    private int w;

    private int n;

    private static final int PERMUTATION_WIDTH = 1600;


    public static void main(final String[] theArgs) {
        boolean done = false;

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

    /* My attempt at Saarinen's code
    private static String sha3(File theInputFile) {

        sha3Context ctx = new sha3Context();
        //BigInteger md = new BigInteger(null);

        sha3InitializeContext(ctx, 0);
        sha3Update(ctx, theInputFile);
        sha3Final(ctx);

        return null;
    }

    private static void sha3InitializeContext(sha3Context ctx, int mdlen) {
        ctx.setMdlen(mdlen);
        ctx.setRsiz(200 - 2 * mdlen);
        ctx.setPt(0);

        BigInteger[] theState = ctx.getState();

        for (int i = 0; i < 25; i++) {
            theState[i] = BigInteger.ZERO;
        }

        ctx.setState(theState);
    }

    private static sha3Context sha3Update(sha3Context ctx, File theInputFile) {
        byte[] data;

        try {
            data = Files.readAllBytes(theInputFile.toPath());
            int len = data.length;

            BigInteger databi = new BigInteger(1, data);
            BigInteger[] theState = ctx.getState();
            int j = ctx.getPt();

            for (int i = 0; i < len; i++) {
                theState[j++].xor()
            }
        } catch (Exception e) { e.printStackTrace(); }



        return null;
    }

    private static String sha3Final(sha3Context ctx) {
        return null;
    }

    private static long ROTL64(long x, int y) {
        //(((x) << (y)) | ((x) >> (64 - (y))));

        //long wrapMe = (x.longValue() << y) | (x.longValue() >> (64 - (y)));
        //Long returnMe = new Long(wrapMe);
        long returnMe = ((x << y) | (x >> (64 - (y))));

        return returnMe;
    }
 */
    private void init() {
        //TODO take arguments instead of the static width for more functionality.
        w = PERMUTATION_WIDTH / 25;
        int l = (int) (Math.log(w) / Math.log(2));
        n = 12 + 2 * 1;
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
        message = message + "06";

        while (((message.length() / 2) * 8 % 576) != 576 - 8) { //todo figure out if this is the right number. its either 576 or 64
            message = message + "00";
        }

        message = message + "80";
        size = (((message.length() / 2) * 8) / 576); //todo again this might be 64 not 576

        BigInteger[][] messageArray = new BigInteger[size][];
        messageArray[0] = new BigInteger[1600 / w];
        initArray(messageArray[0]);

        int count = 0;
        int j = 0;
        int i = 0;

        for (int _n = 0; _n < message.length(); _n++) {
            if (j > 576 / w - 1) {
                j = 0;
                i++;
                messageArray[i] = new BigInteger[1600 / w];
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
}
