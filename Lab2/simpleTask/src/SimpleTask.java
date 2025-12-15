import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class SimpleTask {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void main(String[] args) {
        System.out.println("=== Завдання 2.1: Person (equals) + Gson JSON ===\n");

        Person original = new Person("Шевченко", "Тарас", 25);

        String json = GSON.toJson(original);
        Person restored = GSON.fromJson(json, Person.class);

        printSection("1) Оригінальний об'єкт", original);
        printSection("2) JSON", json);
        printSection("3) Десеріалізований об'єкт", restored);

        System.out.println("\n4) Перевірка equals/hashCode");
        System.out.println("   equals: " + original.equals(restored));
        System.out.println("   hashCode: " + original.hashCode() + " == " + restored.hashCode());

        runAdditionalEqualsChecks();
    }

    private static void runAdditionalEqualsChecks() {
        System.out.println("\n5) Додаткові перевірки equals");

        Person p1 = new Person("Іванов", "Іван", 30);
        Person p2 = new Person("Іванов", "Іван", 30);
        Person p3 = new Person("Петров", "Петро", 25);

        System.out.println("   p1.equals(p2): " + p1.equals(p2));
        System.out.println("   p1.equals(p3): " + p1.equals(p3));
        System.out.println("   p1.equals(null): " + p1.equals(null));
        System.out.println("   p1.equals(p1): " + p1.equals(p1));
    }

    private static void printSection(String title, Object value) {
        System.out.println(title + ":");
        System.out.println("   " + value);
    }

    static class Person {
        private String lastName;
        private String firstName;
        private int age;

        public Person() {}

        public Person(String lastName, String firstName, int age) {
            this.lastName = lastName;
            this.firstName = firstName;
            this.age = age;
        }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person person = (Person) o;
            return age == person.age
                    && Objects.equals(lastName, person.lastName)
                    && Objects.equals(firstName, person.firstName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lastName, firstName, age);
        }

        @Override
        public String toString() {
            return "Person{lastName='" + lastName + "', firstName='" + firstName + "', age=" + age + "}";
        }
    }
}
