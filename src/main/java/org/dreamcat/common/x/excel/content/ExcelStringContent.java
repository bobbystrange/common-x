package org.dreamcat.common.x.excel.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Create by tuke on 2020/7/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelStringContent implements IExcelContent {

    private String value;

    @Override
    public void fill(Cell cell) {
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
