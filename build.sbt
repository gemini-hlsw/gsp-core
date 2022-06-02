import sbtcrossproject.crossProject
import sbtcrossproject.CrossType

lazy val doobieVersion               = "0.8.6"
lazy val catsVersion                 = "2.1.1"
lazy val catsTestkitScalaTestVersion = "1.0.1"
lazy val fs2Version                  = "2.4.4"
lazy val geminiLocalesVersion        = "0.5.0"
lazy val gspMathVersion              = "0.2.8"
lazy val kindProjectorVersion        = "0.11.0"
lazy val monocleVersion              = "2.1.0"
lazy val paradiseVersion             = "2.1.1"
lazy val flywayVersion               = "6.4.2"
lazy val http4sVersion               = "0.21.7"
lazy val scalaXmlVerson              = "1.3.0"
lazy val mouseVersion                = "0.25"
lazy val silencerVersion             = "1.6.0"
lazy val coulombVersion              = "0.5.0"
lazy val spireVersion                = "0.17.0"
lazy val singletonOpsVersion         = "0.5.1"

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    homepage := Some(url("https://github.com/gemini-hlsw/gsp-core")),
    addCompilerPlugin(
      ("org.typelevel" % "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)
    ),
    scalacOptions += "-Ymacro-annotations"
  ) ++ gspPublishSettings
)

// doesn't work to do this `inThisBuild`
lazy val commonSettings = Seq(
  Compile / doc / scalacOptions --= Seq(
    "-Xfatal-warnings"
  )
)

// don't publish an artifact for the [empty] root project
skip in publish := true

addCommandAlias(
  "genEnums",
  "; gen/runMain gem.sql.Main modules/model/shared/src/main/scala/gem/enum; headerCreate"
)
addCommandAlias("rebuildEnums",
                "; schema/flywayClean; schema/flywayMigrate; genEnums; modelJVM/compile"
)

lazy val schema = project
  .in(file("modules/schema"))
  .enablePlugins(FlywayPlugin)
  .settings(commonSettings)
  .settings(
    name := "gsp-core-schema",
    skip in publish := true,
    flywayUrl := "jdbc:postgresql:gem",
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
      "co.fs2"                     %%% "fs2-core"           % fs2Version,
      "edu.gemini"                 %%% "gsp-math"           % gspMathVersion,
      "com.github.julien-truffaut" %%% "monocle-core"       % monocleVersion,
      "com.github.julien-truffaut" %%% "monocle-macro"      % monocleVersion,
      "com.manyangled"             %%% "coulomb"            % coulombVersion,
      "com.manyangled"             %%% "coulomb-si-units"   % coulombVersion,
      "org.typelevel"              %%% "spire"              % spireVersion,
      "eu.timepit"                 %%% "singleton-ops"      % singletonOpsVersion
    )
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
    name := "gsp-core-model-tests"
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
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test"
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
    testOptions in Test += Tests
      .Argument(TestFrameworks.ScalaTest, "-l", "gem.test.Tags.RequiresNetwork"),
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-async-http-client" % http4sVersion,
      "org.typelevel" %% "mouse"                    % mouseVersion
      // GspCoreDb.value,
      // GspCoreTestkit.value,
      // Mouse.value,
      // Fs2IO
    )
  )
