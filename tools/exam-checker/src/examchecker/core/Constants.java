package examchecker.core;

public final class Constants {

    private Constants() {
    }

    public static final String MAIN_FRAME_TITLE =           "Exam Checker";
    public static final String ABOUT_DIALOG_TEXT =          "Checker";

    public static final String RESOURCES_DIR = "resources/";

    public static final String PROPERTIES_FILE = RESOURCES_DIR + "application.properties";
    public static final String LOGO_FILE = RESOURCES_DIR + "logo.jpg";

    public static final String KEY_MODEL_PATH = "model_path";
    public static final String KEY_DATA_PATH = "data_path";
    public static final String KEY_RESULTS_PATH = "results_path";

    public static final String KEY_RECOGNIZER_BLACK_WHITE_THRESHOLD =        "recognizer.blackWhiteThreshold";
    public static final String KEY_RECOGNIZER_CONTENT =                      "recognizer.contentPolygon";
    public static final String KEY_RECOGNIZER_LINE_THICKNESS =                "recognizer.lineThickness";
    public static final String KEY_RECOGNIZER_MAX_SQUARES_ON_PAGE =          "recognizer.maxSquaresOnPage";
    public static final String KEY_RECOGNIZER_MIN_SQUARES_ON_PAGE =          "recognizer.minSquaresOnPage";
    public static final String KEY_RECOGNIZER_MAX_EDGE_RATIO =               "recognizer.maxEdgeRatio";
    public static final String KEY_RECOGNIZER_MIN_EDGE_FILL_PERCENTAGE =     "recognizer.minEdgeFillPercentage";
    public static final String KEY_RECOGNIZER_MAX_SQUARE_SECTION_AREA  =     "recognizer.maxSquareSectionArea";
    public static final String KEY_RECOGNIZER_MIN_SQUARE_FILL_PERCENTAGE =   "recognizer.minSquareFillPercentage";
    public static final String KEY_DEBUG_FRAME_SCALE =                       "recognizer.debugFrameScale";

    public static final String UNDEFINED = "#";

}
