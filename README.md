# Self-assembling-swarm
This is the implementation of my research project for the Multi-Agent Systems course at KU Leuven. In this project I reproduced a self assembling robot swarm in the MASON simulation environment. The system gets a pattern as input and the agents move to reconstruct this pattern.

The original paper of the system architecture can be found here: https://science.sciencemag.org/content/345/6198/795
And an article can be found here: https://spectrum.ieee.org/automaton/robotics/robotics-hardware/a-thousand-kilobots-self-assemble

## Extensions


## Examples
In the first tests I only tried to make the swarm form rectangles. The correct shapes are formed, although they seem to be rotated a bit.

![Form a rectangle](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/example_rectangle.gif)

In this example the swarm needs to form a 5 by 20 rectangle. As there are 100 bots, all bots should be used.

![Form a small square](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/example_small_square.gif)

In this example the swarm needs to form a 5 by 5 square. Not all bots should be used.
