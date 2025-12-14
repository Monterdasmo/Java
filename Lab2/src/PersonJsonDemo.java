import com.google.gson.Gson;

/**
 * Демонстрація:
 *  a) створити Person
 *  b) конвертувати в JSON
 *  c) конвертувати назад
 *  d) перевірити equals
 */
public class PersonJsonDemo {

    public static void main(String[] args) {
        // a. Створюємо екземпляр Person
        Person original = new Person("Ivanenko", "Petro", 20);
        System.out.println("Original person: " + original);

        // b. Конвертуємо в JSON
        Gson gson = new Gson();
        String json = gson.toJson(original);
        System.out.println("JSON: " + json);

        // c. Конвертуємо назад в об'єкт
        Person restored = gson.fromJson(json, Person.class);
        System.out.println("Restored person: " + restored);

        // d. Перевіряємо equals
        boolean equalsResult = original.equals(restored);
        System.out.println("original.equals(restored) = " + equalsResult);
    }
}
