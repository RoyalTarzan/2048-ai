package agent;

import game.Engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public class Agent {
    ArrayList<Neuron> neurons= new ArrayList<>(0);
    Engine engine=new Engine();
    ArrayList<Integer> sortedNeurons=new ArrayList<>();
    public int score;
    public String lastMove;

    public Agent(){
        ArrayList<Integer> connections=new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            connections.add(i);
        }
        for (int i = 0; i < 16; i++) {
            neurons.add(new Neuron(0,new ArrayList<>()));
        }
        for (int i = 16; i < 20; i++) {
            neurons.add(new Neuron(new Random().nextFloat(-1,1),
                    connections));
        }
        sortNeurons();
        //for (Neuron neuron:neurons){
        //    System.out.println(neurons.indexOf(neuron)+" "+neuron.connections);
        //}
    }

    public Agent(Agent parent1, Agent parent2){
        if (parent1.neurons==parent2.neurons){
            neurons.addAll(parent1.neurons);
        }else if (parent1.neurons.size()==parent2.neurons.size()){
            for (int i=0;i<parent1.neurons.size();i++){
                neurons.add(new Neuron((parent1.neurons.get(i).bias+parent2.neurons.get(i).bias)/2,
                        parent1.score<parent2.score?parent1.neurons.get(i).connections:parent2.neurons.get(i).connections,
                        parent1.score<parent2.score?parent1.neurons.get(i).weights:parent2.neurons.get(i).weights));
            }
        } else if (parent1.neurons.size()<parent2.neurons.size()) {
            for (int i=0;i<parent1.neurons.size();i++){
                neurons.add(new Neuron((parent1.neurons.get(i).bias+parent2.neurons.get(i).bias)/2,
                        parent1.score<parent2.score?parent1.neurons.get(i).connections:parent2.neurons.get(i).connections,
                        parent1.score<parent2.score?parent1.neurons.get(i).weights:parent2.neurons.get(i).weights));
            }
            for (int i = 0; i < parent2.neurons.size()-parent1.neurons.size(); i++) {
                if (new Random().nextInt(0,4)==2){continue;}
                neurons.add(new Neuron((parent2.neurons.get(i).bias)/2,
                        parent2.neurons.get(i).connections,
                        parent2.neurons.get(i).weights));
            }
        } else {
            for (int i=0;i<parent2.neurons.size();i++){
                neurons.add(new Neuron((parent1.neurons.get(i).bias+parent2.neurons.get(i).bias)/2,
                        parent1.score<parent2.score?parent1.neurons.get(i).connections:parent2.neurons.get(i).connections,
                        parent1.score<parent2.score?parent1.neurons.get(i).weights:parent2.neurons.get(i).weights));
            }
            for (int i = 0; i < parent1.neurons.size()-parent2.neurons.size(); i++) {
                if (new Random().nextInt(0,4)==2){continue;}
                neurons.add(new Neuron((parent1.neurons.get(i).bias)/2,
                        parent1.neurons.get(i).connections,
                        parent1.neurons.get(i).weights));
            }
        }
        //System.out.println("Mutating new agent");
        mutate();
        sortNeurons();
        //for (Neuron neuron:neurons){
        //    System.out.println(neurons.indexOf(neuron)+" "+neuron.connections);
        //}
    }

    private void sortNeurons(){
        ArrayList<Neuron[]> copyOfNeurons = new ArrayList<>();
        for (Neuron neuron:neurons){
            copyOfNeurons.add(new Neuron[]{neuron,new Neuron(neuron.bias, neuron.connections)});
        }
        sortedNeurons.clear();
        ArrayList<Integer> removedNeuronIndex=new ArrayList<>();
        int i=0;
        while (!Objects.equals(copyOfNeurons, new ArrayList<Neuron[]>()) && i<100) {
            //System.out.println("Try "+(i+1)+" to sort neurons.");
            for (Neuron[] neuron:copyOfNeurons) {
                if (!neuron[1].connections.isEmpty()) {
                    continue;
                }
                sortedNeurons.add(neurons.indexOf(neuron[0]));
                for (Neuron[] neuron1 : copyOfNeurons) {
                    if (neuron == neuron1 || neuron1[1].connections.equals(new ArrayList<>())) {
                        continue;
                    }
                    neuron1[1].connections.removeIf(connection -> connection == neurons.indexOf(neuron[0]));
                }
                removedNeuronIndex.add(copyOfNeurons.indexOf(neuron));
            }
            //for (Neuron[] neuron:copyOfNeurons){
            //    System.out.println(neurons.indexOf(neuron[0]));
            //}
            copyOfNeurons.removeIf(neuron -> removedNeuronIndex.contains(copyOfNeurons.indexOf(neuron)));
            removedNeuronIndex.clear();
            i++;
        }
        //System.out.println(sortedNeurons);
    }

    public void calculateOutput(){
        for (int neuronIndex:sortedNeurons){
            neurons.get(neuronIndex).calculateValue(this);
        }
    }

    public boolean outputMove(){
        calculateOutput();
            /*System.out.print("Current neuron: "+neurons.get(i));
            System.out.print(", value: "+neurons.get(i).value);
            System.out.println(", biggest value: "+biggest+", current neuron: "+currentNeuron);*/
        ArrayList<Neuron> outputNeurons=new ArrayList<>();
        for (int i = 16; i < 20; i++) {
            outputNeurons.add(neurons.get(i));
        }
        outputNeurons.sort(Comparator.comparingDouble(neuron->neuron.value));
        return switch (neurons.indexOf(outputNeurons.get(0))) {
            case 16 -> {lastMove = "Left";yield engine.move(0, 1);}
            case 17 -> {lastMove = "Up";yield engine.move(1, 0);}
            case 18 -> {lastMove = "Right";yield engine.move(0, -1);}
            case 19 -> {lastMove = "Down";yield engine.move(-1, 0);}
            default -> false;
        };
    }

    public void mutate(){
        if (new Random().nextInt(0,10)>1){return;}
        int randNeuronIndex=new Random().nextInt(16,neurons.size());
        System.out.println(randNeuronIndex+":"+neurons.get(randNeuronIndex).connections);
        int randConnectionIndex=new Random().nextInt(0,neurons.get(randNeuronIndex).connections.size());
        int randWeightIndex=new Random().nextInt(0,neurons.get(randNeuronIndex).weights.size());
        switch (new Random().nextInt(0,6)){
            case 0:
                //Adds new neuron
                ArrayList<Integer> connections=new ArrayList<>();
                ArrayList<Float> weight=new ArrayList<>();
                weight.add(1f);
                connections.add(neurons.get(randNeuronIndex).connections.get(randConnectionIndex));
                neurons.add(new Neuron(0,connections,weight));
                neurons.get(randNeuronIndex).connections.set(randConnectionIndex,neurons.size()-1);
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
                int randNeuronIndex2=new Random().nextInt(0,neurons.size());
                if (randNeuronIndex2==randNeuronIndex || (randNeuronIndex2<=19 && randNeuronIndex2>=16) || neurons.get(randNeuronIndex).connections.contains(randNeuronIndex2)){return;}
                for (int connection:neurons.get(randNeuronIndex2).connections) {
                    if (randNeuronIndex == connection){return;}
                }
                neurons.get(randNeuronIndex).connections.add(randNeuronIndex2);
                neurons.get(randNeuronIndex).weights.add(new Random().nextFloat(-1,1));
                break;
            case 4:
                //Remove neuron
                if (neurons.size()<=20 || randNeuronIndex<20){return;}
                for (Neuron neuron:neurons){
                    if (neuron==neurons.get(randNeuronIndex)){continue;}
                    for (int connection: neuron.connections){
                        if (connection<randNeuronIndex){continue;}
                        if (connection==randNeuronIndex){
                            neuron.weights.remove(neuron.connections.indexOf(connection));
                            neuron.connections.remove((Integer) connection);
                        }else {
                            neuron.connections.set(neuron.connections.indexOf(connection),connection-1);
                        }
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

    public void setEngine(Engine engine){
        this.engine=engine;
    }

    public void calculateScore(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 50 & !engine.lose(); j++) {
                if(!outputMove()){break;}
            }
            score+= engine.getPoints();
            engine.reset();
        }
    }
}