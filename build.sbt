import sbtcrossproject.crossProject
import sbtcrossproject.CrossType

lazy val doobieVersion        = "0.6.0"
lazy val fs2Version           = "2.2.2"
lazy val geminiLocalesVersion = "0.2.0"
lazy val gspMathVersion       = "0.1.10"
lazy val kindProjectorVersion = "0.10.3"
lazy val monocleVersion       = "2.0.1"
lazy val paradiseVersion      = "2.1.1"
lazy val flywayVersion        = "6.2.1"
lazy val http4sVersion        = "0.21.0-RC3"
lazy val scalaXmlVerson       = "1.2.0"
lazy val mouseVersion         = "0.24"

inThisBuild(Seq(
  homepage := Some(url("https://github.com/gemini-hlsw/gsp-core")),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion),
  resolvers += "Gemini Repository" at "https://github.com/gemini-hlsw/maven-repo/raw/master/releases", // for gemini-locales
  crossScalaVersions := Seq(scalaVersion.value), // for now, until we get doobie/fs2 upgraded
) ++ gspPublishSettings)

// doesn't work to do this `inThisBuild`
lazy val commonSettings = Seq(
  Compile / doc / scalacOptions --= Seq(
    "-Xfatal-warnings"
  )
)

// don't publish an artifact for the [empty] root project
skip in publish := true

addCommandAlias("genEnums", "; gen/runMain gem.sql.Main modules/model/shared/src/main/scala/gem/enum; headerCreate")
addCommandAlias("rebuildEnums", "; schema/flywayClean; schema/flywayMigrate; genEnums; modelJVM/compile")

lazy val schema = project
  .in(file("modules/schema"))
  .enablePlugins(FlywayPlugin)
  .settings(commonSettings)
  .settings(
    name := "gsp-core-schema",
    skip in publish := true,
    flywayUrl  := "jdbc:postgresql:gem",
    flywayUser := "postgres",
    flywayLocations := Seq(
      s"filesystem:${baseDirectory.value}/src/main/resources/db/migration"
    )
  )

lazy val gen = project
  .in(file("modules/gen"))
  .settings(commonSettings)
  .dependsOn(schema)
  .settings(
    name := "gsp-core-gen",
    skip in publish := true,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-postgres" % doobieVersion
    )
  )
  .enablePlugins(AutomateHeaderPlugin)

lazy val model = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/model"))
  .settings(commonSettings)
  .settings(
    name := "gsp-core-model",
    libraryDependencies ++= Seq(
      "co.fs2"                     %%% "fs2-core"      % fs2Version,
      "edu.gemini"                 %%% "gsp-math"      % gspMathVersion,
      "com.github.julien-truffaut" %%% "monocle-core"  % monocleVersion,
      "com.github.julien-truffaut" %%% "monocle-macro" % monocleVersion,
    ),
    addCompilerPlugin("org.scalamacros" %% "paradise" % paradiseVersion cross CrossVersion.patch),
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)
  .jsSettings(
    libraryDependencies += "edu.gemini" %%% "gemini-locales" % geminiLocalesVersion
  )

lazy val testkit = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/testkit"))
  .settings(commonSettings)
  .dependsOn(model)
  .settings(
    name := "gsp-core-testkit",
    libraryDependencies ++= Seq(
      "edu.gemini" %%% "gsp-math-testkit" % gspMathVersion
    )
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)

lazy val model_tests = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("modules/model-tests"))
  .settings(commonSettings)
  .dependsOn(model)
  .dependsOn(testkit)
  .settings(
    skip in publish := true,
    name := "gsp-core-model-tests",
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)

lazy val db = project
  .in(file("modules/db"))
  .settings(commonSettings)
  .dependsOn(schema)
  .dependsOn(model.jvm)
  .dependsOn(testkit.jvm)
  .settings(
    name := "gsp-core-db",
    skip in publish := true,
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion  % "test"
    ),
    Test / parallelExecution := false
  )
  .enablePlugins(AutomateHeaderPlugin)

lazy val ocs2_api = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/ocs2_api"))
  .dependsOn(model)
  .settings(commonSettings)
  .settings(
    name := "gsp-core-ocs2-api"
  )

// This module is here to ensure it stays up to date, but it's unpublished and unused for now.
lazy val ocs2 = project
  .in(file("modules/ocs2"))
  .dependsOn(model.jvm, ocs2_api.jvm, db)
  .settings(commonSettings)
  .settings(
    name := "gsp-core-ocs2",
    libraryDependencies ++= Seq(
      "org.flywaydb"            % "flyway-core"              % flywayVersion,
      "org.http4s"             %% "http4s-dsl"               % http4sVersion,
      "org.http4s"             %% "http4s-blaze-server"      % http4sVersion,
      "org.http4s"             %% "http4s-async-http-client" % http4sVersion,
      "org.http4s"             %% "http4s-scala-xml"         % http4sVersion,
      "org.scala-lang.modules" %% "scala-xml"                % scalaXmlVerson
    )
  )

// This module is here to ensure it stays up to date, but it's unpublished and unused for now.
lazy val ephemeris = project
  .in(file("modules/ephemeris"))
  .dependsOn(model.jvm, testkit.jvm, db)
  .settings(commonSettings)
  .settings(
    name := "gsp-core-ephemeris",
    // by default, ignore network tests
    // to run these:
    //    sbt:gsp-core-ephemeris> set Test/testOptions := Nil
    //    sbt:gsp-core-ephemeris> test
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l", "gem.test.Tags.RequiresNetwork"),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-async-http-client" % http4sVersion,
      "org.typelevel" %% "mouse"                    % mouseVersion,
      // GspCoreDb.value,
      // GspCoreTestkit.value,
      // Mouse.value,
      // Fs2IO
    )
  )
