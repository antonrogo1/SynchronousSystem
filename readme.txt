BellmanFord Implementation with ConvergeCast for Termination.
Team: Daren Cheng, Anton Rogozhnikov, Zoe Jones

Instructions:
Main executable is available at: src/com/syncsys/Main.java



Input may be changed by either changing the contents of
input.txt or by changing "input.txt" in
Main.java


How to execute:

Place input file input.txt in the same directory of location of SynchronousSystem.jar file.
run command:
java -jar SynchronousSystem.jar

On completion results of execution will be placed in output.txt

----------------------------------------------------------------------------
Expected Input file format:
Sample input from professor is included in input.txt
First number is the number of processes, (determines size of connectivity matrix)
Second line ids of processes separated by comma
Third Line id of the root.
Remaining lines are connectivity matrix. Each line represent process' connections to other processes(columns). -1 means no connection.

-----------------------------------------------------------------------------

Sample output from professorInput.txt is below.

Note:
id - id of the node
dist - distance from root
done - if the node is done processing, used for convergecast termination
terminating - if the node is ready to terminate
children/done - ratio of number of children to number of children done
parent - id of the parent of this node

-----------------------------------------------------------------------------
Finished reading InputFile
id: 0, dist: 0, done: false, terminating: false, children/done: 0/0,
id: 1, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 2, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 3, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 4, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 5, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 6, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 7, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 8, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
id: 9, dist: 2147483647, done: false, terminating: false, children/done: 0/0,
Round completed

id: 0, dist: 0, done: false, terminating: false, children/done: 0/0,
id: 1, dist: 5, done: false, terminating: false, children/done: 0/0, parent: 0
id: 2, dist: 0, done: false, terminating: false, children/done: 0/0, parent: 0
id: 4, dist: 1, done: false, terminating: false, children/done: 0/0, parent: 0
id: 6, dist: 7, done: false, terminating: false, children/done: 0/0, parent: 0
id: 8, dist: 9, done: false, terminating: false, children/done: 0/0, parent: 0
id: 3, dist: 7, done: false, terminating: false, children/done: 0/0, parent: 0
id: 7, dist: 2, done: false, terminating: false, children/done: 0/0, parent: 0
id: 5, dist: 3, done: false, terminating: false, children/done: 0/0, parent: 0
id: 9, dist: 7, done: false, terminating: false, children/done: 0/0, parent: 0
Round completed

id: 0, dist: 0, done: false, terminating: false, children/done: 9/0,
id: 1, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 5, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 4, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 0
id: 2, dist: 0, done: true, terminating: false, children/done: 0/0, parent: 0
id: 3, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 2
id: 6, dist: 3, done: true, terminating: false, children/done: 0/0, parent: 7
id: 9, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 2
id: 7, dist: 2, done: true, terminating: false, children/done: 0/0, parent: 0
id: 8, dist: 2, done: true, terminating: false, children/done: 0/0, parent: 2
Round completed

id: 1, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 0, dist: 0, done: false, terminating: true, children/done: 3/3,
id: 3, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 2
id: 5, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 6, dist: 2, done: true, terminating: false, children/done: 0/0, parent: 9
id: 7, dist: 2, done: true, terminating: false, children/done: 1/1, parent: 0
id: 8, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 3
id: 2, dist: 0, done: true, terminating: false, children/done: 3/3, parent: 0
id: 9, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 2
id: 4, dist: 1, done: true, terminating: false, children/done: 2/2, parent: 0
Round completed

id: 1, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 2, dist: 0, done: true, terminating: true, children/done: 2/2, parent: 0
id: 7, dist: 2, done: true, terminating: true, children/done: 0/0, parent: 0
id: 6, dist: 2, done: true, terminating: false, children/done: 0/0, parent: 9
id: 8, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 3
id: 9, dist: 1, done: true, terminating: false, children/done: 1/1, parent: 2
id: 4, dist: 1, done: true, terminating: true, children/done: 2/2, parent: 0
id: 5, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 4
id: 3, dist: 1, done: true, terminating: false, children/done: 1/1, parent: 2
Round completed

id: 1, dist: 1, done: true, terminating: true, children/done: 0/0, parent: 4
id: 3, dist: 1, done: true, terminating: true, children/done: 1/1, parent: 2
id: 6, dist: 2, done: true, terminating: false, children/done: 0/0, parent: 9
id: 8, dist: 1, done: true, terminating: false, children/done: 0/0, parent: 3
id: 5, dist: 1, done: true, terminating: true, children/done: 0/0, parent: 4
id: 9, dist: 1, done: true, terminating: true, children/done: 1/1, parent: 2
Round completed

id: 6, dist: 2, done: true, terminating: true, children/done: 0/0, parent: 9
id: 8, dist: 1, done: true, terminating: true, children/done: 0/0, parent: 3
Round completed

All Nodes Terminated

Final output:

Root ID: 0
Process ID: 0; Distance: 0; Shortest Path: 0
Process ID: 1; Distance: 1; Shortest Path: 1 =>4 =>0
Process ID: 2; Distance: 0; Shortest Path: 2 =>0
Process ID: 3; Distance: 1; Shortest Path: 3 =>2 =>0
Process ID: 4; Distance: 1; Shortest Path: 4 =>0
Process ID: 5; Distance: 1; Shortest Path: 5 =>4 =>0
Process ID: 6; Distance: 2; Shortest Path: 6 =>9 =>2 =>0
Process ID: 7; Distance: 2; Shortest Path: 7 =>0
Process ID: 8; Distance: 1; Shortest Path: 8 =>3 =>2 =>0
Process ID: 9; Distance: 1; Shortest Path: 9 =>2 =>0

Process finished with exit code 0

-----------------------------------------------------------------------------