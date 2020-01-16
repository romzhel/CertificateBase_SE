package ui_windows.product.certificatesChecker;

import core.CoreModule;
import ui_windows.options_window.product_lgbk.NormsList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.requirements_types_editor.RequirementType;
import ui_windows.product.Product;
import utils.ItemsGroup;
import utils.ItemsGroups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import static ui_windows.product.certificatesChecker.CertificatesChecker.*;
import static ui_windows.product.certificatesChecker.CheckStatusResult.*;

public class NormsChecker {
    private ItemsGroups<String, String, String> results;
    private HashSet<Integer> globalNeededNorms;
    private HashSet<Integer> productNeededNorms;
    private ArrayList<CertificateVerificationItem> treatedResults;
    private CheckStatusResult checkStatusResult = NO_DATA;
    private TreeSet<CertificateVerificationItem> resultTableItems;
    private HashSet<Integer> correctNorms;
    private HashSet<Integer> faultNorms;

    public NormsChecker(Product product, TreeSet<CertificateVerificationItem> resultTableItems) {
        init(resultTableItems);
        checkNorms(product);
    }

    private void init(TreeSet<CertificateVerificationItem> resultTableItems) {
        this.resultTableItems = resultTableItems;
        results = new ItemsGroups<>("Результаты", (o1, o2) -> o1.getGroupNode().compareTo(o2.getGroupNode()));
        treatedResults = new ArrayList<>();
        correctNorms = new HashSet<>();
        faultNorms = new HashSet<>();
        globalNeededNorms = new HashSet<>();
        productNeededNorms = new HashSet<>();
        for (CertificateVerificationItem cvi : resultTableItems) {
            ItemsGroup<String, String> result = new ItemsGroup<>(cvi.getNorm(), (o1, o2) -> o1.compareTo(o2));
            result.addItem(cvi.getStatus().startsWith(OK) ? OK : NOT_OK);
            results.addGroup(result);
//            foundNorms.add(CoreModule.getRequirementTypes().getRequirementByShortName(cvi.getNorm()).getId());
        }

        for (ItemsGroup<String, String> singleNorm : results.getItems()) {
            RequirementType rt = CoreModule.getRequirementTypes().getRequirementByShortName(singleNorm.getGroupNode());
            if (rt != null) {
                if (singleNorm.getItems().contains(OK)) {
                    correctNorms.add(rt.getId());
                } else if (!singleNorm.getItems().contains(OK) && singleNorm.getItems().contains(NOT_OK)) {
                    faultNorms.add(CoreModule.getRequirementTypes().getRequirementByShortName(singleNorm.getGroupNode()).getId());
                }
            }
        }
    }

    private void checkNorms(Product product) {
        globalNeededNorms.clear();
        productNeededNorms.clear();

        HashSet<Integer> normsForChecking = new HashSet<>();

        productNeededNorms.addAll(product.getNormsList().getIntegerItems());
        globalNeededNorms.addAll(CoreModule.getProductLgbkGroups().getGlobalNormIds(new ProductLgbk(product)));

        if (product.getNormsMode() == NormsList.ADD_TO_GLOBAL) {
            normsForChecking.addAll(globalNeededNorms);

            if (product.getArticle().endsWith("-EX")) {
                int normExId = CoreModule.getRequirementTypes().getExNormId();
                if (normExId > 0) normsForChecking.add(normExId);
            }
        }

        normsForChecking.addAll(productNeededNorms);
        int normsForCheckingCount = normsForChecking.size();
        boolean certNotNeeded = false;

        normsForChecking.removeAll(correctNorms);
        normsForChecking.removeAll(faultNorms);
        if (correctNorms.contains(1)) {
            normsForChecking.remove(9);//НВО СС заменяет НВО ДС
            faultNorms.remove(9);
        }
        if (correctNorms.contains(3)) {
            normsForChecking.remove(11);//ЭМС СС заменяет ЭМС ДС
            faultNorms.remove(11);
        }

        if (normsForCheckingCount > 0) {
            for (int normIndex : normsForChecking) {
                String shortName = CoreModule.getRequirementTypes().getRequirementByID(normIndex).getShortName();
                CertificateVerificationItem cvi = new CertificateVerificationItem(shortName);
                if (shortName.equals(CERT_NO_NEEDED)) {
                    cvi.setStatus(OK);
                    certNotNeeded = true;
                } else {
                    faultNorms.add(normIndex);
                }
                resultTableItems.add(cvi);
            }
        }

        if (certNotNeeded) {
            checkStatusResult = STATUS_OK;
        } else if (faultNorms.size() > 0) {
            checkStatusResult = STATUS_NOT_OK;
        } else if (normsForCheckingCount > 0) {
            checkStatusResult = STATUS_OK;
        } else {
            checkStatusResult = NO_DATA;
        }
    }

    public CheckStatusResult getCheckStatusResult() {
        return checkStatusResult;
    }
}
