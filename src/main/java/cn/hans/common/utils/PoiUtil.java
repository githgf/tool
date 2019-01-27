package cn.hans.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hans.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 解析xls|xlsx
 * @author admin
 */
public class PoiUtil {
    /**Excel 2003*/
    private static final String EXCEL_XLS = "xls";
    /**Excel 2007/2010*/
    private static final String EXCEL_XLSX = "xlsx";
    private static final String PATH = "/Users/ge-boox/Desktop";

    /**
     * 判断Excel的版本,获取Workbook
     */
    public static Workbook getWorkbok(InputStream in, File file) throws IOException {
        Workbook wb = null;
        if(file.getName().endsWith(EXCEL_XLS)){
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }


    /**
     * 判断文件是否是excel
     */

    public static void checkExcelVaild(File file) throws Exception{
        if(!file.exists()){
            throw new Exception("文件不存在");
        }
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){
            throw new Exception("文件不是Excel");
        }
    }



    /**
     * 是否是2003的excel，返回true是2003
     */
    public static boolean isExcel2003(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 是否是2007的excel，返回true是2007
     */
    public static boolean isExcel2007(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }


    //验证当前列的数据能不能转为（long）,如果可以将数据转为string返回
    public static Object parseCellToString(Cell cell){

        Object noObj = getCellValue(cell);

        if (noObj == null)return null;
        //如果是string类型
        if (noObj instanceof String){

            String no = noObj.toString();

            try {
                Long.parseLong(no);
                return no;
            } catch (NumberFormatException e) {
                System.out.println("数据转换异常！！");
                return null;
            }

        }else{
            return String.valueOf(noObj);
        }
    }

    //获取普通格式的单元格数值
    public  static Object getCellValue(Cell cell){

        if (cell == null) return null;
        int cellType = cell.getCellType();

        if (cellType == Cell.CELL_TYPE_NUMERIC){return cell.getNumericCellValue();}
        else
        if (cellType == Cell.CELL_TYPE_BOOLEAN){return cell.getBooleanCellValue();}
        else
        if (cellType == Cell.CELL_TYPE_STRING){return cell.getStringCellValue();}
        else
        if (cellType == Cell.CELL_TYPE_ERROR){return "非法字符";}
        else
        if (cellType == Cell.CELL_TYPE_BLANK){return "";}
        else{return cell.getStringCellValue();}

    }

    //获取格式之后，进行格式化
    public static Object getCellValueAndFormat(Cell cell, FormulaEvaluator formulaEvaluator){
        if (cell == null) return null;
        int cellType = cell.getCellType();

        if (cellType == Cell.CELL_TYPE_FORMULA && formulaEvaluator != null){
            return String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
        }else {
            return getCellValue(cell);
        }

    }

}
