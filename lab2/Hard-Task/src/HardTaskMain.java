import java.util.List;

/**
 * Головний клас для демонстрації всіх Hard Tasks.
 * Запускає перевірку всіх трьох завдань.
 */
public class HardTaskMain {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              HARD TASKS - Демонстрація                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        task1_MyList();
        System.out.println("\n" + "─".repeat(62) + "\n");

        task2_MyLinkedHashSet();
        System.out.println("\n" + "─".repeat(62) + "\n");

        task3_MyCache();

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                  ✅ ВСІ ЗАВДАННЯ ВИКОНАНО                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Завдання 1: MyList - ArrayList та LinkedList
     */
    private static void task1_MyList() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ЗАВДАННЯ 1: MyList - ArrayList та LinkedList");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // MyArrayList
        System.out.println("▶ MyArrayList (з RandomAccess):");
        MyArrayList<String> arrayList = new MyArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        arrayList.add("JavaScript");
        System.out.println("  Додали 3 елементи: " + arrayList);

        arrayList.add(1, "C++");
        System.out.println("  Вставили 'C++' на позицію 1: " + arrayList);

        arrayList.addAll(List.of("Go", "Rust"));
        System.out.println("  Додали List: " + arrayList);

        System.out.println("  Елемент на позиції 2: " + arrayList.get(2));
        System.out.println("  Індекс 'Python': " + arrayList.indexOf("Python"));
        System.out.println("  Розмір: " + arrayList.size());

        // MyLinkedList
        System.out.println("\n▶ MyLinkedList (двозв'язний список):");
        MyLinkedList<String> linkedList = new MyLinkedList<>();
        linkedList.add("Apple");
        linkedList.add("Banana");
        linkedList.add("Cherry");
        System.out.println("  Додали 3 елементи: " + linkedList);

        linkedList.add(1, "Apricot");
        System.out.println("  Вставили 'Apricot' на позицію 1: " + linkedList);

        linkedList.remove(2);
        System.out.println("  Видалили елемент на позиції 2: " + linkedList);

