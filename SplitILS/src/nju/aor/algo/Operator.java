package nju.aor.algo;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import nju.aor.common.*;
import sun.security.x509.PolicyConstraintsExtension;

import java.util.*;

/**
 * Operator
 *  - local search neighborhood operators
 *  - perturbation/other operators
 *  - TODO: implement your operators
 */
public class Operator {
    Instance inst;
    Result res;
    Random rnd;

    Operator(Instance inst, Result res, Random rnd){
        this.inst = inst;
        this.res = res;
        this.rnd = rnd;
    }

    public void two_opt(Solution sol) {
        // todo: improve two_opt
        long start = System.currentTimeMillis();
        for(int r=0;r<sol.route.size();r++)
        for (int left = 1; left + 2 < sol.route.get(r).point.size(); left++) {
            for (int right = sol.route.get(r).point.size()-1; right > left + 2; right--) {
                int x1 = sol.route.get(r).point.get(left - 1).id, x2 =sol.route.get(r).point.get(left ).id;
                int y1 = sol.route.get(r).point.get(right - 1).id, y2 = sol.route.get(r).point.get(right).id;
                double diff = inst.dist[x1-1][y1-1] + inst.dist[x2-1][y2-1] - inst.dist[x1-1][x2-1] - inst.dist[y1-1][y2-1];
                if (diff >= 0) continue;

                int p = left, q = right - 1;
                while (p < q) {
                    Point v1 = new Point(sol.route.get(r).point.get(p).id,sol.route.get(r).point.get(p).MeetDemand);
                    Point v2 = new Point(sol.route.get(r).point.get(q).id,sol.route.get(r).point.get(q).MeetDemand);
                    sol.route.get(r).point.set(p, v2);
                    sol.route.get(r).point.set(q, v1);
                    ++p;
                    --q;
                }
                sol.calculateCost(inst);

            }
        }
        res.time_two_opt += 0.001 * (System.currentTimeMillis() - start);
    }

    public void or_opt(Solution sol){
        long start=System.currentTimeMillis();
        for(int r=0;r<sol.route.size();r++)
        for(int i=1;i+5<sol.route.get(r).point.size();i++)
            for(int j=i+4;j+1<sol.route.get(r).point.size();j++)
            {
                int x1=sol.route.get(r).point.get(i).id; int x2=sol.route.get(r).point.get(i+1).id;
                int y1=sol.route.get(r).point.get(i+2).id;int y2=sol.route.get(r).point.get(i+3).id;
                int z1=sol.route.get(r).point.get(j).id;int z2=sol.route.get(r).point.get(j+1).id;
                double differ=inst.dist[x1-1][y2-1]+inst.dist[z1-1][x2-1]+inst.dist[y1-1][z2-1]-inst.dist[x1-1][x2-1]-inst.dist[y1-1][y2-1]-inst.dist[z1-1][z2-1];
                if(differ>=0)continue;
                Point v1=new Point(sol.route.get(r).point.get(i+1).id,sol.route.get(r).point.get(i+1).MeetDemand);
                Point v2=new Point(sol.route.get(r).point.get(i+2).id,sol.route.get(r).point.get(i+2).MeetDemand);
                sol.route.get(r).point.add(j+1,v1);
                sol.route.get(r).point.add(j+2,v2);
                sol.route.get(r).point.remove(i+1);
                sol.route.get(r).point.remove(i+1);
                sol.calculateCost(inst);
            }
        res.time_or_opt+=0.001*(System.currentTimeMillis()-start);
    }

    public void one_insert(Solution sol){
        long start=System.currentTimeMillis();
        for(int r=0;r<sol.route.size();r++)
            for(int i=2;i+2<sol.route.get(r).point.size();i++)
                for(int j=i+1;j+1<sol.route.get(r).point.size();j++)
                {
                    int x1=sol.route.get(r).point.get(i-1).id;int x2=sol.route.get(r).point.get(i).id;int x3=sol.route.get(r).point.get(i+1).id;
                    int y1=sol.route.get(r).point.get(j).id;int y2=sol.route.get(r).point.get(j+1).id;
                    double differ=inst.dist[y1-1][x2-1]+inst.dist[x2-1][y2-1]+inst.dist[x1-1][x3-1]-inst.dist[x1-1][x2-1]-inst.dist[x2-1][x3-1]-inst.dist[y1-1][y2-1];
                    if(differ>=0)continue;
                    Point a=new Point(sol.route.get(r).point.get(i).id,sol.route.get(r).point.get(i).MeetDemand);
                    sol.route.get(r).point.add(j+1,a);
                    sol.route.get(r).point.remove(i);
                    sol.calculateCost(inst);
                }
        res.time_one_insert+=0.001*(System.currentTimeMillis()-start);
    }

