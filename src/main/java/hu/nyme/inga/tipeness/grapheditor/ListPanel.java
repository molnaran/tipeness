/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.grapheditor;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Andrew
 */
public class ListPanel extends JPanel {

    private JList<String> list;

    public ListPanel(PlaceListModel defaultListModel) {
        list = new JList<>();
        list.setModel(defaultListModel);
        list.setVisibleRowCount(3);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setFixedCellWidth(100);
        init();
    }

    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JScrollPane watchAvgTokenScroll = new JScrollPane(list);        

        watchAvgTokenScroll.setPreferredSize(new Dimension(250, 80));
        this.add(watchAvgTokenScroll);
    }

    public PlaceListModel getListModel() {
        PlaceListModel listModel = (PlaceListModel) list.getModel();
        return listModel;
    }

    public JList<String> getList() {
        return this.list;
    }

    public void addElement(String element) {
        ((PlaceListModel) this.list.getModel()).addElement(element);
    }

    public void removeElement(String element) {
        ((PlaceListModel) this.list.getModel()).removeElement(element);
    }

    public void setListData(HashSet<String> data) {
        PlaceListModel listModel = ((PlaceListModel) this.list.getModel());
        listModel.setData(data);
    }

    public void setListDataWKeySet(HashMap<String, ?> data) {
        PlaceListModel listModel = ((PlaceListModel) this.list.getModel());
        listModel.setData(data);
    }
}
