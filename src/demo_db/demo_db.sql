CREATE DATABASE store_product;

CREATE TABLE products
(
    product_id      SERIAL PRIMARY KEY,
    product_name    VARCHAR(100) NOT NULL UNIQUE,
    product_price   FLOAT        NOT NULL CHECK ( product_price > 0 ),
    product_title   VARCHAR(200) NOT NULL,
    product_created DATE         NOT NULL DEFAULT CURRENT_DATE,
    product_catalog VARCHAR(100) NOT NULL,
    product_status  BIT(1)                DEFAULT B'1'
);

-- LẤY TẤT CẢ SẢN PHẨM
CREATE OR REPLACE FUNCTION fn_get_all_product()
    RETURNS SETOF products
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
        SELECT * FROM products;
END;
$$;

-- KIỂM TRA SỰ TỒN TẠI CỦA DANH MỤC
CREATE OR REPLACE FUNCTION fn_check_exist_catalog(
    IN p_catalog VARCHAR(100)
) RETURNS BOOLEAN
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN EXISTS (SELECT 1
                   FROM products
                   WHERE product_catalog = p_catalog);
END;
$$;

-- THÊM MỚI MỘT SẢN PHẨM
CREATE OR REPLACE PROCEDURE proc_add_product(
    p_name VARCHAR,
    p_price FLOAT,
    p_title VARCHAR,
    p_created_date DATE,
    p_catalog VARCHAR
)
    LANGUAGE plpgsql AS
$$
BEGIN

    IF p_price <= 0 THEN
        RAISE EXCEPTION 'GIÁ SẢN PHẨM PHẢI LỚN HƠN 0';
    END IF;

    INSERT INTO products (product_name, product_price, product_title, product_catalog, product_created)
    VALUES (p_name, p_price, p_title, p_catalog, p_created_date);

END;
$$;

-- CẬP NHẬT MỘT SẢN PHẨM THEO MÃ SẢN PHẨM
CREATE OR REPLACE PROCEDURE proc_update_product_by_id(
    p_id INT,
    p_name VARCHAR,
    p_price FLOAT,
    p_title VARCHAR,
    p_created_date DATE,
    p_catalog VARCHAR
)
    LANGUAGE plpgsql AS
$$
BEGIN

    IF p_price <= 0 THEN
        RAISE EXCEPTION 'GIÁ SẢN PHẨM PHẢI LỚN HƠN 0';
    END IF;

    IF EXISTS (SELECT 1
               FROM products
               WHERE product_name = p_name
                 AND product_id <> p_id) THEN
        RAISE EXCEPTION 'TÊN SẢN PHẨM ĐÃ TỒN TẠI';
    END IF;

    UPDATE products
    SET product_name    = p_name,
        product_price   = p_price,
        product_created = p_created_date,
        product_title   = p_title,
        product_catalog = p_catalog
    WHERE product_id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'SẢN PHẨM KHÔNG TỒN TẠI';
    END IF;
END;
$$;

-- XÓA MỘT SẢN PHẨM THEO ID
CREATE OR REPLACE PROCEDURE proc_delete_product_by_id(
    p_id INT
)
    LANGUAGE plpgsql AS
$$
BEGIN
    DELETE FROM products WHERE product_id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'SẢN PHẨM KHÔNG TỒN TẠI';
    END IF;

END;
$$;

-- Lấy thông tin sản phẩm theo mã sản phẩm
CREATE OR REPLACE FUNCTION fn_get_product_by_id(
    p_id INT
)
    RETURNS SETOF products
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
        SELECT * FROM products WHERE product_id = p_id;
END;
$$;

-- Tìm kiếm sản phẩm theo tên sản phẩm (tương đối)
CREATE OR REPLACE FUNCTION fn_get_product_by_name_keyword(
    p_keyword VARCHAR
)
    RETURNS SETOF products
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
        SELECT * FROM products WHERE product_name ILIKE '%' || p_keyword || '%';
END;
$$;

-- Thống kê số lượng sản phẩm theo danh mục
CREATE OR REPLACE FUNCTION fn_count_product_by_catalog()
    RETURNS TABLE
            (
                catalog       VARCHAR(100),
                product_count BIGINT
            )
    LANGUAGE sql
