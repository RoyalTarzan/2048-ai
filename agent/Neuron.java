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
            switch (i){
                case 4: value = ((float) 1 / brain.getEngine().getBoard()[0][0] );return;
                case 5: value =  ((float) 1 / brain.getEngine().getBoard()[0][1] );return;
                case 6: value =  ((float) 1 / brain.getEngine().getBoard()[0][2] );return;
                case 7: value =  ((float) 1 / brain.getEngine().getBoard()[0][3] );return;
                case 8: value =  ((float) 1 / brain.getEngine().getBoard()[1][0] );return;
                case 9: value =  ((float) 1 / brain.getEngine().getBoard()[1][1] );return;
                case 10: value =  ((float) 1 / brain.getEngine().getBoard()[1][2] );return;
                case 11: value =  ((float) 1 / brain.getEngine().getBoard()[1][3] );return;
                case 12: value =  ((float) 1 / brain.getEngine().getBoard()[2][0] );return;
                case 13: value =  ((float) 1 / brain.getEngine().getBoard()[2][1] );return;
                case 14: value =  ((float) 1 / brain.getEngine().getBoard()[2][2] );return;
                case 15: value =  ((float) 1 / brain.getEngine().getBoard()[2][3] );return;
                case 16: value =  ((float) 1 / brain.getEngine().getBoard()[3][0] );return;
                case 17: value =  ((float) 1 / brain.getEngine().getBoard()[3][1] );return;
                case 18: value =  ((float) 1 / brain.getEngine().getBoard()[3][2] );return;
                case 19: value =  ((float) 1 / brain.getEngine().getBoard()[3][3] );return;
            }
        }
        for (int connection:connections){
            value+=brain.neurons.get(connection).getValue()* weights.get(connection);
        }
        value= (float) activationFunction(value+bias);
        System.out.println(value);
    }

    private double activationFunction(float x){
        return 1 / (1 + Math.exp(-x));
    }
}
