# bash file to run hallway experiment
# by Feng, May 27, 2012
mkdir PRHallway0527

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq manual -e hallway 0.15 -exp 5000 2000 -to PRHallway0527 -run 5) 2> PRHallway0527/bmaxqmanual.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none -e hallway 0.15 -exp 5000 2000 -to PRHallway0527 -run 5) 2> PRHallway0527/bmaxqnone.txt

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e hallway 0.15 -exp 5000 2000 -to PRHallway0527 -run 5) 2> PRHallway0527/alispqnone.txt




