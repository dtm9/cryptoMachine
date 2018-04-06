import java.math.BigInteger;

/**
 * Instantiation of the context for sha3. Based on Dr Saarinen's tiny_sha3 on github
 * @author Dylan Miller
 * @author Markku-Juhani O. Saarinen <mjos@iki.fi>
 */
public class sha3Context {
    private BigInteger state;
    private BigInteger pt;
    private BigInteger rsiz;
    private BigInteger mdlen;

//TODO decide if i even need this
    public sha3Context() {}
    public void setState(BigInteger theState) {this.state = theState;}
    public BigInteger getState() {return this.state;}
    public void setPt(BigInteger thePt) {this.pt = thePt;}
    public BigInteger getPt() {return this.pt;}
    public void setRsiz(BigInteger theRsiz) {this.rsiz = theRsiz;}
    public BigInteger getRsiz() {return this.rsiz;}
    public void setMdlen(BigInteger theMdlen) {this.mdlen = theMdlen;}
    public BigInteger getMdlen() {return this.mdlen;}
}
