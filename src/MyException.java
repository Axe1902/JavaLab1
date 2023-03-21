import javax.swing.*;
import java.awt.*;

public class MyException extends Exception {
    private String message;
    private String title;

    public MyException(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public void ShowMessage(Component parent)
    {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
