package agent;

import java.util.ArrayList;

public class Neuron {
    public final ArrayList<Integer> connections=new ArrayList<>();
    public float bias;
    public final ArrayList<Float> weights=new ArrayList<>();
    public float value;

    public Neuron(float bias, ArrayList<Integer> connections,ArrayList<Float> weights){
        this.bias =bias;
        this.connections.addAll(connections);
        this.weights.addAll(weights);
    }

    public float getValue(){
        return value;
    }

    public void calculateValue(Agent brain){
        value=0;
        for (int i = 4; i < 20; i++) {
            if (brain.neurons.get(i)!=this){continue;}
            value = brain.getEngine().getBoard()[(int) (((double) ((i - 4) / 4))%4)][i%4]==0? 1 : ((float) 1 / brain.getEngine().getBoard()[(int) (((double) ((i - 4) / 4))%4)][i%4] );
            System.out.println(value);return;
        }
        for (int connection:connections){
            value+=brain.neurons.get(connection).getValue()* weights.get(connection);
            System.out.println(value);
        }
        value= (float) activationFunction(value+bias);
        System.out.println(value);
    }

    private double activationFunction(float x){
        return 1 / (1 + Math.exp(-x));
    }
}
