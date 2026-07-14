# FEI5 Repositories

## Installations

### Prerequisites

* OpenJDK 1.8
* Maven 3.6.3
* Completed configurations:
  * [CAE Artifactory](https://cae-artifactory.jpl.nasa.gov) access (see wiki.jpl.nasa.gov)
  * settings.xml in ~/.m2 (mkdir -p ~/.m2 && cd ~/.m2)
    * Sample settings.xml available at https://github.jpl.nasa.gov/MIPL/parent-mipl/
    * Follow instructions to replace username and password with actual user_id and api_token generated in CAE Artifactory
  * preinstall MIPL Parent
    * Download parent to a local directory from https://github.jpl.nasa.gov/MIPL/parent-mipl/ and cd into directory
    * mvn -U clean install

## Checkout and Complete Development Tasks
* Create a branch from develop and clone repo using Git coordinates
* checkout new branch
* Commit changes and push
* Create a Pull Request on Github to merge from the new branch to develop

## Build, Release and Test Instrumentation
Releases are conducted automatically on PR in [CAE Jenkins](https://cae-jenkins2.jpl.nasa.gov/)
* Validate branch build locally and push
  * mvn -U clean install
  * git commit -m "Commit message" && git push
* Branch develop: SNAPSHOT RELEASE
  * Merge PR from a branch to develop
* Branch master: Production Release
  * Merge PR from develop to master

Release artifacts are uploaded automatically using the GAV parameters in the POM to CAE Artifactory Maven repos. 