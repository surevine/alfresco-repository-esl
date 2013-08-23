Alfresco Repository Enhanced Security Labelling Module
======================================================

### Summary

This module adds Enhanced Security Labelling to an Alfresco Repository.


### Static Analysis Standards

The code coverage target for this project is 70% by block as measured by emma.  A coverage report is built automatically via ant.

As of 23/8/2013, we have only reached 59% by block.  Until 70% is reached, no changes must be committed that would reduce this figure.  Once 70% is reached, no changes must be committed that would reduce the figure to below 70%.  Contirbutions will be rejected on this basis.

This code is analaysed using findbugs.  The project's tolerances allow for one high priority warning, two normal priority warnings, and unlimited low priority warnings, per 5,000 lines of Java code, excluding comments and whitespace.  As of 23/8/13, the project comprises of 3190 lines of Java Code (from a total of 7100 lines of code).  No commits must be made outside of these tolerances, and contributions which breach these tolerances will be rejected.

The style of our Java code is analysed using checkstyle.  This project aims for a tolerance level of no more than 2 warnings per 1,000 lines of code.  At the moment, we are greatly in excess of this level, as our checkstyle template has only recently been applied and we have not yet migrated all our code across to the new style.  While we remain in excess of our tolerance, no changes must be committed that increase the number of chwckstyle warnings per 1,000 lines of code.  Once we reach our tolerance level of 2 warnings per 1,000 lines of code, no changes must be committed that increase the number of checkstyle warnings outside of this level.  Constributions which breach these tolerances will be rejected.

As an experiment, we are passing our javascript code through gjslint.  We're using the following code to do so:

for f in `find ~/git/alfresco-repository-esl/ -name *js`; do gjslint --nojsdoc --max_line_length 1000 --disable 0005,0200,0216,0213 $f; done

To count the errors, we are using:

for f in `find ~/git/alfresco-repository-esl/ -name *js`; do gjslint --nojsdoc --max_line_length 1000 --disable 0005,0200,0216,0213 $f | grep ^Line ; done | wc -l

Although not yet an absolute requirement, contributors are strongly encouraged not to check in any changes that increase the reported warnings from gjslint.  This may become an absolute requirement in the future.  As of 23/8/13, there are 36 reported warnings.