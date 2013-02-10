package GA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @param <E>
 * E is the class that represents an individual.
 * @author Drew
 */
public class GA<E> {

    /*
     * Fields
     */
    // currGen stores the individuals of the current generation
    private ArrayList<ArrayList<Integer>> currGen;
    // A field to store fitness values of the currGen individuals
    private ArrayList<Double> fitScores;
    // The size of the population ie number of individuals in currGen
    private int popSize;
    //  The current generation GA is on.
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

    /**
     *
     * @param individual
     * @param fitness
     */
    public GA(GAInterface<E> individual, GAFitness<E> fitness) {
        this.individual = individual;
        this.fitness = fitness;


        fitScores = new ArrayList<Double>();
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
    public GA(GAInterface<E> individual, GAFitness<E> fitness, double mutRate, double crossRate) {
        this.individual = individual;
        this.fitness = fitness;


        fitScores = new ArrayList<Double>();
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
     * This method will run the GA and return E the individual that is the best
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

    /**
     *
     * This method will run the GA and return E the individual that is the best
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
    public E runGAByTime(long maxSeconds, int popSize, boolean findMax) {
        this.popSize = popSize;

        createInitialPop();
        System.gc();

        long start = System.currentTimeMillis();

        System.gc();
        long stop = System.currentTimeMillis();


        while ((stop - start) / 1000 <= maxSeconds) {
            evalPop();
            currGen = createGen();
            whichGen++;
            System.gc();
            stop = System.currentTimeMillis();
        }

        return getResult(findMax);

    }

    /**
     *
     * This method will run the GA and return E the individual that is the best
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
        if (findMax == true) {
            Double max = Collections.max(fitScores);
            index = fitScores.indexOf(max);
        } else {
            Double min = Collections.min(fitScores);
            index = fitScores.indexOf(min);
        }

        bestFit = currGen.get(index);
        return individual.decode(currGen.get(index));

    }

    private void evalPop() {
        fitScores = new ArrayList<Double>();
        for (int i = 0; i < popSize; i++) {
            fitScores.add(fitness.getFitness(individual.decode(currGen.get(i))));
            if (goalFit != null && fitScores.get(i) == goalFit.doubleValue()) {
                goalFound = true;
                break;
            }
        }
    }
    
  

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

    public ArrayList<Double> getFitScores() {
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
