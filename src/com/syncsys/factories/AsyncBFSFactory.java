package com.syncsys.factories;

import com.syncsys.Links.AsyncLink;
import com.syncsys.Links.Link;
import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.AsyncBFSStrategy;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by z on 3/26/17.
 */
public class AsyncBFSFactory implements Factory {
    @Override
    public RoundStrategy newRoundStrategy(ProcessNode node) {
        return new AsyncBFSStrategy(node);
    }

    @Override
    public Link newLink() {
        return new AsyncLink();
    }
}
