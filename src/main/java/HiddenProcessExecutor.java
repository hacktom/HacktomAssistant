import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class HiddenProcessExecutor {

    public static void executeCommand(String command) {
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        WinBase.PROCESS_INFORMATION processInformation = new WinBase.PROCESS_INFORMATION();

        // Evita que se muestre la ventana
        startupInfo.dwFlags = WinBase.STARTF_USESHOWWINDOW;
        startupInfo.wShowWindow = new WinDef.WORD(WinUser.SW_HIDE);

        boolean success = Kernel32.INSTANCE.CreateProcess(
                null,
                command,
                null,
                null,
                true,
                new WinDef.DWORD(0),
                null,
                null,
                startupInfo,
                processInformation
        );

        if (success) {
            Kernel32.INSTANCE.WaitForSingleObject(processInformation.hProcess, Kernel32.INFINITE);
            Kernel32.INSTANCE.CloseHandle(processInformation.hProcess);
            Kernel32.INSTANCE.CloseHandle(processInformation.hThread);
        } else {
            int lastError = Native.getLastError();
            System.err.println("Error al ejecutar el comando (Código de error: " + lastError + ")");
        }
    }
}
