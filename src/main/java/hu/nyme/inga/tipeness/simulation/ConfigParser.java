/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.nyme.inga.tipeness.simulation;

import hu.nyme.inga.tipeness.petrinetelements.AbstractEdge;
import hu.nyme.inga.tipeness.petrinetelements.BreakpointXMLNode;
import hu.nyme.inga.tipeness.petrinetelements.InhibitorEdge;
import hu.nyme.inga.tipeness.petrinetelements.InputEdge;
import hu.nyme.inga.tipeness.petrinetelements.MemoryPolicyAtTransition;
import hu.nyme.inga.tipeness.petrinetelements.OutputEdge;
import hu.nyme.inga.tipeness.petrinetelements.PetriNetNode;
import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hu.nyme.inga.tipeness.statdata.MeasureUnitStatistic;
import hu.nyme.inga.tipeness.petrinetelements.BasicTimedTransition;
import hu.nyme.inga.tipeness.petrinetelements.DeterministicTransition;
import hu.nyme.inga.tipeness.petrinetelements.ExponentialTransition;
import hu.nyme.inga.tipeness.petrinetelements.GammaTransition;
import hu.nyme.inga.tipeness.petrinetelements.ImmediateTransition;
import hu.nyme.inga.tipeness.petrinetelements.Place;
import hu.nyme.inga.tipeness.petrinetelements.Transition;
import hu.nyme.inga.tipeness.petrinetelements.TruncNormalTransition;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConfigParser implements XMLConstants {

    private HashMap<Place, Integer> initialTokenDistribution;
    private HashSet<Place> places;
    private HashSet<ImmediateTransition> immedTranstions;
    private HashSet<BasicTimedTransition> memoryTransitions;

    public static enum MethodType {
        repdel, batchmean, analysis
    };
    private boolean isErrorFatal;
    private MethodType mType;
    private double terminatingTime;
    private double warmupLength;
    private double alpha;
    private int minSampleSize;
    private double maxRelError;
    private int batch;
    private String outFileNamePath;
    private String inputFilePath;

    private HashSet<String> watchTokenList = new HashSet<>();
    private HashSet<String> watchAvgTokenList = new HashSet<>();
    private HashSet<String> watchDiffTokenList = new HashSet<>();
    private HashSet<String> listTokenList = new HashSet<>();
    private HashSet<String> listAvgTokenList = new HashSet<>();
    private HashSet<String> listDiffTokenList = new HashSet<>();

    public ConfigParser() {
        initialTokenDistribution = new HashMap<>();
        places = new HashSet<>();
        immedTranstions = new HashSet<>();
        memoryTransitions = new HashSet<>();
        initDefaultSimulationParams();
    }

    public ConfigParser(String paramFilePath) throws InvalidParamfileException {
        initialTokenDistribution = new HashMap<>();
        places = new HashSet<>();
        immedTranstions = new HashSet<>();
        memoryTransitions = new HashSet<>();
        inputFilePath = paramFilePath;

        try {
            places = readPlaceParams(paramFilePath);
            immedTranstions = readImmediateTransitionParams(paramFilePath);
            memoryTransitions = readMemoryTransitions(paramFilePath);
            readMemoryPolicies(paramFilePath);
            readRunParams(paramFilePath);
        } catch (XPathExpressionException xe) {
            System.out.println("XPathExpressionExcetion thrown!");
            System.out.println(Arrays.toString(xe.getStackTrace()));
            throw new InvalidParamfileException("XPathExpressionExcetion thrown!");
        } catch (SAXException se) {
            System.out.println("SAXException thrown!");
            System.out.println(Arrays.toString(se.getStackTrace()));
            throw new InvalidParamfileException("SAXException thrown!");
        } catch (ParserConfigurationException pe) {
            System.out.println("ParserConfigurationException thrown!");
            System.out.println(Arrays.toString(pe.getStackTrace()));
            throw new InvalidParamfileException("ParserConfigurationException thrown!");
        } catch (IOException ie) {
            System.out.println("IOException thrown!");
            System.out.println(Arrays.toString(ie.getStackTrace()));
            throw new InvalidParamfileException("IOException thrown!");
        }
    }

    private void initTransitionParams(Node transitionNode, XPath xpath, Transition transition) throws XPathExpressionException, InvalidParamfileException {
        XPathExpression transitionInhibitorExpression = xpath.compile(inhibitorEdgeTag);
        NodeList inhibitorNodeList = (NodeList) transitionInhibitorExpression.evaluate(transitionNode, XPathConstants.NODESET);
        for (int j = 0; j < inhibitorNodeList.getLength(); j++) {
            Node inhibitorNode = inhibitorNodeList.item(j);

            String inhibitorName = xpath.evaluate(nodeNameTag, inhibitorNode).toLowerCase();

            Place addPlace = getViablePlace(transition.getInhibitor(), inhibitorName);
            if (addPlace != null) {
                InhibitorEdge inhibitorEdge = new InhibitorEdge(addPlace, transition, readEdgeShape(inhibitorNode, xpath));
                addPlace.addEdge(inhibitorEdge);

                try {
                    inhibitorEdge.setArcWeight(Integer.parseInt(xpath.evaluate(arcWeightTag, inhibitorNode)));
                } catch (NumberFormatException ne) {
                    if (!xpath.evaluate(arcWeightTag, inhibitorNode).equals("")) {
                        throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongArcWeight, transition.getName()));
                    }
                    inhibitorEdge.setArcWeight(1);
                }
                transition.addEdge(inhibitorEdge);
            } else {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongIOPlaceName, transition.getName()));
            }
        }

        XPathExpression transitionInplaceExpression = xpath.compile(inputEdgeTag);
        NodeList inplaceNodeList = (NodeList) transitionInplaceExpression.evaluate(transitionNode, XPathConstants.NODESET);
        for (int j = 0; j < inplaceNodeList.getLength(); j++) {
            Node inplaceNode = inplaceNodeList.item(j);
            String inplaceName = xpath.evaluate(nodeNameTag, inplaceNode).toLowerCase();
            Place addPlace = getViablePlace(transition.getInput(), inplaceName);
            if (addPlace != null) {
                InputEdge inputEdge = new InputEdge(addPlace, transition, readEdgeShape(inplaceNode, xpath));
                addPlace.addEdge(inputEdge);
                try {
                    inputEdge.setArcWeight(Integer.parseInt(xpath.evaluate(arcWeightTag, inplaceNode)));
                } catch (NumberFormatException ne) {
                    if (!xpath.evaluate(arcWeightTag, inplaceNode).equals("")) {
                        throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongArcWeight, transition.getName()));
                    }
                    inputEdge.setArcWeight(1);
                }
                transition.addEdge(inputEdge);
            } else {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongIOPlaceName, transition.getName()));
            }
        }

        XPathExpression transitionOutputPlaceExpression = xpath.compile(outputEdgeTag);
        NodeList outputPlaceNodeList = (NodeList) transitionOutputPlaceExpression.evaluate(transitionNode, XPathConstants.NODESET);
        for (int j = 0; j < outputPlaceNodeList.getLength(); j++) {
            Node outputPlaceNode = outputPlaceNodeList.item(j);
            String outplaceName = xpath.evaluate(nodeNameTag, outputPlaceNode).toLowerCase();

            Place addPlace = getViablePlace(transition.getOutput(), outplaceName);
            if (addPlace != null) {
                OutputEdge outputEdge = new OutputEdge(transition, addPlace, readEdgeShape(outputPlaceNode, xpath));
                addPlace.addEdge(outputEdge);

                try {
                    outputEdge.setArcWeight(Integer.parseInt(xpath.evaluate(arcWeightTag, outputPlaceNode)));
                } catch (NumberFormatException ne) {
                    if (!xpath.evaluate(arcWeightTag, outputPlaceNode).equals("")) {
                        throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongArcWeight, transition.getName()));
                    }
                    outputEdge.setArcWeight(1);
                }
                transition.addEdge(outputEdge);
            } else {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongIOPlaceName, transition.getName()));
            }
        }

        initLocationParams(transitionNode, transition);

    }

    private void initImmediateTransition(Node immediateTransitionNode, XPath xpath, ImmediateTransition immediateTransition) throws XPathExpressionException, InvalidParamfileException {
        initTransitionParams(immediateTransitionNode, xpath, immediateTransition);

        double priority = 1;
        double weight = 1;
        String conString = null;
        try {
            priority = Double.parseDouble(xpath.evaluate(priorityTag, immediateTransitionNode));
            if (priority < 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongPriority, immediateTransition.getName()));
            }
        } catch (Exception e) {
            if (!xpath.evaluate(priorityTag, immediateTransitionNode).equals("")) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongPriority, immediateTransition.getName()));
            }
        }

        try {
            weight = Double.parseDouble(xpath.evaluate(weightTag, immediateTransitionNode));
            if (weight < 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransWeight, immediateTransition.getName()));
            }
        } catch (Exception e) {
            if (!xpath.evaluate(weightTag, immediateTransitionNode).equals("")) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransWeight, immediateTransition.getName()));
            }
        }

        conString = xpath.evaluate(conditionTag, immediateTransitionNode).toLowerCase();
        if (conString.equals("") || conString == null) {
            conString = null;
        } else {
            conString = conString.replace("&lt;", "<").replace("&gt;", ">");
            try {
                ImmediateTransition.parseCondition(conString, this.getPlaces());
                immediateTransition.setConditionString(conString);
            } catch (Exception e) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongFireCondition, immediateTransition.getName()));
            }
        }

        immediateTransition.setPriority(priority);
        immediateTransition.setWeight(weight);

    }

    private void initDefaultSimulationParams() {
        this.mType = MethodType.analysis;
        this.batch = 1000;
        this.maxRelError = 0.1;
        this.alpha = 0.05;
        this.minSampleSize = 30;
        this.warmupLength = 0;
    }

    private void initDeterministicTransition(Node deterministicTransitionNode, XPath xpath, DeterministicTransition deterministicTransition) throws XPathExpressionException, InvalidParamfileException {
        initTransitionParams(deterministicTransitionNode, xpath, deterministicTransition);
        double delay = 0.0;
        try {
            delay = Double.parseDouble(xpath.evaluate("delay", deterministicTransitionNode));
            if (delay <= 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongDelay, deterministicTransition.getName()));
            }
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongDelay, deterministicTransition.getName()));
        }
        deterministicTransition.setConstantGen(delay);
    }

    private void initExponentialTransition(Node exponentialTransitionNode, XPath xpath, ExponentialTransition exponentialTransition) throws XPathExpressionException, InvalidParamfileException {
        initTransitionParams(exponentialTransitionNode, xpath, exponentialTransition);

        double delay = 0.0;
        try {
            delay = Double.parseDouble(xpath.evaluate("delay", exponentialTransitionNode));
            if (delay <= 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongDelay, exponentialTransition.getName()));
            }
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongDelay, exponentialTransition.getName()));
        }
        exponentialTransition.setExpGen(delay);

        ExponentialTransition.ServerType serverType = ExponentialTransition.ServerType.exclusive;
        String serverTypeString = xpath.evaluate("servertype", exponentialTransitionNode);
        switch (serverTypeString) {
            case "infinite":
                serverType = ExponentialTransition.ServerType.infinite;
                break;
            case "exclusive":
                serverType = ExponentialTransition.ServerType.exclusive;
                break;
            case "":
                serverType = ExponentialTransition.ServerType.exclusive;
                break;
            default:
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongServerType, exponentialTransition.getName()));
        }
        exponentialTransition.setsType(serverType);
    }

    private Point2D.Double getNodeLocation(Node petriNetXPathNode) throws XPathExpressionException {
        Point2D.Double shapePosition = new Point2D.Double();
        try {
            String positionStringX = petriNetXPathNode.getAttributes().getNamedItem(shapePosXAttr).getTextContent();
            shapePosition.x = Double.parseDouble(positionStringX);
            String positionStringY = petriNetXPathNode.getAttributes().getNamedItem(shapePosYAttr).getTextContent();
            shapePosition.y = Double.parseDouble(positionStringY);
        } catch (NumberFormatException e) {
            if (petriNetXPathNode.getAttributes().getNamedItem(shapePosXAttr).getTextContent().equals("")) {
                shapePosition.x = 10;
            } else {
                ShowError.showError(ShowError.ErrorType.wrongLocationParam);
            }
            if (petriNetXPathNode.getAttributes().getNamedItem(shapePosXAttr).getTextContent().equals("")) {
                shapePosition.y = 10;
            } else {
                ShowError.showError(ShowError.ErrorType.wrongLocationParam);
            }
        } catch (NullPointerException ne) {
            shapePosition.x = 10;
            shapePosition.y = 10;
        }
        return shapePosition;

    }

    private void initLocationParams(Node petriNetXPathNode, PetriNetNode petriNetNode) throws XPathExpressionException {
        Point2D.Double textPosition = new Point2D.Double();
        String positionStringX = null;
        String positionStringY = null;

        try {
            positionStringX = petriNetXPathNode.getAttributes().getNamedItem(textPosXAttr).getTextContent();
            textPosition.x = Double.parseDouble(positionStringX);
            positionStringY = petriNetXPathNode.getAttributes().getNamedItem(textPosYAttr).getTextContent();
            textPosition.y = Double.parseDouble(positionStringY);
        } catch (NumberFormatException e) {
            textPosition.x = petriNetNode.getInnerShape().getBounds().getCenterX() - petriNetNode.getNodeText().getWidth() / 2;
            textPosition.y = petriNetNode.getInnerShape().getY() + petriNetNode.getInnerShape().getHeight() + 2;
        } catch (NullPointerException ne) {
            textPosition.x = petriNetNode.getInnerShape().getBounds().getCenterX() - petriNetNode.getNodeText().getWidth() / 2;
            textPosition.y = petriNetNode.getInnerShape().getY() + petriNetNode.getInnerShape().getHeight() + 2;
        }
        petriNetNode.getNodeText().setTextNodePosition(textPosition);

    }

    private HashSet<ImmediateTransition> readImmediateTransitionParams(String paramFilePath) throws XPathExpressionException,
            SAXException, IOException, ParserConfigurationException, InvalidParamfileException {
        HashSet<ImmediateTransition> immediateTransitions = new HashSet<>();

        File parameterFile = new File(paramFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parameterFile);
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression readImmediateTransitionExpression = xpath.compile("//" + roolElementTag + "/" + immedTransitionTag);
        NodeList immediateTransitionNodes = (NodeList) readImmediateTransitionExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < immediateTransitionNodes.getLength(); i++) {
            Node immediateTransitionNode = immediateTransitionNodes.item(i);
            String transitionName = xpath.evaluate(nodeNameTag, immediateTransitionNode).toLowerCase();
            if (!isViableTransitionName(transitionName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransName));
            }

            Point2D.Double location = getNodeLocation(immediateTransitionNode);
            ImmediateTransition immediateTransition = new ImmediateTransition(transitionName, location);
            initImmediateTransition(immediateTransitionNode, xpath, immediateTransition);

            immediateTransitions.add(immediateTransition);
        }

        return immediateTransitions;
    }

    private HashSet<Place> readPlaceParams(String paramFilePath) throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException, InvalidParamfileException {

        HashSet<Place> readPlaces = new HashSet<>();;
        File parameterFile = new File(paramFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parameterFile);
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression readPlaceExpression = xpath.compile("//" + roolElementTag + "/" + placeTag);
        NodeList placeNodeList = (NodeList) readPlaceExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < placeNodeList.getLength(); i++) {
            Node placeNode = placeNodeList.item(i);
            int initialToken;
            String placeName = xpath.evaluate(nodeNameTag, placeNode).toLowerCase();
            if (!isViablePlaceName(placeName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongPlaceName, placeName));
            }
            Point2D.Double location = getNodeLocation(placeNode);
            Place addPlace = new Place(placeName, location);

            try {
                initialToken = Integer.parseInt(xpath.evaluate(tokenNumTag, placeNode));
            } catch (NumberFormatException ne) {
                if (!xpath.evaluate(tokenNumTag, placeNode).equals("")) {
                    ShowError.showError(ShowError.ErrorType.wrongTokenNum, placeName);
                }
                initialToken = 0;
            }
            addPlace.setTokenNumber(initialToken);
            initLocationParams(placeNode, addPlace);
            readPlaces.add(addPlace);

        }
        return readPlaces;
    }

    private HashSet<BasicTimedTransition> readMemoryTransitions(String paramFilePath) throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException, InvalidParamfileException {

        HashSet<BasicTimedTransition> readMemoryTransitions = new HashSet<>();
        File parameterFile = new File(paramFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parameterFile);
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression readExponentialTransitionExpression = xpath.compile("//" + roolElementTag + "/" + expTransitionTag);
        NodeList exponentialTransitionNodeList = (NodeList) readExponentialTransitionExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < exponentialTransitionNodeList.getLength(); i++) {
            Node memoryTransitionNode = exponentialTransitionNodeList.item(i);
            String transitionName = xpath.evaluate(nodeNameTag, memoryTransitionNode).toLowerCase();
            if (!isViableTransitionName(transitionName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransName));
            }

            Point2D.Double location = getNodeLocation(memoryTransitionNode);
            ExponentialTransition exponentialTransition = new ExponentialTransition(transitionName, location);
            initExponentialTransition(memoryTransitionNode, xpath, exponentialTransition);
            readMemoryTransitions.add(exponentialTransition);

        }

        XPathExpression readDeterministicTransitionExpression = xpath.compile("//" + roolElementTag + "/" + detTransitionTag);
        NodeList deterministicTransitionNodeList = (NodeList) readDeterministicTransitionExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < deterministicTransitionNodeList.getLength(); i++) {
            Node deterministicTransitionNode = deterministicTransitionNodeList.item(i);

            String transitionName = xpath.evaluate(nodeNameTag, deterministicTransitionNode).toLowerCase();
            if (!isViableTransitionName(transitionName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransName));
            }

            Point2D.Double location = getNodeLocation(deterministicTransitionNode);
            DeterministicTransition deterministicTransition = new DeterministicTransition(transitionName, location);
            initDeterministicTransition(deterministicTransitionNode, xpath, deterministicTransition);
            readMemoryTransitions.add(deterministicTransition);
        }

        XPathExpression readGammaTransitionExpression = xpath.compile("//" + roolElementTag + "/" + gammaTransitionTag);
        NodeList gammaTransitionNodeList = (NodeList) readGammaTransitionExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < gammaTransitionNodeList.getLength(); i++) {
            Node gammaTransitionNode = gammaTransitionNodeList.item(i);
            String transitionName = xpath.evaluate(nodeNameTag, gammaTransitionNode).toLowerCase();
            if (!isViableTransitionName(transitionName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransName));
            }

            Point2D.Double location = getNodeLocation(gammaTransitionNode);
            GammaTransition gammaTransition = new GammaTransition(transitionName, location);
            initGammaTransition(gammaTransitionNode, xpath, gammaTransition);
            readMemoryTransitions.add(gammaTransition);
        }

        XPathExpression readNormalTransitionExpression = xpath.compile("//" + roolElementTag + "/" + normalTransitionTag);
        NodeList normalTransitionNodeList = (NodeList) readNormalTransitionExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < normalTransitionNodeList.getLength(); i++) {
            Node normalTransitionNode = normalTransitionNodeList.item(i);
            String transitionName = xpath.evaluate(nodeNameTag, normalTransitionNode).toLowerCase();
            if (!isViableTransitionName(transitionName)) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTransName));
            }

            Point2D.Double location = getNodeLocation(normalTransitionNode);
            TruncNormalTransition normalTransition = new TruncNormalTransition(transitionName, location);
            initNormalTransition(normalTransitionNode, xpath, normalTransition);
            readMemoryTransitions.add(normalTransition);
        }

        return readMemoryTransitions;

    }

    private void readMemoryPolicies(String paramFilePath) throws XPathExpressionException,
            SAXException, ParserConfigurationException, IOException, InvalidParamfileException {

        File parameterFile = new File(paramFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parameterFile);

        readMemoryPoliciesByTransitionType(immedTransitionTag, doc);
        readMemoryPoliciesByTransitionType(expTransitionTag, doc);
        readMemoryPoliciesByTransitionType(detTransitionTag, doc);
        readMemoryPoliciesByTransitionType(gammaTransitionTag, doc);
        readMemoryPoliciesByTransitionType(normalTransitionTag, doc);
    }

    private void readMemoryPoliciesByTransitionType(String transType, Document doc) throws XPathExpressionException, InvalidParamfileException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression transitionTypeMemoryMatrixExpression = xpath.compile("//" + roolElementTag + "/" + transType);
        NodeList transitionNodeList = (NodeList) transitionTypeMemoryMatrixExpression.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < transitionNodeList.getLength(); i++) {
            Node transitionNode = transitionNodeList.item(i);
            setMemoryPolicyAtTransition(transitionNode, xpath);
        }
    }

    private void setMemoryPolicyAtTransition(Node transitionNode, XPath xpath) throws XPathExpressionException, InvalidParamfileException {

        String transitionName = xpath.evaluate(nodeNameTag, transitionNode).toLowerCase();        
        Transition bossTransition= null;
        for (ImmediateTransition immediateTransition: immedTranstions){
            if (transitionName.equals(immediateTransition.getName())){
                bossTransition=immediateTransition;
            }
        }
        for (BasicTimedTransition basicTimedTransition: memoryTransitions){
            if (transitionName.equals(basicTimedTransition.getName())){
                bossTransition=basicTimedTransition;
            }
        }
                
        XPathExpression memoryTransitionsExpression = xpath.compile(memoryMainTag);
        NodeList memoryTransitionNodeList = (NodeList) memoryTransitionsExpression.evaluate(transitionNode, XPathConstants.NODESET);
        for (int j = 0; j < memoryTransitionNodeList.getLength(); j++) {
            Node memoryTransitionNode = memoryTransitionNodeList.item(j);
            String memoryTransName = xpath.evaluate(nodeNameTag, memoryTransitionNode).toLowerCase();
            
            BasicTimedTransition slaveTransition= null;
            for(BasicTimedTransition basicTimedTransition: memoryTransitions){
                if (basicTimedTransition.getName().equals(memoryTransName)){
                    slaveTransition= basicTimedTransition;
                }
            }
            
            if (slaveTransition!=null) {
                String serverTypeString = xpath.evaluate(memoryPolicyTag, memoryTransitionNode);
                MemoryPolicyAtTransition memoryPolicyAtTransition = null;
                switch (serverTypeString) {
                    case resamplingMemoryTag:
                        memoryPolicyAtTransition = new MemoryPolicyAtTransition(slaveTransition, Transition.MemoryPolicy.resampling);
                        break;
                    case enablingMemoryTag:
                        memoryPolicyAtTransition = new MemoryPolicyAtTransition(slaveTransition, Transition.MemoryPolicy.enablingMemory);
                        break;
                    case ageMemoryTag:
                        memoryPolicyAtTransition = new MemoryPolicyAtTransition(slaveTransition, Transition.MemoryPolicy.ageMemory);
                        break;
                    default:
                        throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongMemoryPolicy, transitionName));
                }
                bossTransition.getMemoryPolicyList().put(slaveTransition, memoryPolicyAtTransition);
            } else {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongMemoryPolicy, transitionName));
            }

        }
        /*
         for (String delayedTransName : memoryTransitions.keySet()) {
         if (!transition.getMemoryPolicyList().containsKey(delayedTransName)) {
         transition.getMemoryPolicyList().put(delayedTransName, Transition.MemoryPolicy.enablingMemory);
         }
         }
         */
    }

    private ArrayList<BreakpointXMLNode> readEdgeShape(Node edgeNode, XPath xpath) throws XPathExpressionException, InvalidParamfileException {
        ArrayList<BreakpointXMLNode> breakpointXMLNodeList = new ArrayList<>();
        Point2D.Double shapePosition = new Point2D.Double();
        int number = 0;
        XPathExpression readBreakpointsExpression = xpath.compile(breakpointTag);
        NodeList breakpointNodeList = (NodeList) readBreakpointsExpression.evaluate(edgeNode, XPathConstants.NODESET);
        for (int i = 0; i < breakpointNodeList.getLength(); i++) {
            shapePosition = new Point2D.Double();
            Node breakpointNode = breakpointNodeList.item(i);
            try {
                number = Integer.parseInt(breakpointNode.getAttributes().getNamedItem(breakpointNumberTag).getTextContent());
            } catch (NumberFormatException nume) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongBreakpointNum));
            } catch (NullPointerException ne) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongBreakpointNum));
            }

            try {
                String positionStringX = breakpointNode.getAttributes().getNamedItem(shapePosXAttr).getTextContent();
                shapePosition.x = Double.parseDouble(positionStringX);
                String positionStringY = breakpointNode.getAttributes().getNamedItem(shapePosYAttr).getTextContent();
                shapePosition.y = Double.parseDouble(positionStringY);
            } catch (NullPointerException ne) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongLocationParam));
            } catch (NumberFormatException nume) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongLocationParam));
            }
            BreakpointXMLNode breakpointXMLNode = new BreakpointXMLNode(number, shapePosition);
            breakpointXMLNodeList.add(breakpointXMLNode);
        }
        Collections.sort(breakpointXMLNodeList);
        return breakpointXMLNodeList;
    }

    private void initGammaTransition(Node gammaTransitionNode, XPath xpath, GammaTransition gammaTransition) throws XPathExpressionException, InvalidParamfileException {
        initTransitionParams(gammaTransitionNode, xpath, gammaTransition);
        double shape = 0.0;
        double rate = 0.0;

        try {
            shape = Double.parseDouble(xpath.evaluate(gammaShapeTag, gammaTransitionNode));
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongGammaParam, gammaTransition.getName()));
        }
        try {
            rate = Double.parseDouble(xpath.evaluate(gammaRateTag, gammaTransitionNode));
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongGammaParam, gammaTransition.getName()));
        }
        gammaTransition.setGammaGen(shape, rate);
    }

    private void initNormalTransition(Node normalTransitionNode, XPath xpath, TruncNormalTransition normalTransition) throws XPathExpressionException, InvalidParamfileException {
        initTransitionParams(normalTransitionNode, xpath, normalTransition);

        double mean = 0.0;
        double variance = 0.0;

        String meanString = xpath.evaluate(normalMeanTag, normalTransitionNode);
        String varianceString = xpath.evaluate(normalVarianceTag, normalTransitionNode);
        try {
            mean = Double.parseDouble(meanString);
            variance = Double.parseDouble(varianceString);

            if (mean < 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongNormDistParam, normalTransition.getName()));
            }
        } catch (Exception e) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongNormDistParam, normalTransition.getName()));
        }
        normalTransition.setNormalGen(mean, variance);
    }

    private void readRunParams(String paramFilePath) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, InvalidParamfileException {
        File parameterFile = new File(paramFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(parameterFile);
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            minSampleSize = Integer.parseInt(xpath.evaluate("//" + simulationParamsTag + "/" + minSampleSizeTag, doc));
        } catch (NumberFormatException ne) {
            if (!xpath.evaluate("//" + simulationParamsTag + "/" + minSampleSizeTag, doc).equals("")) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongMinSampleSize));
            }
            minSampleSize = 30;
        }
        try {
            alpha = 1 - Double.parseDouble(xpath.evaluate("//" + simulationParamsTag + "/" + confidenceLevelTag, doc));
            if (alpha < 0 || alpha > 1) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongAlphaValue));
            }
        } catch (Exception e) {
            if (!xpath.evaluate("//" + simulationParamsTag + "/" + confidenceLevelTag, doc).equals("")) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongAlphaValue));
            } else {
                alpha = 0.5;
            }
        }
        watchAvgTokenList = readTokenList(doc, xpath, watchAvgTokenTag);
        listAvgTokenList = readTokenList(doc, xpath, listAvgTokenTag);
        String methodTypeString = xpath.evaluate("//" + simulationParamsTag + "/" + simulationMethodTag, doc);
        if (methodTypeString.equals("") || methodTypeString.equals(batchMeansMethodTag) || methodTypeString.equals("batch")) {
            mType = MethodType.batchmean;
            watchDiffTokenList = readTokenList(doc, xpath, watchDiffTokenTag);
            listDiffTokenList = readTokenList(doc, xpath, listDiffTokenTag);

            try {
                batch = Integer.parseInt(xpath.evaluate("//" + simulationParamsTag + "/" + batchLengthTag, doc));
            } catch (NumberFormatException ne) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongBatchLength));
            }
            if (watchAvgTokenList.isEmpty() && watchDiffTokenList.isEmpty()) {
                ShowError.showError(ShowError.ErrorType.noWatchAvgOrDiffPlaceAtBatch);
            }
        } else if (methodTypeString.equals(replicationMethodTag)) {
            mType = MethodType.repdel;
            watchTokenList = readTokenList(doc, xpath, watchTokenTag);
            listTokenList = readTokenList(doc, xpath, listTokenTag);

            try {
                terminatingTime = Double.parseDouble(xpath.evaluate("//" + simulationParamsTag + "/" + terminatingTimeTag, doc));
            } catch (NumberFormatException ne) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongTerminatingTime));
            }
            if (watchAvgTokenList.isEmpty() && watchTokenList.isEmpty()) {
                ShowError.showError(ShowError.ErrorType.noWatchTokenOrAvgPlaceAtRepDel);
            }
        } else if (methodTypeString.equals(analysisMethodTag)) {
            mType = MethodType.analysis;
            try {
                batch = Integer.parseInt(xpath.evaluate("//system/batch", doc));
            } catch (NumberFormatException ne) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongBatchLength));
            }
        } else {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongMethodType));
        }
        try {
            warmupLength = Double.parseDouble(xpath.evaluate("//" + simulationParamsTag + "/" + warmupLengthTag, doc));
        } catch (NumberFormatException ne) {
            if (xpath.evaluate("//" + simulationParamsTag + "/" + warmupLengthTag, doc).equals("")) {
                warmupLength = 0;
            } else {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongWarmupLength));
            }
        }
        outFileNamePath = xpath.evaluate("//" + simulationParamsTag + "/" + outfilepathTag, doc);
        if (outFileNamePath.equals("")) {
            outFileNamePath = this.inputFilePath.replace(".xml", ".txt");
        }
        try {
            maxRelError = Double.parseDouble(xpath.evaluate("//" + simulationParamsTag + "/" + maxrelerrorTag, doc));
            if (maxRelError < 0) {
                throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongRelErrorValue));
            }
        } catch (NumberFormatException ne) {
            throw new InvalidParamfileException(ShowError.showError(ShowError.ErrorType.wrongRelErrorValue));
        }
    }

    private HashSet<String> readTokenList(Node doc, XPath xpath, String watchType) throws XPathExpressionException {
        XPathExpression tokenNumExpression = xpath.compile("//" + simulationParamsTag + "/" + watchType);
        NodeList tokenNumNodeList = (NodeList) tokenNumExpression.evaluate(doc, XPathConstants.NODESET);
        HashSet<String> tokenList = new HashSet<>();
        for (int i = 0; i < tokenNumNodeList.getLength(); i++) {
            Node file = tokenNumNodeList.item(i);
            String tokenNumName = file.getTextContent();
            Place existPlace=null;
            for (Place place: places){
                if (place.getName().equals(tokenNumName)){
                    existPlace=place;
                }
            }
            if (existPlace!=null) {
                tokenList.add(tokenNumName);
            } else {
                ShowError.showError(ShowError.ErrorType.wrongTokenPlace, tokenNumName);
            }
        }
        return tokenList;
    }

    public boolean isViablePlaceName(String placeName) {
        if (placeName == null || placeName.equals("")) {
            return false;
        }
        for (Place place : places) {
            if (place.getName().equals(placeName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isViableTransitionName(String transitionName) {
        if (transitionName == null || transitionName.equals("")) {
            return false;
        }
        for (ImmediateTransition immediateTransition: immedTranstions){
            if (immediateTransition.getName().equals(transitionName)){
                return false;
            }
        }
        for (BasicTimedTransition basicTimedTransition: memoryTransitions){
            if (basicTimedTransition.getName().equals(transitionName)){
                return false;
            }
        }
        
        return true;
    }
    

    private Place getViablePlace(HashSet<? extends AbstractEdge> ioList, String placeName) {
        for (AbstractEdge edge : ioList) {
            if (edge.getConnectedPlaceName().equals(placeName)) {
                return null;
            }
        }

        for (Place place: places){
            if (place.getName().equals(placeName)){
                return place;
            }
        }
        return null;        
    }

    public String outPetrinet() {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("PETRI NET:").append(nl).append(nl);
        sb.append("Places:").append(nl);
        sb.append(nl);
        for (Place place : getPlaces()) {
            sb.append(place.toString());
        }
        sb.append(nl);
        sb.append("Transitions:").append(nl);
        sb.append(nl);
        for (ImmediateTransition immediateTransition : getImmedTranstions()) {
            sb.append(immediateTransition.toString());
        }
        sb.append(nl);
        for (BasicTimedTransition memoryTransitionName : getMemoryTransitions()) {
            sb.append(memoryTransitionName.toString());
        }
        sb.append(nl);
        return sb.toString();
    }

    public String outSystemParams(MeasureUnitStatistic mUnitStat) {
        String nl = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("SIMULATION PARAMETERS:").append(nl).append(nl);
        sb.append("Method: ");
        double sumTime = 0.0;
        String numberOfNString = new String();
        if (mType == MethodType.repdel) {
            sb.append("Replication/deletion").append(nl);
            sb.append("Terminating time: ").append(terminatingTime).append(nl);
            numberOfNString = "Number of replications: " + Integer.toString(mUnitStat.numberOfN);
            sumTime = mUnitStat.sumLength;
        } else if (mType == MethodType.batchmean) {
            sb.append("Batch means").append(nl);
            sb.append("Batch length: ").append(batch).append(nl);
            numberOfNString = "Number of batches: " + Integer.toString(mUnitStat.numberOfN);
            sumTime = mUnitStat.sumLength;
        } else if (mType == MethodType.analysis) {
            sb.append("Batch means").append(nl);
            sb.append("Batch length: ").append(batch).append(nl);
            numberOfNString = "Number of batches: " + Integer.toString(mUnitStat.numberOfN);
            sumTime = mUnitStat.sumLength;
        }
        sb.append("Minimal sample size: ").append(minSampleSize).append(nl);
        sb.append("Warmup time: ").append(warmupLength).append(nl);
        sb.append("Accuracy: ").append(maxRelError).append(nl);
        sb.append("Confidencelevel: ").append(1 - alpha).append(nl);
        sb.append("Runtime: ").append(sumTime).append(nl);
        sb.append(numberOfNString).append(nl);
        sb.append("----------------------------------").append(nl);

        return sb.toString();
    }

    public void writeXMLFile(String xmlOutFilePath) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(roolElementTag);
            document.appendChild(rootElement);
            for (Place place : this.getPlaces()) {                
                rootElement.appendChild(place.getXMLNode(document));
            }

            for (ImmediateTransition immedTransition : this.getImmedTranstions()) {
                rootElement.appendChild(immedTransition.getXMLNode(document));
            }

            for (BasicTimedTransition memoryTransition : this.getMemoryTransitions()) {
                rootElement.appendChild(memoryTransition.getXMLNode(document));
            }

            rootElement.appendChild(getXMLNode(document));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();

            FileWriter fileWriter = new FileWriter(xmlOutFilePath);
            PrintWriter out = new PrintWriter(fileWriter);
            out.print(xmlString);
            out.close();
        } catch (IOException ioe) {
            System.out.println("IOException thrown!");
            System.out.println(Arrays.toString(ioe.getStackTrace()));
            System.exit(-1);
        } catch (ParserConfigurationException pe) {
            System.out.println("ParserConfigurationException thrown!");
            System.out.println(Arrays.toString(pe.getStackTrace()));
            System.exit(-1);
        } catch (Exception e) {
            System.out.println("Exception thrown!");
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.exit(-1);
        }

    }

    public Node getXMLNode(Document doc) {
        Element systemNode = doc.createElement(simulationParamsTag);
        Element methodNode = doc.createElement(simulationMethodTag);

        Element batchLengthNode;
        Element terminatingTimeNode;
        switch (this.getmType()) {
            case repdel:
                methodNode.appendChild(doc.createTextNode(replicationMethodTag));

                terminatingTimeNode = doc.createElement(terminatingTimeTag);
                terminatingTimeNode.appendChild(doc.createTextNode(String.valueOf(this.getTerminatingTime())));
                systemNode.appendChild(terminatingTimeNode);

                addListedPlacesToXML(doc, systemNode, listTokenList, listTokenTag);
                addListedPlacesToXML(doc, systemNode, watchTokenList, watchTokenTag);
                addListedPlacesToXML(doc, systemNode, listAvgTokenList, listAvgTokenTag);
                addListedPlacesToXML(doc, systemNode, watchAvgTokenList, watchAvgTokenTag);
                break;
            case batchmean:
                methodNode.appendChild(doc.createTextNode(batchMeansMethodTag));

                batchLengthNode = doc.createElement(batchLengthTag);
                batchLengthNode.appendChild(doc.createTextNode(String.valueOf(this.getBatch())));
                systemNode.appendChild(batchLengthNode);

                addListedPlacesToXML(doc, systemNode, listAvgTokenList, listAvgTokenTag);
                addListedPlacesToXML(doc, systemNode, watchAvgTokenList, watchAvgTokenTag);
                addListedPlacesToXML(doc, systemNode, listDiffTokenList, listDiffTokenTag);
                addListedPlacesToXML(doc, systemNode, watchDiffTokenList, watchDiffTokenTag);
                break;
            case analysis:
                methodNode.appendChild(doc.createTextNode(analysisMethodTag));

                batchLengthNode = doc.createElement(batchLengthTag);
                batchLengthNode.appendChild(doc.createTextNode(String.valueOf(this.getBatch())));
                systemNode.appendChild(batchLengthNode);

                break;
        }
        systemNode.appendChild(methodNode);

        Element minSampleSizeNode = doc.createElement(minSampleSizeTag);
        minSampleSizeNode.appendChild(doc.createTextNode(String.valueOf(minSampleSize)));
        systemNode.appendChild(minSampleSizeNode);

        Element warmupLengthNode = doc.createElement(warmupLengthTag);
        warmupLengthNode.appendChild(doc.createTextNode(String.valueOf(warmupLength)));
        systemNode.appendChild(warmupLengthNode);

        Element confidenceLevelNode = doc.createElement(confidenceLevelTag);
        confidenceLevelNode.appendChild(doc.createTextNode(String.valueOf(1 - alpha)));
        systemNode.appendChild(confidenceLevelNode);

        Element maxrelErrorNode = doc.createElement(maxrelerrorTag);
        maxrelErrorNode.appendChild(doc.createTextNode(String.valueOf(maxRelError)));
        systemNode.appendChild(maxrelErrorNode);

        if (outFileNamePath != null) {
            Element outFileNamePathNode = doc.createElement(outfilepathTag);
            outFileNamePathNode.appendChild(doc.createTextNode(outFileNamePath));
            systemNode.appendChild(outFileNamePathNode);
        }

        return systemNode;
    }

    private void addListedPlacesToXML(Document doc, Node addToSystemNode, HashSet<String> from, String listTypeByTag) {
        for (String palceName : from) {
            Element listTypeNode = doc.createElement(listTypeByTag);
            listTypeNode.appendChild(doc.createTextNode(palceName));
            addToSystemNode.appendChild(listTypeNode);
        }
    }

    public void addPlace(Place place) {
        this.places.add(place);
    }

    public void removePlace(Place place) {
        this.places.remove(place);
        this.listAvgTokenList.remove(place.getName());
        this.listDiffTokenList.remove(place.getName());
        this.listTokenList.remove(place.getName());
        this.watchAvgTokenList.remove(place.getName());
        this.watchDiffTokenList.remove(place.getName());
        this.watchTokenList.remove(place.getName());
    }

    public void addTransition(Transition transition) {
        if (transition instanceof ImmediateTransition) {
            ImmediateTransition immediateTransition = (ImmediateTransition) transition;
            this.immedTranstions.add(immediateTransition);
        } else if (transition instanceof ExponentialTransition) {
            ExponentialTransition exponentialTransition = (ExponentialTransition) transition;
            this.memoryTransitions.add(exponentialTransition);
        } else if (transition instanceof DeterministicTransition) {
            DeterministicTransition deterministicTransition = (DeterministicTransition) transition;
            this.memoryTransitions.add(deterministicTransition);
        } else if (transition instanceof GammaTransition) {
            GammaTransition gammaTransition = (GammaTransition) transition;
            this.memoryTransitions.add(gammaTransition);
        } else if (transition instanceof TruncNormalTransition) {
            TruncNormalTransition truncNormalTransition = (TruncNormalTransition) transition;
            this.memoryTransitions.add(truncNormalTransition);
        }
    }

    public void removeTransition(Transition transition) {
        if (transition instanceof ImmediateTransition) {
            ImmediateTransition immediateTransition=(ImmediateTransition)transition;
            this.immedTranstions.remove(immediateTransition);
        } else if (transition instanceof ExponentialTransition) {
            ExponentialTransition exponentialTransition = (ExponentialTransition) transition;
            this.memoryTransitions.remove(exponentialTransition);
        } else if (transition instanceof DeterministicTransition) {
            DeterministicTransition deterministicTransition = (DeterministicTransition) transition;
            this.memoryTransitions.remove(deterministicTransition);
        } else if (transition instanceof GammaTransition) {
            GammaTransition gammaTransition = (GammaTransition) transition;
            this.memoryTransitions.remove(gammaTransition);
        } else if (transition instanceof TruncNormalTransition) {
            TruncNormalTransition truncNormalTransition = (TruncNormalTransition) transition;
            this.memoryTransitions.remove(truncNormalTransition);
        }
        if (!(transition instanceof ImmediateTransition)) {
            for (ImmediateTransition immediateTransition : immedTranstions) {
                if (transition instanceof BasicTimedTransition) {
                    BasicTimedTransition deletedTransition = (BasicTimedTransition) transition;
                    immediateTransition.removeMemoryPolicyAtFiring(deletedTransition);
                }
            }
            for (BasicTimedTransition memoryTransitionName : memoryTransitions) {
                if (transition instanceof BasicTimedTransition) {
                    BasicTimedTransition deletedTransition = (BasicTimedTransition) transition;
                    memoryTransitionName.removeMemoryPolicyAtFiring(deletedTransition);
                }
            }
        }
    }

    public void resetPlaces() {
        for (Place place : initialTokenDistribution.keySet()) {
            place.setTokenNumber(initialTokenDistribution.get(place));
        }
    }
    

    public HashSet<Place> getPlaces() {
        return places;
    }

    public HashSet<Place> copyPlaces() {
        HashSet<Place> copiedPlaces = new HashSet<>();
        for (Place place : this.places) {
            Place copiedPlace = place.copy();
            copiedPlaces.add(copiedPlace);
        }
        return copiedPlaces;
    }

    public HashSet<ImmediateTransition> getImmedTranstions() {
        return immedTranstions;
    }

    public HashSet<BasicTimedTransition> getMemoryTransitions() {
        return memoryTransitions;
    }

    public MethodType getmType() {
        return mType;
    }

    public double getTerminatingTime() {
        return terminatingTime;
    }

    public double getWarmupLength() {
        return warmupLength;
    }

    public double getAlpha() {
        return alpha;
    }

    public int getMinSampleSize() {
        return minSampleSize;
    }

    public double getMaxRelError() {
        return maxRelError;
    }

    public int getBatch() {
        return batch;
    }

    public String getOutFileNamePath() {
        if (outFileNamePath == null) {
            if (getInputFilePath() != null) {
                return getInputFilePath().replace(".xml", ".txt");
            } else {
                Date now = new Date();
                return System.getProperty("user.dir") + "\\" + now.toString().replace(" ", "").replace(":", "").replace(".", "") + ".txt";
            }
        }
        return outFileNamePath;
    }

    public HashSet<String> getWatchTokenList() {
        return watchTokenList;
    }

    public HashSet<String> getWatchAvgTokenList() {
        return watchAvgTokenList;
    }

    public HashSet<String> getWatchDiffTokenList() {
        return watchDiffTokenList;
    }

    public HashSet<String> getListTokenList() {
        return listTokenList;
    }

    public HashSet<String> getListAvgTokenList() {
        return listAvgTokenList;
    }

    public HashSet<String> getListDiffTokenList() {
        return listDiffTokenList;
    }

    public void setmType(MethodType mType) {
        this.mType = mType;
    }

    public void setTerminatingTime(double terminatingTime) {
        this.terminatingTime = terminatingTime;
    }

    public void setWarmupLength(double warmupLength) {
        this.warmupLength = warmupLength;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setMinSampleSize(int minSampleSize) {
        this.minSampleSize = minSampleSize;
    }

    public void setMaxRelError(double maxRelError) {
        this.maxRelError = maxRelError;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public void setOutFileNamePath(String outFileNamePath) {
        this.outFileNamePath = outFileNamePath;
    }

    public void setWatchTokenList(HashSet<String> watchTokenList) {
        this.watchTokenList = watchTokenList;
    }

    public void setWatchAvgTokenList(HashSet<String> watchAvgTokenList) {
        this.watchAvgTokenList = watchAvgTokenList;
    }

    public void setWatchDiffTokenList(HashSet<String> watchDiffTokenList) {
        this.watchDiffTokenList = watchDiffTokenList;
    }

    public void setListTokenList(HashSet<String> listTokenList) {
        this.listTokenList = listTokenList;
    }

    public void setListAvgTokenList(HashSet<String> listAvgTokenList) {
        this.listAvgTokenList = listAvgTokenList;
    }

    public void setListDiffTokenList(HashSet<String> listDiffTokenList) {
        this.listDiffTokenList = listDiffTokenList;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public boolean isIsErrorFatal() {
        return isErrorFatal;
    }

    public void setIsErrorFatal(boolean isErrorFatal) {
        this.isErrorFatal = isErrorFatal;
    }

    public void updatePlaceNameInWatchLists(String newName, String oldName) {       

        if (watchAvgTokenList.contains(oldName)) {
            watchAvgTokenList.remove(oldName);
            watchAvgTokenList.add(newName);
        }
        if (watchDiffTokenList.contains(oldName)) {
            watchDiffTokenList.remove(oldName);
            watchDiffTokenList.add(newName);
        }
        if (watchTokenList.contains(oldName)) {
            watchTokenList.remove(oldName);
            watchTokenList.add(newName);
        }
        if (listAvgTokenList.contains(oldName)) {
            listAvgTokenList.remove(oldName);
            listAvgTokenList.add(newName);
        }
        if (listDiffTokenList.contains(oldName)) {
            listDiffTokenList.remove(oldName);
            listDiffTokenList.add(newName);
        }
        if (listTokenList.contains(oldName)) {
            listTokenList.remove(oldName);
            listTokenList.add(newName);
        }
    }
    public HashSet<String> getPlacesNames(){
        HashSet<String> placeNames= new HashSet<>();
        for (Place place: places){
            placeNames.add(place.getName());
        }
        return placeNames;
    }
    /*
    public HashMap<ImmediateTransition, ImmediateTransition> copyImmedtransitions(){
        HashMap<ImmediateTransition, ImmediateTransition> copiedImmedtransitions= new HashMap<>();
        for (ImmediateTransition immediateTransition: immedTranstions){
            ImmediateTransition newImmediateTransition= immediateTransition;
            copiedImmedtransitions.put(immediateTransition, immediateTransition);
        }
        return copiedImmedtransitions;
    } 
*/
}
