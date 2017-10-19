package Experiemnts;


import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GridWorldExp {

    GridWorldDomain gwdg;
    OOSADomain domain;
    TerminalFunction tf;
    StateConditionTest goalCondition;
    State initialState;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;


    public GridWorldExp(){
        gwdg = new GridWorldDomain(11, 11);
        gwdg.setMapToFourRooms();
        tf = new GridWorldTerminalFunction(6, 5);
        gwdg.setTf(tf);
        goalCondition = new TFGoalCondition(tf);
        domain = gwdg.generateDomain();

        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(6, 5, "loc0"));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);


//		VisualActionObserver observer = new VisualActionObserver(domain, GridWorldVisualizer.getVisualizer(gwdg.getMap()));
//		observer.initGUI();
//		env.addObservers(observer);
    }


    public void visualize(String outputpath){
        Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
        new EpisodeSequenceVisualizer(v, domain, outputpath);
    }


    public void valueIterationExample(String outputPath){
        Planner planner = new ValueIteration(domain, 0.99, hashingFactory, 0.001, 100);
        Policy p = planner.planFromState(initialState);
        PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "vi");
        simpleValueFunctionVis((ValueFunction)planner, p);
    }

    public void valueIterationExp(){
        List<Integer> runtime1 = new ArrayList<Integer>();
        List<Integer> runtime2 = new ArrayList<Integer>();
        List<Integer> runtime3 = new ArrayList<Integer>();
        List<Integer> runtime4 = new ArrayList<Integer>();
        List<Integer> iterations = new ArrayList<Integer>();
        int[] its = {1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner1 = new ValueIteration(domain, 0.99, hashingFactory, -1, its[i]);
            Policy p1 = planner1.planFromState(initialState);
            runtime1.add((int) (System.nanoTime()-startTime)/1000000);
            iterations.add(its[i]);
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner2 = new ValueIteration(domain, 0.99, hashingFactory, 0.001, its[i]);
            Policy p2 = planner2.planFromState(initialState);
            runtime2.add((int) (System.nanoTime()-startTime)/1000000);
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner3 = new ValueIteration(domain, 0.99, hashingFactory, 0.01, its[i]);
            Policy p3 = planner3.planFromState(initialState);
            runtime3.add((int) (System.nanoTime()-startTime)/1000000);
        }

        for(int i=0; i<its.length; i++) {
            long startTime = System.nanoTime();
            Planner planner4 = new ValueIteration(domain, 0.99, hashingFactory, 0.1, its[i]);
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
        simpleValueFunctionVis((ValueFunction)planner, p);
    }

    public void policyIterationExp(){
        int[] its = {1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
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


    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(allStates, 11, 11, valueFunction, p);
        gui.initGUI();

    }

    public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashingFactory);

        //define color function
        LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
        rb.addNextLandMark(0., Color.RED);
        rb.addNextLandMark(1., Color.BLUE);

        //define a 2D painter of state values, specifying which attributes correspond to the x and y coordinates of the canvas
        StateValuePainter2D svp = new StateValuePainter2D(rb);
        svp.setXYKeys("agent:x", "agent:y", new VariableDomain(0, 11), new VariableDomain(0, 11), 1, 1);

        //create our ValueFunctionVisualizer that paints for all states
        //using the ValueFunction source and the state value painter we defined
        ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp, valueFunction);

        //define a policy painter that uses arrow glyphs for each of the grid world actions
        PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
        spp.setXYKeys("agent:x", "agent:y", new VariableDomain(0, 11), new VariableDomain(0, 11), 1, 1);

        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
        spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);


        //add our policy renderer to it
        gui.setSpp(spp);
        gui.setPolicy(p);

        //set the background color for places where states are not rendered to grey
        gui.setBgColor(Color.GRAY);

        //start it
        gui.initGUI();



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
                return new QLearning(domain, 0.99, hashingFactory, 0.3, 0.1);
            }
        };



        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, 5, 500, qLearningFactory);
        exp.setUpPlottingConfiguration(500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
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
                return "QL gamma:0.90";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.90, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory2 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.80";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.80, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory3 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.70";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.70, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory4 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.60";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.60, hashingFactory, 0.3, 0.6);
            }
        };

        LearningAgentFactory qLearningFactory5 = new LearningAgentFactory() {

            public String getAgentName() {
                return "QL gamma:0.50";
            }


            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.50, hashingFactory, 0.3, 0.6);
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
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));


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

        GridWorldExp example = new GridWorldExp();
        String outputPath = "output/";

        //example.valueIterationExample(outputPath);
        //example.policyIterationExample(outputPath);
        //example.valueIterationExp();
        //example.policyIterationExp();
        //example.qLearningExample(outputPath);


        //example.QL_exp();
        //example.QL_gamma_exp();
        //example.QL_LearningRate_exp();
        //example.QL_Qinit_exp();


        //example.visualize(outputPath);

    }

}
