#!/bin/bash

maxBenchmarkingProcesses=2

currentlyRunning=$(ps aux|grep java|wc -l)
maxNumberRunning=$((currentlyRunning+maxBenchmarkingProcesses))

java -cp DNA.jar dna.profiler.benchmarking.BenchmarkingActions getDS | while read line;
do
	# Don't start more processes if there are already enough running!
	while [ $(ps aux|grep java|wc -l) -ge $maxNumberRunning ];
	do
		sleep 5
	done
	
	echo "Starting benchmark for $line"
	java -cp DNA.jar dna.profiler.benchmarking.BenchmarkingExperiments $line &
	sleep 1
done

# Wait for all benchmarking processes to be finished
while [ $(ps aux|grep java|wc -l) -gt $currentlyRunning ];
do
	sleep 5
done

java -cp DNA.jar  dna.profiler.benchmarking.BenchmarkingActions plot