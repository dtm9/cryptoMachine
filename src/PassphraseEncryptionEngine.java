import static extras.HexTools.generateHexFromByteArray;

public class PassphraseEncryptionEngine implements KeccakAttributes {

    /**
        Empty constructor as this is for static methods.
     */
    public PassphraseEncryptionEngine() {}

    public String encrypt(String plaintext, int outputLength, String key) {

        //NIST newX creation to pass into cSHAKE
        String hexKey = generateHexFromByteArray(key.getBytes());
        String kmac_zero = generateHexFromByteArray(KMAC_ZEROLENGTH_ENCODING.toByteArray());
        plaintext = hexKey + plaintext + kmac_zero;

        //TODO check slides and notes for block ciphers and modes of operation

        //TODO i can implement any cipher I want really. I have a working hash algorithm with KMAC

        //TODO maybe one-time pad or something similar. Going to do lots of XOR'ing and the hash algorithm makes it easy

        return null;
    }


}
