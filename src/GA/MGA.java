package GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

/**
 * Multi-threaded version of GA 
 * @param <E>
 * E is the class that represents an individual.
 * @author Drew
 */
public class MGA<E> {

    /*
     * Fields
     */
    // currGen stores the indivuals of the current generation
    private ArrayList<ArrayList<Integer>> currGen;
    // A field to store fitness values of the currGen individuals
    private Map<Integer, Double> fitScores;
    // The size of the population ie number of individuals in currGen
    private int popSize;
    //  The current generation MGA is on.
    private int whichGen;
    // Crossover rate is the chance that genes will perform crossover opperaton
    private double crossRate;
    //  Mutation rate is the chance that a gene will be mutated
    private double mutRate;
    // This is a class that implements the GAInterface for use of the
    // makeIndivual, fitness and mutate methods
    private GAInterface<E> individual;
    //  This class will be able to evaluate the fitness of the individual
    private GAFitness<E> fitness;
    //  Set to true if Goal is found in the runGAByGoal method
    private boolean goalFound;
    // Goal Value
    private Double goalFit;
    // Index of best fit
    private ArrayList<Integer> bestFit;
    private Random rand = new Random();

    private ExecutorService pool;

    /**
     *
     * @param individual
     * @param fitness
     */
    public MGA(GAInterface<E> individual, GAFitness<E> fitness) {
        this.individual = individual;
        this.fitness = fitness;


        fitScores = new TreeMap<Integer,Double>();
        currGen = new ArrayList<ArrayList<Integer>>();
        bestFit = new ArrayList<Integer>();

        crossRate = .7;
        mutRate = .001;
        popSize = 0;
        whichGen = 0;

        goalFound = false;
        goalFit = null;
    }

     /**
     *
     * @param individual
      * @param fitness
      * @param mutRate
      * @param crossRate
     */
    public MGA(GAInterface<E> individual, GAFitness<E> fitness, double mutRate, double crossRate) {
        this.individual = individual;
        this.fitness = fitness;


        fitScores = new TreeMap<Integer, Double>();
        currGen = new ArrayList<ArrayList<Integer>>();
        bestFit = new ArrayList<Integer>();

        this.crossRate = crossRate;
        this.mutRate = mutRate;
        popSize = 0;
        whichGen = 0;

        goalFound = false;
        goalFit = null;
    }

    /*
     * Methods
     */


    /**
     *
     * This method will run the MGA and return E the individual that is the best
     * fit out of the populaton.
     *
     * @param maxGens
     * The maximum number of generations the algorithm will create
     * @param popSize
     * The number of individuals to create in each generation.
     * @param findMax
     * When true runGA will return the individual with max fitness
     * If false runGA will return the individual with minimum fitness
     * @return
     * The individual based on findMax parameter.
     */
    public E runGA(int maxGens, int popSize, boolean findMax) {
        this.popSize = popSize;

        createInitialPop();

        while (whichGen <= maxGens) {
            evalPop();
            currGen = createGen();
            whichGen++;
        }

        return getResult(findMax);
    }

     public E runGA(int maxGens, int popSize, boolean findMax, int numProc) {
        this.popSize = popSize;

        createInitialPop();

        pool = java.util.concurrent.Executors.newFixedThreadPool(numProc);

        while (whichGen <= maxGens) {
            evalPopThreaded();
            currGen = createGen();
            whichGen++;
        }
        pool.shutdown();
        return getResult(findMax);
    }

    /**
     *
     * This method will run the MGA and return E the individual that is the best
     * fit out of the populaton.
     *
     * @param maxSeconds
     * The maximum number of seconds the algorithm will run.
     * @param popSize
     * The number of individuals to create in each generation.
     * @param findMax
     * When true runGA will return the individual with max fitness
     * If false runGA will return the individual with minimum fitness
     * @return
     * The individual based on findMax parameter.
     */
    public E runGAByTime(long maxSeconds, int popSize, boolean findMax, int numProc) {
        this.popSize = popSize;
        pool = java.util.concurrent.Executors.newFixedThreadPool(numProc);
        createInitialPop();
        System.gc();

        long start = System.currentTimeMillis();

        System.gc();
        long stop = System.currentTimeMillis();


        while ((stop - start) / 1000 <= maxSeconds) {
            evalPopThreaded();
            currGen = createGen();
            whichGen++;
            System.gc();
            stop = System.currentTimeMillis();
        }

        return getResult(findMax);

    }

    /**
     *
     * This method will run the MGA and return E the individual that is the best
     * fit out of the populaton.
     *
     * @param goalFitness
     * The fitness goal number.
     * @param maxGen
     * Maximum number of generations the algorithm will complete
     * @param popSize
     * The number of individuals to create in each generation.
     * @param findMax
     * When true runGA will return the individual with max fitness
     * If false runGA will return the individual with minimum fitness
     * @return
     * The individual based on findMax parameter.
     */
    public E runGAByGoal(double goalFitness, int maxGen, int popSize, boolean findMax) {
        this.popSize = popSize;

        createInitialPop();

        goalFit = goalFitness;


        while (goalFound == false && whichGen <= maxGen) {
            evalPop();
            if (goalFound == true) {
                break;
            }
            currGen = createGen();
            whichGen++;

        }
        return getResult(findMax);
    }

    private void createInitialPop() {
        for (int i = 0; i < popSize; i++) {
            currGen.add(individual.makeIndividual());
        }
    }