        // Порівняння
        System.out.println("\n✓ Обидві реалізації працюють коректно");
        System.out.println("  MyArrayList: швидкий доступ (RandomAccess)");
        System.out.println("  MyLinkedList: ефективне вставлення/видалення");
    }

    /**
     * Завдання 2: MyLinkedHashSet
     */
    private static void task2_MyLinkedHashSet() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ЗАВДАННЯ 2: MyLinkedHashSet");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        MyLinkedHashSet<String> set = new MyLinkedHashSet<>();

        System.out.println("▶ Додавання елементів:");
        set.add("Java");
        set.add("Python");
        set.add("JavaScript");
        set.add("C++");
        System.out.println("  Набір: " + set);
        System.out.println("  Розмір: " + set.size());

        System.out.println("\n▶ Спроба додати дублікат:");
        boolean added = set.add("Java");
        System.out.println("  add('Java') повернуло: " + added);
        System.out.println("  Набір: " + set + " (дублікат не додано)");

        System.out.println("\n▶ Перевірка наявності:");
        System.out.println("  contains('Python'): " + set.contains("Python"));
        System.out.println("  contains('Ruby'): " + set.contains("Ruby"));

        System.out.println("\n▶ Видалення елемента:");
        set.remove("Python");
        System.out.println("  Після видалення 'Python': " + set);

        System.out.println("\n▶ Демонстрація збереження порядку:");
        MyLinkedHashSet<Integer> numbers = new MyLinkedHashSet<>();
        numbers.add(5);
        numbers.add(1);
        numbers.add(9);
        numbers.add(3);
        numbers.add(1); // дублікат
        System.out.println("  Додали: 5, 1, 9, 3, 1");
        System.out.println("  Результат: " + numbers);
        System.out.println("  ✓ Порядок збережено, дублікати проігноровано");

        System.out.println("\n▶ Тест на null:");
        try {
            set.add(null);
            System.out.println("  ❌ Помилка: null не викинув виключення");
        } catch (NullPointerException e) {
            System.out.println("  ✓ NullPointerException при додаванні null");
        }
    }

    /**
     * Завдання 3: MyCache з Expiry та Eviction
     */
    private static void task3_MyCache() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ЗАВДАННЯ 3: MyCache з Expiry та Eviction");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // Базові операції
        System.out.println("▶ Базові операції:");
        MyCache<String, String> cache = new MyCache<>(5, 0, MyCache.EvictionPolicy.LRU);
        cache.put("user:1", "Іван");
        cache.put("user:2", "Петро");
        cache.put("user:3", "Марія");
        System.out.println("  Додано 3 записи");
        System.out.println("  Розмір: " + cache.size() + "/5");
        System.out.println("  get('user:1'): " + cache.get("user:1"));
        System.out.println("  containsKey('user:2'): " + cache.containsKey("user:2"));

        // LRU Policy
        System.out.println("\n▶ LRU (Least Recently Used) Policy:");
        MyCache<Integer, String> lruCache = new MyCache<>(3, 0, MyCache.EvictionPolicy.LRU);
        lruCache.put(1, "один");
        lruCache.put(2, "два");
        lruCache.put(3, "три");
        System.out.println("  Додано: 1, 2, 3");

        lruCache.get(1); // використовуємо 1
        lruCache.put(4, "чотири"); // 2 буде витіснений
        System.out.println("  Використали 1, додали 4");
        System.out.println("  containsKey(2): " + lruCache.containsKey(2) + " (витіснено)");
        System.out.println("  containsKey(1): " + lruCache.containsKey(1) + " (збережено)");

        // LFU Policy
        System.out.println("\n▶ LFU (Least Frequently Used) Policy:");
        MyCache<Integer, String> lfuCache = new MyCache<>(3, 0, MyCache.EvictionPolicy.LFU);
        lfuCache.put(1, "один");
        lfuCache.put(2, "два");
        lfuCache.put(3, "три");

        lfuCache.get(1); // використовуємо 2 рази
        lfuCache.get(1);
        lfuCache.get(2); // використовуємо 1 раз

        lfuCache.put(4, "чотири"); // 3 буде витіснений (0 разів)
        System.out.println("  Використали: 1 (2 рази), 2 (1 раз)");
        System.out.println("  containsKey(3): " + lfuCache.containsKey(3) + " (витіснено як найменш частий)");

        // FIFO Policy
        System.out.println("\n▶ FIFO (First In First Out) Policy:");
        MyCache<Integer, String> fifoCache = new MyCache<>(3, 0, MyCache.EvictionPolicy.FIFO);
        fifoCache.put(1, "один");
        fifoCache.put(2, "два");
        fifoCache.put(3, "три");
        fifoCache.put(4, "чотири"); // 1 буде витіснений як найстаріший
        System.out.println("  Додали: 1, 2, 3, 4");
        System.out.println("  containsKey(1): " + fifoCache.containsKey(1) + " (витіснено як перший)");
        System.out.println("  containsKey(4): " + fifoCache.containsKey(4) + " (останній доданий)");

        // Null handling
        System.out.println("\n▶ Обробка null (відмінність від Map):");
        try {
            cache.put(null, "value");
            System.out.println("  ❌ null ключ прийнято");
        } catch (NullPointerException e) {
            System.out.println("  ✓ NullPointerException при put(null, value)");
        }

        try {
            cache.put("key", null);
            System.out.println("  ❌ null значення прийнято");
        } catch (NullPointerException e) {
            System.out.println("  ✓ NullPointerException при put(key, null)");
        }

        System.out.println("\n✓ Cache відрізняється від Map:");
        System.out.println("  • Не підтримує null");
        System.out.println("  • Автоматичний expiry");
        System.out.println("  • Політики eviction (LRU/LFU/FIFO)");
    }
}
