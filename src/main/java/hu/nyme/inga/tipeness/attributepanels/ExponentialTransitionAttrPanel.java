/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import static hu.nyme.inga.tipeness.attributepanels.PetriNetNodeAttrConstants.DELAYATTR;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.DeterministicTransition;
import hu.nyme.inga.tipeness.petrinetelements.ExponentialTransition;
import hu.nyme.inga.tipeness.petrinetelements.MemoryPolicyAtTransition;
import hu.nyme.inga.tipeness.petrinetelements.Transition;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author Alkohol
 */
public class ExponentialTransitionAttrPanel extends TransitionAttrPanel {

    JComboBox<ExponentialTransition.ServerType> serverTypeComboBox;

    public ExponentialTransitionAttrPanel(ExponentialTransition transition, ConfigParser configParser) {
        super(transition, configParser);
        this.serverTypeComboBox = new JComboBox<>();
        initServerTypeComboBox();
        serverTypeComboBox.setSelectedItem(transition.getsType());
        ItemListener serverType = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Object item = e.getItem();
                    if (item instanceof ExponentialTransition.ServerType) {
                        ExponentialTransition.ServerType serverType = (ExponentialTransition.ServerType) serverTypeComboBox.getSelectedItem();
                        if (serverType != null) {
                            ExponentialTransition exponentialTransition = (ExponentialTransition) currentTransition;
                            ExponentialTransition.ServerType serverTypeOption = (ExponentialTransition.ServerType) serverTypeComboBox.getSelectedItem();
                            exponentialTransition.setsType(serverTypeOption);
                        }
                    }
                }
            }        
        };
        serverTypeComboBox.addItemListener(serverType);
        super.editMemoryPolicyPanel.add(serverTypeComboBox);
        //this.add(serverTypeComboBox);
    }

    @Override
    public void initTable() {
        super.initTable();

        TableModel tableModel = this.getAttrTable().getModel();
        ExponentialTransition currentTransition = (ExponentialTransition) super.getCurrentTransition();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;
            transitionTableModel.addRow(new Object[]{DELAYATTR, currentTransition.getDelay()});
        }
    }

    protected void initServerTypeComboBox() {
        DefaultComboBoxModel defaultModel = (DefaultComboBoxModel) this.serverTypeComboBox.getModel();
        defaultModel.removeAllElements();
        defaultModel.addElement(ExponentialTransition.ServerType.exclusive);
        defaultModel.addElement(ExponentialTransition.ServerType.infinite);
        this.serverTypeComboBox.setModel(defaultModel);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            ExponentialTransition currentTransition = null;
            if (getCurrentTransition() instanceof ExponentialTransition) {
                currentTransition = (ExponentialTransition) this.getCurrentTransition();
            }
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column - 1) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case DELAYATTR:
                            try {
                                double delay = Double.parseDouble((String) super.getAttrTable().getValueAt(row, column));
                                if (delay <= 0) {
                                    throw new NumberFormatException();
                                }
                                currentTransition.setExpGen(delay);
                            } catch (NumberFormatException ne) {
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getDelay()), row, column);
                            }
                            break;
                    }
                }
            }
        }
    }

}
