package reader;

import cellValueExtractor.CellValueExtractor;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;

public class ExcelReader {

    public Workbook readWorkbook(String filePath) throws IOException {
        return WorkbookFactory.create(new File(filePath));
    }

    public String getCellValue(Row row, int cellIndex, FormulaEvaluator evaluator) {
        if (row == null) return null;

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) return null;

        return CellValueExtractor.getCellValue(cell, evaluator);
    }
}

