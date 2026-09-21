package src.agent;

import src.game.Engine;
import src.game.Window;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static src.agent.Neuron.NeuronType.INPUT;
import static src.agent.Neuron.NeuronType.OUTPUT;
import static src.util.ListFunctions.*;

public class Agent {
    ArrayList<Neuron> neurons= new ArrayList<>(0);
    Engine engine;
    ArrayList<ArrayList<String>> sortedNeurons=new ArrayList<>();
    private final static AtomicInteger hidden=new AtomicInteger(0);
    public float score;
    public String lastMove;
    private static int numberOfGames=50;
    public int size;
    private String[] parents=new String[2];
    private final int initialGen;

    public Agent(int size){
        this.size=size;
        engine=new Engine(size);
        int inNeurons= (int) Math.pow(size,2);
        ArrayList<String> inIds=new ArrayList<>();
        for (int i = 0; i < inNeurons; i++) {
            inIds.add("in"+"0".repeat(Integer.toString(inNeurons).length() - Integer.toString(i).length())+i);
        }
        int outNeurons=4;
        ArrayList<String> outIds=new ArrayList<>();
        for (int i = 0; i < outNeurons; i++) {
            outIds.add("out"+i);
        }
        for (String id: outIds){
            neurons.add(new Neuron(inIds,new ArrayList<>(),id, OUTPUT));
        }
        for (String id: inIds){
            neurons.add(new Neuron(new ArrayList<>(),outIds,id, INPUT));
        }
        sortNeurons();
        this.parents=new String[]{"Creator","Program Runner"};
        this.initialGen=0;
    }
    public Agent(Agent parent, Window window){
        for (Neuron neuron: parent.neurons){
            this.neurons.add(neuron.copy());
        }
        this.size= parent.size;
        this.engine=new Engine(parent.size);
        this.score=0;
        this.lastMove="";
        this.mutate();
        this.sortNeurons();
        this.parents=new String[]{"Creator","agent"+window.agents.indexOf(parent)+"gen"+parent.initialGen};
        this.initialGen=window.generations;
    }
    public Agent(Agent parent1,Agent parent2,Window window){
        ArrayList<Neuron> neurons=new ArrayList<>();
        ArrayList<String> ids=new ArrayList<>();
        for (Neuron neuron:parent1.neurons){
            neurons.add(neuron.copy());
            ids.add(neuron.getId());
        }
        for (Neuron neuron:parent2.neurons){
            if (ids.contains(neuron.getId())){
                Neuron neuron1=getWithId(neuron.getId(),neurons);
                neuron1.setBias((neuron1.getBias()+ neuron.getBias())/2+new Random().nextFloat( -0.01f, 0.01f));
                for (String conn:neuron.getConnOut()){
                    if (!neuron1.getConnOut().contains(conn)){
                        neuron1.getConnOut().add(conn);
                    }
                }
                for (String conn:neuron.getConnIn().keySet()){
                    if (neuron1.getConnIn().containsKey(conn)) {
                        neuron1.getConnIn().replace(conn,(neuron.getConnIn().get(conn)+neuron1.getConnIn().get(conn))/2+new Random().nextFloat(-0.01f,0.01f));
                    } else {
                        neuron1.getConnIn().put(conn,neuron.getConnIn().get(conn));
                    }
                }
            }else{
                neurons.add(neuron.copy());
                ids.add(neuron.getId());
            }
        }
        for (Neuron neuron : neurons) {
            ArrayList<String> remove=new ArrayList<>();
            neuron.getConnIn().forEach((s,_)-> {
                if (getWithId(s, neurons) == null) {
                    remove.add(s);
                }
            });
            remove.forEach((s)->neuron.getConnIn().remove(s));
            neuron.getConnOut().removeIf(s->getWithId(s, neurons) == null);
        }
        this.neurons=neurons;
        this.size=parent1.size;
        this.engine=new Engine(size);
        this.score=0;
        this.lastMove="";
        this.mutate();
        for (Neuron neuron : neurons) {
            ArrayList<String> remove=new ArrayList<>();
            neuron.getConnIn().forEach((conn,_)->{if(getWithId(conn,neurons)==null){remove.add(conn);}});
            remove.forEach(s -> neuron.getConnIn().remove(s));
            neuron.getConnOut().removeIf(conn->getWithId(conn,neurons)==null);
        }
        this.sortNeurons();
        this.parents=new String[]{"agent"+window.agents.indexOf(parent1)+"gen"+parent1.initialGen,"agent"+window.agents.indexOf(parent2)+"gen"+parent2.initialGen};
        this.initialGen=window.generations;
    }

