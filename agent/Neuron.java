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

    public float calculateValue(Agent brain){
        for (int i = 4; i < 20; i++) {
            if (brain.neurons.get(i)==this){
                switch (i){
                    case 4,5,6,7:switch (i){
                        case 4: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[0][0])/Math.log(2)));
                        case 5: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[0][1])/Math.log(2)));
                        case 6: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[0][2])/Math.log(2)));
                        case 7: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[0][3])/Math.log(2)));
                    }
                    case 8,9,10,11:switch (i){
                        case 8: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[1][0])/Math.log(2)));
                        case 9: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[1][1])/Math.log(2)));
                        case 10: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[1][2])/Math.log(2)));
                        case 11: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[1][3])/Math.log(2)));
                    }
                    case 12,13,14,15:switch (i){
                        case 12: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[2][0])/Math.log(2)));
                        case 13: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[2][1])/Math.log(2)));
                        case 14: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[2][2])/Math.log(2)));
                        case 15: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[2][3])/Math.log(2)));
                    }
                    case 16,17,18,19:switch (i){
                        case 16: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[3][0])/Math.log(2)));
                        case 17: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[3][1])/Math.log(2)));
                        case 18: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[3][2])/Math.log(2)));
                        case 19: return value = (float) ((float) 1/(Math.log(brain.getEngine().getBoard()[3][3])/Math.log(2)));
                    }
                }
            }
        }
        for (int connection:connections){
            value+=brain.neurons.get(connection).getValue()* weights.get(connection);
        }
        return value+= bias;
    }
}
