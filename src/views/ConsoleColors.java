package views;

/**
 * Created by Chris on 28-Sep-17.
 */

// private static final String CHECK_MARK = "\u2713";
// private static final String ERROR_MARK = "\u2717";
public enum ConsoleColors {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    RESET("\u001B[0m"),
    BOLD("\033[0;1m");

    private String ansiColor;

    ConsoleColors(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return ansiColor;
    }

    public String getAnsiColor() {
        return ansiColor;
    }


    // Taken from http://cesarloachamin.github.io/2015/03/31/System-out-print-colors-and-unicode-characters/
//    public class PrintColorWriter extends PrintWriter{
//
//        private static final String ANSI_RESET = "\u001B[0m";
//
//        public PrintColorWriter(PrintStream out) throws UnsupportedEncodingException {
//            super(new OutputStreamWriter(out, "UTF-8"), true);
//        }
//
//        public void println(ConsoleColors color, String string) {
//            print(color);
//            print(string);
//            super.println(ConsoleColors.RESET.toString());
//            flush();
//        }
//
//        public void green(String string) {
//            println(ConsoleColors.GREEN, string);
//        }
//
//        public void red(String string) {
//            println(ConsoleColors.RED, string);
//        }
//    }
}
