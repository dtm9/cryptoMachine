import java.io.Serializable;

public class Signature implements Serializable {
    private final byte[] h;
    private final byte[] z;

    public Signature(byte[] h, byte[] z) {
        this.h = h;
        this.z = z;
    }

    public byte[] getH() {
        return h;
    }

    public byte[] getZ() {
        return z;
    }
}
