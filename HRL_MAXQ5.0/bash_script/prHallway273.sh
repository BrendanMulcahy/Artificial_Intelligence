# bash file to run hallway experiment
# by Feng, May 29, 2012
mkdir PRHallway0529

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e hallway 0.15 -exp 5000 2000 -to PRHallway0529 -run 5) 2> PRHallway0529/alispqnone.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq func -e hallway 0.15 -exp 5000 2000 -to PRHallway0529 -run 5) 2> PRHallway0529/maxqfunc.txt