AS
$$
SELECT product_catalog,
       COUNT(*) AS product_count
FROM products
GROUP BY product_catalog
ORDER BY product_catalog;
$$;

-- Tìm xem tên có tồn tại chưa
CREATE OR REPLACE FUNCTION fn_check_exist_product_name(
    p_name VARCHAR(100),
    p_exclude_id INT DEFAULT NULL
)
    RETURNS BOOLEAN
    LANGUAGE sql
AS
$$
SELECT EXISTS (SELECT 1
               FROM products
               WHERE product_name = p_name
                 AND (p_exclude_id IS NULL OR product_id <> p_exclude_id));
$$;

INSERT INTO products
(product_name, product_price, product_title, product_created, product_catalog, product_status)
VALUES ('Laptop Dell Inspiron 15',
        18500000,
        'Laptop văn phòng hiệu năng ổn định',
        '2026-01-05',
        'Laptop',
        B'1'),

       ('Laptop Lenovo IdeaPad Slim 3',
        15990000,
        'Laptop học tập và làm việc',
        '2026-01-12',
        'Laptop',
        B'1'),

       ('MacBook Air M3',
        25990000,
        'Laptop Apple chip M3',
        '2026-01-20',
        'Laptop',
        B'1'),

       ('Laptop ASUS Vivobook 15',
        17200000,
        'Laptop mỏng nhẹ cho sinh viên',
        '2026-02-03',
        'Laptop',
        B'1'),

       ('Samsung Galaxy S25',
        21990000,
        'Điện thoại flagship Samsung',
        '2026-02-10',
        'Smartphone',
        B'1'),

       ('iPhone 16',
        22990000,
        'Điện thoại Apple thế hệ mới',
        '2026-02-15',
        'Smartphone',
        B'1'),

       ('Xiaomi 15',
        16990000,
        'Smartphone hiệu năng cao',
        '2026-02-22',
        'Smartphone',
        B'1'),

       ('OPPO Reno 13',
        12990000,
        'Điện thoại thiết kế hiện đại',
        '2026-03-01',
        'Smartphone',
        B'1'),

       ('Samsung Galaxy Tab S10',
        18990000,
        'Máy tính bảng cao cấp',
        '2026-03-05',
        'Tablet',
        B'1'),

       ('iPad Air M3',
        16990000,
        'Máy tính bảng Apple chip M3',
        '2026-03-12',
        'Tablet',
        B'1'),

       ('Xiaomi Pad 7',
        10990000,
        'Tablet giải trí và học tập',
        '2026-03-20',
        'Tablet',
        B'1'),

       ('Logitech MX Master 3S',
        2290000,
        'Chuột không dây dành cho công việc',
        '2026-04-02',
        'Accessory',
        B'1'),

       ('Logitech K380',
        790000,
        'Bàn phím Bluetooth nhỏ gọn',
        '2026-04-08',
        'Accessory',
        B'1'),

       ('Keychron K2',
        2190000,
        'Bàn phím cơ không dây',
        '2026-04-15',
        'Accessory',
        B'1'),

       ('Sony WH-1000XM5',
        6990000,
        'Tai nghe chống ồn cao cấp',
        '2026-04-22',
        'Audio',
        B'1'),

       ('Apple AirPods Pro 2',
        5990000,
        'Tai nghe không dây chống ồn',
        '2026-05-01',
        'Audio',
        B'1'),

       ('JBL Flip 7',
        2990000,
        'Loa Bluetooth di động',
        '2026-05-08',
        'Audio',
        B'1'),

       ('Samsung T7 Shield 1TB',
        2490000,
        'Ổ SSD di động tốc độ cao',
        '2026-05-15',
        'Storage',
        B'1'),

       ('Kingston NV3 1TB',
        1890000,
        'Ổ SSD NVMe hiệu năng cao',
        '2026-05-22',
        'Storage',
        B'1'),

       ('WD My Passport 2TB',
        2390000,
        'Ổ cứng di động dung lượng lớn',
        '2026-06-01',
        'Storage',
        B'1');

