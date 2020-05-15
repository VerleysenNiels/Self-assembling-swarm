# Self-assembling-swarm
This is the implementation of my research project for the Multi-Agent Systems course at KU Leuven. In this project I reproduced a self assembling robot swarm in the MASON simulation environment. The system gets a pattern as input and the agents move to reconstruct this pattern.

The original paper of the system architecture can be found here: https://science.sciencemag.org/content/345/6198/795
And an article can be found here: https://spectrum.ieee.org/automaton/robotics/robotics-hardware/a-thousand-kilobots-self-assemble

## Extensions to the original algorithm
I added some extensions and made some modifications to the original algorithms to get this swarm system to work in this simulated environment. They are described more in depth in the paper that is also available in this repository, I will give a short description of them here.

For the gradient formation algorithm only neighbors are considered. These neighbors are found by a lookup within a very short radius around an agent. As a moving agent can sometimes move too far from visible neighbors I do not change the gradient to the maximum gradient if the gradient has already been set in one of the previous steps. This causes the gradients to become more stable.

The localization algorithm has been modified to use agents in a larger radius than the gradient formation algorithm in order to be able to find three noncollinear localized agents. To make the localization stable the agent waits until it has tried to pinpoint its location 60 times before saying that it has been localized. When this is not done the locations keep changing through the swarm and are very unstable.

## Adding the ability to construct and use bridges
I extended the original agent behavior (as can be found in Bot.java) to make use of the idea of bridge formation to be able to form multiple target shapes that are not connected. The extended behavior can be found in BridgeBot.java. I did not use inheritance to extend the BridgeBot as I basically replaced the original Bot class with the BridgeBot class. The Bot class is still here to show my work to reproduce the original system and to show what was changed to add the ability to form bridges. 

The idea of bridge construction also comes from nature, ants can cross difficult terrain by forming a bridge consisting of ants to allow other ants to move accross. When a shape exists of multiple separate shapes the space between these shapes can be seen as such difficult terrain, as the agents can only move along the edges of the swarm. I therefore added a new state to the finite-state automaton that models the agent behavior. This new state is JOINED_BRIDGE. To know when to join a bridge the agents know which coordinates are part of a bridge, like they know which coordinates are part of the target shape. When an agent is moving and enters this area it joins the bridge. While inside a bridge the agent waits until it has seen an agent move the other way, signifying that the other shapes have been formed and the bridge is no longer necessary. The agents of the bridge then wait until they can no longer see an agent moving and start deconstructing the bridge from lowest to highest gradient. To prevent the agents from continuously joining and leaving a bridge, they determine the amount of time inside and outside the bridge and only consider to change from one to the other when this value is high enough.

The part that I did not implement in this project due to time restrictions is the determination of where bridges should be build. This would involve the use of some kind of shortest path algorithm, combined with a new algorithm determining where and how the connection should be formed. Instead of this I extended the files that define the shape to also mark where the bridges should be constructed (see the rule about this in the section about defining your own shape).

In the software the agents that are part of a bridge are colored cyan. These examples show that the bridges are constructed and used to construct the other shapes. When they are no longer necessary they are removed. When a bridge is again necessary it is reconstructed and used and afterwards broken down. These bridges can however make localization very unstable as they are singly connected and localization needs three noncollinear localized agents to determine its location. It might be possible that simply increasing the amount of agents in the swarm and increasing the width of a bridge might solve this problem. The formation of a bridge would also need to be changed to be more like the algorithm for joining a target shape. The current swarm system in this project serves as a proof of concept that bridge formation can be used to form multiple target shapes.

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
* Bridges should go upwards or to the right and are marked with U and R respectively.

## Examples
In the first tests I only tried to make the swarm form rectangles. The correct shapes are formed, although they seem to be rotated a bit. I then gradually increased the complexity of the shape. The swarm was always able to form the target shape. I then moved over to multiple target shapes that are not connected. The animated results can be found below and a more in depth description is given in the paper.

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


## Examples using bridges
Below are some examples showing the use of bridge formation to form multiple rectangles that are not connected.

![Form two rectangles with a bridge](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/two_rectangles_bridge2.gif)

The target shape exists of two rectangles separated by multiple rows. The swarm builds a bridge and is this way able to form both target shapes. As there are still some agents left that keep moving around the shape, the bridge is not completely broken down.

![Form two rectangles with a bridge](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/four_rectangles_bridge.gif)

The target shape exists of four rectangles above eachother separated by multiple rows. The swarm builds bridges and is this way able to form the target shapes. As there are still some agents left that keep moving around the shape, the bridges are not completely broken down.

![Form five rectangles with bridges](https://github.com/VerleysenNiels/Self-assembling-swarm/blob/master/Examples/five_rectangles_bridge.gif)

The target shape exists of five rectangles separated by multiple rows, four of them are situated above eachother like in the previous example and the fifth is on the right of the third shape. The swarm builds bridges and is this way able to form both target shapes. These bridges are then broken down.
