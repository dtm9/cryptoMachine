import java.math.BigInteger;
import static extras.HexTools.*;

public class ECCEngine {

    private BigInteger r;

    /** A Mersenne Prime Number.*/
    private BigInteger p;

    /** The Edwards curve. */
    private BigInteger E521;

    /** The point O on the curve. */
    public final CoordinatePair NEUTRAL = new CoordinatePair(new BigInteger("0"), new BigInteger("1"));

    private CoordinatePair G;

    public ECCEngine() {
        //initialize r where r = 2^519 - 337554763258501705789107630418782636071904961214051226618635150085779108655765
        r = new BigInteger("2");
        r.pow(519);
        r = r.subtract(new BigInteger("337554763258501705789107630418782636071904961214051226618635150085779108655765"));

        //initialize p, a Mersenne prime number p := 2^521 - 1
        p = new BigInteger("2");
        p.pow(521);
        p = p.subtract(new BigInteger("1"));

        //initialize G, G := (x,y), a point on E521 with x = 18 and y = an even number
        //TODO figure out how to initialize this as a global parameter. Also why is x = 18?
        G = new CoordinatePair(new BigInteger("18"), BigInteger.ZERO); //TODO make Y not zero, need to figure out what Y is supposed to be and when.
    }

    public void generateKeyPair(String passphrase) {
        //generate s: s <-- KMACXOF256(pw, "", 512, "K"); s <-- 4s
        byte[] pw = asciiStringToByteArray(passphrase);
        byte[] K_string = asciiStringToByteArray("K");
        byte[] emptyString = asciiStringToByteArray("");
        byte[] s = SHAKE.KMACXOF256(pw, emptyString, 512, K_string);

        String hex_s = generateHexFromByteArray(s);
        BigInteger bigint_s = new BigInteger(hex_s, 16);
        bigint_s.multiply(new BigInteger("4"));
        s = bigint_s.toByteArray();

        //get V: V <-- s * G
        //TODO ask how s*G is supposed to work. S is a byte array and G is a coordinate pair

        //TODO what is the key pair (s, V)? A byte array? Two byte arrays?
        //TODO START HERE WHEN I WAKE UP RETURN SOME BULLSHIT TO MOVE ON TO THE NEXT PART
    }


    public class CoordinatePair {
        private BigInteger x;
        private BigInteger y;

        public CoordinatePair(BigInteger xCoordinate, BigInteger yCoordinate) {
            x = xCoordinate;
            y = yCoordinate;
        }

        public BigInteger getX() {
            return x;
        }
        public BigInteger getY() {
            return y;
        }

        public CoordinatePair getOpposite() {
            BigInteger oppX;
            if (this.x == BigInteger.ZERO) {
                oppX = this.x;
            } else {
                oppX = this.x.negate();
            }

            return new CoordinatePair(oppX, this.y);
        }

        public CoordinatePair computeSum(CoordinatePair otherPoint) {
            //compute new X: (x1y2 + y1x2) / (1 + dx1x2y1y2)
            BigInteger topLeftOfX = this.x.multiply(otherPoint.getY());
            BigInteger topRightOfX = this.y.multiply(otherPoint.getX());
            BigInteger topOfX = topLeftOfX.add(topRightOfX);

            BigInteger one = new BigInteger("1");
            BigInteger d = new BigInteger("-376014");
            BigInteger bottomRightOfXorY = d.multiply(this.x).multiply(otherPoint.getX()).multiply(this.y).multiply(otherPoint.getY());
            BigInteger bottomOfX = one.add(bottomRightOfXorY);

            BigInteger sumX = topOfX.divide(bottomOfX);

            //compute new Y: (y1y2 - x1x2) / (1 - dx1x2y1y2)
            BigInteger topLeftOfY = this.y.multiply(otherPoint.getY());
            BigInteger topRightOfY = this.x.multiply(otherPoint.getX());
            BigInteger topOfY = topLeftOfY.subtract(topRightOfY);

            BigInteger bottomOfY = one.subtract(bottomRightOfXorY);

            BigInteger sumY = topOfY.divide(bottomOfY);

            return new CoordinatePair(sumX, sumY);
        }

        public CoordinatePair computeFromLeastSignificantBitOfY(CoordinatePair point) {
            //TODO ask about the +- option
            //we want to compute +-squareRoot( (1 - x^2) / (1 + 376014x^2 ) ) mod p

            BigInteger xSquared = point.getX().pow(2);
            BigInteger numerator = BigInteger.ONE.subtract(xSquared).mod(p);
            BigInteger denominator = xSquared.multiply(new BigInteger("376014")).add(BigInteger.ONE).mod(p);

            BigInteger newY = sqrt(numerator.divide(denominator), p, true);

            return new CoordinatePair(point.x, newY);
        }

        @Override
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof CoordinatePair) {
                if (((CoordinatePair) o).getX() == this.x && ((CoordinatePair)o).getY() == this.y)  {
                    result = true;
                }
            }
            return result;
        }
    }
}

