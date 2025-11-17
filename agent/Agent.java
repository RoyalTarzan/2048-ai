package agent;

import game.Engine;

import java.util.ArrayList;
import java.util.Random;

public class Agent {
    ArrayList<Neuron> neurons= new ArrayList<>(0);
    Engine engine=new Engine();
    ArrayList<Neuron> sortedNeurons=new ArrayList<>();

    public Agent(){
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

    private void sortNeurons(){
        ArrayList<Neuron> copyOfneurons= (ArrayList<Neuron>) neurons.clone();
        sortedNeurons.clear();
        for (Neuron neuron:copyOfneurons){
            if (!neuron.connections.equals(new ArrayList<Integer>())){continue;}
            sortedNeurons.add(neuron);
            for (Neuron neuron1:copyOfneurons){
                if (neuron==neuron1){continue;}
                for (int connection:neuron1.connections){
                    if (connection!=copyOfneurons.indexOf(neuron)){continue;}
                    neuron1.connections.remove((Integer) connection);
                }
            }
        }
    }

    public void outputMove(){
        int biggest=Integer.MIN_VALUE;
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

    public int getScore(){
        return engine.getPoints();
    }

    private void mutate(){
        if (new Random().nextInt(0,100)>5){return;}
        int randNeuronIndex=new Random().nextInt(0,neurons.size());
        int randConnectionIndex=new Random().nextInt(0,neurons.get(randNeuronIndex).connections.size());
        int randWeightIndex=new Random().nextInt(0,neurons.get(randNeuronIndex).weights.size());
        switch (new Random().nextInt(0,5)){
            case 0:
                //Adds new neuron
                ArrayList<Float> weights=new ArrayList<>();
                weights.add(1f);
                ArrayList<Integer> connections=new ArrayList<>();
                connections.add(neurons.get(randNeuronIndex).connections.get(randConnectionIndex));
                neurons.get(randNeuronIndex).connections.set(randConnectionIndex,neurons.size()+1);
                neurons.add(new Neuron(0,connections,weights));break;
            case 1:
                //changes bias
                neurons.get(randNeuronIndex).bias+=new Random().nextFloat(-1,1);
                break;
            case 2:
                //Changes 1 weight
                neurons.get(randNeuronIndex).weights.set(randWeightIndex, neurons.get(randNeuronIndex).weights.get(randWeightIndex) + new Random().nextFloat(-0.1f, 0.1f));
                break;
            case 3:
                //Add connection
                int randNeuron2=new Random().nextInt(4,neurons.size());
                if (randNeuron2==randNeuronIndex){break;}
                for (int j = 0; j < neurons.get(randNeuron2).connections.size(); j++) {
                    if (randNeuronIndex == neurons.get(randNeuron2).connections.get(j)){return;}
                }
                neurons.get(randNeuronIndex).connections.add(randNeuron2);
                break;
            case 4:
                //Remove neuron
                if (neurons.size()<=20 && randNeuronIndex<20){return;}
                for (Neuron neuron:neurons){
                    if (neuron==neurons.get(randNeuronIndex)){continue;}
                    for (int connection: neuron.connections){
                        if (neuron.connections.get(connection)!=randNeuronIndex){continue;}
                        neuron.connections.remove(connection);
                        neuron.weights.remove(connection);
                    }
                }
                neurons.remove(randNeuronIndex);
        }
    }

    public Engine getEngine(){
        return engine;
    }
}