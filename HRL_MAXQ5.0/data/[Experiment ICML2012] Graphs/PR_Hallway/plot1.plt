set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:5000]
set yrange [-2000:0]
set key right bottom

set term postscript 20
set output 'Hallwayb.ps'

plot 'Hallway-BMaxQPrBayes-5000-avg5.rl' using 1:2 title 'B-MaxQ Bayes PR' with linespoints pi 150 lt 5 pt 5 lw 2, \
	'Hallway-BMaxQPrMan-5000-avg5.rl' using 1:2 title 'B-MaxQ Manual PR' with linespoints pi 170 lt 7 pt 7 lw 2, \
	'Hallway-BMaxQPrNo-5000-avg5.rl' using 1:2 title 'B-MaxQ No PR' with linespoints pi 130 lt 9 pt 9 lw 2

set terminal pop
replot

