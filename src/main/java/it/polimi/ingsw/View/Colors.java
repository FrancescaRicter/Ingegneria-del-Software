package it.polimi.ingsw.View;

public enum Colors {

    RED ( "\033[31m"),
    GREEN("\033[32m"),
    YELLOW ("\033[1;33m"),
    PINK("\033[1;35m"),
    BLUE("\033[0;34m"),
    MAGENTA("\u001b[35;1m"),

    CYAN("\u001B[36m"),

    WHITE("\033[0;97m"),
    BLACK ( "\033[30m"),
    GREY("\033[0;37m"),

    RESET("\033[0m"),

    WHITE_BACKGROUND ("\033[7m" ),
    BLACK_BACKGROUND ( "\u001B[40m"),

    RED_BACKGROUND ("\u001B[41m"),
    GREEN_BACKGROUND ("\u001B[42m"),
    YELLOW_BACKGROUND ( "\u001B[43m"),
    BLUE_BACKGROUND ("\u001B[44m"),
    PINK_BACKGROUND ( "\u001B[45m"),
    CYAN_BACKGROUND ("\u001B[46m");


    private final String string;

    Colors(String s) {
        this.string = s;
    }

    @Override
    public String toString() {
        return string;
    }
}
