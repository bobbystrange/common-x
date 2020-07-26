package org.dreamcat.common.x.excel.core;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelStyle;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Create by tuke on 2020/7/21
 */
@Data
public class ExcelWorkbook<T extends IExcelSheet> implements IExcelWorkbook<T> {
    private final List<T> sheets;
    private ExcelStyle defaultStyle;
    private ExcelFont defaultFont;

    public ExcelWorkbook() {
        this.sheets = new LinkedList<>();
    }

    public static ExcelWorkbook<ExcelSheet> from(File file) throws IOException, InvalidFormatException {
        return from(new XSSFWorkbook(file));
    }

    public static ExcelWorkbook<ExcelSheet> fromBigGrid(File file) throws IOException, InvalidFormatException {
        return from(new SXSSFWorkbook(new XSSFWorkbook(file)));
    }

    public static ExcelWorkbook<ExcelSheet> from2003(File file) throws IOException {
        return from(new HSSFWorkbook(new POIFSFileSystem(file, true)));
    }

    public static <T extends Workbook> ExcelWorkbook<ExcelSheet> from(T workbook) {
        ExcelWorkbook<ExcelSheet> book = new ExcelWorkbook<>();

        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            book.sheets.add(ExcelSheet.from(workbook, sheet));
        }
        return book;
    }

    @Override
    public Iterator<T> iterator() {
        return sheets.iterator();
    }

    @Override
    public IExcelWorkbook<T> add(T sheet) {
        sheets.add(sheet);
        return this;
    }
}
