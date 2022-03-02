import java.io.*;

import com.opencsv.exceptions.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, CsvValidationException {
        Store store = Store.getInstance();
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the input file name:");
        File in = new File(input.next());
        Scanner scnr = new Scanner(in);

        System.out.print("Enter the output file name:");
        PrintWriter writer = new PrintWriter(input.next());
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            String[] words = line.split(" ");
            if (words[0].equals("listcurrencies")) {
                store.listCurrencies(writer);
                writer.println();
                continue;
            }
            if (words[0].equals("addcurrency")) {
                try {
                    store.createCurrency(words[1], words[2], Double.parseDouble(words[3]));
                    continue;
                } catch (DuplicateCurrencyException e) {
                    System.err.println(e.getMessage());
                    continue;
                }
            }
            if (words[0].equals("getstorecurrency")) {
                writer.println(store.getCurrentCurrency().getName());
                writer.println();
                continue;
            }
            if (words[0].equals("loadcsv")) {
                store.readCSV(words[1]);
                continue;
            }
            if (words[0].equals("savecsv")) {
                store.saveCSV(words[1]);
                continue;
            }
            if (words[0].equals("setstorecurrency")) {
                try {
                    store.changeCurrency(store.findCurrencyByName(words[1]));
                    continue;
                } catch (CurrencyNotFoundException e) {
                    System.err.println(e.getMessage());
                    continue;
                }
            }
            if (words[0].equals("updateparity")) {
                store.findCurrencyByName(words[1]).updateParity(Double.parseDouble(words[2]));
            }
            if (words[0].equals("listproducts")) {
                store.listProducts(writer);
                writer.println();
                continue;
            }
            if (words[0].equals("showproduct")) {
                if (store.findProductById(words[1]) == null) {
                    System.err.println("There could not be found any product with the specified unique id.");
                    continue;
                }
                writer.println(store.findProductById(words[1]).toString(store.getCurrentCurrency().getSymbol()));
                writer.println();
                continue;
            }
            if (words[0].equals("listmanufacturers")) {
                store.listManufacturers(writer);
                writer.println();
                continue;
            }
            if (words[0].equals("listproductsbymanufacturer")) {
                String manufacturerName = String.join(" ", ArrayUtils.remove(words, 0));
                ArrayList<Product> products = store.getProductsByManufacturer(store.findManufacturer(manufacturerName));
                for (Product p : products)
                    writer.println(p.toString(store.getCurrentCurrency().getSymbol()));
                writer.println();
                continue;
            }
            if (words[0].equals("listdiscounts")) {
                store.listDiscounts(writer);
                writer.println();
                continue;
            }
            if (words[0].equals("adddiscount")) {
                String discountName = line.substring(line.indexOf("\""), line.lastIndexOf("\"") + 1);
                DiscountType discountType;
                if (words[1].equals("PERCENTAGE"))
                    discountType = DiscountType.PERCENTAGE_DISCOUNT;
                else
                    discountType = DiscountType.FIXED_DISCOUNT;
                double discountValue = Double.parseDouble(words[2]);
                store.createDiscount(discountType, discountName, discountValue);
                continue;
            }
            if (words[0].equals("applydiscount")) {
                DiscountType discountType;
                if (words[1].equals("PERCENTAGE"))
                    discountType = DiscountType.PERCENTAGE_DISCOUNT;
                else
                    discountType = DiscountType.FIXED_DISCOUNT;
                double discountValue = Double.parseDouble(words[2]);
                Discount d = store.findDiscount(discountType, discountValue);
                try {
                    store.applyDiscount(d);
                    continue;
                } catch (DiscountNotFoundException e) {
                    System.err.println("The given discount could not be found.");
                    continue;
                } catch (NegativePriceException e) {
                    System.err.println("A product reached a negative price.");
                    break;
                }
            }
            if (words[0].equals("calculatetotal")) {
                ArrayList<Product> products = new ArrayList<>();
                for (int j = 1; j < words.length; j++)
                    products.add(store.findProductById(words[j]));
                writer.println(store.calculateTotal(products));
                continue;
            }
            if (words[0].equals("savestore"))
                store.savestore(words[1]);
            if (words[0].equals("loadstore")) {
                store = store.loadstore(words[1]);
            }
            if (words[0].equals("exit") || words[0].equals("quit"))
                break;
        }
        writer.close();
    }

}
