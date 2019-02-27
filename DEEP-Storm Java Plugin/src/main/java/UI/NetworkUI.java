package UI;

import javax.swing.*;
import java.awt.*;

public class NetworkUI extends JFrame {

    private JPanel mainPanel= new JPanel(new GridLayout(3, 1));
    private JPanel csvPanel = new JPanel();
    private JPanel modelPanel = new JPanel();
    private JTextField csvPathTextField= new JTextField(20);
    private FilePathButton csvChoosingButton= new FilePathButton("Import csv file");
    private JTextField modelPathTextField= new JTextField(20);
    private FilePathButton modelChoosingButton= new FilePathButton("Import model");
    private  JFileChooser csvChooser = new JFileChooser();
    private  JFileChooser modelChooser = new JFileChooser();
    private  Button runMyNetButton= new Button();
    private JTextField csvTextBox;


    public NetworkUI(){
        super("Set your net");

        setSize(400, 500);


        //Enable text editing by user.
        csvPathTextField.setEditable(false);
        csvPathTextField.setBackground(Color.WHITE);
        modelPathTextField.setEditable(false);
        modelPathTextField.setBackground(Color.WHITE);

        csvPanel.add(csvPathTextField);
        csvPanel.add(csvChoosingButton);
        modelPanel.add(modelPathTextField);
        modelPanel.add(modelChoosingButton);

        mainPanel.add(csvPanel);
        mainPanel.add(modelPanel);

        add(mainPanel);
        setVisible(true);

    }

    public static void main(String[]args){
        NetworkUI ui= new NetworkUI();

    }
}
