resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt"               % "6.4.2")
addSbtPlugin("edu.gemini"            % "sbt-gsp"                  % "0.2.2")
addSbtPlugin("com.geirsson"          % "sbt-ci-release"           % "1.5.3")
addSbtPlugin("org.scala-js"          % "sbt-scalajs"              % "1.0.1")
addSbtPlugin("org.portable-scala"    % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("com.timushev.sbt"      % "sbt-updates"              % "0.5.1")

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql"  % "42.2.13", // needed by flyway
)
