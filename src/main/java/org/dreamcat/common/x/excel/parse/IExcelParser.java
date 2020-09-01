package org.dreamcat.common.x.excel.parse;

import org.dreamcat.common.x.excel.map.ExcelMapper;

import java.io.File;
import java.util.List;

/**
 * Create by tuke on 2020/8/14
 */
public interface IExcelParser<T> {

    default List<T> readSheetAsValue(File excelFile, int sheetIndex) throws Exception {
        return readSheetAsValue(ExcelMapper.parseAsString(excelFile, sheetIndex));
    }

    default List<T> readSheetAsValue(File excelFile, String sheetName) throws Exception {
        return readSheetAsValue(ExcelMapper.parseAsString(excelFile, sheetName));
    }

    List<T> readSheetAsValue(List<List<String>> sheet) throws Exception;
}
