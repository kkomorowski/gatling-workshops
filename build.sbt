name := "gatling-workshops"

version := "0.1"

scalaVersion := "2.12.6"

enablePlugins(GatlingPlugin)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.1" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.3.1" % "test"
)

