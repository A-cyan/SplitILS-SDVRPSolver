import java.util.Scanner;

public class compute {
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        double []a=new double[16];
        for (int i = 0; i < 16; i++) {
            a[i]=scan.nextDouble();
        }
        double sum=0;
        for(int i=0;i<16;i++){
            sum=sum+a[i];
        }
        double mean=sum/16;
        double varsum=0;
        for(int i=0;i<16;i++){
            varsum=varsum+(a[i]-mean)*(a[i]-mean);
        }
        double var=varsum/15;
        System.out.println(mean);
        System.out.println(var);
    }
}
