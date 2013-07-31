import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "tonek"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.10.2"

  val defaultSettings = Defaults.defaultSettings ++
    Seq(
      organization := buildOrganization,
      version := buildVersion,
      scalaVersion := buildScalaVersion,
      checksums := Nil,
      unmanagedResourceDirectories in Compile <+= sourceDirectory / "main/webapp",
      resolvers ++= Seq(
        "Scala-Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
        "asual" at "http://www.asual.com/maven/content/groups/public",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
      )
    )

  val webSettings = defaultSettings ++ com.earldouglas.xsbtwebplugin.WebPlugin.webSettings ++
    Seq(
      ivyXML :=
        <dependency org="org.eclipse.jetty.orbit" name="javax.servlet" rev="3.0.0.v201112011016">
            <artifact name="javax.servlet" type="orbit" ext="jar"
                      url="http://repo1.maven.org/maven2/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar"/>
        </dependency>
    )

}

object Dependencies {
  val liftVer = "2.5.1"
  val liftScala = "2.10.2"
  val lessVer = "1.3.3"
  val jettyVer = "8.1.3.v20120416"

  private def l(name: String) = name //+ "_" + liftScala

  val jsoup = "org.jsoup" % "jsoup" % "1.6.2"

  val slf = "org.slf4j" % "slf4j-log4j12" % "[1.6.4,)"

  val jodaTime = "joda-time" % "joda-time" % "2.0"
  val jodaConvert = "org.joda" % "joda-convert" % "1.2"

  val liftweb = "net.liftweb" %% l("lift-webkit") % liftVer
  val liftrecord = "net.liftweb" %% l("lift-record") % liftVer
  val liftutil = "net.liftweb" %% l("lift-util") % liftVer
  val liftactor = "net.liftweb" %% l("lift-actor") % liftVer
  val liftcommon = "net.liftweb" %% l("lift-common") % liftVer
  val liftjson = "net.liftweb" %% l("lift-json") % liftVer
  val liftjsonext = "net.liftweb" %% l("lift-json-ext") % liftVer
  val liftsquerrec = "net.liftweb" %% l("lift-squeryl-record") % liftVer
  //val liftwidgets = "net.liftweb" %% l("lift-widgets") % liftVer
  val postgresql = "postgresql" % "postgresql" % "9.1-901.jdbc4"
  //val cssSel = "se.fishtank" %% "css-selectors-scala" % "[0.1.2,)"

  val akka = "com.typesafe.akka" %% "akka-actor" % "2.2.0"

  val textile = "net.liftweb" %% l("lift-textile") % liftVer

  val lesscss = "com.asual.lesscss" % "lesscss-engine" % lessVer
  val lesscssservlet = "com.asual.lesscss" % "lesscss-servlet" % lessVer

  val oauth = "oauth.signpost" % "signpost-core" % "[1.2,)"
  val oauthHttp = "oauth.signpost" % "signpost-commonshttp4" % "[1.2,)"
  val httpClient = "org.apache.httpcomponents" % "httpclient" % "4.2.1"
  val elasticsearchClient = "org.elasticsearch" % "elasticsearch" % "[0.20.2]"

  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided->default"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVer % "container"

  val jettyWebApp = "org.eclipse.jetty" % "jetty-webapp" % jettyVer % "test->default"
  val jsp = "javax.servlet.jsp" % "jsp-api" % "2.1"
  val jspCompiler = "tomcat" % "jasper-compiler" % "5.5.9"

  val scalap = "org.scala-lang" % "scalap" % "2.10.2"
  val junit = "junit" % "junit" % "4.8.2" % "test->default"
  val testng = "org.testng" % "testng" % "6.0.1" % "test->default"
  val scalatest = "org.scalatest" %% "scalatest" % "[1.7.2,)" % "test"
  val log4jTest = "log4j" % "log4j" % "[1.2.16,)" % "test"

  val liftDeps = Seq(
    liftweb, liftrecord, liftutil, liftactor, liftcommon, liftjson, liftjsonext, liftsquerrec, /*liftwidgets, cssSel,*/
    postgresql, scalap
  )

  val commonDeps = Seq(slf, scalatest, log4jTest, jodaTime, jodaConvert)

  val lessDeps = Seq(lesscss, lesscssservlet)

  val jettyDeps = Seq(jettyServer, jettyWebApp)

  val webDeps = liftDeps ++ lessDeps ++ jettyDeps ++ commonDeps ++ Seq(httpClient, oauth, oauthHttp, akka)
}

object AnyReadBuild extends Build {

  import BuildSettings._
  import Dependencies._

  lazy val common = createProject("common", defaultSettings, commonDeps:_*)

  lazy val search = createProject("search", defaultSettings, (commonDeps ++ Seq(httpClient, elasticsearchClient, liftjson)):_*) dependsOn(common)

  lazy val aweb = createProject("web", webSettings, webDeps:_*) dependsOn(common, search)

  def createProject(name: String, settings: Seq[Setting[_]], deps: ModuleID*) =
    Project(name, file(name), settings = settings ++ Seq(libraryDependencies ++= deps))

}
