package src.util;

import src.agent.Agent;
import src.agent.Neuron;

import java.util.*;

public class ListFunctions {
    public static <T> T getRandom(ArrayList<T> list){
        int index=new Random().nextInt(0, list.size());
        return list.get(index);
    }
    public static <T> T getRandom(Map<T,?> map){
        int index=new Random().nextInt(0, map.size());
        return (T) map.keySet().toArray()[index];
    }
    public static <T> T getRandom(List<T> list){
        int index=new Random().nextInt(0, list.size());
        return list.get(index);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> finalList=new ArrayList<>();
        for (T s:list){
            if (finalList.contains(s)){continue;}
            finalList.add(s);
        }
        return finalList;
    }
    public static  ArrayList<Neuron> retainNeurons(ArrayList<Neuron> list, Neuron.NeuronType type){
        ArrayList<Neuron> finalList=new ArrayList<>();
        for (Neuron neuron:list){
            if (neuron.getType()==type){
                finalList.add(neuron);
            }
        }
        return finalList;
    }

    public static float averageScore(ArrayList<Agent> agents){
        float average=0;
        for (Agent agent : agents) {
            average+=agent.score;
        }
        average/=agents.size();
        return average;
    }
}
