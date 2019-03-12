package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class viewNetwork extends JFrame {
    private JTextField textFieldCsvPath;
    private JButton buttonImportCsv;
    private JTextField textFieldJasonPath;
    private JButton buttonImortJason;
    public JButton runMyNetButton;
    private JPanel rootPanel;
    private JTextField textFieldLoadWeights;
    private JButton buttonImportWeight;

    public viewNetwork() {
        setTitle("Net Settings");
        setSize(300, 300);
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

        buttonImortJason.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    textFieldJasonPath.setText(chooser.getSelectedFile().toString());
                }
            }
        });

        buttonImportWeight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    textFieldLoadWeights.setText(chooser.getSelectedFile().toString());
                }
            }
        });

    }

    public String getCsvPath(){
        return textFieldCsvPath.getText();
    }

    public String getJSonPath(){ return textFieldJasonPath.getText(); }

    public String getWeightsPath(){return textFieldLoadWeights.getText();}


    public static void main(String[] args) {
        viewNetwork view = new viewNetwork();
    }
}
