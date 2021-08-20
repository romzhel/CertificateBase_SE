package ui_windows.options_window.certificates_editor;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import utils.Countries;
import utils.Utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Log4j2
public class Certificate {
    private int id = 0;
    private String name;
    private String expirationDate;
    private String countries;
    private String norms;
    private String fileName;
    private List<CertificateContent> content;
    private boolean fullNameMatch;
    private boolean materialMatch;
    private int userId;

    public Certificate(ResultSet rs) {
        try {
            id = rs.getInt("id");
            name = rs.getString("name");
            expirationDate = rs.getString("expiration_date");
            countries = rs.getString("countries");
            norms = rs.getString("norms");
            fileName = rs.getString("file_name");
            content = CertificatesContent.getInstance().getContentByCertID(id);
            fullNameMatch = rs.getBoolean("name_match");
            materialMatch = rs.getBoolean("material_match");
            userId = rs.getInt("user_id");
        } catch (Exception e) {
            log.error("Certificate creating error: {}", e.getMessage(), e);
        }
    }

    public Certificate(Stage stage) {
        AnchorPane root = (AnchorPane) stage.getScene().getRoot();

        name = Utils.getControlValue(root, "tfCertName");
        expirationDate = Utils.getControlValue(root, "dpDatePicker");
        countries = Countries.getShortNames(Utils.getALControlValueFromLV(root, "lvCountries"));
        norms = RequirementTypes.getInstance().getReqIdsLineFromShortNamesAL(Utils.getALControlValueFromLV(root, "lvNorms"));
        fileName = Utils.getControlValue(root, "tfFileName");
        fullNameMatch = Utils.getControlValue(root, "ckbNameMatch").equals("true");
        materialMatch = Utils.getControlValue(root, "ckbMaterialMatch").equals("true");
        content = new ArrayList<>();
    }

    public String getMonthToExpiration(){
        Date certDate = Utils.getDateYYYYMMDD(expirationDate);
        Date now = new Date();
        long diff = certDate.getTime() - now.getTime();

        long months = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 30;
        return Long.toString(months);
    }
}
