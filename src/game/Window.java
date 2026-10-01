package src.game;

import src.agent.Agent;
import src.agent.Neuron;
import src.util.LineGraph;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public final JButton startButton=new JButton();
    public final JButton resetButton=new JButton();
    public final JButton updateButton=new JButton();
    public final JButton ownEngineButton=new JButton();
    private final JButton autoGen=new JButton();
    private final JButton stopSim=new JButton();
    private final JButton switchRepopulation=new JButton();
    private boolean sexualRepopulation=true;
    private FunctionType functionType=FunctionType.CUSTOM;
    private int functionTypeValue=5;
    private int currentAgent;
    private boolean simulationStarted=false;
    public int generations;
    private double maxScore=0;
    private File currentRunFile;
    private int numberOfAgents =10;
    private float populationProportion =0.5f;
    private final JLabel numberOfAgentsLabel=new JLabel();
    private final JLabel populationProportionLabel=new JLabel();
    private final JLabel numberOfGamesLabel=new JLabel();
    private final JLabel functionTypeValueLabel=new JLabel();
    private final JLabel offsetLabel=new JLabel();
    private final JLabel proportionalLabel=new JLabel();
    private final JLabel minMovesLabel=new JLabel();
    private final JComboBox<String> runSelector=new JComboBox<>();
    private final JComboBox<Agent> agentSelector=new JComboBox<>();
    private final JComboBox<String> genSelector=new JComboBox<>();
    private final JScrollBar populationProportionBar=new JScrollBar(Adjustable.HORIZONTAL);
    private final Timer autoGenTimer=new Timer(1000,(_)-> new SwingWorker<Void, Void>(){
        @Override
        protected Void doInBackground(){
            if (generating){return null;}
            generating=true;
            doGeneration();
            return null;
        }
        @Override
        protected void done(){
            generating=false;
            try {
                get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            if (stopRequested){
                stopRequested=false;
                try {
                    stopSimulation();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (autoGenRunning) {
                autoGenTimer.restart();
            }
        }
    }.execute());
    private int offset=10;
    private int proportional=3;
    private int minMoves=50;
    private final LineGraph graph=new LineGraph();
    private boolean autoGenRunning=false;
    private boolean stopRequested=false;
    private boolean generating=false;
    private int additionalMoves=0;
    private final JPanel neuralNetVis=new JPanel(){
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintNeuralNet();
            Window.this.update();
        }
    };

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
            int numberOfGames = event.getValue();
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
            if (itemEvent.getStateChange()== ItemEvent.DESELECTED){return;}
            genSelector.removeAllItems();
            File run=new File("src\\generated\\"+runSelector.getSelectedItem());
            File[] generation=run.listFiles();
            assert generation != null;
            for (File generationFile : generation) {
                genSelector.addItem(generationFile.getPath().replace("src\\generated\\"+runSelector.getSelectedItem()+"\\",""));
            }
            update(getGraphics());
        });
        agentSelector.addItemListener(_->{
            if (agentSelector.getSelectedItem() == null) {
                engine = ownEngine;
                currentAgent=0;
            } else {
                engine = ((Agent) (agentSelector.getSelectedItem())).getEngine();
                currentAgent=agents.indexOf((Agent) agentSelector.getSelectedItem());
                neuralNetVis.update(neuralNetVis.getGraphics());
            }
            update();
        });
        File generated=new File("src\\generated");
        if (!generated.mkdir()){
            System.out.println("Shit1");
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
        this.setSize(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width,1205), Math.min(Toolkit.getDefaultToolkit().getScreenSize().height, 955));
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
        add(startButton,"Starts a new run/Does a new generation",230,550,100,30,"Start");
        add(resetButton,"Resets the current game to a begin state",130,550,100,30,"Reset");
        add(autoGen,"Enables/Disables automatic running of generations",30,550,100,30,"Auto Gen");
        add(stopSim,"Stops the current run and writes the final generation and scores to files",330,520,100,30,"Stop Sim");
        add(switchRepopulation,"Switches what type of repopulation is used: sexual (2 parents) or non-sexual(1 parent)",330,550,100,30,"Sexual");
        add(updateButton,"Advances the current game 1 step if an agent is selected",230,520,100,30,"Update");
        add(ownEngineButton,"Return to your own game so you can play a bit",130,520,100,30,"Own Game");
        add(printScoresButton,"Prints the current generation and scores to files",30,520,100,30,"Print Score");
        add(functionTypeSelector,null,30,640,100,20,null);
        barStuff(numberOfAgentsBar,10,10,510,100);
        add(runSelector,"Select which run you want to continue from",30,460,400,30,null);
        add(genSelector,"Select which generation you want to continue from",30,490,400,30,null);
        add(numberOfAgentsBar,"Number Of Agents Each Generation",30,580,375,20,null);
        add(numberOfAgentsLabel,null,405,580,25,20,String.valueOf(numberOfAgents));
        barStuff(populationProportionBar,5,10,110,50);
        add(populationProportionBar,"Proportion of the population advancing to the next generation",30,600,375,20,null);
        add(populationProportionLabel,null,405,600,25,20,String.valueOf(populationProportion));
        barStuff(numberOfGamesBar,5,1,210,50);
        add(numberOfGamesBar,"Number of games each agents plays for its fitness",30,620,375,20,null);
        add(numberOfGamesLabel,null,405,620,25,20,String.valueOf(numberOfGamesBar.getValue()));
        barStuff(functionTypeValueBar,1,5,25,5);
        add(functionTypeValueBar,"Changes the bias of selection.\nThe higher the stronger the bias is",130,640,275,20,null);
        add(functionTypeValueLabel,null,405,640,25,20,String.valueOf(functionTypeValue));
        barStuff(offsetBar,10,0,110,10);
        add(offsetBar,"Changes the offset of the maximum amount of moves an agent can make each game",30,660,375,20,null);
        add(offsetLabel,null,405,660,25,20,String.valueOf(offset));
        barStuff(proportionalBar,1,1,20,3);
        add(proportionalBar,"Changes the amount the number of generations influences the amount of moves that can be made. The higher this number is the lower the impact.",
                30,680,375,20,null);
        add(proportionalLabel,null,405,680,25,20, String.valueOf(proportional));
        barStuff(minMovesBar,10,10,210,50);
        add(minMovesBar,"Changes the minimum amount of moves an agent can do",30,700,375,20,null);
        add(minMovesLabel,null,405,700,25,20,String.valueOf(minMoves));
        add(agentSelector,"Selects an agents to be shown and inspected",30,720,400,20,null);
        add(graph,null,430,10,getWidth()-450,500,null);
        add(neuralNetVis,null,430,520,getWidth()-450,this.getHeight()-graph.getHeight()-20,null);
        autoGenTimer.setRepeats(false);

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
        updateButton.addActionListener((_)->update());
        ownEngineButton.addActionListener(this);
        autoGen.addActionListener(this);
        stopSim.addActionListener(this);
        printScoresButton.addActionListener(this);
        switchRepopulation.addActionListener(this);
        this.add(label);
        this.setVisible(true);
        this.engine=ownEngine;
        update();
    }

    private static void barStuff(JScrollBar bar,int increment,int min,int max,int start) {
        bar.setBlockIncrement(increment);
        bar.setMinimum(min);
        bar.setMaximum(max);
        bar.setValue(start);
    }

    private void add(JComponent comp,String toolTip,int xStart,int yStart,int width,int height,String text){
        super.add(comp);
        if (toolTip!=null) comp.setToolTipText(toolTip);
        if(text!=null) {
            if (comp instanceof JButton) {
                ((JButton) comp).setText(text);
            } else if(comp instanceof JLabel){
                ((JLabel) comp).setText(text);
            }
        }
        comp.setBounds(xStart,yStart,width,height);
        comp.setVisible(true);
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
            points.setText(points.getText()+" You Lost!");
        }
    }

    private void newGeneration(){
        int populationProportion= (int) (agents.size()*this.populationProportion);
        while(agents.size()>populationProportion){
            agents.remove(biasedRandInt((int)(populationProportion*0.8),agents.size()));
        }
        while (agents.size()< numberOfAgents){
            if (sexualRepopulation){
                agents.add(new Agent(agents.get(biasedRandInt(populationProportion,0)),agents.get(biasedRandInt(populationProportion,0)),this));
            }else agents.add(new Agent(agents.get(biasedRandInt(populationProportion,0)),this));
        }
    }

    private void doGeneration() {
        int moves=Math.max(generations/proportional+offset, minMoves+additionalMoves);
        if (!simulationStarted){startSimulation();}
        System.out.println("Started generation "+(generations+1));
        AtomicBoolean reachedMaxMoves= new AtomicBoolean(false);
        agents.parallelStream().forEach(agent -> {
            if (agent.calculateScore(moves)) {
                reachedMaxMoves.set(true);
            }
        });
        if (reachedMaxMoves.get()){
            additionalMoves++;
        }
        agents.sort(Comparator.comparingDouble(agent -> agent.score*-1));
        for (Agent agent:agents){
            if (agents.indexOf(agent)>4 && agents.indexOf(agent)<((agents.size()/2)-3)){continue;}
            if (agents.indexOf(agent)<(agents.size()-5) && agents.indexOf(agent)>((agents.size()/2)+1)){continue;}
            System.out.println((agents.indexOf(agent) + 1)+" "+agent.score);
        }
        if (generations%50==49){
            agentsToFile();
        }
        double[] scores=new double[3];
        maxScoreGeneration.add(agents.getFirst().score);
        scores[0]=agents.getFirst().score;
        minScoreGeneration.add(agents.getLast().score);
        scores[1]=agents.getLast().score;
        averageScoreGeneration.add(averageScore(agents));
        scores[2]=averageScore(agents);
        if (agents.getFirst().score>maxScore){
            maxScore=agents.getFirst().score;
        }
        maxScoreAllTime.add(maxScore);
        SwingUtilities.invokeLater(()->{
            graph.addDataPoint(scores);
            maxScoreLabel.setText("Maximum Score Achieved:"+ (float)maxScore+", Moves: "+moves);
            update();
        });
        agentSelector.removeAllItems();
        for (Agent agent:agents){
            agentSelector.addItem(agent);
        }
        engine=agents.getFirst().getEngine();
        newGeneration();
        generations++;
    }

    private void startSimulation(){
        for (int i = 0; i < numberOfAgents; i++) {
            agents.add(new Agent(engine.size,i));
            agentSelector.addItem(agents.getLast());
        }
        this.requestFocus();
        update(this.getGraphics());
        simulationStarted=true;
        startButton.setText("Next Gen");
        String string= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        string=string.replace(" ","_").replace(":","-");
        string=string.strip();
        currentRunFile=new File("src\\generated\\"+string);
        if(!currentRunFile.mkdir()){
            System.out.println("Shit2");
        }
        graph.clearData();
        System.out.println(currentRunFile.getPath());
        agentsToFile();
    }

    private void printScores(){
        File runScores=new File(currentRunFile,"runScores.txt");
        try(FileWriter fileWriter=new FileWriter(runScores.getAbsoluteFile(),true)){
            fileWriter.append("Scores at generation").append(String.valueOf(generations + 1)).append("\n");
            fileWriter.append(maxScoreGeneration.toString()).append("\n");
            fileWriter.append(minScoreGeneration.toString()).append("\n");
            fileWriter.append(averageScoreGeneration.toString()).append("\n");
            fileWriter.append(maxScoreAllTime.toString()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopSimulation() throws IOException {
        if (simulationStarted) simulationStarted = false;
        agentsToFile();
        printScores();
        agents.clear();
        autoGenTimer.stop();
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
        File generationFile=new File(currentRunFile,"generation_"+(generations+1));
        if(!generationFile.mkdir()){
            System.out.println("Shit3");
        }
        try {
            for (Agent agent:agents){
                File agentFile=new File(generationFile, "agent_"+(agents.indexOf(agent) + 1)+".json");
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
                if (generating)return;
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
        } else if (e.getSource()==autoGen) {
            if (autoGenRunning){
                autoGenTimer.stop();
                autoGenRunning=false;
                autoGen.setText("Auto Gen");
            }else {
                autoGenRunning=true;
                autoGen.setText("Stop");
                autoGenTimer.restart();
            }
        } else if (e.getSource()==stopSim) {
            autoGenRunning=false;
            autoGenTimer.stop();
            autoGen.setText("Auto Gen");
            if (generating){
                stopRequested=true;
            }else {
                try {
                    stopSimulation();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSource()==printScoresButton) {
            agentsToFile();
            printScores();
        } else if (e.getSource()==switchRepopulation) {
            sexualRepopulation=!sexualRepopulation;
            switchRepopulation.setText(sexualRepopulation?"Sexual":"non-Sexual");
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

    private void paintNeuralNet(){
        if (agentSelector.getSelectedItem()==null)return;
        Agent agent= (Agent) agentSelector.getSelectedItem();

        Map<String,Integer[]> locations=new HashMap<>();
        ArrayList<ArrayList<String>> sortedNeurons=agent.sortedNeurons;
        Graphics2D g2= (Graphics2D) neuralNetVis.getGraphics();

        int xSpacing=(neuralNetVis.getWidth())/sortedNeurons.size();
        int x=xSpacing/2;
        for (ArrayList<String> sortedNeuronLayer : sortedNeurons) {
            int ySpacing=(neuralNetVis.getHeight()-20)/sortedNeuronLayer.size();
            int y=ySpacing/2;
            for (String sortedNeuron : sortedNeuronLayer) {
                locations.put(sortedNeuron,new Integer[]{x,y});
                y+=ySpacing;
            }
            x+=xSpacing;
        }
        //Draws Connections
        for (int i=1;i<sortedNeurons.size();i++){
            for (String s : sortedNeurons.get(i)) {
                Neuron neuron= agent.getWithId(s);
                neuron.getConnIn().forEach((id,weight)->{
                    g2.setColor((weight<0?Color.RED:Color.GREEN));
                    g2.setStroke(new BasicStroke(5*Math.abs(weight)));
                    g2.drawLine(locations.get(id)[0],locations.get(id)[1],locations.get(s)[0],locations.get(s)[1]);
                });
            }
        }
        //Draws circles for each Neuron
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        for (Integer[] value : locations.values()) {
            g2.fillOval(value[0]-5,value[1]-5,10,10);
        }

    }
}
