/**
 * Created by Ahmed on 10/26/2016.
 */

import javax.swing.*;

import asciiPanel.AsciiPanel;

public class ApplicationMain extends JFrame
{
    private AsciiPanel terminal;

    public ApplicationMain()
    {
        super();
        terminal = new AsciiPanel();
        add(terminal);
        pack();
    }

    public static void main(String[] args)
    {
        ApplicationMain app = new ApplicationMain();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);

        app.terminal.write("rl tutorial", 0, 0);
    }
}
