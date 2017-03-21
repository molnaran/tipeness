/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.TruncNormalTransition;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author Alkohol
 */
public class NormalTransitionAttrPanel extends TransitionAttrPanel{    
    
    public NormalTransitionAttrPanel(TruncNormalTransition normalTransition, ConfigParser configParser) {
        super(normalTransition, configParser);
        initTable();
    }

    @Override
    public void initTable() {
        super.initTable();

        TableModel tableModel = this.getAttrTable().getModel();
        TruncNormalTransition currentTransition = (TruncNormalTransition) super.getCurrentTransition();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;            
            transitionTableModel.addRow(new Object[]{MEANATTR, currentTransition.getMean()});
            transitionTableModel.addRow(new Object[]{VARIANCEATTR, currentTransition.getVariance()});
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {        
        super.tableChanged(e);
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TruncNormalTransition currentTransition = null;
            if (getCurrentTransition() instanceof TruncNormalTransition) {
                currentTransition = (TruncNormalTransition) this.getCurrentTransition();
            }
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column - 1) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case MEANATTR:                            
                            try{
                                double mean=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (mean<0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setNormalGen(mean, currentTransition.getVariance());
                            }catch(NumberFormatException ne){                                
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getMean()), row, column);
                            }
                            break;   
                        case VARIANCEATTR:                            
                            try{
                                double variance=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (variance<0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setNormalGen(currentTransition.getMean(), variance);
                            }catch(NumberFormatException ne){                                
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getVariance()), row, column);
                            }
                            break;     
                    }
                }
            }
        }

    }
}
