name := "httpserver"

version := "0.1"

scalaVersion := "2.12.8"      // operator %% inserts "2.12" from scalaVersion

lazy val akkaVersion = "2.6.0-M1"

//libraryDependencies ++= Seq(
//    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
////    "com.typesafe.akka" %% "akka-http-core" % akkaVersion2,
////    "com.typesafe.akka" %% "akka-stream" % akkaVersion2,
////    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion2,
////    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
////    "com.typesafe.akka" %% "akka-http" % "2.12",
//    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
//)



libraryDependencies ++= Seq(
    // akka
    "com.typesafe.akka" % "akka-actor_2.12" % "2.6.0-M1",
    "com.typesafe.akka" % "akka-testkit_2.12" % "2.6.0-M1" % "test",
    // streams
    "com.typesafe.akka" % "akka-stream_2.12" % "2.6.0-M1",
    // akka http
    //##"com.typesafe.akka" % "akka-http-core_2.12" % "10.1.8",
    ///"com.typesafe.akka" % "akka-http-experimental_2.12.0-M3" % "2.4.2",
    //"com.typesafe.akka" % "akka-http" % "3.0.0-RC1"
    "com.typesafe.akka" %% "akka-http" % "10.1.8",
    // persistence
    "com.typesafe.akka"          %% "akka-persistence" % "2.6.0-M1",  //"2.5.22", 
    "org.iq80.leveldb"            % "leveldb"          % "0.7",
    "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8"
    ///"com.typesafe.akka" % "akka-http-testkit_2.12" % "10.1.8" % "test",
    // the next one add only if you need Spray JSON support
    ///"com.typesafe.akka" % "akka-http-spray-json-experimental_2.12.0-RC2" % "2.4.11"
//    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)


// https://repo1.maven.org/maven2/com/typesafe/akka/