/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Andrew
 */
public class CommonSimulationPropertiesPanel extends JPanel {

    private ConfigParser configParser;
    private JFileChooser chooser;

    private JLabel minSampleLabel;
    private JTextField minSampleInput;
    private JLabel maxRelErrorLabel;
    private JTextField maxRelErrorInput;
    private JLabel confidenceLevelLabel;
    private JTextField confidenceLevelInput;
    private JLabel warmupLengthLabel;
    private JTextField warmupLengthInput;
    private JLabel resultFileLabel;
    private JTextField resultFileInput;
    private JButton resultFileBtn;

    public CommonSimulationPropertiesPanel(ConfigParser configParser) {
        super();
        this.configParser = configParser;
        init();
    }

    private void init() {
        minSampleLabel = new JLabel("Minimal sample size: ");
        minSampleInput = new JTextField(configParser.getMinSampleSize() + "", 5);
        maxRelErrorLabel = new JLabel("Maximal relative error: ");
        maxRelErrorInput = new JTextField(configParser.getMaxRelError() + "", 5);
        confidenceLevelLabel = new JLabel("Confidence level: ");
        confidenceLevelInput = new JTextField(1 - configParser.getAlpha() + "", 5);
        resultFileLabel = new JLabel("Result file location: ");
        resultFileInput = new JTextField(configParser.getOutFileNamePath(), 15);
        resultFileBtn= new JButton("...");
        warmupLengthLabel = new JLabel("Warmup length: ");
        warmupLengthInput = new JTextField(configParser.getWarmupLength() + "", 5);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;

        c.gridx = 0;
        c.gridy = 0;
        this.add(minSampleLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        this.add(minSampleInput, c);

        c.gridx = 0;
        c.gridy = 1;
        this.add(maxRelErrorLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        this.add(maxRelErrorInput, c);

        c.gridx = 0;
        c.gridy = 2;
        this.add(confidenceLevelLabel, c);

        c.gridx = 1;
        c.gridy = 2;
        this.add(confidenceLevelInput, c);

        c.gridx = 2;
        c.gridy = 0;
        this.add(warmupLengthLabel, c);
        c.gridx = 3;
        this.add(warmupLengthInput, c);
        c.gridx = 2;
        c.gridy = 1;
        this.add(resultFileLabel, c);
        c.gridx = 3;
        this.add(resultFileInput, c);
        
        JPanel filechooserPanel= new JPanel(new FlowLayout());
        filechooserPanel.add(resultFileInput);
        filechooserPanel.add(resultFileBtn);
        this.add(filechooserPanel, c);
        resultFileInput.setEditable(false);
        
        ActionListener filechooserListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource()==resultFileBtn){
                    resultFileInput.setText(askOutputFilePath());                    
                }                
            }
        };
        resultFileBtn.addActionListener(filechooserListener);
        
        this.setPreferredSize(new Dimension(100, 100));
        this.setMinimumSize(new Dimension(200, 200));

        FocusListener propertyFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (fe.getSource() instanceof JTextField) {
                            JTextField textField = (JTextField) fe.getSource();

                            textField.selectAll();

                        }
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (fe.getSource() instanceof JTextField) {
                            JTextField textField = (JTextField) fe.getSource();

                            textField.selectAll();
                        }
                    }
                });
                if (fe.getSource() == minSampleInput) {
                    if (!checkMinSampleInput()) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (fe.getSource() == maxRelErrorInput) {
                    if (!checkMaxRelErrorInput()) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (fe.getSource() == confidenceLevelInput) {
                    if (!checkConfidenceLevelInput()) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (fe.getSource() == warmupLengthInput) {
                    if (!checkWarmupLengthInput()) {
                        JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                //checkValue();
            }
        };
        minSampleInput.addFocusListener(propertyFocusListener);
        maxRelErrorInput.addFocusListener(propertyFocusListener);
        confidenceLevelInput.addFocusListener(propertyFocusListener);
        warmupLengthInput.addFocusListener(propertyFocusListener);
    }

    private boolean checkMinSampleInput() {
        try {
            Integer.parseInt(minSampleInput.getText());
            return true;
        } catch (NumberFormatException nfe) {
            minSampleInput.setText(configParser.getMinSampleSize() + "");
            minSampleInput.requestFocus();
            return false;
        }
    }

    private boolean checkMaxRelErrorInput() {
        try {
            double maxRelError = Double.parseDouble(maxRelErrorInput.getText());
            if (maxRelError < 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException nfe) {
            maxRelErrorInput.setText(configParser.getMaxRelError() + "");
            maxRelErrorInput.requestFocus();
            return false;
        }
    }

    private boolean checkConfidenceLevelInput() {
        try {
            double confidenceLevel = Double.parseDouble(confidenceLevelInput.getText());
            if (confidenceLevel < 0 || confidenceLevel > 1) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException nfe) {
            confidenceLevelInput.setText(1 - configParser.getAlpha() + "");
            confidenceLevelInput.requestFocus();
            return false;
        }
    }

    private boolean checkWarmupLengthInput() {
        try {
            double warmupLength = Double.parseDouble(warmupLengthInput.getText());
            if (warmupLength < 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException nfe) {
            warmupLengthInput.setText(configParser.getWarmupLength() + "");
            warmupLengthInput.requestFocus();
            return false;
        }
    }

    public double getConfidenceLevelInput() {
        checkConfidenceLevelInput();
        return Double.parseDouble(confidenceLevelInput.getText());
    }

    public double getMaxRelErrorInputInput() {
        checkMaxRelErrorInput();
        return Double.parseDouble(maxRelErrorInput.getText());
    }

    public double getWarmupLengthInput() {
        checkWarmupLengthInput();
        return Double.parseDouble(warmupLengthInput.getText());
    }

    public int getMinSampleSizeInput() {
        checkMinSampleInput();
        return Integer.parseInt(minSampleInput.getText());
    }

    public boolean checkCommonValues() {
        if (!checkConfidenceLevelInput()) {
            return false;
        }
        if (!checkMaxRelErrorInput()) {
            return false;
        }
        if (!checkMinSampleInput()) {
            return false;
        }
        if (!checkWarmupLengthInput()) {
            return false;
        }
        return true;
    }

    public void refreshProperties(ConfigParser freshConfigParser) {    
        this.configParser=freshConfigParser;
        minSampleInput.setText(configParser.getMinSampleSize() + "");
        maxRelErrorInput.setText(configParser.getMaxRelError() + "");
        confidenceLevelInput.setText(1 - configParser.getAlpha() + "");
        warmupLengthInput.setText(configParser.getWarmupLength() + "");
        resultFileInput.setText(configParser.getOutFileNamePath());
    }

    private String askOutputFilePath() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Set resultfile location");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        chooser.setFileFilter(filter);
        
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            if (!chooser.getSelectedFile().getAbsolutePath().endsWith(".txt")) {
                saveFile = new File(chooser.getSelectedFile() + ".txt");
            }            
            return saveFile.getAbsolutePath();
        } else {
            return configParser.getOutFileNamePath();
        }
    }
    
    public String getOutputFilePath(){        
        if (!resultFileInput.getText().equals("")){
            return resultFileInput.getText();
        }else{         
            return null;
        }
        
    }
}
