package org.dreamcat.common.x.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dreamcat.common.x.excel.content.IExcelContent;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelHyperLink;
import org.dreamcat.common.x.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public interface IExcelSheet extends Iterable<IExcelCell> {
    String getName();

    default void fill(Workbook workbook, Sheet sheet, CellStyle defaultStyle, Font defaultFont) {
        for (IExcelCell excelCell : this) {
            int ri = excelCell.getRowIndex();
            int ci = excelCell.getColumnIndex();
            IExcelContent cellContent = excelCell.getContent();
            ExcelFont cellFont = excelCell.getFont();
            ExcelStyle cellStyle = excelCell.getStyle();
            ExcelHyperLink cellLink = excelCell.getHyperLink();

            if (excelCell.hasMergedRegion()) {
                int rs = excelCell.getRowSpan();
                int cs = excelCell.getColumnSpan();
                if (rs > 1 || cs > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            ri, ri + rs - 1, ci, ci + cs - 1));
                }
            }

            Row row = sheet.getRow(ri);
            if (row == null) {
                row = sheet.createRow(ri);
            }
            Cell cell = row.createCell(ci);

            cellContent.fill(cell);
            if (cellLink != null) {
                cellLink.fill(excelCell, workbook, cell);
            }

            if (cellStyle == null && cellFont == null) {
                cell.setCellStyle(defaultStyle);
            } else {
                CellStyle style = workbook.createCellStyle();

                // cellStyle == null && cellFont != null
                if (cellStyle == null) {
                    Font font = workbook.createFont();
                    cellFont.fill(font);
                    style.setFont(font);
                }
                // cellStyle != null
                else if (cellFont == null) {
                    cellStyle.fill(style, defaultFont);
                }
                // cellStyle != null && cellFont != null
                else {
                    Font font = workbook.createFont();
                    cellFont.fill(font);
                    cellStyle.fill(style, font);
                }

                cell.setCellStyle(style);
            }
        }
    }
}
