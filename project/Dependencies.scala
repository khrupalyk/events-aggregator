import sbt._

object Dependencies {
  object V {
    val catsCore         = "2.6.1"
    val catsEffect       = "2.5.1"
    val circe            = "0.14.1"
    val http4s           = "0.21.25"
    val logbackClassic   = "1.2.3"
    val pureConfig       = "0.15.0"
    val kinesis          = "1.12.89"
    val mongo            = "4.2.3"
  }

  object Deps {
    lazy val kinesis                = "com.amazonaws"                 % "aws-java-sdk-kinesis"           % V.kinesis
    lazy val mongo                  = "org.mongodb.scala"             %% "mongo-scala-driver"            % V.mongo
    lazy val catsCore               = "org.typelevel"                 %% "cats-core"                     % V.catsCore
    lazy val catsEffect             = "org.typelevel"                 %% "cats-effect"                   % V.catsEffect
    lazy val fs2                    = "co.fs2"                        %% "fs2-core"                      % V.catsEffect
    lazy val circeCore              = "io.circe"                      %% "circe-core"                    % V.circe
    lazy val circeGeneric           = "io.circe"                      %% "circe-generic"                 % V.circe
    lazy val circeGenericExtra      = "io.circe"                      %% "circe-generic-extras"          % V.circe
    lazy val circeOptics            = "io.circe"                      %% "circe-optics"                  % V.circe
    lazy val circeParser            = "io.circe"                      %% "circe-parser"                  % V.circe
    lazy val http4sBlazeClient      = "org.http4s"                    %% "http4s-blaze-client"           % V.http4s
    lazy val http4sCirce            = "org.http4s"                    %% "http4s-circe"                  % V.http4s
    lazy val http4sClient           = "org.http4s"                    %% "http4s-blaze-client"           % V.http4s
    lazy val http4sDsl              = "org.http4s"                    %% "http4s-dsl"                    % V.http4s
    lazy val http4sServer           = "org.http4s"                    %% "http4s-blaze-server"           % V.http4s
    lazy val logbackClassic         = "ch.qos.logback"                 % "logback-classic"               % V.logbackClassic
    lazy val pureConfig             = "com.github.pureconfig"         %% "pureconfig"                    % V.pureConfig
  }
}
