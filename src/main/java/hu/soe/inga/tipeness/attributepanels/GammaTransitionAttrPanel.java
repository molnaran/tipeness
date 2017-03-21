/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.attributepanels;

import hu.soe.inga.tipeness.petrinetelements.GammaTransition;
import hu.soe.inga.tipeness.simulation.ConfigParser;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 *
 * @author Alkohol
 */
public class GammaTransitionAttrPanel extends TransitionAttrPanel{    
    
    public GammaTransitionAttrPanel(GammaTransition gammaTransition, ConfigParser configParser) {
        super(gammaTransition, configParser);
        initTable();
    }

    @Override
    public void initTable() {
        super.initTable();

        TableModel tableModel = this.getAttrTable().getModel();
        GammaTransition currentTransition = (GammaTransition) super.getCurrentTransition();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;            
            transitionTableModel.addRow(new Object[]{GAMMASHAPEATTR, currentTransition.getShape()});
            transitionTableModel.addRow(new Object[]{GAMMARATEATTR, currentTransition.getRate()});
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {        
        super.tableChanged(e);
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            GammaTransition currentTransition = null;
            if (getCurrentTransition() instanceof GammaTransition) {
                currentTransition = (GammaTransition) this.getCurrentTransition();
            }
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column - 1) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case GAMMASHAPEATTR:                            
                            try{
                                double shape=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (shape<=0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setGammaGen(shape, currentTransition.getRate());
                            }catch(NumberFormatException ne){                                
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getShape()), row, column);
                            }
                            break;   
                        case GAMMARATEATTR:                            
                            try{
                                double rate=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (rate<=0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setGammaGen(currentTransition.getShape(), rate);
                            }catch(NumberFormatException ne){                                
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getRate()), row, column);
                            }
                            break;     
                    }
                }
            }
        }

    }
}
