import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonCommandExecutor {

    public static void main(String[] args) {
        String jsonString = "[\n" +
                "    {\n" +
                "      \"comando\": \"cd downloads\",\n" +
                "      \"descripcion\": \"ir a la carpeta downloads\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"comando\": \"mkdir IA\",\n" +
                "      \"descripcion\": \"crear una carpeta llamada IA\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"comando\": \"cd IA && touch prueba.txt\",\n" +
                "      \"descripcion\": \"dentro de esta carpeta crear un archivo de texto con el nombre prueba\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"comando\": \"echo 'hola hacktom' >> prueba.txt\",\n" +
                "      \"descripcion\": \"dentro de ese archivo el texto hola hacktom\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"comando\": \"if [[ -f CURP_ZACR910309HMCVNC07.pdf ]]; then cp CURP_ZACR910309HMCVNC07.pdf IA; fi\",\n" +
                "      \"descripcion\": \"si en la carpeta downloads existe el archivo CURP_ZACR910309HMCVNC07 PDF copialo a la carpeta IA\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"comando\": \"cd IA && ls -lh >> lista_archivos.docx\",\n" +
                "      \"descripcion\": \"crea un documento de WORD y dentro escribe la lista de archivos sus extenciones y hora de creacion de los archivos de la carpeta IA\"\n" +
                "    }\n" +
                "]\n" +
                "\n" +
                "Process finished with exit code 0\n";
        executeCommandsFromJson(jsonString);
        System.out.println(jsonString);
    }

    public static void executeCommandsFromJson(String jsonString) {
        JSONArray jsonArray = new JSONArray(jsonString);
        StringBuilder combinedCommand = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String command = jsonObject.getString("comando");
            combinedCommand.append(command).append("\n");
        }

        executeCommand(combinedCommand.toString());
    }

    public static void executeCommand(String command) {
        try {
            // Crea un archivo de script temporal
            Path tempScript = Files.createTempFile("tempScript", ".sh");

            // Escribe los comandos en el archivo de script
            try (BufferedWriter writer = Files.newBufferedWriter(tempScript)) {
                writer.write(command);
            }

            // Ejecuta el archivo de script con Git Bash
            Process process = Runtime.getRuntime().exec(new String[]{"C:\\Program Files\\Git\\git-bash.exe", tempScript.toString()});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();

            // Elimina el archivo de script temporal
            Files.delete(tempScript);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
