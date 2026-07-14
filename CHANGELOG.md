# Changelog

All notable changes to this project will be documented in this file. 

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.4.3](https://github.jpl.nasa.gov/MIPL/fei-client/releases/tag/2.4.3) - 2023-06-05

### FEI Client release 2.4.3 

- Enhancements and fixes in support of FEI client services
- Code changes: 
  - Convert int multiplication into long multiplication
  - New client Constants class, contains refactored CLIENTVERSIONSTR
- Fixes for BAT file not working in Windows environment
- Update Log4J in support of security fixes
- Remove internal dependencies that shadow JDK, creating unpredictable benavior in certain rare circumstances

## [2.4.0](https://github.jpl.nasa.gov/MIPL/fei-client/releases/tag/2.4.0) - 2021-07-26

### FEI Client release 2.4.0 

- Enhancements and fixes in support of FEI client services
- Major dependency hierarchy refactor to break FEI into API libraries and eliminate circular dependencies

## [2.3.20](https://github.jpl.nasa.gov/MIPL/fei-client/releases/tag/2.3.20) - 2021-07-21

### FEI Client release 2.3.20 

- Enhancements and fixes in support of FEI client services
- Add 12-bit unpacking mode to unpack10 
- Add support for non-float Java versions
- Add new certificates
