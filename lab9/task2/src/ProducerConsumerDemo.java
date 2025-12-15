import java.util.concurrent.ThreadLocalRandom;

public class ProducerConsumerDemo {

    public static void main(String[] args) throws Exception {
        RingBuffer<String> buf1 = new RingBuffer<>(30);
        RingBuffer<String> buf2 = new RingBuffer<>(30);

        // 1) П’ять потоків генерують рядки в перший буфер (daemon)
        for (int i = 1; i <= 5; i++) {
            int producerId = i;
            Thread t = new Thread(() -> {
                long k = 1;
                while (true) {
                    try {
                        String msg = "Потік № " + producerId + " згенерував повідомлення " + (k++);
                        buf1.put(msg);
                        // невелика "нерівномірність", щоб було видно паралельність
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1, 6));
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }, "producer-" + i);
            t.setDaemon(true);
            t.start();
        }

        // 2) Два потоки перекладають з першого буфера у другий (daemon)
        for (int i = 1; i <= 2; i++) {
            int translatorId = i;
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        String original = buf1.take();
                        String translated = "Потік № " + translatorId + " переклав повідомлення: " + original;
                        buf2.put(translated);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }, "translator-" + i);
            t.setDaemon(true);
            t.start();
        }

        // 3) Основний потік зчитує і друкує 100 повідомлень із другого буфера
        for (int i = 1; i <= 100; i++) {
            String msg = buf2.take();
            System.out.println(i + ") " + msg);
        }

        // daemon-потоки не тримають JVM -> програма коректно завершиться після друку 100 рядків
    }
}
