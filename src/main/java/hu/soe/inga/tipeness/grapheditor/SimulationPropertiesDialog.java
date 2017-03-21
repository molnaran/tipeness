/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.soe.inga.tipeness.grapheditor;

import hu.soe.inga.tipeness.simulation.ConfigParser;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author András Molnár
 */
public class SimulationPropertiesDialog extends JDialog {

    private ConfigParser configParser;
    private JComboBox simulationTypeCombobox;
    private JPanel mainPanel;    

    private BatchCardPanel batchCardPanel;
    private RepDelCardPanel repDelCardPanel;
    private StabilityAnalysisCardPanel analysisCardPanel;
    private CommonSimulationPropertiesPanel commonSimulationPropertiesPanel;
    private ListPanel placeListPanel;

    private JButton acceptButton;
    private JButton cancelButton;

    private PlaceListModel allPlaceModel;
    private PlaceListModel watchAvgModel;
    private PlaceListModel watchDiffModel;
    private PlaceListModel watchTokenModel;
    private PlaceListModel listAvgModel;
    private PlaceListModel listDiffModel;
    private PlaceListModel listTokenModel;

    private JPanel placePanel;
    private JPanel cards;
    
    final static String BATCHMEANS = "Batch means";
    final static String REPDEL = "Replication/deletion";
    final static String STABILITY = "Stability analysis";


    public SimulationPropertiesDialog(Frame owner, ConfigParser configParser, String title) {
        super(owner, title, ModalityType.DOCUMENT_MODAL);
        this.configParser = configParser;
        init();
        pack();
    }

    private void saveProperties() {
        switch ((String) simulationTypeCombobox.getSelectedItem()) {
            case BATCHMEANS:
                configParser.setmType(ConfigParser.MethodType.batchmean);
                configParser.setBatch(batchCardPanel.getBatchLengthInput());
                break;
            case REPDEL:
                configParser.setmType(ConfigParser.MethodType.repdel);
                configParser.setTerminatingTime(repDelCardPanel.getTerminatingTimeInput());
                break;
            case STABILITY:
                configParser.setmType(ConfigParser.MethodType.analysis);
                configParser.setBatch(analysisCardPanel.getBatchLengthInput());
                break;

        }
        configParser.setAlpha(commonSimulationPropertiesPanel.getConfidenceLevelInput());
        configParser.setMinSampleSize(commonSimulationPropertiesPanel.getMinSampleSizeInput());
        configParser.setWarmupLength(commonSimulationPropertiesPanel.getWarmupLengthInput());
        configParser.setMaxRelError(commonSimulationPropertiesPanel.getMaxRelErrorInputInput());
        configParser.setAlpha(1 - commonSimulationPropertiesPanel.getConfidenceLevelInput());
        if (commonSimulationPropertiesPanel.getOutputFilePath()!=null){
            configParser.setOutFileNamePath(commonSimulationPropertiesPanel.getOutputFilePath());
        }        
        configParser.setWatchAvgTokenList(getModelDataAsHashSet(watchAvgModel));
        configParser.setWatchDiffTokenList(getModelDataAsHashSet(watchDiffModel));
        configParser.setWatchTokenList(getModelDataAsHashSet(watchTokenModel));
        configParser.setListAvgTokenList(getModelDataAsHashSet(listAvgModel));
        configParser.setListDiffTokenList(getModelDataAsHashSet(listDiffModel));
        configParser.setListTokenList(getModelDataAsHashSet(listTokenModel));
        this.setVisible(false);
    }

    private void cancelProperties() {
        refreshData(configParser);
        setVisible(false);
    }

    private void init() {
        this.setMinimumSize(new Dimension(600, 600));
        this.setPreferredSize(new Dimension(1000, 600));
        this.setLocation(200, 200);

        JPanel buttonPanel = new JPanel();
        acceptButton = new JButton("Accept");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(acceptButton);
        buttonPanel.add(cancelButton);

        this.allPlaceModel = new PlaceListModel("All places", configParser.getPlacesNames());
        this.watchAvgModel = new PlaceListModel("Sign. Average Tokennumber", configParser.getWatchAvgTokenList());
        this.watchDiffModel = new PlaceListModel("Sign. Average Diff. Tukennumber", configParser.getWatchDiffTokenList());
        this.watchTokenModel = new PlaceListModel("Sign. Tokennumber", configParser.getWatchTokenList());
        this.listDiffModel = new PlaceListModel("Average Diff. Tukennumber", configParser.getListDiffTokenList());
        this.listAvgModel = new PlaceListModel("Average Tokennumber", configParser.getListAvgTokenList());
        this.listTokenModel = new PlaceListModel("Tokennumber", configParser.getListTokenList());

        placePanel = initPlacePanel();
        batchCardPanel = initBatchListPanel();
        repDelCardPanel = initRepDelListPanel();
        analysisCardPanel = new StabilityAnalysisCardPanel(configParser);
        commonSimulationPropertiesPanel = new CommonSimulationPropertiesPanel(configParser);
        mainPanel = new JPanel();
        BoxLayout box = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS);
        mainPanel.setLayout(box);
        initCardLayout();

