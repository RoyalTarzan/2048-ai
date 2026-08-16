package src.game;

import src.agent.Agent;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static src.util.ListFunctions.averageScore;

public class Window extends JFrame implements ActionListener {
    private final ArrayList<Float> maxScoreGeneration=new ArrayList<>();
    private final ArrayList<Float> minScoreGeneration=new ArrayList<>();
    private final ArrayList<Float> averageScoreGeneration=new ArrayList<>();
    private final ArrayList<Double> maxScoreAllTime =new ArrayList<>();
    private final JButton printScoresButton=new JButton();
    public Engine engine;
    private final Engine ownEngine;
    private static final JLabel label=new JLabel();
    public JLabel[][] labels;
    public final JLabel points=new JLabel();
    public final JLabel maxScoreLabel =new JLabel();
    public ArrayList<Agent> agents=new ArrayList<>();
    public ArrayList<JButton> buttons=new ArrayList<>();
    public final JButton startButton=new JButton();
    public final JButton resetButton=new JButton();
    public final JButton updateButton=new JButton();
    public final JButton ownEngineButton=new JButton();
    private final JButton autoGen=new JButton();
    private final JButton stopSim=new JButton();
    private FunctionType functionType=FunctionType.CUSTOM;
    private int functionTypeValue=5;
    private int currentAgent;
    private boolean simulationStarted=false;
    private int generations;
    private double maxScore=0;
    private File currentRunFile;
    private int numberOfAgents =10;
    private float populationProportion =0.5f;
    private int numberOfGames =50;
    private final JLabel numberOfAgentsLabel=new JLabel();
    private final JLabel populationProportionLabel=new JLabel();
    private final JLabel numberOfGamesLabel=new JLabel();
    private final JLabel functionTypeValueLabel=new JLabel();
    private final JLabel offsetLabel=new JLabel();
    private final JLabel proportionalLabel=new JLabel();
    private final JLabel minMovesLabel=new JLabel();
    private final JComboBox<String> runSelector=new JComboBox<>();
    private final JComboBox<String> genSelector=new JComboBox<>();
    private final JScrollBar populationProportionBar=new JScrollBar(Adjustable.HORIZONTAL);
    private final Timer autoGenTimer=new Timer(1000,(_)-> new SwingWorker<Void,Void>(){
        @Override
        protected Void doInBackground(){
            autoGenTimer.stop();
            doGeneration();
            return null;
        }
        @Override
        protected void done(){
            if (autoGen.getText().equals("Stop"))autoGenTimer.start();
        }
    }.execute());
    private int offset=10;
    private int proportional=3;
    private int minMoves=50;