    public void sortNeurons(){
        ArrayList<Neuron> neurons=new ArrayList<> (this.neurons);
        ArrayList<ArrayList<String>> sortedNeurons=new ArrayList<>();
        ArrayList<Neuron> prevNeurons=new ArrayList<>();
        int g=0;
        while (!neurons.isEmpty() && g<=this.neurons.size()&&!neurons.equals(prevNeurons)){
            prevNeurons=new ArrayList<> (neurons);
            int i=0;
            while (neurons.size()>i){
                Neuron neuron=neurons.get(i);
                ArrayList<String> arrayListId=new ArrayList<>(Collections.singletonList(neuron.getId()));
                if (neuron.getConnIn().isEmpty() && neuron.getConnOut().isEmpty()){
                    neurons.remove(neuron);
                    this.neurons.remove(neuron);
                    continue;
                }
                //Inserts if there are no sorted neurons
                if (sortedNeurons.isEmpty()){
                    sortedNeurons.add(new ArrayList<>(Collections.singletonList(neuron.getId())));
                    neurons.remove(neuron);
                    continue;
                }

                //Inserts inputs
                if (neuron.getType()== INPUT){
                    if (getWithId(sortedNeurons.getFirst().getFirst()).getType()== INPUT){
                        sortedNeurons.getFirst().add(neuron.getId());
                    }else {
                        sortedNeurons.addFirst(arrayListId);
                    }
                    neurons.remove(neuron);
                    continue;
                }
                //Inserts outputs
                if (neuron.getType()== OUTPUT){
                    if (getWithId(sortedNeurons.getLast().getFirst()).getType()== OUTPUT){
                        sortedNeurons.getLast().add(neuron.getId());
                    }else {
                        sortedNeurons.addLast(arrayListId);
                    }
                    neurons.remove(neuron);
                    continue;
                }

                int lastDepthIndex=0;
                int firstDepthIndex=Integer.MAX_VALUE;
                if (sortedNeurons.size()>3){firstDepthIndex= sortedNeurons.size()-1;}
                ArrayList<String> connIn=new ArrayList<>(neuron.getConnIn().keySet());
                ArrayList<String> connOut=new ArrayList<>(neuron.getConnOut());
                for (ArrayList<String> layer:sortedNeurons){
                    int j=0;
                    while (connIn.size()>j){
                        String conn= connIn.get(j);
                        if (layer.contains(conn)){
                            if (sortedNeurons.indexOf(layer)>lastDepthIndex){
                                lastDepthIndex=sortedNeurons.indexOf(layer);
                            }
                            connIn.remove(conn);
                        }else {
                            j+=1;
                        }
                    }
                    for (String conn:connOut){
                        if (layer.contains(conn)&&sortedNeurons.indexOf(layer)<firstDepthIndex){
                            firstDepthIndex=sortedNeurons.indexOf(layer);
                        }
                    }
                }
                if (!connIn.isEmpty()){i++;continue;}

                if (firstDepthIndex>= sortedNeurons.size()-1){firstDepthIndex=sortedNeurons.size()-1;}
                if (firstDepthIndex-lastDepthIndex>1){
                    try{
                        if (lastDepthIndex+1>= neurons.size()-1){throw new Exception();}
                        sortedNeurons.get(lastDepthIndex+1).add(neuron.getId());
                        neurons.remove(neuron);
                    }catch (Exception _){
                        sortedNeurons.get(firstDepthIndex-1).add(neuron.getId());
                        neurons.remove(neuron);
                    }
                } else if (firstDepthIndex - lastDepthIndex == 1) {
                    sortedNeurons.add(lastDepthIndex+1,arrayListId);
                    neurons.remove(neuron);
                } else if (firstDepthIndex == lastDepthIndex) {
                    ArrayList<String> newLayer=new ArrayList<>();
                    for (String id:sortedNeurons.get(firstDepthIndex)){
                        if (neuron.getConnOut().contains(id)){newLayer.add(id);sortedNeurons.get(firstDepthIndex).remove(id);}
                        sortedNeurons.add(firstDepthIndex+1,newLayer);
                        sortedNeurons.add(firstDepthIndex+1,arrayListId);
                        neurons.remove(neuron);
                    }
                }else {
                    neuron.getConnOut().forEach(conn->getWithId(conn).getConnIn().remove(neuron.getId()));
                    neuron.getConnIn().keySet().forEach(conn->getWithId(conn).getConnOut().remove(neuron.getId()));
                    System.out.println(neuron.getId()+" "+neuron.getConnIn()+" "+neuron.getConnOut());
                    System.out.println("There might bge a loop present");
                    neurons.remove(neuron);
                    this.neurons.remove(neuron);
                    i+=1;
                }

            }
            g++;
        }
        this.sortedNeurons=sortedNeurons;
    }

