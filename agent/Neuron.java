package agent;

import java.util.ArrayList;
import java.util.Random;

public class Neuron {
    public final ArrayList<Integer> connections=new ArrayList<>();
    public float bias;
    public final ArrayList<Float> weights=new ArrayList<>();
    public float value;

    public Neuron(float bias, ArrayList<Integer> connections){
        this.bias =bias;
        this.connections.addAll(connections);
        for(int ignored :connections){
            this.weights.add(new Random().nextFloat(-1,1));
        }
    }

    public float getValue(){
        return value;
    }

    public void calculateValue(Agent brain){
        value=0;
        if (4<brain.neurons.indexOf(this) && brain.neurons.indexOf(this)<20){
            for (int i = 4; i < 20; i++) {
                if (brain.neurons.get(i)!=this){continue;}
                value = brain.getEngine().getBoard()[(int) (((double) ((i - 4) / 4))%4)][i%4]==0? 1 : ((float) 1 / brain.getEngine().getBoard()[(int) (((double) ((i - 4) / 4))%4)][i%4] );
            }
        }else {
            for (int connection:connections){
                value+=brain.neurons.get(connection).getValue()* weights.get(connection-4);
            }
        }
        value=value+bias;
    }
}
