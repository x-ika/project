package model;

public enum Language {
    JAVA("Java", "Main.java"),
    CPP_MINGW("C++ (GNU)", "main.cpp"),
    CPP_VS("C++ (VS)", "main.cpp");

    private final String name;

    private final String defaultFileName;

    Language(String name, String defaultFileName) {
        this.name = name;
        this.defaultFileName = defaultFileName;
    }

    public String getName() {
        return name;
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public String toString() {
        return name;
    }
}
