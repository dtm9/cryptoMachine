import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


import static extras.HexTools.asciiStringToByteArray;
import static extras.HexTools.generateHexFromByteArray;

//TODO byte arrays go in, byte arrays come out. Replace my Encryption Machine from part 1 with SHAKE.java's API. It's for the better.

//TODO backtrack and fix PassphraseEncryptionEngine to use SHAKE instead of my code, then do part 3 and 4 ASAP. In one day if possible

/**
 * @author Dylan Miller
 * TCSS 487 Cryptography - Practical Project
 * @version Deliverable Two
 * Written and tested using IntelliJ IDEA Community Edition 2018.1 on Ubuntu 17.10
 * For a one point penalty, this submission implements the SHAKE.java class that was provided in class to perform Keccak KMACXOF256 hashing.
 * my original KMACXOF256 implementation from Deliverable One did not accept strings 256 in length or higher due to an overflow bug, and it
 * wasn't worth going back to fix it. See comments in SHAKE.java for proper citation and attribution.
 */

public class Main implements KeccakAttributes {
    /** OS-independent method to add line breaks to String Builder object. */
    private static final String LINE_BREAK = System.getProperty("line.separator");

    /** StringBuilder object for re-use through the program anytime we need to print text. */
    private static StringBuilder mySB = new StringBuilder();

    /** Scanner object to take user input from keyboard for menus and manual message input. */
    private static Scanner myScanner = new Scanner(System.in);


    public static void main(final String[] theArgs) {
        boolean done = false;
        KMACXOF256EncryptionEngine ee = new KMACXOF256EncryptionEngine();
        //PassphraseEncryptionEngine pe = new PassphraseEncryptionEngine();

        while (!done) {
            printMainMenu();

            byte[] S; //diversification string
            byte[] K; //key
            byte[] M; //message

            int theChoice = myScanner.nextInt();

            switch (theChoice) {
                case 1: //compute hash

                    myScanner.nextLine(); //no assignment to sanitize the scanner

                    printMenu1();
                    int secondChoice = myScanner.nextInt();
                    switch (secondChoice) {
                        case 1: //choose file
                            S = asciiStringToByteArray("D"); //D is the diversification string for this action.
                            K = asciiStringToByteArray(""); //K is the key which we are not using for this so it is empty.

                            JFileChooser myChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                            int retValue = myChooser.showOpenDialog(null);
                            File selectedFile = null;

                            if (retValue == JFileChooser.APPROVE_OPTION) {
                                selectedFile = myChooser.getSelectedFile();
                                Path path = Paths.get(selectedFile.getAbsolutePath());
                                try {
                                    M = Files.readAllBytes(path);

                                    byte[] message = SHAKE.KMACXOF256(K, M, 512, S);
                                    System.out.println("SHA3 result: " + generateHexFromByteArray(message));
                                } catch (Exception e) { e.printStackTrace(); }



                            }
                            break;

                        case 2: //type message

                            System.out.println("Type your message:");
                            System.out.println();
                            myScanner.nextLine(); //no assignment to sanitize the scanner

                            String rawInput = myScanner.nextLine();
                            S = asciiStringToByteArray("D"); //D is the diversification string for this action.
                            K = asciiStringToByteArray(""); //K is the key which we are not using for this so it is empty.

                            M = rawInput.getBytes();

                            byte[] message = SHAKE.KMACXOF256(K, M, 512, S);
                            System.out.println("SHA3 result: " + generateHexFromByteArray(message));
                            break;

                        case 3: //test vector
                            System.out.println("NIST test vector: KMACXOF Sample #4 https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/KMACXOF_samples.pdf");
                            byte[] data_test = {(byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03};
                            byte[] key_test = {(byte)0x40, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47,
                                               (byte)0x48, (byte)0x49, (byte)0x4A, (byte)0x4B, (byte)0x4C, (byte)0x4D, (byte)0x4E, (byte)0x4F,
                                               (byte)0x50, (byte)0x51, (byte)0x52, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57,
                                               (byte)0x58, (byte)0x59, (byte)0x5A, (byte)0x5B, (byte)0x5C, (byte)0x5D, (byte)0x5E, (byte)0x5F};
                            byte[] string_test = asciiStringToByteArray("My Tagged Application");
                            int length_test = 512;

                            System.out.println("NIST test 1: " + data_test);
                            System.out.println("\n\nKey: " + key_test);
                            System.out.println("\n\nString: " + string_test);

                            byte[] test_result = SHAKE.KMACXOF256(key_test, data_test, length_test, string_test);
                            System.out.println("Result: " + generateHexFromByteArray(test_result));

                    }

                    break;

                case 2: //symmetric encrypt

                    System.out.println("Type your message:");
                    System.out.println();
                    myScanner.nextLine(); //no assignment to sanitize the scanner

                    String rawInput = myScanner.nextLine();
                    myScanner.nextLine(); //no assignment to sanitize the scanner

                    System.out.println("Type your passphrase:");
                    System.out.println();
                    myScanner.nextLine(); //no assignment to sanitize the scanner

                    String rawPassword = myScanner.nextLine();

                    System.out.println("proceeding with message/pass: " + rawInput + " " + rawPassword);
                    PassphraseEncryptionEngine pe = new PassphraseEncryptionEngine();

                    SymmetricCryptogram cipherobj = pe.encrypt(rawInput, rawPassword);

                    System.out.println("object created with secrets");

                    System.out.println("\nWe will re-use the password variable to test");

                    byte[] secret = pe.decrypt(cipherobj, rawPassword);

                    System.out.println("original message as byte[]: " + rawInput.getBytes() + "\ndecrypted: " + secret);


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
        mySB.append("1) Encrypt something with SHA3");
        mySB.append(LINE_BREAK);
        mySB.append("2) Encrypt file symmetrically with passphrase");
        mySB.append(LINE_BREAK);
        mySB.append("3) Exit Program");
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        System.out.print(mySB.toString());
        System.out.print("Enter a command: ");
        mySB.delete(0, mySB.capacity());
    }

    private static void printMenu1() {
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        mySB.append("1) Encrypt a file with SHA3");
        mySB.append(LINE_BREAK);
        mySB.append("2) Type a new message to hash using SHA3");
        mySB.append(LINE_BREAK);
        mySB.append("3) Run Test Vector");
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        mySB.append("4) Exit");
        mySB.append(LINE_BREAK);
        System.out.print(mySB.toString());
        System.out.print("Enter a command: ");
        mySB.delete(0, mySB.capacity());
    }
}
