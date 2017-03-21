package hu.nyme.inga.tipeness.grapheditor;

import hu.nyme.inga.tipeness.petrinetelements.Link;
import hu.nyme.inga.tipeness.petrinetelements.Breakpoint;
import hu.nyme.inga.tipeness.petrinetelements.InputEdge;
import hu.nyme.inga.tipeness.petrinetelements.AbstractEdge;
import hu.nyme.inga.tipeness.petrinetelements.AbstractIOEdge;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.DeterministicTransition;
import hu.nyme.inga.tipeness.petrinetelements.ExponentialTransition;
import hu.nyme.inga.tipeness.petrinetelements.GammaTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import hu.nyme.inga.tipeness.petrinetelements.OutputEdge;
import hu.nyme.inga.tipeness.petrinetelements.InhibitorEdge;
import hu.nyme.inga.tipeness.petrinetelements.NodeText;
import hu.nyme.inga.tipeness.petrinetelements.PetriNetNode;
import hu.nyme.inga.tipeness.simulation.ConfigParser;
import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.petrinetelements.Selectable;
import hu.nyme.inga.tipeness.petrinetelements.Transition;
import hu.nyme.inga.tipeness.petrinetelements.TruncNormalTransition;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PLayer;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PDragEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;

public class PetriNetEditor extends PCanvas {

    private PetriNetFrame parentFrame;
    private ConfigParser configParser;
    private JPopupMenu popup;
    private JMenuItem menuItem;
    private PPath line;
    private PNode popUpNode;
    private PetriNetNode startingNode;
    private PetriNetNode endingNode;

    public enum guiMode {
        movingMode, placeCreateMode, immedTransitionCreateMode, expTransitionCreateMode,
        deterministicTransitionCreateMode, normalTransitionCreateMode, gammaTransitionCreateMode, IOEdgeMode, InhibEdgeMode
    };

    guiMode graphGuiMode;
    PLayer nodeLayer;
    PLayer edgeLayer;

    public PetriNetEditor(PetriNetFrame parentFrame) {
        this.parentFrame = parentFrame;
        configParser = new ConfigParser();
        line = new PPath.Double();
        initPetriNetEditorGUI();

    }

    public void createPlace(Place newPlace) {
        String placeName = newPlace.getName().toLowerCase();
        while (configParser.isViablePlaceName(placeName) == false) {
            placeName = placeName + "!";
        }
        newPlace.setName(placeName);
        this.nodeLayer.addChild(newPlace);
        this.updateUI();
        this.configParser.addPlace(newPlace);
    }

    public void createTransition(Transition transition) {
        String transitionName = transition.getName().toLowerCase();
        while (configParser.isViableTransitionName(transitionName) == false) {
            transitionName = transitionName + "!";
        }
        transition.setName(transitionName);
        this.nodeLayer.addChild(transition);
        this.updateUI();
        this.configParser.addTransition(transition);
    }

    public void createEdge(AbstractEdge edge) {
        edge.setStart(this.startingNode);
        edge.setEnd(this.endingNode);
        this.startingNode.addEdge(edge);
        this.endingNode.addEdge(edge);
        this.edgeLayer.addChild(edge);
        resetEdgeEndPoints();
        edge.updateLink();
    }

