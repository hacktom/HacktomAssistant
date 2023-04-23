import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            FileWatcher fileWatcher = new FileWatcher();
            String directoryToWatch = fileWatcher.mp3Path.toString();
            String fileExtension = ".mp3";
            String commandToExecute = "Get-ChildItem -Filter *.mp3 | ForEach-Object {\n" +
                    "    whisper $_.FullName --language es --model medium --device cpu\n" +
                    "}\n";
            fileWatcher.setParameters(directoryToWatch, fileExtension, commandToExecute);

            // Crea una instancia de AudioRecorder
            AudioRecorder audioRecorder = new AudioRecorder();

            // Configura el icono en la bandeja del sistema
            TrayIconHandler trayIconHandler = new TrayIconHandler(audioRecorder, fileWatcher);
            trayIconHandler.setupTrayIcon();

            // Comienza la vigilancia de archivos
            fileWatcher.watchDirectory();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
