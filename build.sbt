//import com.github.play2war.plugin._

name := "university"

version := "1.0-SNAPSHOT"



libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.apache.commons" % "commons-email" % "1.4"
)

play.Project.playJavaSettings

// Play2WarPlugin.play2WarSettings

// Play2WarKeys.servletVersion := "3.0"

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
//libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.27"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28"

libraryDependencies += "javax.mail" % "mail" % "1.4.7"

// https://mvnrepository.com/artifact/joda-time/joda-time
libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

// https://mvnrepository.com/artifact/com.jolbox/bonecp
libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0.RELEASE"

// https://mvnrepository.com/artifact/com.sendgrid/sendgrid-java
libraryDependencies += "com.sendgrid" % "sendgrid-java" % "4.2.1"

libraryDependencies += "com.hynnet" % "jxl" % "2.6.12.1"

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.1"

resolvers += DefaultMavenRepository

resolvers += "Maven Repo" at "https://repo1.maven.org/maven2/"

