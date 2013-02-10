/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GA;

import java.util.ArrayList;

/**
 *
 * @author Drew
 */
public interface GAInterface <E> {

    /**
     * This function will create a random individual represented by an
     * array of integers.
     * @return
     */
    public ArrayList<Integer> makeIndividual();

    /**
     * This method will return the individual
     * @param individual 
     * @return
     */
    public E decode(ArrayList<Integer> individual);

    /**
     * This method takes a index representing the index of the gene to mutate
     * and returns a new value for this gene
     * @param index
     * @return
     */
    public Integer mutate(int index);


}
