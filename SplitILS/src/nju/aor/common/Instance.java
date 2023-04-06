package nju.aor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Instance {
    public String instname;
    public int N;
    public ArrayList<Vertex> vertices;
    public double[][] dist;
    public double capacity;

    public Instance() {
        vertices = new ArrayList<>();
    }

    public void prepare() {
        compute_distance();
    }

    public double dist(int i, int j){
        return dist[i-1][j-1];
    }

    public void compute_distance() {
        dist = new double[N][N];
        for (int i = 0; i < N; ++i) {
            for (int j = i + 1; j < N; ++j) {
                Vertex a = vertices.get(i);
                Vertex b = vertices.get(j);
                dist[i][j] = dist[j][i] = (double) ( Math.sqrt((a.coord_x - b.coord_x) * (a.coord_x - b.coord_x)
                        + (a.coord_y - b.coord_y) * (a.coord_y - b.coord_y))); // do not modify
            }
        }
    }
    public double get_demand(int id){
        double demand=0;
        for(int i=0;i<vertices.size();i++)
        {
            if(vertices.get(i).id==id)
            {
                demand=vertices.get(i).demand;
                break;
            }
        }
        return demand;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(N + " vertices in Graph > \r\n" );
        for(Vertex a : vertices){
            sb.append(a.toString() + "\r\n");
        }
        sb.append("Distance matrix > \r\n");
        for(int i = 0; i < N; ++i){
            sb.append(Arrays.toString(dist[i]) + "\r\n");
        }
        return sb.toString();
    }
}


