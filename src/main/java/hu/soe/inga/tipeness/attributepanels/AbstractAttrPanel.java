/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.attributepanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Alkohol
 */
public abstract class AbstractAttrPanel extends JPanel implements PetriNetNodeAttrConstants {

    private JTable attrTable;
    private JPanel tablePanel;

    JLabel generalPropertyLabel;

    public AbstractAttrPanel() {
        this.setLayout(new BorderLayout(20, 20));
        this.attrTable = new JTable();
        GridLayout editMemoryLayout = new GridLayout(2, 1);
        editMemoryLayout.setVgap(20);
        this.tablePanel = new JPanel(editMemoryLayout);
        this.generalPropertyLabel = new JLabel("Attributes: ");
        generalPropertyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.tablePanel.add(generalPropertyLabel);
        this.tablePanel.add(attrTable);
        this.add(tablePanel, BorderLayout.NORTH);
        this.setVisible(true);
    }

    public void initTable() {
        String[] columnNames = {"Attribute", "Value"};
        MatteBorder border = new MatteBorder(1, 1, 1, 1, Color.BLACK);
        attrTable.setBorder(border);
        attrTable.setModel(new BasicTableModel(new Object[0][0], columnNames));
        attrTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        attrTable.getColumnModel().getColumn(0).setPreferredWidth(130);

    }

    public JTable getAttrTable() {
        return attrTable;
    }

}