    public void calculateOutput(){
        for (ArrayList<String> layer:sortedNeurons){
            for (String id:layer){
                Neuron neuron=getWithId(id);
                neuron.calculateValue(this);
            }
        }
    }

    public boolean outputMove(){
        calculateOutput();
        ArrayList<Neuron> outputNeurons=retainNeurons(this.neurons,OUTPUT);
        try {
            outputNeurons.sort(Comparator.comparingDouble(Neuron::getValue));
        }catch (Exception _){
            for (Neuron neuron:outputNeurons){
                System.out.println(neuron.getValue());
            }
        }
        return switch (Integer.parseInt(outputNeurons.getFirst().getId().replace("out",""))) {
            case 0 -> {lastMove = "Left";yield engine.moveLeft();}
            case 1 -> {lastMove = "Up";yield engine.moveUp();}
            case 2 -> {lastMove = "Right";yield engine.moveRight();}
            case 3 -> {lastMove = "Down";yield engine.moveDown();}
            default -> false;
        };
    }

    Neuron getWithId(String id) {
        for (Neuron neuron:this.neurons){
            if (Objects.equals(neuron.getId(), id)){
                return neuron;
            }
        }
        throw new RuntimeException("The neuron with id:"+id+" was not found in agent "+ this.toString(0));
    }
    Neuron getWithId(String id, List<Neuron> neurons) {
        for (Neuron neuron:neurons){
            if (Objects.equals(neuron.getId(), id)){
                return neuron;
            }
        }
        return null;
    }

