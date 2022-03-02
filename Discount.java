import java.io.Serializable;
import java.time.*;

public class Discount implements Serializable {
    private DiscountType discountType;
    private String name;
    private double value;
    private LocalDateTime lastDateApplied;

    public Discount(DiscountType discountType, String name, double value) {
        this.discountType = discountType;
        this.name = name;
        this.value = value;
    }

    public Discount() {
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setAsAppliedNow() {
        lastDateApplied = LocalDateTime.now();
    }

    public String toString() {
        return discountType + " " + value + " " + name + " " + lastDateApplied;
    }
}
