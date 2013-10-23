#set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:100]
set key right bottom

set term postscript 20
set output 'Taxi_Modified_b.ps'

plot 'Taxi-BMaxQPrBayes-1000-avg5.rl' using 1:2 title 'B-MaxQ Bayes PR' with linespoints pi 15 lt 5 pt 5 lw 2, \
	'Taxi-BMaxQPrMan-1000-avg5.rl' using 1:2 title 'B-MaxQ Manual PR' with linespoints pi 12 lt 7 pt 7 lw 2, \
	'Taxi-BMaxQPrNo-1000-avg5.rl' using 1:2 title 'B-MaxQ No PR' with linespoints pi 10 lt 9 pt 9 lw 2

set terminal pop
replot

