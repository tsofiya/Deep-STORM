package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilePathButton extends JButton implements ActionListener{

    private JFileChooser chooser= new JFileChooser();
    public String path;

    public FilePathButton(String text){
        super(text);
        this.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            path=  chooser.getCurrentDirectory().getAbsolutePath();
        }
    }
}
