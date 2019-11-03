package ui_windows.product.certificatesChecker;

public enum CheckStatusResult {
    NO_DATA("Нет данных"), NO_CERT("Нет сертификатов"), PART_OF_CERT("Есть часть сертификатов"),
    CERT_WITH_ERR("Есть сертификаты с ошибками"), NO_NORMS_DEFINED("Нет данных по нормам"), STATUS_OK("OK"),
    STATUS_NOT_OK("НЕ ОК");

    private String text;

    CheckStatusResult(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
