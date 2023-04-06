package nju.aor.common;

import java.util.ArrayList;
import java.util.BitSet;

public class Solution {
    public double cost;
    public ArrayList<Route> route;

    public Solution(Solution other){
        this.route = new ArrayList<Route>(other.route);
        this.cost = other.cost;
    }

    public Solution(Instance inst, ArrayList<Route> route){
        this.route = new ArrayList<Route>(route);
        calculateCost(inst);
    }


    public void calculateCost(Instance inst){
        cost=0;
        int n=route.size();
        for(int i=0;i<n;i++) {
            cost = cost + route.get(i).calculateCost(inst);
        }
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("cost: " + cost + "\r\n");
        sb.append("route: " + route.toString() +"\r\n");
        return sb.toString();
    }


}
