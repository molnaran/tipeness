/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
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
public class BatchCardPanel extends AbstractListModelCardPanel{
    private JLabel batchLengthLabel;
    private JTextField batchLengthInput;
    
    public BatchCardPanel(ListPanel placeListPanel, HashMap<String, PlaceListModel> listmodels, ConfigParser configParser) {        
        super(placeListPanel, listmodels, configParser);        
    }    

    @Override
    public JPanel initProperties() {
        this.batchLengthLabel= new JLabel("Batch length:");
        this.batchLengthInput= new JTextField(super.getConfigParser().getBatch()+"",10);
        JPanel batchmeanPropertiesPanel= new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx=20;
        batchmeanPropertiesPanel.add(batchLengthLabel, c);
        c.gridx = 1;
        batchmeanPropertiesPanel.add(batchLengthInput, c);
        
        FocusListener propertyFocusListener=new FocusListener() {
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
        return batchmeanPropertiesPanel;
    }

    @Override
    public void refreshProperties(ConfigParser configParser) {
        super.refreshProperties(configParser);
        this.batchLengthInput.setText(configParser.getBatch()+"");
    }
    
    public int getBatchLengthInput(){
        checkValue();
        return Integer.parseInt(this.batchLengthInput.getText());
    }
    
    private void checkValue() {
        try {
            int batchLength=Integer.parseInt(batchLengthInput.getText());
            if (batchLength<0){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid input!", "Error!", JOptionPane.ERROR_MESSAGE);
            batchLengthInput.setText(super.getConfigParser().getBatch() + "");
            batchLengthInput.requestFocus();
        }
    }
}
