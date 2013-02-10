/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gatest;

import GA.GAInterface;
import java.util.ArrayList;
import java.util.Random;
/**
 *
 * @author Drew
 */
public class TestGAInterface implements GAInterface<Integer>{



    private String characters[] = new String[] {"1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "0",
            "+",
            "-",
            "*",
            "/"};
    Random rand = new Random();
    public String whole = "";
     /*
     * This function will create a random individual represented by an
     * array of integers.
     */
    public ArrayList<Integer> makeIndividual()
    {
       // System.out.println("Making Individual");
        ArrayList<Integer> individual = new ArrayList<Integer>();

        for (int i = 0; i < 6; i++)
        {
            individual.add(rand.nextInt(14));
        }
        return individual;
    }

    /*
     * This method will return the indivdual
     */
    public Integer decode(ArrayList<Integer> individual)
    {

        int sum = 0;
        ArrayList<String> numOps =  getNumOps(individual);
        
      

       // System.out.println(Integer.parseInt(numOps.get(0).substring(0, 1)));
        if (numOps.get(0).length() >= 1)
        {
         sum = Integer.parseInt(numOps.get(0).substring(0, 1));
         numOps.set(0, numOps.get(0).substring(1));
        }
         if (numOps.get(1).length() == 0)
         {
             sum = 0;
         }

        while(numOps.get(0).length() >= 1 && numOps.get(1).length() >= 1)
        {
            int first = Integer.parseInt(numOps.get(0).substring(0, 1));
           // System.out.println(Integer.parseInt(numOps.get(0).substring(0, 1)));
            numOps.set(0, numOps.get(0).substring(1));


            String op =  numOps.get(1).substring(0, 1);
           numOps.set(1, numOps.get(1).substring(1));

            if (op.equals("+"))
            {
                sum += first;
            }
            else if (op.equals("-"))
            {
                sum -= first;
            }
            else if (op.equals("*"))
            {
                sum *= first;
            }
            else if (op.equals("/"))
            {
                if (first == 0)
                {
                    sum = 0;
                }
                else
                {
                sum /= first;
                }
            }
        }
        // System.out.println("the sum is " + sum);
         return sum;
    }

    public ArrayList<String> getNumOps(ArrayList<Integer> individual)
    {
        String numbers = "";
        String ops = "";
        whole = "";
      //  System.out.println("decoding individual");
        boolean onNumber = true;
        for (int i = 0; i < individual.size(); i++)
        {
            if (individual.get(i) < 10 && onNumber == true)
            {
                numbers += characters[individual.get(i)];
                whole += characters[individual.get(i)];
               // System.out.println(i + " -  " + characters[individual.get(i)]);
                onNumber = false;
            }
            else if (individual.get(i) > 9 && onNumber == false)
            {
                ops += characters[individual.get(i)];
                whole += characters[individual.get(i)];
               // System.out.println(i + " -  " + characters[individual.get(i)]);
                onNumber = true;

            }
            else
            {
               // System.out.println(i + " -  " + characters[individual.get(i)] +  " bad");
            }
        }

        

        ArrayList<String> numOps = new ArrayList<String>();
        numOps.add(numbers);
        numOps.add(ops);
        return numOps;
    }
    /*
     * This method takes a index representing the index of the gene to mutate
     * and returns a new value for this gene
     */
    public Integer mutate(int index)
    {
        return rand.nextInt(14);
    }
}
