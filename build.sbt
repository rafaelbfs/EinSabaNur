name := "Bund"

organizationName := "Bundherr GmBH"

organization := "br.com.bundherr"

version := "1.0.0-alpha"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "1.1.5",
  "com.typesafe.slick" %% "slick-testkit" % "1.1.5" % "test",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "ch.qos.logback" % "logback-classic" % "0.9.28" % "test",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
  "com.github.tminglei" % "slick-pg_2.10.1" % "0.2.2",
  "com.vividsolutions" % "jts" % "1.13",
  "org.json4s" %% "json4s-native" % "3.2.6",
  "org.threeten" % "threetenbp" % "0.8.1"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

parallelExecution in Test := false

logBuffered := false

