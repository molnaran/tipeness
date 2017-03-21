/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.attributepanels;

import hu.nyme.inga.tipeness.petrinetelements.MemoryPolicyAtTransition;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author András Molnár
 */
public class MemoryPolicyEditDialog extends JDialog {

    private ListSelectionListener listSelectionListener;
    private ActionListener actionListener;
    DefaultListModel<MemoryPolicyAtTransition> memoryDataModel;
    HashSet<BasicTimedTransition> basicTimedTransitions;
    JButton addHandledButton;
    JButton removeHandledButton;
    JButton okBtn;
    JButton noBtn;
    
    JPanel buttonPanel;
    JLabel editedLabel;
    JLabel notEditedLabel;
    JList<MemoryPolicyAtTransition> editedTransitionList;
    JList<MemoryPolicyAtTransition> notEditedTransitionList;
    JScrollPane editedScrollPane;
    JScrollPane notEditedScrollPane;

    public MemoryPolicyEditDialog(JFrame parentFrame, HashSet<BasicTimedTransition> timedTransitions, DefaultListModel<MemoryPolicyAtTransition> memoryDataModel, String title) {
        super(parentFrame, title, ModalityType.APPLICATION_MODAL);
        this.basicTimedTransitions = timedTransitions;
        this.memoryDataModel=memoryDataModel;
        
        this.buttonPanel = new JPanel();
        this.addHandledButton = new JButton("<< Add");
        this.removeHandledButton = new JButton("Remove >>");
        this.okBtn = new JButton("Accept");
        this.noBtn = new JButton("Cancel");
        this.editedLabel= new JLabel("Edited");
        this.notEditedLabel= new JLabel("Not edited");
        this.editedTransitionList = new JList(new DefaultListModel());
        this.editedScrollPane = new JScrollPane(editedTransitionList);
        this.editedScrollPane.setViewportView(editedTransitionList);
        notEditedTransitionList = new JList(new DefaultListModel());        
        this.notEditedScrollPane = new JScrollPane(notEditedTransitionList);        
        this.notEditedScrollPane.setViewportView(notEditedTransitionList);
        //this.memoryPolicyComboBox= new JComboBox<>(new MemoryPolicy[]{MemoryPolicy.resampling, MemoryPolicy.enablingMemory,
        //  MemoryPolicy.ageMemory});
        this.actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == addHandledButton) {                    
                    if (!notEditedTransitionList.getSelectedValuesList().isEmpty()) {
                        ArrayList<MemoryPolicyAtTransition> selectedList = (ArrayList<MemoryPolicyAtTransition>) notEditedTransitionList.getSelectedValuesList();
                        for (MemoryPolicyAtTransition selectedMemoryPolicyAtTransition : selectedList) {                           
                            ((DefaultListModel<MemoryPolicyAtTransition>) notEditedTransitionList.getModel()).removeElement(selectedMemoryPolicyAtTransition);
                            ((DefaultListModel<MemoryPolicyAtTransition>) editedTransitionList.getModel()).addElement(selectedMemoryPolicyAtTransition);
                        }                        
                    }
                } else if (e.getSource() == removeHandledButton) {                    
                    if (!editedTransitionList.getSelectedValuesList().isEmpty()) {
                        ArrayList<MemoryPolicyAtTransition> selectedList=(ArrayList<MemoryPolicyAtTransition>) editedTransitionList.getSelectedValuesList();
                        for (MemoryPolicyAtTransition selectedMemoryPolicyAtTransition: selectedList){
                            ((DefaultListModel<MemoryPolicyAtTransition>) editedTransitionList.getModel()).removeElement(selectedMemoryPolicyAtTransition);
                            ((DefaultListModel<MemoryPolicyAtTransition>) notEditedTransitionList.getModel()).addElement(selectedMemoryPolicyAtTransition);
                        
                        }
                    }
                } else if (e.getSource() == okBtn) {
                    finalizeHashMap();
                    setVisible(false);
                }else if (e.getSource() == noBtn){
                    setVisible(false);                    
                }
            }
        };
        addHandledButton.addActionListener(actionListener);
        removeHandledButton.addActionListener(actionListener);
        okBtn.addActionListener(actionListener);
        noBtn.addActionListener(actionListener);

        editedTransitionList.addListSelectionListener(listSelectionListener);
        notEditedTransitionList.addListSelectionListener(listSelectionListener);

        //this.getContentPane().add(dialogPanel);
        this.setResizable(false);
        refreshData();
        initLayout();
        pack();
    }

    public void refreshData() {
        initEditedMemoryListModel();
        initNotEditedMemoryListModel();

    }   
    
    public void initLayout(){
        
        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGap(228, 228, 228)
                .addComponent(okBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn)
                    .addComponent(noBtn))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editedLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(editedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(addHandledButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(removeHandledButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(notEditedLabel)
                            .addComponent(notEditedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(notEditedLabel)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(notEditedScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(addHandledButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeHandledButton)))))
                .addGap(18, 18, 18)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(222, Short.MAX_VALUE))
        );
        
        
        this.setMinimumSize(new Dimension(300, 300));
        this.setPreferredSize(new Dimension(660, 300));
        this.setLocation(200, 200);
    }

    private void initEditedMemoryListModel() {
        DefaultListModel<MemoryPolicyAtTransition>editMemoryListModel=((DefaultListModel<MemoryPolicyAtTransition>) editedTransitionList.getModel());
        
        editMemoryListModel.clear();
        if (!memoryDataModel.isEmpty()) {
            for (int i = 0; i < memoryDataModel.getSize(); i++) {
                MemoryPolicyAtTransition memoryPolicyAtTransition= new MemoryPolicyAtTransition(memoryDataModel.get(i).getTransition(),
                        memoryDataModel.get(i).getMemoryPolicy());
                editMemoryListModel.addElement(memoryPolicyAtTransition);
            }
        }
    }
    private void initNotEditedMemoryListModel() {
        DefaultListModel<MemoryPolicyAtTransition>notEditMemoryListModel=((DefaultListModel<MemoryPolicyAtTransition>) notEditedTransitionList.getModel());
        notEditMemoryListModel.clear();
        if (!basicTimedTransitions.isEmpty()) {            
            for (BasicTimedTransition transitionName : basicTimedTransitions) {
                MemoryPolicyAtTransition m1= new MemoryPolicyAtTransition(transitionName);
                if (!memoryDataModel.contains(m1)) {
                    notEditMemoryListModel.addElement(m1);
                }
            }
        }
    }

    private void finalizeHashMap() {        
        DefaultListModel<MemoryPolicyAtTransition> model = (DefaultListModel) editedTransitionList.getModel();
        this.memoryDataModel.clear();
        for (int i = 0; i < model.getSize(); i++) {
            this.memoryDataModel.addElement(model.elementAt(i));
        }
    }
}
