set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:100]
set key right bottom

set term postscript
set output 'Taxi_Modified.ps'

plot 'Taxi-BMaxQPrBayes-1000-avg5.rl' using 1:2 title 'B-MaxQ Bayes PR' with linespoints pi 10 lt 5 pt 5, \
	'Taxi-BMaxQPrMan-1000-avg5.rl' using 1:2 title 'B-MaxQ Manual PR' with linespoints pi 10 lt 7 pt 7, \
	'Taxi-BMaxQPrNo-1000-avg5.rl' using 1:2 title 'B-MaxQ None PR' with linespoints pi 10 lt 9 pt 9, \
	'Taxi-MaxQPrFunc-1000-avg5.rl' using 1:2 title 'MaxQ Func PR' with linespoints pi 10 lt 4 pt 4, \
	'Taxi-MaxQPrMan-1000-avg5.rl' using 1:2 title 'MaxQ Manual PR' with linespoints pi 10 lt 6 pt 6, \
	'Taxi-MaxQPrNo-1000-avg5.rl' using 1:2 title 'MaxQ None PR' with linespoints pi 10 lt 8 pt 8, \
	'Taxi-FlatQ-1000-avg5.rl' using 1:2 title 'FlatQ' with linespoints pi 10 lt 1 pt 1

set terminal pop
replot

