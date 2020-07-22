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
public class ExcelNumericContent implements IExcelContent {
    private double value;

    @Override
    public void fill(Cell cell) {
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(value);
    }

    @Override
    public String toString() {
        long round = Math.round(value);
        if (Math.abs(value - round) < Double.MIN_NORMAL) {
            return Long.toString(round);
        }
        if (value > -1000 && value < 10000) {
            return String.format("%.6g", value);
        }
        return String.format("%.8g", value);
    }
}
