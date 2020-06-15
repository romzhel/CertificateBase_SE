package ui_windows.product.certificatesChecker;

public enum CheckStatusResult {//не менять порядок приоритетов (используются в CertificateChecker, line 50
    STATUS_OK("OK", 0),
    NO_DATA("Нет данных", 1),
    STATUS_NOT_OK("НЕ ОК", 2),
    NO_CERT("Нет сертификатов", 3),
    PART_OF_CERT("Есть часть сертификатов", 4),
    CERT_WITH_ERR("Есть сертификаты с ошибками", 5),
    NO_NORMS_DEFINED("Нет данных по нормам", 6);

    private String text;
    private int prio;

    CheckStatusResult(String text, int prio){
        this.text = text;
        this.prio = prio;
    }

    public String getText() {
        return text;
    }

    public int getPrio() {
        return prio;
    }
}
