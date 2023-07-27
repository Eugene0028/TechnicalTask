import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main
{
    static boolean isInt = false, isString = false, isAscending = true, isDescending = false;
    static int CountInputFiles = 0;
    static String OutputName = null;
    static ArrayList<String> InputNames = new ArrayList<>();

    private static int CheckArgs(String[] args)
    {
        String StringArgs = Arrays.toString(args);
        if (StringArgs.length() < 3) {
            System.out.println("""
                    Error. Enter minimum three args.
                    Example: sort-it.exe -i -a out.txt in.txt (for integers ascending)
                    sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for strings ascending)
                    sort-it.exe -d -s out.txt in1.txt in2.txt (for strings descending)""");
            return -1;
        } else if (!StringArgs.contains("out") || !StringArgs.contains("in") || (!StringArgs.contains("-s") && !StringArgs.contains("-i"))) {
            System.out.println("""
                    Error. Try again.
                    Example: sort-it.exe -i -a out.txt in.txt (for integers ascending)
                    sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for strings ascending)
                    sort-it.exe -d -s out.txt in1.txt in2.txt (for strings descending)""");
            return -2;
        }

        for (String arg : args)
        {
            if (arg.contains("in")) {
                CountInputFiles++;
                InputNames.add("input\\" + arg);
            }
            if (arg.contains("out")) OutputName = arg;
            if (arg.equals("-d") && !isDescending)
            {
                isDescending = true;
                isAscending = false;
            }
            if (arg.equals("-s") && !isString) isString = true;
            if (arg.equals("-i") && !isInt) isInt = true;
        }
        System.out.printf("countInputFiles: %d\nisInt - %b\nisString - %b\nisDescending - %b\nisAscending - %b\n", CountInputFiles, isInt, isString, isDescending, isAscending);
        return 0;
    }

    public static void main(String[] args) throws IOException {
        if (CheckArgs(args) < 0) return;
        //Runtime runtime = Runtime.getRuntime();
        //long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        //long TotalFreeMemory = runtime.maxMemory() - usedMemory; // if file with objects will be bigger than TotalFreeMemory, we should to use separated sorting while StreamInput is open.
        new FileSorting(InputNames);
    }
}