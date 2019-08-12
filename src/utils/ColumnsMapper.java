package utils;

import java.util.Arrays;

public class ColumnsMapper {
    private String[] properties;
    private int[] mapper;
    private String marerial;
    private String article;
    private String hierarchy;
    private String lgbk;
    private String validFrom;
    private String endOfService;
    private String dangerous;
    private String cntryOfOrigin;
    private String dchain;
    private String descriptionRu;
    private String descriptionEn;
    private String productPrint;

    public ColumnsMapper(RowData rowData) {
        properties = new String[]{
                "material", //0
                "article type", //1
                "product hierarchy", //2
                "lgbk", //3
                "valid from",//4
                "end of service period", //5
                "dangerousgoods - un-no", //6
                "ctry of origin", //7
                "dchain-spec.",//8
                "заказной номер", //9
                "описание", //10
                "марка", //11
                "gbk", //12
                "order number", //13
                "type", //14
                "eccn", //15
                "country of origin",//16
                "countoforig",//17
                "prod.hier.",//18
                "battery code",//19
                "prod.nr.print",//20
                "material description"};//21
        mapper = new int[properties.length];
        Arrays.fill(mapper, -1);

        for (int j = 0; j < rowData.getSize(); j++) {
            for (int i = 0; i < properties.length; i++) {
                if (rowData.get(j) != null && rowData.get(j).toLowerCase().matches(".*(" + properties[i] + ").*")) {//found col with title
                    if (mapper[i] < 0) {
                        mapper[i] = j;//pointer to col
                        break;
                    }
                }
            }
        }
    }

    public boolean isTitleRow() {
        for (int val : mapper) {
            if (val >= 0) return true;
        }
        return false;
    }

    public boolean isRowHasData(RowData rowData) {
        marerial = Utils.toEN(new DoubleProperties().merge(
                Utils.toEN(rowData.get(mapper[0]).replaceAll("\\,", ".")),
                Utils.toEN(rowData.get(mapper[9]).replaceAll("\\,", ".")),
                Utils.toEN(rowData.get(mapper[13]))));
//        if (marerial == null || marerial.trim().isEmpty() || !marerial.matches("^.*\\d.*$")) return false;
        if (marerial == null || marerial.trim().isEmpty()) return false;

        article = Utils.toEN(new DoubleProperties().merge(
                Utils.toEN(rowData.get(mapper[1])),
                Utils.toEN(rowData.get(mapper[11])),
                Utils.toEN(rowData.get(mapper[14]))).replaceAll("\\,", "."));
        if (article.toLowerCase().trim().matches(".*type.*")) return false;
        boolean articleEmpty = article == null || article.trim().isEmpty();

        hierarchy = Utils.toEN(rowData.get(mapper[2]));
        boolean hierarchyEmpty = hierarchy == null || hierarchy.trim().length() == 0;

        lgbk = Utils.toEN(new DoubleProperties().merge(
                rowData.get(mapper[3]),
                rowData.get(mapper[12]),
                rowData.get(mapper[18])));
        boolean lgbkEmpty = lgbk == null || lgbk.trim().length() == 0;

        validFrom = rowData.get(mapper[4]).replaceAll("\\,", ".");
        boolean validFromEmpty = validFrom == null || validFrom.trim().length() == 0;

        endOfService = rowData.get(mapper[5]).matches("00.00.0000") ?
                "" : rowData.get(mapper[5]).replaceAll("\\,", ".");
        boolean endOfServiceEmpty = endOfService == null || endOfService.trim().length() == 0;

        dangerous = new DoubleProperties().merge(
                Utils.toEN(rowData.get(mapper[6])),
                Utils.toEN(rowData.get(mapper[19])));
        if (dangerous.equals("N")) dangerous = "";
        boolean dangerousEmpty = dangerous == null || dangerous.trim().length() == 0;

        cntryOfOrigin = new DoubleProperties().merge(Utils.toEN(rowData.get(mapper[7])),
                Utils.toEN(rowData.get(mapper[16])), Utils.toEN(rowData.get(mapper[17])));
        boolean cntryOfOriginEmpty = cntryOfOrigin == null || cntryOfOrigin.trim().isEmpty();

        dchain = rowData.get(mapper[8]);
        boolean dchainEmpty = dchain == null || dchain.trim().isEmpty();

        descriptionRu = rowData.get(mapper[10]);
        boolean descriptionRuEmpty = descriptionRu == null || descriptionRu.trim().isEmpty();

        descriptionEn = rowData.get(mapper[21]);
        boolean descriptionEnEmpty = descriptionEn == null || descriptionEn.trim().isEmpty();

        productPrint = Utils.toEN(rowData.get(mapper[20]).replaceAll("\\,", "."));

        if (articleEmpty && hierarchyEmpty && lgbkEmpty && validFromEmpty && endOfServiceEmpty && dangerousEmpty &&
                cntryOfOriginEmpty && dchainEmpty && descriptionRuEmpty && descriptionEnEmpty) return false;

        return true;
    }

    public void treatRow(RowData rowData) {
        marerial = new DoubleProperties().merge(
                Utils.toEN(rowData.get(mapper[0]).replaceAll("\\,", ".")),
                Utils.toEN(rowData.get(mapper[9]).replaceAll("\\,", ".")
                        .replaceAll(Utils.toEN("BPZ:"), "")));
        article = new DoubleProperties().merge(
                Utils.toEN(rowData.get(mapper[1]).replaceAll("\\,", ".")),
                Utils.toEN(rowData.get(mapper[11]).replaceAll("\\,", ".")));
        hierarchy = Utils.toEN(rowData.get(mapper[2]));
        lgbk = Utils.toEN(new DoubleProperties().merge(rowData.get(mapper[3]), rowData.get(mapper[12])));
        validFrom = rowData.get(mapper[4]).replaceAll("\\,", ".");
        endOfService = rowData.get(mapper[5]).matches("00.00.0000") ?
                "" : rowData.get(mapper[5]).replaceAll("\\,", ".");
        dangerous = Utils.toEN(rowData.get(mapper[6]));
        cntryOfOrigin = Utils.toEN(rowData.get(mapper[7]));
        dchain = rowData.get(mapper[8]);
        descriptionRu = rowData.get(mapper[10]);
    }

    public int[] getColsIndexes() {
        return mapper;
    }

    public String getMarerial() {
        return marerial;
    }

    public String getArticle() {
        return article;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public String getLgbk() {
        return lgbk;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public String getEndOfService() {
        return endOfService;
    }

    public String getDangerous() {
        return dangerous;
    }

    public String getCntryOfOrigin() {
        return cntryOfOrigin;
    }

    public String getDchain() {
        return dchain;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public String getProductPrint() {
        return productPrint;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }
}
