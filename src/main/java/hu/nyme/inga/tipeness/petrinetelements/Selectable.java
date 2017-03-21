/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.petrinetelements;

import java.awt.Color;

/**
 *
 * @author Andrew
 */
public interface Selectable {
    void highlightNode(Color color);
    void removeHighlight();    
}
