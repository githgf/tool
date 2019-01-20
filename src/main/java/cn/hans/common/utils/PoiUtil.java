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

    public static JSONArray getObjects(Workbook wb) {
        String sheetName = wb.getSheetName(0);
        Sheet sheet = null;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.getSheet(sheetName);
        }

        JSONArray list = new JSONArray();
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {

            JSONObject jsonObject = new JSONObject();
            Row row = sheet.getRow(i);
            if(row == null){
                continue;
            }
            if (row.getCell(1) != null){
                JSONObject orderInfoVO = makeOrderInfo(row, 1, 1);
                jsonObject.put("orderInfoVO",orderInfoVO);
                JSONObject orderUserInfoVO = makeOrderUserInfo(row);
                jsonObject.put("orderUserInfoVO", orderUserInfoVO);
                JSONObject orderSenderInfoVO = makeOrderSendUserInfo(row);
                jsonObject.put("orderSenderInfoVO",orderSenderInfoVO);
                JSONObject orderShippingInfoVO = makeShippingInfo(row);
                jsonObject.put("orderShippingInfoVO", orderShippingInfoVO);
                JSONArray orderPackageVOList = makePackageInfo(row);
                jsonObject.put("orderPackageVOList", orderPackageVOList);
                JSONArray orderItemList = makeItemInfo(row);
                jsonObject.put("items", orderItemList);
                if(row.getCell(40) != null){
                    jsonObject.put("needPick", Boolean.valueOf(row.getCell(40).getStringCellValue()));
                }
                list.add(jsonObject);
            }

        }
        return list;
    }

    /**
     * TODO 将有多个sheet的workBook解析为map对象
     * @param wb
     * @return
     */
    public static Map<String,Map<String,Map<String,Object>>> getObjectsPlus(Workbook wb) {
        Map<String,Map<String,Map<String,Object>>> dataMap = new LinkedHashMap<>();

        Map<String,Map<String,Object>> mapList = null;
        String sheetName = null;
        Sheet sheet = null;
        for (int i = 0; i < 4; i++) {

            sheet = wb.getSheetAt(i);
            if (sheet == null)return null;
            sheetName = sheet.getSheetName();
            if (StringUtils.isBlank(sheetName)) return null;
            //summary表
            if (i == 0){
                mapList = parseSummarySheet(sheet);
                sheetName = CommonConstant.SUMMARY_SHEET;
            }
            //total revd 表
            else if(i == 1){
                mapList = parseTotalRecvdSheet(sheet);
                sheetName = CommonConstant.TOTALRECVD_SHEET;
            }
            //delivered表
            else if (i == 2){
                sheetName = CommonConstant.DELIVERED_SHEET;
                mapList = parseDeliveredSheet(sheet);
            }
            //rtc
            else if (i == 3){
                sheetName = CommonConstant.RTC_SHEET;
                mapList = parseRtcSheet(sheet);
            }

            dataMap.put(sheetName,mapList);

        }

        return dataMap;
    }

    /**
     * 获取Excel数据
     * @param wb            Excel的Workbook实例
     * @param startLine     开始获取数据的行数
     * @param titleTypes    标题列类型，目前支持类型：Date,Int,BigInt/Long,BigDecimal,String(默认)
     * @param titleNames    标题列名称
     * @param isPrint       是否打印excel获取到的数据
     * @return  如果titleTypes和titleNames数据为空或长度不一致，返回null
     */
    public static JSONArray getObjectsByTitles(Workbook wb, int startLine, String[] titleTypes, String[] titleNames, boolean isPrint) {
        if(titleTypes==null || titleNames==null ||
                titleTypes.length != titleNames.length){
            return null;
        }
        String sheetName = wb.getSheetName(0);
        Sheet sheet = null;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.getSheet(sheetName);
        }

        //获取昨天22点的时间
        Date dateTemp = new Date();
        Date yesterday = new Date(dateTemp.getYear(),dateTemp.getMonth(),
                dateTemp.getDate()-1,22,0,0);
        dateTemp = null;

        JSONArray list = new JSONArray();
        try {
            String titleType, titleName;
            for (int i = startLine; i <= sheet.getLastRowNum(); i++) {
                JSONObject jsonObject = new JSONObject();
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                System.out.println("---------" + (i+1) + "---------");
                for (int index = 0; index < titleTypes.length; index++) {
                    titleType = titleTypes[index].toLowerCase();
                    titleName = titleNames[index];

                    if(StringUtils.isBlank(titleName) ||
                            (index == 0 && StringUtils.isBlank(row.getCell(index).toString()))){
                        continue;
                    }
                    if(titleType.startsWith("date")){
                        Date value = row.getCell(index).getDateCellValue();
                        if(value==null){
                            value = yesterday;
                        }else {
                            value.setHours(22);
                            value.setMinutes(0);
                            value.setSeconds(0);
                        }
                        jsonObject.put(titleName, value);
                        if (isPrint) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            System.out.println("[" + index + "]" + titleName + ":" + sdf.format(value) + ";");
                            sdf = null;
                        }

//                        value = row.getCell(index).toString();
//                        value = value.replace("年","")
//                                .replace("月","")
//                                .replace("日","");
                    }else if(titleType.startsWith("int")){
                        int value = (int)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("bigint")||titleType.startsWith("long")){
                        long value = (long)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("double")){
                        double value = (double)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("bigdecimal")){
                        BigDecimal value = new BigDecimal(row.getCell(index).toString());
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("stringdate")){
                        String value = "";
                        Date dateValue = null;
                        if(row.getCell(index)!=null){
                            value = row.getCell(index).toString();
                            if(StringUtils.isNotBlank(value) && value.length()>9) {
//                              SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                dateValue = sdf.parse(value.split(" ")[0].trim());
                                dateValue.setHours(22);
                                dateValue.setMinutes(0);
                                dateValue.setSeconds(0);
                                sdf = null;
                            }
                        }
                        if(dateValue==null){
                            dateValue = yesterday;
                        }
                        jsonObject.put(titleName,dateValue);
                        if(isPrint) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            System.out.println("[" + index + "]" + titleName + ":" + sdf.format(dateValue) + "(value:"+value+");");
                            sdf = null;
                        }
                    }else{
                        String value = row.getCell(index).toString();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }
                }
                list.add(jsonObject);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        yesterday = null;
        return list;
    }

    /**
     * 获取Excel数据（只获取一个运单号）
     * @param wb            Excel的Workbook实例
     * @param startLine     开始获取数据的行数
     * @param titleTypes    标题列类型，目前支持类型：Date,Int,BigInt/Long,BigDecimal,String(默认)
     * @param titleNames    标题列名称
     * @param isPrint       是否打印excel获取到的数据
     * @return  如果titleTypes和titleNames数据为空或长度不一致，返回null
     */
    public static JSONArray getObjectsByTitlesWithDelivered(Workbook wb, int startLine, String[] titleTypes, String[] titleNames, boolean isPrint) {
        if(titleTypes==null || titleNames==null ||
                titleTypes.length != titleNames.length){
            return null;
        }
        String sheetName = wb.getSheetName(0);
        Sheet sheet = null;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.getSheet(sheetName);
        }

        //获取昨天22点的时间
        Date dateTemp = new Date();
        Date yesterday = new Date(dateTemp.getYear(),dateTemp.getMonth(),
                dateTemp.getDate()-1,22,0,0);
        dateTemp = null;

        JSONArray list = new JSONArray();
        try {
            String titleType, titleName;
            for (int i = startLine; i <= sheet.getLastRowNum(); i++) {
                JSONObject jsonObject = new JSONObject();
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                jsonObject.put("Status","delivered");
                System.out.println("---------" + (i+1) + "---------");
                for (int index = 0; index < titleTypes.length; index++) {
                    titleType = titleTypes[index].toLowerCase();
                    titleName = titleNames[index];

                    if(StringUtils.isBlank(titleName) ||
                            (index == 0 && StringUtils.isBlank(row.getCell(index).toString()))){
                        continue;
                    }
                    if(titleType.startsWith("date")){
                        Date value = row.getCell(index).getDateCellValue();
                        if(value==null){
                            value = yesterday;
                        }else {
                            value.setHours(22);
                            value.setMinutes(0);
                            value.setSeconds(0);
                        }
                        jsonObject.put(titleName, value);
                        if (isPrint) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            System.out.println("[" + index + "]" + titleName + ":" + sdf.format(value) + ";");
                            sdf = null;
                        }

//                        value = row.getCell(index).toString();
//                        value = value.replace("年","")
//                                .replace("月","")
//                                .replace("日","");
                    }else if(titleType.startsWith("int")){
                        int value = (int)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("bigint")||titleType.startsWith("long")){
                        long value = (long)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("double")){
                        double value = (double)row.getCell(index).getNumericCellValue();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("bigdecimal")){
                        BigDecimal value = new BigDecimal(row.getCell(index).toString());
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }else if(titleType.startsWith("stringdate")){
                        String value = "";
                        Date dateValue = null;
                        if(row.getCell(index)!=null){
                            value = row.getCell(index).toString();
                            if(StringUtils.isNotBlank(value) && value.length()>9) {
//                              SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                dateValue = sdf.parse(value.split(" ")[0].trim());
                                dateValue.setHours(22);
                                dateValue.setMinutes(0);
                                dateValue.setSeconds(0);
                                sdf = null;
                            }
                        }
                        if(dateValue==null){
                            dateValue = yesterday;
                        }
                        jsonObject.put(titleName,dateValue);
                        if(isPrint) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            System.out.println("[" + index + "]" + titleName + ":" + sdf.format(dateValue) + "(value:"+value+");");
                            sdf = null;
                        }
                    }else{
                        String value = row.getCell(index).toString();
                        jsonObject.put(titleName,value);
                        if(isPrint) {
                            System.out.println("[" + index + "]" + titleName + ":" + value + ";");
                        }
                    }
                }
                list.add(jsonObject);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        yesterday = null;
        return list;
    }

    public static JSONArray getExcelInfo(MultipartFile Mfile){

        JSONArray result = null;
        CommonsMultipartFile cf= (CommonsMultipartFile)Mfile; //获取本地存储路径
        File file = new  File(PATH);
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        if (!file.exists()) {
            file.mkdirs();
        }
        //新建一个文件
        File file1 = new File(PATH + DateUtils.getDate() + ".xlsx");

        //将上传的文件写入新建的文件中
        try {
            cf.getFileItem().write(file1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileFileName = Mfile.getOriginalFilename();
        if (fileFileName == null || !fileFileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
            return null;
        }
        String fileType = "2003";
        if (!isExcel2003(fileFileName)) {
            fileType = "2007";
        }
        try {
            Workbook wb = null;
            if (fileType.equals("2003")) {
                wb = new HSSFWorkbook(Mfile.getInputStream());
            }
            if (fileType.equals("2007")) {
                wb = new XSSFWorkbook(Mfile.getInputStream());
            }
            result = getObjects(wb);
        }catch (Exception e){

        }
        return result;
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

    public static JSONObject makeOrderInfo(Row row, int indexStart, int indexEnd){
        JSONObject result = new JSONObject();

        if (row.getCell(1) != null) {
            String orderSn = row.getCell(1).toString();
            result.put("orderSn", orderSn);
        }

        Double goodsNum;
        if (row.getCell(2) != null) {
            goodsNum = row.getCell(2).getNumericCellValue();
            result.put("goodsNum", goodsNum.intValue());
        }

        if(row.getCell(3) != null){
            String str = row.getCell(3).toString();
            if(StringUtils.isNotBlank(str)){
                BigDecimal orderAmount = new BigDecimal(str);
                result.put("orderAmount", orderAmount);
            }

        }

        if(row.getCell(4) != null){
            String str = row.getCell(4).toString();
            if(StringUtils.isNotBlank(str)){
                BigDecimal codeAmount = new BigDecimal(str);
                result.put("codAmount", codeAmount);
            }
        }

        if(row.getCell(5) != null){
            String currency = row.getCell(5).toString();
            result.put("currency", currency);
        }
        return result;
    }

    public static JSONObject makeOrderUserInfo(Row row){
        JSONObject result = new JSONObject();

        if (row.getCell(6) != null) {
            result.put("country", row.getCell(6).toString());
        }

        if (row.getCell(7) != null) {
            result.put("province", row.getCell(7).toString());
        }

        if(row.getCell(8) != null){
            result.put("city", row.getCell(8).toString());
        }

        if(row.getCell(9) != null){
            result.put("district", row.getCell(9).toString());
        }

        if(row.getCell(10) != null){
            result.put("landMark", row.getCell(10).toString());
        }
        if(row.getCell(11) != null){
            result.put("address", row.getCell(11).toString());
        }
        if(row.getCell(12) != null){
            result.put("lng", row.getCell(12).getNumericCellValue());
        }
        if(row.getCell(13) != null){
            result.put("lat", row.getCell(13).getNumericCellValue());
        }
        if(row.getCell(14) != null){
            result.put("mobile", row.getCell(14).toString());
        }
        if(row.getCell(15) != null){
            result.put("firstName", row.getCell(15).toString());
        }
        if(row.getCell(16) != null){
            result.put("lastName", row.getCell(16).toString());
        }
        return result;
    }

    public static JSONObject makeOrderSendUserInfo(Row row){
        JSONObject result = new JSONObject();

        if (row.getCell(17) != null) {
            result.put("country", row.getCell(17).toString());
        }

        if (row.getCell(18) != null) {
            result.put("province", row.getCell(18).toString());
        }

        if(row.getCell(19) != null){
            result.put("city", row.getCell(19).toString());
        }

        if(row.getCell(20) != null){
            result.put("district", row.getCell(20).toString());
        }

        if(row.getCell(21) != null){
            result.put("landMark", row.getCell(21).toString());
        }
        if(row.getCell(22) != null){
            result.put("address", row.getCell(22).toString());
        }
        if(row.getCell(23) != null){
            result.put("lng", row.getCell(23).getNumericCellValue());
        }
        if(row.getCell(24) != null){
            result.put("lat", row.getCell(24).getNumericCellValue());
        }
        if(row.getCell(25) != null){
            result.put("mobile", row.getCell(25).toString());
        }
        if(row.getCell(26) != null){
            result.put("firstName", row.getCell(26).toString());
        }
        if(row.getCell(27) != null){
            result.put("lastName", row.getCell(27).toString());
        }
        return result;
    }

    public static JSONObject makeShippingInfo(Row row){
        JSONObject result = new JSONObject();
        if(row.getCell(28) != null){
            String str = row.getCell(28).toString();
            if(StringUtils.isNotBlank(str)){
                result.put("packageWeight", new Double(str));
            }
        }
        if(row.getCell(29) != null){
            String str = row.getCell(29).toString();
            if(StringUtils.isNotBlank(str)){
                result.put("volume", new Double(str));
            }
        }
        if(row.getCell(30) != null){
            String str = row.getCell(30).toString();
            if(StringUtils.isNotBlank(str)){
                Double totalPackages = new Double(str);
                result.put("totalPackages", totalPackages.intValue());
            }

        }
        return result;
    }

    public static JSONArray makePackageInfo(Row row){
        JSONArray result = new JSONArray();
        JSONObject resultObj = new JSONObject();

        if(row.getCell(31) != null){
            String str = row.getCell(31).toString();
            if(StringUtils.isNotBlank(str)){
                resultObj.put("materialName", str);
            }

        }
        if(row.getCell(32) != null){
            String str = row.getCell(32).toString();
            if(StringUtils.isNotBlank(str)){
                resultObj.put("materialWeight", str);
            }
        }
        if(row.getCell(33) != null){
            String str = row.getCell(33).toString();
            if(StringUtils.isNotBlank(str)){
                resultObj.put("materialVolume", str);
            }
        }
        if(row.getCell(34) != null){
            resultObj.put("materialStandard", row.getCell(34).toString());
        }
        result.add(resultObj);
        return result;
    }

    public static JSONArray makeItemInfo(Row row){
        JSONArray result = new JSONArray();
        JSONObject resultObj = new JSONObject();

        if(row.getCell(35) != null){
            resultObj.put("productNameEN", row.getCell(35).toString());
        }
        if(row.getCell(36) != null){
            resultObj.put("productNameCN", row.getCell(36).toString());
        }
        if(row.getCell(37) != null){
            String str = row.getCell(37).toString();
            if(StringUtils.isNotBlank(str)){
                resultObj.put("num", Integer.valueOf(str));
            }
        }
        if(row.getCell(38) != null){
            String str = row.getCell(38).toString();
            if(StringUtils.isNotBlank(str)){
                resultObj.put("decaleValue", new BigDecimal(str));
            }
        }
        if(row.getCell(39) != null){
            resultObj.put("code", row.getCell(39).toString());
        }
        result.add(resultObj);
        return result;
    }



    //将totalrecvd工作表转为jsonObject 相当于map<string,jsonObject(expressInfo)>
    public static Map<String, Map<String,Object>> parseTotalRecvdSheet(Sheet sheet){
        Map<String, Map<String,Object>> totalRecvdObj = new LinkedHashMap<>();
        Map<String,Object> expressInfo = null;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            expressInfo = new JSONObject();
            Row row = sheet.getRow(i);
            if(row == null){
                continue;
            }
            expressInfo = makeExpressInfo(row);
            if (expressInfo == null) continue;

            totalRecvdObj.put(expressInfo.get("expressNo").toString(),expressInfo);

        }

        return totalRecvdObj;

    }
    //将delibered工作表转为jsonObject 相当于map<string,jsonObject(ExpressAndDeliveredInfo)>
    public static Map<String, Map<String,Object>> parseDeliveredSheet(Sheet sheet){
        Map<String, Map<String,Object>> deliveredObj = new LinkedHashMap<>();
        Map<String,Object> expressAndDeliveredInfo = null;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            expressAndDeliveredInfo = new JSONObject();
            Row row = sheet.getRow(i);
            if(row == null){
                continue;
            }
            expressAndDeliveredInfo = makeExpressAndDeliveredInfo(row);
            if (expressAndDeliveredInfo == null) continue;

            deliveredObj.put(expressAndDeliveredInfo.get("expressNo").toString(),expressAndDeliveredInfo);

        }

        return deliveredObj;

    }
    //将rtc工作表转为jsonObj  相当于map<string,jsonObject(ExpressAndDeliveredInfo)>
    public static Map<String, Map<String,Object>> parseRtcSheet(Sheet sheet){
        Map<String,Map<String,Object>> rtcObject = new LinkedHashMap<>();
        Map<String,Object> expressAndBackInfo = null;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            expressAndBackInfo = new JSONObject();
            Row row = sheet.getRow(i);
            if(row == null){
                continue;
            }
            expressAndBackInfo = makeExpressAndBackInfo(row);
            if (expressAndBackInfo == null) continue;

            rtcObject.put(expressAndBackInfo.get("expressNo").toString(),expressAndBackInfo);

        }

        return rtcObject;

    }
    //转Summary为Map<String, JSONObject>形式
    public static Map<String, Map<String,Object>> parseSummarySheet(Sheet sheet){

        Map<String, Map<String,Object>> summaryMap = new LinkedHashMap<>();
        Map<String,Object> summaryInfo = new JSONObject();

        Row row = null;
        Object verifyCell = null;
        if(row == null) return summaryMap;

        //循环找出正常的数据行的index，条件就是判断每一行的第一个单元格
        for (int i = 0; i < sheet.getLastRowNum(); i++) {

            row = sheet.getRow(1);
            //，如果符合判断规则，退出
            verifyCell = parseCellToString(row.getCell(0));

            if (verifyCell != null){break;}

        }
        if (row.getCell(0) != null){
            summaryInfo.put("totalReceivedNum",Integer.parseInt(verifyCell.toString()));
        }
        if (row.getCell(1) != null){
            summaryInfo.put("deliveredNum",Integer.parseInt(verifyCell.toString()));
        }
        if (row.getCell(2) != null){
            summaryInfo.put("RTCNum",getCellValue(row.getCell(2)));
        }
        if (row.getCell(3) != null){
            summaryInfo.put("CODAmount",getCellValue(row.getCell(3)));
        }
        if (row.getCell(4) != null){
            summaryInfo.put("DeliveryCharge",getCellValue(row.getCell(4)));
        }
        if (row.getCell(5) != null){
            summaryInfo.put("RefundAmount",getCellValue(row.getCell(5)));
        }

        summaryMap.put("excelSummary",summaryInfo);
        return summaryMap;

    }

    //解析为express和orderBackLog实体类,如果no、expressNo单元格不是数字，那么则判定不是数据行，跳过
    public static JSONObject makeExpressAndBackInfo(Row row){

        Object cellValueObj = parseCellToString(row.getCell(0));
        if (cellValueObj == null)return null;

        JSONObject result = new JSONObject();
        if (row.getCell(1) != null) {
            result.put("orderNo", getCellValue(row.getCell(1)));
        }
        cellValueObj = parseCellToString(row.getCell(2));
        if (cellValueObj == null)return null;

        result.put("expressNo", cellValueObj.toString());
        if (row.getCell(3) != null) {
            result.put("excelCreateTime", getCellValue(row.getCell(3)));
        }
//        if (row.getCell(4) != null){
//            String actualReceiveDate = row.getCell(4).toString();
//            result.put("actualReceiveDate",actualReceiveDate);
//        }

        if (row.getCell(4) != null){
            result.put("type", getCellValue(row.getCell(4)));
        }
        if (row.getCell(5) != null){
            result.put("orderAmount",getCellValue(row.getCell(5)));
        }
        if (row.getCell(6) != null){
            result.put("reason",getCellValue(row.getCell(6)));
        }
        if (row.getCell(7) != null){
            result.put("excelReturnDate",getCellValue(row.getCell(7)));
        }

        return result;
    }

    //解析为express和delivered实体类，如果no、expressNo单元格不是数字，那么则判定不是数据行，跳过
    public static JSONObject makeExpressAndDeliveredInfo(Row row){

        Object cellValue = parseCellToString(row.getCell(0));

        if (cellValue == null)return null;

        JSONObject result = new JSONObject();
        if (row.getCell(1) != null) {
            result.put("orderNo", getCellValue(row.getCell(1)));
        }

        cellValue = parseCellToString(row.getCell(2));
        if (cellValue == null){return null;}
        result.put("expressNo", cellValue.toString());

        if (row.getCell(3) != null) {
            result.put("excelCreateTime", getCellValue(row.getCell(3)));
        }
        if (row.getCell(4) != null){
            result.put("type", getCellValue(row.getCell(4)));
        }
        if (row.getCell(5) != null){
            result.put("orderAmount",getCellValue(row.getCell(5)));
        }
//        if (row.getCell(6) != null){
//            result.put("excelStatus",getCellValue(row.getCell(6)));
//        }
        if (row.getCell(6) != null){
            result.put("excelDeliveredDate",getCellValue(row.getCell(6)));
        }

        return result;
    }

    //解析为express实体类，如果no、expressNo单元格不是数字，那么则判定不是数据行，跳过
    public static Map<String, Object> makeExpressInfo(Row row){

        Object cellValue = parseCellToString(row.getCell(0));
        if (cellValue == null){return null;}

        Map<String, Object> result = new LinkedHashMap<>();
        if (row.getCell(1) != null) {
            result.put("orderNo", getCellValue(row.getCell(1)));
        }

        cellValue = parseCellToString(row.getCell(2));
        if (cellValue == null)return null;
        result.put("expressNo", cellValue.toString());

        if (row.getCell(3) != null) {
            result.put("excelCreateTime", getCellValue(row.getCell(3)));
        }
        if (row.getCell(4) != null){
            result.put("orderAmount",getCellValue(row.getCell(4)));
        }

        return result;
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
