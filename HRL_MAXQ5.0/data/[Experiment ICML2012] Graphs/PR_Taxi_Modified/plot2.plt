#set xlabel 'Episode'
#set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:100]
set key right bottom

set term postscript 20
set output 'Taxi_Modified_nb.ps'

plot 'Taxi-BMaxQPrBayes-1000-avg5.rl' using 1:2 title 'B-MaxQ Bayes PR' with linespoints pi 15 lt 5 pt 5 lw 2, \
	'Taxi-MaxQPrFunc-1000-avg5.rl' using 1:2 title 'MaxQ Non-Bayes PR' with linespoints pi 10 lt 4 pt 4 lw 2, \
	'Taxi-MaxQPrMan-1000-avg5.rl' using 1:2 title 'MaxQ Manual PR' with linespoints pi 10 lt 6 pt 6 lw 2, \
	'Taxi-MaxQPrNo-1000-avg5.rl' using 1:2 title 'MaxQ No PR' with linespoints pi 10 lt 8 pt 8 lw 2, \
	'Taxi-FlatQ-1000-avg5.rl' using 1:2 title 'FlatQ' with linespoints pi 10 lt 1 pt 1 lw 2

set terminal pop
replot

