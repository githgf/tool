package cn.hans.common.utils;


import cn.hans.common.constant.CommonConstant;
import cn.hans.common.utils.PoiUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 
 * 共分为六部完成根据模板导出excel操作：<br/>
 * 第一步、设置excel模板路径（setSrcPath）<br/>
 * 第二步、设置要生成excel文件路径（setDesPath）<br/>
 * 第三步、设置模板中哪个Sheet列（setSheetName）<br/>
 * 第四步、获取所读取excel模板的对象（getSheet）<br/>
 * 第五步、设置数据（分为6种类型数据：setCellStrValue、setCellDateValue、setCellDoubleValue、
 * setCellBoolValue、setCellCalendarValue、setCellRichTextStrValue）<br/>
 * 第六步、完成导出 （exportToNewFile）<br/>
 * 
 * 
 */
public class ExcelUtil {
	// excel模板路径
	private String srcXlsPath = "";
	private String sheetName = "";
//	XSSFWorkbook wb = null;
	Workbook wb = null;
//	XSSFSheet sheet = null;
	Sheet sheet = null;
	OutputStream out;

	final static int MAXEXCEL_LINE = 40000;

	public ExcelUtil(String srcXlsPath, String sheetName, OutputStream out) {
		this.srcXlsPath = srcXlsPath;
		this.sheetName = sheetName;
		this.out = out;
	}

	public ExcelUtil(String srcXlsPath, String sheetName) {
		this.srcXlsPath = srcXlsPath;
		this.sheetName = sheetName;
//		this.out = out;
	}

