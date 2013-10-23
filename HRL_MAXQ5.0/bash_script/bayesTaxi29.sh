# bash file to run taxi experiment
# by Feng, May 29, 2012
# for when run 1000 sim for flat mb method
mkdir BayesTaxi0529
(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none f -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0529 -run 5) 2> BayesTaxi0529/bmbqnone.txt




