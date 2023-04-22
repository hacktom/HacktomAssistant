import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileWatcher {

    private  String directoryToWatch;
    private  String fileExtension;
    private  String commandToExecute;
    private final Path recordsPath;
    private final Path mp3Path;
    private final Path whisCodePath;
    private final Path savePath;


    public FileWatcher() throws IOException {
        recordsPath = Paths.get("records");
        mp3Path = recordsPath.resolve("mp3");
        whisCodePath = recordsPath.resolve("whisCode");
        savePath = recordsPath.resolve("save");

        createDirectories(recordsPath, mp3Path, whisCodePath, savePath);
    }

    public void setParameters(String directoryToWatch, String fileExtension, String commandToExecute) {
        this.directoryToWatch = directoryToWatch;
        this.fileExtension = fileExtension;
        this.commandToExecute = commandToExecute;
    }

    private void createDirectories(Path... paths) throws IOException {
        for (Path path : paths) {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        }
    }

    public void watchDirectory() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            mp3Path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    if (fileName.toString().endsWith(fileExtension)) {
                        System.out.println("Archivo .mp3 detectado: " + fileName);
                        Files.move(mp3Path.resolve(fileName), whisCodePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                        executeCommand(commandToExecute);
                        checkForTxtFileAndMoveFiles();
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkForTxtFileAndMoveFiles() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(whisCodePath)) {
            for (Path file : stream) {
                if (file.toString().endsWith(".txt")) {
                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    Path newSaveFolder = savePath.resolve(dateTime.format(formatter));
                    Files.createDirectory(newSaveFolder);
                    try (DirectoryStream<Path> whisCodeStream = Files.newDirectoryStream(whisCodePath)) {
                        for (Path whisCodeFile : whisCodeStream) {
                            Files.move(whisCodeFile, newSaveFolder.resolve(whisCodeFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void executeCommand(String command) {
        try {
            System.out.println("entro al command: " + command);
            String[] cmd = {"powershell.exe", "-Command", command};
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.directory(whisCodePath.toFile()); // Establecer el directorio de trabajo como la carpeta whisCode
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            FileWatcher fileWatcher = new FileWatcher();
            String directoryToWatch = fileWatcher.mp3Path.toString();
            String fileExtension = ".mp3";
            String commandToExecute = "Get-ChildItem -Filter *.mp3 | ForEach-Object {\n" +
                    "    whisper $_.FullName --language es --model medium --device cpu\n" +
                    "}\n";
            fileWatcher.setParameters(directoryToWatch, fileExtension, commandToExecute);
            fileWatcher.watchDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}