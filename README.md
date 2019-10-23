This repository only hosts the version of the automated tool for the detection of anti-patterns in our running example, which was used for the evaluation of our approach submitted in [FASE 2020](https://www.etaps.org/2020/fase) and will not receive any maintenance. 

# Automated Anti-pattern Detection

This site contains the source code of our automated method's implementation, as well as the results of our experimental evaluation which show how the operational profile affects the QoS analysis and refactoring of software systems.

## Installation and Usage

### Requirements
* Mac OS or Linux
* [Storm Model Checker](https://www.stormchecker.org/)

### Installation
1. Install Storm (installation instructions at https://www.stormchecker.org/documentation/installation/installation.html)
2. Download the [Anti-PatternDetection](https://github.com/Fase20/automated-antipattern-detection) folder
3. A fast and tested way to build and run the code, is to import it as an existing project in [Eclipse](https://www.eclipse.org/downloads/)

#### Note:
If using Linux operating system replace the first line of the "storm_CTMC.sh" script ```#!/bin/sh``` with ```#!/bin/bash```, located in the folder "res.scripts"

### Usage
1. Running the "mainGen.java" as Java Application will initiate the anti-pattern detection procedures and the generation of the "FX_System.sm" CTMC model, under "res.models" 
2. The model will automatically be verified using Storm based on the already specified properties in "script_CTMC.sh" for a wide range of operational profile parameters 
3. After the successful completion of the verification phase, multiple graphs will appear in the "graphs" folder, one of each anti-pattern type (i.e. BLOB, CPS, P&F)  
4. The "config" folder ("res.config") contains the configuration files where the user can specify internal, external and system paramaters whose change will affect the detection of anti-patterns

## Re-creating the experiements
The tool-generated graphs from the evaluation section of the paper can be found [here], placed in the "evaluation_results" folder. In order to re-create them, perform the refactoring actions listed in our paper.
