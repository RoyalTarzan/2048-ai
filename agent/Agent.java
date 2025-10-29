package agent;

import java.util.ArrayList;
import java.util.Random;

public class Agent {
    ArrayList<Integer> neurons= new ArrayList<>(0);
    ArrayList<ArrayList<Integer>> neuronConnections=new ArrayList<>(0);
    ArrayList<Integer> bias;
    ArrayList<Integer> weights;

    public Agent(int agentNumber){
        for (int i = 0; i < 20; i++) {
            neurons.add(i);
        }
        for (int i = 0; i < 4; i++) {
            neuronConnections.add(0,new ArrayList<>(0));
            for (int j = 4; j < 20; j++) {
                neuronConnections.get(0).add(j);
            }
        }

        new AgentWindow("Agent "+agentNumber);
    }

    public Agent(Agent parent1, Agent parent2){
        if (parent1.neurons== parent2.neurons){
            neurons=parent1.neurons;
        }else {
            if (parent1.neurons.size()==parent2.neurons.size()){
                for (int i = 0; i < parent1.neurons.size(); i++) {
                    if (new Random().nextBoolean()){
                        neurons.set(i, parent1.neurons.get(i));
                    }else {
                        neurons.set(i, parent2.neurons.get(i));
                    }
                }
            }else {
                for (int i = 0; i < parent1.neurons.size()-(parent1.neurons.size()>parent2.neurons.size()?
                        parent1.neurons.size()-parent2.neurons.size():0); i++) {
                    if (new Random().nextBoolean()){
                        neurons.set(i, parent1.neurons.get(i));
                    }else {
                        neurons.set(i, parent2.neurons.get(i));
                    }
                }
                for (int i = Math.min(parent1.neurons.size(), parent2.neurons.size()); i < (parent1.neurons.size()>parent2.neurons.size()?
                        (parent1.neurons.size()-parent2.neurons.size())/2:(parent2.neurons.size()-parent1.neurons.size())/2); i++) {
                    if (parent1.neurons.size()>parent2.neurons.size()){
                        if (new Random().nextBoolean()){
                            neurons.add(parent1.neurons.get(i));
                        }
                    }else {
                        if (new Random().nextBoolean()){
                            neurons.add(parent2.neurons.get(i));
                        }
                    }
                }
            }
        }
        if (parent1.bias== parent2.bias){
            bias= parent1.bias;
        }else {
            if (parent1.bias.size()==parent2.bias.size()){
                for (int i = 0; i < parent1.bias.size(); i++) {
                    if (new Random().nextBoolean()){
                        bias.set(i, parent1.bias.get(i));
                    }else {
                        bias.set(i, parent2.bias.get(i));
                    }
                }
            }else {
                for (int i = 0; i < parent1.bias.size()-(parent1.bias.size()>parent2.bias.size()?
                        parent1.bias.size()-parent2.bias.size():0); i++) {
                    if (new Random().nextBoolean()){
                        bias.set(i, parent1.bias.get(i));
                    }else {
                        bias.set(i, parent2.bias.get(i));
                    }
                }
                for (int i = Math.min(parent1.bias.size(), parent2.bias.size()); i < (parent1.bias.size()>parent2.bias.size()?
                        (parent1.bias.size()-parent2.bias.size())/2:(parent2.bias.size()-parent1.bias.size())/2); i++) {
                    if (parent1.bias.size()>parent2.bias.size()){
                        if (new Random().nextBoolean()){
                            bias.add(parent1.bias.get(i));
                        }
                    }else {
                        if (new Random().nextBoolean()){
                            bias.add(parent2.bias.get(i));
                        }
                    }
                }
            }
        }
        if (parent1.weights== parent2.weights){
            weights= parent1.weights;
        }else {
            if (parent1.weights.size()==parent2.weights.size()){
                for (int i = 0; i < parent1.weights.size(); i++) {
                    if (new Random().nextBoolean()){
                        weights.set(i, parent1.weights.get(i));
                    }else {
                        weights.set(i, parent2.weights.get(i));
                    }
                }
            }else {
                for (int i = 0; i < parent1.weights.size()-(parent1.weights.size()>parent2.weights.size()?
                        parent1.weights.size()-parent2.weights.size():0); i++) {
                    if (new Random().nextBoolean()){
                        weights.set(i, parent1.weights.get(i));
                    }else {
                        weights.set(i, parent2.weights.get(i));
                    }
                }
                for (int i = Math.min(parent1.weights.size(), parent2.weights.size()); i < (parent1.weights.size()>parent2.weights.size()?
                        (parent1.weights.size()-parent2.weights.size())/2:(parent2.weights.size()-parent1.weights.size())/2); i++) {
                    if (parent1.weights.size()>parent2.weights.size()){
                        if (new Random().nextBoolean()){
                            weights.add(parent1.weights.get(i));
                        }
                    }else {
                        if (new Random().nextBoolean()){
                            weights.add(parent2.weights.get(i));
                        }
                    }
                }
            }
        }
        if (parent1.neuronConnections== parent2.neuronConnections){
            neuronConnections= parent1.neuronConnections;
        }else {
            for (int i = 0; i < Math.min(parent1.weights.size(), parent2.weights.size()); i++) {

            }
        }
    }
}