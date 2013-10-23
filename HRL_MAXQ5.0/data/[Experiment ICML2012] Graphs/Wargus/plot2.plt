set xlabel 'Episode'
#set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:1000]
set yrange [-1000:0]
set key right top

set bmargin 1
set tmargin 0.5

set term postscript 20
set output 'Wargus3322nb.ps'

plot 'Wargus3322-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 25 lt 5 pt 5 lw 2, \
	'Wargus3322-MaxQ-smoothed.RLDAT' using 1:2 title 'MaxQ' with linespoints pi 20  lt 2 pt 2 lw 2, \
	'Wargus3322-FlatQ-smoothed.RLDAT' using 1:2 title 'FlatQ' with linespoints pi 20 lt 1 pt 1 lw 2


set terminal pop
replot

