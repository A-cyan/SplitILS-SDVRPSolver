package nju.aor;

import nju.aor.common.*;
import nju.aor.algo.Algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        AlgoParam param = new AlgoParam(
                "vrp",       // problem_name
                "123456",     // id
                "Accyan",   // author_name
                "SplitILS",        // algo_name
                24000,            // time_limit = 24000 seconds
                "", ".vrp",  // teston_prefix, teston_extension
                "data/vrp"   // path_data
        );
        preparePaths(param);

        ArrayList<Instance> instances = readInstances(param);
        Collections.sort(instances, Comparator.comparing(inst -> inst.instname));
        System.out.println(instances.size() + " instances > ");

        instances.forEach(inst -> System.out.println("\t" + inst.instname));
        for(Instance inst: instances){
            inst.prepare();
            System.out.println("Solving " + inst.instname +" > ");
            Algorithm algo = new Algorithm(inst, param);
            algo.run();
        }
    }

    static void preparePaths(AlgoParam param) {
        try{
            File dir_result = new File("result");
            if(dir_result.exists() == false || dir_result.isDirectory() == false){
                dir_result.mkdir();
            }
            File dir_problem = new File(dir_result, param.problem_name);
            if(dir_problem.exists() == false || dir_problem.isDirectory() == false){
                dir_problem.mkdir();
            }
            File dir_algo = new File(dir_problem, param.algo_name);
            if(dir_algo.exists() == false || dir_algo.isDirectory() == false){
                dir_algo.mkdir();
            }

            param.path_result_sol = dir_algo.getAbsolutePath();
            param.path_result_csv = dir_problem.getAbsolutePath() + "/" + param.csv_name();
            param.initial_result_csv();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static ArrayList<Instance> readInstances(AlgoParam param) throws FileNotFoundException {
        File data = new File(param.path_data);

        Queue<File> que = new LinkedList<>();
        if(data.isDirectory()) {
            que.offer(data);
        }

        ArrayList<File> files = new ArrayList<>();
        while(que.isEmpty() == false){
            File folder = que.poll();
            File[] tmpFiles = folder.listFiles();
            for(File file: tmpFiles){
                if(file.isDirectory()){
                    que.offer(file);
                }
                else {
                    String fname = file.getName();
                    if(fname.startsWith(param.teston_prefix) && fname.endsWith(param.teston_extension)){
                        files.add(file);
                    }
                }
            }
        }
        ArrayList<Instance> instances = new ArrayList<>();
        for(File file: files){
            Instance inst = readInstance_vrp(file);
            instances.add(inst);
        }
        return instances;
    }

    public static Instance readInstance_vrp(File file) throws FileNotFoundException {
        Scanner cin = new Scanner(file);
        Instance inst = new Instance();
        inst.instname = file.getName();

        String line = cin.nextLine();
        while (line.startsWith("NODE_COORD_SECTION AND DEMAND_SECTION") == false) {
            if (line.startsWith("DIMENSION")) {
                inst.N = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
            }
            else if(line.startsWith("CAPACITY")){
                inst.capacity= Double.parseDouble(line.substring(line.indexOf(":")+1).trim());
            }
            line = cin.nextLine();
        }

        for(int i = 0; i < inst.N; ++i){
            int id = cin.nextInt();
            long x = cin.nextLong();
            long y = cin.nextLong();
            double demand=cin.nextDouble();
            Vertex vertex = new Vertex(id, x, y,demand);
            inst.vertices.add(vertex);
        }

        return inst;
    }
}
