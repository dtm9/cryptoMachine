import java.io.Serializable;

public class AsymmetricCryptogram implements Serializable {
    private final ECCEngine.CoordinatePair Z;
    private final byte[] c;
    private final byte[] t;

    public AsymmetricCryptogram(ECCEngine.CoordinatePair z, byte[] c, byte[] t) {
        this.Z = z;
        this.c = c;
        this.t = t;
    }

    public ECCEngine.CoordinatePair getZ() {
        return Z;
    }

    public byte[] getC() {
        return c;
    }

    public byte[] getT() {
        return t;
    }
}
