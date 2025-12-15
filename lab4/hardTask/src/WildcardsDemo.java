import java.util.*;

/**
 * Демонстрація Generics та Wildcards в MyLinkedHashSet
 * Custom Collections 2.0 - Hard Task 2
 */
public class WildcardsDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("ДЕМОНСТРАЦІЯ WILDCARDS В MyLinkedHashSet");
        System.out.println("=".repeat(70));
        System.out.println();

        demo1_BasicGenerics();
        demo2_ExtendsWildcard();
        demo3_SuperWildcard();
        demo4_UnboundedWildcard();
        demo5_SetOperations();
        demo6_RealWorldExamples();
        demo7_TypeSafety();
    }

    /**
     * Демонстрація 1: Базові Generics
     */
    private static void demo1_BasicGenerics() {
        System.out.println("📌 DEMO 1: Базові Generics");
        System.out.println("-".repeat(70));

        // Type-safe колекції
        MyLinkedHashSet<String> strings = new MyLinkedHashSet<>();
        strings.add("Java");
        strings.add("Python");
        strings.add("JavaScript");

        System.out.println("MyLinkedHashSet<String>: " + strings);

        MyLinkedHashSet<Integer> numbers = new MyLinkedHashSet<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        System.out.println("MyLinkedHashSet<Integer>: " + numbers);

        // Compile-time type safety
        // strings.add(123); // ❌ Помилка компіляції!

        System.out.println("✅ Type safety забезпечено на етапі компіляції");
        System.out.println();
    }

    /**
     * Демонстрація 2: ? extends E (Upper Bounded Wildcard)
     * Producer Extends - читаємо з колекції
     */
    private static void demo2_ExtendsWildcard() {
        System.out.println("📌 DEMO 2: ? extends E (Upper Bounded Wildcard)");
        System.out.println("-".repeat(70));

        // Ієрархія класів для демонстрації
        class Animal {
            String name;
            Animal(String name) { this.name = name; }
            @Override public String toString() { return name; }
        }

        class Dog extends Animal {
            Dog(String name) { super(name); }
        }

        class Cat extends Animal {
            Cat(String name) { super(name); }
        }

        // 1. addAll(Collection<? extends E>)
        System.out.println("1️⃣ addAll(Collection<? extends E>):");

        MyLinkedHashSet<Animal> animals = new MyLinkedHashSet<>();

        // Можемо додати List<Dog> в MyLinkedHashSet<Animal>
        List<Dog> dogs = Arrays.asList(
                new Dog("Рекс"),
                new Dog("Бобік")
        );
        animals.addAll(dogs);  // ✅ Dog extends Animal

        // Можемо додати List<Cat> в MyLinkedHashSet<Animal>
        List<Cat> cats = Arrays.asList(
                new Cat("Мурчик"),
                new Cat("Барсик")
        );
        animals.addAll(cats);  // ✅ Cat extends Animal

        System.out.println("   Animals: " + animals);
        System.out.println("   ✅ Додано собак і котів у колекцію тварин");
        System.out.println();

        // 2. intersection(MyLinkedHashSet<? extends E>)
        System.out.println("2️⃣ intersection(MyLinkedHashSet<? extends E>):");

        MyLinkedHashSet<Animal> animals1 = new MyLinkedHashSet<>();
        animals1.addAll(Arrays.asList(
                new Dog("Рекс"),
                new Cat("Мурчик")
        ));

        MyLinkedHashSet<Dog> dogs2 = new MyLinkedHashSet<>();
        dogs2.add(new Dog("Рекс"));

        // Можемо знайти перетин з MyLinkedHashSet<Dog>
        MyLinkedHashSet<Animal> common = animals1.intersection(dogs2);
        System.out.println("   Перетин: " + common);
        System.out.println();

        // 3. Чому extends?
        System.out.println("3️⃣ Чому використовуємо ? extends E:");
        System.out.println("   - Гарантуємо що можемо безпечно ЧИТАТИ елементи");
        System.out.println("   - Будь-який елемент з колекції є підтипом E");
        System.out.println("   - Принцип PECS: Producer Extends");
        System.out.println();
    }

    /**
     * Демонстрація 3: ? super E (Lower Bounded Wildcard)
     * Consumer Super - записуємо в колекцію
     */
    private static void demo3_SuperWildcard() {
        System.out.println("📌 DEMO 3: ? super E (Lower Bounded Wildcard)");
        System.out.println("-".repeat(70));

        // 1. copyTo(Collection<? super E>)
        System.out.println("1️⃣ copyTo(Collection<? super E>):");

        MyLinkedHashSet<Integer> integers = new MyLinkedHashSet<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);

        // Можемо копіювати Integer у Collection<Number>
        Collection<Number> numbers = new ArrayList<>();
        integers.copyTo(numbers);  // ✅ Number - супертип Integer
        System.out.println("   Integers: " + integers);
        System.out.println("   Copied to Numbers: " + numbers);

        // Можемо копіювати Integer у Collection<Object>
        Collection<Object> objects = new ArrayList<>();
        integers.copyTo(objects);  // ✅ Object - супертип Integer
        System.out.println("   Copied to Objects: " + objects);
        System.out.println();

        // 2. Чому super?
        System.out.println("2️⃣ Чому використовуємо ? super E:");
        System.out.println("   - Гарантуємо що можемо безпечно ЗАПИСУВАТИ елементи");
        System.out.println("   - Колекція може зберігати будь-який супертип E");
        System.out.println("   - Принцип PECS: Consumer Super");
        System.out.println();

        // 3. Приклад з ієрархією
        System.out.println("3️⃣ Практичний приклад:");
        System.out.println("   Integer -> Number -> Object");
        System.out.println("   copyTo може приймати:");
        System.out.println("   ✅ Collection<Integer>");
        System.out.println("   ✅ Collection<Number>");
        System.out.println("   ✅ Collection<Object>");
        System.out.println("   ❌ Collection<String> - не супертип");
        System.out.println();
    }

    /**
     * Демонстрація 4: ? (Unbounded Wildcard)
     * Для операцій що не залежать від типу
     */
    private static void demo4_UnboundedWildcard() {
        System.out.println("📌 DEMO 4: ? (Unbounded Wildcard)");
        System.out.println("-".repeat(70));

        MyLinkedHashSet<String> strings = new MyLinkedHashSet<>();
        strings.add("Java");
        strings.add("Python");
        strings.add("C++");

        // 1. containsAll(Collection<?>)
        System.out.println("1️⃣ containsAll(Collection<?>):");

        List<String> check1 = Arrays.asList("Java", "Python");
        System.out.println("   Чи містить " + check1 + "? " +
                strings.containsAll(check1));

        // Можемо перевірити колекцію будь-якого типу
        List<Object> check2 = Arrays.asList("Java", 123, "Unknown");
        System.out.println("   Чи містить " + check2 + "? " +
                strings.containsAll(check2));
        System.out.println();

        // 2. removeAll(Collection<?>)
        System.out.println("2️⃣ removeAll(Collection<?>):");

        MyLinkedHashSet<Integer> numbers = new MyLinkedHashSet<>();
        numbers.addAll(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println("   До видалення: " + numbers);

        List<?> toRemove = Arrays.asList(2, 4);
        numbers.removeAll(toRemove);
        System.out.println("   Після видалення " + toRemove + ": " + numbers);
        System.out.println();

        // 3. retainAll(Collection<?>)
        System.out.println("3️⃣ retainAll(Collection<?>):");

        MyLinkedHashSet<String> languages = new MyLinkedHashSet<>();
        languages.addAll(Arrays.asList("Java", "Python", "C++", "JavaScript", "Ruby"));
        System.out.println("   До фільтрації: " + languages);

        Collection<?> toKeep = Arrays.asList("Java", "Python", "Go");
        languages.retainAll(toKeep);
        System.out.println("   Після retainAll " + toKeep + ": " + languages);
        System.out.println();

        // 4. Чому unbounded?
        System.out.println("4️⃣ Чому використовуємо ?:");
        System.out.println("   - Операція не залежить від конкретного типу");
        System.out.println("   - Використовуємо тільки методи Object (equals)");
        System.out.println("   - Максимальна гнучкість");
        System.out.println();
    }

    /**
     * Демонстрація 5: Операції над множинами з wildcards
     */
    private static void demo5_SetOperations() {
        System.out.println("📌 DEMO 5: Операції над множинами з Wildcards");
        System.out.println("-".repeat(70));

        // Приклад з числами
        MyLinkedHashSet<Number> set1 = new MyLinkedHashSet<>();
        set1.addAll(Arrays.asList(1, 2, 3.5, 4));

        MyLinkedHashSet<Integer> set2 = new MyLinkedHashSet<>();
        set2.addAll(Arrays.asList(3, 4, 5));

        System.out.println("Set1 (Number): " + set1);
        System.out.println("Set2 (Integer): " + set2);
        System.out.println();

        // Перетин
        System.out.println("1️⃣ Intersection (Set1 ∩ Set2):");
        MyLinkedHashSet<Number> intersection = set1.intersection(set2);
        System.out.println("   Результат: " + intersection);
        System.out.println();

        // Об'єднання
        System.out.println("2️⃣ Union (Set1 ∪ Set2):");
        MyLinkedHashSet<Number> union = set1.union(set2);
        System.out.println("   Результат: " + union);
        System.out.println();

        // Різниця
        System.out.println("3️⃣ Difference (Set1 \\ Set2):");
        MyLinkedHashSet<Number> difference = set1.difference(set2);
        System.out.println("   Результат: " + difference);
        System.out.println();

        // copyOf - статичний метод з wildcard
        System.out.println("4️⃣ copyOf(Collection<? extends T>):");
        List<String> list = Arrays.asList("A", "B", "C");
        MyLinkedHashSet<String> copied = MyLinkedHashSet.copyOf(list);
        System.out.println("   Створено з List: " + copied);
        System.out.println();
    }

    /**
     * Демонстрація 6: Реальні приклади використання
     */
    private static void demo6_RealWorldExamples() {
        System.out.println("📌 DEMO 6: Реальні приклади");
        System.out.println("-".repeat(70));

        // Приклад 1: Унікальні теги з різних джерел
        System.out.println("1️⃣ Збір унікальних тегів:");

        MyLinkedHashSet<String> allTags = new MyLinkedHashSet<>();

        List<String> blogTags = Arrays.asList("java", "programming", "tutorial");
        List<String> videoTags = Arrays.asList("java", "coding", "beginner");
        List<String> podcastTags = Arrays.asList("programming", "interview");

        allTags.addAll(blogTags);    // ? extends String
        allTags.addAll(videoTags);   // ? extends String
        allTags.addAll(podcastTags); // ? extends String

        System.out.println("   Всі унікальні теги: " + allTags);
        System.out.println();

        // Приклад 2: Фільтрація даних
        System.out.println("2️⃣ Фільтрація категорій товарів:");

        MyLinkedHashSet<String> productCategories = new MyLinkedHashSet<>();
        productCategories.addAll(Arrays.asList(
                "Electronics", "Books", "Clothing", "Food", "Toys"
        ));

        Collection<String> availableCategories = Arrays.asList(
                "Electronics", "Books", "Sports"
        );

        productCategories.retainAll(availableCategories); // ?
        System.out.println("   Доступні категорії: " + productCategories);
        System.out.println();

        // Приклад 3: Експорт даних
        System.out.println("3️⃣ Експорт ID користувачів:");

        MyLinkedHashSet<Integer> userIds = new MyLinkedHashSet<>();
        userIds.addAll(Arrays.asList(101, 102, 103, 104, 105));

        // Експорт в List<Number> для API
        List<Number> apiIds = new ArrayList<>();
        userIds.copyTo(apiIds); // ? super Integer

        System.out.println("   User IDs: " + userIds);
        System.out.println("   Exported to API: " + apiIds);
        System.out.println();
    }

    /**
     * Демонстрація 7: Type Safety та помилки компіляції
     */
    private static void demo7_TypeSafety() {
        System.out.println("📌 DEMO 7: Type Safety");
        System.out.println("-".repeat(70));

        System.out.println("✅ ЩО ПРАЦЮЄ:");
        System.out.println();

        // 1. Правильне використання extends
        System.out.println("1️⃣ MyLinkedHashSet<Number>.addAll(List<Integer>)");
        MyLinkedHashSet<Number> numbers = new MyLinkedHashSet<>();
        List<Integer> integers = Arrays.asList(1, 2, 3);
        numbers.addAll(integers); // ✅ Integer extends Number
        System.out.println("   ✅ Компілюється: " + numbers);
        System.out.println();

        // 2. Правильне використання super
        System.out.println("2️⃣ MyLinkedHashSet<Integer>.copyTo(Collection<Number>)");
        MyLinkedHashSet<Integer> ints = new MyLinkedHashSet<>();
        ints.addAll(Arrays.asList(10, 20, 30));
        Collection<Number> nums = new ArrayList<>();
        ints.copyTo(nums); // ✅ Number super Integer
        System.out.println("   ✅ Компілюється: " + nums);
        System.out.println();

        // 3. Unbounded wildcard
        System.out.println("3️⃣ containsAll(Collection<?>)");
        MyLinkedHashSet<String> strings = new MyLinkedHashSet<>();
        strings.addAll(Arrays.asList("A", "B", "C"));
        Collection<Object> mixed = Arrays.asList("A", 123, "C");
        boolean result = strings.containsAll(mixed); // ✅
        System.out.println("   ✅ Компілюється: " + result);
        System.out.println();

        System.out.println("❌ ЩО НЕ ПРАЦЮЄ (помилки компіляції):");
        System.out.println();
        System.out.println("// MyLinkedHashSet<Integer> ints = new MyLinkedHashSet<>();");
        System.out.println("// List<Number> numbers = Arrays.asList(1.5, 2.5);");
        System.out.println("// ints.addAll(numbers);");
        System.out.println("// ❌ Помилка: Number не є підтипом Integer");
        System.out.println();
        System.out.println("// MyLinkedHashSet<Number> nums = new MyLinkedHashSet<>();");
        System.out.println("// Collection<Integer> ints = new ArrayList<>();");
        System.out.println("// nums.copyTo(ints);");
        System.out.println("// ❌ Помилка: Integer не є супертипом Number");
        System.out.println();
    }

    /**
     * Бонус: PECS принцип
     */
    @SuppressWarnings("unused")
    private static void bonusPECS() {
        System.out.println("📌 БОНУС: Принцип PECS");
        System.out.println("-".repeat(70));
        System.out.println("PECS = Producer Extends, Consumer Super");
        System.out.println();
        System.out.println("🔹 Producer Extends (? extends E):");
        System.out.println("   - Використовуйте коли ЧИТАЄТЕ з колекції");
        System.out.println("   - Колекція є PRODUCER (виробник) даних");
        System.out.println("   - Приклад: addAll(Collection<? extends E>)");
        System.out.println();
        System.out.println("🔹 Consumer Super (? super E):");
        System.out.println("   - Використовуйте коли ЗАПИСУЄТЕ в колекцію");
        System.out.println("   - Колекція є CONSUMER (споживач) даних");
        System.out.println("   - Приклад: copyTo(Collection<? super E>)");
        System.out.println();
        System.out.println("🔹 Unbounded (?):");
        System.out.println("   - Операція не залежить від типу");
        System.out.println("   - Використовуємо тільки Object методи");
        System.out.println("   - Приклад: containsAll(Collection<?>)");
        System.out.println();
    }
}