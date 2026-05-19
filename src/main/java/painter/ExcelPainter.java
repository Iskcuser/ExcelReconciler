package painter;

import model.CompareResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;

public class ExcelPainter {

    private final CellStyle greenStyle;

    public ExcelPainter(Workbook workbook) {
        this.greenStyle = createGreenStyle(workbook);
    }

    private CellStyle createGreenStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Green HEX #92D050
        byte[] rgb = new byte[]{(byte) 146, (byte) 208, (byte) 80};
        XSSFColor customColor = new XSSFColor(rgb, new DefaultIndexedColorMap());

        ((XSSFCellStyle) style).setFillForegroundColor(customColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }

    public void paint(Row row, CompareResult result, int col1, int col2) {
        if (result.status() == CompareResult.Status.MATCH) {
            applyStyle(row, col1);
            applyStyle(row, col2);
        }
    }

    private void applyStyle(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        cell.setCellStyle(greenStyle);
    }
}
