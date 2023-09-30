#!/bin/bash

# The script takes two arguments
model=$1
numthreads=$2

# Check to see if the required amount of arguments are passed
if [ "$#" -ne 2 ]; then
    echo "You must enter exactly 2 arguments: model and numthreads"
    exit 1
fi

# Define text formatting escape sequences
bold=$(tput bold)
normal=$(tput sgr0)

# Define table formatting characters
horizontal_line="========================================================================================================="
table_line="+-------------------------------------------------------------------------------------------------------+"

# Print the table header with styling and borders
echo "$horizontal_line"
echo "$table_line"
printf "| ${bold}%-30s${normal} | ${bold}%-20s${normal} | ${bold}%-20s${normal} | ${bold}%-22s${normal} |\n" "Input File" "Model" "Num Threads" "Value"
echo "$table_line"

# Enumerate over files in directory and execute the command for each
for filename in ./input/*; do
    output=$(./bin/ndfs.sh "$filename" "$model" "$numthreads")
    # Extract the relevant values from the output
    input_file=$(echo "$output" | grep -o "Graph .*" | cut -d ' ' -f 2)
    value=$(echo "$output" | grep -o "took .*" | cut -d ' ' -f 2)
    # Print the values in a table format with borders
    printf "| %-30s | %-20s | %-20s | %-20s ms|\n" "$input_file" "$model" "$numthreads" "$value"
    echo "$table_line"
done