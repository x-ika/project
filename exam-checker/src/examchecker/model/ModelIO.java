package examchecker.model;

import com.google.gson.*;

import java.io.*;

public final class ModelIO {

    private ModelIO() {
    }

    public static void write(Object model, String file) {

        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(model);

            PrintWriter pw = new PrintWriter(file);
            pw.println(json);
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <T> T read(String file, Class<T> clazz) throws IOException {
        return new GsonBuilder().create().fromJson(new FileReader(file), clazz);
    }

}
