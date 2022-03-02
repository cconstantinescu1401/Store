import java.util.ArrayList;
import java.io.*;

import org.apache.commons.csv.*;

public class Store implements Serializable {
    private static Store instance;
    private String name;
    private Currency currentCurrency;
    private final ArrayList<Product> products;
    private final ArrayList<Manufacturer> manufacturers;
    private final ArrayList<Currency> currencies;
    private final ArrayList<Discount> discounts;

    private Store() {
        this.currentCurrency = new Currency("EUR", "€", 1.0);
        this.products = new ArrayList<>();
        this.currencies = new ArrayList<>();
        this.currencies.add(this.currentCurrency);
        this.manufacturers = new ArrayList<>();
        this.discounts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrentCurrency() {
        return currentCurrency;
    }

    public static Store getInstance() {
        if (instance == null) {
            instance = new Store();
        }
        return instance;
    }

    public void readCSV(String filename) throws IOException {
        CSVParser parser = new CSVParser(new FileReader(filename), CSVFormat.DEFAULT);
        for (CSVRecord line : parser) {
            String uniqueId = line.get(0);
            String productName = line.get(1);
            String manufacturerName = line.get(2);
            String stringPrice = line.get(3);
            String stringQuantity = line.get(4);

            //verific daca apare pretul produsului
            if (stringPrice.length() == 0) {
                continue;
            }
            Product p = new ProductBuilder()
                    .withUniqueId(uniqueId)
                    .withName(productName)
                    .build();
            try {
                double price = convertPrice(stringPrice);
                p.setPrice(price);
            } catch (CurrencyNotFoundException e) {
                continue;
            }
            try {
                addProduct(p);
            } catch (DuplicateProductException e) {
                System.err.println(e.getMessage());
                return;
            }

            if (manufacturerName.length() == 0) {
                manufacturerName = "Not Available";
            }
            Manufacturer m = new Manufacturer(manufacturerName);
            try {
                addManufacturer(m);
            } catch (DuplicateManufacturerException e) {
                m = findManufacturer(manufacturerName);
            }
            m.addNewProduct();
            p.setManufacturer(m);

            if (stringQuantity.contains("\u00a0"))
                stringQuantity = stringQuantity.substring(0, stringQuantity.indexOf("\u00a0"));
            if (stringQuantity.length() > 0)
                p.setQuantity(Integer.parseInt(stringQuantity));
            else
                p.setQuantity(0);
        }
    }

    public void saveCSV(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT
                .withHeader("uniq_id", "product_name", "manufacturer", "price", "number_available_in_stock"));
        for (Product p : products)
            printer.printRecord(p.getUniqueId(), p.getName(), p.getManufacturer().getName(), currentCurrency.getSymbol() + p.getPrice(), p.getQuantity());
        fw.close();
    }

    public void addProduct(Product product) throws DuplicateProductException {
        if (productExistsInArray(product.getUniqueId()))
            throw new DuplicateProductException("Two products have the same unique ID.");
        products.add(product);

    }

    public boolean productExistsInArray(String uniqueId) {
        for (Product p : products) {
            if (p.getUniqueId().equals(uniqueId))
                return true;
        }
        return false;
    }

    public void addManufacturer(Manufacturer manufacturer) throws DuplicateManufacturerException {
        if (findManufacturer(manufacturer.getName()) != null)
            throw new DuplicateManufacturerException();
        manufacturers.add(manufacturer);
    }

    public Manufacturer findManufacturer(String name) {
        for (Manufacturer m : manufacturers) {
            if (m.getName().equals(name))
                return m;
        }
        return null;
    }

    public void createCurrency(String name, String symbol, double parityToEur) throws DuplicateCurrencyException {
        if (findCurrencyByName(name) != null)
            throw new DuplicateCurrencyException("The specified currency already exists.");
        Currency c = new Currency(name, symbol, parityToEur);
        currencies.add(c);
    }

