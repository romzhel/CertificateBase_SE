package database.mappers;

import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.product.Product;
import ui_windows.product.vendors.VendorEnum;
import utils.property_change_protect.ChangeProtectService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbToProductMapper {

    public Product mapToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        String vendorRaw = rs.getString("vendor");
        product.setVendor(vendorRaw == null ? VendorEnum.SIEMENS : VendorEnum.recognizeVendor(vendorRaw));
        product.setMaterial(rs.getString("material"));
        product.setProductForPrint(rs.getString("product_print"));
        product.setArticle(rs.getString("article"));
        product.setHierarchy(rs.getString("hierarchy"));
        product.setLgbk(rs.getString("lgbk"));
        product.setFamily_id(rs.getInt("family"));
        product.setEndofservice(rs.getString("end_of_service"));
        product.setDangerous(rs.getString("dangerous"));
        product.setCountry(rs.getString("country"));
        product.setDchain(nullToEmpty(rs.getString("dchain")));

        product.setDescriptionru(nullToEmpty(rs.getString("description_ru")));
        product.setDescriptionen(nullToEmpty(rs.getString("description_en")));
        product.setPrice(rs.getBoolean("price"));
        product.setBlocked(rs.getBoolean("not_used"));
        product.setPriceHidden(rs.getBoolean("archive"));
        product.setWarranty(rs.getInt("warranty"));

        product.setHistory(rs.getString("history"));
        product.setLastChangeDate(rs.getString("last_change_date"));
        product.setFileName(rs.getString("file_name"));
        product.setComments(rs.getString("comments"));
        product.setCommentsPrice(rs.getString("comments_price") == null ? "" : rs.getString("comments_price"));
        product.setReplacement(rs.getString("replacement"));

        product.setType_id(rs.getInt("type_id"));
        product.setChangecodes(rs.getString("change_codes"));
        product.setLastImportcodes(rs.getString("last_import_codes") == null ? "" : rs.getString("last_import_codes"));

        product.setNormsList(new NormsList(rs.getString("norms_list")));
        product.setNormsMode(rs.getInt("norms_mode"));

        product.setMinOrder(rs.getInt("min_order"));
        product.setPacketSize(rs.getInt("packet_size"));
        product.setLeadTime(rs.getInt("lead_time"));
        product.setWeight(rs.getDouble("weight"));
        product.setLocalPrice(rs.getDouble("local_price"));

        ChangeProtectService protectService = new ChangeProtectService();
        product.setProtectedData(protectService.mapStringToSet(rs.getString("protected_fields")));

        return product;
    }

    private String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}
