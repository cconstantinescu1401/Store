public class ProductBuilder {
    private final Product product = new Product();

    public ProductBuilder withUniqueId(String uniqueId) {
        product.setUniqueId(uniqueId);
        return this;
    }

    public ProductBuilder withName(String name) {
        product.setName(name);
        return this;
    }

    public Product build() {
        return product;
    }
}
