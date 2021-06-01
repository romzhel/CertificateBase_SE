package utils.comparation.te;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TotalPriceComparisonResult extends TotalComparisonResult {
    private Map<String, ChangedValue<String>> changedSourceMap = new HashMap<>();
}
