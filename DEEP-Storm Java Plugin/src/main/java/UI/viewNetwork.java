package UI;

import com.opencsv.bean.CsvToBeanFilter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class viewNetwork extends JFrame {
    private JTextField textFieldCsvPath;
    private JButton buttonImportCsv;
    private JTextField textFieldModelPath;
    private JButton buttonImortModel;
    private JButton runMyNetButton;
    private JPanel rootPanel;

    public viewNetwork() {
        setTitle("Net Settings");
        setSize(400, 500);
        add(rootPanel);


        buttonImportCsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    if (chooser.getSelectedFile().toString().endsWith(".csv"))
                        textFieldCsvPath.setText(chooser.getSelectedFile().toString());
                    else
                        JOptionPane.showMessageDialog(null, "Please choose csv file", "warning",JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });

        buttonImortModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    textFieldModelPath.setText(chooser.getSelectedFile().toString());
                }
            }
        });

    }

    public String getCsvPath(){
        return textFieldCsvPath.getText();
    }

    public String getModelPath(){
        return textFieldModelPath.getText();
    }


    public static void main(String[] args) {
        viewNetwork view = new viewNetwork();
    }
}
