package modpackSwitcher;

import org.ini4j.Ini;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        // Parse args
        Main main = new Main();

        boolean ansi = true;
        boolean debug = false;
        for (String arg : args) {
            switch (arg) {
                case "--no-ansi":
                case "-n":
                    ansi = false;
                    break;
                case "--help":
                case "-h":
//                System.out.println("usage: switcher.jar [--version] [--help] [--no-ansi]");
                    System.out.println("usage: switcher.jar [--help] [--no-ansi]");
                    System.out.println();
                    System.out.println("optional arguments:");
                    System.out.println("  -h, --help     show this help message and exit");
//                System.out.println("  -V, --version  Print the program version and exit");
                    System.out.println("  -n, --no-ansi  Don't use ANSI Colors");
                    System.out.println("  -d, --debug    Show debug information and exit");
                    System.exit(0);
                case "--debug":
                case "-d":
                    debug = true;
            }
        }

        main.run(ansi, debug);

        }

    public void run(boolean ansi, boolean debug) {

        String absolutePath;
        String[] modpacks;
        String stringChoice;
        String jarCommand;
        String selectedPackDir;
        int exitCode;
        int choice;
        String lastUsedPackString;
        int lastUsedPack;
        Ini config = null;


        // Read last used modpack from config

        try {
            config = new Ini(new File("./mpswconfig.ini"));
            lastUsedPackString = config.get("system", "lastusedpack");
        } catch (IOException e) {
            try {
                // create file if it doesn't exist
                if (writeFile("./", "mpswconfig.ini", "[system]\nlastusedpack = 0\nabsoluteworkingdir = " + new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/") != 0) {
                    throw new IOException();
                } else {
                    config = new Ini(new File("./mpswconfig.ini"));
                    lastUsedPackString = config.get("system", "lastusedpack");
                }
            } catch (IOException | URISyntaxException e2) {
                File file = new File("./mpswconfig.ini");
                System.out.println(ansi ? Fore.YELLOW + "Warning:" + Fore.RESET + " Error on reading config file at " + Fore.WHITE + "\"" + file.getAbsoluteFile() + "\"" + Fore.RESET : "Warning: Error on reading config file at \"./mpswconfig.ini\"");
                lastUsedPackString = "0";
            }
        }
        
        if (debug) {
        	System.out.println("--- MODPACKSWITCHER DEBUG START ---\n");

                    System.out.println("OS: " + System.getProperty("os.name") + " | " + System.getProperty("os.version") + " | " + System.getProperty("os.arch") + "\n");

                    String absolutePath = config.get("system", "absoluteworkingdir") + "/packs";
                    System.out.println("packs directory in current directory: " + Files.exists(Paths.get(absolutePath)));
                    System.out.println("packs directory path: " + absolutePath);

                    File packsDir = new File(absolutePath);
                    String[] modpacks = packsDir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            return new File(file, s).isDirectory();
                        }
                    });
                    System.out.println("packs directory contents: " + Arrays.toString(modpacks) + "\n");

                    System.out.print("Java: ");
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process proc = rt.exec("java -version");
                        int exitVal = proc.waitFor();
                        if (exitVal == 0) {
                            System.out.println("yes");
                            System.out.println("Version: " + System.getProperty("java.version"));
                        } else {
                            System.out.println("no");
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("no (IOException | InterruptedException)");
                    }

                    System.out.println("\n--- MODPACKSWITCHER DEBUG END ---");
                    System.exit(0);
        }

        absolutePath = config.get("system", "absoluteworkingdir") + "/packs";
        boolean packsDirExists = Files.exists(Paths.get(absolutePath));
        if (packsDirExists) {

            // Scan packs directory for subdirectories
            File packsDir = new File(absolutePath);

            modpacks = packsDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return new File(file, s).isDirectory();
                }
            });

            if (modpacks != null && modpacks.length == 0) {
                System.out.println(ansi ? Fore.RED + "Fatal Error. No modpack folders found in " + Fore.WHITE + "\"" + absolutePath + "\"" + Fore.RESET : "Fatal Error. No modpack folders found in \"" + absolutePath + "\"");
                System.exit(1);
            }

            if (Arrays.asList(modpacks).contains(lastUsedPackString)) {
                lastUsedPack = Arrays.asList(modpacks).indexOf(lastUsedPackString);
            } else {
                lastUsedPack = 0;
            }


            // Print out modpack choices
            System.out.println("Select a pack. Last used pack will be started in 5 seconds.");
            for (int i = 0; i < (modpacks.length); i++) {
                System.out.print((i + 1) + ". " + modpacks[i]);

                if (i == lastUsedPack) {
                    System.out.println(ansi ? Fore.GREEN + " (last used)" + Fore.RESET : " (last used)");
                } else {
                    System.out.println();
                }
            }
            System.out.println();

            stringChoice = getModpackSelectionWithTimeout(5, lastUsedPack, modpacks, ansi);
            choice = Integer.parseInt(stringChoice);

            // Write last used modpack if changed
            if (choice != lastUsedPack) {
                config.put("system", "lastusedpack", modpacks[choice]);
                try{
                    File inioutfile = new File("./mpswconfig.ini");
                    if (!inioutfile.exists()) {
                            System.out.println(ansi ? Fore.YELLOW + "Warning:" + Fore.RESET + " Error on writing config file at " + Fore.WHITE + "\"./mpswconfig.ini\"" + Fore.RESET : "Warning: Error on writing config file at \"./mpswconfig.ini\"");
                    } else {
                        config.store();
                    }
                } catch (IOException e){
                    System.out.println(ansi ? Fore.YELLOW + "Warning:" + Fore.RESET + " Error on writing config file at " + Fore.WHITE + "\"./mpswconfig.ini\"" + Fore.RESET : "Warning: Error on writing config file at \"./mpswconfig.ini\"");
                }
            }

            // Read `modpackswitcher.txt` file in selected modpack directory to find the correct jar to execute
            selectedPackDir = absolutePath + "/" + modpacks[choice] + "/";
            jarCommand = readFile(selectedPackDir + "modpackswitcher.txt");
            if (jarCommand.equals("FileNotFoundException")){
                System.out.println(ansi ? Fore.RED + "Fatal error. File " + Fore.WHITE + "\"" + selectedPackDir + "modpackswitcher.txt" + "\"" + Fore.RED + " not found." + Fore.RESET : "File \"" + selectedPackDir + "modpackswitcher.txt" + "\" not found.");
                System.exit(1);
            } else if (jarCommand.equals("IOException")) {
                System.out.println(ansi ? Fore.RED + "Fatal error. IOException" + Fore.RESET : "Fatal error. IOException");
                System.exit(1);
            }


            try {
                System.out.println();
                Process process = new ProcessBuilder("/bin/bash", "-c", "cd \"" + selectedPackDir + "\" && " + jarCommand).inheritIO().start();
                exitCode = process.waitFor();
                if (exitCode == 0)
                    System.exit(0);
                else {
                    System.out.println(ansi ? Fore.RED + "Fatal error\n" + Fore.RESET : "Fatal error\n");
                    System.exit(1);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println(ansi ? Fore.RED + "Fatal error\n" + Fore.RESET : "Fatal error\n");
                e.printStackTrace();
                System.exit(1);
            }

        } else {
            System.out.println(ansi ? Fore.RED + "\"packs\" directory not found." + Fore.RESET : "\"packs\" directory not found.");
            System.exit(1);
        }

    }

    public String readFile(String path){
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString().trim();

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        }
    }

    public int writeFile(String directoryName, String fileName, String value){

        File directory = new File(directoryName);
        if (! directory.exists()){
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        File file = new File(directoryName + "/" + fileName);
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
            return 0;
        }
        catch (IOException e){
            return 1;
        }
    }

    public String getModpackSelectionWithTimeout(int delay, int defaultChoice, String[] modpacks, boolean ansi){
        defaultChoice++;
        Callable<Integer> k = new Callable<Integer>() {
            @Override
            public Integer call() {
                return new Scanner(System.in).nextInt();
            }
        };
        long start = System.currentTimeMillis();
        int choice = defaultChoice;
        boolean valid;
        ExecutorService l = Executors.newFixedThreadPool(1);
        Future<Integer> g;
        System.out.print("Which modpack? [1-" + modpacks.length + "] ");
        g = l.submit(k);
        done: while (System.currentTimeMillis() - start < delay * 1000L) {
            do {
                valid = true;
                if (g.isDone()) {
                    try {
                        choice = g.get();
                        if (choice >= 0 && choice <= modpacks.length) {
                            if (choice == 0){
                                choice = defaultChoice;
                            }
                            break done;
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
                        System.out.println(ansi ? Fore.RED + "Please select a valid number.\n" + Fore.RESET : "Please select a valid number.\n");
                        g = l.submit(k);
                        valid = false;
                        // Reset timer
                        System.out.print("Which modpack? [1-" + modpacks.length + "] ");
                        start = System.currentTimeMillis();
                    }
                }
            } while (!valid);
        }

        g.cancel(true);
        return String.valueOf(choice - 1);
    }

}

class Fore {

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