    public void deleteNode(PetriNetNode petriNetnode) {
        if (petriNetnode instanceof Place) {
            Place place = (Place) petriNetnode;
            ArrayList<AbstractEdge> deleteEdges = new ArrayList<>();
            place.getEdges().stream().forEach((edge) -> {
                deleteEdges.add(edge);
            });

            for (AbstractEdge edge : deleteEdges) {
                deleteEdge(edge);
            }
            configParser.removePlace(place);
        } else if (petriNetnode instanceof Transition) {
            Transition transition = (Transition) petriNetnode;
            ArrayList<AbstractEdge> deleteEdges = new ArrayList<>();
            for (InputEdge inputEdge : transition.getInput()) {
                deleteEdges.add(inputEdge);
            }
            for (InhibitorEdge inhibitorEdge : transition.getInhibitor()) {
                deleteEdges.add(inhibitorEdge);
            }
            for (OutputEdge outputEdge : transition.getOutput()) {
                deleteEdges.add(outputEdge);
            }
            for (AbstractEdge edge : deleteEdges) {
                deleteEdge(edge);
            }

            for (ImmediateTransition immediateTransition : configParser.getImmedTranstions()) {
                if (transition instanceof BasicTimedTransition) {
                    BasicTimedTransition removeTimedTransition = (BasicTimedTransition) transition;
                    immediateTransition.removeMemoryPolicyAtFiring(removeTimedTransition);
                }
            }
            for (BasicTimedTransition basicTimedTransition : configParser.getMemoryTransitions()) {
                if (transition instanceof BasicTimedTransition) {
                    BasicTimedTransition removeTimedTransition = (BasicTimedTransition) transition;
                    basicTimedTransition.removeMemoryPolicyAtFiring(removeTimedTransition);
                }
            }
            configParser.removeTransition(transition);
        }
        nodeLayer.removeChild(petriNetnode);
        popUpNode = null;
        parentFrame.clearSelections();
    }

    private void deleteEdge(AbstractEdge edge) {
        edge.getStart().removeEdge(edge);
        edge.getEnd().removeEdge(edge);
        edgeLayer.removeChild(edge);
        popUpNode = null;
        parentFrame.clearSelections();
    }

