README file for the 2023-2024 Concurrency and Multi-threading assignment

File structure

bin/ndfs.sh
    script to start the application for Linux/MacOS.
bin/ndfs.bat
    script to start the application for Windows.
build.gradle
    gradle script.
doc
    javadoc for the graph package.
report
    directory where you should put the PDF of your report.
input
    various input files to run the application with.
lib 
    jar files needed for the application.
README.txt
    this file.
src
    The source code for this project.
    There are also two example directories demonstrating the use of java.util.concurrent.ExecutorService,
    and java.lang.Thread.

File structure source code
    driver/Main.java
	The Main class that drives the application. 
    ndfs/nndfs
	The package for the sequential nndfs version.
    ndfs/mcndfs_1_naive
	The package for the naive multi-core version. Initially, this is a copy
	of the ndfs/nndfs package.


Building the application

Make sure you have configured Java (release 11 or higher, JDK version)
with the JAVA_HOME environment variable set correspondingly.
On DAS-5, JDK 11 is setup by default in your .bashrc .

Run Gradle from the root directory (ndfs)
    $ ./gradlew


Running the application

From the root directory run 
    $ bin/ndfs.sh

This will print the usage. An example of a correct run of the application is:
    $ bin/ndfs.sh input/accept-cycle.prom seq 1
The first argument is the input file, the second argument indicates the version to use, the third
argument is the number of worker threads.


Reading the documentation of the graph package

Open doc/index.html in a browser.


Programming the mcndfs_alg3_naive version

Your first programming task is to create a multi-core NDFS version that uses
a naive approach to solve the concurrency problems, based on Alg 3 in the Laarman paper.
This should simply use global locks to protect data that is shared between threads.

Initially, the package "ndfs.mcndfs_alg3_naive" contains the same code as the
package ndfs.nndfs, only the package name in the Java files has been modified.
Modify the source to create the actual multi-core version and add
the version to the driver.Main.dispatch() method if needed. If you adhere to
the convention of the NDFSFactory.createMCNDFS() method, the current dispatch()
method is fine and does not need to be changed.

You can then run it using:
   $ bin/ndfs.sh input/accept-cycle.prom alg3_naive <nthreads>
where <nthreads> indicates the number of worker threads.


Programming other multi-core versions

After the naive version is tested, you can continue and create an optimized multicore
version in the same manner, as described in the assignment text. 
When it is called "mcndfs_alg3_optimized", you can then run it using:
   $ bin/ndfs.sh input/accept-cycle.prom alg3_optimized <nthreads>


Warnings

When doing timing runs, please make sure that you use a separate run for
each version, because otherwise timing information may become less reliable,
as the Java runtime system may cause unexpected overhead.

You may find that for several example problems, your multicore implementations will
not provide improved exetution times compared to the sequential version.
This is to be expected, as it is due to the nature of the problem domain.
See the paper by Laarman et al for details.  Discuss this in your report.

For the evaluation, pick several problems that show different behaviour.
Some will have large search spaces containing an accepting cycle, others won't,
which will give different performance.   Some of the examples are so small
that it is not worth discussing them; only use these for basic testing.

The base implementation provided prints out the number of colors set while
searching through the graph.  This should be helpful both for debugging and
for analyzing the performance resulst, so good to also implement this in
the multicore version.
