import sbtcrossproject.crossProject
import sbtcrossproject.CrossType

lazy val doobieVersion        = "0.6.0"
lazy val fs2Version           = "1.0.5"
lazy val gspMathVersion       = "0.1.1"
lazy val kindProjectorVersion = "0.9.10"
lazy val monocleVersion       = "1.5.1-cats"
lazy val paradiseVersion      = "2.1.1"

inThisBuild(Seq(
  homepage := Some(url("https://github.com/gemini-hlsw/gsp-core")),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % kindProjectorVersion),
) ++ gspPublishSettings)

addCommandAlias("genEnums", "; gen/runMain gsp.core.gen.Main modules/model/shared/src/main/scala/gem/enum; headerCreate")
addCommandAlias("rebuildEnums", "; schema/flywayClean; schema/flywayMigrate; genEnums; modelJVM/compile")

lazy val schema = project
  .in(file("modules/schema"))
  .settings(
    name := "gsp-core",
    flywayUrl  := "jdbc:postgresql:gsp",
    flywayUser := "postgres",
    flywayLocations := Seq(
      s"filesystem:${baseDirectory.value}/src/main/resources/db/migration"
    )
  )

lazy val gen = project
  .in(file("modules/gen"))
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
  .settings(
    name := "gsp-core-model",
    libraryDependencies ++= Seq(
      "co.fs2"                     %%% "fs2-core"         % fs2Version,
      "edu.gemini"                 %%% "gsp-math"         % gspMathVersion,
      "edu.gemini"                 %%% "gsp-math-testkit" % gspMathVersion % "test",
      "com.github.julien-truffaut" %%% "monocle-core"     % monocleVersion,
      "com.github.julien-truffaut" %%% "monocle-macro"    % monocleVersion,
      ),
      addCompilerPlugin("org.scalamacros" %% "paradise" % paradiseVersion cross CrossVersion.patch),
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)

lazy val db = project
  .in(file("modules/db"))
  .dependsOn(model.jvm % "compile->compile;test->test")
  .settings(
    name := "gsp-core-db",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test"
    )
  )
  .enablePlugins(AutomateHeaderPlugin)
