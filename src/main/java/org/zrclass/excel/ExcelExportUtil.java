package org.zrclass.excel;

import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.zrclass.utils.DateUtil;
import org.zrclass.utils.ReflectUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author zhourui
 * @module
 * @Date 2021/11/12/11:40
 * @description 导出工具类，结合自定义注解ExportKey+反射的方式实现注解驱动导出列
 * 通过注解拿到需要导出的字段及列名称，再结合反射获取导出列数据值，
 * 所解决的问题：每次导出由于各自业务功能及导出字段名称不一致，业务代码多处重复创建导出的问题
 */
public class ExcelExportUtil {

    /**
     * 生成excel
     *
     * @param title    标题,sheet名称
     * @param dataList 需要导出的数据
     * @param response response
     * @param fileName 导出文件名
     * @param clazz 范型对应的类，dataList为空时无法拿到列名称，所以需要这个参数
     */
    public static <T> void exportExcel(String title, List<T> dataList,
                                       HttpServletResponse response, String fileName,Class clazz) {
        try {
            // 创建工作簿对象
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 创建工作表
            HSSFSheet sheet = workbook.createSheet(title);

            // 产生表格标题行
            HSSFRow row = sheet.createRow(0);
            HSSFCell cellTitle = row.createCell(0);

            // 获取列头样式对象
            HSSFCellStyle columnTopStyle = getColumnTopStyle(workbook);
            // 单元格样式对象
            HSSFCellStyle style = getStyle(workbook);

            // 定义所需列数
            List<String> rowNameList = Lists.newArrayList();
            List<String> fieldNameList = Lists.newArrayList();
            getColumnNameAndKey(clazz, rowNameList, fieldNameList);
            String[] rowName = rowNameList.toArray(new String[0]);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (rowName.length - 1)));
            cellTitle.setCellStyle(columnTopStyle);
            cellTitle.setCellValue(title);

            // 在索引2的位置创建行(最顶端的行开始的第二行)
            HSSFRow rowRowName = sheet.createRow(2);
            // 将列头设置到sheet的单元格中
            for (int n = 0; n < rowNameList.size(); n++) {
                // 创建列头对应个数的单元格
                HSSFCell cellRowName = rowRowName.createCell(n);
                //设置列头单元格的数据类型
                cellRowName.setCellType(CellType.STRING);
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                // 设置列头单元格的值
                cellRowName.setCellValue(text);
                // 设置列头单元格样式
                cellRowName.setCellStyle(columnTopStyle);
            }
            //将查询出的数据设置到sheet对应的单元格中
            if (!CollectionUtils.isEmpty(dataList)) {
                // 将list转array
                for (int i = 0; i < dataList.size(); i++) {
                    //获取属性值数组
                    T data = dataList.get(i);
                    List<Object> values = ReflectUtil.getValueListByFields(fieldNameList, data);
                    Object[] obj = values.toArray();
                    //创建所需的行数
                    HSSFRow rows = sheet.createRow(i + 3);
                    for (int j = 0; j < obj.length; j++) {
                        HSSFCell cell;
                        Object o = obj[j];
                        if (o instanceof Integer || o instanceof Double || o instanceof BigDecimal || o instanceof Long) {
                            cell = rows.createCell(j, CellType.NUMERIC);
                            double doubleValue = ((Number) o).doubleValue();
                            cell.setCellValue(doubleValue);
                        } else {
                            cell = rows.createCell(j, CellType.STRING);
                            if (o instanceof Date) {
                                String date = DateUtil.formatDate((Date) o);
                                cell.setCellValue(date);
                            } else if (!"".equals(o) && o != null) {
                                //设置单元格的值
                                cell.setCellValue(obj[j].toString());
                            }else{
                                cell.setCellValue("-");
                            }
                        }
                        // 设置单元格样式
                        cell.setCellStyle(style);
                    }
                }

                // 让列宽随着导出的列长自动适应
                for (int colNum = 0; colNum < rowNameList.size(); colNum++) {
                    int columnWidth = sheet.getColumnWidth(colNum) / 256;
                    for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                        HSSFRow currentRow;
                        //当前行未被使用过
                        if (sheet.getRow(rowNum) == null) {
                            currentRow = sheet.createRow(rowNum);
                        } else {
                            currentRow = sheet.getRow(rowNum);
                        }
                        if (currentRow.getCell(colNum) != null) {
                            HSSFCell currentCell = currentRow.getCell(colNum);
                            if (currentCell!=null && !StringUtil.isEmpty(currentCell.getStringCellValue()) && currentCell.getCellType() == CellType.STRING) {
                                int length = currentCell.getStringCellValue().getBytes().length;
                                if (columnWidth < length) {
                                    columnWidth = length;
                                }
                            }
                        }
                    }
                    if (colNum == 0) {
                        sheet.setColumnWidth(colNum, (columnWidth - 2) * 256);
                    } else {
                        sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
                    }
                }

            }

            try {
                // 设置导出的表名称
                fileName = URLEncoder.encode(fileName,"UTF-8");
                String headStr = "attachment; filename=\"" + fileName + DateUtil.formatDateString(new Date()) + ".xls" + "\"";
                response.setCharacterEncoding("UTF-8");
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", headStr);
                OutputStream out = response.getOutputStream();
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getColumnNameAndKey(Class clazz, List<String> rowNameList, List<String> fieldNameList) {
        List<Field> fields = ReflectUtil.listFields(clazz);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            ExportKey key = field.getAnnotation(ExportKey.class);
            if (key != null && key.isExport()) {
                if (StringUtil.isEmpty(key.remark())) {
                    //如果为空就取字段名称
                    String name = field.getName();
                    rowNameList.add(name);
                }
                rowNameList.add(key.remark());
                fieldNameList.add(field.getName());
                continue;
            }
        }
    }

    /**
     * 列头单元格样式
     */
    public static HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {

        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setBold(true);
        //设置字体名字
//        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
//        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
//        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;

    }

    /**
     * 列数据信息单元格样式
     */
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
//        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
//        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;

    }
}

