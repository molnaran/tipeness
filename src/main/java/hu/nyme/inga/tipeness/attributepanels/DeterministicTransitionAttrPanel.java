/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.DeterministicTransition;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import java.util.HashMap;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author Alkohol
 */
public class DeterministicTransitionAttrPanel extends TransitionAttrPanel{    
    
    public DeterministicTransitionAttrPanel(DeterministicTransition deterministicTransition, ConfigParser configParser) {
        super(deterministicTransition, configParser);
        initTable();
    }

    @Override
    public void initTable() {
        super.initTable();

        TableModel tableModel = this.getAttrTable().getModel();
        DeterministicTransition currentTransition = (DeterministicTransition) super.getCurrentTransition();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;            
            transitionTableModel.addRow(new Object[]{DELAYATTR, currentTransition.getDelay()});
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {        
        super.tableChanged(e);
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            DeterministicTransition currentTransition = null;
            if (getCurrentTransition() instanceof DeterministicTransition) {
                currentTransition = (DeterministicTransition) this.getCurrentTransition();
            }
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column - 1) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case DELAYATTR:                            
                            try{
                                double delay=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (delay<=0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setConstantGen(delay);
                            }catch(NumberFormatException ne){
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getDelay()), row, column);
                            }
                            break;                        
                    }
                }
            }
        }

    }
}
