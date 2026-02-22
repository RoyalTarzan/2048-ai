package src.agent;

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

    public Neuron(float bias, ArrayList<Integer> connections, ArrayList<Float> weights){
        this.bias=bias;
        this.connections.addAll(connections);
        this.weights.addAll(weights);
        while (this.weights.size()<this.connections.size()){
            weights.add(new Random().nextFloat(-1,1));
        }
    }

    public float getValue(){
        return value;
    }

    public void calculateValue(Agent brain){
        value=0;
        if (brain.neurons.indexOf(this)<16){
            for (int i = 0; i < 16; i++) {
                if (brain.neurons.get(i)!=this){continue;}
                value = brain.getEngine().getBoard()[(int) (((double) ((i ) / 4))%4)][i%4]==0? 1 : ((float) 1 / brain.getEngine().getBoard()[(int) (((double) ((i ) / 4))%4)][i%4] );
            }
        }else {
            for (int connection:connections){
                float connectionValue=0,weight =0;
                try{
                    connectionValue=brain.neurons.get(connection).getValue();
                } catch (IndexOutOfBoundsException e){
                    System.out.println(STR."\{brain.neurons.size()-1} \{connections} There was an invalid connection");
                }
                try{
                    weight=weights.get(connections.indexOf(connection));
                }catch(IndexOutOfBoundsException e){
                    System.out.println(STR."Connections:\{connections}, Weights:\{weights}");
                }
                value+=connectionValue*
                        weight;
            }
        }
        value=value+bias;
    }

    public String toString(Agent brain){
        StringBuilder finalString=new StringBuilder();
        finalString.append(STR."{\n\t\"neuron_\{brain.neurons.indexOf(this)}\":{\n\t\t\"bias\":\{bias},\n\t\"connections+weights\":[");
        for (int connection:connections){
            try {
                finalString.append("\t{\n\t\t\"connection\":").append(connection)
                        .append(",\n\t\t\"weight\":").append(weights.get(connections.indexOf(connection))).append("\n\t}");
            }catch (IndexOutOfBoundsException e){
                System.out.println(STR."Connections:\{connections}, Weights:\{weights}");
            }
            if (connections.indexOf(connection)<connections.size()-1){
                finalString.append(",\n");
            }
        }

        finalString.append("]\n\t}\n}");
        return finalString.toString();
    }
}
