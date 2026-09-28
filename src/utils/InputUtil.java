package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {
    private static Scanner scanner = new Scanner(System.in);

    public static int getInt() {
        do {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số nguyên.");
            }
        } while (true);
    }

    public static String getNonBlankString() {
        do {
            String result = scanner.nextLine();
            if (result.isBlank()) {
                System.out.println("Vui lòng nhập chuỗi có giá trị.");
            } else {
                return result;
            }
        } while (true);
    }

    public static double getDouble() {
        do {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số.");
            }
        } while (true);
    }

    public static LocalDate getDate(String pattern) {
        do {
            try {
                return LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException e) {
                System.out.println("Vui lòng nhập đúng định dạng ngày tháng " + pattern);
            }
        } while (true);
    }

    public static int getPositiveInt() {
        while (true) {
            try {
                int value = getInt();

                if (value <= 0) {
                    System.out.println("Mã phải lớn hơn 0.");
                    continue;
                }

                return value;
            } catch (IllegalArgumentException e) {
                System.out.println("Vui lòng nhập một số hợp lệ.");
            }
        }
    }

    public static double getPositiveDouble() {
        while (true) {
            try {
                double value = getDouble();

                if (value <= 0) {
                    System.out.println("Giá phải lớn hơn 0.");
                    continue;
                }

                return value;
            } catch (IllegalArgumentException e) {
                System.out.println("Vui lòng nhập một số hợp lệ.");
            }
        }
    }
}
