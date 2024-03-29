set xlabel 'Episode'
set ylabel 'Average Cumulative Reward Per Episode'
set xrange [0:5000]
set yrange [-2000:0]
set key right bottom

set term postscript 20
set output 'Hallway.ps'

plot 'Hallway-BMaxQPrBayes-5000-avg5.rl' using 1:2 title 'B-MaxQ Bayes PR' with linespoints pi 100 lt 5 pt 5 lw 2, \
	'Hallway-BMaxQPrMan-5000-avg5.rl' using 1:2 title 'B-MaxQ Manual PR' with linespoints pi 100 lt 7 pt 7 lw 2, \
	'Hallway-BMaxQPrNo-5000-avg5.rl' using 1:2 title 'B-MaxQ None PR' with linespoints pi 100 lt 9 pt 9 lw 2, \
	'Hallway-MaxQPrFunc-5000-avg5.rl' using 1:2 title 'MaxQ N-Bayes PR' with linespoints pi 100 lt 4 pt 4 lw 2, \
	'Hallway-MaxQPrMan-5000-avg5.rl' using 1:2 title 'MaxQ Manual PR' with linespoints pi 100 lt 6 pt 6 lw 2, \
	'Hallway-MaxQPrNo-5000-avg5.rl' using 1:2 title 'MaxQ None PR' with linespoints pi 100 lt 8 pt 8 lw 2, \
	'Hallway-FlatQ-5000-avg5.rl' using 1:2 title 'FlatQ' with linespoints pi 100 lt 1 pt 1 lw 2

set terminal pop
replot

