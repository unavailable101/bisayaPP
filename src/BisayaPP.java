import interpreter.Interpreter;
import lexer.Lexer;
import parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BisayaPP {
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
//            new Interpreter().interpret(
//                    new Parser(
                            new Lexer(
                                    Files.readAllLines(
                                            Paths.get(
                                                    args[0]
                                            )
                                    )
                            ).readlines()
//                    ).parse()
//            )
            ;
        } catch (IOException e) {
            System.out.println("Unable to read file: " + filename);
        }
    }
}
