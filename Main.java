import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String absolutePath = FileSystems.getDefault().getPath("packs").normalize().toAbsolutePath().toString();
        boolean packsDirExists = Files.exists(Paths.get(absolutePath));
        if (packsDirExists){

            // Scan packs directory for subdirectories
            File packsDir = new File(absolutePath);

            String[] modpacks = packsDir.list((file, s) -> new File(file, s).isDirectory());

            System.out.println(Arrays.toString(modpacks));

        } else {
            System.out.println(Fore.RED + "\"packs\" directory not found." + Fore.RESET);
            System.exit(1);
        }
    }
}

class Fore{
    public static String BLACK = "\u001b[30m";
    public static String RED = "\u001b[31m";
    public static String GREEN = "\u001b[32m";
    public static String YELLOW = "\u001b[33m";
    public static String BLUE = "\u001b[34m";
    public static String MAGENTA = "\u001b[35m";
    public static String CYAN = "\u001b[36m";
    public static String WHITE = "\u001b[37m";
    public static String RESET = "\u001b[0m";
}
