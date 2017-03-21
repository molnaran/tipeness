/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.AbstractEdge;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author Andrew
 */
public class EdgeAttrPanel extends AbstractAttrPanel implements TableModelListener, PetriNetNodeAttrConstants {

    AbstractEdge currentEdge;

    public EdgeAttrPanel(AbstractEdge abstractEdge) {
        super();
        this.currentEdge = abstractEdge;
        initTable();
    }

    @Override
    public void initTable() {
        super.initTable();
        TableModel tableModel = this.getAttrTable().getModel();
        if (tableModel instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) tableModel;
            transitionTableModel.addRow(new Object[]{EDGEWEIGHTATTR, currentEdge.getArcWeight()});
        }
        super.getAttrTable().getModel().addTableModelListener(this);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (currentEdge != null) {
                for (int i = 0; i < super.getAttrTable().getRowCount(); i++) {
                    switch ((String) super.getAttrTable().getValueAt(i, 0)) {
                        case EDGEWEIGHTATTR:
                            try {
                                int arcWeight = Integer.parseInt((String) super.getAttrTable().getValueAt(row, column));
                                if (arcWeight < 0) {
                                    throw new NumberFormatException();
                                }
                                currentEdge.setArcWeight(arcWeight);
                            } catch (NumberFormatException ne) {
                                super.getAttrTable().setValueAt(Integer.toString(currentEdge.getArcWeight()), row, column);
                            }
                            break;
                    }
                }
            }
        }

    }

}