    public Window(int size){
        JComboBox<FunctionType> functionTypeSelector = new JComboBox<>();
        for (FunctionType type:FunctionType.values()){
            functionTypeSelector.addItem(type);
        }
        functionTypeSelector.addItemListener(event->{
            if (simulationStarted){return;}
            if (event.getStateChange()==ItemEvent.DESELECTED){return;}
            functionType= (FunctionType) event.getItem();
            functionTypeValueLabel.setText(switch (functionType){
                case LINEAR -> "2";
                case QUADRATIC -> "3";
                case CUBE -> "4";
                case CUSTOM -> String.valueOf(functionTypeValue);
            });
        });
        labels=new JLabel[size][size];
        ownEngine=new Engine(size);
        engine=new Engine(size);
        JScrollBar numberOfAgentsBar = new JScrollBar(Adjustable.HORIZONTAL);
        numberOfAgentsBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            numberOfAgents = event.getValue();
            numberOfAgentsLabel.setText(String.valueOf(numberOfAgents));
        });
        populationProportionBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            populationProportion = (float) event.getValue() /(populationProportionBar.getMaximum()-10);
            populationProportionLabel.setText(String.valueOf(populationProportion));
        });
        JScrollBar numberOfGamesBar = new JScrollBar(Adjustable.HORIZONTAL);
        numberOfGamesBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            numberOfGames = event.getValue() ;
            Agent.setNumberOfGames(numberOfGames);
            numberOfGamesLabel.setText(String.valueOf(numberOfGames));
        });
        JScrollBar functionTypeValueBar = new JScrollBar(Adjustable.HORIZONTAL);
        functionTypeValueBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            if (functionType!=FunctionType.CUSTOM){return;}
            functionTypeValue= event.getValue();
            functionTypeValueLabel.setText(String.valueOf(functionTypeValue));
        });
        JScrollBar offsetBar = new JScrollBar(Adjustable.HORIZONTAL);
        offsetBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            offset= event.getValue();
            offsetLabel.setText(String.valueOf(offset));
        });
        JScrollBar proportionalBar = new JScrollBar(Adjustable.HORIZONTAL);
        proportionalBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            proportional= event.getValue();
            proportionalLabel.setText(String.valueOf(proportional));
        });
        JScrollBar minMovesBar = new JScrollBar(Adjustable.HORIZONTAL);
        minMovesBar.addAdjustmentListener(event->{
            if (simulationStarted){return;}
            minMoves= event.getValue();
            minMovesLabel.setText(String.valueOf(minMoves));
        });
        runSelector.addItemListener(itemEvent->{
            if (itemEvent.getStateChange()== ItemEvent.DESELECTED){genSelector.setVisible(false);return;}
            genSelector.removeAllItems();
            File run=new File(STR."src\\generated\\\{runSelector.getSelectedItem()}");
            File[] generation=run.listFiles();
            assert generation != null;
            for (File generationFile : generation) {
                genSelector.addItem(generationFile.getPath().replace(STR."src\\generated\\\{runSelector.getSelectedItem()}\\",""));
            }
            update(getGraphics());
        });
        File generated=new File("src\\generated");
        if (!generated.mkdir()){
            System.out.println("Shit");
        }
        File[] previousRuns=generated.listFiles();
        assert previousRuns != null;
        for (File previousRun : previousRuns) {
            runSelector.addItem(previousRun.getPath().replace("src\\generated\\",""));
        }

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("2048 AI agent Training");
        this.setBackground(Color.cyan);
        this.setOpacity(1.0f);
        this.setLayout(null);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0),"left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0),"right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),"up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),"down");

        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("left", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {engine.moveLeft();update();System.out.println("left");}});
        actionMap.put("right", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {engine.moveRight();update();System.out.println("right");}});
        actionMap.put("up", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {engine.moveUp();update();System.out.println("up");}});
        actionMap.put("down", new AbstractAction() {@Override public void actionPerformed(ActionEvent e) {engine.moveDown();update();System.out.println("down");}});

        label.setBounds(30,30,400,400);
        label.setBackground(Color.black);
        label.setOpaque(true);
        points.setBounds(30,0,400,30);
        points.setBackground(Color.cyan);
        points.setOpaque(true);
        maxScoreLabel.setBounds(30,430,400,30);
        maxScoreLabel.setBackground(Color.cyan);
        maxScoreLabel.setText("Maximum Score Achieved: 0");
        maxScoreLabel.setOpaque(true);
        startButton.setBounds(230,550,100,30);
        startButton.setText("Start");
        startButton.setToolTipText("Starts a new run/Does a new generation");
        startButton.setVisible(true);
        resetButton.setBounds(130,550,100,30);
        resetButton.setText("Reset");
        resetButton.setToolTipText("Resets the current game to a begin state");
        resetButton.setVisible(true);
        autoGen.setBounds(30,550,100,30);
        autoGen.setText("Auto Gen");
        autoGen.setToolTipText("Enables/Disables automatic running of generations");
        autoGen.setVisible(true);
        stopSim.setBounds(330,520,100,30);
        stopSim.setText("Stop Sim");
        stopSim.setToolTipText("Stops the current run and writes the final generation and scores to files");
        stopSim.setVisible(true);
        updateButton.setBounds(230,520,100,30);
        updateButton.setText("Update");
        updateButton.setToolTipText("Advances the current game 1 step if an agent is selected");
        updateButton.setVisible(true);
        ownEngineButton.setBounds(130,520,100,30);
        ownEngineButton.setText("Own Game");
        ownEngineButton.setToolTipText("Return to your own game so you can play a bit");
        ownEngineButton.setVisible(true);
        printScoresButton.setBounds(30,520,100,30);
        printScoresButton.setText("Print Score");
        printScoresButton.setToolTipText("Prints the current generation and scores to files");
        printScoresButton.setVisible(true);
        runSelector.setBounds(30,460,400,30);
        runSelector.setToolTipText("Select which run you want to continue from");
        runSelector.setVisible(true);
        genSelector.setBounds(30,490,400,30);
        genSelector.setToolTipText("Select which generation you want to continue from");
        genSelector.setVisible(true);
        functionTypeSelector.setBounds(30,640,100,20);
        functionTypeSelector.setVisible(true);
        numberOfAgentsBar.setBlockIncrement(10);
        numberOfAgentsBar.setMinimum(10);
        numberOfAgentsBar.setMaximum(510);
        numberOfAgentsBar.setToolTipText("Number Of Agents Each Generation");
        numberOfAgentsBar.setBounds(30,580,375,20);
        numberOfAgentsBar.setVisible(true);
        numberOfAgentsBar.setValue(100);
        numberOfAgentsLabel.setBounds(405,580,25,20);
        numberOfAgentsLabel.setText(String.valueOf(numberOfAgents));
        numberOfAgentsLabel.setVisible(true);
        populationProportionBar.setBlockIncrement(5);
        populationProportionBar.setMinimum(10);
        populationProportionBar.setMaximum(110);
        populationProportionBar.setToolTipText("Proportion of the population advancing to the next generation");
        populationProportionBar.setBounds(30,600,375,20);
        populationProportionBar.setVisible(true);
        populationProportionBar.setValue(50);
        populationProportionLabel.setBounds(405,600,25,20);
        populationProportionLabel.setText(String.valueOf(populationProportion));
        populationProportionLabel.setVisible(true);
        numberOfGamesBar.setBlockIncrement(5);
        numberOfGamesBar.setMinimum(1);
        numberOfGamesBar.setMaximum(210);
        numberOfGamesBar.setToolTipText("Number of games each agents plays for its fitness");
        numberOfGamesBar.setBounds(30,620,375,20);
        numberOfGamesBar.setVisible(true);
        numberOfGamesBar.setValue(50);
        numberOfGamesLabel.setBounds(405,620,25,20);
        numberOfGamesLabel.setText(String.valueOf(numberOfGames));
        numberOfGamesLabel.setVisible(true);
        functionTypeValueBar.setBlockIncrement(1);
        functionTypeValueBar.setMinimum(5);
        functionTypeValueBar.setMaximum(25);
        functionTypeValueBar.setToolTipText("Changes the bias of selection.\nThe higher the stronger the bias is");
        functionTypeValueBar.setBounds(130,640,275,20);
        functionTypeValueBar.setVisible(true);
        functionTypeValueBar.setValue(5);
        functionTypeValueLabel.setBounds(405,640,25,20);
        functionTypeValueLabel.setText(String.valueOf(functionTypeValue));
        functionTypeValueLabel.setVisible(true);
        offsetBar.setBlockIncrement(10);
        offsetBar.setMinimum(0);
        offsetBar.setMaximum(110);
        offsetBar.setToolTipText("Changes the offset of the maximum amount of moves an agent can make each game");
        offsetBar.setBounds(30,660,375,20);
        offsetBar.setVisible(true);
        offsetBar.setValue(10);
        offsetLabel.setBounds(405,660,25,20);
        offsetLabel.setText(String.valueOf(offset));
        offsetLabel.setVisible(true);
        proportionalBar.setBlockIncrement(1);
        proportionalBar.setMinimum(1);
        proportionalBar.setMaximum(20);
        proportionalBar.setToolTipText("Changes the amount the number of generations influences the amount of moves that can be made. The higher this number is the lower the impact.");
        proportionalBar.setBounds(30,680,375,20);
        proportionalBar.setVisible(true);
        proportionalBar.setValue(3);
        proportionalLabel.setBounds(405,680,25,20);
        proportionalLabel.setText(String.valueOf(proportional));
        proportionalLabel.setVisible(true);
        minMovesBar.setBlockIncrement(10);
        minMovesBar.setMinimum(10);
        minMovesBar.setMaximum(210);
        minMovesBar.setToolTipText("Changes the minimum amount of moves an agent can do");
        minMovesBar.setBounds(30,700,375,20);
        minMovesBar.setVisible(true);
        minMovesBar.setValue(50);
        minMovesLabel.setBounds(405,700,25,20);
        minMovesLabel.setText(String.valueOf(minMoves));
        minMovesLabel.setVisible(true);

        for (int i = 0; i < engine.size; i++) {
            for (int j = 0; j < engine.size; j++) {
                labels[i][j]=new JLabel();
                labels[i][j].setBackground(Color.white);
                labels[i][j].setOpaque(true);
                labels[i][j].setBounds(5+i*(400/ engine.size),5+j*(400/ engine.size),(400/ engine.size)-10,(400/ engine.size)-10);
                labels[i][j].setHorizontalTextPosition(SwingConstants.CENTER);
                labels[i][j].setVerticalTextPosition(SwingConstants.CENTER);
                label.add(labels[i][j]);
            }
        }
        this.add(points);
        this.add(maxScoreLabel);
        startButton.addActionListener(this);
        resetButton.addActionListener(this);
        updateButton.addActionListener(this);
        ownEngineButton.addActionListener(this);
        autoGen.addActionListener(this);
        stopSim.addActionListener(this);
        printScoresButton.addActionListener(this);
        this.add(label);
        this.setVisible(true);
        this.add(resetButton);
        this.add(startButton);
        this.add(updateButton);
        this.add(ownEngineButton);
        this.add(autoGen);
        this.add(stopSim);
        this.add(runSelector);
        this.add(genSelector);
        this.add(printScoresButton);
        this.add(numberOfAgentsBar);
        this.add(numberOfAgentsLabel);
        this.add(populationProportionBar);
        this.add(populationProportionLabel);
        this.add(numberOfGamesBar);
        this.add(numberOfGamesLabel);
        this.add(functionTypeValueBar);
        this.add(functionTypeValueLabel);
        this.add(functionTypeSelector);
        this.add(minMovesLabel);
        this.add(minMovesBar);
        this.add(proportionalLabel);
        this.add(proportionalBar);
        this.add(offsetLabel);
        this.add(offsetBar);
        this.engine=ownEngine;
        update();
    }

    public void update(){
        int[][] board=engine.getBoard();
        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length; j++) {
                labels[i][j].setText(String.valueOf(board[j][i]));
            }
        }
        points.setText(String.valueOf(engine.getPoints()));
        if (engine.lose()){
            points.setText(STR."\{points.getText()}You Lost!");
        }
    }

    private void newGeneration(){
        int populationProportion= (int) (agents.size()*this.populationProportion);
        while(agents.size()>populationProportion){
            agents.remove(biasedRandInt((int)(populationProportion*0.8),agents.size()));
        }
        while (agents.size()< numberOfAgents){
            agents.add(new Agent(agents.get(biasedRandInt(populationProportion,0))));
        }
    }

    private void doGeneration() {
        int moves=Math.max(generations/proportional+offset, minMoves);
        if (!simulationStarted){startSimulation();}
        System.out.println(STR."Started generation \{generations+1}");
        agents.parallelStream().forEach(agent -> agent.calculateScore(moves));
        agents.sort(Comparator.comparingDouble(agent -> agent.score*-1));
        for (Agent agent:agents){
            if (agents.indexOf(agent)>4 && agents.indexOf(agent)<((agents.size()/2)-3)){continue;}
            if (agents.indexOf(agent)<(agents.size()-5) && agents.indexOf(agent)>((agents.size()/2)+1)){continue;}
            System.out.println(STR."\{agents.indexOf(agent) + 1} \{agent.score}");
        }
        if (generations%50==49){
            agentsToFile();
        }
        maxScoreGeneration.add(agents.getFirst().score);
        minScoreGeneration.add(agents.getLast().score);
        averageScoreGeneration.add(averageScore(agents));
        if (agents.getFirst().score>maxScore){
            maxScore=agents.getFirst().score;
        }
        maxScoreAllTime.add(maxScore);
        maxScoreLabel.setText(STR."Maximum Score Achieved: \{(float)maxScore}, Moves: \{moves}");
        engine=agents.getFirst().getEngine();
        update();
        newGeneration();
        generations++;
    }

    private void startSimulation(){
        int usableHeight= ((getHeight()-50)/25)*25;
        int usableWidth=((getWidth())/100)*100;
        for (int i = 0; i < numberOfAgents; i++) {
            agents.add(new Agent(engine.size));
            int finalI = i;
            buttons.add(new JButton());
            buttons.get(i).addActionListener((_)->{
                engine=agents.get(finalI).getEngine();
                update();
                currentAgent=finalI;
            });
            buttons.get(i).setText(STR."Agent \{finalI + 1}");
            buttons.get(i).setBounds((430+(i/(usableHeight/25))*100)%usableWidth,(i*25)%usableHeight,100,25);
            add(buttons.get(i));
        }
        this.requestFocus();
        update(this.getGraphics());
        simulationStarted=true;
        startButton.setText("Next Gen");
        String string= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        string=string.replace(" ","_").replace(":","-");
        string=string.strip();
        currentRunFile=new File(STR."src\\generated\\\{string}");
        if(!currentRunFile.mkdir()){
            System.out.println("Shit");
        }
        System.out.println(currentRunFile.getPath());
        agentsToFile();
    }

    private void printScores(){
        File runScores=new File(currentRunFile,"runScores.txt");
        try(FileWriter fileWriter=new FileWriter(runScores.getAbsoluteFile(),true)){
            fileWriter.append(STR."Scores at generation \{generations+1}\n");
            fileWriter.append(STR."\{maxScoreGeneration.toString()}\n");
            fileWriter.append(STR."\{minScoreGeneration.toString()}\n");
            fileWriter.append(STR."\{averageScoreGeneration.toString()}\n");
            fileWriter.append(STR."\{maxScoreAllTime.toString()}\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopSimulation() throws IOException {
        if (simulationStarted) simulationStarted = false;
        agentsToFile();
        printScores();
        agents.clear();
        runSelector.addItem(currentRunFile.getPath().replace("src\\generated\\",""));
        maxScoreGeneration.clear();
        minScoreGeneration.clear();
        averageScoreGeneration.clear();
        maxScoreAllTime.clear();
        maxScore=0;
        maxScoreLabel.setText("Maximum Score Achieved: 0");
        generations=0;
        startButton.setText("Start");
    }

    private void agentsToFile(){
        File generationFile=new File(currentRunFile,STR."generation_\{generations+1}");
        if(!generationFile.mkdir()){
            System.out.println("Shit");
        }
        try {
            for (Agent agent:agents){
                File agentFile=new File(generationFile, STR."agent_\{agents.indexOf(agent) + 1}.json");
                if(agentFile.createNewFile()){
                    try(FileWriter fileWriter=new FileWriter(agentFile.getAbsolutePath())){
                        fileWriter.append(agent.toString(0));}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==startButton){
            if (!simulationStarted){
                startSimulation();
            }else {
                doGeneration();
            }
        } else if (e.getSource()==resetButton) {
            engine.reset();
        } else if (e.getSource()==updateButton) {
            if (engine==ownEngine){update();return;}
            agents.get(currentAgent).setEngine(engine);
            agents.get(currentAgent).outputMove();
            System.out.println(agents.get(currentAgent).lastMove);
            update();
        } else if (e.getSource()==ownEngineButton){
            engine=ownEngine;
            updateButton.addActionListener((_)->update());
        } else if (e.getSource()==autoGen) {
            if (autoGenTimer.isRunning()){
                autoGenTimer.stop();
                autoGen.setText("Auto Gen");
            }else {
                autoGenTimer.start();
                autoGen.setText("Stop");
            }
        } else if (e.getSource()==stopSim) {
            try {
                if (autoGenTimer.isRunning()){
                    autoGenTimer.stop();
                    autoGen.setText("Auto Gen");
                }
                stopSimulation();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource()==printScoresButton) {
            agentsToFile();
            printScores();
        }else {
            System.out.println(e.paramString());
        }
    }

    private int biasedRandInt(int start,int end){
        int dif=end-start;
        int div=switch (functionType){
            case LINEAR -> 2;
            case QUADRATIC -> 3;
            case CUBE -> 4;
            case CUSTOM -> functionTypeValue;
        };
        return (int) (start+dif*Math.pow((new Random().nextFloat(0,1)), (double) 1 /div));
    }

    private enum FunctionType{
        LINEAR,
        QUADRATIC,
        CUBE,
        CUSTOM
    }
}
