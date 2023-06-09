import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class AudioRecorder {
    static String mp3FolderPath = "";
    static String temporaryFolderPath = "temporal";

    public void startRecording(String mp3Folder) {
        mp3FolderPath = mp3Folder;

        // Crear la carpeta temporal si no existe
        File tempFolder = new File(temporaryFolderPath);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }

        AudioRecorder recorder = new AudioRecorder();
        int durationInSeconds = 10; // Cambia esta variable para ajustar la duración de la grabación
        String outputFileName = "grabacion.mp3";

        System.out.println("Grabando audio...");
        recorder.recordAudio(durationInSeconds, outputFileName);
        System.out.println("Audio grabado y guardado en " + outputFileName);

        String folderPath = new File(outputFileName).getAbsoluteFile().getParent();
        System.out.println("Abriendo carpeta: " + folderPath);
        recorder.openFolder(folderPath);
    }


    public void recordAudio(int durationInSeconds, String outputFileName) {
        AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Error: Linea de audio no soportada.");
            System.exit(-1);
        }

        try {
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            File outputFile = new File(temporaryFolderPath, outputFileName);


            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
            Thread recordingThread = new Thread(() -> {
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    System.err.println("Error al grabar audio: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            recordingThread.start();

            for (int i = 0; i < durationInSeconds; i++) {
                System.out.printf("Tiempo de grabación: %d segundos%n", i);
                Thread.sleep(1000);
            }

            targetDataLine.stop();
            targetDataLine.close();
            recordingThread.join();

            // Mover el archivo de la carpeta temporal a la carpeta mp3
            Path source = Paths.get(temporaryFolderPath, outputFileName);
            Path destination = Paths.get(mp3FolderPath, outputFileName);
            try {
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Error al mover el archivo de la carpeta temporal a la carpeta mp3: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (LineUnavailableException | InterruptedException e) {
            System.err.println("Error al grabar audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openFolder(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            System.err.println("Error: La ruta proporcionada no es una carpeta.");
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            System.err.println("Error: No se puede abrir la carpeta en el escritorio.");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(folder);
        } catch (IOException e) {
            System.err.println("Error al abrir la carpeta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
