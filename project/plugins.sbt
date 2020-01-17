resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt"               % "6.0.0")
addSbtPlugin("edu.gemini"            % "sbt-gsp"                  % "0.1.11")
addSbtPlugin("com.geirsson"          % "sbt-ci-release"           % "1.5.0")
addSbtPlugin("org.scala-js"          % "sbt-scalajs"              % "0.6.28")
addSbtPlugin("org.portable-scala"    % "sbt-scalajs-crossproject" % "0.6.0")

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql"  % "42.2.6", // needed by flyway
)
