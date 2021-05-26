import java.io.*;
import java.time.*;

public class DLException extends Exception {
    private Exception exception;
    private String message;

    public DLException(Exception type) {
        this.exception = type;
    }

    public DLException(Exception type, String message) {
        super(message);
        this.exception = type;
        this.message = message;
        //writeLog();
    }

    public void writeLog() {
        try {
            FileWriter file = new FileWriter("exceptions.log", true);
            file.write("Exception thrown at: " +LocalDateTime.now().toString()+"\n");
            file.write("Exception Message: " + this.exception.getMessage()+"\n");
            file.write("Exception Cause: " +this.exception.getCause()+"\n");
            //file.write("Custom Message: " +getMessage()+"\n\n");
            file.close();
        }
        catch (IOException a) {
        }
    }
}
