# v0.2 (10/20/2017)
# Release Notes

## Notable Changes
The Barcelona Release (v 0.2) of the Support Logging micro service includes the following:
* Application of Google Style Guidelines to the code base
* Increase in unit/intergration tests from 0 tests to 241 tests
* POM changes for appropriate repository information for distribution/repos management, checkstyle plugins, etc.
* Added Dockerfile for creation of micro service targeted for ARM64 
* Added interfaces for all Controller classes
* Added implementation for log updates, deletes
* Removed groovy tests and groovy plugins from POM

## Bug Fixes
* None

## Pull Request/Commit Details
 - [#9](https://github.com/edgexfoundry/support-notifications/pull/9) - Remove staging plugin contributed by Jeremy Phelps ([JPWKU](https://github.com/JPWKU))
 - [#8](https://github.com/edgexfoundry/support-notifications/pull/8) - Fixes Maven artifact dependency path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#7](https://github.com/edgexfoundry/support-notifications/pull/7) - added staging and snapshots repos to pom along with nexus staging mav… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#6](https://github.com/edgexfoundry/support-notifications/pull/6) - Add aarch64 docker file contributed by ([feclare](https://github.com/feclare))
 - [#5](https://github.com/edgexfoundry/support-notifications/pull/5) - Adds Docker build capability contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#4](https://github.com/edgexfoundry/support-notifications/pull/4) - added nexus repos elements to pom, and add property to allow for opti… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#3](https://github.com/edgexfoundry/support-notifications/pull/3) - checkstyle added to pom, new unit tests, new integration tests, remov… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#2](https://github.com/edgexfoundry/support-notifications/pull/2) - Add distributionManagement for artifact storage contributed by Andrew Grimberg ([tykeal](https://github.com/tykeal))
 - [#1](https://github.com/edgexfoundry/support-notifications/pull/1) - Contributed Project Fuse source code contributed by Tyler Cox ([trcox](https://github.com/trcox))
