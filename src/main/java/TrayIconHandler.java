import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class TrayIconHandler {
    private final AudioRecorder audioRecorder;
    private final FileWatcher fileWatcher;

    public TrayIconHandler(AudioRecorder audioRecorder, FileWatcher fileWatcher) {
        this.audioRecorder = audioRecorder;
        this.fileWatcher = fileWatcher;
    }

    private Image loadIconImage() {
        URL iconURL = getClass().getClassLoader().getResource("mago_hacktom.png");
        if (iconURL == null) {
            System.err.println("No se encontró el archivo de icono 'mago_hacktom.png' en la carpeta 'resources'.");
            return null;
        }
        return new ImageIcon(iconURL).getImage();
    }

    public void setupTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = loadIconImage(); // Cambia esta línea
            PopupMenu popup = new PopupMenu();

            MenuItem recordItem = new MenuItem("Grabar");
            recordItem.addActionListener(e -> audioRecorder.startRecording(fileWatcher.mp3Path.toString()));
            popup.add(recordItem);

            MenuItem showFilesItem = new MenuItem("Mostrar archivos");
            showFilesItem.addActionListener(e -> audioRecorder.openFolder(fileWatcher.savePath.toString()));
            popup.add(showFilesItem);

            MenuItem exitItem = new MenuItem("Salir");
            exitItem.addActionListener(e -> System.exit(0));
            popup.add(exitItem);

            TrayIcon trayIcon = new TrayIcon(image, "Hacktom Assistant", popup);
            trayIcon.setImageAutoSize(true);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("No se pudo agregar el icono a la bandeja del sistema.");
                e.printStackTrace();
            }
        } else {
            System.err.println("La bandeja del sistema no es compatible en esta plataforma.");
        }
    }
}
