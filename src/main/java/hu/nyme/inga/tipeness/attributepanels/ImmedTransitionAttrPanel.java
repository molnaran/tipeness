/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import hu.nyme.inga.tipeness.simulation.ShowError;
import java.util.HashMap;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import org.mvel2.MVEL;

/**
 *
 * @author Andrew
 */
public class ImmedTransitionAttrPanel extends TransitionAttrPanel {

    
    public ImmedTransitionAttrPanel(ImmediateTransition immediateTransition, ConfigParser configParser) {
        super(immediateTransition, configParser);
    }

    @Override
    public void initTable() {
        super.initTable();
        TableModel tableModel = this.getAttrTable().getModel();
        ImmediateTransition currentTransition = (ImmediateTransition) super.getCurrentTransition();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;            
            transitionTableModel.addRow(new Object[]{PRIORITYATTR, currentTransition.getPriority()});
            transitionTableModel.addRow(new Object[]{WEIGHTTRANSITIONATTR, currentTransition.getWeight()});
            transitionTableModel.addRow(new Object[]{ENABLINGFUNCTIONATTR, currentTransition.getConditionString()});
        }
    }

    @Override
    public void tableChanged(TableModelEvent event) {        
        super.tableChanged(event);
        if (event.getType() == TableModelEvent.UPDATE) {
            int row = event.getFirstRow();
            int column = event.getColumn();
            ImmediateTransition currentTransition = null;
            if (getCurrentTransition() instanceof ImmediateTransition) {
                currentTransition = (ImmediateTransition) this.getCurrentTransition();
            }
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column - 1) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case PRIORITYATTR:    
                            try{
                                double priority=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (priority<0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setPriority(priority);
                            }catch (NumberFormatException ne){
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getPriority()), row, column);
                            }
                            break;
                        case WEIGHTTRANSITIONATTR:                            
                            try{
                                double weight=Double.parseDouble((String)super.getAttrTable().getValueAt(row, column));
                                if (weight<0){
                                    throw new NumberFormatException();
                                }
                                currentTransition.setWeight(weight);
                            }catch(NumberFormatException ne){
                                super.getAttrTable().setValueAt(Double.toString(currentTransition.getWeight()), row, column);
                            }
                            break;
                        case ENABLINGFUNCTIONATTR:                            
                            try{
                                String tempConString=(String)super.getAttrTable().getValueAt(row, column);
                                tempConString=tempConString.toLowerCase();
                                String originalString=tempConString;
                                if (tempConString.equals("")){
                                } else {
                                    String placePatern = "#";
                                    String placeName;
                                    while (tempConString.contains("#")) {
                                        placeName = tempConString.substring(tempConString.indexOf("#") + 1);
                                        placeName = placeName.substring(0, placeName.indexOf(placePatern));
                                        Place foundPlace= null;
                                        for (Place place: configParser.getPlaces()){
                                            if (place.getName().equals(placeName)){
                                                foundPlace=place;
                                            }
                                        }                                        
                                        if (foundPlace!=null) {
                                            tempConString = tempConString.replaceAll("#" + placeName + "#", Integer.toString(foundPlace.getCurrent()));
                                        } else {
                                            throw new Exception();
                                        }
                                    }
                                    boolean asd= (boolean) MVEL.eval(tempConString);
                                    currentTransition.setConditionString(originalString);
                                }
                            }catch (Exception ee) {
                                super.getAttrTable().setValueAt("", row, column);
                            }
                            break;
                    }
                }
            }
        }

    }
}
