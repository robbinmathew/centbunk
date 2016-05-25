package bronz.accounting.bunk.products.model;

public enum ProductType
{
    ALL("ALL_PRODUCTS"),
    PRIMARY("PRIMARY_PRODUCTS"),
    SECONDARY("SECONDARY_PRODUCTS"),
    FUEL_PRODUCTS("FUEL_PRODUCTS"),
    OIL_PRODUCTS("OIL_PRODUCTS"),
    LUBE_PRODUCTS("LUBE_PRODUCTS", 10),
    ADDITIONAL_PRODUCTS("ADDITIONAL_PRODUCTS", 1000);

    Integer MIN_OIL_PROD_ID = 10;
    Integer MIN_ADDITIONAL_PROD_ID = 1000;

    private final String typeName;
    private final Integer minId;

    private ProductType(final String typeName, final Integer minId) {
        this.typeName = typeName;
        this.minId = minId;
    }

    private ProductType(final String typeName) {
        this.typeName = typeName;
        this.minId = null;
    }
}
