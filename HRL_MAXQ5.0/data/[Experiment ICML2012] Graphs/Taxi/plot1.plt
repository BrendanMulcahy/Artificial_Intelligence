#set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:200]
set key right bottom

set bmargin 1
set tmargin 0.5

set term postscript 20
set output 'Taxib.ps'

plot 'Taxi-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 17 lt 5 pt 5 lw 2, \
	'Taxi-Bayesian-MaxQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MaxQ Good' with linespoints pi 23 lt 7 pt 7 lw 2, \
	'Taxi-Bayesian-MBQ-smoothed.RLDAT' using 1:2 title 'B-MB-Q Uninformed' with linespoints pi 13 lt 4 pt 4 lw 2, \
	'Taxi-Bayesian-MBQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good' with linespoints pi 10 lt 6 pt 6 lw 2, \
	'Taxi-Bayesian-MBQ-Prior-LessSim-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good Comparable Simulations' with linespoints pi 10 lt 8 pt 8 lw 2


set terminal pop
replot

