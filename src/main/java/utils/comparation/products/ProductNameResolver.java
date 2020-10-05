package utils.comparation.products;

public class ProductNameResolver {

    public static String resolve(String name) {
        return name.replaceAll("(^0+)*(\\-)*(\\:)*(VBPZ)*(BPZ)*(\\s)*([/()])*", "").trim();
    }
}
