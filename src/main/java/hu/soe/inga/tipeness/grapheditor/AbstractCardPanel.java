/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

/**
 *
 * @author Andrew
 */
public abstract class AbstractCardPanel extends JPanel{
    private ConfigParser configParser;

    public AbstractCardPanel(ConfigParser configParser) {
        this.configParser = configParser;        
        this.setLayout(new GridBagLayout());
    } 
    
    public abstract JPanel initProperties();
    public void refreshProperties(ConfigParser configParser){
        this.configParser=configParser;
    }

    public ConfigParser getConfigParser() {
        return configParser;
    }
}
