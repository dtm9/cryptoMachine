import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Scanner;

/**
 * @author Dylan Miller
 * TCSS 487 Cryptography - Practical Project
 * @version Deliverable One
 * Written and tested using IntelliJ IDEA Community Edition 2018.1 on Ubuntu 17.10
 */

//TODO implement SHA3/KMACXOF256 see that one c implementation for help
//TODO bonus: let the user type text into the terminal app to encrypt instead of specifying a file.

public class Main {
    /** OS-independent method to add line breaks to String Builder object. */
    public static final String LINE_BREAK = System.getProperty("line.separator");

    /** StringBuilder object for re-use through the program anytime we need to print text. */
    private static StringBuilder mySB = new StringBuilder();

    /** Scanner object to take user input from keyboard for menus and manual message input. */
    private static Scanner myScanner = new Scanner(System.in);

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

                    System.out.println(selectedFile.toString());
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
}
