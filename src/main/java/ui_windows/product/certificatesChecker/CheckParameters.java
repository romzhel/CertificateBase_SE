package ui_windows.product.certificatesChecker;

public class CheckParameters {
    private int temporaryTypeId = 0;
    private boolean useTemporaryTypeId = false;
    private boolean isEqTypeFiltered = true;

    private CheckParameters() {
    }

    public static CheckParameters getDefault() {
        return new CheckParameters();
    }

    public int getTemporaryTypeId() {
        return temporaryTypeId;
    }

    public CheckParameters setTemporaryTypeId(int typeId) {
        temporaryTypeId = typeId;
        useTemporaryTypeId = typeId > 0 ? true : false;
        return this;
    }

    public boolean isUseTemporaryTypeId() {
        return useTemporaryTypeId;
    }

    public boolean isEqTypeFiltered() {
        return isEqTypeFiltered;
    }

    public CheckParameters setEqTypeFiltered(boolean eqTypeFiltered) {
        isEqTypeFiltered = eqTypeFiltered;
        return this;
    }

    public CheckParameters setUseTemporaryTypeId(boolean useTemporaryTypeId) {
        this.useTemporaryTypeId = useTemporaryTypeId;
        return this;
    }
}
