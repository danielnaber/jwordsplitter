Internal README
===============

How to make a new release for Maven Central:

* update: `<version>` in `pom.xml`, `README.md`, and `CHANGES.md`
* `mvn clean deploy -P release`
* go to https://oss.sonatype.org/ -> Staging repositories, scroll down, select, and click "Release"
* `git tag -a vx.y -m 'version x.y'`
* `git push origin vx.y`
* set version in `pom.xml` to `x.y-SNAPSHOT`

Also see http://central.sonatype.org/pages/apache-maven.html
