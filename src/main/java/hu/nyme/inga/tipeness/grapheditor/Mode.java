/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.grapheditor;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author András
 */
public class Mode {
    private PetriNetEditor.guiMode graphGuiMode;
    private JButton modeButton;
    private static Dimension pictureSize = new Dimension(20, 20);
    private static Dimension buttonSize = new Dimension(25, 25);
    
    public Mode(String toolTip,String iconPath, PetriNetEditor.guiMode guiMode){
        this.graphGuiMode=guiMode;
        this.modeButton = new JButton();
        this.modeButton.setToolTipText(toolTip);
        
        try {
            InputStream in = getClass().getResourceAsStream(iconPath);
            Image img = ImageIO.read(in); 
            img=resize((BufferedImage)img, pictureSize.width, pictureSize.height);
            //ImageIcon imageIcon=new javax.swing.ImageIcon(getClass().getResource(iconPath));
            modeButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            String placeHolderName=iconPath.replace(".png", "").replace("/", "").replace("img","");
            System.out.println("Picture not found at path: "+ iconPath);
            System.out.println(ex);
            modeButton.setText(placeHolderName);
        }
        
            this.modeButton.setPreferredSize(buttonSize);
            this.modeButton.setMinimumSize(buttonSize);
            this.modeButton.setMaximumSize(buttonSize);
    }

    public PetriNetEditor.guiMode getGraphGuiMode() {
        return graphGuiMode;
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    public JButton getModeButton() {
        return modeButton;
    }
    
    
}
