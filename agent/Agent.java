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
        sortNeurons();
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
        sortNeurons();
    }

    private void sortNeurons(){
        @SuppressWarnings("unchecked") ArrayList<Neuron> copyOfNeurons= (ArrayList<Neuron>) neurons.clone();
        sortedNeurons.clear();
        for (Neuron neuron:copyOfNeurons){
            if (!neuron.connections.equals(new ArrayList<Integer>())){continue;}
            sortedNeurons.add(neuron);
            for (Neuron neuron1:copyOfNeurons){
                if (neuron==neuron1){continue;}
                for (int connection:neuron1.connections){
                    if (connection!=copyOfNeurons.indexOf(neuron)){continue;}
                    neuron1.connections.remove((Integer) connection);
                    neuron1.weights.remove(neuron1.connections.indexOf(connection));
                }
            }
        }
    }

    public void outputMove(){
        calculateValues();
        float biggest=neurons.getFirst().value;
        int currentNeuron = 0;
        for (int i = 0; i < 4; i++) {
            if (neurons.get(i).value > biggest){
                currentNeuron=i;
            }
            System.out.println(neurons.get(i));
            System.out.println(neurons.get(i).value);
            System.out.println(biggest);
        }
        switch (currentNeuron){
            case 0:engine.moveLeft();
            case 1:engine.moveUp();
            case 2:engine.moveRight();
            case 3:engine.moveDown();
        }
    }

    public void calculateValues(){
        for (Neuron neuron:sortedNeurons){
            neuron.calculateValue(this);
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
        switch (new Random().nextInt(0,6)){
            case 0:
                //Adds new neuron
                ArrayList<Float> weights=new ArrayList<>();
                weights.add(1f);
                ArrayList<Integer> connections=new ArrayList<>();
                connections.add(neurons.get(randNeuronIndex).connections.get(randConnectionIndex));
                neurons.get(randNeuronIndex).connections.set(randConnectionIndex,neurons.size()+1);
                neurons.add(new Neuron(0,connections,weights));
                break;
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
                int randNeuronIndex2=new Random().nextInt(4,neurons.size());
                if (randNeuronIndex2==randNeuronIndex){return;}
                for (int connection:neurons.get(randNeuronIndex2).connections) {
                    if (randNeuronIndex == connection){return;}
                }
                neurons.get(randNeuronIndex).connections.add(randNeuronIndex2);
                break;
            case 4:
                //Remove neuron
                if (neurons.size()<=20 || randNeuronIndex<20){return;}
                for (Neuron neuron:neurons){
                    if (neuron==neurons.get(randNeuronIndex)){continue;}
                    for (int connection: neuron.connections){
                        if (connection!=randNeuronIndex){continue;}
                        neuron.connections.remove((Integer) connection);
                        neuron.weights.remove(neuron.connections.indexOf(connection));
                    }
                }
                neurons.remove(randNeuronIndex);
                break;
            case 5:
                //Remove connection
                for (Neuron neuron:neurons){
                    if (neurons.indexOf(neuron)==neurons.get(randNeuronIndex).connections.get(randConnectionIndex) || neuron==neurons.get(randNeuronIndex)){continue;}
                    for (int connection:neuron.connections){
                        if (connection==neurons.get(randNeuronIndex).connections.get(randConnectionIndex)){break;}
                    }
                }
                neurons.get(randNeuronIndex).connections.remove(randConnectionIndex);
                neurons.get(randNeuronIndex).weights.remove(randConnectionIndex);
        }
    }

    public Engine getEngine(){
        return engine;
    }
}