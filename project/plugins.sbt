addSbtPlugin("org.flywaydb"       % "flyway-sbt"               % "4.2.0")
addSbtPlugin("edu.gemini"         % "sbt-gsp"                  % "0.1.6")
addSbtPlugin("com.geirsson"       % "sbt-ci-release"           % "1.2.6")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.28")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql"  % "42.2.6", // needed by flyway
  // "org.slf4j"      % "slf4j-nop"   % "1.7.26", // to silence some log messages
  // "org.typelevel" %% "cats-core"   % "1.6.1",
  // "org.typelevel" %% "cats-effect" % "0.8"
)
