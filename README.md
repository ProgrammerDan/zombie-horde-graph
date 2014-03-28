Zombie Horde - The Final Stand
======

My submission for the code-golf problem [The Final Stand - Defeat the Zombie Horde](http://codegolf.stackexchange.com/q/23775/17546).

Basic approach (check problem statement for challenge details):

Assign routes to a 3-d array. Row indicates source output. Column, destination. If there is more than one route between two outputs, extend into third dimension. Value of 0th entry in 3rd dimension indicates # of routes. Value of nth entry in 3rd dimension indicates number of zombies on the route. Duplicate all routes in reverse direction as they are always bidirectional.

Build another array for outputs to store ammunition.

Algorithm?

1. Optimize ammunition. Always follow branch that has highest zombie/ammunition ratio.
2. Optimize ammunication one step ahead. Follow branch that when combined with a second step has highest zombie/ammunition ratio.
3. Optimize ammunition two steps ahead. Follow branch that when combined with next two steps has highest zombie/ammunition ratio.

4. Optimize zombies. Always follow branch that has highest ammunition/zombie ratio.

