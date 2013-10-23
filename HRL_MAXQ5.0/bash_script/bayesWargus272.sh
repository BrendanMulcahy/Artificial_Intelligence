# bash file to run wargus experiment
# by Feng, May 27, 2012
mkdir BayesWargus0527
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a flatq -e wargus 3322 0.15 -exp 1000 1000 -to BayesWargus0527 -run 5) 2> BayesWargus0527/flatq.txt
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a maxq none -e wargus 3322 0.15 -exp 1000 1000 -to BayesWargus0527 -run 5) 2> BayesWargus0527/maxqnone.txt
#(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none -e wargus 3322 0.15 -exp 1000 1000 -to BayesWargus0527 -run 5) 2> BayesWargus0527/bmaxqnone.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none f -e wargus 3322 0.15 -exp 1000 1000 -to BayesWargus0527 -run 5) 2> BayesWargus0527/bmbqnone.txt
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a rmaxq none -e wargus 3322 0.15 -exp 1000 1000 -to BayesWargus0527 -run 5) 2> BayesWargus0527/rmaxqnone.txt




