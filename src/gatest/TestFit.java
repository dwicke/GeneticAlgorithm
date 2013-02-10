/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gatest;

import GA.GAFitness;

/**
 *
 * @author Drew
 */
public class TestFit implements GAFitness<Integer>{

    int target = 15;
    public double getFitness(Integer individual)
    {

        
        if ((target - individual) != 0)
        {
           //return 1 / (target - individual);
            return target - individual;
           
        }
        return 100000;
        
    }
}