	/**
	 * 第四步、获取所读取excel模板的对象
	 */
	public Sheet getSheet(String excelType) {
		try {
			File fi = new File(srcXlsPath);
			if (!fi.exists()) {
				System.out.println("模板文件:" + srcXlsPath + "不存在!");
				return null;
			}
			// fs = new POIFSFileSystem(new FileInputStream(fi));
//			wb = new XSSFWorkbook(new FileInputStream(fi));
			if(excelType.equals("2003")) {
				wb = new HSSFWorkbook(new FileInputStream(fi));
			}
			if(excelType.equals("2007")) {
				wb = new XSSFWorkbook(new FileInputStream(fi));
			}
			
			if (sheetName == null || "".equals(sheetName)) {
				sheet = wb.getSheetAt(0);
			} else {
				sheet = wb.getSheet(sheetName);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sheet;
	}

	/**
	 * 第五步、设置字符串类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --字符串类型的数据
	 */
	public void setCellStrValue(int rowIndex, int cellnum, String value) {
		Row row = sheet.getRow(rowIndex);
		if(row==null){
			row = sheet.createRow(rowIndex);
		}
		if(row.getCell(cellnum)==null){
			row.createCell(cellnum);
		}
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置日期/时间类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --日期/时间类型的数据
	 */
	public void setCellDateValue(int rowIndex, int cellnum, Date value) {
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置浮点类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --浮点类型的数据
	 */
	public void setCellDoubleValue(int rowIndex, int cellnum, double value) {
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置Bool类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --Bool类型的数据
	 */
	public void setCellBoolValue(int rowIndex, int cellnum, boolean value) {
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置日历类型的数据
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --日历类型的数据
	 */
	public void setCellCalendarValue(int rowIndex, int cellnum, Calendar value) {
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第五步、设置富文本字符串类型的数据。可以为同一个单元格内的字符串的不同部分设置不同的字体、颜色、下划线
	 * 
	 * @param rowIndex
	 *            --行值
	 * @param cellnum
	 *            --列值
	 * @param value
	 *            --富文本字符串类型的数据
	 */
	public void setCellRichTextStrValue(int rowIndex, int cellnum, RichTextString value) {
		Cell cell = sheet.getRow(rowIndex).getCell(cellnum);
		cell.setCellValue(value);
	}

	/**
	 * 第六步、完成导出
	 */
	public void exportToNewFile() {
		try {
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * 	对数据进行拆分，相当于分页
	 * @param pageNum			页码
	 * @param sheetName			工作空间名
	 * @param mapList			数据集合
	 * @param finalResult		最终数据集合
	 * @return					最终的页码
	 */
	public static int group(int pageNum,String sheetName,List<Map<String, Object>> mapList,Map<String,List<Map<String, Object>>> finalResult ){

		int maxExcelLine = MAXEXCEL_LINE;

		//即将超过excel临界值
		if (mapList.size() > maxExcelLine){

			finalResult.put(sheetName + "_" + pageNum,mapList.subList(0, maxExcelLine));
			pageNum++;
			List<Map<String, Object>> other = mapList.subList(maxExcelLine, mapList.size());

			if (other.size() > maxExcelLine){

				pageNum = group(pageNum,sheetName,other,finalResult);

			}else{
				finalResult.put(sheetName + "_" + pageNum,other);
			}

		}
		return pageNum;
	}

	/**
	 * 将excel数据拼装成excel对象
	 * @param titles			程序中和列名对应的key
	 * @param titleName			显示在eccel中的标题名
	 * @param list				excel表格数据
	 * @param sheetName			sheet工作空间名
	 * @return					excel对象
	 */
	public static HSSFWorkbook getWorkBook(String[] titles, String[] titleName, List<Map<String, Object>> list,String sheetName){
		if (titles == null || titles.length == 0 || titleName == null || titleName.length == 0 || list == null || list.size() == 0){return null;}
		//判断是否超出excel临界值
		if (list.size() > MAXEXCEL_LINE){
			Map<String,List<Map<String, Object>>> finalResult = new LinkedHashMap<>();
			int group = group(1,sheetName, list, finalResult);

			String[][] newTitles = new String[group][titles.length];
			String[][] newTitleNames = new String[group][titleName.length];

			String[] newSheetNames = new String[group];
			for (int i = 0; i < group; i++) {
				newTitles[i] = titles.clone();
				newTitleNames[i] = titleName.clone();
				newSheetNames[i] = sheetName + "_" + (i + 1);
			}

			return getWorkbook(newTitles,newTitleNames,finalResult,newSheetNames);
		}

		//添加excel
		HSSFWorkbook wb = new HSSFWorkbook();
		//创建sheet表
		HSSFSheet sheet=wb.createSheet();
		//参数分别为sheet表索引值，sheet表的名字，处理中文问题用的编码
		wb.setSheetName(0, StringUtils.isBlank(sheetName) ? "report" : sheetName);
		//合并单元格，Region(起始行号,起始列号,终止行号,终止列号)即起始单元格，终止单元格
		//列号限制为short型
		//sheet.addMergedRegion(new Region(0,(short)0,0,(short)3));
		HSSFCellStyle style=wb.createCellStyle();
		//创建居中样式，更多单元格格式设置百度“POI导出Excel”
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//导入第一行
		HSSFRow row0 = sheet.createRow(0);
		for(int index =0 ; index< titleName.length;index++){
			//导入第一单元格
			HSSFCell cell = row0.createCell(index);
			cell.setCellValue(titleName[index]);
		}

		for(int index =0 ; index< list.size();index++){
			Map<String,Object> map = list.get(index);
			//导入第一单元格
			HSSFRow row = sheet.createRow(index+1);
			for(int j = 0;j< titles.length;j++){
				//导入第一单元格
				HSSFCell cell = row.createCell(j);

				String content = "";
				Object o = map.get(titles[j]);
				if (o != null) {
					content = o.toString();
				}
				cell.setCellValue(map.containsKey(titles[j])? content:"");
			}
		}
		return wb;
	}

	/**
	 *
	 * @param titles            程序中和列名对应的key
	 * @param titleName			显示在eccel中的标题名
	 * @param list				excel表格数据
	 * @param exportFileName	导出的excel文件名
	 * @param sheetName			sheet工作空间名
	 */
	public static void export(HttpServletResponse response, String[] titles, String[] titleName, List<Map<String, Object>> list, String exportFileName,String sheetName){
		HSSFWorkbook workBook = getWorkBook(titles, titleName, list, sheetName);
		if (workBook == null)return;
		exportFileName+=".xls";
		try{
			response.setContentType("application/octet-stream");
			response.setHeader("name", exportFileName);
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-disposition","attachment; filename="+ URLEncoder.encode(exportFileName,"utf-8"));
			// 输出流控制workbook
			workBook.write(response.getOutputStream());
			response.getOutputStream().flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				response.getOutputStream().close();
			}catch(IOException e1){
				e1.printStackTrace();
			}
		}
	}


	/**
	 * 将excel文件转为workbook
	 * @param file		需要转换的excel文件
	 * @return			excel对象
	 */
	public static Workbook parseFileForWB(MultipartFile file){
		Workbook wb = null;
		try {
			if (file == null) {
				return null;
			}else{
				String fileFileName = file.getOriginalFilename();
				if (fileFileName == null || !fileFileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
					return null;
				}else{

					if (PoiUtil.isExcel2003(fileFileName)) {
						wb = new HSSFWorkbook(file.getInputStream());
					}else if(PoiUtil.isExcel2007(fileFileName)){
						wb = new XSSFWorkbook(file.getInputStream());
					}
				}
			}
		} catch (IOException e) {
		}
		return wb;
	}

	/**
	 * 导出多个sheet的excel表格（直接下载）
	 * @param titles				程序中和列名对应的key
	 * @param titleName				显示在eccel中的标题名
	 * @param dataMap				excel表格数据
	 * @param exportFileName		导出到本地的文件路径名
	 * @param sheetNameList			sheet名字列表
	 */
	public static void exportForDown(HttpServletResponse response, String[][] titles, String[][] titleName, Map<String,List<Map<String, Object>>> dataMap, String exportFileName, String[] sheetNameList){
		//添加excel
		HSSFWorkbook wb = getWorkbook(titles,titleName,dataMap,sheetNameList);

		try{

			response.setContentType("application/octet-stream");
			response.setHeader("name", exportFileName+".xls");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-disposition","attachment; filename="+exportFileName+".xls");
			wb.write(response.getOutputStream()); // 输出流控制workbook
			response.getOutputStream().flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				response.getOutputStream().close();
			}catch(IOException e1){
				e1.printStackTrace();
			}
			try {
				wb.close();
			}catch(IOException e1){
				e1.printStackTrace();
			}
			wb = null;
		}
	}

	/**
	 *  获取excel工作表对象（多个sheet）
	 * @param titles				程序中和列名对应的key
	 * @param titleName				显示在eccel中的标题名
	 * @param dataMap				excel表格数据
	 * @param sheetNameList			sheet名字列表
	 * @return						excel工作表对象
	 */
	public static HSSFWorkbook getWorkbook(String[][] titles, String[][] titleName, Map<String,List<Map<String, Object>>> dataMap,String[] sheetNameList){

		HSSFWorkbook wb = new HSSFWorkbook();

		for (int i = 0; i < sheetNameList.length; i++) {
			HSSFSheet sheet=wb.createSheet();//创建sheet表
			HSSFCellStyle style=wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); //创建居中样式，更多单元格格式设置百度“POI导出Excel”

			HSSFRow row0 = sheet.createRow(0);//导入第一行
			for(int index =0 ; index< titleName[i].length;index++){
				HSSFCell cell = row0.createCell(index);//导入第一单元格
				cell.setCellValue(titleName[i][index]);
			}
			List<Map<String, Object>> mapList = dataMap.get(sheetNameList[i]);
			for(int index =0 ; index< mapList.size();index++){
				Map<String,Object> map = mapList.get(index);
				HSSFRow row = sheet.createRow(index+1);//导入第一单元格
				for(int j = 0;j< titles[i].length;j++){
					HSSFCell cell = row.createCell(j);//导入第一单元格
					cell.setCellValue(map.containsKey(titles[i][j])&& map.get(titles[i][j])!=null?map.get(titles[i][j]).toString():"");
				}
			}
		}

		return wb;
	}

	/**
	 * 导出excel到本地（多个sheet）
	 * @param titles				程序中和列名对应的key
	 * @param titleName				显示在eccel中的标题名
	 * @param dataMap				excel表格数据
	 * @param exportFileName		导出到本地的文件路径名
	 * @param sheetNameList			sheet名字列表
	 * @return						导出到本地的文件路径名（全名）
	 */
	public static String exportPlusToLocal(String[][] titles, String[][] titleName, Map<String,List<Map<String, Object>>> dataMap, String exportFileName,String[] sheetNameList,String filePath){
		//添加excel
		HSSFWorkbook wb = getWorkbook(titles,titleName,dataMap,sheetNameList);
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(new File(filePath,exportFileName+".xls"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		try {
			wb.write(out);
			out.close();
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return "" + exportFileName+".xls";
	}

	/**
	 * 	导出excel到本地（一个sheet）
	 * @param titles			程序中和列名对应的key
	 * @param titleName			显示在eccel中的标题名
	 * @param dataMap			excel表格数据
	 * @param exportFileName	导出的excel文件名
	 * @param sheetName			sheet工作空间名
	 * @param filePath			导出到本地的文件路径名
	 * @return					导出到本地的文件路径名（全名）
	 */
	public static String exportToLocal(String[] titles, String[] titleName, List<Map<String, Object>> dataMap, String exportFileName,String sheetName,String filePath){
		//添加excel
		HSSFWorkbook wb = getWorkBook(titles,titleName,dataMap,sheetName);

		FileOutputStream out = null;
		File file = null;
		try {
			file = new File(filePath, exportFileName + ".xls");
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		try {
			wb.write(out);
			out.close();
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file.getPath();
	}
}
