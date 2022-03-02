import java.io.Serializable;

public class Currency implements Serializable {

    private String name;
    private String symbol;
    private double parityToEur;

    public String getName() {
        return name;
    }

    public double getParityToEur() {
        return parityToEur;
    }

    public String getSymbol() {
        return symbol;
    }

    public Currency() {
    }

    public Currency(String name, String symbol, double parityToEur) {
        this.name = name;
        this.symbol = symbol;
        this.parityToEur = parityToEur;
    }

    public void updateParity(double parityToEUR) {
        this.parityToEur = parityToEUR;
    }

    public String toString() {
        return name + " " + parityToEur;
    }

}