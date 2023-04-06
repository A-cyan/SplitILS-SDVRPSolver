package nju.aor.algo;

import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import nju.aor.common.*;

import java.util.*;

/**
 * Algorithm
 * - TODO: implement your algorithm
 */
public class Algorithm {
    Instance inst;
    AlgoParam param;
    Result res;
    Operator opt;

    Random rnd;
    long start;

    int Imax = 10;


    public Algorithm(Instance inst, AlgoParam param) {
        this.inst = inst;
        this.param = param;
        this.res = new Result(inst, param);
        this.rnd = new Random(param.seed);
        this.opt = new Operator(inst, res, rnd);
    }

    public void run() {
        this.start = System.currentTimeMillis();
        double _cost= Long.MAX_VALUE;
        int i=0;
        Solution sol=construct();
        res.setBestSol(sol);
            while (i<Imax      /*timeout() == false*/) {
            sol = construct();
            int iterILS = 0;
            int IILS=5000;   //to be changed
            while (iterILS < IILS) {
                localSearch(sol);
                sol.calculateCost(inst);
                if (sol.cost<_cost){
                    res.setBestSol(sol);
                    _cost=sol.cost;
                    iterILS=0;
                    sol.calculateCost(inst);
                    System.out.println("\tVNS " + i + " > " + sol.cost + "\t" + sol.cost + "\ttime = " + time());
                    /*int test=0;
                    for(int m=0;m<sol.route.size();m++)
                        test=test+sol.route.get(m).point.size();
                    test=test-sol.route.size();
                    System.out.println(test);*/
                }
                sol.calculateCost(inst);
                perturb(sol);
                iterILS=iterILS+1;
            }
            if(sol.cost<_cost){
                res.setBestSol(sol);
                _cost=sol.cost;
            }
            i++;
        }
        System.out.println("\tbest > " + res.cost() + "\ttime: " + time());
            double clossest=0;
            /*if(inst.N==23)
            {
                clossest=inst.dist(1,22)+inst.dist(22,5)+inst.dist(5,6)+inst.dist(6,11)+inst.dist(11,8)+inst.dist(8,1)+inst.dist(1,19)+inst.dist(19,20)+inst.dist(20,21)+inst.dist(21,23)+inst.dist(23,18)+inst.dist(18,15)+inst.dist(15,16)+inst.dist(16,17)+inst.dist(17,4)+inst.dist(4,3)+inst.dist(3,2)+inst.dist(2,7)+inst.dist(7,13)+inst.dist(13,1)+inst.dist(1,12)+inst.dist(12,14)+inst.dist(14,11)+inst.dist(11,10)+inst.dist(10,9)+inst.dist(9,1);
                System.out.println(clossest);
            }*/
        res.time = time();
        res.output(inst);
    }

    public void localSearch(Solution sol) {
        int random=rnd.nextInt(12);
        switch(random){
            case 0:opt.one_insert(sol);break;
            case 1:opt.two_exchange(sol);break;
            case 2:opt.or_opt(sol);break;
            case 3:opt.two_opt(sol);break;
            case 4:opt.cross(sol);opt.repair(sol);break;
            case 5:opt.shift_one_zero(sol);opt.repair(sol);break;
            case 6:opt.shift_two_zero(sol);opt.repair(sol);break;
            case 7:opt.swap_one_one(sol);opt.repair(sol);break;
            case 8:opt.swap_one_one_new(sol);opt.repair(sol);break;
            case 9:opt.swap_two_one(sol);opt.repair(sol);break;
            case 10:opt.swap_two_two(sol);opt.repair(sol);break;
            case 11:opt.swap_two_one_new(sol);opt.repair(sol);break;

        }
        if (sol.cost < res.cost())
            res.setBestSol(sol);

    }




