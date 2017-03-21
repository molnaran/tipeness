package hu.nyme.inga.tipeness.grapheditor;

import hu.nyme.inga.tipeness.petrinetelements.AbstractEdge;
import hu.nyme.inga.tipeness.petrinetelements.Selectable;
import hu.nyme.inga.tipeness.attributepanels.NormalTransitionAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.PlaceAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.DeterministicTransitionAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.TransitionAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.ImmedTransitionAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.GammaTransitionAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.EdgeAttrPanel;
import hu.nyme.inga.tipeness.attributepanels.ExponentialTransitionAttrPanel;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.petrinetelements.DeterministicTransition;
import hu.nyme.inga.tipeness.petrinetelements.ExponentialTransition;
import hu.nyme.inga.tipeness.petrinetelements.GammaTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import hu.nyme.inga.tipeness.petrinetelements.Transition;
import hu.nyme.inga.tipeness.petrinetelements.TruncNormalTransition;
import hu.nyme.inga.tipeness.simulation.InvalidParamfileException;
import hu.nyme.inga.tipeness.simulation.SimulationUnit;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import nl.tudelft.simulation.dsol.interpreter.operations.AALOAD;

public class PetriNetFrame extends JFrame {

    private JFileChooser fc;
    private PetriNetEditor petriNetEditor;
    private Selectable activeElement;
    private JMenuBar menuBar;
    private JMenu file, saveMenu, simulationMenu;
    private JMenuItem newFile, open, save, savAs, exit, options, simulate;
    private JPanel infoPanel;
    private SimulationPropertiesDialog simulationDialog;
    private JSplitPane splitPane;
    private JScrollPane graphScrollPane;
    private JScrollPane listScrollPane;
    private JToolBar toolbar;
    private ArrayList<Mode> modeList;
    private JButton moveModeButton, placeCreateModeButton, immedTransitionCreateModeButton, expTransitionCreateModeButton,
            deterministicTransitionCreateModeButton, normalTransitionCreateModeButton, gammaTransitionCreateModeButton, ioEdgeModeButton,
            inhibEdgeModeButton;

