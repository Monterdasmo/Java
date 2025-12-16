package ua.kpi.lab11.db;

import ua.kpi.lab11.config.DatabaseConfig;

import java.sql.*;

/**
 * Клас для виконання всіх операцій з базою даних.
 * Містить методи для виконання SELECT, INSERT, DELETE запитів.
 */
public class DatabaseOperations {

    /**
     * Встановити з'єднання з базою даних.
     *
     * @return об'єкт Connection
     * @throws SQLException якщо не вдалося підключитися
     */
    private Connection getConnection() throws SQLException {
        try {
            // Завантажуємо JDBC драйвер
            Class.forName(DatabaseConfig.getDriver());
            // Встановлюємо з'єднання
            return DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
            );
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Помилка: JDBC драйвер не знайдено!");
            System.err.println("  Переконайтеся, що mysql-connector-j.jar додано до classpath");
            throw new SQLException("JDBC драйвер не знайдено", e);
        }
    }

    /**
     * Отримати список всіх співробітників з інформацією про їх відділи.
     * Використовує LEFT JOIN для отримання повної інформації.
     *
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void getAllEmployees() throws SQLException {
        String sql = "SELECT e.employee_id, e.first_name, e.last_name, " +
                "e.position, d.name as department_name " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.department_id = d.department_id " +
                "ORDER BY e.last_name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String position = rs.getString("position");
                String department = rs.getString("department_name");

                System.out.printf("  ID: %d | %s %s | Посада: %s | Відділ: %s%n",
                        id, lastName, firstName,
                        position != null ? position : "не вказано",
                        department != null ? department : "не призначено");
            }
        }
    }

    /**
     * Отримати список всіх завдань з інформацією про відповідальних співробітників.
     * Використовує LEFT JOIN для отримання повної інформації.
     *
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void getAllTasks() throws SQLException {
        String sql = "SELECT t.task_id, t.description, " +
                "e.first_name, e.last_name " +
                "FROM tasks t " +
                "LEFT JOIN employees e ON t.employee_id = e.employee_id " +
                "ORDER BY t.task_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int taskId = rs.getInt("task_id");
                String description = rs.getString("description");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                String employeeName = (firstName != null && lastName != null)
                        ? lastName + " " + firstName
                        : "не призначено";

                System.out.printf("  Завдання #%d: %s%n", taskId, description);
                System.out.printf("    Відповідальний: %s%n", employeeName);
            }
        }
    }

    /**
     * Отримати список співробітників зазначеного відділу.
     *
     * @param departmentId ID відділу
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void getEmployeesByDepartment(int departmentId) throws SQLException {
        String sql = "SELECT e.employee_id, e.first_name, e.last_name, e.position, d.name as department_name " +
                "FROM employees e " +
                "INNER JOIN departments d ON e.department_id = d.department_id " +
                "WHERE e.department_id = ? " +
                "ORDER BY e.last_name";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int id = rs.getInt("employee_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String position = rs.getString("position");
                    String department = rs.getString("department_name");

                    if (!found) {
                        System.out.printf("  Відділ: %s%n", department);
                    }

                    System.out.printf("  ID: %d | %s %s | Посада: %s%n",
                            id, lastName, firstName,
                            position != null ? position : "не вказано");
                }

                if (!found) {
                    System.out.println("  (Співробітників не знайдено в цьому відділі)");
                }
            }
        }
    }

    /**
     * Додати нове завдання для зазначеного співробітника.
     *
     * @param employeeId ID співробітника
     * @param description опис завдання
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void addTaskForEmployee(int employeeId, String description) throws SQLException {
        // Спочатку перевіримо чи існує такий співробітник
        String checkSql = "SELECT first_name, last_name FROM employees WHERE employee_id = ?";
        String insertSql = "INSERT INTO tasks (description, employee_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            // Перевірка існування співробітника
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, employeeId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.printf("  ✗ Співробітника з ID=%d не знайдено!%n", employeeId);
                        return;
                    }
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    System.out.printf("  Співробітник: %s %s%n", lastName, firstName);
                }
            }

            // Додавання завдання
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, description);
                insertStmt.setInt(2, employeeId);

                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Отримуємо ID новоствореного завдання
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int taskId = generatedKeys.getInt(1);
                            System.out.printf("  ✓ Завдання успішно додано! (ID=%d)%n", taskId);
                            System.out.printf("  Опис: \"%s\"%n", description);
                        }
                    }
                }
            }
        }
    }

    /**
     * Отримати список завдань для зазначеного співробітника.
     *
     * @param employeeId ID співробітника
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void getTasksByEmployee(int employeeId) throws SQLException {
        // SQL для отримання інформації про співробітника
        String employeeSql = "SELECT first_name, last_name, position FROM employees WHERE employee_id = ?";
        // SQL для отримання завдань
        String tasksSql = "SELECT task_id, description FROM tasks WHERE employee_id = ? ORDER BY task_id";

        try (Connection conn = getConnection()) {
            // Отримуємо інформацію про співробітника
            try (PreparedStatement empStmt = conn.prepareStatement(employeeSql)) {
                empStmt.setInt(1, employeeId);
                try (ResultSet rs = empStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.printf("  ✗ Співробітника з ID=%d не знайдено!%n", employeeId);
                        return;
                    }
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String position = rs.getString("position");

                    System.out.printf("  Співробітник: %s %s (%s)%n",
                            lastName, firstName,
                            position != null ? position : "не вказано");
                }
            }

            // Отримуємо завдання
            try (PreparedStatement tasksStmt = conn.prepareStatement(tasksSql)) {
                tasksStmt.setInt(1, employeeId);
                try (ResultSet rs = tasksStmt.executeQuery()) {
                    boolean hasTasks = false;
                    while (rs.next()) {
                        hasTasks = true;
                        int taskId = rs.getInt("task_id");
                        String description = rs.getString("description");
                        System.out.printf("    • Завдання #%d: %s%n", taskId, description);
                    }

                    if (!hasTasks) {
                        System.out.println("    (Завдань не призначено)");
                    }
                }
            }
        }
    }

    /**
     * Видалити співробітника з бази даних.
     * Завдання співробітника також будуть видалені через CASCADE.
     *
     * @param employeeId ID співробітника для видалення
     * @throws SQLException якщо виникла помилка при виконанні запиту
     */
    public void deleteEmployee(int employeeId) throws SQLException {
        // Спочатку отримаємо інформацію про співробітника та його завдання
        String infoSql = "SELECT e.first_name, e.last_name, e.position, " +
                "(SELECT COUNT(*) FROM tasks WHERE employee_id = ?) as task_count " +
                "FROM employees e WHERE e.employee_id = ?";
        String deleteSql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = getConnection()) {
            // Отримуємо інформацію перед видаленням
            try (PreparedStatement infoStmt = conn.prepareStatement(infoSql)) {
                infoStmt.setInt(1, employeeId);
                infoStmt.setInt(2, employeeId);

                try (ResultSet rs = infoStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.printf("  ✗ Співробітника з ID=%d не знайдено!%n", employeeId);
                        return;
                    }

                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String position = rs.getString("position");
                    int taskCount = rs.getInt("task_count");

                    System.out.printf("  Видалення співробітника: %s %s (%s)%n",
                            lastName, firstName,
                            position != null ? position : "не вказано");

                    if (taskCount > 0) {
                        System.out.printf("  ⚠ Увага: Також буде видалено %d завдань(ня) через CASCADE%n", taskCount);
                    }
                }
            }

            // Виконуємо видалення
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, employeeId);
                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.printf("  ✓ Співробітника успішно видалено! (Рядків оновлено: %d)%n", rowsAffected);
                }
            }
        }
    }

    /**
     * Перевірити підключення до бази даних.
     *
     * @return true якщо підключення успішне, false інакше
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✓ З'єднання з базою даних успішно встановлено!");
            System.out.println("  Database: " + conn.getCatalog());
            System.out.println("  Driver: " + conn.getMetaData().getDriverName());
            System.out.println("  Version: " + conn.getMetaData().getDriverVersion());
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Помилка підключення до бази даних:");
            System.err.println("  " + e.getMessage());
            return false;
        }
    }
}
