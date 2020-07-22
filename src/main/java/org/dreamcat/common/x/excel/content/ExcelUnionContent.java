package org.dreamcat.common.x.excel.content;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Create by tuke on 2020/7/22
 */
@SuppressWarnings("rawtypes")
public class ExcelUnionContent implements IExcelContent {
    private final ExcelStringContent stringContent;
    private final ExcelNumericContent numericContent;
    private final ExcelBooleanContent booleanContent;
    private transient IExcelContent rawContent;
    private transient Class type;

    protected ExcelUnionContent() {
        this.stringContent = new ExcelStringContent();
        this.numericContent = new ExcelNumericContent();
        this.booleanContent = new ExcelBooleanContent();
    }

    public ExcelUnionContent(String value) {
        this();
        setStringContent(value);
    }

    public ExcelUnionContent(double value) {
        this();
        setNumericContent(value);
    }

    public ExcelUnionContent(boolean value) {
        this();
        setBooleanContent(value);
    }

    public void setStringContent(String value) {
        this.stringContent.setValue(value);
        this.type = ExcelStringContent.class;
    }

    public void setNumericContent(double value) {
        this.numericContent.setValue(value);
        this.type = ExcelNumericContent.class;
    }

    public void setBooleanContent(boolean value) {
        this.booleanContent.setValue(value);
        this.type = ExcelBooleanContent.class;
    }

    public void setRawContent(IExcelContent rawContent) {
        this.rawContent = rawContent;
        this.type = IExcelContent.class;
    }

    @Override
    public void fill(Cell cell) {
        if (type.equals(ExcelStringContent.class)) {
            stringContent.fill(cell);
        } else if (type.equals(ExcelNumericContent.class)) {
            numericContent.fill(cell);
        } else if (type.equals(ExcelBooleanContent.class)) {
            booleanContent.fill(cell);
        } else if (rawContent != null) {
            rawContent.fill(cell);
        }
    }
}
