#!/bin/bash

maxBenchmarkingProcesses=3

currentlyRunning=$(ps aux|grep java|wc -l)
maxNumberRunning=$((currentlyRunning+maxBenchmarkingProcesses))

todoList=(`java -cp DNA.jar dna.profiler.benchmarking.BenchmarkingActions getDS`)

for todoLine in "${todoList[@]}"
do
	# Don't start more processes if there are already enough running!
	while [ $(ps aux|grep java|wc -l) -ge $maxNumberRunning ];
	do
		sleep 10
	done
	
	startTime=$(date +"%d. %B %T")
	echo "[$startTime] Starting benchmark for $todoLine"
	java -cp DNA.jar dna.profiler.benchmarking.BenchmarkingExperiments $todoLine &
	sleep 1
done

# Wait for all benchmarking processes to be finished
while [ $(ps aux|grep java|wc -l) -gt $currentlyRunning ];
do
	sleep 5
done

java -cp DNA.jar  dna.profiler.benchmarking.BenchmarkingActions plot