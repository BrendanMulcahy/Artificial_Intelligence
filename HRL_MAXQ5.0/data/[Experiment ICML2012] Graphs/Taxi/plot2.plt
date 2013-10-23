#set xlabel 'Episode'
#set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:500]
set yrange [-1000:200]
set key right bottom

set bmargin 1
set tmargin 0.5

set term postscript 20
set output 'Taxinb.ps'

plot 'Taxi-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 20 lt 5 pt 5 lw 2, \
	'Taxi-MaxQ-smoothed.RLDAT' using 1:2 title 'MaxQ' with linespoints pi 15  lt 2 pt 2 lw 2, \
	'Taxi-FlatQ-smoothed.RLDAT' using 1:2 title 'FlatQ' with linespoints pi 13 lt 1 pt 1 lw 2


set terminal pop
replot

