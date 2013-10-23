# bash file to run taxi experiment
# by Feng, May 27, 2012
mkdir PRTaxi0530
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e taxi 0 0 true -exp 500 1000 -to PRTaxi0530 -run 5) 2> PRTaxi0530/alispqnone.txt

# don't need pseudo reward
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a alispq none -e taxi 0 10 true -exp 500 1000 -to PRTaxi0530 -run 5) 2> PRTaxi0530/alispqnone10.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq bayes -e taxi 0 10 true -exp 500 1000 -to PRTaxi0530 -run 5) 2> PRTaxi0530/bmaxqbayes10.txt




