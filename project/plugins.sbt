resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt"               % "6.4.0")
addSbtPlugin("edu.gemini"            % "sbt-gsp"                  % "0.1.16")
addSbtPlugin("com.geirsson"          % "sbt-ci-release"           % "1.5.3")
addSbtPlugin("org.scala-js"          % "sbt-scalajs"              % "0.6.32")
addSbtPlugin("org.portable-scala"    % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("com.timushev.sbt"      % "sbt-updates"              % "0.5.0")

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql"  % "42.2.11", // needed by flyway
)
