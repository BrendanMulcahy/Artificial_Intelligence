# bash file to run taxi experiment
# by Feng, May 29, 2012
# for good prior, run 200 for bmb method

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none f -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0529/Taxi-n0.15r10-BMaxQPrNoiF-500-r0(2012-05-29)/goodprior -p BayesTaxi0529/Taxi-n0.15r10-BMaxQPrNoiF-500-r0(2012-05-29) -run 5)  2> BayesTaxi0529/bmbqnone_goodprior.txt

(time java -cp bin:lib/* edu.cwru.eecs.rl.MultiThreadMain -a bmaxq none -e taxi 0.15 10 true -exp 500 1000 -to BayesTaxi0527/Taxi-n0.15r10-BMaxQPrNo-500-r0(2012-05-27)/goodprior -p BayesTaxi0527/Taxi-n0.15r10-BMaxQPrNo-500-r0(2012-05-27) -run 5)  2> BayesTaxi0527/bmaxqnone_goodprior.txt