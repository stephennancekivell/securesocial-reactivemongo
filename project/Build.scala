import sbt._
import sbt.Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "securesocial-reactivemongo"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % "0.9",
    "securesocial" %% "securesocial" % "2.1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "com.stephenn",
    resolvers ++= Seq(
	      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      	Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
      )
  )
}
