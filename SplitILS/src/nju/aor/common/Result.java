package nju.aor.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Result {
    String instname;
    int N;
    AlgoParam param;

    public double time;
    public Solution sol;

    // your performance metrics
    public double time_two_opt;
    public double time_or_opt;
    public double time_one_insert;
    public double time_two_exchange;
    public double time_rand_swap;
    public double time_cross;
    public double time_k_split;
    public double time_routeaddition;
    public double time_shift_one_zero;
    public double time_shift_two_zero;
    public double time_swap_one_one;
    public double time_swap_one_one_new;
    public double time_swap_two_one;
    public double time_swap_two_one_new;
    public double time_swap_two_two;


    public Result(Instance inst, AlgoParam param) {
        this.instname = inst.instname;
        this.N = inst.N;
        this.param = param;
    }

    public void setBestSol(Solution sol){
        this.sol = new Solution(sol);
    }

    public double cost(){
        return sol.cost;
    }

    public Solution sol(){
        return sol;
    }

    public String toCsvString() {
        return instname + "," + N + "," + param.author_id + "," + param.author_name + "," + param.algo_name + "," + sol.cost + "," + time;
    }

    public String toJSonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public void output(Instance inst){
        Log.start(new File(param.path_result_csv), true);
        Log.writeln(toCsvString());
        Log.end();

        Log.start(new File(param.path_result_sol+"/result_"+instname+".json"), false);
        Log.writeln(toJSonString());
        Log.end();

    }
}
