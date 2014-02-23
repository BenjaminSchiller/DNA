#!/bin/bash

maxBenchmarkingProcesses=2

currentlyRunning=$(ps aux|grep java|wc -l)
maxNumberRunning=$((currentlyRunning+maxBenchmarkingProcesses))

java -cp "lib/*;bin/" dna.profiler.benchmarking.BenchmarkingActions getDS | while read line;
do
	# Don't start more processes if there are already enough running!
	while [ $(ps aux|grep java|wc -l) -ge $maxNumberRunning ];
	do
		sleep 5
	done
	
	echo "Starting benchmark for $line"
	java -cp "lib/*;bin/" dna.profiler.benchmarking.BenchmarkingExperiments $line &
	sleep 1
done

# Wait for all benchmarking processes to be finished
while [ $(ps aux|grep java|wc -l) -ge $maxNumberRunning ];
do
	sleep 5
done

java -cp "lib/*;bin/" dna.profiler.benchmarking.BenchmarkingActions plot