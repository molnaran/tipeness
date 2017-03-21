/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Andrew
 */
public class PlaceAttrPanel extends AbstractAttrPanel implements TableModelListener, PetriNetNodeAttrConstants {

    Place currentPlace;
    ConfigParser configParser;
    
    public PlaceAttrPanel(Place place, ConfigParser configParser) {
        super();
        this.currentPlace = place;
        this.configParser=configParser;
        initTable();
    }

    @Override
    public void initTable() {
        super.initTable();
        if (super.getAttrTable().getModel() instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) super.getAttrTable().getModel();
            transitionTableModel.addRow(new Object[]{NAMEATTR, currentPlace.getName()});
            transitionTableModel.addRow(new Object[]{INITTOKENNUMATTR, currentPlace.getCurrent()});
        }
        super.getAttrTable().getModel().addTableModelListener(this);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (e.getType() == TableModelEvent.UPDATE) {            
            if (currentPlace != null) {
                if (super.getAttrTable().getValueAt(row, column) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case NAMEATTR:                  
                            String inputString=(String) super.getAttrTable().getValueAt(row, column);
                            if (configParser.isViablePlaceName(inputString) || currentPlace.getName().equals(inputString)){
                                configParser.updatePlaceNameInWatchLists(inputString, currentPlace.getName());
                                currentPlace.setName(inputString);
                            }else{
                                super.getAttrTable().setValueAt(currentPlace.getName(), row, column);
                            }                            
                            break;
                        case INITTOKENNUMATTR:
                        try {
                            int initialTokenNum = Integer.parseInt((String) super.getAttrTable().getValueAt(row, column));
                            if (initialTokenNum < 0) {
                                throw new NumberFormatException();
                            }
                            currentPlace.setTokenNumber(initialTokenNum);
                        } catch (NumberFormatException ne) {
                            super.getAttrTable().setValueAt(Integer.toString(currentPlace.getCurrent()), row, column);
                        }
                        break;
                    }
                }
            }            
        }        
    }
    
}
