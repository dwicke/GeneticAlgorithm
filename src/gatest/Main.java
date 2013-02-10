/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gatest;

import GA.MGA;
import java.util.ArrayList;

/**
 *
 * @author Drew
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
System.out.println("HI");
        TestGAInterface inte = new TestGAInterface();
        MGA<Integer> myGA = new MGA<Integer>(inte, new TestFit(), .2, .3);
        System.out.println("HI");
        System.out.println(myGA.runGA(100, 1000, true, 1));// 30s with 12 threads 2s with one thread
        //System.out.println(myGA.runGAByGoal((double)100000, 1000000, 100,true) + "  number of generations " + myGA.getWhichGen());
        //System.out.println(myGA.runGAByTime(5, 100,true) + "  number of generations " + myGA.getWhichGen());

        ArrayList<ArrayList<Integer>> allBest = myGA.getCurrGen();


        for (int i = 0; i < allBest.size(); i++)
        {

            inte.getNumOps(allBest.get(i));
            System.out.println("Found with " + inte.whole + " = " +  inte.decode(allBest.get(i)));

        }

       inte.getNumOps(myGA.getBestFit());
       System.out.println("Found with " + inte.whole);

    }

}
