package src.agent;

import java.util.*;

/**
 * A helper class to be able to make a neural net a lot easier.
 */
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

    /**
     * Calculates the value of this Neuron based on the inputs or the cell it's connected to.
     **/
    public void calculateValue(Agent brain){
        if(this.type==NeuronType.INPUT){
            int i= Integer.parseInt(this.id.replace("in",""));
            int size=brain.size;
            value = brain.getEngine().getBoard()[(int) (((double) ((i) / size)) % size)][i % size] == 0 ? 0 : (float) ((Math.log(brain.getEngine().getBoard()[(int) (((double) ((i) / size)) % size)][i % size]) / Math.log(2))/ (size*size));
            return;
        }
        value=0;
        for (var conn:this.connIn.entrySet()){
            value+=brain.getWithId(conn.getKey()).value* conn.getValue();
        }
        value+=this.bias;
        value= (float) Math.tanh(value);
    }

    public String toString(int indent){
        StringBuilder finalString=new StringBuilder();
        finalString.repeat("\t", indent-1).append("\"").append(this.id).append("\":{\n").repeat("\t", indent + 1).append("\"connIn\":");
        if (connIn.isEmpty()){
            finalString.append("{},\n");
        }else {
            finalString.append("{\n");
            for (var connIn : this.connIn.entrySet()) {
                finalString.repeat("\t", indent + 2).append("\"").append(connIn.getKey()).append("\":").append(connIn.getValue());
                if (connIn != this.connIn.entrySet().stream().toList().getLast()) {
                    finalString.append(",\n");
                }
            }
            finalString.append("\n").repeat("\t", indent + 1).append("},\n");
        }
        finalString.repeat("\t", indent + 1).append("\"connOut\":\"").append(this.connOut).append("\",\n").repeat("\t", indent + 1).append("\"bias\" : ").append(this.bias).append("\n").repeat("\t", indent).append("}");
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

    public Neuron copy(){
        return new Neuron(new ArrayList<>(this.getConnIn().keySet()),this.getConnOut(),this.getBias(),new ArrayList<>(this.getConnIn().values()),this.getId(),this.getType());
    }

    public static Neuron fromString(String neuronString) {
        String id=neuronString.substring(neuronString.indexOf("\""),neuronString.indexOf(":"));
        NeuronType type=id.contains("in")?NeuronType.INPUT:(id.contains("out")?NeuronType.OUTPUT:NeuronType.HIDDEN);
        neuronString=neuronString.replace(id,"");
        id=id.replace("\"","").replace(":","");
        String[] connInsString=neuronString.substring(neuronString.indexOf("\"connIn"),neuronString.substring(neuronString.indexOf("\"connIn")).indexOf("}")+2)
                .replace("\"","")
                .replace("connIn","")
                .replaceFirst(":","")
                .replace("}","")
                .replace("{","").split(",");
        int start=neuronString.indexOf("\"bias");
        String bias= (neuronString.substring(start,(!neuronString.substring(start).contains(",")?neuronString.substring(start).indexOf("}")+start:neuronString.substring(start).indexOf(",")+start))
                .replace("\"","")
                .replace("bias","").replace(":","").strip());
        start=neuronString.indexOf("\"connOut");
        String[] connOut=neuronString.substring(start,(neuronString.substring(start).contains("]\",")?neuronString.substring(start).indexOf("]\",")+start:neuronString.substring(start).indexOf("}")+start))
                .replace("\"","").replace("connOut:","").replace("[","").replace("]","").split(",");
        ArrayList<String> connIn=new ArrayList<>();
        ArrayList<Float> weights=new ArrayList<>();
        for (String connInString : connInsString) {
            if (connInString.isEmpty()){continue;}
            String[] connWeight=connInString.split(":");
            connIn.add(connWeight[0].replace("\"","").strip());
            weights.add(Float.valueOf(connWeight[1].strip()));
        }
        return new Neuron(connIn,new ArrayList<>(List.of(connOut)),Float.parseFloat(bias) ,weights,id,type);
    }
}
