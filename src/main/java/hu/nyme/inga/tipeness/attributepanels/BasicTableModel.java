/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Andrew
 */
public class BasicTableModel extends DefaultTableModel implements PetriNetNodeAttrConstants {
    
    
    public BasicTableModel(Object[][] data, Object[] columnames){
        super(data, columnames);
    }
    @Override
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col < 1) {
        return false;
      } else {
        return true;
      }
    }
    
}
