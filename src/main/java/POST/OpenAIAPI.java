package POST;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAIAPI {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) throws IOException {
        String criteriosBase = "el formato JSON debe ser el siguiente para a respuesta: [\n" +
                "    {\n" +
                "      \"comando\": \"\",\n" +
                "      \"descripcion\": \"\"\n" +
                "    },\n" +
                "    \n" +
                "  ]";
        String criterioBase2 = "dame en formato JSON la lista de comandos bash necesarios para: ";
        //String criterioBase3 "la ruta del usuari"
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "assistant");
        //message.put("content", criteriosBase + " " + criterioBase2 + " crear una carpeta llamada prueba y dentro un documento de texto que lleve el nombre logrado y que dentro contenga este es el mensaje dentro del archivo");
        //message.put("content", criteriosBase + " " + criterioBase2 + " saber cual es la ruta del perfil de usuario usuado actualmente y la ruta de la carpeta documentos");
        message.put("content", criteriosBase + " " + criterioBase2 + "ir a la carpeta Downloads y verificar si existe un archivo llamado recibo_cfe.pdf en caso de SI existir copialo con el nombre recibo_cfe_IA_COMPLETE.pdf");
        messages.put(message);
        requestBody.put("messages", messages);

        String response = makePostRequest(API_URL, requestBody.toString(), API_KEY);
        System.out.println(response);

        // Parse the response JSON string
        JSONObject responseJson = new JSONObject(response);

        // Extract the content from the assistant's message
        JSONArray choices = responseJson.getJSONArray("choices");
        JSONObject choice = choices.getJSONObject(0);
        JSONObject assistantMessage = choice.getJSONObject("message");
        String content = assistantMessage.getString("content");

        // Print the content
        System.out.println(content);
    }

    public static String makePostRequest(String url, String jsonInputString, String apiKey) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}

