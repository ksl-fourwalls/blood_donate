#!/bin/bash
# generate userdata for registeration
for i in {1..1000};
do
	email="$(cat /dev/urandom | tr -dc '[:alpha:]' | fold -w ${1:-6} | head -n 1)@gmail.com";
	username="$(cat /dev/urandom | tr -dc '[:alpha:]' | fold -w ${1:-8} | head -n 1)";
	phoneno="$(cat /dev/urandom | tr -dc '[:digit:]' | fold -w ${1:-10} | head -n 1)";
	password="$(cat /dev/urandom | tr -dc '[:alnum:]' | fold -w ${1:-12} | head -n 1)";
	 
	#curl  -s "http://localhost:8000/app.php?register&email=$email&password=$password&username=$username&phoneno=$phoneno" > /dev/null

	RANDDATE="2023$(printf "%.02d%.02d" $(($RANDOM%2+5)) $(($RANDOM%30+1)))"
	DATE="$(date --date=$RANDDATE +"%Y-%m-%d")";

	curl  -s "http://localhost:8000/app.php?receiver&email=$email&password=$password&" > /dev/null
done