    private void initPetriNetEditorGUI() {
        setPreferredSize(new Dimension(900, 600));
        popup = new JPopupMenu();
        menuItem = new JMenuItem("Delete");

        popup.add(menuItem);

        nodeLayer = getLayer();
        edgeLayer = new PLayer();
        this.add(popup);
        getRoot().addChild(edgeLayer);
        getCamera().addLayer(0, edgeLayer);

        edgeLayer.addChild(line);

        NodeLayerEventHandler nodeLayerEventHandler = new NodeLayerEventHandler();
        this.addInputEventListener(nodeLayerEventHandler);
        //this.removeInputEventListener(this.getZoomEventHandler());
        this.setZoomEventHandler(new CustomZoomEventHandler());
        //menuItem.addActionListener(this);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (popUpNode != null) {
                    PNode pickedNodeParent = popUpNode.getParent();
                    if (popUpNode.getParent() instanceof PetriNetNode) {
                        PetriNetNode popUpPetriNetNode = (PetriNetNode) popUpNode.getParent();
                        deleteNode(popUpPetriNetNode);
                    } else if (popUpNode.getParent() instanceof Breakpoint) {
                        Breakpoint popUpBreakpoint = (Breakpoint) popUpNode.getParent();
                        popUpBreakpoint.getParentEdge().deleteBreakpoint(popUpBreakpoint);
                    } else if (pickedNodeParent instanceof Link) {
                        Link link = (Link) pickedNodeParent;
                        AbstractEdge edge = link.getParentEdge();
                        deleteEdge(edge);
                    }
                }
                parentFrame.clearSelections();
                popup.setVisible(false);
            }
        });
    }

    public void setGraphGuiMode(guiMode graphGuiMode) {
        this.graphGuiMode = graphGuiMode;
    }

    public guiMode getGraphGuiMode() {
        return graphGuiMode;
    }

    public PetriNetNode getStart() {
        return startingNode;
    }

    public void setStart(PetriNetNode start) {
        this.startingNode = start;
    }

    public PetriNetNode getEnd() {
        return endingNode;
    }

    public void setEnd(PetriNetNode end) {
        this.endingNode = end;
    }

    public int getNumOFPlaces() {
        return this.configParser.getPlaces().size();
    }

    public int getNumOFTransitions() {
        return configParser.getImmedTranstions().size() + configParser.getMemoryTransitions().size();
    }

    public PNode getPopUpNode() {
        return popUpNode;
    }

    public void setPopUpNode(PNode popUpNode) {
        this.popUpNode = popUpNode;
    }

    public void resetEdgeEndPoints() {
        if (startingNode != null) {
            startingNode.removeHighlight();
        }
        startingNode = null;
        if (endingNode != null) {
            endingNode.removeHighlight();
        }
        this.line.reset();
        endingNode = null;
    }

    private void initPetriNet() {
        for (Place place : configParser.getPlaces()) {
            this.nodeLayer.addChild(place);
        }

        for (ImmediateTransition immedTransition : configParser.getImmedTranstions()) {
            this.nodeLayer.addChild(immedTransition);
        }

        for (BasicTimedTransition memoryTransition : configParser.getMemoryTransitions()) {
            this.nodeLayer.addChild(memoryTransition);
        }

        for (Place place : configParser.getPlaces()) {
            for (AbstractEdge edge : place.getEdges()) {
                this.edgeLayer.addChild(edge);
                edge.updateLink();
            }

        }
        this.updateUI();
    }

    public void clear() {
        this.configParser = new ConfigParser();
        this.nodeLayer.removeAllChildren();
        this.edgeLayer.removeAllChildren();
    }

    public ConfigParser getConfigParser() {
        return configParser;
    }

    private class NodeLayerEventHandler extends PDragEventHandler {

        {
            PInputEventFilter filter = new PInputEventFilter();
            filter.setAcceptsAlreadyHandledEvents(false);
            filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
            setEventFilter(filter);
        }

        @Override
        public void mousePressed(PInputEvent event) {
            PetriNetEditor.guiMode modeType = getGraphGuiMode();
            super.mousePressed(event);
            popup.setVisible(false);
            PNode pickedNodeParent = event.getPickedNode().getParent();
            if (modeType==null){
                super.mousePressed(event);
                return;
            }
            if (event.isLeftMouseButton()) {
                switch (modeType) {
                    case placeCreateMode:
                        resetEdgeEndPoints();
                        Place newPlace = new Place("Place" + getNumOFPlaces(), (Point2D.Double) event.getPosition());
                        createPlace(newPlace);
                        selectElement(newPlace);
                        break;
                    case immedTransitionCreateMode:
                        resetEdgeEndPoints();
                        ImmediateTransition immedTransitionNode = new ImmediateTransition("Transition" + getNumOFTransitions(), (Point2D.Double) event.getPosition());
                        createTransition(immedTransitionNode);
                        selectElement(immedTransitionNode);
                        break;
                    case expTransitionCreateMode:
                        resetEdgeEndPoints();
                        ExponentialTransition expTransitionNode = new ExponentialTransition("Transition" + getNumOFTransitions(), (Point2D.Double) event.getPosition());
                        createTransition(expTransitionNode);
                        selectElement(expTransitionNode);
                        break;
                    case deterministicTransitionCreateMode:
                        resetEdgeEndPoints();
                        DeterministicTransition detTransitionNode = new DeterministicTransition("Transition" + getNumOFTransitions(), (Point2D.Double) event.getPosition());
                        createTransition(detTransitionNode);
                        selectElement(detTransitionNode);
                        break;
                    case normalTransitionCreateMode:
                        resetEdgeEndPoints();
                        TruncNormalTransition normalTransitionNode = new TruncNormalTransition("Transition" + getNumOFTransitions(), (Point2D.Double) event.getPosition());
                        createTransition(normalTransitionNode);
                        selectElement(normalTransitionNode);
                        break;
                    case gammaTransitionCreateMode:
                        resetEdgeEndPoints();
                        GammaTransition gammaTransitionNode = new GammaTransition("Transition" + getNumOFTransitions(), (Point2D.Double) event.getPosition());
                        createTransition(gammaTransitionNode);
                        selectElement(gammaTransitionNode);
                        break;
                    case movingMode:
                        resetEdgeEndPoints();
                        if (pickedNodeParent instanceof Link) {
                            Link link = (Link) pickedNodeParent;
                            AbstractEdge edge = link.getParentEdge();
                            if (event.getClickCount() == 2) {
                                Point2D correctedPosition = new Point2D.Double();
                                correctedPosition.setLocation(event.getPosition().getX() - Breakpoint.DIAMETER / 2, event.getPosition().getY() - Breakpoint.DIAMETER / 2);
                                edge.addBreakpoint(correctedPosition);
                                selectElement(edge);
                            } else if (event.getClickCount() == 1) {
                                selectElement(edge);
                            }
                        } else if (pickedNodeParent instanceof PetriNetNode) {
                            PetriNetNode petriNetNode = (PetriNetNode) pickedNodeParent;
                            selectElement(petriNetNode);
                        }
                        break;

                    case IOEdgeMode:
                        if (pickedNodeParent != null && pickedNodeParent instanceof PetriNetNode) {
                            if (getStart() == null) {
                                setStart((PetriNetNode) pickedNodeParent);
                                getStart().highlightNode(Color.CYAN);
                            } else if (getStart() == (PetriNetNode) pickedNodeParent) {
                                getStart().removeHighlight();
                                setStart(null);
                            } else {
                                AbstractEdge createdEdge = null;
                                if (getStart() instanceof Place) {
                                    if (pickedNodeParent instanceof Transition) {
                                        setEnd((Transition) pickedNodeParent);
                                        InputEdge newEdge = new InputEdge((Place) getStart(), (Transition) getEnd());
                                        if (!isDuplicateEdge(newEdge)) {
                                            createEdge(newEdge);
                                            createdEdge = newEdge;
                                        }
                                    }

                                } else if (getStart() instanceof Transition) {
                                    if (pickedNodeParent instanceof Place) {
                                        setEnd((Place) pickedNodeParent);
                                        OutputEdge newEdge = new OutputEdge((Transition) getStart(), (Place) getEnd());
                                        if (!isDuplicateEdge(newEdge)) {
                                            createEdge(newEdge);
                                            createdEdge = newEdge;
                                        }
                                    }
                                }
                                resetEdgeEndPoints();
                                if (createdEdge != null) {
                                    selectElement(createdEdge);
                                }
                            }
                        } else {                            
                            resetEdgeEndPoints();
                        }
                        break;

                    case InhibEdgeMode:
                        if (event.isLeftMouseButton() && pickedNodeParent instanceof PetriNetNode) {
                            if (getStart() == null) {
                                setStart((PetriNetNode) pickedNodeParent);
                                getStart().highlightNode(Color.CYAN);
                            } else if (getStart() == (PetriNetNode) pickedNodeParent) {
                                getStart().removeHighlight();
                                setStart(null);
                            } else {
                                AbstractEdge createdEdge = null;
                                if (getStart() instanceof Place) {
                                    if (event.getPickedNode().getParent() instanceof Transition) {
                                        setEnd((PetriNetNode) pickedNodeParent);
                                        InhibitorEdge newEdge = new InhibitorEdge((Place) getStart(), (Transition) getEnd());
                                        if (!isDuplicateEdge(newEdge)) {
                                            createEdge(newEdge);
                                            createdEdge = newEdge;
                                        }
                                    }
                                }
                                resetEdgeEndPoints();
                                if (createdEdge != null) {
                                    selectElement(createdEdge);
                                }
                            }
                        } else {
                            resetEdgeEndPoints();
                        }
                        break;
                }
            } else if (event.isRightMouseButton()) {
                resetEdgeEndPoints();
            }
        }

        @Override
        public void mouseMoved(PInputEvent event) {
            PetriNetEditor.guiMode modeType = getGraphGuiMode();
            if (modeType==null){
                super.mouseMoved(event);
                return;
            }
            if (getStart() == null) {
                line.reset();
            }
            switch (modeType) {
                case IOEdgeMode:
                    if (getStart() != null) {
                        line.reset();
                        line = PPath.createLine(getStart().getInnerShape().getGlobalBounds().getCenterX(),
                                getStart().getInnerShape().getGlobalBounds().getCenterY(), event.getPosition().getX(), event.getPosition().getY());
                        line.setStroke(new BasicStroke(2));
                        edgeLayer.addChild(line);
                    }
                    break;
                case InhibEdgeMode:
                    if (getStart() != null) {
                        line.reset();
                        line = PPath.createLine(getStart().getInnerShape().getGlobalBounds().getCenterX(),
                                getStart().getInnerShape().getGlobalBounds().getCenterY(), event.getPosition().getX(), event.getPosition().getY());
                        line.setStroke(new BasicStroke(2));
                        edgeLayer.addChild(line);
                    }
                    break;
            }
        }

        @Override
        protected void startDrag(PInputEvent event) {
            super.startDrag(event);
            event.setHandled(true);
        }

        @Override
        protected void endDrag(PInputEvent event) {
            super.endDrag(event);
            if (event.isPopupTrigger()) {
                showPopup(event);
            }
            event.setHandled(true);
        }

        @Override
        protected void drag(PInputEvent event) {
            if (event.isRightMouseButton()) {
                event.setHandled(true);
                return;
            }
            PetriNetEditor.guiMode modeType = getGraphGuiMode();
            if (modeType==null){
                super.drag(event);
                return;
            }
            
            PNode pickedNodeParent = event.getPickedNode().getParent();
            if (pickedNodeParent instanceof Selectable) {
                Selectable node = (Selectable) event.getPickedNode().getParent();
                this.selectElement(node);
            }
            switch (modeType) {
                case movingMode:
                    if (pickedNodeParent instanceof PetriNetNode) {
                        super.drag(event);
                        PetriNetNode node = (PetriNetNode) event.getPickedNode().getParent();
                        for (AbstractEdge edge : node.getConnectedEdges()) {
                            edge.updateLink();
                        }
                    } else if (pickedNodeParent instanceof Breakpoint) {
                        super.drag(event);
                        Breakpoint breakPoint = (Breakpoint) pickedNodeParent;
                        breakPoint.getParentEdge().updateLink();
                        selectElement(breakPoint.getParentEdge());
                    } else if (pickedNodeParent instanceof NodeText) {
                        super.drag(event);
                    }
                    break;
            }
        }

        public boolean isDuplicateEdge(AbstractEdge edge) {
            if (edge instanceof InhibitorEdge) {
                InhibitorEdge inhibEdge = (InhibitorEdge) edge;
                for (AbstractEdge startEdge : getStart().getConnectedEdges()) {
                    if (startEdge instanceof InhibitorEdge) {
                        InhibitorEdge inhibStartEdge = (InhibitorEdge) startEdge;
                        if (inhibStartEdge.getStart() == inhibEdge.getStart() && inhibStartEdge.getEnd() == inhibEdge.getEnd()) {
                            return true;
                        }
                    }
                }
                return false;
            } else if (edge instanceof AbstractIOEdge) {
                AbstractIOEdge ioEdge = (AbstractIOEdge) edge;
                for (AbstractEdge startEdge : getStart().getConnectedEdges()) {
                    if (startEdge instanceof AbstractIOEdge) {
                        AbstractIOEdge ioStartEdge = (AbstractIOEdge) startEdge;
                        if (ioStartEdge.getStart() == ioEdge.getStart() && ioStartEdge.getEnd() == ioEdge.getEnd()) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return false;
        }

        private void showPopup(PInputEvent event) {
            popup.setLocation((int) event.getCanvasPosition().getX() + parentFrame.getX(),
                    (int) event.getCanvasPosition().getY() + parentFrame.getY());
            setPopUpNode(event.getPickedNode());
            popup.setVisible(true);

        }

        private void selectElement(Selectable selected) {
            parentFrame.setActiveElement(selected);
            if (selected instanceof PetriNetNode) {
                PetriNetNode petriNetNode = (PetriNetNode) selected;
                petriNetNode.highlightNode(Color.BLUE);
            } else if (selected instanceof AbstractEdge) {
                AbstractEdge abstractEdge = (AbstractEdge) selected;
                abstractEdge.highlightNode(Color.BLUE);
            }
        }
    }

    public void setConfigParser(ConfigParser configParser) {
        this.configParser = configParser;
        initPetriNet();
    }

}