    /**
     * Mutates the selected Agent:
     * This changes 1 weight, 1 bias of a random neuron,
     * removes a random neuron, adds a neuron on a connection,
     * adds a connection, removes a connection
     */
    public void mutate(){
        Random rand=new Random();
        if (rand.nextFloat(0,1)<0.1){return;}
        float chance=rand.nextFloat(0,1);
        Neuron neuron= getRandom(this.neurons);
        if (chance<=0.25){
            //Changes 1 bias
            neuron.setBias(neuron.getBias()+rand.nextFloat(-0.25f,0.25f));
        } else if (chance<=0.5) {
            //Changes 1 weight
            if (neuron.getConnIn().isEmpty()){return;}
            String conn= getRandom(neuron.getConnIn());
            neuron.getConnIn().put(conn,neuron.getConnIn().get(conn)+rand.nextFloat(-0.25f,0.25f));
        } else if (chance<=0.625) {
            //Adds 1 new neuron
            String newId= "hid"+(hidden.get()+1);
            hidden.getAndIncrement();
            List<String> conns= new ArrayList<>(neuron.getConnIn().keySet());
            conns.addAll(neuron.getConnOut());
            String conn= getRandom(conns);
            Neuron neuron2=getWithId(conn);
            if (neuron.getConnOut().contains(conn)){
                this.neurons.add(new Neuron(new ArrayList<>(Collections.singletonList(neuron.getId())), new ArrayList<>(Collections.singletonList(conn)), new ArrayList<>(Collections.singletonList(rand.nextFloat(-1,1))),newId, Neuron.NeuronType.HIDDEN));
                neuron2.getConnIn().put(newId,neuron2.getConnIn().get(neuron.getId()));
                neuron2.getConnIn().remove(neuron.getId());
                neuron.getConnOut().remove(conn);
                if (!neuron.getConnOut().contains(newId)){
                    neuron.getConnOut().add(newId);
                }
            }else {
                this.neurons.add(new Neuron(new ArrayList<>(Collections.singletonList(conn)), new ArrayList<>(Collections.singletonList(neuron.getId())), new ArrayList<>(Collections.singletonList(rand.nextFloat(-1,1))),newId, Neuron.NeuronType.HIDDEN));
                neuron.getConnIn().put(newId,neuron.getConnIn().get(conn));
                neuron.getConnIn().remove(conn);
                neuron2.getConnOut().remove(neuron.getId());
                if (!neuron2.getConnOut().contains(newId)){
                    neuron2.getConnOut().add(newId);
                }
            }
        } else if (chance<=0.75) {
            //Removes a random Neuron
            if (neuron.getType()== INPUT || neuron.getType()== OUTPUT){return;}
            for (String conn: neuron.getConnIn().keySet()){
                Neuron neuron2=getWithId(conn);
                if (neuron2.getConnOut().size()==1){
                    String randId=getRandom(neuron.getConnOut());
                    Neuron neuron3=getWithId(randId);
                    if (!neuron2.getConnOut().contains(randId)){
                        neuron2.getConnOut().add(randId);
                    }
                    if (!neuron3.getConnIn().containsKey(conn)){
                        neuron3.getConnIn().put(conn,rand.nextFloat(-1,1));
                    }
                }
                neuron2.getConnOut().remove(neuron.getId());
            }
            for (String conn:neuron.getConnOut()){
                Neuron neuron2=getWithId(conn);
                if (neuron2.getConnIn().size()==1){
                    String randId=getRandom(neuron.getConnIn());
                    Neuron neuron3=getWithId(randId);
                    if (!neuron3.getConnOut().contains(conn)){
                        neuron3.getConnOut().add(conn);
                    }
                    if (!neuron2.getConnIn().containsKey(randId)){
                        neuron2.getConnIn().put(randId,rand.nextFloat(-1,1));
                    }
                }
                neuron2.getConnIn().remove(neuron.getId());
            }
            this.neurons.remove(neuron);
        } else if (chance<=0.875) {
            //Removes a random connection
            if (neuron.getConnIn().size()<2 || neuron.getConnOut().size()<2){return;}
            List<String> conns= new ArrayList<>(neuron.getConnIn().keySet().stream().toList());
            conns.addAll(neuron.getConnOut());
            String conn=getRandom(conns);
            Neuron neuron2=getWithId(conn);
            if (neuron.getConnIn().containsKey(conn)){
                if (neuron2.getConnOut().size()<2){return;}
                neuron2.getConnOut().remove(neuron.getId());
                neuron.getConnIn().remove(conn);
            }else {
                if (neuron2.getConnIn().size()<2){return;}
                neuron2.getConnIn().remove(neuron.getId());
                neuron.getConnOut().remove(conn);
            }
        } else if (chance <= 1) {
            Neuron neuron2=getRandom(this.neurons);
            if (neuron2==neuron){return;}
            if (neuron.getType()== INPUT){
                if (neuron2.getType() != INPUT && !neuron.getConnOut().contains(neuron2.getId())){
                    neuron.getConnOut().add(neuron2.getId());
                    neuron2.getConnIn().put(neuron.getId(), rand.nextFloat(-1,1));
                }
            } else if (neuron.getType() == OUTPUT) {
                if (neuron2.getType() != OUTPUT && !neuron.getConnIn().containsKey(neuron2.getId())){
                    neuron2.getConnOut().add(neuron.getId());
                    neuron.getConnIn().put(neuron2.getId(), rand.nextFloat(-1,1));
                }
            } else {
                if (neuron2.getType()== INPUT && !neuron.getConnIn().containsKey(neuron2.getId())){
                    neuron2.getConnOut().add(neuron.getId());
                    neuron.getConnIn().put(neuron2.getId(), rand.nextFloat(-1,1));
                } else if (neuron2.getType() == OUTPUT && !neuron.getConnOut().contains(neuron2.getId())) {
                    neuron.getConnOut().add(neuron2.getId());
                    neuron2.getConnIn().put(neuron.getId(), rand.nextFloat(-1,1));
                }else {
                    if (rand.nextBoolean()){
                        List<String> connOut=checkConnOut(new ArrayList<>(Collections.singletonList(neuron.getId())));
                        if (!connOut.contains(neuron2.getId())&&!neuron.getConnIn().containsKey(neuron2.getId())){
                            if (!neuron2.getConnOut().contains(neuron.getId())){
                                neuron2.getConnOut().add(neuron.getId());
                            }
                            neuron.getConnIn().put(neuron2.getId(), rand.nextFloat(-1,1));
                        }
                    }else {
                        List<String> connIn=checkConnIn(new ArrayList<>(Collections.singletonList(neuron.getId())));
                        if (!connIn.contains(neuron2.getId())&&!neuron.getConnOut().contains(neuron2.getId())){
                            if (!neuron2.getConnIn().containsKey(neuron.getId())){
                                neuron2.getConnIn().put(neuron.getId(), rand.nextFloat(-1,1));
                            }
                            neuron.getConnOut().add(neuron2.getId());
                        }
                    }
                }
            }
        }
    }

