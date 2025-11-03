package agent;

import game.Engine;

import java.util.ArrayList;
import java.util.Random;

public class Agent {
    ArrayList<Neuron> neurons= new ArrayList<>(0);
    AgentWindow agentWindow;

    public Agent(int agentNumber){
        ArrayList<Integer> connections=new ArrayList<>();
        ArrayList<Float> weights=new ArrayList<>();
        for (int i = 4; i < 20; i++) {
            connections.add(i);
            weights.add(new Random().nextFloat(-1,1));
        }
        for (int i = 0; i < 4; i++) {
            neurons.add(new Neuron(new Random().nextFloat(),
                    connections,
                    weights));
        }
        for (int i = 4; i < 20; i++) {
            neurons.add(new Neuron(new Random().nextFloat(),new ArrayList<>(),new ArrayList<>()));
        }

        agentWindow=new AgentWindow("Agent "+agentNumber);
    }

    public Agent(Agent parent1, Agent parent2){
        if (parent1.neurons==parent2.neurons){
            neurons.addAll(parent1.neurons);
        }else if (parent1.neurons.size()==parent2.neurons.size()){
            for (int i=0;i<parent1.neurons.size();i++){
                neurons.add(new Neuron((parent1.neurons.get(i).bias+parent2.neurons.get(i).bias)/2,
                        parent1.getScore()<parent2.getScore()?parent1.neurons.get(i).connections:parent2.neurons.get(i).connections,
                        parent1.getScore()< parent2.getScore()?parent1.neurons.get(i).weights:parent2.neurons.get(i).weights));
            }
        }

        mutate();
    }

    public void visible(){
        agentWindow.setVisible(false);
    }

    public int getScore(){
        return agentWindow.engine.getPoints();
    }

    private void mutate(){
        if (new Random().nextInt(0,100)<5){
            int rand1=new Random().nextInt(0,neurons.size());
            int rand2=new Random().nextInt(0,neurons.get(rand1).connections.size());
            int rand3=new Random().nextInt(0,neurons.get(rand1).weights.size());
            switch (new Random().nextInt(0,5)){
                case 0:
                    ArrayList<Float> weights=new ArrayList<>();
                    weights.add(1f);
                    ArrayList<Integer> connections=new ArrayList<>();
                    connections.add(neurons.get(rand1).connections.get(rand2));
                    neurons.get(rand1).connections.set(rand2,neurons.size()+1);
                    neurons.add(new Neuron(0,connections,weights));break;
                case 1:
                    neurons.get(rand1).bias+=new Random().nextFloat(-1,1);
                    break;
                case 2:
                    for (int i = 0; i < rand3; i++) {
                        if (new Random().nextInt(0,5)==1) {
                            neurons.get(rand1).weights.set(i, neurons.get(rand1).weights.get(i) + new Random().nextFloat(-0.1f, 0.1f));
                        }
                    }
                    break;
                case 3:
                    int rand4=new Random().nextInt(4,neurons.size());
                    boolean add=false;
                    if (rand4!=rand1){
                        for (int j = 0; j < neurons.get(rand4).connections.size(); j++) {
                            add= rand1 != neurons.get(rand4).connections.get(j);
                        }
                    }
                    if (add) {
                        neurons.get(rand1).connections.add(rand4);
                    }
                    break;
                case 4:
                    for (Neuron neuron : neurons) {
                        for (int j = 0; j < neuron.connections.size(); j++) {
                            if (j == rand2) {
                                neurons.get(rand1).connections.remove(rand2);
                            }
                        }
                    }
            }
        }
    }

    public Engine getEngine(){
        return agentWindow.engine;
    }
}