    Solution construct() {
        double Kmin=0;
        for(int i=0;i<inst.vertices.size();i++)
        {
            Kmin=Kmin+inst.vertices.get(i).demand;
        }
        Kmin=Kmin/inst.capacity+1;
        ArrayList<Integer> CL=new ArrayList<Integer>();
        for(int i=1;i<inst.N;i++)
            CL.add(inst.vertices.get(i).id);
        ArrayList<Route>route=new ArrayList<Route>();
        //System.out.println(CL.size());
        //System.out.println(route.size());
        for(int i=0;i<(int)Kmin;i++){
            int random=rnd.nextInt(CL.size());
            ArrayList<Point> pointnew=new ArrayList<>();
            pointnew.add(new Point(1,0));
            route.add(new Route(pointnew,inst));
            route.get(i).point.add(new Point(CL.get(random),inst.get_demand(CL.get(random))));
            CL.remove(random);
        }
        String InsertionCriterion="";
        String InsertionStrategy="";
        int criterionrandom=rnd.nextInt(2);
        int strategyrandom=rnd.nextInt(2);
        if(criterionrandom==0)
            InsertionCriterion="MCFIC";
        else if(criterionrandom==1)
            InsertionCriterion="NFIC";
        if(strategyrandom==0)
            InsertionStrategy="SIS";
        else if(strategyrandom==1)
            InsertionStrategy="PIS";
        if(InsertionStrategy=="SIS")
            route=SequentialInsertion(route,(int)Kmin,CL,InsertionCriterion);
        else if(InsertionStrategy=="PIS")
            route=ParallelInsertion(route,(int)Kmin,CL,InsertionCriterion);
        //if route is infeasible go to the initialized the CL
        ArrayList<Point> point2=new ArrayList<>();
        point2.add(new Point(1,0));
        route.add(new Route(point2,inst));
        Solution sol=new Solution(inst,route);
        System.out.println("\tconstruction > " + sol.cost + "\t time = " + time());
        return sol;

    }

    public ArrayList<Route> SequentialInsertion(ArrayList<Route> route, int Kmin, ArrayList<Integer> CL,String InsertionCriterion){
        int random=rnd.nextInt(35);
        long gama=(long)0.05*random;
        int v0=0;
        double panduan=0;
        for(int i=0;i<CL.size();i++)
            for(int j=0;j<route.size();j++) {
                if(inst.get_demand(CL.get(i))<=route.get(j).getRemain_capacity()) {
                    panduan=1;
                    break;
                }
            }
        while(CL.size()!=0){
            while(panduan==1&&CL.size()>0){
                for(int v=v0;v<Kmin&&CL.size()!=0;v++){
                    int panduanv=0;
                    for(int i=0;i<CL.size();i++)
                        if(inst.get_demand(CL.get(i)) <= route.get(v).getRemain_capacity()){
                            panduanv=1;
                            break;
                        }
                    if(panduanv==0)
                        continue;

                    double cost_best = Long.MAX_VALUE;
                    double cost=Long.MAX_VALUE;
                    int i_best=0;
                    int j_best=0;
                        for (int i = 0; i < CL.size(); i++) {
                            if (inst.get_demand(CL.get(i)) <= route.get(v).getRemain_capacity()) {
                                if(InsertionCriterion=="MCFIC") {
                                    for (int j = 0; j < route.get(v).point.size(); j++) {
                                        if (j != route.get(v).point.size()-1){
                                            cost=inst.dist[route.get(v).point.get(j).id-1][CL.get(i)-1]+inst.dist[CL.get(i)-1][route.get(v).point.get(j+1).id-1]-inst.dist[route.get(v).point.get(j).id-1][route.get(v).point.get(j+1).id-1]-gama*(inst.dist[0][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]);
                                        }
                                        else {
                                            int length=route.get(v).point.size();
                                            cost = inst.dist[route.get(v).point.get(length-1).id-1][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]-inst.dist[route.get(v).point.get(length-1).id-1][0]-gama*(inst.dist[0][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]);
                                        }
                                        if (cost<cost_best){
                                            cost_best=cost;
                                            i_best=i;
                                            j_best=j;
                                        }
                                    }
                                }
                                else if(InsertionCriterion=="NFIC"){
                                    for (int j = 0; j < route.get(v).point.size(); j++) {
                                        cost=inst.dist[route.get(v).point.get(j).id-1][CL.get(i)-1];
                                        if (cost<cost_best){
                                            cost_best=cost;
                                            i_best=i;
                                            j_best=j;
                                        }
                                    }
                                }
                            }
                        }
                    route.get(v).point.add(j_best+1,new Point(CL.get(i_best),inst.get_demand(CL.get(i_best))));
                    CL.remove(i_best);
                }
                panduan=0;
                for(int i=0;i<CL.size();i++)
                    for(int j=0;j<route.size();j++) {
                        if(inst.get_demand(CL.get(i))<=route.get(j).getRemain_capacity()) {
                            panduan=1;
                            break;
                        }
                    }
            }
            if(CL.size()>0)
            {
                ArrayList<Point> point3=new ArrayList<>();
                point3.add(new Point(1,0));
                route.add(new Route(point3,inst));
                panduan=1;
                v0=Kmin;
                Kmin=Kmin+1;

            }

        }
        return route;
    }
    public ArrayList<Route> ParallelInsertion(ArrayList<Route> route,int Kmin,ArrayList<Integer> CL, String InsertionCriterion){
        int random=rnd.nextInt(35);
        long gama=(long)0.05*random;
        int v0=0;
        double panduan=0;
        for(int i=0;i<CL.size();i++)
            for(int j=0;j<route.size();j++) {
                if(inst.get_demand(CL.get(i))<=route.get(j).getRemain_capacity()) {
                    panduan=1;
                    break;
                }
            }
        while(CL.size()!=0){
            while(panduan==1&&CL.size()>0){
                double cost_best = Long.MAX_VALUE;
                double cost=Long.MAX_VALUE;
                int i_best=0;
                int j_best=0;
                int v_best=0;
                for(int v=v0;v<Kmin&&CL.size()!=0;v++){
                    for (int i = 0; i < CL.size(); i++) {
                        if (inst.get_demand(CL.get(i)) <= route.get(v).getRemain_capacity()) {
                            if(InsertionCriterion=="MCFIC") {
                                for (int j = 0; j < route.get(v).point.size(); j++) {
                                    if (j != route.get(v).point.size()-1){
                                        cost=inst.dist[route.get(v).point.get(j).id-1][CL.get(i)-1]+inst.dist[CL.get(i)-1][route.get(v).point.get(j+1).id-1]-inst.dist[route.get(v).point.get(j).id-1][route.get(v).point.get(j+1).id-1]-gama*(inst.dist[0][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]);
                                    }
                                    else {
                                        int length=route.get(v).point.size();
                                        cost = inst.dist[route.get(v).point.get(length-1).id-1][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]-inst.dist[route.get(v).point.get(length-1).id-1][0]-gama*(inst.dist[0][CL.get(i)-1]+inst.dist[CL.get(i)-1][0]);
                                    }
                                    if (cost<cost_best){
                                        cost_best=cost;
                                        i_best=i;
                                        j_best=j;
                                        v_best=v;
                                    }
                                }
                            }
                            else if(InsertionCriterion=="NFIC"){
                                for (int j = 0; j < route.get(v).point.size(); j++) {
                                    cost=inst.dist[route.get(v).point.get(j).id-1][CL.get(i)-1];
                                    if (cost<cost_best){
                                        cost_best=cost;
                                        i_best=i;
                                        j_best=j;
                                        v_best=v;
                                    }
                                }
                            }
                        }
                    }
                }
                route.get(v_best).point.add(j_best+1,new Point(CL.get(i_best),inst.get_demand(CL.get(i_best))));
                CL.remove(i_best);
                panduan=0;
                for(int i=0;i<CL.size();i++)
                    for(int j=0;j<route.size();j++) {
                        if(inst.get_demand(CL.get(i))<=route.get(j).getRemain_capacity()) {
                            panduan=1;
                            break;
                        }
                    }
            }
            if(CL.size()>0)
            {
                ArrayList<Point> point3=new ArrayList<>();
                point3.add(new Point(1,0));
                route.add(new Route(point3,inst));
                panduan=1;
                Kmin=Kmin+1;
            }
        }
        return route;
    }