    public Engine getEngine(){
        return engine;
    }

    public void setEngine(Engine engine){
        this.engine=engine;
    }

    public boolean calculateScore(int maxMoves){
        score=0;
        int setReturn=0;
        for (int i = 0; i < numberOfGames; i++) {
            for (int j = 0; j < maxMoves & !engine.lose(); j++) {
                if(!outputMove()){
                    score-=10;
                    break;}
                if (j==maxMoves-1){
                    setReturn++;
                }
            }
            int gameScore=engine.getPoints();
            score+= gameScore;
            engine.reset();
        }
        score/=numberOfGames;
        return setReturn>numberOfGames*0.7;
    }

    public String toString(int indent){
        StringBuilder finalString=new StringBuilder();
        indent++;
        finalString.repeat("\t", indent-1).append("{\n").repeat("\t", indent).append("\"agent\":{\n").repeat("\t", indent + 1).append("\"sorted neurons\":");
        finalString.append("\"").append(sortedNeurons.toString()).append("\",\n").repeat("\t", indent + 1).append("\"neurons\":{");
        for (Neuron neuron:neurons){
            finalString.append("\n").repeat("\t", indent).append(neuron.toString(indent + 2));
            if(neurons.indexOf(neuron)< neurons.size()-1){
                finalString.append(",");
            }
        }
        finalString.append("\n").repeat("\t", indent+1).append("},\n").repeat("\t", indent+1).append("\"parents\": \"").append(Arrays.toString(parents)).append("\"\n").repeat("\t", indent).append("}").repeat("\t", indent).append("\n}");
        return finalString.toString();
    }

    private ArrayList<String> checkConnOut(ArrayList<String> ids,int startIndex){
        ArrayList<String> oldIds = new ArrayList<>(ids);
        for (int i = startIndex; i < ids.size(); i++) {
            ids.addAll(getWithId(ids.get(i)).getConnOut());
        }
        ids= removeDuplicates(ids);
        if (oldIds.equals(ids)){
            return ids;
        }
        return checkConnOut(ids, oldIds.size()-1);
    }
    private ArrayList<String> checkConnOut(ArrayList<String> ids){
        return checkConnOut(ids,0);
    }

    private ArrayList<String> checkConnIn(ArrayList<String> ids,int startIndex){
        ArrayList<String> oldIds=new ArrayList<> (ids) ;
        for (int i = startIndex; i < ids.size(); i++) {
            ids.addAll(getWithId(ids.get(i)).getConnIn().keySet());
        }
        ids=removeDuplicates(ids);
        if (oldIds.equals(ids)){
            return ids;
        }
        return checkConnIn(ids, oldIds.size()-1);
    }
    private ArrayList<String> checkConnIn(ArrayList<String> ids){
        return checkConnIn(ids,0);
    }

    public static void setNumberOfGames(int numberOfGames) {
        Agent.numberOfGames = numberOfGames;
    }

}