package org.dreamcat.common.x.excel.style;

import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelStyle {
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private boolean hidden;
    private boolean wrapText;
    private boolean locked;

    private boolean quotePrefix;
    // Controls if the Cell should be auto-sized to shrink to fit if the text is too long
    private boolean shrinkToFit;

    public static ExcelStyle from(CellStyle style) {
        ExcelStyle excelStyle = null;
        if (ExcelRichStyle.hasRichStyle(style)) {
            ExcelRichStyle excelRichStyle = new ExcelRichStyle();

            excelRichStyle.setIndent(style.getIndention());
            excelRichStyle.setRotation(style.getIndention());

            excelRichStyle.setBgColor(style.getFillBackgroundColor());
            excelRichStyle.setFgColor(style.getFillForegroundColor());
            excelRichStyle.setFillPattern(style.getFillPattern());

            excelRichStyle.setBorderBottom(style.getBorderBottom());
            excelRichStyle.setBorderLeft(style.getBorderLeft());
            excelRichStyle.setBorderTop(style.getBorderTop());
            excelRichStyle.setBorderRight(style.getBorderRight());

            excelRichStyle.setBottomBorderColor(style.getBottomBorderColor());
            excelRichStyle.setLeftBorderColor(style.getLeftBorderColor());
            excelRichStyle.setTopBorderColor(style.getTopBorderColor());
            excelRichStyle.setRightBorderColor(style.getRightBorderColor());

            excelStyle = excelRichStyle;
        }

        if (excelStyle == null) {
            excelStyle = new ExcelStyle();
        }

        excelStyle.setHorizontalAlignment(style.getAlignment());
        excelStyle.setVerticalAlignment(style.getVerticalAlignment());
        excelStyle.setHidden(style.getHidden());
        excelStyle.setHidden(style.getWrapText());
        excelStyle.setHidden(style.getLocked());
        excelStyle.setQuotePrefix(style.getQuotePrefixed());
        excelStyle.setShrinkToFit(style.getShrinkToFit());

        return excelStyle;
    }

    public void fill(CellStyle style, Font font) {
        if (horizontalAlignment != null) style.setAlignment(horizontalAlignment);
        if (verticalAlignment != null) style.setVerticalAlignment(verticalAlignment);
        style.setLocked(locked);

        style.setQuotePrefixed(quotePrefix);
        style.setShrinkToFit(shrinkToFit);
        style.setHidden(hidden);
        style.setWrapText(wrapText);
        if (font != null) style.setFont(font);
    }

}
