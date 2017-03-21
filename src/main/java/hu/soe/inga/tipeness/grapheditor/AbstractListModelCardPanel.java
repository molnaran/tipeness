/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Alkohol
 */
public abstract class AbstractListModelCardPanel extends AbstractCardPanel{

    private ListPanel placeListPanel;
    private JTabbedPane tabbedPane;
    private JButton addBtn;
    private JButton rmvBtn;
    
    public AbstractListModelCardPanel(ListPanel placeListPanel, HashMap<String, PlaceListModel> listmodels, ConfigParser configParser) {
        super(configParser);
        this.placeListPanel=placeListPanel;
        this.addBtn= new JButton("Add");
        this.rmvBtn = new JButton("Remove");
        init(listmodels);       
        
    }
    private void init(HashMap<String, PlaceListModel> listmodels){        
        
        this.tabbedPane = new JTabbedPane();
        initTabs(listmodels);      
        GridBagConstraints c = new GridBagConstraints();
        this.setBorder(new EmptyBorder(5, 50, 5, 50));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridwidth = 5;
        c.gridx = 0;
        c.gridy = 1;
        this.add(tabbedPane, c);
        JPanel listBtnPanel = new JPanel();
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(1, 2));
        listBtnPanel.setLayout(new GridBagLayout());

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                if (placeListPanel.getList().getSelectedValue() != null && placeListPanel.getList().getSelectedValue() instanceof String) {
                    if (tabbedPane.getSelectedComponent() instanceof ListPanel) {
                        ListPanel listPanel = (ListPanel) tabbedPane.getSelectedComponent();
                        ArrayList<String> selectedList = (ArrayList<String>) placeListPanel.getList().getSelectedValuesList();
                        for (String placeName : selectedList) {
                            if (!listPanel.getListModel().contains(placeName)) {
                                listPanel.addElement(placeName);
                            }
                        }
                    }
                }
            }
        });

        rmvBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                if (tabbedPane.getSelectedComponent() instanceof ListPanel) {
                    ListPanel listPanel = (ListPanel) tabbedPane.getSelectedComponent();
                    listPanel.removeElement(listPanel.getList().getSelectedValue());
                    if (!listPanel.getList().getSelectedValuesList().isEmpty()) {
                        ArrayList<String> selectedList = (ArrayList<String>) listPanel.getList().getSelectedValuesList();
                        for (String placeName : selectedList) {
                            listPanel.removeElement(placeName);
                        }
                    }
                }
            }
        });

        btnPanel.add(addBtn);
        btnPanel.add(rmvBtn);
        listBtnPanel.add(btnPanel);
        
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(listBtnPanel, c);
        
        c.ipady=30;
        c.weightx = 0.3;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 5;
        this.add(initProperties(), c);
    }
    
    protected void initTabs(HashMap<String, PlaceListModel> listmodels){          
        for (String tabName:listmodels.keySet()){
            ListPanel listPanel= new ListPanel(listmodels.get(tabName));
            this.tabbedPane.addTab(tabName, listPanel);
        }    
        
    }    
    
}
