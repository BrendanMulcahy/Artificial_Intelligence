set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:1000]
set yrange [-1000:0]
set key right top

set bmargin 1
set tmargin 0.5

set term postscript 20
set output 'Wargus3322b.ps'

plot 'Wargus3322-Bayesian-MaxQ-smoothed.RLDAT' using 1:2 title 'B-MaxQ Uninformed' with linespoints pi 25 lt 5 pt 5 lw 2, \
	'Wargus3322-Bayesian-MaxQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MaxQ Good' with linespoints pi 20 lt 7 pt 7 lw 2, \
	'Wargus3322-Bayesian-MBQ-smoothed.RLDAT' using 1:2 title 'B-MB-Q Uninformed' with linespoints pi 20 lt 4 pt 4 lw 2, \
	'Wargus3322-Bayesian-MBQ-Prior-smoothed.RLDAT' using 1:2 title 'B-MB-Q Good' with linespoints pi 20 lt 6 pt 6 lw 2


set terminal pop
replot

