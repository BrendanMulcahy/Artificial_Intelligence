set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:200]
set key right bottom

set term postscript
set output 'Taxi.ps'

plot 'Taxi-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 10 lt 5 pt 5, \
	'Taxi-Bayesian-MaxQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MaxQ Good' with linespoints pi 10 lt 7 pt 7, \
	'Taxi-MaxQ-smoothed.RLDAT' using 1:2 title 'MaxQ' with linespoints pi 10  lt 2 pt 2, \
	'Taxi-Bayesian-MBQ-smoothed.RLDAT' using 1:2 title 'B-MB-Q Uninformed' with linespoints pi 10 lt 4 pt 4, \
	'Taxi-Bayesian-MBQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good' with linespoints pi 10 lt 6 pt 6, \
	'Taxi-Bayesian-MBQ-Prior-LessSim-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good Comparable Simulations' with linespoints pi 10 lt 8 pt 8, \
	'Taxi-FlatQ-smoothed.RLDAT' using 1:2 title 'FlatQ' with linespoints pi 10 lt 1 pt 1

set terminal pop
replot

