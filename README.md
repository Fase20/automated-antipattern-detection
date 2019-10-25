This repository is specifically created to support the double-blind submission of a research paper. Therefore, it only hosts the developed tool to automatically detect performance antipatterns and the obtained experimental results for reproducibility. 

# Automated Antipattern Detection

This site contains the source code of our automated method's implementation, as well as the results of our experimental evaluation which show how the operational profile affects the QoS analysis and refactoring of software systems.

## Installation and Usage

### Requirements
* Mac OS or Linux
* [Storm Model Checker](https://www.stormchecker.org/)

### Installation
1. Install Storm (installation instructions at https://www.stormchecker.org/documentation/installation/installation.html)
2. Download the [AntiPatternDetection](https://github.com/Fase20/automated-antipattern-detection) folder
3. A fast and tested way to build and run the code, is to import it as an existing project in [Eclipse](https://www.eclipse.org/downloads/)

#### Note:
If using Linux operating system replace the first line of the "storm_CTMC.sh" script ```#!/bin/sh``` with ```#!/bin/bash```, located in the folder "res.scripts"

### Usage
1. Running the "mainGen.java" as Java Application will initiate the antipattern detection procedures and the generation of the "FX_System.sm" CTMC model, under "res.models" 
2. The model will automatically be verified using Storm based on the already specified properties in "script_CTMC.sh" for a wide range of operational profile parameters 
3. After the successful completion of the verification phase, multiple graphs will appear in the generated "Anti-PatternDetection.graphs" folder, one of each antipattern type (i.e. BLOB, Concurrent Processing Systems, Pipe and Filter) 
4. The "config" folder ("res.config") contains the configuration files where the user can specify internal, external and system paramaters whose change will affect the detection of antipatterns
5. This version of the tool supports the automated generation of all experimental results reported in the submitted paper as part of our evaluation. Thus, the user will not have to make any mannual change. The only requirement is to run the code and all graphs will appear in the generated "Anti-PatternDetection.graphs" folder.

## Reproducing the experiments
The tool-generated graphs from the evaluation section of the paper can be found [here](https://github.com/Fase20/automated-antipattern-detection/tree/master/evaluation_results), placed in the "evaluation_results" folder. Additionally, the models used for every scenario can be found here, under the "evaluation_models" folder. To reproduce the experimental results, the only requirement is to run the "mainGen.java" as Java Application and the graphs will be automatically produced.
