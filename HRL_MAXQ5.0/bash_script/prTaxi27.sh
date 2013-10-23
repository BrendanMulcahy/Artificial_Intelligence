# bash file to run taxi experiment
# by Feng, May 27, 2012
mkdir PRTaxi0527
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a flatq -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/flatq.txt

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq func -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/maxqfunc.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq manual -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/maxqmanual.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq none -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/maxqnone.txt

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq bayes -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/bmaxqbayes.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq manual -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/bmaxqmanual.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/bmaxqnone.txt

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e taxi 0.15 0 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/alispqnone.txt

# don't need pseudo reward
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq bayes -e taxi 0.15 10 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/bmaxqbayes10.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e taxi 0.15 10 true -exp 500 1000 -to PRTaxi0527 -run 5) 2> PRTaxi0527/alispqnone10.txt




