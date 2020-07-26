package org.dreamcat.common.x.excel.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dreamcat.common.x.excel.content.IExcelContent;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelHyperLink;
import org.dreamcat.common.x.excel.style.ExcelStyle;

/**
 * Create by tuke on 2020/7/21
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExcelRichCell extends ExcelCell {
    protected ExcelFont font;
    protected ExcelStyle style;
    protected ExcelHyperLink hyperLink;

    public ExcelRichCell(
            IExcelContent content, int rowIndex, int columnIndex) {
        super(content, rowIndex, columnIndex);
    }

    public ExcelRichCell(
            IExcelContent content, int rowIndex, int columnIndex,
            int rowSpan, int columnSpan) {
        super(content, rowIndex, columnIndex, rowSpan, columnSpan);
    }

    public ExcelRichCell(
            IExcelContent content, int rowIndex, int columnIndex,
            int rowSpan, int columnSpan,
            ExcelFont font, ExcelStyle style, ExcelHyperLink hyperLink) {
        super(content, rowIndex, columnIndex, rowSpan, columnSpan);
        this.font = font;
        this.style = style;
        this.hyperLink = hyperLink;
    }
}
