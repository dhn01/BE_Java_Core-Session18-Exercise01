package presentation;

import entity.Product;
import exception.ProductException;
import persistence.ProductDAO;
import utils.InputUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
    public static ProductDAO productDAO = new ProductDAO();

    public static void main(String[] args) {
        int choice;
        do {
            do {
                try {
                    System.out.print("""
                            ********************PRODUCT MANAGEMENT****************
                            1. Danh sách sản phẩm
                            2. Thêm mới sản phẩm
                            3. Cập nhật sản phẩm
                            4. Xóa sản phẩm
                            5. Tìm kiếm sản phẩm theo tên sản phẩm
                            6. Sắp xếp sản phẩm theo giá tăng dần
                            7. Thống kê số lượng sản phẩm theo danh mục
                            8. Thoát
                            Nhập vào lựa chọn:
                            """);
                    choice = InputUtil.getInt();
                    if (choice < 1 || choice > 8) {
                        throw new IllegalArgumentException("Vui lòng nhập giá trị 1-8");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            } while (true);

            switch (choice) {
                case 1: {
                    System.out.print("""
                            ===== Danh sách sản phẩm =====
                            """);
                    List<Product> products = productDAO.getAll();
                    if (products.isEmpty()) {
                        System.out.println("Không có sản phẩm nào.");
                    } else {
                        products.forEach(System.out::println);
                    }
                }
                break;
                case 2: {
                    try {
                        Product product = inputProduct(null);
                        productDAO.addProduct(product);
                        System.out.println("Thêm sản phẩm thành công.");
                    } catch (ProductException e) {
                        System.err.println(e.getMessage());
                    }
                }
                break;
                case 3: {
                    System.out.println("Nhập vào mã sản phẩm: ");
                    int id = InputUtil.getPositiveInt();
                    Product product = inputProduct(id);
                    try {
                        productDAO.updateById(id, product);
                        System.out.println("Thêm sản phẩm thành công.");
                    } catch (ProductException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 4: {
                    System.out.println("Nhập vào id sản phẩm muốn xóa: ");
                    int id = InputUtil.getInt();
                    try {
                        productDAO.deleteById(id);
                        System.out.println("Xóa thành công.");
                    } catch (ProductException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 5: {
                    System.out.println("Nhập vào từ khóa: ");
                    String name = InputUtil.getNonBlankString();
                    try {
                        List<Product> products = productDAO.searchByName(name);
                        System.out.print("""
                                ===== Danh sách sản phẩm =====
                                """);
                        if (products.isEmpty()) {
                            System.out.println("Không tìm thấy sản phẩm nào khớp.");
                        } else {
                            products.forEach(System.out::println);
                        }
                    } catch (ProductException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 6: {
                    try {
                        List<Product> products = productDAO.getAll();
                        System.out.print("""
                                ===== Danh sách sản phẩm =====
                                """);
                        if (products.isEmpty()) {
                            System.out.println("Không có dữ liệu.");
                        } else {
                            products.stream()
                                    .sorted(Comparator.comparingDouble(Product::getPrice))
                                    .forEach(System.out::println);
                        }
                    } catch (ProductException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 7: {
                    try {
                        Map<String, Integer> catalog = productDAO.getProductCatalog();
                        System.out.println("===== Catalog =====");
                        if (catalog.isEmpty()) {
                            System.out.println("Không có sản phẩm.");
                        } else {
                            catalog.forEach((k, v) -> System.out.println(k + ": " + v));
                        }
                    } catch (ProductException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
                case 8:
                    System.out.println("Cảm ơn bạn đã sử dụng.");
                    break;
            }
        } while (choice != 8);

    }

    private static Product inputProduct(Integer id) {
        System.out.println("===== Nhập thông tin sản phẩm =====");

        String name;
        while (true) {
            System.out.print("Tên sản phẩm: ");
            name = InputUtil.getNonBlankString();

            if (productDAO.isNameExist(name, id)) {
                System.out.println("Tên sản phẩm đã tồn tại. Vui lòng nhập tên khác.");
            } else {
                break;
            }
        }

        System.out.print("Nhập vào giá sản phẩm: ");
        double price = InputUtil.getPositiveDouble();

        System.out.print("Nhập vào tiêu đề sản phẩm: ");
        String title = InputUtil.getNonBlankString();

        System.out.print("Nhập vào ngày tạo: ");
        LocalDate createdDate = InputUtil.getDate("dd/MM/yyyy");

        System.out.print("Nhập vào danh mục sản phẩm: ");
        String catalog = InputUtil.getNonBlankString();

        return new Product(
                0,
                name,
                price,
                title,
                createdDate,
                catalog,
                true
        );
    }
}
