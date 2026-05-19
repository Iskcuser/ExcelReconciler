package util;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;

public class FileSaver {

    public void save(Workbook workbook, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}