/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.grapheditor;

import hu.nyme.inga.tipeness.simulation.ConfigParser;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Andrew
 */
public class StabilityAnalysisCardPanel extends AbstractCardPanel {

    private JLabel infoLabel;
    private JLabel batchLengthLabel;
    private JTextField batchLengthInput;

    public StabilityAnalysisCardPanel(ConfigParser configParser) {
        super(configParser);
        init();
    }

    private void init() {
        GridBagConstraints c = new GridBagConstraints();
        this.setBorder(new EmptyBorder(5, 50, 5, 50));
        this.add(initProperties());
    }

    @Override
    public void refreshProperties(ConfigParser configParser) {
        super.refreshProperties(configParser);
        this.batchLengthInput.setText(configParser.getBatch() + "");
    }

    public int getBatchLengthInput() {
        checkValue();
        return Integer.parseInt(this.batchLengthInput.getText());
    }

    @Override
    public JPanel initProperties() {
        infoLabel = new JLabel("The stability analysis simulation method watches all of the places during the simulation run.",
                SwingConstants.CENTER);
        this.batchLengthLabel = new JLabel("Batch length:");
        this.batchLengthInput = new JTextField(super.getConfigParser().getBatch() + "", 10);
        JPanel stabilityPropertiesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        c.weighty = 1;
        c.gridwidth = 2;
        c.gridy = 0;
        c.insets = new Insets(10, 20, 20, 20);
        stabilityPropertiesPanel.add(infoLabel, c);
        JPanel batchLengthPanel = new JPanel(new FlowLayout());
        batchLengthPanel.add(batchLengthLabel);
        batchLengthPanel.add(batchLengthInput);

        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 0;
        stabilityPropertiesPanel.add(batchLengthPanel, c);
        FocusListener propertyFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (fe.getSource() == batchLengthInput) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            batchLengthInput.selectAll();
                        }
                    });
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (fe.getSource() == batchLengthInput) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            batchLengthInput.selectAll();
                        }
                    });
                }
                checkValue();
            }
        };

        this.batchLengthInput.addFocusListener(propertyFocusListener);

        return stabilityPropertiesPanel;
    }

    private void checkValue() {
        try {
            int batchLength = Integer.parseInt(batchLengthInput.getText());
            if (batchLength < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
            batchLengthInput.setText(super.getConfigParser().getBatch() + "");
            batchLengthInput.requestFocus();
        }
    }
}
