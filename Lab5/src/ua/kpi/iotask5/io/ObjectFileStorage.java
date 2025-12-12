package ua.kpi.iotask5.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectFileStorage<T extends Serializable> {

    public void save(String path, List<T> items) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(items); // серіалізація "за замовчуванням"
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> load(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<T>) obj;
            }
            return new ArrayList<>();
        }
    }
}
