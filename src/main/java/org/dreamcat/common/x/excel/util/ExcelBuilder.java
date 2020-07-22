package org.dreamcat.common.x.excel.util;

import lombok.RequiredArgsConstructor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.dreamcat.common.bean.BeanCopyUtil;
import org.dreamcat.common.x.excel.content.ExcelNumericContent;
import org.dreamcat.common.x.excel.content.ExcelStringContent;
import org.dreamcat.common.x.excel.content.IExcelContent;
import org.dreamcat.common.x.excel.core.ExcelCell;
import org.dreamcat.common.x.excel.core.ExcelRichCell;
import org.dreamcat.common.x.excel.core.ExcelSheet;
import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelHyperLink;
import org.dreamcat.common.x.excel.style.ExcelRichStyle;
import org.dreamcat.common.x.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/22
 */
public class ExcelBuilder {

    public static SheetTerm sheet(String sheetName) {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<ExcelSheet>();
        ExcelSheet sheet = new ExcelSheet(sheetName);
        book.getSheets().add(sheet);

        return new SheetTerm(book, sheet);
    }

    public static IExcelContent term(String string) {
        return new ExcelStringContent(string);
    }

    public static IExcelContent term(double number) {
        return new ExcelNumericContent(number);
    }

    @RequiredArgsConstructor
    public static class SheetTerm {
        private final ExcelWorkbook<ExcelSheet> book;
        private final ExcelSheet sheet;
        private transient RichSheetTerm richSheetTerm;

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex) {
            return cell(term, rowIndex, columnIndex, 1, 1);
        }

        public SheetTerm cell(IExcelContent term, int rowIndex, int columnIndex, int rowSpan, int columnSpan) {
            sheet.getCells().add(new ExcelCell(term, rowIndex, columnIndex, rowSpan, columnSpan));
            return this;
        }

        public RichSheetTerm richCell(IExcelContent term, int rowIndex, int columnIndex) {
            return richCell(term, rowIndex, columnIndex, 1, 1);
        }

        public RichSheetTerm richCell(IExcelContent term, int rowIndex, int columnIndex, int rowSpan, int columnSpan) {
            ExcelRichCell cell = new ExcelRichCell(term, rowIndex, columnIndex, rowSpan, columnSpan);
            sheet.getCells().add(cell);
            if (richSheetTerm == null) {
                richSheetTerm = new RichSheetTerm(this, cell);
            }
            return richSheetTerm;
        }

