package org.zrclass.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.util.StringUtils;
import org.zrclass.utils.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhourui
 * @module
 * @Date 2021/12/13/17:22
 * @description
 */
public class ExcelImportUtil<T> {

    /**
     * 将excel表单数据源的数据导入到list
     *
     * @param sheetName 工作表的名称
     * @param filePath  excel上传路径
     */
    public static <T> List getExcelToList(String sheetName, String filePath, Class<T> clazz) throws IOException {
        List<T> list = new ArrayList<T>();
        InputStream input = null;
        try {
            input = new FileInputStream(filePath);
            HSSFWorkbook book = new HSSFWorkbook(input);
            HSSFSheet sheet = null;
            // 如果指定sheet名,则取指定sheet中的内容.
            if (!StringUtils.isEmpty(sheetName)) {
                sheet = book.getSheet(sheetName);
            }
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            if (sheet == null) {
                sheet = book.getSheetAt(0);
            }
            // 得到数据的行数
            int rows = sheet.getLastRowNum();
            // 有数据时才处理
            if (rows > 0) {
                // 得到类的所有field
                Field[] allFields = clazz.getDeclaredFields();
                // 定义一个map用于存放列的序号和field
                Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();
                for (int i = 0,index =0; i < allFields.length; i++) {
                    Field field = allFields[i];
                    // 将有注解的field存放到map中
                    if (field.isAnnotationPresent(ImportKey.class)) {
                        ImportKey importKey = field.getAnnotation(ImportKey.class);
                        // 设置类的私有字段属性可访问
                        field.setAccessible(true);
                        fieldsMap.put(index, field);
                        index ++;
                    }
                }
                // 从第2行开始取数据,默认第一行是表头,但是rows是不算表头的行数
                for (int i = 1, len = rows+1; i < len; i++) {
                    // 得到一行中的所有单元格对象.
                    HSSFRow row = sheet.getRow(i);
                    Iterator<Cell> cells = row.cellIterator();
                    T entity = null;
                    int index = 0;
                    while (cells.hasNext()) {
                        // 从map中得到对应列的field
                        Field field = fieldsMap.get(index);
                        // 单元格中的内容.
                        String c = getCellValue(cells.next());
                        if (StringUtils.isEmpty(c)) {
                            continue;
                        }
                        if (c.indexOf("合计：") != -1) {
                            continue;
                        }
                        // 如果不存在实例则新建
                        entity = (entity == null ? clazz.newInstance() : entity);

                        if (field == null) {
                            continue;
                        }
                        // 取得类型,并根据对象类型设置值.
                        Class<?> fieldType = field.getType();
                        if (fieldType == null) {
                            continue;
                        }
                        if (String.class == fieldType) {
                            field.set(entity, String.valueOf(c));
                        } else if (BigDecimal.class == fieldType) {
                            c = c.indexOf("%") != -1 ? c.replace("%", "") : c;
                            field.set(entity, BigDecimal.valueOf(Double.valueOf(c)));
                        } else if (Date.class == fieldType) {
                            field.set(entity, DateUtil.formatDate(c));
                        } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                            field.set(entity, Integer.parseInt(c));
                        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                            field.set(entity, Long.valueOf(c));
                        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                            field.set(entity, Float.valueOf(c));
                        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                            field.set(entity, Short.valueOf(c));
                        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                            field.set(entity, Double.valueOf(c));
                        } else if (Character.TYPE == fieldType) {
                            if ((c != null) && (c.length() > 0)) {
                                field.set(entity, Character.valueOf(c.charAt(0)));
                            }
                        }
                        index++;

                    }
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("将excel表单数据源的数据导入到list异常!", e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return list;
    }

    private static String getCellValue(Cell cell) {
        String cellValue = "";
        CellType cellType = cell.getCellType();
        if (CellType.NUMERIC == cellType) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        } else if (CellType.STRING == cellType) {
            cellValue = cell.getStringCellValue();
        } else if (CellType.BOOLEAN == cellType) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }
        return cellValue;
    }


//    /**
//     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
//     *
//     * @param col
//     */
//    public static int getExcelCol(String col) {
//        col = col.toUpperCase();
//        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
//        int count = -1;
//        char[] cs = col.toCharArray();
//        for (int i = 0; i < cs.length; i++) {
//            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
//        }
//        return count;
//    }

//    /**
//     * 设置单元格上提示
//     *
//     * @param sheet         要设置的sheet.
//     * @param promptTitle   标题
//     * @param promptContent 内容
//     * @param firstRow      开始行
//     * @param endRow        结束行
//     * @param firstCol      开始列
//     * @param endCol        结束列
//     * @return 设置好的sheet.
//     */
//    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle, String promptContent, int firstRow, int endRow,
//                                          int firstCol, int endCol) {
//        // 构造constraint对象
//        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");
//        // 四个参数分别是：起始行、终止行、起始列、终止列
//        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
//        // 数据有效性对象
//        HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
//        data_validation_view.createPromptBox(promptTitle, promptContent);
//        sheet.addValidationData(data_validation_view);
//        return sheet;
//    }
//
//    /**
//     * 设置某些列的值只能输入预制的数据,显示下拉框.
//     *
//     * @param sheet    要设置的sheet.
//     * @param textlist 下拉框显示的内容
//     * @param firstRow 开始行
//     * @param endRow   结束行
//     * @param firstCol 开始列
//     * @param endCol   结束列
//     * @return 设置好的sheet.
//     */
//    public static HSSFSheet setHSSFValidation(HSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
//        // 加载下拉列表内容
//        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
//        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
//        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
//        // 数据有效性对象
//        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
//        sheet.addValidationData(data_validation_list);
//        return sheet;
//    }


}
