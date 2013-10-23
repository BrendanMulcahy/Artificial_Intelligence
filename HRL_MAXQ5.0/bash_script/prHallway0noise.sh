# bash file to run hallway experiment
# by Feng, May 27, 2012
mkdir PRHallway0530

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq func -e hallway 0 -exp 5000 2000 -to PRHallway0530 -run 5) 2> PRHallway0530/alispqnone.txt




