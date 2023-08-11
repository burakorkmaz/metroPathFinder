# metroPathFinder
First of all you have to use StdDraw library in Java to run it appropriate. 

My main aim in this assignment is to find the path between two given stations in Istanbul and draw its route. We have a given file which contains some data about stations, lines, breakPoints (breakPoints are the stations which connect metro lines each other. In the other words, a station where passengers can change metro lines.), and its coordinates. First of all, we want to read the contents of the input file and then store them in separate arrays to understand and interpret easily.
After reading and storing the data, we should handle the algorithm part. Our algorithm works by using recursion. It starts with current station and ends with end station. If the algorithm hasn’t reached the end station yet, it looks neighbors of it and keeps using recursion by setting current station to one of its neighbor stations if it is not visited.
We have 2 ArrayLists out of the methods. First one is named visited to determine whether we have passed this station already or not and another one is named path to store our route. If the working recursion cannot reach the end station, it removes the path and looks for other recursions.
We have also some helper methods to help to find neighbors of current station, to get index of station in belonging line, to get index of line, to check whether the station exists or not and to check whether a station is a breakPoint or not.
In the end, if we have a path, it prints out the path on console and draw its route on a canvas. If two stations are not connected (the path is empty) or the station names don't exist, it will give an error.

<img width="856" alt="Screenshot 2023-08-11 at 15 27 05" src="https://github.com/burakorkmaz/metroPathFinder/assets/119005365/9b081e6e-a2d7-4600-b3db-bc7387210096">

<img width="223" alt="Screenshot 2023-08-11 at 15 26 35" src="https://github.com/burakorkmaz/metroPathFinder/assets/119005365/78065023-1ac4-4d31-b793-048dbf08edd9">
