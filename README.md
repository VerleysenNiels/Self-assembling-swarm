# Self-assembling-swarm
This is the implementation of my research project for the Multi-Agent Systems course at KU Leuven. In this project I reproduced a self assembling robot swarm in the MASON simulation environment. The system gets a pattern as input and the agents move to reconstruct this pattern.

The original paper of the system architecture can be found here: https://science.sciencemag.org/content/345/6198/795
And an article can be found here: https://spectrum.ieee.org/automaton/robotics/robotics-hardware/a-thousand-kilobots-self-assemble

## Extensions
I added some extensions and made some modifications to the original algorithms to get this swarm system to work in this simulated environment. They are described more in depth in the paper that is also available in this repository, I will give a short description of them here.

For the gradient formation algorithm only neighbors are considered. These neighbors are found by a lookup within a very short radius around an agent. As a moving agent can sometimes move too far from visible neighbors I do not change the gradient to the maximum gradient if the gradient has already been set in one of the previous steps. This causes the gradients to become more stable.

The localization algorithm has been modified to use agents in a larger radius than the gradient formation algorithm in order to be able to find three noncollinear localized agents. To make the localization stable the agent waits until it has tried to pinpoint its location 60 times before saying that it has been localized. When this is not done the locations keep changing through the swarm and are very unstable.

## Using the software
The environment is defined in the Environment.java file. Here the simulated space and the agents are initialized. By changing the parameters in this file the size of the environment and agents, the amount of agents, the starting locations of the agents and the seed agents can be changed. The Bot.java file contains the agent behavior and some parameters can be changed in here to change this behavior, however this could cause the swarm to no longer function properly (the current parameters work fine). The representation of the agents in the GUI can be changed here as well.

To run the simulation please execute the ProjectWithGui.java file and press play in the GUI. The target shape is determined by the file in Shapes/Using. Some example files are given in the Shapes folder, simply replace the current file in Shapes/Using with one of these or one of your own files to set a new target shape. This should be done before executing the ProjectWithGui file as this shape is immediately processed at startup.

## Defining your own shape
New shapes can easily be defined by creating a text file and filling it in according to the following rules.
* The first line should contain the bottom left corner of the shape. I set this to "1,1" as some of the agents already start within the shape if "0,0" is used, causing them to join the shape and block other agents.
* The following lines define the shape from the bottom to the top. The shape is flipped over when read. The first character in a line is in the same collumn as the bottom left corner given on the first line. Each additional character moves one coordinate to the right.
* An X marks that the coordinate is inside the shape, an O that the coordinate is not inside the shape.
* Each line should be ended with an O.
* The final line should only contain O characters.

## Examples
In the first tests I only tried to make the swarm form rectangles. The correct shapes are formed, although they seem to be rotated a bit.

![Form a rectangle](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/example_rectangle_cropped.gif)

In this example the swarm needs to form a 5 by 20 rectangle. As there are 100 bots, all bots should be used.

![Form a small square](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/example_small_square_cropped.gif)

In this example the swarm needs to form a 5 by 5 square. Not all bots should be used.

![Form a triangle](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/example_triangle.gif)

In this example the swarm needs to form a triangle.

![Form a rectangle with two holes](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/two_holes.gif)

In this example the swarm needs to form a rectangle with two holes, the swarm forms the letter B.

![Form the letter R](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/R.gif)

In this example the swarm needs to form the letter R.

![Form two rectangles](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/two_rectangles.gif)

In this example we can see where the swarm fails. The target shape exists of two rectangles that are separated by two rows. The swarm only constructs the bottom one.

![Form two rectangles close](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/two_rectangles_close.gif)

The target shape exists again of two rectangles, but this time they are separated by just a single row. The swarm builds a bridge on both sides and constructs both rectangles.
