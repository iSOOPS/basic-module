package com.isoops.basicmodule.common.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.isoops.basicmodule.common.easypoi.source.SEasyPoiBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SEasypoi {

    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义 easypoi 结构数组 处理root结构
     * @param col 结构数组
     * @param bean 对象
     */
    public static void addColListEntity(List<ExcelExportEntity> col, SEasyPoiBean bean){
        ExcelExportEntity colEntity = new ExcelExportEntity(bean.getKeyName(), bean.getKey());
        col.add(colEntity);
    }

    /**
     * 自定义 easypoi 结构数组 group结构
     * @param col 结构数组
     * @param main 处理root结构
     * @param arg group结构
     */
    public static void addColListGroup(List<ExcelExportEntity> col, SEasyPoiBean main, SEasyPoiBean...arg){
        ExcelExportEntity group = new ExcelExportEntity(main.getKeyName(), main.getKey());
        List<ExcelExportEntity> groupList = new ArrayList<>();
        for (SEasyPoiBean bean : arg){
            groupList.add(new ExcelExportEntity(bean.getKeyName(), bean.getKey()));
        }
        group.setList(groupList);
        col.add(group);
    }

    /***** easy poi 输出 *****/
    private static byte[] workbookToByte(Workbook workbook){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**输出byte[]**/
    public static byte[] exportExcelByte(List<ExcelExportEntity> list,
                                         List<Map<String, Object>> mapDatas ,
                                        String title,
                                        String sheetName,
                                        boolean isCreateHeader){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, list, mapDatas);
        return workbookToByte(workbook);
    }

    public static byte[] exportExcelByte(List<?> list,
                                     String title,
                                     String sheetName,
                                     Class<?> pojoClass,
                                     boolean isCreateHeader){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        return workbookToByte(workbook);

    }
    public static byte[] exportExcelByte(List<?> list,
                                     String title,
                                     String sheetName,
                                     Class<?> pojoClass){
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(title, sheetName),pojoClass,list);
        return workbookToByte(workbook);
    }
    public static byte[] exportExcelByte(List<Map<String, Object>> list){
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        return workbookToByte(workbook);
    }

    /**输出Workbook**/
    public static Workbook exportExcel(List<ExcelExportEntity> list,
                                       List<Map<String, Object>> mapDatas ,
                                       String title, String sheetName,
                                       boolean isCreateHeader){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        return ExcelExportUtil.exportExcel(exportParams, list, mapDatas);
    }

    public static Workbook exportExcel(List<?> list,
                                       String title,
                                       String sheetName,
                                       Class<?> pojoClass,
                                       boolean isCreateHeader){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        return ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
    }

    public static Workbook exportExcel(List<Map<String, Object>> list){
        return ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
    }





    /***** easy poi 录入excel *****/

    public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Map> importExcelMap(MultipartFile file, Integer titleRows, Integer headerRows){
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<Map> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), Map.class, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
