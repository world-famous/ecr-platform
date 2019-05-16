package com.tianwen.springcloud.ecrapi.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel
{
    private int totalRows = 0;  

    private int totalCells = 0;  

    private String errorInfo;

    private static MissingCellPolicy xRow;

    public ReadExcel()
    {  
  
    }  

    public int getTotalRows()  
    {  
  
        return totalRows;  
  
    }  

    public int getTotalCells()  
    {  
  
        return totalCells;  
  
    }  

    public String getErrorInfo()  
    {  
  
        return errorInfo;  
  
    }  

    public boolean validateExcel(String filePath)  
    {  

  
        if (filePath == null || !(WDWUtil.isExcel2003(filePath) || WDWUtil.isExcel2007(filePath)))  
        {  
  
            errorInfo = "文件类型错了！";
  
            return false;  
  
        }  

        File file = new File(filePath);  
  
        if (file == null || !file.exists())  
        {  
  
            errorInfo = "文件不存在！";
  
            return false;  
  
        }  
  
        return true;  
  
    }  

    public List<List<String>> read(String filePath)  
    {  
  
        List<List<String>> dataLst = new ArrayList<List<String>>();  
  
        InputStream is = null;  
  
        try  
        {  

            if (!validateExcel(filePath))  
            {  
  
                System.out.println(errorInfo);  
  
                return null;  
  
            }  

            boolean isExcel2003 = true;  
  
            if (WDWUtil.isExcel2007(filePath))  
            {  
  
                isExcel2003 = false;  
  
            }  

            File file = new File(filePath);  
  
            is = new FileInputStream(file);  
  
            dataLst = read(is, isExcel2003);  
  
            is.close();  
  
        }  
        catch (Exception ex)  
        {  
  
            ex.printStackTrace();  
  
        }  
        finally  
        {  
  
            if (is != null)  
            {  
  
                try  
                {  
  
                    is.close();  
  
                }  
                catch (IOException e)  
                {  
  
                    is = null;  
  
                    e.printStackTrace();  
  
                }  
  
            }  
  
        }  

  
        return dataLst;  
  
    }  

    public List<List<String>> read(InputStream inputStream, boolean isExcel2003)  
    {  
  
        List<List<String>> dataLst = null;  
  
        try  
        {
  
            Workbook wb = null;  
  
            if (isExcel2003)  
            {  
                wb = new HSSFWorkbook(inputStream);  
            }  
            else  {
                wb = new XSSFWorkbook(inputStream);
            }  
            dataLst = read(wb);
  
        }  
        catch (IOException e)  
        {
  
            e.printStackTrace();
  
        }  
  
        return dataLst;  
  
    }  

    private List<List<String>> read(Workbook wb)  
    {  
  
        List<List<String>> dataLst = new ArrayList<List<String>>();  

        Sheet sheet = wb.getSheetAt(0);  

        this.totalRows = sheet.getPhysicalNumberOfRows();  

        if (this.totalRows >= 1 && sheet.getRow(0) != null)  
        {  
  
            this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();  
  
        }  

        for (int r = 0; r < this.totalRows; r++)  
        {  
  
            Row row = sheet.getRow(r);  
  
            if (row == null)  
            {  
  
                continue;  
  
            }  
  
            List<String> rowLst = new ArrayList<String>();

            // Totalcells of current row
            int totalCells = row.getPhysicalNumberOfCells();

            for (int c = 0; c < this.totalCells; c++)
            {  
  
                Cell cell = row.getCell(c);
  
                String cellValue = "";  
  
                if (null != cell)  
                {
                    switch (cell.getCellType())  
                    {  
                    case HSSFCell.CELL_TYPE_NUMERIC: // ����  
                        cellValue = cell.getNumericCellValue() + "";  
                        break;  
  
                    case HSSFCell.CELL_TYPE_STRING: // �ַ���  
                        cellValue = cell.getStringCellValue();  
                        break;  
  
                    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean  
                        cellValue = cell.getBooleanCellValue() + "";  
                        break;  
  
                    case HSSFCell.CELL_TYPE_FORMULA: // ��ʽ  
                        cellValue = cell.getCellFormula() + "";  
                        break;  
  
                    case HSSFCell.CELL_TYPE_BLANK: // ��ֵ  
                        cellValue = "";  
                        break;  
  
                    case HSSFCell.CELL_TYPE_ERROR: // ����  
                        cellValue = "";
                        break;  
  
                    default:  
                        cellValue = "";
                        break;  
                    }  
                }  
  
                rowLst.add(cellValue);  
  
            }  

            dataLst.add(rowLst);  
  
        }  
  
        return dataLst;  
  
    }  

}  

class WDWUtil  
{  

    public static boolean isExcel2003(String filePath)  
    {  
  
        return filePath.matches("^.+\\.(?i)(xls)$");  
  
    }  

    public static boolean isExcel2007(String filePath)  
    {  
  
        return filePath.matches("^.+\\.(?i)(xlsx)$");  
  
    }  
  
}