/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import java.util.HashMap;
import java.util.HashSet;
import javax.swing.DefaultListModel;

/**
 *
 * @author Andrew
 */
public class PlaceListModel extends DefaultListModel{
    
    private final String name;

    public PlaceListModel(String name, HashSet<String> listData) {
        this.name=name;
        init(listData);
    }
    
    
    private void init(HashSet<String> listData){
        super.clear();
        for (String data: listData){
            super.addElement(data);
        }
    }
    
    private void init(HashMap<String, ?> listData){
        super.clear();
        for (String data: listData.keySet()){
            super.addElement(data);
        }
    }
    
    public void setData(HashSet<String> listData){
        super.clear();
        for (String data: listData){
            super.addElement(data);
        }
    }
    
    public void setData(HashMap<String, ?> listData){
        super.clear();
        for (String data: listData.keySet()){
            super.addElement(data);
        }
    }

    public String getName() {
        return name;
    }
    
}
