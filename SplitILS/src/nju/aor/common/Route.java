package nju.aor.common;

import java.util.ArrayList;

public class Route {
    public ArrayList<Point> point;
    public Instance inst;
    public Route(ArrayList<Point> point,Instance inst){
            this.point=point;this.inst=inst;
    };
    public double calculateCost(Instance inst){
        if(point.size()==1)
            return 0;
        int head = point.get(0).id;
        int tail = point.get(point.size() - 1).id;
        double cost;
        cost = inst.dist(tail, head);
        for(int i = 0; i + 1 <= point.size()-1; i++){
            int first = point.get(i).id;
            int second = point.get(i + 1).id;
            cost = cost+inst.dist(first, second);
        }
        return cost;
    }
    public double getRemain_capacity(){
        double Remain_capacity=inst.capacity;
        for(int i=0; i<point.size();i++)
            Remain_capacity=Remain_capacity-point.get(i).MeetDemand;
        return Remain_capacity;

    }





}
