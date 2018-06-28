Gatling Workshops
=================

Krzysztof Komorowski [@hiqualitypl](https://www.twitter.com/HiQualityPL)

---

## Gatling overview

---

## Environment setup

Gatling tests could be run using:

* Your favourite build tool (e.g. `sbt` or Maven)
* Standalone application

---

### `sbt` configuration

plugins.sbt

```scala
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")
```    

build.sbt

```scala
libraryDependencies ++= Seq(
  "io.gatling.highcharts" %
     "gatling-charts-highcharts" % "2.3.1" % "test",
  "io.gatling"            %
     "gatling-test-framework"    % "2.3.1" % "test"
)
```

---

### Maven configuration

---

### Standalone Gatling runner

---

## Basic simulation

---

### Gatling DSL

---

## `gatling.conf`

---

## An eye on the test report

---

## Excersises

---

## Thank You!