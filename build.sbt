import Dependencies.Deps
import sbtrelease.ReleasePlugin.autoImport.releaseIgnoreUntrackedFiles

lazy val root = (project in file("."))
  .settings(name := "events", publish / skip := true, releaseIgnoreUntrackedFiles := true)
  .aggregate(`events-service`, datasource)

lazy val `events-service` = (project in file("events-service"))
  .dependsOn(datasource)
  .enablePlugins(JavaAppPackaging)
  .settings(
    libraryDependencies ++= Seq(
      Deps.mongo,
      Deps.catsCore,
      Deps.catsEffect,
      Deps.circeGeneric,
      Deps.http4sCirce,
      Deps.http4sClient,
      Deps.http4sDsl,
      Deps.http4sServer,
      Deps.logbackClassic,
      Deps.pureConfig
    )
  )

lazy val datasource = (project in file("datasource"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    libraryDependencies ++=  Seq(
        Deps.catsCore,
        Deps.catsEffect,
        Deps.fs2,
        Deps.circeCore,
        Deps.circeGeneric,
        Deps.pureConfig,
        Deps.kinesis
    )
  )