    public void changeCurrency(Currency currency) throws CurrencyNotFoundException {
        if (!currencies.contains(currency))
            throw new CurrencyNotFoundException("The specified currency could not be found.");
        for (Product p : products) {
            p.setPrice((p.getPrice() * currentCurrency.getParityToEur()) / currency.getParityToEur());
        }
        currentCurrency = currency;
    }

    public Currency findCurrencyBySymbol(String symbol) {
        for (Currency c : currencies) {
            if (c.getSymbol().equals(symbol))
                return c;
        }
        return null;
    }

    public Currency findCurrencyByName(String name) {
        for (Currency c : currencies) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public void createDiscount(DiscountType discountType, String name, double value) {
        Discount d = new Discount(discountType, name, value);
        discounts.add(d);
    }

    public void applyDiscount(Discount discount) throws DiscountNotFoundException, NegativePriceException {
        if (discount == null || !(discounts.contains(discount)))
            throw new DiscountNotFoundException("The specified discount could not be found.");

        discount.setAsAppliedNow();
        if (discount.getDiscountType() == DiscountType.PERCENTAGE_DISCOUNT) {
            for (Product p : products) {
                p.setDiscount(discount);
                double newPrice = p.getPrice() - discount.getValue() * p.getPrice() / 100;
                if (newPrice < 0)
                    throw new NegativePriceException("A product reached a negative price.");
                p.setPrice(newPrice);
            }
        } else {
            for (Product p : products) {
                double newPrice = p.getPrice() - discount.getValue();
                if (newPrice < 0)
                    throw new NegativePriceException("A product reached a negative price.");
                p.setPrice(newPrice);
            }

        }
    }

    public ArrayList<Product> getProductsByManufacturer(Manufacturer manufacturer) {
        ArrayList<Product> listOfProducts = new ArrayList<>();
        for (Product p : products) {
            if (p.getManufacturer().getName().equals(manufacturer.getName())) {
                listOfProducts.add(p);
            }
        }
        return listOfProducts;
    }

    public String calculateTotal(ArrayList<Product> productList) {
        double total = 0;
        for (Product p : productList) {
            total += p.getPrice();
        }
        return currentCurrency.getSymbol() + total;
    }

    public Product findProductById(String id) {
        for (Product p : products) {
            if (p.getUniqueId().equals(id))
                return p;
        }
        return null;
    }

    public Discount findDiscount(DiscountType type, double value) {
        for (Discount d : discounts) {
            if (d.getDiscountType() == type && d.getValue() == value)
                return d;
        }
        return null;
    }

    public double convertPrice(String price) throws CurrencyNotFoundException {
        String symbol = price.substring(0, 1);
        Currency currency = findCurrencyBySymbol(symbol);
        if (currency == null)
            throw new CurrencyNotFoundException("The specified currency could not be found.");
        StringBuilder correctPrice = new StringBuilder();
        for (int i = 0; i < price.length(); i++) {
            char c = price.charAt(i);
            if (c == ' ' || c == '-')
                break;
            if (c == ',')
                continue;
            if ((c <= '9' && c >= '0') || (c == '.'))
                correctPrice.append(c);
        }
        return Double.parseDouble(correctPrice.toString()) * currency.getParityToEur() / currentCurrency.getParityToEur();

    }

    public void listProducts(PrintWriter w) {
        for (Product p : products)
            w.println(p.toString(currentCurrency.getSymbol()));
    }

    public void listCurrencies(PrintWriter w) {
        for (Currency c : currencies)
            w.println(c.toString());
    }

    public void listManufacturers(PrintWriter w) {
        for (Manufacturer m : manufacturers)
            w.println(m.toString());
    }

    public void listDiscounts(PrintWriter w) {
        for (Discount d : discounts)
            w.println(d.toString());
    }

    public void savestore(String filename) {
        try {

            FileOutputStream out = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(Store.getInstance());
            objectOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Store loadstore(String filename) {

        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream objectIn = new ObjectInputStream(in);
            Store s = (Store) objectIn.readObject();
            objectIn.close();
            return s;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