        mainPanel.add(buttonPanel);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                saveProperties();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae) {
                cancelProperties();
            }
        });

        this.getContentPane().add(mainPanel);

        pack();
    }

    private JPanel initPlacePanel() {
        Dimension spacing = new Dimension(0, 8);
        JPanel panel = new JPanel();
        placeListPanel = new ListPanel(this.allPlaceModel);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 50, 5, 50));
        JLabel allPlaceListLabel = new JLabel("Place list:");
        allPlaceListLabel.setHorizontalAlignment(SwingConstants.LEFT);

        panel.add(allPlaceListLabel);
        panel.add(Box.createRigidArea(spacing));
        panel.add(placeListPanel);
        return panel;
    }

    private BatchCardPanel initBatchListPanel() {
        HashMap<String, PlaceListModel> listModels = new HashMap<>();
        listModels.put(this.listAvgModel.getName(), this.listAvgModel);
        listModels.put(this.listDiffModel.getName(), this.listDiffModel);
        listModels.put(this.watchAvgModel.getName(), this.watchAvgModel);
        listModels.put(this.watchDiffModel.getName(), this.watchDiffModel);
        return new BatchCardPanel(placeListPanel, listModels, configParser);
    }

    private RepDelCardPanel initRepDelListPanel() {
        HashMap<String, PlaceListModel> listModels = new HashMap<>();
        listModels.put(this.listAvgModel.getName(), this.listAvgModel);
        listModels.put(this.listTokenModel.getName(), this.listTokenModel);
        listModels.put(this.watchAvgModel.getName(), this.watchAvgModel);
        listModels.put(this.watchTokenModel.getName(), this.watchTokenModel);
        return new RepDelCardPanel(placeListPanel, listModels, configParser);
    }

    private void initCardLayout() {
        JPanel methodPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(methodPanel, BoxLayout.X_AXIS);
        methodPanel.setLayout(boxLayout);
        GridBagConstraints c = new GridBagConstraints();
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel methodComboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        String comboBoxItems[] = {BATCHMEANS, REPDEL, STABILITY};
        simulationTypeCombobox = new JComboBox(comboBoxItems);
        simulationTypeCombobox.setEditable(false);
        simulationTypeCombobox.addItemListener(new MethodItemListener());

        methodComboBoxPanel.add(new JLabel("<html>Simulation<br>method</html>"));
        methodComboBoxPanel.add(simulationTypeCombobox);

        cards = new JPanel(new CardLayout());
        cards.add(batchCardPanel, BATCHMEANS);
        cards.add(repDelCardPanel, REPDEL);
        cards.add(analysisCardPanel, STABILITY);

        switch (configParser.getmType()) {
            case analysis:
                simulationTypeCombobox.setSelectedItem(STABILITY);
                break;
            case batchmean:
                simulationTypeCombobox.setSelectedItem(BATCHMEANS);
                break;
            case repdel:
                simulationTypeCombobox.setSelectedItem(REPDEL);
                break;
        }

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        methodPanel.add(cards, c);

        c.weightx = 0.5;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 5;
        c.gridy = 0;

        //methodPanel.add(placePanel, c);
        mainPanel.add(methodComboBoxPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(placePanel);
        //dialogPanel.add(initListEditBtnPanel());

        mainPanel.add(methodPanel);
        mainPanel.add(commonSimulationPropertiesPanel);
    }

    public void refreshData(ConfigParser configParser) {
        this.configParser = configParser;
        this.allPlaceModel.setData(configParser.getPlacesNames());
        this.watchAvgModel.setData(configParser.getWatchAvgTokenList());
        this.watchDiffModel.setData(configParser.getWatchDiffTokenList());
        this.listDiffModel.setData(configParser.getListDiffTokenList());
        this.listAvgModel.setData(configParser.getListAvgTokenList());
        this.repDelCardPanel.refreshProperties(configParser);
        this.batchCardPanel.refreshProperties(configParser);
        this.analysisCardPanel.refreshProperties(configParser);
        this.commonSimulationPropertiesPanel.refreshProperties(configParser);
    }

    private class MethodItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            CardLayout cl = (CardLayout) (cards.getLayout());
            cl.show(cards, (String) e.getItem());
        }
    }

    private HashSet<String> getModelDataAsHashSet(PlaceListModel placeListModel) {
        HashSet<String> dataAsHashSet = new HashSet<>();
        for (int i = 0; i < placeListModel.size(); i++) {
            dataAsHashSet.add((String) placeListModel.get(i));
        }
        return dataAsHashSet;
    }
}
