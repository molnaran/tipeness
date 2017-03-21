/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.grapheditor;

import hu.nyme.inga.tipeness.simulation.ConfigParser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Alkohol
 */
public class RepDelCardPanel extends AbstractListModelCardPanel {

    JLabel terminatingTimeLabel;
    JTextField terminatingTimeInput;

    public RepDelCardPanel(ListPanel placeListPanel, HashMap<String, PlaceListModel> listmodels, ConfigParser configParser) {
        super(placeListPanel, listmodels, configParser);
    }

    @Override
    public JPanel initProperties() {
        terminatingTimeLabel = new JLabel("Terminating time");
        terminatingTimeInput = new JTextField(super.getConfigParser().getTerminatingTime() + "", 10);
        JPanel repDelPropertiesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 20;
        repDelPropertiesPanel.add(terminatingTimeLabel, c);
        c.gridx = 1;
        repDelPropertiesPanel.add(terminatingTimeInput, c);

        FocusListener propertyFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (fe.getSource() == terminatingTimeInput) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            terminatingTimeInput.selectAll();
                        }
                    });
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (fe.getSource() == terminatingTimeInput) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            terminatingTimeInput.selectAll();
                        }
                    });
                }
                checkValue();
            }
        };
        terminatingTimeInput.addFocusListener(propertyFocusListener);

        return repDelPropertiesPanel;
    }

    @Override
    public void refreshProperties(ConfigParser configParser) {
        super.refreshProperties(configParser);
        terminatingTimeInput.setText(configParser.getTerminatingTime() + "");
    }

    public double getTerminatingTimeInput() {
        checkValue();
        return Double.parseDouble(this.terminatingTimeInput.getText());
    }

    private void checkValue() {
        try {
            Double.parseDouble(terminatingTimeInput.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
            terminatingTimeInput.setText(super.getConfigParser().getTerminatingTime() + "");
            terminatingTimeInput.requestFocus();
        }
    }
}
