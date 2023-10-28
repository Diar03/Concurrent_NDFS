import yaml
import matplotlib.pyplot as plt
import re
from matplotlib.ticker import FuncFormatter

# Function to parse the YAML file and extract data
def parse_yaml(file_path):
    data = []
    with open(file_path, 'r') as file:
        is_data = False  # To track whether we are inside the data section
        entry = []
        for line in file:
            if re.match(r'^\++', line):
                is_data = not is_data  # Toggle the data flag
            elif is_data and not re.match(r'^=', line):
                entry_parts = line.split(", ")
                entry.append(entry_parts[0].split()[-1])
                entry.append(entry_parts[1].split()[-2])

                if (len(entry) == 4):
                    data.append(list(entry))
                    entry.clear()

    return data

# Function to create and save bar plots
def create_bar_plots(data, output_file, algorithm_colors, log_scale=False):
    plt.figure(figsize=(12, 8))

    # Sort data by input file, then by algorithm
    data.sort(key=lambda entry: (entry[2], entry[0]))

    input_files = list(set(entry[2] for entry in data))
    algorithms = list(set("{} {} threads".format(entry[0], entry[1]) for entry in data))

    num_input_files = len(input_files)
    num_algorithms = len(algorithms)

    # Create a dictionary to hold the data for each algorithm
    algorithm_data = {algorithm: [0] * num_input_files for algorithm in algorithms}

    # Create x-axis positions for each group of bars
    x = range(num_input_files)

    # Create a list of labels for each bar group
    labels = list(input_files)

    # Populate the algorithm_data dictionary
    for entry in data:
        algorithm = "{} {} threads".format(entry[0], entry[1])
        input_file = entry[2]
        time = float(entry[3])
        file_index = labels.index(input_file)
        algorithm_data[algorithm][file_index] = time

    bar_width = 0.1  # Adjust as needed
    algorithms = [x.split() for x in algorithms]
    algorithms.sort(key=lambda entry: (len(entry[0]), entry[0], int(entry[1])))
    algorithms = [" ".join(x) for x in algorithms]
    
    print(algorithms)
    for i, algorithm in enumerate(algorithms):
        x_adjusted = [pos + i * bar_width for pos in x]
        plt.bar(x_adjusted, algorithm_data[algorithm], bar_width, label=algorithm, color=algorithm_colors.get(algorithm, 'blue'))

    plt.xlabel('Input File')
    plt.title('Algorithm Performance Bar Plots')
    plt.xticks([pos + (bar_width * (num_algorithms) / 2) for pos in x], labels, rotation=45)
    plt.legend()
    plt.tight_layout()

    if log_scale:
        plt.ylabel('Average Time (ms) - Log Scale')
        plt.yscale('log')  # Set Y-axis to logarithmic scale
    else:
        plt.ylabel('Average Time (ms) - Linear Scale')  # Linear Y-axis scale
        plt.subplots_adjust(left=0.075)

    plt.savefig(output_file)
    plt.close()

if __name__ == "__main__":
    input_file = 'results-5runs.yml'
    output_plot_file = 'performance_bar_plots_sorted.png'

    # Define custom colors for each algorithm
    algorithm_colors = {
        'seq 1 threads': 'red',
        'alg3_naive 4 threads': 'seagreen',
        'alg3_naive 8 threads': 'mediumseagreen',
        'alg3_naive 16 threads': 'springgreen',
        'alg3_optimized 4 threads': 'dodgerblue',
        'alg3_optimized 8 threads': 'deepskyblue',
        'alg3_optimized 16 threads': 'lightskyblue',
    }

    data = parse_yaml(input_file)

    # Create a bar plot with linear Y-axis scale
    create_bar_plots(data, output_plot_file, algorithm_colors, log_scale=False)

    print(f'Linear Y-Axis Bar plot saved as {output_plot_file}')

    # Create a bar plot with log Y-axis scale
    log_output_file = 'performance_bar_plots_sorted_log.png'
    create_bar_plots(data, log_output_file, algorithm_colors, log_scale=True)

    print(f'Log Y-Axis Bar plot saved as {log_output_file}')
