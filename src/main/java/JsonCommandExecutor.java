import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonCommandExecutor {

    public static void main(String[] args) {
        String jsonString = "[\n" +
                "  {\n" +
                "    \"comando\": \"cd ~/Downloads\",\n" +
                "    \"descripcion\": \"Ir a la carpeta Downloads\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"comando\": \"if [ -f recibo_cfe.pdf ]; then\\n  cp recibo_cfe.pdf recibo_cfe_IA_COMPLETE.pdf\\nfi\",\n" +
                "    \"descripcion\": \"Verificar si existe el archivo recibo_cfe.pdf y copiarlo con el nombre recibo_cfe_IA_COMPLETE.pdf en caso de existir\"\n" +
                "  }\n" +
                "]";
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
