# uncertainModelChecking
Machine learning methods for model checking uncertain CTMCs


## Projects

### gaussianProcesses
Library for GP regression and classification.
Depends on:
- commons.math
- jblas

### gpOptimisation
Library that uses GP regression as a model to direct global optimisation.
Depends on:
- gaussianProcesses

### stochasticSimulation
Stochastic simulation library for CTMCs; MiTL model checking of CTMC trajectories is included.
Depends on:
- biopepa-core
- commons.math
- java-cup-11a-runtime

### smoothedMC
Library that uses GP probit regression (classification) to perform model checking for uncertain CTMCs.
Depends on:
- stochasticSimulation
- commons.cli

### gpoMC
Library that uses GP optimisation as a mean to perform inference for uncertain CTMCs.
Depends on:
- gaussianProcesses
- gpOptimisation
- stochasticSimulation

### Ucheck
Command-line tool for model checking and parameter synthesis under uncertainty
Depends on:
- gpoMC
- smoothedMC

