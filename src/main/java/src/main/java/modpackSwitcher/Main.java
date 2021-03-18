package src.main.java.modpackSwitcher;

//import com.beust.jcommander.JCommander;
//import com.beust.jcommander.Parameter;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

//    @Parameter(names = {"--no-ansi", "-n"})
//    public boolean ansi = true;
//
//    @Parameter(names = {"--help", "-h"})
//    public boolean help = false;

//    @Parameter(names = {"--version", "-V"})
//    public boolean show_version = false;

    public static void main(String[] args) {
        // Parse args
        Main main = new Main();
//        JCommander.newBuilder()
//                .addObject(main)
//                .build()
//                .parse(argv);

        boolean ansi = false;
        for (int i = 0; i < args.length; i++){
            if (args[i].equals("--no-ansi") || args[i].equals("-n")){
                ansi = true;
            } else if (args[i].equals("--help") || args[i].equals("-h")){
//                System.out.println("usage: switcher.jar [--version] [--help] [--no-ansi]");
                System.out.println("usage: switcher.jar [--help] [--no-ansi]");
                System.out.println();
                System.out.println("optional arguments:");
                System.out.println("  -h, --help     show this help message and exit");
//                System.out.println("  -V, --version  Print the program version and exit");
                System.out.println("  -n, --no-ansi  Don\'t use ANSI Colors");
                System.exit(0);
            }
        }


        main.run(ansi);

        }

    public void run(boolean ansi) {

        String absolutePath;
        String[] modpacks;
        String stringChoice;
        String line;
        String jarCommand = null;
        String selectedPackDir;
        int exitCode;
        int choice;
        String errorLine;

        absolutePath = FileSystems.getDefault().getPath("packs").normalize().toAbsolutePath().toString();
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

            // Print out modpack choices
            for (int i = 0; i < modpacks.length; i++) {
                System.out.print((i + 1) + ". " + modpacks[i]);
                //Possible default config file
                if (i == 0) {
                    System.out.println(" (default)");
                } else {
                    System.out.println();
                }
            }
            System.out.println();
            Scanner input = new Scanner(System.in);

            // While user input is invalid (not in range), ask them to choose a modpack
            boolean valid = false;
            stringChoice = "";
            while (!valid) {
                System.out.print("Which modpack? [1-" + modpacks.length + "] ");
                stringChoice = input.nextLine();
                try {
                    if (stringChoice.isEmpty()) {
                        stringChoice = "1";
                        valid = true;
                    } else if (Integer.parseInt(stringChoice) >= 1 && Integer.parseInt(stringChoice) <= modpacks.length) {
                        valid = true;
                    } else {
                        System.out.println(ansi ? Fore.RED + "Please select a valid number.\n" + Fore.RESET : "Please select a valid number.\n");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ansi ? Fore.RED + "Please select a valid number.\n" + Fore.RESET : "Please select a valid number.\n");
                }
            }

            choice = Integer.parseInt(stringChoice);

            // Read `modpackswitcher.txt` file in selected modpack directory to find the correct jar to execute
            selectedPackDir = absolutePath + "/" + modpacks[choice - 1] + "/";
            jarCommand = readFile(selectedPackDir);
            if (jarCommand.equals("FileNotFoundException")){
                System.out.println(ansi ? Fore.RED + "Fatal error. File " + Fore.WHITE + "\"" + selectedPackDir + "modpackswitcher.txt" + "\"" + Fore.RED + " not found." + Fore.RESET : "File \"" + selectedPackDir + "modpackswitcher.txt" + "\" not found.");
                System.exit(1);
            } else if (jarCommand.equals("IOException")) {
                System.out.println(ansi ? Fore.RED + "Fatal error. IOException" + Fore.RESET : "Fatal error. IOException");
                System.exit(1);
            }


            try {
                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "cd \"" + selectedPackDir + "\" && " + jarCommand);
                Process process = processBuilder.start();
                exitCode = process.waitFor();
                if (exitCode == 0)
                    System.exit(0);
                else {
                    System.out.println(ansi ? Fore.RED + "Fatal error\n" + Fore.RESET : "Fatal error\n");
                    try (final BufferedReader b = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        if ((errorLine = b.readLine()) != null)
                            System.out.println(errorLine);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
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
            BufferedReader br = new BufferedReader(new FileReader(path + "modpackswitcher.txt"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String jarCommand = sb.toString().trim();
            return jarCommand;

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        }
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