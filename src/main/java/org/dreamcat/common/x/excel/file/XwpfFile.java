package org.dreamcat.common.x.excel.file;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Create by tuke on 2018/10/20
 */
public class XwpfFile implements AutoCloseable {

    private final XWPFDocument document;

    private XwpfFile(XWPFDocument document) {
        this.document = document;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static XwpfFile open() {
        XWPFDocument document = new XWPFDocument();
        return new XwpfFile(document);
    }

    public static XwpfFile open(String path) throws IOException {
        XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(path));
        return new XwpfFile(document);
    }

    public static XwpfFile open(File file) throws IOException {
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        return new XwpfFile(document);
    }

    // paragraph
    public XwpfFile createParagraph(List<String> texts) {
        XWPFParagraph paragraph = document.createParagraph();

        for (String text : texts) {
            XWPFRun run = paragraph.createRun();
            run.setText(text);
        }
        return this;
    }

    // chart
    public XwpfFile createChart(XssfFile xssfFile) throws IOException, InvalidFormatException {
        XWPFChart chart = document.createChart();
        chart.setWorkbook((XSSFWorkbook) xssfFile.getWorkbook());
        return this;
    }

    public void write(String file) throws IOException {
        write(new FileOutputStream(file));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public void write(File file) throws IOException {
        write(new FileOutputStream(file));
    }

    public void write(OutputStream ostream) throws IOException {
        document.write(ostream);
    }

    @Override
    public void close() throws IOException {
        document.close();
    }
}
