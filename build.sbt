import sbtcrossproject.crossProject
import sbtcrossproject.CrossType

lazy val doobieVersion        = "0.6.0"
lazy val fs2Version           = "2.2.1"
lazy val geminiLocalesVersion = "0.1.0-2019a"
lazy val gspMathVersion       = "0.1.6"
lazy val kindProjectorVersion = "0.10.3"
lazy val monocleVersion       = "2.0.0"
lazy val paradiseVersion      = "2.1.1"

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
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion  % "test"
    ),
    Test / parallelExecution := false
  )
  .enablePlugins(AutomateHeaderPlugin)
