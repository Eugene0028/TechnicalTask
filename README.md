<h1 align="center">Technical Task</h1>
<h2 align = "center"> For CFT team </h2>
<p align = "center"> The goal of the project.

Write a program that performs merge sort on multiple files.

The input files contain data of one of two types: integers or strings. The data is recorded in columns (each line of the file represents a new element). Strings can contain any non-whitespace characters, and strings with spaces are considered invalid. It is also assumed that the files are sorted in advance.

The output of the program should be a new file with the combined content of the input files, sorted in either ascending or descending order using merge sort. If the content of the input files does not allow for merge sorting (for example, if the sorting order is violated), partial sorting should be performed to the extent possible for this algorithm. How to handle a corrupted file is at the discretion of the developer.
The output file should contain the sorted data even in the case of errors. However, there may be a loss of erroneous data.

<h6 align = "center"> Used software </h6>
<ol>
	<li>Windows operating system </li>
	<li>IntelliJ IDEA, Visual Studio Code, Launch4j(for make .exe)</li>
</ol>
<h6 align = "center"> Project start instructions </h6>
<ol>
	<li> The JDK package already exist, just unpack them!</li>
	<li> Just enter: <p align = "center"> sort-it.exe -i -a out.txt in.txt (for integers ascending)</p>
	<p align = "center">sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for strings ascending)</p>
	<p align = "center">sort-it.exe -d -s out.txt in1.txt in2.txt (for strings descending)</li></p>
    <li>If you want to check all project, go to the src folder.</li>
</ol>

