//package Experiments;


import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.blockdude.BlockDude;
import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;
import burlap.domain.singleagent.blockdude.BlockDudeTF;
import burlap.domain.singleagent.blockdude.BlockDudeVisualizer;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sheldon on 11/14/2016.
 */

public class BlockDudeExp {

    BlockDude bd;
    OOSADomain domain;
    TerminalFunction tf;
    StateConditionTest goalCondition;
    State initialState;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;




    public BlockDudeExp(){
        bd = new BlockDude();
        tf = new BlockDudeTF();
        bd.setTf(tf);
        goalCondition = new TFGoalCondition(tf);
        domain = bd.generateDomain();

        initialState = BlockDudeLevelConstructor.getLevel1a(domain);
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);




        //VisualActionObserver observer = new VisualActionObserver(domain, BlockDudeVisualizer.getVisualizer(bd.getMaxx(), bd.getMaxy()));
        //observer.initGUI();
        //env.addObservers(observer);
    }


    public void visualize(String outputpath){
        Visualizer v = BlockDudeVisualizer.getVisualizer(bd.getMaxx(), bd.getMaxy());
        new EpisodeSequenceVisualizer(v, domain, outputpath);
    }

    public void valueIterationExample(String outputPath){
            Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
            Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "vi");
    }

    public void valueIterationExp(){
        List<Integer> runtime1 = new ArrayList<Integer>();
        List<Integer> runtime2 = new ArrayList<Integer>();
        List<Integer> runtime3 = new ArrayList<Integer>();
        List<Integer> runtime4 = new ArrayList<Integer>();
        List<Integer> iterations = new ArrayList<Integer>();
        for(int it=10; it<201; it+=10) {
            long startTime = System.nanoTime();
            Planner planner1 = new ValueIteration(domain, 0.99, hashingFactory, -1, it);
            Policy p1 = planner1.planFromState(initialState);
            runtime1.add((int) (System.nanoTime()-startTime)/1000000);
            iterations.add(it);
        }

        for(int it=10; it<201; it+=10) {
            long startTime = System.nanoTime();
            Planner planner2 = new ValueIteration(domain, 0.99, hashingFactory, 0.001, it);
            Policy p2 = planner2.planFromState(initialState);
            runtime2.add((int) (System.nanoTime()-startTime)/1000000);
        }

        for(int it=10; it<201; it+=10) {
            long startTime = System.nanoTime();
            Planner planner3 = new ValueIteration(domain, 0.99, hashingFactory, 0.01, it);
            Policy p3 = planner3.planFromState(initialState);
            runtime3.add((int) (System.nanoTime()-startTime)/1000000);
        }

        for(int it=10; it<201; it+=10) {
            long startTime = System.nanoTime();
            Planner planner4 = new ValueIteration(domain, 0.99, hashingFactory, 0.1, it);
            Policy p4 = planner4.planFromState(initialState);
            runtime4.add((int) (System.nanoTime()-startTime)/1000000);
        }
        System.out.println(iterations);
        System.out.println(runtime1);
        System.out.println(runtime2);
        System.out.println(runtime3);
        System.out.println(runtime4);


    }

    public void policyIterationExample(String outputPath){
        Planner planner = new PolicyIteration(domain, 0.99, hashingFactory, 0.001, -1, 1, 100);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "pi");
    }

    public void policyIterationExp(){
        int[] its = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        List<Integer> runtime1 = new ArrayList<Integer>();
        List<Integer> runtime2 = new ArrayList<Integer>();
        List<Integer> runtime3 = new ArrayList<Integer>();
        List<Integer> runtime4 = new ArrayList<Integer>();
        List<Integer> iterations = new ArrayList<Integer>();
        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner1 = new PolicyIteration(domain, 0.99, hashingFactory, -1, -1, 1, its[i]);
            Policy p1 = planner1.planFromState(initialState);
            runtime1.add((int) ((System.nanoTime()-startTime)/1000000));
            iterations.add(its[i]);
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner2 = new PolicyIteration(domain, 0.99, hashingFactory, 0.001, -1, 1, its[i]);
            Policy p2 = planner2.planFromState(initialState);
            runtime2.add((int) ((System.nanoTime()-startTime)/1000000));
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner3 = new PolicyIteration(domain, 0.99, hashingFactory, 0.01, -1, 1, its[i]);
            Policy p3 = planner3.planFromState(initialState);
            runtime3.add((int) ((System.nanoTime()-startTime)/1000000));
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner4 = new PolicyIteration(domain, 0.99, hashingFactory, 0.1, -1, 1, its[i]);
            Policy p4 = planner4.planFromState(initialState);
            runtime4.add((int) ((System.nanoTime()-startTime)/1000000));
        }

        System.out.println(iterations);
        System.out.println(runtime1);
        System.out.println(runtime2);
        System.out.println(runtime3);
        System.out.println(runtime4);


    }

    public void qLearningExample(String outputPath){

        LearningAgent agent = new QLearning(domain, 0.99, hashingFactory, 0.3, 0.6 );

        List<Integer> runtime = new ArrayList<Integer>();
        long startTime = System.nanoTime();
        //run learning for n episodes
        for(int i = 0; i < 100; i++){

            Episode e = agent.runLearningEpisode(env);
            runtime.add((int) ((System.nanoTime()-startTime)/1000000));

            //e.write(outputPath + "ql_" + i);
            //System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            env.resetEnvironment();
        }
        System.out.println(runtime);
    }



    public void QL_exp(){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        /**
         * Create factories for Q-learning agent
         */
        LearningAgentFactory qLearningFactory = new LearningAgentFactory() {

            public String getAgentName() {
                return "Q_Learning";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.99, 0.1);
            }
        };



        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 5, 200, qLearningFactory);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.STEPS_PER_EPISODE,
                //PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD);

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV("expData");

    }


    public void QL_gamma_exp(){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, 0.1));

        /**
         * Create factories for Q-learning agents with different gamma values
         */
        LearningAgentFactory qLearningFactory1 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.70";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.70, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory2 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.50";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.50, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory3 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.40";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.40, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory4 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.30";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.30, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory5 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.20";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.20, hashingFactory, 0.3, 0.6);
            }
        };


        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 5, 100, qLearningFactory1, qLearningFactory2, qLearningFactory3, qLearningFactory4, qLearningFactory5);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD);

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV("expData");

    }

    public void QL_LearningRate_exp(){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        /**
         * Create factories for Q-learning agent and SARSA agent to compare
         */
        LearningAgentFactory qLearningFactory1 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL LR:0.1";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.1);
            }
        };

        LearningAgentFactory qLearningFactory2 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL LR:0.2";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.2);
            }
        };

        LearningAgentFactory qLearningFactory3 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL LR:0.4";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.4);
            }
        };

        LearningAgentFactory qLearningFactory4 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL LR:0.6";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory5 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL LR:0.8";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.8);
            }
        };


        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 5, 100, qLearningFactory1, qLearningFactory2, qLearningFactory3, qLearningFactory4, qLearningFactory5);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD);

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV("expData");

    }

    public void QL_Qinit_exp(){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, 0.01));


         // Create factories for Q-learning agent and SARSA agent to compare

        LearningAgentFactory qLearningFactory1 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL init:0.1";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.1, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory2 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL init:0.3";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory3 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL init:0.5";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.5, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory4 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL init:0.7";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.7, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory5 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL init:0.9";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.99, hashingFactory, 0.9, 0.6);
            }
        };


        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 5, 100, qLearningFactory1, qLearningFactory2, qLearningFactory3, qLearningFactory4, qLearningFactory5);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD);

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV("expData");

    }



    public static void main(String[] args) {

        BlockDudeExp example = new BlockDudeExp();
        String outputPath = "BDoutput/";



        //example.valueIterationExample(outputPath);
        //example.policyIterationExample(outputPath);
        //example.valueIterationExp();
        //example.policyIterationExp();
        //example.qLearningExample(outputPath);


        //example.visualize(outputPath);

        //example.QL_exp();
        //example.QL_gamma_exp();
        //example.QL_LearningRate_exp();
        //example.QL_Qinit_exp();



    }

}