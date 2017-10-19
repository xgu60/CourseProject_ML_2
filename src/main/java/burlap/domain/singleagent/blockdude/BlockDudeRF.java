package burlap.domain.singleagent.blockdude;

import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

/**
 * Created by Sheldon on 11/19/2016.
 */
public class BlockDudeRF implements TerminalFunction {

    @Override
    public boolean isTerminal(State s) {

        BlockDudeState bs = (BlockDudeState)s;

        int ax = bs.agent.x;
        int ay = bs.agent.y;

        int ex = bs.exit.x;
        int ey = bs.exit.y;

        return ex == ax && ey == ay;

    }
}
