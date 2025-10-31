package agent;

import java.util.ArrayList;

public class Neuron {
    public final ArrayList<Integer> connections=new ArrayList<>();
    public float bias;
    public final ArrayList<Double> weights=new ArrayList<>();

    public Neuron(float bias, ArrayList<Integer> connections,ArrayList<Double> weights){
        this.bias =bias;
        this.connections.addAll(connections);
        this.weights.addAll(weights);
    }
}