    public PetriNetFrame() {
        init();
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(() -> {
                PetriNetFrame petriNetFrame = new PetriNetFrame();
                petriNetFrame.getPetriNetEditor().getConfigParser().setIsErrorFatal(false);
                petriNetFrame.setVisible(true);
            });
        } else {
            try {
                String inputFilePath = args[0];
                SimulationUnit simUnit = new SimulationUnit(inputFilePath);
                simUnit.run();
            } catch (InvalidParamfileException ie) {
                System.out.println(ie.getMessage());
                System.exit(-1);
            }

        }

    }

    private void init() {
        initMenu();
        initToolBar();
        BorderLayout bord = new BorderLayout();
        this.setLayout(bord);
        getContentPane().add("North", toolbar);
        updateTitle("Petri net Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(0, 0);
        listScrollPane = new JScrollPane();
        infoPanel = new JPanel();
        listScrollPane.add(infoPanel);
        listScrollPane.setViewportView(infoPanel);
        petriNetEditor = new PetriNetEditor(this);
        updateTitle("Untitled");
        graphScrollPane = new JScrollPane(petriNetEditor);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphScrollPane, listScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1);

        splitPane.setDividerLocation(0.8);

        Dimension minimumSize = new Dimension(230, 200);
        listScrollPane.setMinimumSize(minimumSize);
        graphScrollPane.setMinimumSize(minimumSize);
        this.getContentPane().add("Center", splitPane);
        listScrollPane.setVisible(true);
        listScrollPane.revalidate();
        listScrollPane.repaint();
        pack();
        setVisible(true);
        updateMode(moveModeButton);
        simulationDialog = new SimulationPropertiesDialog(this, this.petriNetEditor.getConfigParser(), "Simulate");
        simulationDialog.setVisible(false);
    }

    private void initMenu() {
        MouseListener menuMouseListener = new MenuMouseListener();

        menuBar = new JMenuBar();
        file = new JMenu("File");
        simulationMenu = new JMenu("Simulation");
        exit = new JMenuItem("Exit");
        exit.addMouseListener(menuMouseListener);
        saveMenu = new JMenu("Save...");
        newFile = new JMenuItem("New");
        newFile.addMouseListener(menuMouseListener);
        save = new JMenuItem("Save");
        save.addMouseListener(menuMouseListener);
        savAs = new JMenuItem("Save as..");
        savAs.addMouseListener(menuMouseListener);
        open = new JMenuItem("Open");
        open.addMouseListener(menuMouseListener);
        options = new JMenuItem("Simulatation options");
        options.addMouseListener(menuMouseListener);

        simulate = new JMenuItem("Simulatate");
        simulate.addMouseListener(menuMouseListener);

        simulationMenu.add(options);
        simulationMenu.add(simulate);
        menuBar.add(file);
        menuBar.add(simulationMenu);

        file.add(newFile);
        file.add(open);
        file.add(saveMenu);
        file.add(exit);

        saveMenu.add(save);
        saveMenu.add(savAs);

        this.setJMenuBar(menuBar);
    }
    
        
    private void initToolBar() {     
        
        toolbar = new JToolBar("Modes");
        
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        modeList = new ArrayList<>();
        
        Mode movingMode = new Mode("Move","/img/move.gif", PetriNetEditor.guiMode.movingMode);
        modeList.add(movingMode);

        Mode placeMode = new Mode("Create Place","/img/place.gif", PetriNetEditor.guiMode.placeCreateMode);
        modeList.add(placeMode);

        Mode immedTransitionCreateMode = new Mode("Create immediate transition","/img/immed_transition.gif", PetriNetEditor.guiMode.immedTransitionCreateMode);
        modeList.add(immedTransitionCreateMode);
      
        Mode expTransitionCreateMode = new Mode("Create exponentially delayed transition","/img/exp_transition.gif", PetriNetEditor.guiMode.expTransitionCreateMode);
        modeList.add(expTransitionCreateMode);

        Mode deterministicTransitionCreateMode = new Mode("Create deterministicly delayed transition","/img/det_transition.gif",  PetriNetEditor.guiMode.deterministicTransitionCreateMode);
        modeList.add(deterministicTransitionCreateMode);

        Mode normalTransitionCreateMode = new Mode("Create normal delayed transition","/img/norm_transition.gif", PetriNetEditor.guiMode.normalTransitionCreateMode);
        modeList.add(normalTransitionCreateMode);

        Mode gammaTransitionCreateMode = new Mode("Create gamma delayed transition","/img/gamma_transition.gif", PetriNetEditor.guiMode.gammaTransitionCreateMode);
        modeList.add(gammaTransitionCreateMode);

        Mode ioEdgeCreateMode = new Mode("Create I/O edge","/img/io_edge.gif", PetriNetEditor.guiMode.IOEdgeMode);
        modeList.add(ioEdgeCreateMode);

        Mode inhibEdgeCreateMode = new Mode("Create ingibitor edge","/img/inhib_edge.gif", PetriNetEditor.guiMode.InhibEdgeMode);
        modeList.add(inhibEdgeCreateMode);

        ToolBarMouseListener toolBarListener = new ToolBarMouseListener();

        for (Mode mode : modeList) {
            panel.add(mode.getModeButton());
            mode.getModeButton().addMouseListener(toolBarListener);
            panel.add(Box.createRigidArea(new Dimension(5,5)));
        }
        toolbar.add(panel, BorderLayout.WEST);
        toolbar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propName = evt.getPropertyName();
                if ("orientation".equals(propName)) {                    
                    Integer newValue = (Integer) evt.getNewValue();
                    if (newValue.intValue() == JToolBar.HORIZONTAL) {
                        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                    } else {                        
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    }
                }
            }
        });
    }

    private void updateMode(JButton pressedButton) {
        modeList.stream().forEach((mode) -> {
            if (mode.getModeButton().equals(pressedButton)) {
                mode.getModeButton().setEnabled(false);
                this.petriNetEditor.setGraphGuiMode(mode.getGraphGuiMode());
            } else {
                mode.getModeButton().setEnabled(true);
            }
        });
    }

    private void newFile() {
        petriNetEditor.clear();
        petriNetEditor.setConfigParser(new ConfigParser());
        updateTitle("Untitled");
    }

    private void openFile() {
        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(new FileNameExtensionFilter("XML file", "xml"));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                petriNetEditor.clear();
                petriNetEditor.setConfigParser(new ConfigParser(file.getAbsolutePath()));
                updateTitle(file.getAbsolutePath());
            } catch (InvalidParamfileException ie) {
                JOptionPane.showMessageDialog(null, "Invalid parameter file! " + ie.getMessage());
            }

        }
    }


    private void initSimulation() {
        SimulationUnit simUnit = new SimulationUnit(petriNetEditor.getConfigParser());
        JDialog stopDialog = new JDialog();
        stopDialog.setTitle("Simulation");
        stopDialog.setLayout(new GridBagLayout());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                setEnabled(false);
                simUnit.run();
                setEnabled(true);
                stopDialog.setVisible(false);
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 20, 0, 20);
        stopDialog.add(new JLabel("Simulation running..."), c);
        c.insets = new Insets(5, 0, 0, 0);
        c.weightx = 0.5;
        c.gridy = 1;
        c.gridwidth = 1;
        stopDialog.setMinimumSize(new Dimension(300, 110));
        stopDialog.setResizable(false);
        JButton stopSimulationBtn = new JButton("Stop");
        stopDialog.add(stopSimulationBtn, c);
        stopDialog.setVisible(true);
        stopSimulationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                simUnit.interrupted = true;
                stopDialog.setVisible(false);
                setEnabled(true);
                requestFocus();
            }
        });
        stopDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        stopDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                setEnabled(true);
                stopDialog.setVisible(false);
                requestFocus();
            }
        });
        thread.start();
    }

    private void saveFile() {
        fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("XML file", "xml"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml", "xml");
        fc.setFileFilter(filter);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File saveFile = fc.getSelectedFile();
            if (!fc.getSelectedFile().getAbsolutePath().endsWith(".xml")) {
                saveFile = new File(fc.getSelectedFile() + ".xml");
            }
            this.petriNetEditor.getConfigParser().setInputFilePath(saveFile + "");
            petriNetEditor.getConfigParser().writeXMLFile(saveFile + "");
            updateTitle(saveFile.getAbsolutePath());
        }
    }

    private class MenuMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
            if (e.getSource().equals(open)) {
                clearSelections();
                openFile();
            } else if (e.getSource().equals(newFile)) {
                clearSelections();
                newFile();
            } else if (e.getSource().equals(save)) {
                saveFile();
            } else if (e.getSource().equals(savAs)) {
            } else if (e.getSource().equals(options)) {
                simulationDialog.refreshData(petriNetEditor.getConfigParser());
                simulationDialog.setVisible(true);
            } else if (e.getSource().equals(simulate)) {
                initSimulation();
            } else if (e.getSource().equals(exit)) {
                System.exit(0);
            }
        }
    }

    private class ToolBarMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                updateMode((JButton) e.getSource());
                e.setSource(this);
            }

        }
    }

    public void setActiveElement(Selectable selectable) {
        if (this.activeElement != null) {
            activeElement.removeHighlight();
        }
        this.activeElement = selectable;
        if (selectable instanceof Place) {
            Place activePlace = (Place) selectable;
            infoPanel.removeAll();
            infoPanel.add(new PlaceAttrPanel(activePlace, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof AbstractEdge) {
            AbstractEdge activeEdge = (AbstractEdge) selectable;
            infoPanel.removeAll();
            infoPanel.add(new EdgeAttrPanel(activeEdge));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof ImmediateTransition) {
            ImmediateTransition activeTransition = (ImmediateTransition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new ImmedTransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof DeterministicTransition) {
            DeterministicTransition activeTransition = (DeterministicTransition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new DeterministicTransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof TruncNormalTransition) {
            TruncNormalTransition activeTransition = (TruncNormalTransition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new NormalTransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof GammaTransition) {
            GammaTransition activeTransition = (GammaTransition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new GammaTransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof ExponentialTransition) {
            ExponentialTransition activeTransition = (ExponentialTransition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new ExponentialTransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        } else if (selectable instanceof Transition) {
            Transition activeTransition = (Transition) selectable;
            infoPanel.removeAll();
            infoPanel.add(new TransitionAttrPanel(activeTransition, this.petriNetEditor.getConfigParser()));
            infoPanel.revalidate();
            infoPanel.repaint();
        }
    }

    public void clearSelections() {
        this.infoPanel.removeAll();
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    public PetriNetEditor getPetriNetEditor() {
        return petriNetEditor;
    }

    private void updateTitle(String file) {
        this.setTitle("Tipeness - " + file);
    }
    
}
