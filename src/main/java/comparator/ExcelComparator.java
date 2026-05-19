package comparator;

import model.CompareResult;

public class ExcelComparator {

    public CompareResult compare(String v1, String v2) {

        if (v1 == null || v2 == null) {
            return new CompareResult(CompareResult.Status.SKIP);
        }

        try {
            String s1 = v1.trim().replace(",", ".").replaceAll("[\\s\\u00A0]+", "");
            String s2 = v2.trim().replace(",", ".").replaceAll("[\\s\\u00A0]+", "");

            if (s1.isEmpty() || s2.isEmpty()) {
                return new CompareResult(CompareResult.Status.SKIP);
            }

            double d1 = Double.parseDouble(s1);
            double d2 = Double.parseDouble(s2);

            double r1 = Math.round(d1 * 100.0) / 100.0;
            double r2 = Math.round(d2 * 100.0) / 100.0;

            if (Double.compare(r1, r2) == 0) {
                return new CompareResult(CompareResult.Status.MATCH);
            }

        } catch (NumberFormatException e) {
        }

        return new CompareResult(CompareResult.Status.SKIP);
    }
}