    public void two_exchange(Solution sol){
        long start=System.currentTimeMillis();
        for(int r=0;r<sol.route.size();r++)
        for(int i=1;i+4<sol.route.get(r).point.size();i++)
            for(int j=i+2;j+2<sol.route.get(r).point.size();j++)
            {
                int x1=sol.route.get(r).point.get(i).id;int x2=sol.route.get(r).point.get(i+1).id;int x3=sol.route.get(r).point.get(i+2).id;
                int y1=sol.route.get(r).point.get(j).id;int y2=sol.route.get(r).point.get(j+1).id;int y3=sol.route.get(r).point.get(j+2).id;
                double differ=inst.dist[x1-1][y2-1]+inst.dist[y2-1][x3-1]+inst.dist[y1-1][x2-1]+inst.dist[x2-1][y3-1]-inst.dist[x1-1][x2-1]-inst.dist[x2-1][x3-1]-inst.dist[y1-1][y2-1]-inst.dist[y2-1][y3-1];
                if(differ>=0)continue;
                Point v1=new Point(sol.route.get(r).point.get(i+1).id,sol.route.get(r).point.get(i+1).MeetDemand);
                Point v2=new Point(sol.route.get(r).point.get(j+1).id,sol.route.get(r).point.get(j+1).MeetDemand);
                sol.route.get(r).point.set(i+1,v2);
                sol.route.get(r).point.set(j+1,v1);
                sol.calculateCost(inst);
            }
        res.time_two_exchange+=0.001*(System.currentTimeMillis()-start);
    }
    public void shift_one_zero(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size();r1++)
            for(int r2=0;r2<sol.route.size();r2++){
                if(r1==r2)
                    continue;
                else{
                    for(int i=1;i<sol.route.get(r1).point.size();i++){
                        if(sol.route.get(r1).point.get(i).MeetDemand>sol.route.get(r2).getRemain_capacity())
                            continue;
                        for(int j=1;j<sol.route.get(r2).point.size();j++){
                            Point v1=new Point(sol.route.get(r1).point.get(i-1).id,sol.route.get(r1).point.get(i-1).MeetDemand);
                            Point v2=new Point(sol.route.get(r1).point.get(i).id,sol.route.get(r1).point.get(i).MeetDemand);
                            Point v3;
                            if(i==sol.route.get(r1).point.size()-1)
                                v3=new Point(sol.route.get(r1).point.get(0).id,sol.route.get(r1).point.get(0).MeetDemand);
                            else
                                v3=new Point(sol.route.get(r1).point.get(i+1).id,sol.route.get(r1).point.get(i+1).MeetDemand);
                            Point w1=new Point(sol.route.get(r2).point.get(j-1).id,sol.route.get(r2).point.get(j-1).MeetDemand);
                            Point w2=new Point(sol.route.get(r2).point.get(j).id,sol.route.get(r2).point.get(j).MeetDemand);
                            double differ=inst.dist[v1.id-1][v2.id-1]+inst.dist[v2.id-1][v3.id-1]+inst.dist[w1.id-1][w2.id-1]-inst.dist[v1.id-1][v3.id-1]-inst.dist[w1.id-1][v2.id-1]-inst.dist[v2.id-1][w2.id-1];
                            if(differ<0&&i!=sol.route.get(r1).point.size())
                                continue;
                            else {
                                sol.route.get(r2).point.add(j, v2);
                                sol.route.get(r1).point.remove(i);
                                sol.calculateCost(inst);
                                break;
                            }
                        }

                    }

                }

            }
        res.time_shift_one_zero+=0.001*(System.currentTimeMillis()-start);
    }
    public void swap_one_one(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size()-1;r1++)
            for(int r2=r1+1;r2<sol.route.size();r2++)
                for(int i=1;i<sol.route.get(r1).point.size();i++)
                    for(int j=1;j<sol.route.get(r2).point.size();j++){
                        if(sol.route.get(r1).point.get(i).MeetDemand-sol.route.get(r2).point.get(j).MeetDemand>sol.route.get(r2).getRemain_capacity())
                            continue;
                        if(sol.route.get(r2).point.get(j).MeetDemand-sol.route.get(r1).point.get(i).MeetDemand>sol.route.get(r1).getRemain_capacity())
                            continue;
                        Point v1=new Point(sol.route.get(r1).point.get(i-1).id,sol.route.get(r1).point.get(i-1).MeetDemand);
                        Point v2=new Point(sol.route.get(r1).point.get(i).id,sol.route.get(r1).point.get(i).MeetDemand);
                        Point v3;
                        if(i==sol.route.get(r1).point.size()-1)
                            v3=new Point(sol.route.get(r1).point.get(0).id,sol.route.get(r1).point.get(0).MeetDemand);
                        else
                            v3=new Point(sol.route.get(r1).point.get(i+1).id,sol.route.get(r1).point.get(i+1).MeetDemand);
                        Point w1=new Point(sol.route.get(r2).point.get(j-1).id,sol.route.get(r2).point.get(j-1).MeetDemand);
                        Point w2=new Point(sol.route.get(r2).point.get(j).id,sol.route.get(r2).point.get(j).MeetDemand);
                        Point w3;
                        if(j==sol.route.get(r2).point.size()-1)
                            w3=new Point(sol.route.get(r2).point.get(0).id,sol.route.get(r2).point.get(0).MeetDemand);
                        else
                            w3=new Point(sol.route.get(r2).point.get(j+1).id,sol.route.get(r2).point.get(j+1).MeetDemand);
                        double differ=inst.dist[v1.id-1][v2.id-1]+inst.dist[v2.id-1][v3.id-1]+inst.dist[w1.id-1][w2.id-1]+inst.dist[w2.id-1][w3.id-1]-inst.dist[v1.id-1][w2.id-1]-inst.dist[w2.id-1][v3.id-1]-inst.dist[w1.id-1][v2.id-1]-inst.dist[v2.id-1][w3.id-1];
                        if(differ<=0)
                            continue;
                        sol.route.get(r1).point.add(i,w2);
                        sol.route.get(r2).point.add(j,v2);
                        sol.route.get(r1).point.remove(i+1);
                        sol.route.get(r2).point.remove(j+1);
                        sol.calculateCost(inst);
                    }
        res.time_swap_one_one+=0.001*(System.currentTimeMillis()-start);

    }
    public void shift_two_zero(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size();r1++){
            if(sol.route.get(r1).point.size()<3)
                continue;
            for(int r2=0;r2<sol.route.size();r2++){
                if(r1==r2)
                    continue;
                else {
                    for (int i = 1; i < sol.route.get(r1).point.size() - 1; i++) {
                        if (sol.route.get(r1).point.get(i).MeetDemand + sol.route.get(r1).point.get(i + 1).MeetDemand > sol.route.get(r2).getRemain_capacity())
                            continue;
                        for (int j = 1; j < sol.route.get(r2).point.size(); j++) {
                            Point v1 = new Point(sol.route.get(r1).point.get(i - 1).id, sol.route.get(r1).point.get(i - 1).MeetDemand);
                            Point v2 = new Point(sol.route.get(r1).point.get(i).id, sol.route.get(r1).point.get(i).MeetDemand);
                            Point v3=new Point(sol.route.get(r1).point.get(i+1).id,sol.route.get(r1).point.get(i+1).MeetDemand);
                            Point v4;
                            if (i == sol.route.get(r1).point.size() - 2)
                                v4 = new Point(sol.route.get(r1).point.get(0).id, sol.route.get(r1).point.get(0).MeetDemand);
                            else
                                v4 = new Point(sol.route.get(r1).point.get(i + 2).id, sol.route.get(r1).point.get(i + 2).MeetDemand);
                            Point w1 = new Point(sol.route.get(r2).point.get(j - 1).id, sol.route.get(r2).point.get(j - 1).MeetDemand);
                            Point w2 = new Point(sol.route.get(r2).point.get(j).id, sol.route.get(r2).point.get(j).MeetDemand);
                            double differ1 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] - inst.dist[v1.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v2.id - 1] - inst.dist[v3.id - 1][w2.id - 1];
                            double differ2 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] - inst.dist[v1.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v3.id - 1] - inst.dist[v2.id - 1][w2.id - 1];
                            if (differ1 <= 0&&differ2<=0 && i != sol.route.get(r1).point.size())
                                continue;
                            else if(differ1>0&&differ1>=differ2){
                                sol.route.get(r2).point.add(j, v2);
                                sol.route.get(r2).point.add(j+1,v3);
                                sol.route.get(r1).point.remove(i+1);
                                sol.route.get(r1).point.remove(i);
                                sol.calculateCost(inst);
                                break;
                            }
                            else if(differ2>0&&differ2>differ1){
                                sol.route.get(r2).point.add(j, v3);
                                sol.route.get(r2).point.add(j+1,v2);
                                sol.route.get(r1).point.remove(i+1);
                                sol.route.get(r1).point.remove(i);
                                sol.calculateCost(inst);
                                break;
                            }
                        }

                    }
                }
                }

            }
        res.time_shift_two_zero+=0.001*(System.currentTimeMillis()-start);

    }
    public void swap_two_one(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size()-1;r1++) {
            if(sol.route.get(r1).point.size()<4)
                continue;
            for (int r2 = r1 + 1; r2 < sol.route.size(); r2++)
                for (int i = 1; i < sol.route.get(r1).point.size()-1; i++)
                    for (int j = 1; j < sol.route.get(r2).point.size(); j++) {
                        if(sol.route.get(r1).point.size()<4||i>=sol.route.get(r1).point.size()-2)
                            break;
                        if(sol.route.get(r1).point.get(i).MeetDemand+sol.route.get(r1).point.get(i+1).MeetDemand-sol.route.get(r2).point.get(j).MeetDemand>sol.route.get(r2).getRemain_capacity())
                            continue;
                        if(sol.route.get(r2).point.get(j).MeetDemand-sol.route.get(r1).point.get(i).MeetDemand-sol.route.get(r1).point.get(i+1).MeetDemand>sol.route.get(r1).getRemain_capacity())
                            continue;
                        Point v1 = new Point(sol.route.get(r1).point.get(i - 1).id, sol.route.get(r1).point.get(i - 1).MeetDemand);
                        Point v2 = new Point(sol.route.get(r1).point.get(i).id, sol.route.get(r1).point.get(i).MeetDemand);
                        Point v3 = new Point(sol.route.get(r1).point.get(i + 1).id, sol.route.get(r1).point.get(i + 1).MeetDemand);
                        Point v4;
                        if (i == sol.route.get(r1).point.size() - 2)
                            v4 = new Point(sol.route.get(r1).point.get(0).id, sol.route.get(r1).point.get(0).MeetDemand);
                        else
                            v4 = new Point(sol.route.get(r1).point.get(i + 2).id, sol.route.get(r1).point.get(i + 2).MeetDemand);
                        Point w1 = new Point(sol.route.get(r2).point.get(j - 1).id, sol.route.get(r2).point.get(j - 1).MeetDemand);
                        Point w2 = new Point(sol.route.get(r2).point.get(j).id, sol.route.get(r2).point.get(j).MeetDemand);
                        Point w3;
                        if (j == sol.route.get(r2).point.size() - 1)
                            w3 = new Point(sol.route.get(r2).point.get(0).id, sol.route.get(r2).point.get(0).MeetDemand);
                        else
                            w3 = new Point(sol.route.get(r2).point.get(j + 1).id, sol.route.get(r2).point.get(j + 1).MeetDemand);
                        double differ1 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w2.id - 1][w3.id - 1] - inst.dist[v1.id - 1][w2.id - 1] - inst.dist[w2.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v2.id - 1] - inst.dist[v3.id - 1][w3.id - 1];
                        double differ2 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w2.id - 1][w3.id - 1] - inst.dist[v1.id - 1][w2.id - 1] - inst.dist[w2.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v3.id - 1] - inst.dist[v2.id - 1][w3.id - 1];
                        if (differ1 <= 0 && differ2 <= 0)
                            continue;
                        else if (differ1 > 0 && differ1 >= differ2) {
                            sol.route.get(r1).point.add(i, w2);
                            sol.route.get(r2).point.add(j, v2);
                            sol.route.get(r2).point.add(j + 1, v3);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r1).point.remove(i + 1);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        }
                        else if (differ2 > 0 && differ2 > differ1) {
                            sol.route.get(r1).point.add(i, w2);
                            sol.route.get(r2).point.add(j, v3);
                            sol.route.get(r2).point.add(j + 1, v2);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r1).point.remove(i + 1);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        }
                    }
        }
        res.time_swap_two_one+=0.001*(System.currentTimeMillis()-start);
    }
    public void swap_two_two(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size()-1;r1++) {
            if (sol.route.get(r1).point.size() < 4)
                continue;
            for (int r2 = r1 + 1; r2 < sol.route.size(); r2++) {
                if(sol.route.get(r2).point.size()<4)
                    continue;
                for (int i = 1; i < sol.route.get(r1).point.size() - 1; i++)
                    for (int j = 1; j < sol.route.get(r2).point.size()-1; j++) {
                        if (sol.route.get(r1).point.size() < 4 || i >= sol.route.get(r1).point.size() - 2)
                            break;
                        if(sol.route.get(r2).point.size()<4||j>=sol.route.get(r2).point.size()-2)
                            continue;
                        if (sol.route.get(r1).point.get(i).MeetDemand + sol.route.get(r1).point.get(i + 1).MeetDemand - sol.route.get(r2).point.get(j).MeetDemand-sol.route.get(r2).point.get(j+1).MeetDemand > sol.route.get(r2).getRemain_capacity())
                            continue;
                        if (sol.route.get(r2).point.get(j).MeetDemand+sol.route.get(r2).point.get(j+1).MeetDemand - sol.route.get(r1).point.get(i).MeetDemand - sol.route.get(r1).point.get(i + 1).MeetDemand > sol.route.get(r1).getRemain_capacity())
                            continue;
                        Point v1 = new Point(sol.route.get(r1).point.get(i - 1).id, sol.route.get(r1).point.get(i - 1).MeetDemand);
                        Point v2 = new Point(sol.route.get(r1).point.get(i).id, sol.route.get(r1).point.get(i).MeetDemand);
                        Point v3 = new Point(sol.route.get(r1).point.get(i + 1).id, sol.route.get(r1).point.get(i + 1).MeetDemand);
                        Point v4;
                        if (i == sol.route.get(r1).point.size() - 2)
                            v4 = new Point(sol.route.get(r1).point.get(0).id, sol.route.get(r1).point.get(0).MeetDemand);
                        else
                            v4 = new Point(sol.route.get(r1).point.get(i + 2).id, sol.route.get(r1).point.get(i + 2).MeetDemand);
                        Point w1 = new Point(sol.route.get(r2).point.get(j - 1).id, sol.route.get(r2).point.get(j - 1).MeetDemand);
                        Point w2 = new Point(sol.route.get(r2).point.get(j).id, sol.route.get(r2).point.get(j).MeetDemand);
                        Point w3=new Point(sol.route.get(r2).point.get(j+1).id,sol.route.get(r2).point.get(j+1).MeetDemand);
                        Point w4;
                        if (j == sol.route.get(r2).point.size() - 2)
                            w4 = new Point(sol.route.get(r2).point.get(0).id, sol.route.get(r2).point.get(0).MeetDemand);
                        else
                            w4 = new Point(sol.route.get(r2).point.get(j + 2).id, sol.route.get(r2).point.get(j + 2).MeetDemand);
                        double differ1 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w3.id - 1][w4.id - 1] - inst.dist[v1.id - 1][w2.id - 1] - inst.dist[w3.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v2.id - 1] - inst.dist[v3.id - 1][w4.id - 1];
                        double differ2 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w3.id - 1][w4.id - 1] - inst.dist[v1.id - 1][w3.id - 1] - inst.dist[w2.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v2.id - 1] - inst.dist[v3.id - 1][w4.id - 1];
                        double differ3 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w3.id - 1][w4.id - 1] - inst.dist[v1.id - 1][w2.id - 1] - inst.dist[w3.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v3.id - 1] - inst.dist[v2.id - 1][w4.id - 1];
                        double differ4 = inst.dist[v1.id - 1][v2.id - 1] + inst.dist[v3.id - 1][v4.id - 1] + inst.dist[w1.id - 1][w2.id - 1] + inst.dist[w3.id - 1][w4.id - 1] - inst.dist[v1.id - 1][w3.id - 1] - inst.dist[w2.id - 1][v4.id - 1] - inst.dist[w1.id - 1][v3.id - 1] - inst.dist[v2.id - 1][w4.id - 1];
                        if (differ1 <= 0 && differ2 <= 0&&differ3<=0&&differ4<=0)
                            continue;
                        else if (differ1 > 0 && differ1 >= differ2&& differ1 >= differ3&& differ1 >= differ4) {
                            sol.route.get(r1).point.add(i, w2);
                            sol.route.get(r1).point.add(i+1,w3);
                            sol.route.get(r2).point.add(j, v2);
                            sol.route.get(r2).point.add(j + 1, v3);
                            sol.route.get(r1).point.remove(i + 3);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r2).point.remove(j + 3);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        } else if (differ2 > 0 && differ2 > differ1&& differ2 >= differ3&& differ2 >= differ4) {
                            sol.route.get(r1).point.add(i, w3);
                            sol.route.get(r1).point.add(i+1,w2);
                            sol.route.get(r2).point.add(j, v2);
                            sol.route.get(r2).point.add(j + 1, v3);
                            sol.route.get(r1).point.remove(i + 3);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r2).point.remove(j + 3);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        }
                        else if (differ3 > 0 && differ3 > differ1&& differ3 >= differ2&& differ3 >= differ4){
                            sol.route.get(r1).point.add(i, w2);
                            sol.route.get(r1).point.add(i+1,w3);
                            sol.route.get(r2).point.add(j, v3);
                            sol.route.get(r2).point.add(j + 1, v2);
                            sol.route.get(r1).point.remove(i + 3);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r2).point.remove(j + 3);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        }
                        else if (differ4 > 0 && differ4 > differ1&& differ4 >= differ2&& differ4 >= differ3){
                            sol.route.get(r1).point.add(i, w3);
                            sol.route.get(r1).point.add(i+1,w2);
                            sol.route.get(r2).point.add(j, v3);
                            sol.route.get(r2).point.add(j + 1, v2);
                            sol.route.get(r1).point.remove(i + 3);
                            sol.route.get(r1).point.remove(i + 2);
                            sol.route.get(r2).point.remove(j + 3);
                            sol.route.get(r2).point.remove(j + 2);
                            sol.calculateCost(inst);
                        }
                    }
            }
        }
        res.time_swap_two_two+=0.001*(System.currentTimeMillis()-start);

    }
    public void cross(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size()-1;r1++){
            if(sol.route.get(r1).point.size()<3)
                continue;
            for(int r2=r1+1;r2<sol.route.size();r2++){
                if(sol.route.get(r2).point.size()<3)
                    continue;
                for(int i=1;i<sol.route.get(r1).point.size()-1;i++)
                    for(int j=1;j<sol.route.get(r2).point.size()-1;j++){
                        Point v1=new Point(sol.route.get(r1).point.get(i).id,sol.route.get(r1).point.get(i).MeetDemand);
                        Point v2=new Point(sol.route.get(r1).point.get(i+1).id,sol.route.get(r1).point.get(i+1).MeetDemand);
                        Point w1=new Point(sol.route.get(r2).point.get(j).id,sol.route.get(r2).point.get(j).MeetDemand);
                        Point w2=new Point(sol.route.get(r2).point.get(j+1).id,sol.route.get(r2).point.get(j+1).MeetDemand);
                        double r1demand=0;
                        double r2demand=0;
                        for(int m=i+1;m<sol.route.get(r1).point.size();m++)
                            r1demand=r1demand+sol.route.get(r1).point.get(m).MeetDemand;
                        for(int n=j+1;n<sol.route.get(r2).point.size();n++)
                            r2demand=r2demand+sol.route.get(r2).point.get(n).MeetDemand;
                        if(r1demand-r2demand>sol.route.get(r2).getRemain_capacity())
                            continue;
                        if(r2demand-r1demand>sol.route.get(r1).getRemain_capacity())
                            continue;
                        double differ=inst.dist[sol.route.get(r1).point.get(i).id-1][sol.route.get(r1).point.get(i+1).id-1]+inst.dist[sol.route.get(r2).point.get(j).id-1][sol.route.get(r2).point.get(j+1).id-1]-inst.dist[sol.route.get(r1).point.get(i).id-1][sol.route.get(r2).point.get(j+1).id-1]-inst.dist[sol.route.get(r1).point.get(i+1).id-1][sol.route.get(r2).point.get(j).id-1];
                        if(differ<=0)
                            continue;
                        int k=i+1;
                        int l=j+1;
                        while(k<sol.route.get(r1).point.size()&&l<sol.route.get(r2).point.size()){
                            sol.route.get(r1).point.add(k,sol.route.get(r2).point.get(l));
                            sol.route.get(r2).point.add(l,sol.route.get(r1).point.get(k+1));
                            sol.route.get(r1).point.remove(k+1);
                            sol.route.get(r2).point.remove(l+1);
                            k++;
                            l++;
                        }
                        if(k==sol.route.get(r1).point.size()&&l<sol.route.get(r2).point.size()){
                            int initialcount=sol.route.get(r2).point.size()-l;
                            for(int count=0;count<initialcount;count++){
                                sol.route.get(r1).point.add(k,sol.route.get(r2).point.get(l));
                                sol.route.get(r2).point.remove(l);
                                k++;
                            }

                        }
                        else if(k<sol.route.get(r1).point.size()&&l==sol.route.get(r2).point.size()){
                            int initialcount=sol.route.get(r1).point.size()-k;
                            for(int count=0;count<initialcount;count++){
                                sol.route.get(r2).point.add(l,sol.route.get(r1).point.get(k));
                                sol.route.get(r1).point.remove(k);
                                l++;
                            }
                        }
                        sol.calculateCost(inst);
                    }
            }
        }
        res.time_cross+=0.001*(System.currentTimeMillis()-start);

    }
    public void swap_one_one_new(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size()-1;r1++)
            for(int r2=r1+1;r2<sol.route.size();r2++)
                for(int p1=1;p1<sol.route.get(r1).point.size();p1++)
                    for(int p2=1;p2<sol.route.get(r2).point.size();p2++){
                        Point v1=sol.route.get(r1).point.get(p1-1);
                        Point v2=sol.route.get(r1).point.get(p1);
                        Point v3;
                        if(p1==sol.route.get(r1).point.size()-1)
                            v3=sol.route.get(r1).point.get(0);
                        else
                            v3=sol.route.get(r1).point.get(p1+1);
                        Point w1=sol.route.get(r2).point.get(p2-1);
                        Point w2=sol.route.get(r2).point.get(p2);
                        Point w3;
                        if(p2==sol.route.get(r2).point.size()-1)
                            w3=sol.route.get(r2).point.get(0);
                        else
                            w3=sol.route.get(r2).point.get(p2+1);
                        if(v2.MeetDemand>w2.MeetDemand){
                            double differ=inst.dist[v1.id-1][w2.id-1]+inst.dist[w2.id-1][v2.id-1]-inst.dist[v1.id-1][v2.id-1]+inst.dist[w1.id-1][v2.id-1]+inst.dist[v2.id-1][w3.id-1]-inst.dist[w1.id-1][w2.id-1]-inst.dist[w2.id-1][w3.id-1];
                            if(differ>=0)
                                continue;
                            sol.route.get(r1).point.add(p1,w2);
                            sol.route.get(r1).point.add(p1+1,new Point(v2.id,v2.MeetDemand-w2.MeetDemand));
                            sol.route.get(r1).point.remove(p1+2);
                            sol.route.get(r2).point.remove(p2);
                            sol.route.get(r2).point.add(p2,new Point(v2.id,w2.MeetDemand));
                        }
                        else if(v2.MeetDemand<w2.MeetDemand){
                            double differ=inst.dist[w1.id-1][v2.id-1]+inst.dist[v2.id-1][w2.id-1]-inst.dist[w1.id-1][w2.id-1]+inst.dist[v1.id-1][w2.id-1]+inst.dist[w2.id-1][v3.id-1]-inst.dist[v1.id-1][v2.id-1]-inst.dist[v2.id-1][v3.id-1];
                            if(differ>=0)
                                continue;
                            sol.route.get(r2).point.add(p2,v2);
                            sol.route.get(r2).point.add(p2+1,new Point(w2.id,w2.MeetDemand-v2.MeetDemand));
                            sol.route.get(r2).point.remove(p2+2);
                            sol.route.get(r1).point.remove(p1);
                            sol.route.get(r1).point.add(p1,new Point(w2.id,v2.MeetDemand));
                        }
                        sol.calculateCost(inst);

                    }
        res.time_swap_one_one_new+=0.001*(System.currentTimeMillis()-start);
    }
    public void swap_two_one_new(Solution sol){
        long start=System.currentTimeMillis();
        for(int r1=0;r1<sol.route.size();r1++)
            for(int r2=0;r2<sol.route.size();r2++) {
                if(r1==r2)
                    continue;
                if(sol.route.get(r1).point.size()<3)
                    continue;
                for (int p1 = 1; p1 < sol.route.get(r1).point.size() - 1; p1++)
                    for (int p2=1;p2<sol.route.get(r2).point.size();p2++){
                        if(sol.route.get(r1).point.size()<3||p1+1>=sol.route.get(r1).point.size())
                            continue;
                        Point v1=sol.route.get(r1).point.get(p1-1);
                        Point v2=sol.route.get(r1).point.get(p1);
                        Point v3=sol.route.get(r1).point.get(p1+1);
                        Point v4;
                        if(p1==sol.route.get(r1).point.size()-2)
                            v4=sol.route.get(r1).point.get(0);
                        else
                            v4=sol.route.get(r1).point.get(p1+2);
                        Point w1=sol.route.get(r2).point.get(p2-1);
                        Point w2=sol.route.get(r2).point.get(p2);
                        Point w3;
                        if(p2==sol.route.get(r2).point.size()-1)
                            w3=sol.route.get(r2).point.get(0);
                        else
                            w3=sol.route.get(r2).point.get(p2+1);
                        if((v2.MeetDemand+v3.MeetDemand)>w2.MeetDemand&&v2.MeetDemand<w2.MeetDemand){
                            double differ=inst.dist[w1.id-1][v2.id-1]+inst.dist[v3.id-1][w3.id-1]-inst.dist[w1.id-1][w2.id-1]-inst.dist[w2.id-1][w3.id-1]+inst.dist[v1.id-1][w2.id-1]+inst.dist[w2.id-1][v3.id-1]-inst.dist[v1.id-1][v2.id-1]-inst.dist[v2.id-1][v3.id-1];
                            if(differ>=0)
                                continue;
                            sol.route.get(r1).point.add(p1,w2);
                            sol.route.get(r1).point.add(p1+1,new Point(v3.id,v2.MeetDemand+v3.MeetDemand-w2.MeetDemand));
                            sol.route.get(r1).point.remove(p1+2);
                            sol.route.get(r1).point.remove(p1+2);
                            sol.route.get(r2).point.add(p2,v2);
                            sol.route.get(r2).point.add(p2+1,new Point(v3.id,w2.MeetDemand-v2.MeetDemand));
                            sol.route.get(r2).point.remove(p2+2);

                        }
                        else if((v2.MeetDemand+v3.MeetDemand)<w2.MeetDemand){
                            double differ=inst.dist[w1.id-1][v2.id-1]+inst.dist[v3.id-1][w2.id-1]-inst.dist[w1.id-1][w2.id-1]+inst.dist[v1.id-1][w2.id-1]+inst.dist[w2.id-1][v4.id-1]-inst.dist[v1.id-1][v2.id-1]-inst.dist[v3.id-1][v4.id-1];
                            if(differ>=0)
                                continue;
                            sol.route.get(r2).point.add(p2,v2);
                            sol.route.get(r2).point.add(p2+1,v3);
                            sol.route.get(r2).point.add(p2+2,new Point(w2.id,w2.MeetDemand-v2.MeetDemand-v3.MeetDemand));
                            sol.route.get(r2).point.remove(p2+3);
                            sol.route.get(r1).point.add(p1+2,new Point(w2.id,v2.MeetDemand+v3.MeetDemand));
                            sol.route.get(r1).point.remove(p1+1);
                            sol.route.get(r1).point.remove(p1);
                        }
                        sol.calculateCost(inst);
                    }
            }
        res.time_swap_two_one_new+=0.001*(System.currentTimeMillis()-start);
    }


    public void repair(Solution sol){
        for(int r=0;r<sol.route.size();r++)
            for(int i=2;i<=inst.N;i++) {
                int count=0;
                for (int j = 0; j < sol.route.get(r).point.size(); j++) {
                    if(sol.route.get(r).point.get(j).id==i)
                        count++;
                }
                if (count>1){
                    double max_cost=-Double.MAX_VALUE;
                    int location=0;
                    ArrayList<Integer> locations=new ArrayList<>();
                    for(int j=1;j<sol.route.get(r).point.size();j++){
                        if(sol.route.get(r).point.get(j).id==i) {
                            locations.add(j);
                            double cost=0;
                            if (j != sol.route.get(r).point.size() - 1) {
                                cost = inst.dist[sol.route.get(r).point.get(j - 1).id - 1][sol.route.get(r).point.get(j).id - 1] + inst.dist[sol.route.get(r).point.get(j).id - 1][sol.route.get(r).point.get(j + 1).id - 1] - inst.dist[sol.route.get(r).point.get(j - 1).id - 1][sol.route.get(r).point.get(j + 1).id - 1];
                            }
                            else if(j==sol.route.get(r).point.size()-1){
                                cost = inst.dist[sol.route.get(r).point.get(j - 1).id - 1][sol.route.get(r).point.get(j).id - 1] + inst.dist[sol.route.get(r).point.get(j).id - 1][sol.route.get(r).point.get(0).id - 1] - inst.dist[sol.route.get(r).point.get(j - 1).id - 1][sol.route.get(r).point.get(0).id - 1];
                            }
                            if(cost>=max_cost)
                            {
                                max_cost=cost;
                                location=j;
                            }
                        }
                    }
                    Collections.sort(locations);
                    for(int l=locations.size()-1;l>=0;l--){
                        if(locations.get(l)==location){
                            continue;
                        }
                        else if(locations.get(l)!=location)
                        {
                            sol.route.get(r).point.get(location).MeetDemand=sol.route.get(r).point.get(location).MeetDemand+sol.route.get(r).point.get(locations.get(l)).MeetDemand;
                            int delete=locations.get(l);
                            sol.route.get(r).point.remove(delete);
                            if(locations.get(l)<location)
                                location--;
                        }
                    }
                }
            }
        sol.calculateCost(inst);
    }



    public void SplitReinsertion(Solution sol,int id,double demand){
        ArrayList<Integer> L=new ArrayList<Integer>();
        ArrayList<Double> A=new ArrayList<Double>();
        ArrayList<Double> U=new ArrayList<Double>();
        ArrayList<Integer>I=new ArrayList<>();
        ArrayList<Order>order=new ArrayList<>();
        int Ks=sol.route.size();
        double sumRsdidual=0;
        for(int r=0;r<Ks;r++){
            double ar=sol.route.get(r).getRemain_capacity();
            if(ar>0){
                L.add(r);
                A.add(ar);
                sumRsdidual=sumRsdidual+ar;
            }
        }
        if(sumRsdidual>=demand){
            for(int r=0;r<L.size();r++){
                double mincost= Long.MAX_VALUE;
                int mini=0;
                for(int i=0;i<sol.route.get(L.get(r)).point.size();i++){
                    double cost=0;
                    if(i==sol.route.get(L.get(r)).point.size()-1)
                        cost=inst.dist[sol.route.get(L.get(r)).point.get(i).id-1][id-1]+inst.dist[id-1][0]-inst.dist[sol.route.get(L.get(r)).point.get(i).id-1][0];
                    else
                        cost=inst.dist[sol.route.get(L.get(r)).point.get(i).id-1][id-1]+inst.dist[id-1][sol.route.get(L.get(r)).point.get(i+1).id-1]-inst.dist[sol.route.get(L.get(r)).point.get(i).id-1][sol.route.get(L.get(r)).point.get(i+1).id-1];
                    if(cost<mincost) {
                        mincost = cost;
                        mini=i;
                    }
                }
                U.add(mincost);
                I.add(mini);
            }
            for(int i=0;i<L.size();i++)
                order.add(new Order(L.get(i),A.get(i),U.get(i),I.get(i),U.get(i)/A.get(i)));
            order.sort(new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    if(o1.ORDER>=o2.ORDER)
                        return 1;
                    else
                        return -1;
                }
            });
            int count=0;
            while (demand>0){
                if(order.get(count).ar<=demand){
                    sol.route.get(order.get(count).r).point.add(order.get(count).ir+1,new Point(id,order.get(count).ar));
                    demand=demand-order.get(count).ar;
                    count++;
                }
                else if(order.get(count).ar>demand){
                    sol.route.get(order.get(count).r).point.add(order.get(count).ir+1,new Point(id,demand));
                    demand=0;
                }
            }
        }

    }



}
