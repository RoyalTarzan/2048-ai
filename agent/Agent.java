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

    public void outputMove(){
        int biggest=0;
        int currentNeuron = 0;
        for (int i = 0; i < 4; i++) {
            if (neurons.get(i).value > biggest){
                currentNeuron=i;
            }
        }
        switch (currentNeuron){
            case 0:getEngine().moveLeft();
            case 1:getEngine().moveUp();
            case 2:getEngine().moveRight();
            case 3:getEngine().moveDown();
        }
    }

    public void visible(){
        agentWindow.setVisible(!agentWindow.isVisible());
    }

    public int getScore(){
        return agentWindow.engine.getPoints();
    }

    private void mutate(){
        if (new Random().nextInt(0,100)<5){
            int randNeuron=new Random().nextInt(0,neurons.size());
            int randConnection=new Random().nextInt(0,neurons.get(randNeuron).connections.size());
            int randWeight=new Random().nextInt(0,neurons.get(randNeuron).weights.size());
            switch (new Random().nextInt(0,5)){
                case 0:
                    //Adds new neuron
                    ArrayList<Float> weights=new ArrayList<>();
                    weights.add(1f);
                    ArrayList<Integer> connections=new ArrayList<>();
                    connections.add(neurons.get(randNeuron).connections.get(randConnection));
                    neurons.get(randNeuron).connections.set(randConnection,neurons.size()+1);
                    neurons.add(new Neuron(0,connections,weights));break;
                case 1:
                    //changes bias
                    neurons.get(randNeuron).bias+=new Random().nextFloat(-1,1);
                    break;
                case 2:
                    //Changes 1 weight
                    neurons.get(randNeuron).weights.set(randWeight, neurons.get(randNeuron).weights.get(randWeight) + new Random().nextFloat(-0.1f, 0.1f));
                    break;
                case 3:
                    //Add connection
                    int randNeuron2=new Random().nextInt(4,neurons.size());
                    if (randNeuron2!=randNeuron){
                        for (int j = 0; j < neurons.get(randNeuron2).connections.size(); j++) {
                            if (randNeuron == neurons.get(randNeuron2).connections.get(j)){
                                return;
                            }
                        }
                    }
                    neurons.get(randNeuron).connections.add(randNeuron2);
                    break;
                case 4:
                    //Remove neuron
                    if (neurons.size()<=20){return;}
                    for (Neuron neuron:neurons){
                        if (neuron!=neurons.get(randNeuron)){
                            for (int connection: neuron.connections){
                                if (neuron.connections.get(connection)==randNeuron){
                                    neuron.connections.remove(connection);
                                    neuron.weights.remove(connection);
                                }
                            }
                        }
                    }
                    neurons.remove(randNeuron);
            }
        }
    }

    public Engine getEngine(){
        return agentWindow.engine;
    }
}