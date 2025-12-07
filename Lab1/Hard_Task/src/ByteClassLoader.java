import java.nio.file.Files;
import java.nio.file.Path;

public class ByteClassLoader extends ClassLoader {
    private final Path classesDir;

    public ByteClassLoader(Path classesDir) {
        super(ClassLoader.getSystemClassLoader());
        this.classesDir = classesDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // Для default package: name == "TestModule" => файл "TestModule.class"
            Path classFile = classesDir.resolve(name + ".class");
            byte[] bytes = Files.readAllBytes(classFile);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            throw new ClassNotFoundException("Не вдалося завантажити клас: " + name, e);
        }
    }
}
