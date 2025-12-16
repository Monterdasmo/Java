-- =====================================================
-- Лабораторна робота 11: Робота з базами даних
-- Файл: schema.sql
-- Призначення: Створення структури бази даних company_db
-- =====================================================

-- Видалення існуючої бази даних (якщо є)
DROP DATABASE IF EXISTS company_db;

-- Створення бази даних з підтримкою UTF-8
CREATE DATABASE company_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Використовуємо створену базу даних
USE company_db;

-- =====================================================
-- Таблиця: departments (Відділи)
-- Містить інформацію про відділи компанії
-- =====================================================
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    name CHAR(30) NOT NULL,
    phone CHAR(15)
) ENGINE=InnoDB;

-- =====================================================
-- Таблиця: employees (Співробітники)
-- Містить інформацію про співробітників
-- Зв'язок: many-to-one з departments
-- =====================================================
CREATE TABLE employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(20) NOT NULL,
    first_name VARCHAR(10) NOT NULL,
    position VARCHAR(20),
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- Таблиця: tasks (Завдання)
-- Містить завдання призначені співробітникам
-- Зв'язок: many-to-one з employees
-- При видаленні співробітника, його завдання також видаляються (CASCADE)
-- =====================================================
CREATE TABLE tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(50) NOT NULL,
    employee_id INT,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================
-- Створення індексів для покращення продуктивності
-- =====================================================
CREATE INDEX idx_employee_department ON employees(department_id);
CREATE INDEX idx_task_employee ON tasks(employee_id);
