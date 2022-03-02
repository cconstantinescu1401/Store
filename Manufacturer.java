import java.io.Serializable;

public class Manufacturer implements Serializable {

    private String name;
    private int countProducts;

    public Manufacturer(String name) {
        this.name = name;
    }

    public Manufacturer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addNewProduct() {
        this.countProducts += 1;
    }

    public String toString() {
        return name;
    }
}