    private E getResult(boolean findMax) {
        int index = 0;

         Entry<Integer, Double> result = null;

        if (findMax == true) {
           
            Iterator<Entry<Integer, Double>> s = fitScores.entrySet().iterator();

            
           for (; s.hasNext(); )
           {
               Entry<Integer, Double> curr = s.next();
               if (result == null)
               {
                   result = curr;
               }
               else if (curr.getValue() > result.getValue())
               {
                   result = curr;
               }

           }

        } else {
            Iterator<Entry<Integer, Double>> s = fitScores.entrySet().iterator();


           for (; s.hasNext(); )
           {
               Entry<Integer, Double> curr = s.next();
               if (result == null)
               {
                   result = curr;
               }
               else if (curr.getValue() < result.getValue())
               {
                   result = curr;
               }

           }
        }

        bestFit = currGen.get(result.getKey());
        return individual.decode(currGen.get(result.getKey()));

    }

    private void evalPop() {
        fitScores = new TreeMap<Integer, Double>();


        // Maybe here create the new threads


        for (int i = 0; i < popSize; i++) {
            fitScores.put(i,fitness.getFitness(individual.decode(currGen.get(i))));
            
            if (goalFit != null && fitScores.get(i) == goalFit.doubleValue()) {
                goalFound = true;
                break;
            }
        }
    }

    private void evalPopThreaded() {
        //fitScores = new TreeMap<Integer, Double>();
        fitScores = Collections.synchronizedMap(new TreeMap<Integer, Double>());

        
        // Maybe here create the new threads
        for (int i = 0; i < popSize; i++) {
            pool.execute(new thread(i));
        }
        while(fitScores.size() < this.popSize)
        {
        }

    }

    private class thread implements Runnable{

        private int id;
        public thread (int id)
        {
            this.id = id;
        }
        public void run() {

            fitScores.put(id, fitness.getFitness(individual.decode(currGen.get(id))));

            if (goalFit != null && fitScores.get(id) == goalFit.doubleValue()) {
                goalFound = true;

            }
        }

    };

    private ArrayList<ArrayList<Integer>> createGen() {

        ArrayList<ArrayList<Integer>> nextGen = new ArrayList<ArrayList<Integer>>();
        double fitSum = getFitSum();
        for (int i = 0; i < currGen.size(); i++) {
            nextGen.add(new ArrayList<Integer>());
            int parentOne = getIndividual(fitSum);
            int parentTwo = getIndividual(fitSum);
            int crossOverPoint = rand.nextInt() % currGen.get(i).size();

            double shouldCross = rand.nextDouble();
            if (shouldCross <= crossRate) {
                for (int j = 0; j < currGen.get(i).size(); j++) {
                    
                    double shouldMutate = rand.nextDouble();
                    if (shouldMutate <= mutRate) {
                        nextGen.get(i).add(individual.mutate(j));
                    } else {
                        if (j < crossOverPoint) {
                            nextGen.get(i).add(currGen.get(parentOne).get(j));
                        } else {
                            nextGen.get(i).add(currGen.get(parentTwo).get(j));
                        }
                    }
                }
            } else {
                for (int j = 0; j < currGen.get(i).size(); j++) {
                    
                    double shouldMutate = rand.nextDouble();
                    if (shouldMutate <= mutRate) {
                        nextGen.get(i).add(individual.mutate(j));
                    } else {
                        nextGen.get(i).add(currGen.get(parentOne).get(j));

                    }
                }
            }

        }

        return nextGen;
    }

    /*
     * Roulete wheel selection I do not need to sort the individuals based on
     * fitness.
     */
    private int getIndividual(double fitSum) {
        double sum = 0;

        // I need a double number between 0 and fitSum
        double goal = rand.nextDouble() * Math.abs(fitSum);

        


        for (int i = 0; i < fitScores.size(); i++) {
            sum += Math.abs(fitScores.get(i));

            if (sum >= goal) {
                return i;
            }
        }
        System.out.println("The goal fit " + goal + " The sum of fit " + sum);

        // not working
        return -1;

    }

    private double getFitSum() {
        double sum = 0;
        for (int i = 0; i < fitScores.size(); i++) {
            sum += fitScores.get(i);
        }
        return sum;
    }


    /*
     * Field Getters
     */
    public int getWhichGen() {
        return whichGen;
    }

    public double getCrossRate() {
        return crossRate;
    }

    public ArrayList<ArrayList<Integer>> getCurrGen() {
        return currGen;
    }

    public Map<Integer, Double> getFitScores() {
        return fitScores;
    }

    public GAFitness<E> getFitness() {
        return fitness;
    }

    public Double getGoalFit() {
        return goalFit;
    }

    

    public GAInterface<E> getIndividual() {
        return individual;
    }

    public double getMutRate() {
        return mutRate;
    }

    public int getPopSize() {
        return popSize;
    }

    public ArrayList<Integer> getBestFit() {
        return bestFit;
    }

    public ArrayList<ArrayList<Integer>> getAllBestFit()
    {
        ArrayList<ArrayList<Integer>> best = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < fitScores.size(); i++)
        {
            if (fitScores.get(i) == goalFit)
            {
                best.add(currGen.get(i));
            }
        }
        return best;
    }

    /*
     * Field Setters
     */
    public void setCrossRate(double crossRate) {
        this.crossRate = crossRate;
    }

    public void setMutRate(double mutRate) {
        this.mutRate = mutRate;
    }
}