    public void perturb(Solution sol) {
        //Muliple-k-Split
        int random=rnd.nextInt(3);
        random=random+5;
        ArrayList<Vertex> CL=new ArrayList<>();
        ArrayList<Vertex> vertex=inst.vertices;
        for(int i=1;i<=random;i++){
            int choose=rnd.nextInt(inst.N);
            if(vertex.get(choose).id==1){
                i--;
                continue;
            }
            int panduan=1;
            for(int j=0;j<CL.size();j++){
                if(CL.get(j).id==vertex.get(choose).id)
                    panduan=0;
            }
            if(panduan==1)
                CL.add(vertex.get(choose));
            else if(panduan==0)
                i--;
        }
        for(int i=1;i<=random;i++){
            int id=CL.get(i-1).id;
            for(int r=0;r<sol.route.size();r++)
                for(int p=0;p<sol.route.get(r).point.size();){
                    if(sol.route.get(r).point.get(p).id!=id) {
                        p++;
                        continue;
                    }
                    sol.route.get(r).point.remove(p);
                }
        }
        for(int i=1;i<=random;i++){
            int id=CL.get(i-1).id;
            double demand=CL.get(i-1).demand;
            opt.SplitReinsertion(sol,id,demand);

        }
        sol.calculateCost(inst);
    }

    double time() {
        return 0.001 * (System.currentTimeMillis() - start);
    }

    boolean timeout() {
        return System.currentTimeMillis() - start > 1000 * param.time_limit;
    }

}