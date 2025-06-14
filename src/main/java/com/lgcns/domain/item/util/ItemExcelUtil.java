package com.lgcns.domain.item.util;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.global.error.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

public class ItemExcelUtil {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 엑셀 파일 기본 검증
    public static void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ItemErrorCode.FILE_NOT_PROVIDED);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ItemErrorCode.FILE_TOO_LARGE);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new CustomException(ItemErrorCode.INVALID_FILE_TYPE);
        }
    }

    // 행 데이터를 Item 엔티티로 변환
    public static Item parseRowToItem(Popup popup, Row row, int rowIdx) {
        try {
            String name = getCellStringValue(row.getCell(0));
            String imageUrl = getCellStringValue(row.getCell(1));
            int price = getCellIntValue(row.getCell(2));
            int stock = getCellIntValue(row.getCell(3));
            int minStock = getCellIntValue(row.getCell(4));
            String location = getCellStringValue(row.getCell(5));

            // 데이터 검증
            validateItemData(name, imageUrl, price, stock, minStock, location, rowIdx);

            return Item.createItem(popup, name, imageUrl, price, stock, minStock, location);

        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Row %d 처리 중 오류: %s", rowIdx + 1, e.getMessage()));
        }
    }

    // 문자열 값 읽기
    public static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 소수점 제거
                    if (numericValue == Math.floor(numericValue)) {
                        yield String.valueOf((long) numericValue);
                    } else {
                        yield String.valueOf(numericValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    FormulaEvaluator evaluator =
                            cell.getSheet()
                                    .getWorkbook()
                                    .getCreationHelper()
                                    .createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    yield switch (cellValue.getCellType()) {
                        case STRING -> cellValue.getStringValue().trim();
                        case NUMERIC -> String.valueOf((long) cellValue.getNumberValue());
                        case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
                        default -> "";
                    };
                } catch (Exception e) {
                    yield "";
                }
            }
            case BLANK, _NONE -> "";
            default -> "";
        };
    }

    // 정수 값 읽기
    public static int getCellIntValue(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("필수 값이 없습니다");
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value < 0) {
                    throw new IllegalArgumentException("0 이상의 값이어야 합니다");
                }
                if (value > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("값이 너무 큽니다");
                }
                yield (int) value;
            }
            case STRING -> {
                String stringValue = cell.getStringCellValue().trim();
                if (stringValue.isEmpty()) {
                    throw new IllegalArgumentException("필수 값이 없습니다");
                }
                try {
                    int value = Integer.parseInt(stringValue);
                    if (value < 0) {
                        throw new IllegalArgumentException("0 이상의 값이어야 합니다");
                    }
                    yield value;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("숫자 형식이 아닙니다: " + stringValue);
                }
            }
            case FORMULA -> {
                try {
                    FormulaEvaluator evaluator =
                            cell.getSheet()
                                    .getWorkbook()
                                    .getCreationHelper()
                                    .createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue.getCellType() == CellType.NUMERIC) {
                        double value = cellValue.getNumberValue();
                        if (value < 0) {
                            throw new IllegalArgumentException("0 이상의 값이어야 합니다");
                        }
                        yield (int) value;
                    } else {
                        throw new IllegalArgumentException("수식 결과가 숫자가 아닙니다");
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("수식 계산에 실패했습니다");
                }
            }
            default -> throw new IllegalArgumentException("지원하지 않는 셀 형식입니다: " + cell.getCellType());
        };
    }

    // 빈 행 체크
    public static boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        // 6개 컬럼 확인 (상품명, 이미지URL, 가격, 재고, 최소재고, 위치)
        for (int i = 0; i < 6; i++) {
            Cell cell = row.getCell(i);
            String value = getCellStringValue(cell);
            if (!value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // 상품 데이터 검증
    public static void validateItemData(
            String name,
            String imageUrl,
            int price,
            int stock,
            int minStock,
            String location,
            int rowIdx) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("상품명은 필수입니다");
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            errors.add("이미지 URL은 필수입니다");
        }

        if (price < 0) {
            errors.add("가격은 0 이상이어야 합니다");
        }

        if (stock < 0) {
            errors.add("재고는 0 이상이어야 합니다");
        }

        if (minStock < 0) {
            errors.add("최소재고는 0 이상이어야 합니다");
        }

        if (minStock > stock) {
            errors.add(String.format("최소재고(%d)는 재고(%d)보다 클 수 없습니다", minStock, stock));
        }

        if (location == null || location.trim().isEmpty()) {
            errors.add("위치는 필수입니다");
        }

        if (!errors.isEmpty()) {
            String errorMessage =
                    String.format("Row %d: %s", rowIdx + 1, String.join(", ", errors));
            throw new CustomException(ItemErrorCode.EXCEL_DATA_INVALID) {
                @Override
                public String getMessage() {
                    return errorMessage;
                }
            };
        }
    }

    // 엑셀 시트 기본 검증
    public static void validateSheet(Sheet sheet) {
        if (sheet == null) {
            throw new CustomException(ItemErrorCode.EXCEL_FILE_INVALID);
        }

        if (sheet.getPhysicalNumberOfRows() <= 1) {
            throw new CustomException(ItemErrorCode.EXCEL_DATA_INVALID) {
                @Override
                public String getMessage() {
                    return "엑셀 파일에 데이터가 없습니다. 헤더 외에 최소 1개 이상의 데이터 행이 필요합니다.";
                }
            };
        }
    }
}
