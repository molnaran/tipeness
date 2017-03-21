/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.attributepanels;

import hu.soe.inga.tipeness.petrinetelements.MemoryPolicyAtTransition;
import static hu.soe.inga.tipeness.attributepanels.PetriNetNodeAttrConstants.NAMEATTR;
import hu.soe.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.soe.inga.tipeness.petrinetelements.Transition;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import hu.soe.inga.tipeness.petrinetelements.Transition.MemoryPolicy;
import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Andrew
 */
public class TransitionAttrPanel extends AbstractAttrPanel implements ActionListener, ItemListener, ListSelectionListener, TableModelListener, PetriNetNodeAttrConstants {

    JPanel editMemoryPolicyPanel;    
    Transition currentTransition;
    
    JList memoryPolicyList;
    JLabel memoryPolicyPropertyLabel;
    
    MemoryPolicyEditDialog memoryDialog;
    JButton editButton;

    JComboBox<String> transitionComboBox;
    JComboBox<Transition.MemoryPolicy> memoryPolicyComboBox;
    ConfigParser configParser;
            
    
    public TransitionAttrPanel(Transition transition, ConfigParser configParser) {  
        super();
        GridLayout editMemoryLayout= new GridLayout(3, 1);
        editMemoryLayout.setVgap(20);
        this.editMemoryPolicyPanel = new JPanel(editMemoryLayout);
        this.configParser=configParser;
        this.currentTransition = transition;
        this.memoryPolicyPropertyLabel= new JLabel("Memory policy: ");
        
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        this.memoryPolicyList = new JList<>(fillMemoryListModel());

        ListDataListener listDataListener = new ListDataListener() {
            @Override
            public void contentsChanged(ListDataEvent listDataEvent) {                
            }

            @Override
            public void intervalAdded(ListDataEvent listDataEvent) {
                HashMap<BasicTimedTransition, MemoryPolicyAtTransition> newMemory= new HashMap<>();
                DefaultListModel<MemoryPolicyAtTransition> defaultListModel=(DefaultListModel)memoryPolicyList.getModel();
                for (int i = 0; i < defaultListModel.getSize(); i++) {
                    MemoryPolicyAtTransition memoryPolicyAtTransition=defaultListModel.elementAt(i);
                    newMemory.put(memoryPolicyAtTransition.getTransition(),memoryPolicyAtTransition);
                }
                transition.replaceMemoryPolicyHashMap(newMemory);
            }

            @Override
            public void intervalRemoved(ListDataEvent listDataEvent) {
                HashMap<BasicTimedTransition, MemoryPolicyAtTransition> newMemory= new HashMap<>();
                DefaultListModel<MemoryPolicyAtTransition> defaultListModel=(DefaultListModel)memoryPolicyList.getModel();
                for (int i = 0; i < defaultListModel.getSize(); i++) {
                    MemoryPolicyAtTransition memoryPolicyAtTransition=defaultListModel.elementAt(i);
                    newMemory.put(memoryPolicyAtTransition.getTransition(),memoryPolicyAtTransition);
                }
                transition.replaceMemoryPolicyHashMap(newMemory);
            }
        };

        this.memoryPolicyList.getModel().addListDataListener(listDataListener);
        this.memoryDialog = new MemoryPolicyEditDialog(topFrame, configParser.getMemoryTransitions(), (DefaultListModel<MemoryPolicyAtTransition>) memoryPolicyList.getModel(), "Edit");

        this.editButton = new JButton("Edit");

        this.memoryPolicyComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        this.memoryPolicyComboBox.setVisible(false);
        //this.updateMemoryList();
        this.transitionComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        
        initTable();
        initMemoryPolicyOptionComboBox();               
        
        this.editMemoryPolicyPanel.add(memoryPolicyComboBox);
        this.editMemoryPolicyPanel.add(editButton);

        JScrollPane scroll = new JScrollPane(memoryPolicyList);
        scroll.setPreferredSize(new Dimension(80, 200));
        this.add(scroll, BorderLayout.CENTER);
        this.add(editMemoryPolicyPanel, BorderLayout.SOUTH);
        this.editButton.addActionListener(this);
        this.memoryPolicyList.addListSelectionListener(this);
        this.memoryPolicyComboBox.addItemListener(this);
        
    }

    @Override
    public void initTable() {
        super.initTable();
        
        if (super.getAttrTable().getModel() instanceof BasicTableModel) {
            BasicTableModel transitionTableModel = (BasicTableModel) super.getAttrTable().getModel();            
            transitionTableModel.addRow(new Object[]{NAMEATTR, currentTransition.getName()});           
        }
        
        super.getAttrTable().getModel().addTableModelListener(this);

    }

    protected void initMemoryPolicyOptionComboBox() {
        DefaultComboBoxModel defaultModel = (DefaultComboBoxModel) this.memoryPolicyComboBox.getModel();
        defaultModel.removeAllElements();
        defaultModel.addElement(MemoryPolicy.resampling);
        defaultModel.addElement(MemoryPolicy.enablingMemory);
        defaultModel.addElement(MemoryPolicy.ageMemory);
        this.memoryPolicyComboBox.setModel(defaultModel);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (e.getType() == TableModelEvent.UPDATE) {            
            if (currentTransition != null) {
                if (super.getAttrTable().getValueAt(row, column) instanceof String) {
                    String tableAttrString = super.getAttrTable().getValueAt(row, column - 1).toString();
                    switch (tableAttrString) {
                        case NAMEATTR:     
                            String inputString = (String) super.getAttrTable().getValueAt(row, column);
                            if (configParser.isViableTransitionName(inputString) || currentTransition.getName().equals(inputString)) {
                                currentTransition.setName(inputString);
                            }else{
                                super.getAttrTable().setValueAt(currentTransition.getName(), row, column);
                            }
                            break;
                    }
                }
            }            
        }
        
    }

    public Transition getCurrentTransition() {
        return currentTransition;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == memoryPolicyList && !e.getValueIsAdjusting()) {
            if (this.memoryPolicyList.getSelectedValue() == null) {
                //this.memoryPolicyComboBox.setVisible(false);
            } else {
                this.memoryPolicyComboBox.setVisible(true);
                MemoryPolicyAtTransition m = (MemoryPolicyAtTransition) this.memoryPolicyList.getSelectedValue();
                this.memoryPolicyComboBox.setSelectedItem(m.memoryPolicy);
                this.memoryPolicyList.setSelectedValue(m, true);
            }
        }        
    }

    private DefaultListModel<MemoryPolicyAtTransition> fillMemoryListModel() {
        DefaultListModel<MemoryPolicyAtTransition> listModel = new DefaultListModel<>();
        for (BasicTimedTransition basicTimedTransition : currentTransition.getMemoryPolicyList().keySet()) {
            listModel.addElement(currentTransition.getMemoryPolicyList().get(basicTimedTransition));
        }
        return listModel;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            if (item instanceof MemoryPolicy) {
                MemoryPolicyAtTransition m = (MemoryPolicyAtTransition) this.memoryPolicyList.getSelectedValue();
                if (m != null) {
                    MemoryPolicy memoryPolicy = (MemoryPolicy) memoryPolicyComboBox.getSelectedItem();
                    m.setMemoryPolicy(memoryPolicy);
                    currentTransition.addMemoryPolicyAtFiring(m.getTransition(), m.getMemoryPolicy());                    
                    //memoryPolicyList.setListData(fillMemoryListModel());
                    memoryPolicyList.setSelectedValue(m, false);
                }

            }
            // do something with object
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editButton) {
            this.memoryDialog.refreshData();
            this.memoryDialog.setVisible(true);
        }
    }

}
