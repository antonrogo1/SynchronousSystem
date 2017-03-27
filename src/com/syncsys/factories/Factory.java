package com.syncsys.factories;

import com.syncsys.Links.Link;
import com.syncsys.ProcessNode;
import com.syncsys.roundStrategies.RoundStrategy;

/**
 * Created by z on 3/26/17.
 */
public interface Factory {
    public RoundStrategy newRoundStrategy(ProcessNode node);
    public Link newLink();
}
