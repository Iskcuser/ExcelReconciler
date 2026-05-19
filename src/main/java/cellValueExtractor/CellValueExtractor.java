package cellValueExtractor;

import org.apache.poi.ss.usermodel.*;

public class CellValueExtractor {

    public static String getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) yield cell.getDateCellValue().toString();
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                CellValue cv = evaluator.evaluate(cell);
                yield (cv == null) ? null : switch (cv.getCellType()) {
                    case NUMERIC -> String.valueOf(cv.getNumberValue());
                    case STRING -> cv.getStringValue();
                    default -> null;
                };
            }
            default -> null;
        };
    }
}

