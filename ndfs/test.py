import subprocess
import os
import time

# Define the list of algorithms, threads, and number of runs
algorithms = ["seq", "alg3_naive", "alg3_optimized"]
thread_counts = [1, 4, 8, 16]
num_runs = 5

# Path to the input directory
input_dir = "input"

# Path to the script
script_path = "./bin/ndfs.sh"

results_file = "results-5runs.yml"

# Dictionary to store results
results = {alg: {threads: [] for threads in thread_counts} for alg in algorithms}

fout = open(results_file, "w")
fout.write("Benchmark Implementations")
fout.close()

fout = open(results_file, "a")

# Iterate over each input file
for input_file in os.listdir(input_dir):
    input_path = os.path.join(input_dir, input_file)
    for algorithm in algorithms:
        if algorithm == "seq":
            threads_to_test = [1]  # Sequential algorithm runs with 1 thread
        else:
            threads_to_test = [4, 8, 16]  # All other algorithms run with 4 and 16 threads
        for threads in threads_to_test:
            runtimes = []
            for _ in range(num_runs):
                # Construct the command to run the script
                cmd = [script_path, input_path, algorithm, str(threads)]
                start_time = time.time()
                output = subprocess.check_output(cmd).decode('utf-8')
                end_time = time.time()

                # Split the output into lines
                lines = output.strip().split('\n')
                runtime_line = lines[-1]

                # Extract the runtime from the last line
                runtime_parts = runtime_line.split()
                if len(runtime_parts) >= 2:
                    runtime = float(runtime_parts[-2])
                else:
                    runtime = 0.0  # Default to 0.0 if the format is not as expected

                # Extract the correctness information
                correctness = "Cycle Present" if "does contain an accepting cycle." in output else "No Cycle Present"

                runtimes.append(runtime)
                print(f"Algorithm: {algorithm}, Threads: {threads}, Input File: {input_file}, Runtime: {runtime} ms, Correctness: {correctness}")
                fout.write(f"Algorithm: {algorithm}, Threads: {threads}, Input File: {input_file}, Runtime: {runtime} ms, Correctness: {correctness}\n")

            # Compute the average runtime
            average_runtime = sum(runtimes) / num_runs

            # Store the result
            results[algorithm][threads].append((input_file, average_runtime, correctness))


def display_results_table(results):
    table = []
    for algorithm, thread_data in results.items():
        for threads, results_list in thread_data.items():
            for result in results_list:
                input_file, avg_runtime, correctness = result
                table.append([algorithm, threads, input_file, avg_runtime, correctness])

    headers = ["Algorithm", "Threads", "Input File", "Average Runtime (s)", "Correctness"]
    print(tabulate(table, headers, tablefmt="grid"))

# Print the results
print("Results for NDFS")
fout.write("Results for NDFS\n")
print(f"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
fout.write(f"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
print(f"==========================================================")
fout.write(f"==========================================================\n")
for algorithm, thread_data in results.items():
    for threads, results_list in thread_data.items():
        for result in results_list:
            input_file, avg_runtime, correctness = result
            print(f"Algorithm: {algorithm}, with {threads} threads:")
            fout.write(f"Algorithm: {algorithm}, with {threads} threads:\n")
            print(f"Input File: {input_file}, Avg: {avg_runtime} ms")
            fout.write(f"Input File: {input_file}, Avg: {avg_runtime} ms\n")
            print(f"==========================================================")
            fout.write(f"==========================================================\n")
fout.close()

            
