package com.city.common.util.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by wys on 2016/2/2.
 */
public class EsiExcelUtil {
    private Map<String, Workbook> workbooks = new HashMap<>();

    public void addWorkBook(String workbookName, InputStream inputStream) throws IOException {
        Workbook wb = null;
        try {
            try {
                wb = new XSSFWorkbook(inputStream);
            } catch (OfficeXmlFileException e) {
                wb = new HSSFWorkbook(inputStream);
            }
            if (wb != null) {
                workbooks.put(workbookName, wb);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public Workbook getWorkBook(String workbookName) {
        boolean hasWorkbook = workbooks.containsKey(workbookName);
        if (hasWorkbook) {
            return workbooks.get(workbookName);
        } else {
            return null;
        }
    }

    public Map<String, List<Sheet>> getAllSheet() {
        Map<String, List<Sheet>> result = new HashMap<>();
        Set<Entry<String, Workbook>> workbookEntrySet = workbooks.entrySet();
        Workbook workbook = null;
        Sheet sheet = null;
        List<Sheet> sheets = null;
        for (Entry<String, Workbook> entry : workbookEntrySet) {
            workbook = entry.getValue();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheets = result.get(entry.getKey());
                sheet = workbook.getSheetAt(i);
                if (sheets == null) {
                    sheets = new ArrayList<>();
                    sheets.add(sheet);
                    result.put(entry.getKey(), sheets);
                } else {
                    sheets.add(sheet);
                }
            }

        }
        return result;
    }

    public CellRangeAddress getCellRangeAddress(Cell cell) {
        CellRangeAddress result = null;
        Sheet sheet = cell.getSheet();
        boolean isMerged = false;
        List<CellRangeAddress> rangs = sheet.getMergedRegions();
        for (CellRangeAddress range : rangs) {
            isMerged = range.isInRange(cell.getRowIndex(), cell.getColumnIndex());
            if (isMerged) {
                result = range;
                return result;
            }
        }
        return result;
    }
}
