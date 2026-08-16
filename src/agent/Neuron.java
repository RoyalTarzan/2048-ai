package src.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Neuron {
    private final NeuronType type;
    private float bias;
    private final Map<String,Float> connIn=new HashMap<>();
    private final ArrayList<String> connOut=new ArrayList<>();
    private final String id;
    private float value= 0;

    public Neuron(ArrayList<String> connIn, ArrayList<String> connOut,float bias,ArrayList<Float> weights,String id,NeuronType type){
        if (weights.size()<connIn.size()){
            for (int i = 0; i < weights.size(); i++) {
                if (weights.get(i)==null){
                    weights.set(i,new Random().nextFloat(-1,1));
                }
                this.connIn.put(connIn.get(i), weights.get(i));
            }
            for (int i = weights.size(); i < connIn.size(); i++) {
                this.connIn.put(connIn.get(i),new Random().nextFloat(-1,1));
            }
        }else {
            for (int i = 0; i < connIn.size(); i++) {
                if (weights.get(i)==null){
                    weights.set(i,new Random().nextFloat(-1,1));
                }
                this.connIn.put(connIn.get(i), weights.get(i));
            }
        }
        this.connOut.addAll(connOut);
        this.bias=bias;
        this.id=id;
        this.type=type;
    }

    public Neuron(ArrayList<String> connIn, ArrayList<String> connOut, ArrayList<Float> weights, String id, NeuronType type){
        this(connIn,connOut,0,weights,id,type);
    }

    public Neuron(ArrayList<String> connIn, ArrayList<String> connOut,float bias,String id,NeuronType type) {
        this(connIn, connOut, bias, new ArrayList<>(0), id,type);
    }

    public Neuron(ArrayList<String> connIn, ArrayList<String> connOut,String id,NeuronType type){
        this(connIn,connOut,new ArrayList<>(0),id, type);
    }

    public double getValue(){
        return value;
    }

    public void calculateValue(Agent brain){
        if(this.type==NeuronType.INPUT){
            int i= Integer.parseInt(this.id.replace("in",""));
            int size=brain.size;
            value = brain.getEngine().getBoard()[(int) (((double) ((i) / size)) % size)][i % size] == 0 ? 1 : ((float) 1 / brain.getEngine().getBoard()[(int) (((double) ((i) / size)) % size)][i % size]);
            return;
        }
        value=0;
        for (var conn:this.connIn.entrySet()){
            value+=brain.getWithId(conn.getKey()).value* conn.getValue();
        }
        value+=this.bias;
    }

    public String toString(int indent){
        StringBuilder finalString=new StringBuilder();
        finalString.append(STR."\{"\t".repeat(indent)}\"\{this.id}\":{\n\{"\t".repeat(indent + 1)}\"connIn\":{\n");
        for (var connIn:this.connIn.entrySet()){
            finalString.append(STR."\{"\t".repeat(indent + 2)}\"\{connIn.getKey()}\":\{connIn.getValue()}");
            if (connIn!=this.connIn.entrySet().stream().toList().getLast()){
                finalString.append(",\n");
            }
        }
        finalString.append(STR."\n\{"\t".repeat(indent + 1)}},\n\{"\t".repeat(indent + 1)}\"connOut\":\"\{this.connOut}\",\n\{"\t".repeat(indent + 1)}\"bias\" : \{this.bias}\n\{"\t".repeat(indent)}}");
        return finalString.toString();
    }

    public ArrayList<String> getConnOut() {
        return connOut;
    }

    public Map<String, Float> getConnIn() {
        return connIn;
    }

    public float getBias() {
        return bias;
    }

    public String getId() {
        return id;
    }

    public void setBias(float v) {
        this.bias=v;
    }

    public NeuronType getType() {
        return type;
    }

    public enum NeuronType {
        INPUT,
        OUTPUT,
        HIDDEN
    }
}
