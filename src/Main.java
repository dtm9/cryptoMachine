import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static extras.HexTools.generateHexFromByteArray;



/**
 * @author Dylan Miller
 * TCSS 487 Cryptography - Practical Project
 * @version Deliverable One
 * Written and tested using IntelliJ IDEA Community Edition 2018.1 on Ubuntu 17.10
 * This code depends on Java8 or newer, as in Java8 the Long object was updated to support unsigned Integers.
 * @attribution This Keccack implementation was inspired by Dr Markku Saarinen and github user Romus. Several features from both of their
 * sha3 implementations on github were re-implemented in this code.
 * You can see them here: https://github.com/mjosaarinen/tiny_sha3 and https://github.com/romus/sha
 *
 * At this stage of the class there is still much about SHA3/Keccack that I don't yet understand. I hope to refactor more of this code as the project continues
 * to be more original than it already is.
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
            int theChoice = myScanner.nextInt();

            switch (theChoice) {
                case 1: //file chooser

                    myScanner.nextLine(); //no assignment to sanitize the scanner

                    printMenu1();
                    int secondChoice = myScanner.nextInt();
                    switch (secondChoice) {
                        case 1: //choose file
                            JFileChooser myChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                            int retValue = myChooser.showOpenDialog(null);
                            File selectedFile = null;

                            if (retValue == JFileChooser.APPROVE_OPTION) {
                                selectedFile = myChooser.getSelectedFile();
                                Path path = Paths.get(selectedFile.getAbsolutePath());
                                try {
                                    byte[] data = Files.readAllBytes(path);
                                    String messageToEncrypt = generateHexFromByteArray(data);
                                    System.out.println("SHA3 result: " + ee.getHash(messageToEncrypt, 128, ""));
                                } catch (Exception e) { e.printStackTrace(); }



                            }
                            break;

                        case 2: //type message
                            System.out.println("Type your message:");
                            System.out.println();
                            myScanner.nextLine(); //no assignment to sanitize the scanner

                            String rawInput = myScanner.nextLine();
                            String messageToEncrypt = generateHexFromByteArray(rawInput.getBytes());

                            System.out.println("SHA3 result: " + ee.getHash(messageToEncrypt, 128, ""));
                            break;
                    }

                    break;

                case 2: //symmetric encrypt

                    //TODO mirror ee's getHash arguments as pe will need an instantiation of ee.


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
        mySB.append("3) Exit Program");
        mySB.append(LINE_BREAK);
        mySB.append(LINE_BREAK);
        System.out.print(mySB.toString());
        System.out.print("Enter a command: ");
        mySB.delete(0, mySB.capacity());
    }
}
