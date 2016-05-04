#!/bin/bash
filename="$1"
counter=$((0))
while read -r line
do
	echo "$line" >> $((140000+500*counter)).txt
	counter=$((counter+1))
done < "$filename"
