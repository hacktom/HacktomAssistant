import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VoiceRecognizer {
    private LiveSpeechRecognizer recognizer;
    private boolean isActive;

    public VoiceRecognizer() throws IOException {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/es-ms");
        configuration.setDictionaryPath("resource:/es-ms/es.dict");
        configuration.setLanguageModelPath("resource:/es-ms/es-20k.lm");


        configuration.setSampleRate(16000);

        recognizer = new LiveSpeechRecognizer(configuration);
        isActive = false;
    }

    public void startListening() {
        recognizer.startRecognition(true);

        while (true) {
            SpeechResult result = recognizer.getResult();
            String hypothesis = result.getHypothesis();

            System.out.println(hypothesis.toString());
            if (hypothesis.contains("amigo")) {
                isActive = true;
                System.out.println("Activado");
            }

            if (hypothesis.contains("archivo")) {
                createFile("hola.txt");
            }
        }
    }

    private void createFile(String fileName) {
        Path filePath = Paths.get(fileName);

        try {
            Files.createFile(filePath);
            System.out.println("Archivo creado: " + fileName);
            openFile(filePath.toFile());
        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo: " + fileName);
            e.printStackTrace();
        }
    }

    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                System.err.println("No se pudo abrir el archivo: " + file.getName());
                e.printStackTrace();
            }
        } else {
            System.err.println("No se puede abrir el archivo, el sistema no soporta la función Desktop.");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Iniciando....");
        VoiceRecognizer voiceRecognizer = new VoiceRecognizer();
        voiceRecognizer.startListening();
    }
}
