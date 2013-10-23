set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:1000]
set yrange [-1000:0]
set key right top

set term postscript
set output 'Wargus3322.ps'



plot 'Wargus3322-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 20 lt 5 pt 5, \
	'Wargus3322-Bayesian-MaxQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MaxQ Good' with linespoints pi 20 lt 7 pt 7, \
	'Wargus3322-MaxQ-smoothed.RLDAT' using 1:2 title 'MaxQ' with linespoints pi 20  lt 2 pt 2, \
	'Wargus3322-Bayesian-MBQ-smoothed.RLDAT' using 1:2 title 'B-MB-Q Uninformed' with linespoints pi 20 lt 4 pt 4, \
	'Wargus3322-Bayesian-MBQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good' with linespoints pi 20 lt 6 pt 6, \
	'Wargus3322-FlatQ-smoothed.RLDAT' using 1:2 title 'FlatQ' with linespoints pi 20 lt 1 pt 1



set terminal pop
replot

