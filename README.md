FPGrowth
========

1. Import the project into your eclispe (make sure you have the library, commons-cli-1.2)
2. The arguments of run configuration is "-fs 0.1 -ts 0.5 -fc 50 -tc 50 -d 0.1 -o s -db D100kT10N1k.txt"
    -fs from support(%)
    -ts to support(%)
    -fc from confidence(%)
    -tc to confidence(%)
    -d the change rate of support or confidence(%)
    -o choose the parameter you want to apply the change rate, 's' for support and 'c' for confidence
    -db the input database file
3. the output will be stored in exp directory
