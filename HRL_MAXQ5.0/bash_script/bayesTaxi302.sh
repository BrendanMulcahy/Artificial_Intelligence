# bash file to run taxi experiment
# by Feng, May 27, 2012
mkdir BayesTaxi0530
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a flatq -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0530 -run 5) 2> BayesTaxi0530/flatq.txt
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq none -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0530 -run 5) 2> BayesTaxi0530/maxqnone.txt
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0530 -run 5) 2> BayesTaxi0530/bmaxqnone.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none f -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0530 -run 5) 2> BayesTaxi0530/bmbqnone.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a rmaxq none -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0530 -run 5) 2> BayesTaxi0530/rmaxqnone.txt




