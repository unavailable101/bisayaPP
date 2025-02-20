import lexer.Lexer;
import lexer.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class BisayaPPFileInterpreter {
    private static String filename;

    public static void main(String[] args) throws Exception{
        if (args.length != 1){
            System.out.println("Usage: java Bisaya++ <filename>");
            return;
        }

//        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter file you want to run: ");
//
//        filename = args[0] + sc.nextLine() + ".bpp";

        try {
//            List<String> code = Files.readAllLines(Paths.get(filename));
            Lexer lexer = new Lexer(Files.readAllLines(Paths.get(args[0])));
//            lexer.tokenize();
            for (Token token : lexer.tokenize()){
                System.out.println(token);
            }

        } catch (IOException e) {
            System.out.println("Unable to read file: " + filename);
        }
    }
}
