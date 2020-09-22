import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    //Testing Components
    List<Class<?>> testClasses = new ArrayList<>();
    public static boolean stopButtonPushed = false;
    public static List<Failure> failures = new ArrayList<>();
    public static final String xmlFilePath = "/Users/admin/Desktop/Программирование/ProjectPMT/src/Report.xml";

    //GUI Components
    public JLabel statusLabel = new JLabel();
    public JButton startButton = new JButton("Start");
    public JButton stopButton = new JButton("Stop");
    public JButton exportXMLButton = new JButton("Export to XML");
    public JButton getStatusButton = new JButton("Get Status");
    public JButton exportSQLButton = new JButton("Export to the Database");

    //Tree Structure
    public DefaultTreeModel treeModel;
    public JTree tree;
    public FlexibleTreeNode<MyGuifiable> tnRoot;

    //Database Variables
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
    private static String startSQL;
    private static String finishSQL;
    private static Integer testsSQL = 0;
    private static Integer successSQL = 0;
    private static Integer failSQL = 0;


    /**
     * Inner Class. Represents a Thread, that runs the tests.
     */
    class startButtonThread implements Runnable {

        @Override
        public void run() {
            stopButtonPushed = false;

            JOptionPane.showMessageDialog(null, "Tests are running! It could take a few seconds!");
            while (stopButtonPushed == false) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                rebuildTreeStrukture();
                break;
            }

        }

        public void stop() {
            stopButtonPushed = true;
        }
    }


    /**
     * This method creates the tree, that represents all the TestSammlung classes the all the methods in them.
     * It's the starting position of out app.
     */
    public void buildTreeStructure() {

        //Firstly, we need to add all the classes, that we want to test to a testClasses list. It must be donee before the app starts
        testClasses.add(TestSammlung1.class);
        testClasses.add(TestSammlung2.class);

        //This part of code is responsible for creating the roots with the name of TestSammlung Classes and creating nodes with the name of all tests
        for (Class<?> testClass : testClasses
        ) {

            Method[] methods = testClass.getDeclaredMethods();                  // all methods inside the class
            FlexibleTreeNode<MyGuifiable> testSammlungRoot = new FlexibleTreeNode<>(new MyGuifiable(testClass.getName()));                  //creating the root with the name of a class
            tnRoot.add(testSammlungRoot);                   //adding the root with the name of a TestSammlung class

            for (Method method : methods                    //iterate throug all the methods to get the info about the Annotations.
            ) {
                testsSQL++;                 //Increment the number of tests
                Annotation[] annotations = method.getAnnotations();                 //Save all thr Annotations to the list
                for (Annotation annotation : annotations
                ) {

                    if (annotation.toString().equals("@org.junit.Test(timeout=0, expected=org.junit.Test$None.class)")) {                   //If the method has a "Test" Annotation we add it to the root as a "Test
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("Test: " + method.getName())));                    //adding all methods from the class to the node
                    }
                    if (annotation.toString().equals("@org.junit.Ignore(value=\"\")")) {                    //If the method has a "Ignore" Annotation we add it to the root as a "Ignored"
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("Ignored: " + method.getName())));                    //adding all methods from the class to the node
                    }
                    if (annotation.toString().equals("@org.junit.After()")){
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("'After' Annotation: " + method.getName())));
                    }
                    if (annotation.toString().equals("@org.junit.Before()")){
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("'Before' Annotation: " + method.getName())));
                    }
                    if (annotation.toString().equals("@org.junit.BeforeClass()")){
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("'Before Class' Annotation: " + method.getName())));
                    }
                    if (annotation.toString().equals("@org.junit.AfterClass()")){
                        testSammlungRoot.add(new FlexibleTreeNode<>(new MyGuifiable("'After Class' Annotation: " + method.getName())));
                    }
                }
            }
        }
    }


    /**
     * This method is responsible for the rebuild of the tree. After all test are complete we need to rebuild the tree to show not the content of the classes
     * but the results. Now there are only the fails in the tree.
     */
    public void rebuildTreeStrukture() {

        FlexibleTreeNode<MyGuifiable> resultSammlungRoot = new FlexibleTreeNode<>(new MyGuifiable("Results"));                  //Creating the root "Results"

        //Now we need to clean the old tree to make a new on the same place
        tnRoot.removeFromParent();
        tnRoot.removeAllChildren();

        tnRoot.add(resultSammlungRoot);                 //Adding a new Root to the tree

        for (Class<?> testClass : testClasses
        ) {

            Timestamp timestampStart = new Timestamp(System.currentTimeMillis());                   //Timestamp. Saves the time when the tests are beginning to work

            startSQL = sdf.format(timestampStart);                  //Saving the timestamp to the Database

            FlexibleTreeNode<MyGuifiable> resultSammlung = new FlexibleTreeNode<>(new MyGuifiable(testClass.getName()));                    //Creating a new Node in a tree. This Node represents a Class, that was tested

            resultSammlungRoot.add(resultSammlung);                 //Adding a new Node to a Root

            Result result = JUnitCore.runClasses(testClass);                    //Run the tests


            //First we need to check : if all the test were successful.
            if (result.wasSuccessful()) {                   //If all tests were successful

                FlexibleTreeNode<MyGuifiable> resultOfATest = new FlexibleTreeNode<>(new MyGuifiable("All tests are successfully passed!"));                    //Creating a new Node, that says that all the tests are successfully done

                resultSammlung.add(resultOfATest);                  //Adding a Node to the Tree


            } else {                    // If at least one test failes

                for (Failure failure : result.getFailures()) {                  //We get all the fails form the test result and iterate through them

                    FlexibleTreeNode<MyGuifiable> resultOfATest = new FlexibleTreeNode<>(new MyGuifiable(failure.getDescription().getMethodName() + " FAILED"));                    //Creating a Node with a name of the failed method

                    resultSammlung.add(resultOfATest);                  //Adding a new Node

                    failures.add(failure);                  //Adding an information about a failure to the list of failures

                    failSQL++;                  //Increasing the number of fails -> SQL Report

                }

            }

        }

        Timestamp timestampFinish = new Timestamp(System.currentTimeMillis());                  //Timestamp. Saves the time when the testing is done

        finishSQL = sdf.format(timestampFinish);                    //Saving the timestamp to the Database

        successSQL = testsSQL - failSQL;                    //All tests that didnt fail are successful

        treeModel.reload();                 //We need to make a reload to update an old tree

    }


    public Main(String titel) {

        super(titel);

        //All Info Buttons are disabled at the beginning
        getStatusButton.disable();
        exportSQLButton.disable();
        exportXMLButton.disable();


        tnRoot = new FlexibleTreeNode<MyGuifiable>(new MyGuifiable("TestSammlung Classes") {                    //Creating the Root
        });

        buildTreeStructure();                   //Building the start tree

        //Setting up the Tree
        treeModel = new DefaultTreeModel(tnRoot);
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeView = new JScrollPane(tree);


        //START BUTTON

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startButtonThread startButtonThread = new startButtonThread();
                if (stopButtonPushed == false)
                    startButtonThread.run();
                else startButtonThread.stop();

                getStatusButton.enable();
                exportXMLButton.enable();
                exportSQLButton.enable();

            }
        });


        //STOP BUTTON

        //TODO
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (stopButtonPushed == false)
                    stopButtonPushed = true;
                else
                    stopButtonPushed = false;

            }
        });


        //GET STATUS BUTTON


        getStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                FlexibleTreeNode<MyGuifiable> tnselected = (FlexibleTreeNode<MyGuifiable>) tree.getLastSelectedPathComponent();                 //Getting the last selected Node

                for (Failure failure: failures
                     ) {
                    if (tnselected.toString().substring(0,8).equals(failure.toString().substring(0,8)))
                        statusLabel.setText(failure.toString());                    //Setting the label to show the Fail Info
                }
            }
        });


        //EXPORT TO XML BUTTON


        exportXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = null;
                try {
                    documentBuilder = documentFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                }

                Document document = documentBuilder.newDocument();

                Element root = document.createElement("fails");
                document.appendChild(root);

                for (Failure failure: failures)
                {
                    Element description = document.createElement("Description");
                    description.appendChild(document.createTextNode(failure.toString()));
                    root.appendChild(description);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                try {
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource domSource = new DOMSource(document);
                    StreamResult streamResult = new StreamResult(new File(xmlFilePath));

                    transformer.transform(domSource, streamResult);

                } catch (TransformerConfigurationException e1) {
                    e1.printStackTrace();
                } catch (TransformerException e1) {
                    e1.printStackTrace();
                }
            }
        });


        //EXPORT TO SQL BUTTON


        exportSQLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Database database = new Database();
                database.insert(startSQL,finishSQL,testsSQL,successSQL,failSQL);
            }
        });


        //Adding the elements to the GUI
        add(treeView, BorderLayout.NORTH);
        add(statusLabel);
        add(startButton);
        add(stopButton);
        add(exportXMLButton);
        add(exportSQLButton);
        add(getStatusButton);

        //Setting the GUI
        setLayout(new BorderLayout());
        GridLayout grid = new GridLayout(4, 1);
        setLayout(grid);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(1280, 720);
    }



    public static void main(String[] args) {

        new Main("Test runner");

    }

}


