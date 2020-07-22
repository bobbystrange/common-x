package org.dreamcat.common.x.excel.style;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelFont {
    private String fontName;
    private boolean bold;
    private boolean italic;
    /**
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    private byte underline;
    // use a strikeout horizontal line through the text or not
    private boolean strikeout;
    /**
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    private short typeOffset;
    /**
     * @see Font#COLOR_NORMAL
     * @see Font#COLOR_RED
     */
    private short color;
    // font height in points
    private double height;

    public static ExcelFont from(Workbook workbook, CellStyle style) {
        Font font;
        if (style instanceof XSSFCellStyle) {
            font = ((XSSFCellStyle) style).getFont();
        } else if (style instanceof HSSFCellStyle) {
            font = ((HSSFCellStyle) style).getFont(workbook);
        } else {
            return null;
        }

        ExcelFont excelFont = new ExcelFont();
        excelFont.setFontName(font.getFontName());
        excelFont.setBold(font.getBold());
        excelFont.setItalic(font.getItalic());
        excelFont.setUnderline(font.getUnderline());
        excelFont.setStrikeout(font.getStrikeout());
        excelFont.setTypeOffset(font.getTypeOffset());
        excelFont.setColor(font.getColor());
        excelFont.setHeight(font.getFontHeightInPoints());
        return excelFont;
    }

    public void fill(Font font) {
        font.setFontName(fontName);
        font.setBold(bold);
        font.setItalic(italic);
        font.setUnderline(underline);
        font.setStrikeout(strikeout);
        font.setColor(color);
        font.setTypeOffset(typeOffset);

        if (font instanceof XSSFFont) {
            XSSFFont xssfFont = (XSSFFont) font;
            // font height in points
            xssfFont.setFontHeight(height);
        } else if (font instanceof HSSFFont) {
            HSSFFont hssfFont = (HSSFFont) font;
            hssfFont.setFontHeightInPoints((short) height);
        }
    }
}
