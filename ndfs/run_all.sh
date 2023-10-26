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
horizontal_line="================================================================================================================================================"
table_line="+----------------------------------------------------------------------------------------------------------------------------------------------+"

# Print the table header with styling and borders
echo "$horizontal_line"
echo "$table_line"
printf "| ${bold}%-20s${normal} | ${bold}%-15s${normal} | ${bold}%-12s${normal} | ${bold}%-15s${normal} | ${bold}%-12s${normal} | ${bold}%-10s${normal} | ${bold}%-10s${normal} | ${bold}%-10s${normal} | ${bold}%-10s${normal} |\n" "Input File" "Model" "Num Threads" "Accepting Cycle" "Value" "Cyan" "Blue" "Red" "Pink"
echo "$table_line"

# Enumerate over files in directory and execute the command for each
for filename in ./input/*; do
    output=$(./bin/ndfs.sh "$filename" "$model" "$numthreads")
    # Extract the relevant values from the output
    input_file=$(echo "$output" | grep -o "Graph .*" | cut -d ' ' -f 2)
    cyan_nr=$(echo "$output" | grep -o "cyan .*" | cut -d ' ' -f 2)
    blue_nr=$(echo "$output" | grep -o "blue .*" | cut -d ' ' -f 2)
    red_nr=$(echo "$output" | grep -o "red .*" | cut -d ' ' -f 2)
    pink_nr=$(echo "$output" | grep -o "pink .*" | cut -d ' ' -f 2)
    accept_state=$(echo "$output" | grep -o "does .*" | cut -d ' ' -f 2)

    if [ "$accept_state" == "not" ]; then
        accept_state="NO"
    else
        accept_state="YES"
    fi

    value=$(echo "$output" | grep -o "took .*" | cut -d ' ' -f 2)
    # Print the values in a table format with borders
    printf "| %-20s | %-15s | %-12s | %-15s | %-9s ms | ${bold}%-10s${normal} | ${bold}%-10s${normal} | ${bold}%-10s${normal} | ${bold}%-10s${normal} |\n" "$input_file" "$model" "$numthreads" "$accept_state" "$value" "$cyan_nr" "$blue_nr" "$red_nr" "$pink_nr"
    echo "$table_line"
done