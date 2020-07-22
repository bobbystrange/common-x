package org.dreamcat.common.x.excel.file;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Create by tuke on 2018/10/16
 */
@Slf4j
@Getter
public class XssfFile implements AutoCloseable {

    private static final int colWidth = 800;

    private final Workbook workbook;

    private XssfFile(Workbook workbook) {
        this.workbook = workbook;
    }

    // MS Office 2007+
    public static XssfFile open() {
        Workbook workbook = new XSSFWorkbook();
        ;
        return new XssfFile(workbook);
    }

    public static XssfFile open(File file) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(file);
        return new XssfFile(workbook);
    }

    // MS Office 2003
    public static XssfFile openHssf() throws IOException {
        Workbook workbook = new HSSFWorkbook();
        ;
        return new XssfFile(workbook);
    }

    public static XssfFile openHssf(File file) throws IOException {
        return openHssf(file, true);
    }

    public static XssfFile openHssf(File file, boolean readOnly) throws IOException {
        Workbook workbook = new HSSFWorkbook(new POIFSFileSystem(file, readOnly));
        return new XssfFile(workbook);
    }

    private static void addToRow(Row row, List<String> list, CellStyle cellStyle, int[] columnsWidth) {
        Cell cell;
        for (int i = 0; i < list.size(); i++) {
            cell = row.createCell(i);
            String cellValue = list.get(i);
            cell.setCellValue(cellValue);
            cell.setCellStyle(cellStyle);

            int width = cellValue.length() * colWidth;

            if (columnsWidth[i] < width)
                columnsWidth[i] = width;
        }
    }

    public <T> XssfFile createSheet(
            String sheetname,
            String[] heads,
            List<T> list,
            Function<T, List<String>> rowMapper) {
        return createSheet(sheetname, Arrays.asList(heads), list, rowMapper);
    }

    public <T> XssfFile createSheet(
            String sheetname,
            List<String> heads,
            List<T> context,
            Function<T, List<String>> rowMapper) {
        int size = heads.size();
        Sheet sheet = workbook.createSheet(sheetname);

        // 添加表头行
        Row headRow = sheet.createRow(0);
        // 设置单元格格式居中
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.GENERAL);

        XSSFFont font = (XSSFFont) workbook.createFont();//创建字体对象
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeight(18D);
//        XSSFFont font = new XSSFFont();
//        font.setBold(true);
//        font.setFontHeight(18D);
        cellStyle.setFont(font);


        //根据传入的titles, 创建列宽的数组
        int[] columnsWidth = new int[size];
        // 添加表头内容
        addToRow(headRow, heads, cellStyle, columnsWidth);

        // 添加数据内容
        Row row;
        Cell cell;
        for (int i = 0; i < context.size(); i++) {
            row = sheet.createRow(i + 1);
            T entity = context.get(i);

            List<String> list = rowMapper.apply(entity);
            if (list.size() != heads.size()) {
                throw new IllegalArgumentException("heads' size is not equal to context's list size");
            }
            addToRow(row, list, cellStyle, columnsWidth);
        }

        for (int i = 0; i < size; i++) {
            sheet.setColumnWidth(i, columnsWidth[i]);
        }
        return this;
    }

    public Sheet getSheet(String name) {
        return workbook.getSheet(name);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Sheet getSheetAt(int index) {
        return workbook.getSheetAt(index);
    }

    public void write(String newFile) throws IOException {
        write(new File(newFile));
    }

    public void write(File newFile) throws IOException {
        try (OutputStream ostream = new FileOutputStream(newFile)) {
            workbook.write(ostream);
        }
    }

    public void write(OutputStream ostream) throws IOException {
        workbook.write(ostream);
    }

    public byte[] getBytes() {
        try (ByteArrayOutputStream ostream = new ByteArrayOutputStream()) {
            workbook.write(ostream);
            return ostream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public void close() throws IOException {
        workbook.close();
    }
}
