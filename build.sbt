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
  scalaVersion := "2.12.8"
) ++ gspPublishSettings)

lazy val schema = project
  .in(file("modules/schema"))
  .settings(
    name := "gsp-core"
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
      "co.fs2"     %% "fs2-core"         % fs2Version,
      "edu.gemini" %% "gsp-math"         % gspMathVersion,
      "edu.gemini" %% "gsp-math-testkit" % gspMathVersion % "test",
      "com.github.julien-truffaut" %%% "monocle-core"   % monocleVersion,
      "com.github.julien-truffaut" %%% "monocle-macro"  % monocleVersion,
        // "com.github.julien-truffaut" %%% "monocle-unsafe" % LibraryVersions.monocleVersion,
        // "com.github.julien-truffaut" %%% "monocle-law"    % LibraryVersions.monocleVersion % "test"))
      ),
      addCompilerPlugin("org.scalamacros" %% "paradise" % paradiseVersion cross CrossVersion.patch),
  )
  .jvmConfigure(_.enablePlugins(AutomateHeaderPlugin))
  .jsSettings(gspScalaJsSettings: _*)

