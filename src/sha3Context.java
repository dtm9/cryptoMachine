import java.math.BigInteger;

/**
 * Instantiation of the context for sha3. Based on Dr Saarinen's tiny_sha3 on github
 * @author Dylan Miller
 * @author Markku-Juhani O. Saarinen <mjos@iki.fi>
 */
public class sha3Context {
    private BigInteger[] state;
    private int pt;
    private int rsiz;
    private int mdlen;

//TODO decide if i even need this
    public sha3Context() {
        state = new BigInteger[25];
    }
    public void setState(BigInteger[] theState) {this.state = theState;}
    public BigInteger[] getState() {return this.state;}
    public void setPt(int thePt) {this.pt = thePt;}
    public int getPt() {return this.pt;}
    public void setRsiz(int theRsiz) {this.rsiz = theRsiz;}
    public int getRsiz() {return this.rsiz;}
    public void setMdlen(int theMdlen) {this.mdlen = theMdlen;}
    public int getMdlen() {return this.mdlen;}
}