        public ExcelWorkbook<ExcelSheet> finish() {
            return book;
        }
    }

    @RequiredArgsConstructor
    public static class RichSheetTerm {
        private final SheetTerm sheetTerm;
        private final ExcelRichCell cell;
        private transient ExcelFont font;
        private transient ExcelStyle style;

        public SheetTerm finish() {
            this.font = null;
            this.style = null;
            return sheetTerm;
        }

        public RichSheetTerm hyperLink(String address) {
            return hyperLink(address, null);
        }

        public RichSheetTerm hyperLink(String address, String label) {
            return hyperLink(address, label, HyperlinkType.URL);
        }

        public RichSheetTerm hyperLink(String address, String label, HyperlinkType type) {
            cell.setHyperLink(new ExcelHyperLink(type, address, label));
            return this;
        }

        public RichSheetTerm bold() {
            return bold(true);
        }

        public RichSheetTerm bold(boolean bold) {
            getFont().setBold(bold);
            return this;
        }

        public RichSheetTerm italic() {
            return italic(true);
        }

        public RichSheetTerm italic(boolean italic) {
            getFont().setItalic(italic);
            return this;
        }

        public RichSheetTerm underline() {
            return underline(Font.U_SINGLE);
        }

        public RichSheetTerm underline(byte underline) {
            getFont().setUnderline(underline);
            return this;
        }

        public RichSheetTerm strikeout() {
            return strikeout(true);
        }

        public RichSheetTerm strikeout(boolean strikeout) {
            getFont().setStrikeout(strikeout);
            return this;
        }

        public RichSheetTerm typeOffset() {
            return typeOffset(Font.SS_NONE);
        }

        public RichSheetTerm typeOffset(short typeOffset) {
            getFont().setTypeOffset(typeOffset);
            return this;
        }

        public RichSheetTerm color() {
            return color(Font.COLOR_NORMAL);
        }

        public RichSheetTerm color(short color) {
            getFont().setColor(color);
            return this;
        }

        public RichSheetTerm height(double height) {
            getFont().setHeight(height);
            return this;
        }

        public RichSheetTerm horizontalAlignment(HorizontalAlignment horizontalAlignment) {
            getStyle().setHorizontalAlignment(horizontalAlignment);
            return this;
        }

        public RichSheetTerm horizontalAlignment(VerticalAlignment verticalAlignment) {
            getStyle().setVerticalAlignment(verticalAlignment);
            return this;
        }

        public RichSheetTerm hidden() {
            return hidden(true);
        }

        public RichSheetTerm hidden(boolean hidden) {
            getStyle().setHidden(hidden);
            return this;
        }

        public RichSheetTerm wrapText() {
            return wrapText(true);
        }

        public RichSheetTerm wrapText(boolean wrapText) {
            getStyle().setWrapText(wrapText);
            return this;
        }

        public RichSheetTerm locked() {
            return locked(true);
        }

        public RichSheetTerm locked(boolean locked) {
            getStyle().setLocked(locked);
            return this;
        }

        public RichSheetTerm quotePrefix() {
            return quotePrefix(true);
        }

        public RichSheetTerm quotePrefix(boolean quotePrefix) {
            getStyle().setQuotePrefix(quotePrefix);
            return this;
        }

        public RichSheetTerm shrinkToFit() {
            return shrinkToFit(true);
        }

        public RichSheetTerm shrinkToFit(boolean shrinkToFit) {
            getStyle().setShrinkToFit(shrinkToFit);
            return this;
        }

        public RichStyleTerm richStyle() {
            return new RichStyleTerm(this, style);
        }

        private ExcelFont getFont() {
            if (font == null) {
                font = new ExcelFont();
                cell.setFont(font);
            }
            return font;
        }

        private ExcelStyle getStyle() {
            if (style == null) {
                style = new ExcelStyle();
                cell.setStyle(style);
            }
            return style;
        }

    }

    public static class RichStyleTerm {
        private final RichSheetTerm richSheetTerm;
        private ExcelRichStyle richStyle;

        public RichStyleTerm(RichSheetTerm richSheetTerm, ExcelStyle style) {
            this.richSheetTerm = richSheetTerm;
            // fixme replace BeanCopyUtil with asm
            if (style == null) {
                this.richStyle = new ExcelRichStyle();
            } else {
                this.richStyle = BeanCopyUtil.copy(style, ExcelRichStyle.class);
            }
            richSheetTerm.cell.setStyle(this.richStyle);
        }

        public RichSheetTerm finish() {
            this.richStyle = null;
            return richSheetTerm;
        }

        public RichStyleTerm rotation(short rotation) {
            richStyle.setRotation(rotation);
            return this;
        }

        public RichStyleTerm bgColor(short bgColor) {
            richStyle.setBgColor(bgColor);
            return this;
        }

        public RichStyleTerm fgColor(short fgColor) {
            richStyle.setFgColor(fgColor);
            return this;
        }

        public RichStyleTerm fillPattern(FillPatternType fillPatternType) {
            richStyle.setFillPattern(fillPatternType);
            return this;
        }

        public RichStyleTerm borderBottom(BorderStyle borderBottom) {
            richStyle.setBorderBottom(borderBottom);
            return this;
        }

        public RichStyleTerm borderLeft(BorderStyle borderLeft) {
            richStyle.setBorderLeft(borderLeft);
            return this;
        }

        public RichStyleTerm borderTop(BorderStyle borderTop) {
            richStyle.setBorderTop(borderTop);
            return this;
        }

        public RichStyleTerm borderRight(BorderStyle borderRight) {
            richStyle.setBorderRight(borderRight);
            return this;
        }

        public RichStyleTerm bottomBorderColor(short bottomBorderColor) {
            richStyle.setBottomBorderColor(bottomBorderColor);
            return this;
        }

        public RichStyleTerm leftBorderColor(short leftBorderColor) {
            richStyle.setLeftBorderColor(leftBorderColor);
            return this;
        }

        public RichStyleTerm topBorderColor(short topBorderColor) {
            richStyle.setTopBorderColor(topBorderColor);
            return this;
        }

        public RichStyleTerm rightBorderColor(short rightBorderColor) {
            richStyle.setRightBorderColor(rightBorderColor);
            return this;
        }
    }
}
